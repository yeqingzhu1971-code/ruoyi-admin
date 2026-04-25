package com.ruoyi.web.controller.tool.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

/**
 * 【检测告警】列表展示视图对象 (VO)
 * 专门用于响应前端表格的列数据，屏蔽了底层复杂的表结构，做到了数据扁平化
 */
public class BcgAlarmListVo {

    /**
     *
     * 作用：前端点击表格最右侧的【查看详情】时，需要拿着这个 ID 去请求报告详情接口
     */
    private Long recordId;

    // ================== 基础用户信息 ==================
    private Long userId;             // 受试者ID
    private String userName;         // 受试者姓名
    private Integer userAge;         // 受试者年龄 (用于辅助判断生理指标范围是否合理)

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date collectionTime;     // 该条异常数据的原始采集时间

    private Integer sampleRate;      // 硬件采样率(Hz)

    // ================== 核心告警状态信息 ==================
    /**
     * 告警类型 (数字枚举，前端据此翻译成中文或对应 Icon)
     * 1:心率, 2:HRV, 3:心律, 4:呼吸, 5:体动, 6:睡眠
     */
    private Integer alarmType;

    /**
     * 告警方向指示器 (供前端 UI 渲染红绿箭头使用)
     * 1 : 数值偏高 / 发生过于频繁 (建议渲染红色向上箭头 ↑)
     * -1 : 数值偏低 / 评分欠佳 (建议渲染橙色向下箭头 ↓)
     * 2 : 状态不规则 / 特殊异常 (建议直接文字标红警示)
     */
    private Integer alarmDirection;

    /**
     * 异常数值 (直接展示用)
     * 后端已在 SQL 中完成了单位拼接，如 "184.2 ms" 或 "不规则"，前端无需二次处理
     */
    private String alarmValue;

    /**
     * 异常描述及正常范围说明 (直接展示用)
     * 用于向用户解释为什么会触发此条告警
     */
    private String alarmDesc;

    // ================== Getter & Setter ==================
    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public Integer getUserAge() { return userAge; }
    public void setUserAge(Integer userAge) { this.userAge = userAge; }
    public Date getCollectionTime() { return collectionTime; }
    public void setCollectionTime(Date collectionTime) { this.collectionTime = collectionTime; }
    public Integer getSampleRate() { return sampleRate; }
    public void setSampleRate(Integer sampleRate) { this.sampleRate = sampleRate; }
    public Integer getAlarmType() { return alarmType; }
    public void setAlarmType(Integer alarmType) { this.alarmType = alarmType; }
    public Integer getAlarmDirection() { return alarmDirection; }
    public void setAlarmDirection(Integer alarmDirection) { this.alarmDirection = alarmDirection; }
    public String getAlarmValue() { return alarmValue; }
    public void setAlarmValue(String alarmValue) { this.alarmValue = alarmValue; }
    public String getAlarmDesc() { return alarmDesc; }
    public void setAlarmDesc(String alarmDesc) { this.alarmDesc = alarmDesc; }
}