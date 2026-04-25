package com.ruoyi.web.controller.tool.DTO;

/**
 * 【检测告警】列表搜索条件传输对象 (DTO)
 * 用于接收前端大屏或管理后台传递的过滤条件
 */
public class BcgAlarmSearchDto {

    /**
     * 用户ID (精确匹配)
     * 场景：用于从用户详情页穿透过来，只看某一个人的告警历史
     */
    private Long userId;

    /**
     * 用户姓名 (模糊查询)
     * 场景：用于顶部搜索框按姓名检索
     */
    private String userName;

    /**
     * 告警类型字典枚举值 (精确匹配)
     * 1=心率(HR), 2=心率变异性(HRV), 3=心律, 4=呼吸频率(RR), 5=体动, 6=睡眠质量
     * 规范：弃用 String 中文传输，改用 Integer 提高查询效率及防呆
     */
    private Integer alarmType;

    // ================== Getter & Setter ==================
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public Integer getAlarmType() { return alarmType; }
    public void setAlarmType(Integer alarmType) { this.alarmType = alarmType; }
}