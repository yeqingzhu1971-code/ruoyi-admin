package com.ruoyi.web.controller.tool.VO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 每次测量记录的四大健康维度 0/1 状态趋势图 VO
 */
public class BcgHealthAssessVo implements Serializable {
    private static final long serialVersionUID = 1L;

    // X轴：每次检测的具体时间 (例如: ["03-01 08:30", "03-01 22:15", "03-02 08:00"])
    private List<String> times = new ArrayList<>();

    // Y轴1 - 心脏健康：1正常，0异常 (依据 HRV)
    private List<Integer> heartHealth = new ArrayList<>();

    // Y轴2 - 心理健康：1正常，0异常 (依据 HRV+静息心率+睡眠，≥2项正常为1)
    private List<Integer> mentalHealth = new ArrayList<>();

    // Y轴3 - 起居管理：1正常，0异常 (依据 体动次数)
    private List<Integer> dailyLiving = new ArrayList<>();

    // Y轴4 - 睡眠健康：1正常，0异常 (依据 睡眠评分)
    private List<Integer> sleepHealth = new ArrayList<>();

    // ======= 标准 Getter & Setter =======
    public List<String> getTimes() { return times; }
    public void setTimes(List<String> times) { this.times = times; }
    public List<Integer> getHeartHealth() { return heartHealth; }
    public void setHeartHealth(List<Integer> heartHealth) { this.heartHealth = heartHealth; }
    public List<Integer> getMentalHealth() { return mentalHealth; }
    public void setMentalHealth(List<Integer> mentalHealth) { this.mentalHealth = mentalHealth; }
    public List<Integer> getDailyLiving() { return dailyLiving; }
    public void setDailyLiving(List<Integer> dailyLiving) { this.dailyLiving = dailyLiving; }
    public List<Integer> getSleepHealth() { return sleepHealth; }
    public void setSleepHealth(List<Integer> sleepHealth) { this.sleepHealth = sleepHealth; }
}