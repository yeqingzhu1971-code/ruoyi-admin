package com.ruoyi.web.controller.tool.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
/**
 * 下拉框选项 VO
 * 包含前端下拉框所需的：记录ID、采集时间、以及心率
 */
@Schema(description = "下拉框选项 - 检测记录时间与心率对象")
public class BcgRecordOptionVo {
    @Schema(description = "数据记录的主键ID (前端绑定的实际value)")
    private Long id; // 数据记录的唯一标识 (下拉框绑定的真实 value)

    @Schema(description = "采集时间 (yyyy-MM-dd HH:mm:ss)")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date collectionTime; // 采集时间

    @Schema(description = "平均心率 (供前端拼接 '时间 + 心率' 文本使用)")
    private Double heartRate; // 平均心率，供前端拼接展示使用


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Date getCollectionTime() { return collectionTime; }
    public void setCollectionTime(Date collectionTime) { this.collectionTime = collectionTime; }

    public Double getHeartRate() { return heartRate; }
    public void setHeartRate(Double heartRate) { this.heartRate = heartRate; }
}