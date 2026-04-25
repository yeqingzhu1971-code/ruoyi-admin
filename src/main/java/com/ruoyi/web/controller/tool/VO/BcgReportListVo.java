package com.ruoyi.web.controller.tool.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

/**
 * 【报告管理模块】专属视图对象 (VO)
 * 专门用于响应前端“告警与报告”页面的列表展示，与底层数据表解耦。
 */
@Schema(description = "报告列表展示对象")
public class BcgReportListVo {

    @Schema(description = "检测数据主键ID (用于生成和下载报告时的入参)")
    private Long id;

    @Schema(description = "检测对象ID")
    private Long subjectId;

    @Schema(description = "检测对象姓名 (对应前端的'用户姓名')")
    private String subjectName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "数据分析时间 (对应本次检测采集的时间)")
    private Date collectionTime;

    @Schema(description = "报告生成状态 (0: 未生成, 1: 已生成)")
    private Integer reportStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "报告实际生成时间 (如果是未生成状态，此字段为null)")
    private Date reportTime;

    @Schema(description = "报告文件物理路径/URL (用于点击下载和预览，前端不直接展示为列)")
    private String reportFileUrl;



    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public Date getCollectionTime() { return collectionTime; }
    public void setCollectionTime(Date collectionTime) { this.collectionTime = collectionTime; }

    public Integer getReportStatus() { return reportStatus; }
    public void setReportStatus(Integer reportStatus) { this.reportStatus = reportStatus; }

    public Date getReportTime() { return reportTime; }
    public void setReportTime(Date reportTime) { this.reportTime = reportTime; }

    public String getReportFileUrl() { return reportFileUrl; }
    public void setReportFileUrl(String reportFileUrl) { this.reportFileUrl = reportFileUrl; }
}