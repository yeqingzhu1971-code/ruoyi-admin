package com.ruoyi.web.controller.tool.VO;

import java.util.List;

/**
 * 【大屏 WebSocket】全局统计数据实时推送包装类
 * 作用：将顶部的卡片数据和底部的图表数据打包，一次性推给前端
 */
public class BcgDashboardWsVo {
    
    // 消息类型标识，方便前端区分收到的 WebSocket 消息是干嘛的
    private String type = "DASHBOARD_GLOBAL_UPDATE"; 
    
    // 对应原来的 /dashboard/top 接口数据
    private BcgDashboardTopVo topData;
    
    // 对应原来的 /dashboard/abnormal-chart 接口数据
    private List<BcgChartItemVo> chartData;

    // ================== Getter & Setter ==================
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public BcgDashboardTopVo getTopData() { return topData; }
    public void setTopData(BcgDashboardTopVo topData) { this.topData = topData; }
    public List<BcgChartItemVo> getChartData() { return chartData; }
    public void setChartData(List<BcgChartItemVo> chartData) { this.chartData = chartData; }
}