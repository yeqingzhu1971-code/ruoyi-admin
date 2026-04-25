package com.ruoyi.web.controller.tool.VO;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 大屏顶部统计卡片视图对象
 * 完美对应前端大屏的 5 个数据展示块
 */
@JsonPropertyOrder({"totalCollectCount", "totalProcessCount", "healthyRatio", "healthyCount", "abnormalRatio", "abnormalCount", "avgProcessTime"})
@Schema(description = "大屏顶部统计卡片数据")
public class BcgDashboardTopVo {

    @Schema(description = "卡片1：总采集次数 (底层数据库记录总条数)")
    private Integer totalCollectCount;

    @Schema(description = "卡片2：总处理次数 (状态为处理完成的数据条数)")
    private Integer totalProcessCount;

    @Schema(description = "卡片3主数据：身体健康比率 (格式如 95.8%)")
    private String healthyRatio;

    @Schema(description = "卡片3副数据：健康(有效)数据次数")
    private Integer healthyCount;

    @Schema(description = "卡片4主数据：身体异常比率 (格式如 4.2%)")
    private String abnormalRatio;

    @Schema(description = "卡片4副数据：异常数据次数")
    private Integer abnormalCount;

    @Schema(description = "卡片5：平均处理耗时 ")
    private String avgProcessTime;

    // ========= 标准的 Getter 和 Setter =========
    public Integer getTotalCollectCount() { return totalCollectCount; }
    public void setTotalCollectCount(Integer totalCollectCount) { this.totalCollectCount = totalCollectCount; }
    public Integer getTotalProcessCount() { return totalProcessCount; }
    public void setTotalProcessCount(Integer totalProcessCount) { this.totalProcessCount = totalProcessCount; }
    public String getHealthyRatio() { return healthyRatio; }
    public void setHealthyRatio(String healthyRatio) { this.healthyRatio = healthyRatio; }
    public Integer getHealthyCount() { return healthyCount; }
    public void setHealthyCount(Integer healthyCount) { this.healthyCount = healthyCount; }
    public String getAbnormalRatio() { return abnormalRatio; }
    public void setAbnormalRatio(String abnormalRatio) { this.abnormalRatio = abnormalRatio; }
    public Integer getAbnormalCount() { return abnormalCount; }
    public void setAbnormalCount(Integer abnormalCount) { this.abnormalCount = abnormalCount; }
    public String getAvgProcessTime() { return avgProcessTime; }
    public void setAvgProcessTime(String avgProcessTime) { this.avgProcessTime = avgProcessTime; }
}