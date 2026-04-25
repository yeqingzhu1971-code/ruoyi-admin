package com.ruoyi.web.controller.tool;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

/**
 * 检测对象实体类 bcg_subject
 */
public class BcgSubject {
    private Long id;              // 数据库自增ID
    private String subjectName;   // 检测对象姓名
    private String subjectPhone;  // 检测对象手机号
    private Integer subjectAge;   // 检测对象年龄

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime; // 创建时间

    //private Integer isDeleted;    // 逻辑删除标识 (0正常 1删除)


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getSubjectPhone() { return subjectPhone; }
    public void setSubjectPhone(String subjectPhone) { this.subjectPhone = subjectPhone; }

    public Integer getSubjectAge() { return subjectAge; }
    public void setSubjectAge(Integer subjectAge) { this.subjectAge = subjectAge; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }


}