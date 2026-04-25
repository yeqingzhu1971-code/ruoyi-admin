package com.ruoyi.web.controller.tool.VO;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.ruoyi.web.controller.tool.BcgDataRecord;

/**
 * BCG数据呈现专用的视图对象
 * 继承 BcgDataRecord 自动获得 id, subjectId, collectionTime, status, remark 等所有字段
 */
@JsonPropertyOrder({ "id", "subjectId", "subjectName", "subjectAge", "deviceId", "deviceName", "collectionTime", "sampleRate", "status", "remark" })
public class BcgDataListVo extends BcgDataRecord {

    // 只需要定义父类（BcgDataRecord）中没有的“扩展字段”
    private String subjectName;
    private Integer subjectAge;
    private String deviceName;

    // 无参构造
    public BcgDataListVo() {
        super();
    }

    // 只需写这三个扩展字段的 Getter 和 Setter
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public Integer getSubjectAge() { return subjectAge; }
    public void setSubjectAge(Integer subjectAge) { this.subjectAge = subjectAge; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }




    // 注意：id, collectionTime, remark 等字段不需要在这里重复写！
    // 它们已经通过 extends 从父类拿到了。
}