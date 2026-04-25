package com.ruoyi.web.controller.tool.DTO;

import java.util.HashMap;
import java.util.Map;

/**
 * BCG数据搜索专用传输对象 (DTO)
 */
public class BcgDataSearchDto {
    private Long subjectId;       // 用户ID
    private String subjectName;   // 用户姓名
    private Integer status;       // 数据处理状态
    private String beginTime;     // 采集开始时间
    private String endTime;       // 采集结束时间

    // 搜索参数容器，用于兼容若依的分页和范围查询底层逻辑
    private Map<String, Object> params = new HashMap<>();

    public BcgDataSearchDto() {}

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getBeginTime() { return beginTime; }
    public void setBeginTime(String beginTime) { this.beginTime = beginTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public Map<String, Object> getParams() { return params; }
    public void setParams(Map<String, Object> params) { this.params = params; }
    /**
     * 判断搜索条件是否全部为空 (排除分页参数)
     */

}