package com.ruoyi.web.controller.tool.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * BCG 生理指标与睡眠质量历史趋势图表对象
 * 专门适配 ECharts 的双轴折线图数据结构
 */
@Schema(description = "BCG生理指标与睡眠质量趋势数据")
public class BcgTrendVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "X轴：日期集合 (格式: ['2026-03-04', '2026-03-05'])")
    private List<String> dates = new ArrayList<>();

    @Schema(description = "Y轴(左图)：心率数据集合 (HR)")
    private List<Double> hrData = new ArrayList<>();


    @Schema(description = "Y轴(右图)：睡眠质量分数集合")
    private List<Integer> scoreData = new ArrayList<>();

    // 标准的 Getter 和 Setter 方法
    public List<String> getDates() { return dates; }
    public void setDates(List<String> dates) { this.dates = dates; }
    public List<Double> getHrData() { return hrData; }
    public void setHrData(List<Double> hrData) { this.hrData = hrData; }
    public List<Integer> getScoreData() { return scoreData; }
    public void setScoreData(List<Integer> scoreData) { this.scoreData = scoreData; }
}