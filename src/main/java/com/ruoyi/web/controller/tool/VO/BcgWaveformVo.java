package com.ruoyi.web.controller.tool.VO;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * BCG 波形预览视图对象
 * 职责：封装抽稀后的原始曲线、滤波曲线以及特征点坐标
 */
@Schema(description = "BCG波形预览数据对象 (含抽稀后数据)")
public class BcgWaveformVo implements Serializable {  //序列化，为了Redis缓存

    private static final long serialVersionUID = 1L; //加个版本号

    /**
     * 内部坐标点实体类。
     * 作用：替代 HashMap，避免 Redis 序列化时自动生成脏数据 "@type": "java.util.HashMap"
     */
    @Schema(description = "坐标点对象")
    public static class Point implements Serializable {
        private int x;
        private double y;

        public Point(int x, double y) {
            this.x = x;
            this.y = y;
        }
        public int getX() { return x; }
        public void setX(int x) { this.x = x; }
        public double getY() { return y; }
        public void setY(double y) { this.y = y; }
    }

    @Schema(description = "原始波形 (左图，约1500点)")
    private List<Point> originWave;

    @Schema(description = "预处理波形 (右图，平滑滤波后，约200点)")
    private List<Point> processedWave;

    public List<Point> getOriginWave() { return originWave; }
    public void setOriginWave(List<Point> originWave) { this.originWave = originWave; }
    public List<Point> getProcessedWave() { return processedWave; }
    public void setProcessedWave(List<Point> processedWave) { this.processedWave = processedWave; }
}