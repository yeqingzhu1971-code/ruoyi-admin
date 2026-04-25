/*
package com.ruoyi.web.controller.untils;

import java.util.ArrayList;
import java.util.List;

*/
/**
 * BCG 信号处理与指标计算算法工具类
 *//*

public class BcgSignalProcessor {

    // 内部类：表示一个二维数据点 (时间戳 + 电压)
    public static class DataPoint {
        public double time;
        public double value;
        public DataPoint(double time, double value) {
            this.time = time;
            this.value = value;
        }
    }

    // 内部类：算法分析返回的结果封装
    public static class AnalysisResult {
        public double heartRate;     // 平均心率 (BPM)
        public double hrvSdnn;       // 心率变异性 (SDNN, 毫秒)
        public double sampleRate;    // 实际推算出的采样率 (Hz)
        public int peakCount;        // 提取到的有效心跳波峰次数
    }

    */
/**
     * 第一步：滑动平均滤波 (平滑波形，去除高频毛刺)
     *//*

    public static double[] movingAverageFilter(double[] data, int windowSize) {
        double[] smoothed = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            double sum = 0;
            int count = 0;
            int start = Math.max(0, i - windowSize / 2);
            int end = Math.min(data.length - 1, i + windowSize / 2);
            for (int j = start; j <= end; j++) {
                sum += data[j];
                count++;
            }
            smoothed[i] = sum / count;
        }
        return smoothed;
    }

    */
/**
     * 第二步：局部最大值寻峰算法 (找出心跳J波)
     *//*

    public static List<Integer> findPeaks(double[] smoothedData, int windowSize) {
        List<Integer> peaks = new ArrayList<>();
        for (int i = windowSize; i < smoothedData.length - windowSize; i++) {
            boolean isPeak = true;
            for (int j = 1; j <= windowSize; j++) {
                // 如果该点不是左右窗口内的最大值，则说明不是波峰
                if (smoothedData[i] <= smoothedData[i - j] || smoothedData[i] <= smoothedData[i + j]) {
                    isPeak = false;
                    break;
                }
            }
            if (isPeak) {
                peaks.add(i);
                i += windowSize; // 找到波峰后跳过一个窗口，防止重复检测同一个宽峰
            }
        }
        return peaks;
    }

    */
/**
     * 第三步：核心主方法 (分析 BCG 数据并输出医疗指标)
     *//*

    public static AnalysisResult analyzeBcgData(List<DataPoint> dataList) {
        if (dataList == null ) {
            throw new IllegalArgumentException("请上传文件");
        }
        // 2. 【新增拦截】数据量极短拦截
        // 假设至少需要 3 秒的数据才能算出基础心率 (假设最低采样率 50Hz，至少需要 150 行)
        if (dataList.size() < 100) {
            throw new RuntimeException("数据量过少 (" + dataList.size() + "行)，时长不足以提取心率信号，请上传完整记录");
        }
        // 1. 推算实际采样率 (Hz = 总数据量 / 总时长)
        double durationSeconds = dataList.get(dataList.size() - 1).time - dataList.get(0).time;
        double sampleRate = dataList.size() / durationSeconds;

        // 2. 提取电压数组并平滑 (滤波窗口取 0.15 秒的跨度)
        double[] rawVoltages = new double[dataList.size()];
        for (int i = 0; i < dataList.size(); i++) {
            rawVoltages[i] = dataList.get(i).value;
        }
        int smoothWindow = Math.max(1, (int) (sampleRate * 0.15)); 
        double[] smoothedVoltages = movingAverageFilter(rawVoltages, smoothWindow);

        // 3. 寻找心跳波峰 (假设人心跳最快150次/分，间隔最短取 0.4 秒)
        int peakWindow = Math.max(1, (int) (sampleRate * 0.4)); 
        List<Integer> peakIndices = findPeaks(smoothedVoltages, peakWindow);

        // 4. 计算相邻波峰时间差 (即 RR 间期)
        List<Double> rrIntervals = new ArrayList<>();
        for (int i = 1; i < peakIndices.size(); i++) {
            double interval = dataList.get(peakIndices.get(i)).time - dataList.get(peakIndices.get(i - 1)).time;
            // 过滤掉不合理的心跳间隔 (低于0.4秒 或 高于2.0秒)
            if (interval > 0.4 && interval < 2.0) { 
                rrIntervals.add(interval);
            }
        }

        if (rrIntervals.isEmpty()) {
            throw new RuntimeException("未能提取到清晰的心跳信号，可能是文件记录为空或者传感器脱落");
        }

        // 5. 计算平均心率
        double sumInterval = 0;
        for (double interval : rrIntervals) sumInterval += interval;
        double avgInterval = sumInterval / rrIntervals.size();
        
        // 6. 计算心率变异性(SDNN)，并转换为毫秒
        double varianceSum = 0;
        for (double interval : rrIntervals) varianceSum += Math.pow((interval - avgInterval), 2);
        double sdnn = Math.sqrt(varianceSum / rrIntervals.size()) * 1000.0;

        // 7. 组装返回结果
        AnalysisResult result = new AnalysisResult();
        //result.sampleRate = sampleRate;
        result.heartRate = 60.0 / avgInterval; // 60秒除以平均心跳间隔 = 一分钟心跳数
        result.hrvSdnn = sdnn;
        result.peakCount = peakIndices.size();
        return result;
    }
}*/
package com.ruoyi.web.controller.untils;

