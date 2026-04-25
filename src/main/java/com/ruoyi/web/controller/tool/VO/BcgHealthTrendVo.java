package com.ruoyi.web.controller.tool.VO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 统一的单维度健康趋势 VO（适配前端极简渲染）
 */
public class BcgHealthTrendVo implements Serializable {

    // X轴时间 (例如: "04-01 00:00")
    private List<String> times = new ArrayList<>();

    // Y轴状态数据 (例如: [1, 1, 0, 1])
    private List<Integer> data = new ArrayList<>();

    // ======== Getter & Setter ========
    public List<String> getTimes() { return times; }
    public void setTimes(List<String> times) { this.times = times; }
    public List<Integer> getData() { return data; }
    public void setData(List<Integer> data) { this.data = data; }
}