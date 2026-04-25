package com.ruoyi.web.controller.tool;

/**
 * 电压频率统计实体类
 */
public class BcgVoltageStat {
    private Long recordId;    // 关联记录ID
    private Integer voltageVal; // 电压值 (0-3000)
    private Integer occCount;   // 出现次数
    // 构造方法与 Getter, Setter...


    public BcgVoltageStat(Long recordId, Integer voltageVal, Integer occCount) {
        this.recordId = recordId;
        this.voltageVal = voltageVal;
        this.occCount = occCount;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Integer getVoltageVal() {
        return voltageVal;
    }

    public void setVoltageVal(Integer voltageVal) {
        this.voltageVal = voltageVal;
    }

    public Integer getOccCount() {
        return occCount;
    }

    public void setOccCount(Integer occCount) {
        this.occCount = occCount;
    }
}