import java.util.ArrayList;
import java.util.List;

/**
 * BCG 信号处理与多维临床指标计算算法工具类
 */
public class BcgSignalProcessor {

    // 表示一个二维数据点 (真实时间戳 + 电压)
    public static class DataPoint {
        public double time;
        public double value;
        public DataPoint(double time, double value) {
            this.time = time;
            this.value = value;
        }
    }

    // 算法分析返回的多维临床结果封装
    public static class AnalysisResult {
        public double heartRate;     // 平均心率 (BPM)
        public double hrvSdnn;       // 心率变异性全局标准差 (SDNN, ms)
        public double hrvRmssd;      // 心率变异性相邻均方根 (RMSSD, ms) - 新增临床指标
        public double pNn50;         // 相邻心跳差值>50ms的百分比 (pNN50, %) - 新增临床指标
        public double sampleRate;    // 实际推算出的采样率 (Hz)
        public int peakCount;        // 提取到的有效心跳波峰次数
        //
        public int bodyMovementCount;//体动次数
        public double respRate;      //呼吸频率
    }

    /**
     * 第一步：有限脉冲响应(FIR) 滑动平均滤波
     *
     */
    public static double[] movingAverageFilter(double[] data, int windowSize) {
        double[] smoothed = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            double sum = 0;
            int count = 0;
            int start = Math.max(0, i - windowSize / 2);
            int end = Math.min(data.length - 1, i + windowSize / 2);
            for (int j = start; j <= end; j++) {
                sum += data[j];
                count++;
            }
            smoothed[i] = sum / count;
        }
        return smoothed;
    }



    /**
     * 第二步：基于【统计学自适应阈值】的形态学局部寻峰算法
     *
     */
    public static List<Integer> findPeaks(double[] smoothedData, int windowSize) {
        List<Integer> peaks = new ArrayList<>();

        // 1. 计算全局基线均值
        double mean = 0;
        for (double val : smoothedData) {
            mean += val;
        }
        mean /= smoothedData.length;

        // 2. 计算信号的标准差 (Standard Deviation)
        double varianceSum = 0;
        for (double val : smoothedData) {
            varianceSum += Math.pow(val - mean, 2);
        }
        double stdDev = Math.sqrt(varianceSum / smoothedData.length);

        //
        // 依据：正常心跳的 J 波通常在均值上方 0.5~1.0 个标准差之间。
        // 即便有几个巨大的翻身波，也不会导致标准差无限放大，完美保护了正常心跳不被漏检！
        double dynamicThreshold = mean + stdDev * 0.5;

        // 3. 滑动窗口寻找局部极大值
        for (int i = windowSize; i < smoothedData.length - windowSize; i++) {
            // 过滤掉连及格线都没碰到的微小基线起伏
            if (smoothedData[i] < dynamicThreshold) continue;

            boolean isPeak = true;
            for (int j = 1; j <= windowSize; j++) {
                if (smoothedData[i] <= smoothedData[i - j] || smoothedData[i] <= smoothedData[i + j]) {
                    isPeak = false;
                    break;
                }
            }
            if (isPeak) {
                peaks.add(i);
                i += windowSize; // 生理绝对不应期：找到后跳过一个窗口
            }
        }
        return peaks;
    }

    /**
     * 第三步：核心主方法
     */
    public static AnalysisResult analyzeBcgData(List<DataPoint> dataList) {
        if (dataList == null || dataList.size() < 100) {
            throw new RuntimeException("数据量不足以提取心率信号，请上传完整记录");
        }

        // 1. 动态推算实际采样率
        double durationSeconds = dataList.get(dataList.size() - 1).time - dataList.get(0).time;
        double sampleRate = dataList.size() / durationSeconds;

        // 2. 提取电压并进行 FIR 滑动平滑 (0.15秒窗口)
        double[] rawVoltages = new double[dataList.size()];
        for (int i = 0; i < dataList.size(); i++) rawVoltages[i] = dataList.get(i).value;
        int smoothWindow = Math.max(1, (int) (sampleRate * 0.15));
        double[] smoothedVoltages = movingAverageFilter(rawVoltages, smoothWindow);

        // 3. 动态阈值寻峰 (0.4秒生理不应期)
        int peakWindow = Math.max(1, (int) (sampleRate * 0.4));
        List<Integer> peakIndices = findPeaks(smoothedVoltages, peakWindow);

        // 4. 🌟【核心护城河】使用绝对物理时间戳计算 RR 间期，抗硬件丢包
        List<Double> rrIntervals = new ArrayList<>();
        for (int i = 1; i < peakIndices.size(); i++) {
            // 坚决不使用数组下标差，只使用真实的物理时间戳相减
            double interval = dataList.get(peakIndices.get(i)).time - dataList.get(peakIndices.get(i - 1)).time;
            if (interval > 0.4 && interval < 2.0) {
                rrIntervals.add(interval);
            }
        }

        if (rrIntervals.isEmpty()) {
            throw new RuntimeException("未能提取到清晰的心跳信号");
        }

        // 5. 计算多维临床健康指标
        double sumInterval = 0;
        for (double interval : rrIntervals) sumInterval += interval;
        double avgInterval = sumInterval / rrIntervals.size();

        // SDNN (全局变异性)
        double varianceSum = 0;
        for (double interval : rrIntervals) varianceSum += Math.pow((interval - avgInterval), 2);
        double sdnn = Math.sqrt(varianceSum / rrIntervals.size()) * 1000.0;

        // RMSSD (相邻均方根，反映副交感神经活性) & pNN50
        double sumSquaredDiff = 0;
        int nn50Count = 0;
        for (int i = 1; i < rrIntervals.size(); i++) {
            double diff = Math.abs(rrIntervals.get(i) - rrIntervals.get(i - 1));
            sumSquaredDiff += (diff * diff);
            if (diff * 1000.0 > 50) nn50Count++;
        }
        double rmssd = (rrIntervals.size() > 1) ? Math.sqrt(sumSquaredDiff / (rrIntervals.size() - 1)) * 1000.0 : 0;
        double pNn50 = (rrIntervals.size() > 1) ? ((double) nn50Count / (rrIntervals.size() - 1)) * 100.0 : 0;
        int movements = calculateBodyMovement(rawVoltages, sampleRate);
        double respRate = calculateRespRate(rawVoltages, dataList, sampleRate);
        // 6. 组装结果
        AnalysisResult result = new AnalysisResult();
        //result.sampleRate = sampleRate;
        result.heartRate = 60.0 / avgInterval;
        result.hrvSdnn = sdnn;
        result.hrvRmssd = rmssd;
        result.pNn50 = pNn50;
        result.peakCount = peakIndices.size();
        result.bodyMovementCount = movements;
        result.respRate = respRate;
        return result;
    }


    /**
     *  计算体动次数 (Body Movement Detection)
     * 原理：体动产生的机械波振幅远大于正常心跳。使用全局标准差(SD)作为基准，
     * 设定 4倍SD 为体动高能阈值，并加入 2秒的生理冷却期防止单次体动被重复计数。
     */
    public static int calculateBodyMovement(double[] rawVoltages, double sampleRate) {
        if (rawVoltages == null || rawVoltages.length == 0) return 0;

        // 1. 计算全局均值
        double mean = 0;
        for (double v : rawVoltages) mean += v;
        mean /= rawVoltages.length;

        // 2. 计算信号的标准差 (反映正常信号的波动范围)
        double varianceSum = 0;
        for (double v : rawVoltages) {
            varianceSum += Math.pow(v - mean, 2);
        }
        double stdDev = Math.sqrt(varianceSum / rawVoltages.length);

        // 3. 设定动态阈值：超过均值 ± 4倍标准差 视为剧烈体动
        double movementThreshold = stdDev * 4.0;

        int movementCount = 0;
        int cooldown = 0;
        // 冷却期设定：假设一次翻身持续 2 秒，期间的剧烈波动只算 1 次体动
        int cooldownPeriod = (int) (sampleRate * 2.0);

        // 4. 遍历原始信号寻找体动破界点
        for (int i = 0; i < rawVoltages.length; i++) {
            if (cooldown > 0) {
                cooldown--; // 处于冷却期，跳过检测
                continue;
            }
            // 如果某点电压偏离均值超过了体动阈值
            if (Math.abs(rawVoltages[i] - mean) > movementThreshold) {
                movementCount++;
                cooldown = cooldownPeriod; // 触发冷却期
            }
        }

        return movementCount;
    }


    /**
     * 提取呼吸频率 (Respiratory Rate)
     * 原理：呼吸波的频率（0.15~0.5Hz）远低于心跳波（0.8~2.0Hz）。
     * 我们使用大窗口（约1.2秒）的滑动平均滤波，相当于一个重度低通滤波器，
     * 将高频的心跳J波完全“压扁抹平”，剩下的低频基线起伏就是呼吸波。
     */
    public static double calculateRespRate(double[] rawVoltages, List<DataPoint> dataList, double sampleRate) {
        // 1. 重度低通滤波：使用 1.2 秒的大窗口抹平心跳
        int lpWindow = Math.max(1, (int) (sampleRate * 1.2));
        if (rawVoltages.length < lpWindow) return 16.0; // 数据太短兜底

        double[] respSignal = movingAverageFilter(rawVoltages, lpWindow);

        // 2. 寻找呼吸波峰：假设最高呼吸频率为 40次/分，最短间隔约为 1.5 秒
        int respPeakWindow = Math.max(1, (int) (sampleRate * 1.5));
        List<Integer> respPeaks = new ArrayList<>();

        // 计算呼吸信号均值，用于过滤微弱的底噪起伏
        double mean = 0;
        for (double v : respSignal) mean += v;
        mean /= respSignal.length;

        // 滑动寻找局部极大值（呼吸波峰）
        for (int i = respPeakWindow; i < respSignal.length - respPeakWindow; i++) {
            // 过滤掉低于均值的下半轴起伏
            if (respSignal[i] < mean) continue;

            boolean isPeak = true;
            for (int j = 1; j <= respPeakWindow; j++) {
                if (respSignal[i] <= respSignal[i - j] || respSignal[i] <= respSignal[i + j]) {
                    isPeak = false;
                    break;
                }
            }
            if (isPeak) {
                respPeaks.add(i);
                i += respPeakWindow; // 跳过绝对不应期
            }
        }

        // 3. 计算绝对时间差得出呼吸频率
        if (respPeaks.size() < 2) return 16.0; // 找不到两个波峰兜底

        double totalInterval = 0;
        int validCount = 0;
        for (int i = 1; i < respPeaks.size(); i++) {
            // 依然使用真实的物理时间戳相减，免疫物联网丢包！
            double interval = dataList.get(respPeaks.get(i)).time - dataList.get(respPeaks.get(i - 1)).time;
            // 生理约束：正常呼吸间隔应在 1.5秒(40次/分) 到 10秒(6次/分) 之间
            if (interval >= 1.5 && interval <= 10.0) {
                totalInterval += interval;
                validCount++;
            }
        }

        if (validCount == 0) return 16.0;

        // 算出平均一次呼吸花多少秒
        double avgInterval = totalInterval / validCount;
        // 60秒 除以 单次呼吸耗时 = 每分钟呼吸频率
        return 60.0 / avgInterval;
    }
}