package com.ruoyi.web.controller.tool.VO;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 大屏图表通用数据项 (完美适配 ECharts 的饼图和柱状图)
 */
@Schema(description = "图表键值对数据项")
public class BcgChartItemVo {

    @Schema(description = "分类名称 (如：心率(HR))")
    private String name;

    @Schema(description = "异常次数数值")
    private Integer value;

    // 构造函数，方便我们在 Service 里一行代码 new 出来
    public BcgChartItemVo(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    // Getter & Setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getValue() { return value; }
    public void setValue(Integer value) { this.value = value; }
}