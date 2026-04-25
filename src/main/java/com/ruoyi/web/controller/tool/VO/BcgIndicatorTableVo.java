package com.ruoyi.web.controller.tool.VO;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * BCG 生命体征指标汇总表 (专门对接大屏下方的六项指标表格)
 * 每一块对应前端表格的一行数据
 */
@Schema(description = "BCG指标汇总表格行数据")
public class BcgIndicatorTableVo {

    @Schema(description = "第一列：指标名称 (如: 心率(HR))")
    private String indicatorName;

    @Schema(description = "第二列：具体数值 (如: 68.2 次/分)")
    private String actualValue;

    @Schema(description = "第三列：正常范围 (动态推导，如: 60 - 100 次/分)")
    private String normalRange;

    @Schema(description = "第四列：是否异常 (如: 正常 / 偏高 / 偏低)")
    private String status;


    public BcgIndicatorTableVo(String indicatorName, String actualValue, String normalRange, String status) {
        this.indicatorName = indicatorName;
        this.actualValue = actualValue;
        this.normalRange = normalRange;
        this.status = status;
    }


    public String getIndicatorName() { return indicatorName; }
    public void setIndicatorName(String indicatorName) { this.indicatorName = indicatorName; }
    public String getActualValue() { return actualValue; }
    public void setActualValue(String actualValue) { this.actualValue = actualValue; }
    public String getNormalRange() { return normalRange; }
    public void setNormalRange(String normalRange) { this.normalRange = normalRange; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}