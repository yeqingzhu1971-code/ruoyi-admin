package com.ruoyi.web.mapper;

import com.ruoyi.web.controller.tool.BcgDataRecord;
import com.ruoyi.web.controller.tool.BcgVoltageStat;
import com.ruoyi.web.controller.tool.DTO.BcgAlarmSearchDto;
import com.ruoyi.web.controller.tool.DTO.BcgDataSearchDto;
import com.ruoyi.web.controller.tool.VO.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * BCG数据管理 持久层接口
 */
@Mapper
public interface BcgDataMapper {
    /**
     * 新增数据记录主表
     * @param record 主表信息
     * @return 影响行数
     */
    public int insertBcgData(BcgDataRecord record);


    /**
     * 批量插入电压频率统计数据 (核心：处理Map转换后的数据)
     * @param stats 统计数据列表
     * @return 影响行数
     */



    /** 删除单个BCG主记录 */
    public int deleteBcgDataById(Long id);

    /** 级联删除：根据人员ID，删除其名下所有的主检测记录 */
    int deleteBcgDataBySubjectId(Long subjectId);

    List<BcgDataListVo> selectAllBcgData();

    List<BcgDataListVo> searchBcgData(BcgDataSearchDto searchDto);



    /** 根据ID获取详情 VO */
    public BcgDataListVo selectBcgDataById(Long id);

    /** 更新记录（用于保存备注） */
    public int updateBcgData(BcgDataRecord record);

    /** 1. 获取所有存在已解析数据的检测人员（供第一个下拉框） */
    /** 1. 获取所有存在已解析数据的检测人员（供第一个下拉框） */
    List<BcgSubjectOptionVo> selectSubjectOptions();

    /** 2. 根据人员ID，查询他的检测时间及心率（供第二个下拉框） */
    List<BcgRecordOptionVo> selectRecordOptionsBySubjectId(Long subjectId);

    /** 3. 根据记录ID，精准查询生理指标数据（供大屏展示） */
    BcgMetricsVo selectBcgMetricsById(Long id);

    /**
     * 专门用于波形预览：获取包含文件路径的记录详情
     */
    public BcgDataListVo selectBcgDataWithUrlById(Long id);

    /**
     * 根据人员ID查询受试者年龄
     * @param subjectId 受试者ID
     * @return 年龄
     */
    Integer selectSubjectAgeById(Long subjectId);

    /**
     * 专门用于按需解析前，查询记录的当前状态和文件路径 (极轻量查询)
     * @param id 记录ID
     * @return 包含文件路径和状态的视图对象
     */
    public BcgDataListVo selectBcgDataForProcessById(Long id);


    /**
     * 【大屏图表专用】查询某受试者最近 N 天的生理指标日均值趋势
     * 为什么用 Map 接收？因为这只是个中间统计结果，不需要建专门的实体类。
     * * @param subjectId 受试者ID (我们要看的是"这个人"的历史趋势)
     * @param days 最近天数 (如查近7天、近30天)
     * @return 趋势数据列表 (已按日期从小到大排好序)
     */
    // 加上 List<>，告诉 MyBatis 返回的是一个列表，每一行是一个 Map
    List<Map<String, Object>> selectTrendDataBySubjectId(@Param("subjectId") Long subjectId, @Param("days") Integer days);

    /** * 【大屏统计 1】统计总采集次数 (包含未处理的)
     */
    int countTotalBcgData();

    /** * 【大屏统计 2】统计总处理次数 (仅状态为 1 的)
     */
    int countProcessedBcgData();

    /** * 【大屏统计 3】获取所有已处理的数据及其对应的年龄，用于大屏健康率评估
     * 返回的是之前的通用列表对象 BcgDataListVo
     */
    List<BcgDataListVo> selectAllProcessedDataForAnalysis();



    /**
     * 【报告管理专用】查询报告列表
     * @param searchDto 搜索条件 (支持按用户ID、时间范围搜索)
     * @return 专门的报告列表 VO 集合
     */
    List<BcgReportListVo> selectReportList(BcgDataSearchDto searchDto);


    /**
     * 【报告生成专用】根据ID查询包含原始文件路径(fileUrl)和全量生理指标的数据
     * @param id 数据记录ID
     * @return 包含底层数据的全量 VO
     */
    BcgDataListVo selectBcgDataForReportById(Long id);


    /**
     * 【检测告警】查询告警分页列表
     */
    List<BcgAlarmListVo> selectAlarmList(BcgAlarmSearchDto searchDto);

}