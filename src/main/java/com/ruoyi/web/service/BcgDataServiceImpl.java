package com.ruoyi.web.service;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.web.controller.common.BcgScreenWebSocketServer;
import com.ruoyi.web.controller.tool.BcgDataRecord;
import com.ruoyi.web.controller.tool.BcgVoltageStat;
import com.ruoyi.web.controller.tool.DTO.BcgAlarmSearchDto;
import com.ruoyi.web.controller.tool.DTO.BcgDataSearchDto;
import com.ruoyi.web.controller.tool.VO.BcgDataListVo;
import com.ruoyi.web.controller.tool.VO.BcgMetricsVo;
import com.ruoyi.web.controller.tool.VO.BcgRecordOptionVo;
import com.ruoyi.web.controller.tool.VO.BcgSubjectOptionVo;
import com.ruoyi.web.controller.untils.BcgSignalProcessor;
import com.ruoyi.web.controller.untils.PdfReportGenerator;
import com.ruoyi.web.mapper.BcgDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.web.controller.tool.VO.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class BcgDataServiceImpl implements BcgDataService {

    @Autowired
    private BcgDataMapper bcgDataMapper;
    @Autowired
    private RedisCache redisCache; // 【注入 Redis 工具】

/**
 *  上传文件顺便处理了
 */
@Override
@Transactional(rollbackFor = Exception.class)
public void importBcgData(MultipartFile file, BcgDataRecord record) throws Exception {
    // 1. 上传文件保存至服务器
    String[] allowedExtension = { "csv" };
    String filePath = FileUploadUtils.upload(RuoYiConfig.getUploadPath(), file, allowedExtension);

    // 2. 初始化记录状态
    record.setFileUrl(filePath);
    record.setStatus(0); // 初始状态标记为 0：未处理

    // 3. 直接入库
    bcgDataMapper.insertBcgData(record);

    // ================== 🌟 新增：上传即刻处理逻辑 ==================
    try {
        log.info("【上传即处理】文件已保存，立即开始底层算法解析，记录ID: {}", record.getId());
        // 直接调用已经写好的私有处理方法
        this.processDataOnDemand(record.getId(), filePath, record.getSubjectId());
    } catch (Exception e) {
        log.error("【上传即处理】解析失败: {}", e.getMessage());
        // 解析失败不抛出异常，不阻断上传流程，前端可以通过状态 0 知道它失败了
    }
    // ===============================================================
}



   /* public void importBcgData(MultipartFile file, BcgDataRecord record) throws Exception {

        // ================= 1. 上传文件保存至服务器并获取初始记录 =================
        String[] allowedExtension = { "csv" };
        String filePath = FileUploadUtils.upload(RuoYiConfig.getUploadPath(), file, allowedExtension);
        record.setFileUrl(filePath);
        record.setStatus(0); // 初始状态标记为 0：处理中

        // 先插入数据库获取主键 ID (MyBatis 会自动把自增ID回写给 record.getId())
        bcgDataMapper.insertBcgData(record);

        // ================= 2. 边读取 CSV 流，边收集算法所需要的点 =================
        List<BcgSignalProcessor.DataPoint> dataPoints = new ArrayList<>();

        // 使用 try-with-resources 自动关闭输入流
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            br.readLine(); // 跳过 CSV 的第一行表头

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] columns = line.trim().split("[,\\s]+"); // 兼容逗号和空格分隔符

                if (columns.length >= 2) {
                    double time = Double.parseDouble(columns[0]);    // 第一列: 获取时间戳
                    double voltage = Double.parseDouble(columns[1]); // 第二列: 获取电压值
                    dataPoints.add(new BcgSignalProcessor.DataPoint(time, voltage));
                }
            }
        }

        // ================= 3. 调用算法，并构建各项身体健康指标 =================
        try {
            // 将收集到的点阵传入纯Java算法工具类
            BcgSignalProcessor.AnalysisResult result = BcgSignalProcessor.analyzeBcgData(dataPoints);

            // 将真实计算出的指标塞回实体类
            record.setHeartRate(result.heartRate);
            record.setHrvSdnn(result.hrvSdnn);
            record.setSampleRate((int) result.sampleRate); // 更新为算法推算出的实际采样率

            // 下面两项属于高阶指标，需专门的低频/高频滤波算法，这里暂填默认占位值
            record.setRespRate(16.0);       // 成人正常呼吸频率通常为 16-20 次/分
            record.setBodyMovement(0);      // 暂无体动

            // ====== 核心业务：打分与睡眠状态规则引擎 ======
            if (result.heartRate < 60) {
                // 心跳较慢，对应深睡；白天提示心动过缓
                record.setSleepStatus("深睡 / 心率偏低");
                record.setHealthScore(80);
            } else if (result.heartRate > 100) {
                // 心跳过快，通常因身体活动或焦虑状态
                record.setSleepStatus("躁动 / 心率过速");
                record.setHealthScore(75);
            } else {
                // 正常区间
                record.setSleepStatus("平稳浅睡");
                record.setHealthScore(95);
            }

            // 处理成功，记录成功日志
            record.setStatus(1);
            record.setRemark("智能分析成功。检出有效心跳: " + result.peakCount + "次。");

        } catch (Exception e) {
            e.printStackTrace();
            // 处理异常情况 (例如文件里只有一行数据，或者电压是一条死区直线)
            record.setStatus(1); // 流程本身已经结束
            record.setRemark("分析失败，信号异常或数据量不足：" + e.getMessage());
        }

        // ================= 4. 将填满数据的实体类 Update 到数据库中 =================
        bcgDataMapper.updateBcgData(record);
    }*/





    /**
     *  BCG数据呈现
     *
     */

    @Override
    public List<BcgDataListVo> selectAllBcgData() {
        return bcgDataMapper.selectAllBcgData();
    }

    /**
    *   根据用户ID,姓名，采集时间范围，数据状态去搜集
    */
    // ServiceImpl 实现
    @Override
    public List<BcgDataListVo> searchBcgData(BcgDataSearchDto searchDto) {
        return bcgDataMapper.searchBcgData(searchDto);
    }


    /**
     * 删除单个BCG记录
     * @param id 数据ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBcgDataById(Long id) {

        int rows = bcgDataMapper.deleteBcgDataById(id);

        if (rows > 0) {
            // ==========================================================
            // 🌟 1. 清除 Redis 缓存，保证数据一致性，防止产生脏缓存
            // ==========================================================
            String cacheKey = "bcg:waveform:data:" + id;
            redisCache.deleteObject(cacheKey);
            log.info("【Redis清理】数据被删除，已同步清除对应的波形缓存，记录ID: {}", id);

            // ==========================================================
            // 🌟 2. 数据被删除了，大屏统计总数必定变化，立刻广播！
            // ==========================================================
            this.pushGlobalDashboardData();
        }

        return rows;
    }

 //通过ID查询相应的信息
    @Override
    public BcgDataListVo selectBcgDataById(Long id) {
        return bcgDataMapper.selectBcgDataById(id);
    }
   //更新数据记录的信息，保存备注
    @Override
    public int updateBcgData(BcgDataRecord record) {
        return bcgDataMapper.updateBcgData(record);
    }


   //返回所有检测人的姓名
    @Override
    public List<BcgSubjectOptionVo> getSubjectOptions() {
        return bcgDataMapper.selectSubjectOptions();
    }
   //通过检测人的ID  找到对应的收集时间和心率
    @Override
    public List<BcgRecordOptionVo> getRecordOptions(Long subjectId) {
        return bcgDataMapper.selectRecordOptionsBySubjectId(subjectId);
    }

    //通过前端人选中收集时间，那么就返回这个时间段BCG的ID返回对应的指标展示
    /**
     * 【按需触发式】获取核心健康指标数据
     */
    @Override
    public BcgMetricsVo getBcgMetricsById(Long id) {
        BcgDataListVo recordInfo = bcgDataMapper.selectBcgDataForProcessById(id);
        if (recordInfo == null) {
            throw new RuntimeException("读取失败：未找到对应的检测记录或文件路径");
        }

        // 如果未处理，则按需触发处理
        if (recordInfo.getStatus() == null || recordInfo.getStatus() == 0 || recordInfo.getHeartRate() == null) {
            log.info("【按需触发】检测到数据未分析，开始读取 CSV 并执行底层算法，记录ID: {}", id);
            this.processDataOnDemand(id, recordInfo.getFileUrl(), recordInfo.getSubjectId());

            // 🌟 新增判断：处理完之后，再去查一次这行数据
            BcgDataListVo checkResult = bcgDataMapper.selectBcgDataById(id);
            // 如果心率还是空的，说明刚才的 processDataOnDemand 肯定走进了 catch 失败了
            if (checkResult.getHeartRate() == null) {
                // 直接抛出业务异常，若依的全局异常处理器会把它变成 500 错误返回给前端！
                throw new com.ruoyi.common.exception.ServiceException(checkResult.getRemark());
            }
        }

        return bcgDataMapper.selectBcgMetricsById(id);
    }
   /* @Override
    public BcgMetricsVo getBcgMetricsById(Long id) {
        // 1. 先查一下数据库，看看这笔记录的基本信息和状态
        BcgDataListVo recordInfo = bcgDataMapper.selectBcgDataForProcessById(id);
        if (recordInfo == null) {
            throw new RuntimeException("读取失败：未找到对应的检测记录或文件路径");
        }

        // 2. 【核心拦截】如果状态是 0 (未处理)，或者心率为空，说明这是刚上传的数据，立刻触发底层算法！
        if (recordInfo.getStatus() == null || recordInfo.getStatus() == 0 || recordInfo.getHeartRate() == null) {
            log.info("【按需触发】检测到数据未分析，开始读取 CSV 并执行底层算法，记录ID: {}", id);
            // 触发私有的处理方法
            this.processDataOnDemand(id, recordInfo.getFileUrl(), recordInfo.getSubjectId());
        }

        // 3. 此时数据绝对已经处理完毕并落库了，直接调用你原有的 SQL 返回即可
        return bcgDataMapper.selectBcgMetricsById(id);
    }*/


    /**
     * 【私有干活方法】读取 CSV、调用算法库、执行年龄自适应打分，并更新到数据库
     */
    private void processDataOnDemand(Long recordId, String fileUrl, Long subjectId) {
        // 准备一个空实体用于 Update
        BcgDataRecord updateRecord = new BcgDataRecord();
        updateRecord.setId(recordId);

        // 拼接真实的物理路径
        String localPath = RuoYiConfig.getProfile() + fileUrl.replace("/profile", "");
        List<BcgSignalProcessor.DataPoint> dataPoints = new ArrayList<>();
        // 🌟【第一步：按下秒表】记录开始时间 (毫秒)
         long startTime = System.currentTimeMillis();

        try {
            // ================= 1. 读取 CSV 文件流 =================
            try (BufferedReader br = new BufferedReader(new FileReader(localPath))) {
                String line;
                br.readLine(); // 跳过表头
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] columns = line.trim().split("[,\\s]+");
                    if (columns.length >= 2) {
                        dataPoints.add(new BcgSignalProcessor.DataPoint(
                                Double.parseDouble(columns[0]),
                                Double.parseDouble(columns[1])
                        ));
                    }
                }
            }

            // ================= 2. 纯 Java 算法运算 =================
            BcgSignalProcessor.AnalysisResult result = BcgSignalProcessor.analyzeBcgData(dataPoints);

            updateRecord.setHeartRate(result.heartRate);
            updateRecord.setHrvSdnn(result.hrvSdnn);
            updateRecord.setSampleRate((int) result.sampleRate);
            updateRecord.setRespRate(result.respRate);
            updateRecord.setBodyMovement(result.bodyMovementCount);


            // ================= 3. 提取年龄，动态打分 (满分100扣分机制) =================
            Integer age = bcgDataMapper.selectSubjectAgeById(subjectId);
            if (age == null) age = 35; // 兜底35岁

            int score = 100; // 满分 100 分起步

            // --- 扣分项 1：心率 (HR) 越界扣 15 分 ---
            int minNormalHr, maxNormalHr;
            if (age <= 3) { minNormalHr = 80; maxNormalHr = 130; }
            else if (age <= 12) { minNormalHr = 70; maxNormalHr = 110; }
            else if (age <= 60) { minNormalHr = 60; maxNormalHr = 100; }
            else { minNormalHr = 55; maxNormalHr = 90; }

            if (result.heartRate < minNormalHr || result.heartRate > maxNormalHr) {
                score -= 15;
            }

            // --- 扣分项 2：心率变异性 (HRV) 越界扣 20 分 (极其重要，代表心律不齐风险) ---
            if (result.hrvSdnn < 50 || result.hrvSdnn > 150) {
                score -= 20;
            }

            // --- 扣分项 3：呼吸频率 (RR) 越界扣 10 分 ---
            double rr = updateRecord.getRespRate() != null ? updateRecord.getRespRate() : 16.0;
            int minRr = (age <= 12) ? 18 : 12;
            int maxRr = (age <= 12) ? 30 : 20;
            if (rr < minRr || rr > maxRr) {
                score -= 10;
            }

            // --- 扣分项 4：体动频繁扣 10 分 ---
            int move = updateRecord.getBodyMovement() != null ? updateRecord.getBodyMovement() : 0;
            if (move > 5) {
                score -= 10;
            }

            // 兜底：防止极端情况扣成负数
            if (score < 0) score = 0;
            updateRecord.setHealthScore(score); // 真正科学的得分

            // --- 动态生成状态标签 ---
            if (score >= 90) {
                updateRecord.setSleepStatus("平稳浅睡");
            } else if (score >= 75) {
                if (result.heartRate > maxNormalHr) {
                    updateRecord.setSleepStatus("躁动 / 心率偏高");
                } else if (result.heartRate < minNormalHr) {
                    updateRecord.setSleepStatus("深睡 / 心率偏低");
                } else {
                    updateRecord.setSleepStatus("睡眠一般 / 略有波动");
                }
            } else {
                updateRecord.setSleepStatus("异常 / 睡眠欠佳");
            }


            updateRecord.setStatus(1); // 标记为已完成
            //updateRecord.setRemark(String.format("按需解析成功。有效心跳: %d次，基准年龄: %d岁。", result.peakCount, age));

            // 🌟【第二步：停止秒表】计算耗时并存入实体类
            long endTime = System.currentTimeMillis();
            int durationMs = (int) (endTime - startTime);
            updateRecord.setProcessDuration(durationMs); // 将真实毫秒数塞进去

        } catch (Exception e) {
            log.error("【按需触发】数据解析与算法处理异常", e);
            updateRecord.setStatus(0); // 失败也结束状态，防止无限重试
            updateRecord.setRemark("分析失败：" + e.getMessage());
            // 失败了也可以记录耗时
            updateRecord.setProcessDuration((int)(System.currentTimeMillis() - startTime));

        }

        // ================= 4. 将分析结果更新回数据库 =================
        bcgDataMapper.updateBcgData(updateRecord);

        // 🌟 新增代码：新数据解析完成落库了，大屏总数必定变化，立刻广播！
        this.pushGlobalDashboardData();
    }

    /**
     * 【波形预览核心业务逻辑】
     * 功能：读取本地 CSV 文件 -> 提取电压数据 -> 动态读取数据库采样率 ->
     * 调用算法库进行滤波平滑 -> 双路抽稀降采样 -> 存入 Redis 缓存并返回
     */
    @Override
    public BcgWaveformVo getWaveformData(Long id) {

        // ==========================================================
        // 1. 缓存层拦截 (Cache-Aside 旁路缓存模式)
        // ==========================================================
        // 设定 Redis 键名，按数据 ID 唯一区分
        String cacheKey = "bcg:waveform:data:" + id;

        // 尝试从 Redis 中获取数据，若命中则直接返回，极大减轻服务器磁盘和 CPU 压力
        BcgWaveformVo cacheData = redisCache.getCacheObject(cacheKey);
        if (cacheData != null) {
            log.info("【Redis命中】光速返回波形数据，记录ID: {}", id);
            return cacheData;
        }

        log.info("【Redis未命中】开始执行磁盘读取与算法计算，记录ID: {}", id);

        // ==========================================================
        // 2. 数据库查询与路径解析
        // ==========================================================
        // 调用专用的波形查询 SQL，该查询包含 file_url 字段
        BcgDataListVo record = bcgDataMapper.selectBcgDataWithUrlById(id);

        if (record == null || record.getFileUrl() == null) {
            log.error("【BCG波形异常】未找到ID为 {} 的记录，或 file_url 字段为空", id);
            throw new RuntimeException("读取失败：未找到对应的检测记录或文件路径");
        }

        // 动态获取设备的真实采样率 (默认给 100Hz 兜底)，用于后续的滤波算法计算窗口
        int sampleRate = (record.getSampleRate() != null && record.getSampleRate() > 0)
                ? record.getSampleRate() : 100;

        // 将数据库中的相对路径 (/profile/...) 转换为服务器磁盘的绝对路径 (/home/ruoyi/uploadPath/...)
        String localPath = RuoYiConfig.getProfile() + record.getFileUrl().replace("/profile", "");
        log.info("【BCG波形调试】采样率: {}Hz, 绝对路径: {}", sampleRate, localPath);

        // ==========================================================
        // 3. 磁盘 IO：读取 CSV 文件数据
        // ==========================================================
        List<Double> allVoltages = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(localPath))) {
            // 跳过 CSV 文件的两行表头 (根据具体的 CSV 格式调整，这里跳过 2 行)
            br.readLine();


            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                // 拆分每一行数据，兼容逗号和空格
                String[] cols = line.split("[,\\s]+");
                if (cols.length >= 2) {
                    try {
                        // 第一列 cols[0] 通常是时间戳，第二列 cols[1] 是电压值。这里只取电压值。
                        allVoltages.add(Double.parseDouble(cols[1].trim()));
                    } catch (NumberFormatException e) {
                        // 容错机制：某一行数据损坏则直接跳过，不影响全局
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            log.error("【BCG波形异常】读取CSV物理文件失败", e);
            throw new RuntimeException("读取波形文件失败: " + e.getMessage());
        }

        int totalSize = allVoltages.size();
        if (totalSize == 0) return new BcgWaveformVo();

        // ==========================================================
        // 4. 信号算法处理 (滑动平均滤波)
        // ==========================================================
        // 4.1 将 List 转换为 double[] 数组，以匹配算法类入参
        double[] rawDataArray = new double[totalSize];
        for (int i = 0; i < totalSize; i++) {
            rawDataArray[i] = allVoltages.get(i);
        }

        // 4.2 计算平滑窗口：选取 0.15 秒的时间跨度
        // 计算公式：采样率(点/秒) * 0.15(秒) = 窗口包含的点数。例如 100Hz * 0.15 = 15个点。
        int smoothWindow = Math.max(1, (int) (sampleRate * 0.15));

        // 4.3 调用你编写的算法类 BcgSignalProcessor，获得去除毛刺后的平滑数据
        double[] smoothedDataArray = BcgSignalProcessor.movingAverageFilter(rawDataArray, smoothWindow);

        // ==========================================================
        // 5. 降采样 (抽稀) 策略
        // ==========================================================
        // 左图：原始波形，保留所有细节，抽稀到约 1500 个点
        int targetA = 1500;
        int stepA = Math.max(1, totalSize / targetA);
        List<BcgWaveformVo.Point> originWave = new ArrayList<>();

        // 右图：预处理波形，突出极简轮廓，抽稀到约 200 个点
        int targetB = 200;
        int stepB = Math.max(1, totalSize / targetB);
        List<BcgWaveformVo.Point> processedWave = new ArrayList<>();

        // 单次循环同时完成两路数据的抽稀
        for (int i = 0; i < totalSize; i++) {
            // 装载左图坐标点 (使用未处理的 rawDataArray)
            if (i % stepA == 0) {
                originWave.add(new BcgWaveformVo.Point(i, rawDataArray[i]));
            }
            // 装载右图坐标点 (使用滤波后的 smoothedDataArray)
            if (i % stepB == 0) {
                processedWave.add(new BcgWaveformVo.Point(i, smoothedDataArray[i]));
            }
        }

        // ==========================================================
        // 6. 组装结果并写入缓存
        // ==========================================================
        BcgWaveformVo vo = new BcgWaveformVo();
        vo.setOriginWave(originWave);
        vo.setProcessedWave(processedWave);

        // 将最终结果序列化存入 Redis，设置过期时间为 24 小时 ( TimeUnit.HOURS )
        // 由于 BCG 历史数据不会改变，长期缓存可以极大提升后续大屏查询效率
        redisCache.setCacheObject(cacheKey, vo, 24, java.util.concurrent.TimeUnit.HOURS);
        log.info("【Redis写入】波形数据计算、滤波、抽稀完毕并已存入缓存，记录ID: {}", id);

        return vo;
    }

    /**
     * 【大屏表格核心引擎】生成 6 项生命体征汇总表
     * 这个方法会将数据库中生硬的数值，转化为前端可以直接展示的字符串格式，
     * 并且自动根据“年龄”完成健康评估。
     */
    @Override
    public List<BcgIndicatorTableVo> getIndicatorTableData(Long id) {
        // 1. 获取检测记录和受试者基础信息 (包含数据库里的心率、呼吸、年龄等原始字段)
        BcgDataListVo record1 = bcgDataMapper.selectBcgDataById(id);
        BcgMetricsVo record = bcgDataMapper.selectBcgMetricsById(id);

        if (record == null) {
            throw new RuntimeException("生成表格失败：未找到对应的检测记录");
        }

        // 准备一个空列表，用来装最终展示的 6 行数据
        List<BcgIndicatorTableVo> tableData = new ArrayList<>();

        // 提取受试者年龄，这是动态判断的基石。如果数据库没填，默认按 35 岁健康成年人评估。
        int age = record1.getSubjectAge() != null ? record1.getSubjectAge() : 35;

        // ==========================================
        // 【第 1 行】：心率 (HR) —— 结合年龄动态判断
        // ==========================================
        // 取出数据库记录的心率，处理空指针异常
        double hr = record.getHeartRate() != null ? record.getHeartRate() : 0.0;
        int minHr, maxHr; // 正常心率的上下限

        // 临床医学标准：不同年龄段静息心率差异极大
        if (age <= 3) { minHr = 80; maxHr = 130; } // 婴幼儿心跳本身就快
        else if (age <= 12) { minHr = 70; maxHr = 120; } // 儿童期
        else if (age <= 18) { minHr = 60; maxHr = 100; }
        else if (age <= 60) { minHr = 60; maxHr = 80; } // 成年人标准范围
        else { minHr = 60; maxHr = 100; } // 老年人

        // 判断当前心率是否在上面划分的范围内
        String hrStatus = "正常";
        if (hr < minHr) hrStatus = "偏低";
        else if (hr > maxHr) hrStatus = "偏高";

        // 把第一行数据塞进列表 (完全按照图片里的格式拼接字符串)
        tableData.add(new BcgIndicatorTableVo(
                "心率 (HR)",
                String.format("%.1f 次/分", hr),
                String.format("%d - %d 次/分", minHr, maxHr),
                hrStatus
        ));

        // ==========================================
        // 【第 2 行】：心率变异性 (HRV)
        // ==========================================
        // HRV 反映自主神经系统的调节能力，一般认为 100-200ms 是健康基线
        double hrv = record.getHrvSdnn() != null ? record.getHrvSdnn() : 0.0;
        String hrvStatus = "正常";

        if (hrv < 50) {
            hrvStatus = "偏低"; // 提示自主神经调节能力下降，可能与疲劳、压力或心血管隐患有关
        } else if (hrv > 150) {
            // 极高水平的 SDNN 通常见于年轻运动员，但也可能是房颤等心律失常导致的数据失真
            // 这里根据通用正常值上限标为“偏高”，你也可以视业务需求将其归为“极佳”
            hrvStatus = "偏高";
        }

        tableData.add(new BcgIndicatorTableVo(
                "心率变异性 (HRV)",
                String.format("%.0f ms", hrv),
                "50 - 150 ms", // 替换成了专业的临床医学范围
                hrvStatus
        ));

        // ==========================================
        // 【第 3 行】：心律 (Heart Rhythm)
        // ==========================================
        // BCG设备通常难以直接诊断心律失常，这里我们用 HRV 变异系数做粗略的临床模拟
        // 如果变异性过大(>150ms)，可能存在早搏或房颤，导致节律不齐
        String rhythmStatus = "正常";
        String rhythmValue = "整齐";
        if (hrv > 150||hrv < 50) {
            rhythmValue = "不齐";
            rhythmStatus = "异常";
        }

        tableData.add(new BcgIndicatorTableVo(
                "心律",
                rhythmValue,
                "节律波动 50 - 150ms",
                rhythmStatus
        ));

        // ==========================================
        // 【第 4 行】：呼吸频率 (RR) —— 同样受年龄影响
        // ==========================================
        double rr = record.getRespRate() != null ? record.getRespRate() : 0.0;
        // 儿童(12岁及以下)呼吸快于成人
        int minRr = (age <= 12) ? 18 : 12;
        int maxRr = (age <= 12) ? 30 : 20;

        String rrStatus = "正常";
        if (rr < minRr) rrStatus = "偏低";
        else if (rr > maxRr) rrStatus = "偏高";

        tableData.add(new BcgIndicatorTableVo(
                "呼吸频率 (RR)",
                String.format("%.1f 次/分", rr),
                String.format("%d - %d 次/分", minRr, maxRr),
                rrStatus
        ));

        // ==========================================
        // 【第 5 行】：体动 (Body Movement)
        // ==========================================
        // 体动次数影响睡眠深度评估，一般每小时超过5次算作频繁体动
        int move = record.getBodyMovement() != null ? record.getBodyMovement() : 0;
        String moveStatus = move <= 5 ? "轻微" : "频繁";

        tableData.add(new BcgIndicatorTableVo(
                "体动",
                String.format("%d 次/小时", move),
                "0 - 5 次/小时",
                moveStatus
        ));

        // ==========================================
        // 【第 6 行】：睡眠质量 (综合健康得分)
        // ==========================================
        // 满分100，≥80良好，60-79一般，低于60较差
        int score = record.getHealthScore() != null ? record.getHealthScore() : 0;
        String scoreStatus = score >= 80 ? "良好" : (score >= 60 ? "一般" : "较差");

        tableData.add(new BcgIndicatorTableVo(
                "睡眠质量",
                String.format("%d 分", score),
                "≥80 分 (良好)",
                scoreStatus
        ));

        // 将组装好的 6 行数据返回给 Controller
        return tableData;
    }

    @Override
    public BcgTrendVo getBcgTrendData(Long id, Integer days) {
        BcgTrendVo vo = new BcgTrendVo();

        Long subjectId = id;
        log.info("{}", days);
        // 2. 调用刚才写好的强大 SQL，查出这个人最近 N 天的历史汇总数据
        List<Map<String, Object>> list = bcgDataMapper.selectTrendDataBySubjectId(subjectId, days);
        log.info("{}", list);

        // 3. 将数据库返回的 List<Map> 拆解为 ECharts 需要的 4 个独立数组 (X轴1个，Y轴3个)
        for (Map<String, Object> map : list) {

            // 组装 X 轴：日期 (如: "2026-03-04")
            vo.getDates().add(map.get("dateStr").toString());

            // 组装 Y 轴：数值
            // 【安全细节】：MySQL的 AVG 函数可能返回 BigDecimal 或 Double，为了防止类型转换报错，统一用 Number 接，再强转
            Number avgHr = (Number) map.get("avgHr");
            Number avgScore = (Number) map.get("avgScore");

            // 存入对应的数组中
            vo.getHrData().add(avgHr != null ? avgHr.doubleValue() : 0.0);
            vo.getScoreData().add(avgScore != null ? avgScore.intValue() : 0);
        }
        return vo;
    }

    /**
     * 【大屏核心】获取大屏顶部 5 个 KPI 统计卡片数据
     *
     */
    @Override
    public BcgDashboardTopVo getDashboardTopMetrics() {
        BcgDashboardTopVo vo = new BcgDashboardTopVo();

        // 1. 获取最基础的两个总量指标：总采集数 (表内总数据) 和 总处理数 (status=1)
        int total = bcgDataMapper.countTotalBcgData();
        int processed = bcgDataMapper.countProcessedBcgData();
        vo.setTotalCollectCount(total);
        vo.setTotalProcessCount(processed);

        // 防御性编程：如果没有处理过的数据，直接返回 0%，防止出现异常/报错
        if (processed == 0) {
            vo.setHealthyRatio("0.0%");
            vo.setAbnormalRatio("0.0%");
            vo.setHealthyCount(0);
            vo.setAbnormalCount(0);
            vo.setAvgProcessTime("0.0s/次");
            return vo;
        }

        // 2. 查出所有已处理的数据，逐条送入规则引擎进行全身体检
        List<BcgDataListVo> dataList = bcgDataMapper.selectAllProcessedDataForAnalysis();
        int healthyCount = 0;   // 统计健康总数
        int abnormalCount = 0;  // 统计异常总数

        for (BcgDataListVo data : dataList) {
            // 假设一开始是健康的
            boolean isHealthy = true;

            // 提取被测人的年龄，无年龄则默认按 35 岁健康成年人标准要求
            int age = data.getSubjectAge() != null ? data.getSubjectAge() : 35;

            // --- 校验 1：心率 (HR) (完全复用你的各年龄段临床心率标准) ---
            double hr = data.getHeartRate() != null ? data.getHeartRate() : 0.0;
            int minHr, maxHr;
            if (age <= 3) { minHr = 80; maxHr = 130; }
            else if (age <= 12) { minHr = 70; maxHr = 120; }
            else if (age <= 18) { minHr = 60; maxHr = 100; }
            else if (age <= 60) { minHr = 60; maxHr = 80; }
            else { minHr = 60; maxHr = 100; }

            if (hr < minHr || hr > maxHr) isHealthy = false;

            // --- 校验 2：心率变异性 (HRV) & 心律 ---
            // 复用你的判断：HRV < 50 或 > 150 即为异常
            double hrv = data.getHrvSdnn() != null ? data.getHrvSdnn() : 0.0;
            if (hrv < 50 || hrv > 150) isHealthy = false;

            // --- 校验 3：呼吸频率 (RR) ---
            double rr = data.getRespRate() != null ? data.getRespRate() : 0.0;
            int minRr = (age <= 12) ? 18 : 12;
            int maxRr = (age <= 12) ? 30 : 20;
            if (rr < minRr || rr > maxRr) isHealthy = false;

            // --- 校验 4：体动 ---
            // 每小时体动大于5次为频繁
            int movement = data.getBodyMovement() != null ? data.getBodyMovement() : 0;
            if (movement > 5) isHealthy = false;

            // --- 校验 5：睡眠质量得分 ---
            // 满分100，低于80分（一般/较差水平）判定为健康状态不足
            int score = data.getHealthScore() != null ? data.getHealthScore() : 0;
            if (score < 80) isHealthy = false;

            // 最终盘点：只要上面的任意一个界限被突破了，isHealthy就会变为 false
            if (isHealthy) {
                healthyCount++;
            } else {
                abnormalCount++;
            }
        }

        // 3. 计算最终的比率百分比
        // 健康比例 = 健康人数 / 总处理人数 * 100
        double healthyPercent = (double) healthyCount / processed * 100.0;
        // 异常比例 = 100% - 健康比例
        double abnormalPercent = 100.0 - healthyPercent;

        // 4. 将计算结果装填入视图对象，并格式化为带一位小数的字符串
        vo.setHealthyCount(healthyCount);
        vo.setAbnormalCount(abnormalCount);
        vo.setHealthyRatio(String.format("%.1f%%", healthyPercent));
        vo.setAbnormalRatio(String.format("%.1f%%", abnormalPercent));

        // 🌟【动态计算真实平均耗时】
        long totalDurationMs = 0;
        int validDurationCount = 0;
        for (BcgDataListVo data : dataList) {
            if (data.getProcessDuration() != null && data.getProcessDuration() > 0) {
                totalDurationMs += data.getProcessDuration();
                validDurationCount++;
            }
        }

        if (validDurationCount > 0) {
            // 计算平均毫秒数，再除以 1000 转成秒
            double avgSeconds = (totalDurationMs / (double) validDurationCount) / 1000.0;
            // 格式化为 "X.Xs/次" 的形式，比如 "1.2s/次"
            vo.setAvgProcessTime(String.format("%.1fs/次", avgSeconds));
        } else {
            // 兜底防御：如果没有历史耗时记录
            vo.setAvgProcessTime("0.0s/次");
        }

        return vo;
    }

    /**
     * 【大屏图表】获取各指标异常次数分布数据
     * 完美复刻表格的医学判断标准，将 6 项指标的异常次数分类汇总
     */
    @Override
    public List<BcgChartItemVo> getAbnormalChartData() {
        // 1. 复用之前写好的极简 SQL，一次性把所有处理过的数据查出来
        List<BcgDataListVo> dataList = bcgDataMapper.selectAllProcessedDataForAnalysis();

        // 2. 准备 6 个专属的“计数器”
        int hrAbnormal = 0;
        int hrvAbnormal = 0;
        int rhythmAbnormal = 0;
        int rrAbnormal = 0;
        int moveAbnormal = 0;
        int scoreAbnormal = 0;

        // 3. 遍历所有人，逐个指标“阅卷”
        for (BcgDataListVo data : dataList) {
            int age = data.getSubjectAge() != null ? data.getSubjectAge() : 35;

            // --- 阅卷 1：心率 (HR) ---
            double hr = data.getHeartRate() != null ? data.getHeartRate() : 0.0;
            int minHr, maxHr;
            if (age <= 3) { minHr = 80; maxHr = 130; }
            else if (age <= 12) { minHr = 70; maxHr = 120; }
            else if (age <= 18) { minHr = 60; maxHr = 100; }
            else if (age <= 60) { minHr = 60; maxHr = 80; }
            else { minHr = 60; maxHr = 100; }
            if (hr < minHr || hr > maxHr) hrAbnormal++; // 越界就记一次异常

            // --- 阅卷 2 & 3：心率变异性 (HRV) 与 心律 ---
            double hrv = data.getHrvSdnn() != null ? data.getHrvSdnn() : 0.0;
            if (hrv < 50 || hrv > 150) {
                hrvAbnormal++;
                rhythmAbnormal++; // 按照之前的逻辑，HRV失常通常伴随节律异常
            }

            // --- 阅卷 4：呼吸频率 (RR) ---
            double rr = data.getRespRate() != null ? data.getRespRate() : 0.0;
            int minRr = (age <= 12) ? 18 : 12;
            int maxRr = (age <= 12) ? 30 : 20;
            if (rr < minRr || rr > maxRr) rrAbnormal++;

            // --- 阅卷 5：体动 ---
            int movement = data.getBodyMovement() != null ? data.getBodyMovement() : 0;
            if (movement > 5) moveAbnormal++;

            // --- 阅卷 6：睡眠质量 ---
            int score = data.getHealthScore() != null ? data.getHealthScore() : 0;
            if (score < 80) scoreAbnormal++;
        }

        // 4. 将统计好的 6 个总数，组装成前端 ECharts 喜欢的格式
        List<BcgChartItemVo> resultList = new ArrayList<>();
        resultList.add(new BcgChartItemVo("心率(HR)", hrAbnormal));
        resultList.add(new BcgChartItemVo("心率变异性(HRV)", hrvAbnormal));
        resultList.add(new BcgChartItemVo("心律", rhythmAbnormal));
        resultList.add(new BcgChartItemVo("呼吸频率(RR)", rrAbnormal));
        resultList.add(new BcgChartItemVo("体动", moveAbnormal));
        resultList.add(new BcgChartItemVo("睡眠质量", scoreAbnormal));

        return resultList;
    }


    /**
     * 【报告管理】获取报告分页列表
     *
     */
    @Override
    public List<BcgReportListVo> getReportList(BcgDataSearchDto searchDto) {
        return bcgDataMapper.selectReportList(searchDto);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean generateReport(Long recordId) {
        log.info("【报告生成】收到生成报告请求，数据ID: {}", recordId);

        // 1. 查询该笔记录的基础信息
        BcgDataListVo recordInfo = bcgDataMapper.selectBcgDataForReportById(recordId);
        if (recordInfo == null) {
            throw new com.ruoyi.common.exception.ServiceException("生成报告失败：未找到对应的检测记录");
        }

        if (recordInfo.getHeartRate() == null) {
            throw new com.ruoyi.common.exception.ServiceException("生成报告失败：该检测记录尚未完成指标分析");
        }

        // ============================================================
        // 🌟 核心改动：调用已经写好的 getIndicatorTableData，获取 4列详尽表格 数据！
        // ============================================================
        List<BcgIndicatorTableVo> tableData = this.getIndicatorTableData(recordId);
        recordInfo.setHeartRate(recordInfo.getHeartRate());

        // 3. 智能生成综合建议文本
        StringBuilder adviceBuilder = new StringBuilder("智能分析：\n");
        boolean hasAbnormal = false;

        // 遍历表格数据，逐项排查异常
        for (BcgIndicatorTableVo item : tableData) {
            String name = item.getIndicatorName();
            String status = item.getStatus();

            // 只要不是正常状态，就触发专属建议
            if (!"正常".equals(status) && !"轻微".equals(status) && !"良好".equals(status)) {
                hasAbnormal = true;
                adviceBuilder.append("【").append(name).append("】").append(status).append("：");

                // 针对不同指标给出具体的医学建议
                if (name.contains("心率 (HR)")) {
                    if ("偏高".equals(status)) {
                        adviceBuilder.append("夜间交感神经偏活跃，或近期处于疲劳、焦虑状态。建议睡前减少剧烈运动及咖啡因摄入，注意放松身心。\n");
                    } else {
                        adviceBuilder.append("若非长期运动人群，提示夜间心动过缓。若白天伴发头晕、胸闷，建议进一步进行心电图筛查。\n");
                    }
                } else if (name.contains("心率变异性")) {
                    if ("偏低".equals(status)) {
                        adviceBuilder.append("自主神经调节能力有所下降，提示身体处于高压或慢性疲劳状态。建议调整作息，适量增加有氧运动以缓解压力。\n");
                    } else {
                        adviceBuilder.append("数值异常偏高，可能受到心律不齐（如早搏）的信号干扰，建议结合心律指标综合评估。\n");
                    }
                } else if (name.contains("心律")) {
                    adviceBuilder.append("检测到心跳节律不规则，极可能存在早搏、房颤等心律失常风险，强烈建议尽快前往医院进行24小时动态心电图(Holter)复查。\n");
                } else if (name.contains("呼吸频率")) {
                    if ("偏高".equals(status)) {
                        adviceBuilder.append("夜间呼吸偏急促，可能与呼吸道阻力增加或代谢偏高有关，注意保持室内空气流通。\n");
                    } else {
                        adviceBuilder.append("呼吸频率过缓，需警惕睡眠呼吸暂停综合征(OSAHS)，如伴随严重打鼾、憋醒，请及时就医。\n");
                    }
                } else if (name.contains("体动")) {
                    adviceBuilder.append("睡眠期间身体翻动过于频繁，提示深度睡眠不足、睡眠易片段化。建议改善睡眠环境（温度、床具），睡前避免过饱。\n");
                } else if (name.contains("睡眠质量")) {
                    // 睡眠质量作为总评，如果异常简单提一句即可
                    adviceBuilder.append("整体睡眠效率未达标，身体未能得到充分恢复，请引起重视。\n");
                }
            }
        }

        // 如果全部正常，给出健康肯定
        if (!hasAbnormal) {
            adviceBuilder.append("本次检测各项生命体征均在正常范围内，自主神经功能与睡眠节律良好，请继续保持健康的生活方式。\n");
        }


        // 最终转换成字符串传给 PDF 工具类
        String advice = adviceBuilder.toString();




        // 4. 确定文件存储路径
        String subjectName = recordInfo.getSubjectName() != null ? recordInfo.getSubjectName() : "未知";
        String fileName = "bcg_report_" + subjectName + "_" + System.currentTimeMillis() + ".pdf";

        String absolutePath = com.ruoyi.common.config.RuoYiConfig.getUploadPath() + "/reports/" + fileName;
        String relativeUrl = "/profile/upload/reports/" + fileName;

        // 5. 调用 iText 工具类，画出 PDF！(🌟传入的第三个参数换成了 tableData)
        boolean isSuccess = com.ruoyi.web.controller.untils.PdfReportGenerator.createBcgPdf(absolutePath, recordInfo, tableData, advice);
        if (!isSuccess) {
            throw new com.ruoyi.common.exception.ServiceException("生成失败：PDF 文件写入异常，请检查服务器目录权限");
        }

        // 6. 落库保存生成状态和下载路径
        BcgDataRecord updateRecord = new BcgDataRecord();
        updateRecord.setId(recordId);
        updateRecord.setReportStatus(1); // 1 = 已生成
        updateRecord.setReportTime(new java.util.Date());
        updateRecord.setReportFileUrl(relativeUrl);

        return bcgDataMapper.updateBcgData(updateRecord) > 0;
    }

   /*
   *    告警
   * */
    @Override
    public List<BcgAlarmListVo> getAlarmList(BcgAlarmSearchDto searchDto) {
        return bcgDataMapper.selectAlarmList(searchDto);
    }


    @Override
    public void pushGlobalDashboardData() {
        try {
            BcgDashboardWsVo wsVo = new BcgDashboardWsVo();
            wsVo.setTopData(this.getDashboardTopMetrics());
            wsVo.setChartData(this.getAbnormalChartData());

            // 调用 WebSocket Server 进行全网广播 (注意替换为你自己的 WebSocket Server 类路径)
            BcgScreenWebSocketServer.broadcastMessage(wsVo);
            log.info("【大屏 WebSocket】成功触发推送，全局统计数据已发往所有在线大屏终端！");
        } catch (Exception e) {
            log.error("【大屏 WebSocket】推送全局统计数据失败", e);
        }
    }








    /**
     * 【多维健康评估引擎 (点测模式)】
     * 获取用户在选定时间段内的每一次测量记录，并评估其四大维度的健康状态 (0/1)
     */
    /*@Override
    public BcgHealthAssessVo getHealthAssessment(BcgDataSearchDto searchDto) {
        // 1. 只查询底层算法已经处理完成的数据 (有真实的 HR, HRV, Score 等)
        searchDto.setStatus(1);
        List<BcgDataListVo> list = bcgDataMapper.searchBcgData(searchDto);

        BcgHealthAssessVo vo = new BcgHealthAssessVo();
        if (list == null || list.isEmpty()) return vo;

        // 2. 按测量时间从早到晚排序，保证 ECharts X 轴从左到右递增
        list.sort(java.util.Comparator.comparing(BcgDataListVo::getCollectionTime));

        // 时间格式化：精确到分钟，用于区分同一天早晚的多次测量
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM-dd HH:mm");

        // 3. 遍历每一次测试记录
        for (BcgDataListVo data : list) {
            vo.getTimes().add(sdf.format(data.getCollectionTime()));

            // 获取基于 6W+ 底层信号算出来的特征值
            int age = data.getSubjectAge() != null ? data.getSubjectAge() : 35;
            double hr = data.getHeartRate() != null ? data.getHeartRate() : 0.0;
            double hrv = data.getHrvSdnn() != null ? data.getHrvSdnn() : 0.0;
            int move = data.getBodyMovement() != null ? data.getBodyMovement() : 0;
            int score = data.getHealthScore() != null ? data.getHealthScore() : 0;

            // ================= 维度1：心脏健康 =================
            // 判断标准：心率变异性(HRV)在 50-150ms 之间为正常
            boolean isHeartNormal = (hrv >= 50 && hrv <= 150);
            vo.getHeartHealth().add(isHeartNormal ? 1 : 0);

            // ================= 维度2：起居管理 =================
            // 判断标准：由于是5分钟的短测，如果这5分钟内体动 <= 2次 视为安静/正常
            boolean isDailyNormal = (move <= 2);
            vo.getDailyLiving().add(isDailyNormal ? 1 : 0);

            // ================= 维度3：睡眠健康 =================
            // 判断标准：底层算出的综合睡眠评分 >= 80分 视为正常
            boolean isSleepNormal = (score >= 80);
            vo.getSleepHealth().add(isSleepNormal ? 1 : 0);

            // ================= 维度4：心理健康 =================
            // 判断标准：(HRV正常 + 静息心率正常 + 睡眠正常)，3条中满足 ≥2条 即认为心理调节正常

            // 算心率是否正常 (各年龄段基准)
            int minHr = 60, maxHr = 100;
            if (age <= 18) maxHr = 100;
            else if (age <= 60) maxHr = 80;
            boolean isHrNormal = (hr >= minHr && hr <= maxHr);

            int mentalScore = 0;
            if (isHeartNormal) mentalScore++;
            if (isHrNormal) mentalScore++;
            if (isSleepNormal) mentalScore++;

            boolean isMentalNormal = (mentalScore >= 2);
            vo.getMentalHealth().add(isMentalNormal ? 1 : 0);
        }

        return vo;
    }*/


    /**
     * 【多维健康评估引擎 (点测模式)】
     * 获取用户在选定时间段内的每一次测量记录，并评估其四大维度的健康状态 (0/1)
     */
    @Override
    public BcgHealthTrendVo getHealthAssessmentTrend(BcgDataSearchDto searchDto, Integer type) {
        // 1. 强制只查询已经处理完毕的数据 (status = 1)
        searchDto.setStatus(1);

        // 2. 查库
        List<BcgDataListVo> list = bcgDataMapper.searchBcgData(searchDto);

        BcgHealthTrendVo vo = new BcgHealthTrendVo();
        if (list == null || list.isEmpty()) return vo;

        // 3. 按时间从早到晚排序
        list.sort(java.util.Comparator.comparing(BcgDataListVo::getCollectionTime));

        // 🌟 按照前端要求的格式：04-01 00:00
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM-dd HH:mm");

        // 4. 遍历提取数据
        for (BcgDataListVo data : list) {
            vo.getTimes().add(sdf.format(data.getCollectionTime()));

            int age = data.getSubjectAge() != null ? data.getSubjectAge() : 35;
            double hr = data.getHeartRate() != null ? data.getHeartRate() : 0.0;
            double hrv = data.getHrvSdnn() != null ? data.getHrvSdnn() : 0.0;
            int move = data.getBodyMovement() != null ? data.getBodyMovement() : 0;
            int score = data.getHealthScore() != null ? data.getHealthScore() : 0;

            int value = 0; // 默认给 0

            //
            switch (type) {
                case 1: // 心脏健康 (HRV 50-150ms)
                    value = (hrv >= 50 && hrv <= 150) ? 1 : 0;
                    break;
                case 2: // 心理健康 (HRV + 静息心率 + 睡眠，≥2个正常则为1)
                    boolean isHeartNormal = (hrv >= 50 && hrv <= 150);
                    boolean isSleepNormal = (score >= 80);

                    int minHr, maxHr;
                    if (age <= 3) { minHr = 80; maxHr = 130; }
                    else if (age <= 12) { minHr = 70; maxHr = 120; }
                    else if (age <= 18) { minHr = 60; maxHr = 100; }
                    else if (age <= 60) { minHr = 60; maxHr = 80; }
                    else { minHr = 60; maxHr = 100; }
                    boolean isHrNormal = (hr >= minHr && hr <= maxHr);

                    int mentalScore = 0;
                    if (isHeartNormal) mentalScore++;
                    if (isHrNormal) mentalScore++;
                    if (isSleepNormal) mentalScore++;

                    value = (mentalScore >= 2) ? 1 : 0;
                    break;
                case 3: // 起居管理 (体动 <= 5次/小时)
                    value = (move <= 5) ? 1 : 0;
                    break;
                case 4: // 睡眠健康 (评分 >= 80分)
                    value = (score >= 80) ? 1 : 0;
                    break;
            }
            vo.getData().add(value); // 将算好的 0或1 塞进 data 数组
        }

        return vo;
    }


}