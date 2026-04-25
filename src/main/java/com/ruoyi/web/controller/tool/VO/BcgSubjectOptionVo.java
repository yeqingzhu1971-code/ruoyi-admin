package com.ruoyi.web.controller.tool.VO;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 下拉框选项 VO：检测人员
 * 用于下拉框第一个
 */
@Schema(description = "下拉框选项 - 检测人员对象")
public class BcgSubjectOptionVo {

    @Schema(description = "检测人ID (前端绑定的实际value)")
    private Long subjectId;

    @Schema(description = "检测人姓名 (前端展示的文本)")
    private String subjectName;

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
}