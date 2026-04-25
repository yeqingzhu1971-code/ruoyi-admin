package com.ruoyi.web.controller.tool.VO;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * BCG 可视化 - 指标展示专属 VO
 */
@Schema(description = "BCG 可视化 - 核心健康指标返回视图")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BcgMetricsVo {

    @Schema(description = "平均心率(BPM)")
    private Double heartRate;

    @Schema(description = "心率变异性(SDNN，毫秒)")
    private Double hrvSdnn;

    @Schema(description = "呼吸频率(次/分)")
    private Double respRate;

    @Schema(description = "体动次数")
    private Integer bodyMovement;

    @Schema(description = "睡眠质量评估 (例如：深睡、浅睡、躁动)")
    private String sleepStatus;

    @Schema(description = "综合健康评分 (0-100分)")
    private Integer healthScore;

    // ======= Get/Set 方法 =======
    public Double getHeartRate() { return heartRate; }
    public void setHeartRate(Double heartRate) { this.heartRate = heartRate; }

    public Double getHrvSdnn() { return hrvSdnn; }
    public void setHrvSdnn(Double hrvSdnn) { this.hrvSdnn = hrvSdnn; }

    public Double getRespRate() { return respRate; }
    public void setRespRate(Double respRate) { this.respRate = respRate; }

    public Integer getBodyMovement() { return bodyMovement; }
    public void setBodyMovement(Integer bodyMovement) { this.bodyMovement = bodyMovement; }

    public String getSleepStatus() { return sleepStatus; }
    public void setSleepStatus(String sleepStatus) { this.sleepStatus = sleepStatus; }

    public Integer getHealthScore() { return healthScore; }
    public void setHealthScore(Integer healthScore) { this.healthScore = healthScore; }
}