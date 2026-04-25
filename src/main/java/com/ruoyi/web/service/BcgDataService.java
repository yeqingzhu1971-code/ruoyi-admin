package com.ruoyi.web.service;

import com.ruoyi.web.controller.tool.BcgDataRecord;
import com.ruoyi.web.controller.tool.DTO.BcgAlarmSearchDto;
import com.ruoyi.web.controller.tool.DTO.BcgDataSearchDto;
import com.ruoyi.web.controller.tool.VO.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BcgDataService {
    /**
     * 上传并解析BCG原始数据文件
     * @param file 前端上传的CSV文件
     * @param record 包含设备ID、对象ID、采样率、采集时间等信息的实体
     */
    void importBcgData(MultipartFile file, BcgDataRecord record) throws Exception;

    public List<BcgDataListVo> selectAllBcgData();


    public List<BcgDataListVo> searchBcgData(BcgDataSearchDto searchDto);

    //通过id删除数据记录
    int deleteBcgDataById(Long id);

    /**
     *  /数据详情，并且可以保存备注
     */
    public BcgDataListVo selectBcgDataById(Long id);
    public int updateBcgData(BcgDataRecord record);



    /** 1. 获取有数据的人员列表 */
    List<BcgSubjectOptionVo> getSubjectOptions();

    /** 2. 获取指定人员的检测时间列表 */
    List<BcgRecordOptionVo> getRecordOptions(Long subjectId);

    /** 3. 获取单次检测的核心健康指标 */
    BcgMetricsVo  getBcgMetricsById(Long id);


    BcgWaveformVo getWaveformData(Long id);
    /**
     * 获取生命体征指标汇总表数据 (大屏核心表格)
     * @param id 数据记录的唯一ID
     * @return 包含 6 行标准格式的表格数据列表
     */
    List<BcgIndicatorTableVo> getIndicatorTableData(Long id);

    /**
     * 获取大屏趋势折线图表数据
     * @param id 当前查看的数据记录ID (用于反查受试者)
     * @param days 查询天数范围 (如 7天)
     * @return 封装好的 ECharts 数据结构
     */
    BcgTrendVo getBcgTrendData(Long id, Integer days);


    /**
     * 【大屏统计】获取大屏顶部 5 个核心 KPI 卡片数据
     * @return 包含总数、健康率、异常率等统计结果的对象
     */
    BcgDashboardTopVo getDashboardTopMetrics();

    /**
     * 【大屏图表】获取各指标异常次数分布数据 (供柱状图和饼图使用)
     */
    List<BcgChartItemVo> getAbnormalChartData();


    /**
     * 【报告管理】获取报告分页列表
     */
    List<BcgReportListVo> getReportList(BcgDataSearchDto searchDto);

    public boolean generateReport(Long recordId);

    List<BcgAlarmListVo> getAlarmList(BcgAlarmSearchDto searchDto);


    // ========================================================
    // 🌟 WebSocket 专属：主动向前端推送全局统计数据
    // ========================================================
    void pushGlobalDashboardData();
    /**
     * 根据自定义时间段、人员和具体的指标类型，获取对应的 0/1 状态趋势图
     * @param type 1=心脏 2=心理 3=起居 4=睡眠
     */
    BcgHealthTrendVo getHealthAssessmentTrend(BcgDataSearchDto searchDto, Integer type);
    //下面这个舍弃了
    //BcgHealthAssessVo getHealthAssessment(BcgDataSearchDto searchDto);
}