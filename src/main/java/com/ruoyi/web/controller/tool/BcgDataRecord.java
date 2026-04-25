package com.ruoyi.web.controller.tool;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

/**
 * BCG数据记录实体类
 * 映射数据库表: bcg_data_record
 */
@JsonInclude(JsonInclude.Include.NON_NULL) // 忽略空值字段，前端响应更精简
public class BcgDataRecord extends BaseEntity {

    // ============= 基础信息字段 =============
    private Long id;                 // 记录ID
    private Long subjectId;          // 关联检测人ID
    private Long deviceId;           // 关联设备ID

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date collectionTime;     // 采集时间

    private Integer sampleRate;      // 采样率(Hz)
    private String fileUrl;          // 原始CSV文件路径
    private Integer status;          // 解析状态(0:处理中, 1:已完成)

    // ============= 算法分析结果字段 =============
    private Double heartRate;        // 平均心率(BPM)
    private Double hrvSdnn;          // 心率变异性(SDNN)
    private Double respRate;         // 呼吸频率(次/分)
    private Integer bodyMovement;    // 体动次数
    private String sleepStatus;      // 睡眠质量评估(深睡/浅睡/躁动)
    private Integer healthScore;     // 综合健康评分(0-100)
    private String remark;           // 分析结论备注

    // 处理耗时 (毫秒)
    private Integer processDuration;

    // ============= 报告管理专属字段 (新增) =============
    @Schema(description = "报告状态(0:未生成, 1:已生成)")
    private Integer reportStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "报告生成时间")
    private Date reportTime;

    @Schema(description = "报告文件物理路径/URL")
    private String reportFileUrl;


    // ============= 构造函数 =============
    public BcgDataRecord(){}

    public BcgDataRecord(Long id, Long subjectId, Long deviceId, Date collectionTime, Integer sampleRate, String fileUrl, Integer status, Double heartRate, Double hrvSdnn, Double respRate, Integer bodyMovement, String sleepStatus, Integer healthScore, String remark, Integer processDuration, Integer reportStatus, Date reportTime, String reportFileUrl) {
        this.id = id;
        this.subjectId = subjectId;
        this.deviceId = deviceId;
        this.collectionTime = collectionTime;
        this.sampleRate = sampleRate;
        this.fileUrl = fileUrl;
        this.status = status;
        this.heartRate = heartRate;
        this.hrvSdnn = hrvSdnn;
        this.respRate = respRate;
        this.bodyMovement = bodyMovement;
        this.sleepStatus = sleepStatus;
        this.healthScore = healthScore;
        this.remark = remark;
        this.processDuration = processDuration;
        this.reportStatus = reportStatus;
        this.reportTime = reportTime;
        this.reportFileUrl = reportFileUrl;
    }

    // ============= Getter & Setter =============
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public Long getDeviceId() { return deviceId; }
    public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }

    public Date getCollectionTime() { return collectionTime; }
    public void setCollectionTime(Date collectionTime) { this.collectionTime = collectionTime; }

    public Integer getSampleRate() { return sampleRate; }
    public void setSampleRate(Integer sampleRate) { this.sampleRate = sampleRate; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

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

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public Integer getProcessDuration() { return processDuration; }
    public void setProcessDuration(Integer processDuration) { this.processDuration = processDuration; }

    // 新增字段的 Getter & Setter
    public Integer getReportStatus() { return reportStatus; }
    public void setReportStatus(Integer reportStatus) { this.reportStatus = reportStatus; }

    public Date getReportTime() { return reportTime; }
    public void setReportTime(Date reportTime) { this.reportTime = reportTime; }

    public String getReportFileUrl() { return reportFileUrl; }
    public void setReportFileUrl(String reportFileUrl) { this.reportFileUrl = reportFileUrl; }

    @Override
    public String toString() {
        return "BcgDataRecord{" +
                "id=" + id +
                ", subjectId=" + subjectId +
                ", deviceId=" + deviceId +
                ", collectionTime=" + collectionTime +
                ", sampleRate=" + sampleRate +
                ", fileUrl='" + fileUrl + '\'' +
                ", status=" + status +
                ", heartRate=" + heartRate +
                ", hrvSdnn=" + hrvSdnn +
                ", respRate=" + respRate +
                ", bodyMovement=" + bodyMovement +
                ", sleepStatus='" + sleepStatus + '\'' +
                ", healthScore=" + healthScore +
                ", remark='" + remark + '\'' +
                ", processDuration=" + processDuration +
                ", reportStatus=" + reportStatus +
                ", reportTime=" + reportTime +
                ", reportFileUrl='" + reportFileUrl + '\'' +
                '}';
    }
}