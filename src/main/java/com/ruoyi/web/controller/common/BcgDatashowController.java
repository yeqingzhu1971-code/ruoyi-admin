package com.ruoyi.web.controller.common;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.controller.tool.VO.*;
import com.ruoyi.web.service.BcgDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * BCG 可视化数据展示 Controller
 * 提供大屏专属的极简接口：获取人员 -> 获取时间 -> 获取指标
 */
@Tag(name = "BCG可视化接口", description = "用于前端大屏和报告页面的数据展示")
@RestController
@Anonymous
@RequestMapping("/bcg/datashow")
public class BcgDatashowController extends BaseController {

    @Autowired
    private BcgDataService bcgDataService;

    /**
     * 【步骤 1】获取所有有检测数据的人员名单
     */
    @Operation(summary = "1. 获取人员名单(带数据)", description = "指标展示：用于渲染左上角第一个“检测人姓名”下拉框")
    @GetMapping("/subjects")
    public AjaxResult getSubjects() {
        List<BcgSubjectOptionVo> list = bcgDataService.getSubjectOptions();
        return AjaxResult.success(list);
    }

    /**
     * 【步骤 2】联动查询：获取某个人的历史检测时间及心率列表
     */
    @Operation(summary = "2. 获取检测人历史记录下拉选项", description = "指标展示：当用户在第一个下拉框选中人后，调用此接口渲染第二个“检测时间”下拉框")
    @GetMapping("/options/{subjectId}")
    public AjaxResult getOptions(
            @Parameter(description = "检测对象ID(subjectId)", required = true)
            @PathVariable("subjectId") Long subjectId) {
        List<BcgRecordOptionVo> list = bcgDataService.getRecordOptions(subjectId);
        return AjaxResult.success(list);
    }

    /**
     * 【步骤 3】获取核心健康指标数据
     */
    @Operation(summary = "3. 获取核心健康指标数据", description = "指标展示：当用户在第二个下拉框选中具体时间后，调用此接口拿到指标数据并更新仪表盘")
    @GetMapping("/metrics/{id}")
    public AjaxResult getMetrics(
            @Parameter(description = "数据记录的主键ID", required = true)
            @PathVariable("id") Long id) {
        BcgMetricsVo metrics = bcgDataService.getBcgMetricsById(id);

        if (metrics == null) {
            return AjaxResult.error(404, "未找到该条检测记录的指标信息");
        }
        return AjaxResult.success("获取指标成功", metrics);
    }

    /**
     * 【功能】获取波形预览数据
     * 返回结构：originWave(1500点) + processedWave(200点) + 特征标注点
     */
    @Operation(summary = "波形预览：获取波形预览(针对双列CSV优化)")
    @GetMapping("/waveform/{id}")
    public AjaxResult getWaveform(@PathVariable("id") Long id) {
        // 调用 Service 层处理逻辑
        BcgWaveformVo data = bcgDataService.getWaveformData(id);

        if (data == null) {
            return AjaxResult.error("无法加载波形数据");
        }

        return AjaxResult.success("获取波形数据成功", data);
    }


    /**
     * 【最终完美版】获取六项生命体征指标表格数据
     * 支持结合患者年龄动态推导正常参考范围，前端拿到后无需进行任何判断，直接展示即可。
     */
    @Operation(summary = "4. 获取指标表格数据", description = "用于渲染大屏下方【指标名称 | 具体数值 | 正常范围 | 是否异常】的六列表格")
    @GetMapping("/table/{id}")
    public AjaxResult getIndicatorTable(
            @Parameter(description = "数据记录的主键ID", required = true)
            @PathVariable("id") Long id) {

        // 调用 Service 层，拿到带有健康评估状态的 6 行数据
        List<BcgIndicatorTableVo> list = bcgDataService.getIndicatorTableData(id);

        // 封装进标准的若依 AjaxResult 中返回给前端
        return AjaxResult.success("获取指标表格数据成功", list);
    }
    /**
     * 获取生理指标与睡眠质量的历史变化趋势图表数据
     * 支持动态天数查询 (如下拉框选择“近7天”、“近30天”)，完美适配 ECharts
     */
    @Operation(summary = "6. 获取变化趋势折线图数据", description = "指标展示：用于渲染大屏的【生理指标变化趋势】和【睡眠质量变化趋势】折线图")
    @GetMapping("/trend/{id}")
    public AjaxResult getTrend(
            @Parameter(description = "当前数据记录的检测人主键ID", required = true)
            @PathVariable("id") Long id,
            @Parameter(description = "查询的天数范围 (不传默认查询最近 7 天)")
            @RequestParam(value = "days", defaultValue = "7") Integer days) {

        // 调用 Service 生成图表数据包
        BcgTrendVo trendVo = bcgDataService.getBcgTrendData(id, days);

        return AjaxResult.success("获取趋势折线图数据成功", trendVo);
    }


    /**
     * 【大屏核心统计】获取大屏顶部 5 个 KPI 统计卡片数据
     * 该接口会遍历所有已处理数据，动态进行年龄级健康评估
     */
    @Operation(summary = "7. 获取大屏顶部统计数据", description = "返回总采集数、处理数、动态计算出的健康比率和异常比率")
    @GetMapping("/dashboard/top")
    public AjaxResult getDashboardTop() {

        // 调用业务层拿到算好的所有数据
        BcgDashboardTopVo dashboardTopVo = bcgDataService.getDashboardTopMetrics();

        return AjaxResult.success("获取大屏顶部统计成功", dashboardTopVo);
    }


    /**
     * 【大屏下半部】获取各指标异常次数分布 (一键满足柱状图、饼图需求)
     */
    @Operation(summary = "8. 获取指标异常分布数据", description = "返回用于渲染柱状图和饼图的分类异常次数数组")
    @GetMapping("/dashboard/abnormal-chart")
    public AjaxResult getAbnormalChart() {
        List<BcgChartItemVo> chartData = bcgDataService.getAbnormalChartData();
        return AjaxResult.success("获取异常图表分布数据成功", chartData);
    }
}