/*
package com.ruoyi.web.controller.untils;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.web.controller.tool.VO.BcgDataListVo;
import com.ruoyi.web.controller.tool.VO.BcgIndicatorTableVo;

import java.io.File;
import java.util.List;

*/
/**
 * BCG 物理健康检测报告 PDF 生成工具 (详尽表格版)
 *//*

public class PdfReportGenerator {

    // 自定义颜色：主题绿(正常)、主题红(异常)、表头灰
    private static final DeviceRgb COLOR_NORMAL = new DeviceRgb(46, 204, 113); // 绿色
    private static final DeviceRgb COLOR_ABNORMAL = new DeviceRgb(255, 77, 79); // 红色
    private static final DeviceRgb BG_GRAY = new DeviceRgb(247, 248, 250); // 浅灰色

    */
/**
     * 生成高仿前端详情列表版 PDF 报告
     * 🌟 入参换成了 List<BcgIndicatorTableVo>，完美对接 6 行详尽数据！
     *//*

    public static boolean createBcgPdf(String destPath, BcgDataListVo recordInfo, List<BcgIndicatorTableVo> tableData, String advice) {
        try {
            // 1. 初始化文件路径与文档对象
            File file = new File(destPath);
            file.getParentFile().mkdirs();
            PdfWriter writer = new PdfWriter(destPath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(40, 40, 40, 40);

            // 2. 加载自带的官方中文字体
            PdfFont font = PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H", PdfFontFactory.EmbeddingStrategy.PREFER_NOT_EMBEDDED);
            document.setFont(font);

            // ======================== 绘制头部 ========================
            document.add(new Paragraph("BCG健康检测详细报告")
                    .setFontSize(22).setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(15));

            String subjectName = recordInfo.getSubjectName() != null ? recordInfo.getSubjectName() : "未知";
            String collectionTime = recordInfo.getCollectionTime() != null ? DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, recordInfo.getCollectionTime()) : "未知";

            document.add(new Paragraph("检测对象: " + subjectName + "        采集时间: " + collectionTime)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            // ======================== 绘制 4 列详尽数据表格 ========================
            // 比例：指标名称(20%) | 具体数值(25%) | 正常范围(35%) | 是否异常(20%)
            float[] columnWidths = {20, 25, 35, 20};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            // 添加表头
            table.addHeaderCell(createHeaderCell("指标名称"));
            table.addHeaderCell(createHeaderCell("具体数值"));
            table.addHeaderCell(createHeaderCell("正常范围"));
            table.addHeaderCell(createHeaderCell("是否异常"));

            // 遍历渲染 6 行数据
            for (BcgIndicatorTableVo row : tableData) {
                // 判断状态：如果是“正常”、“良好”、“轻微”，用绿色；其他(偏高、偏低、频繁、异常)用红色
                boolean isNormal = "正常".equals(row.getStatus()) || "良好".equals(row.getStatus()) || "轻微".equals(row.getStatus());
                DeviceRgb statusColor = isNormal ? COLOR_NORMAL : COLOR_ABNORMAL;

                // 指标名称 (黑色)
                table.addCell(new Cell().add(new Paragraph(row.getIndicatorName())).setPadding(8).setVerticalAlignment(VerticalAlignment.MIDDLE));

                // 具体数值 (根据状态变色)
                table.addCell(new Cell().add(new Paragraph(row.getActualValue()).setFontColor(statusColor)).setPadding(8).setVerticalAlignment(VerticalAlignment.MIDDLE));

                // 正常范围 (灰色标注)
                table.addCell(new Cell().add(new Paragraph(row.getNormalRange()).setFontColor(ColorConstants.DARK_GRAY)).setPadding(8).setVerticalAlignment(VerticalAlignment.MIDDLE));

                // 状态文字 (根据状态变色)
                table.addCell(new Cell().add(new Paragraph(row.getStatus()).setFontColor(statusColor).setBold()).setPadding(8).setVerticalAlignment(VerticalAlignment.MIDDLE));
            }
            document.add(table);

            // ======================== 绘制底部建议框 ========================
            document.add(new Paragraph("综合分析与建议")
                    .setFontSize(14).setBold()
                    .setMarginTop(30).setMarginBottom(10));

            Table adviceBox = new Table(1).setWidth(UnitValue.createPercentValue(100));
            Cell adviceCell = new Cell().add(new Paragraph(advice))
                    .setPadding(15)
                    .setMinHeight(80)
                    .setBorder(new SolidBorder(ColorConstants.GRAY, 1));
            adviceBox.addCell(adviceCell);

            document.add(adviceBox);
            document.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 辅助方法：生成表头单元格
    private static Cell createHeaderCell(String text) {
        return new Cell().add(new Paragraph(text).setBold())
                .setBackgroundColor(BG_GRAY)
                .setTextAlignment(TextAlignment.LEFT)
                .setPadding(10);
    }
}*/
package com.ruoyi.web.controller.untils;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.web.controller.tool.VO.BcgDataListVo;
import com.ruoyi.web.controller.tool.VO.BcgIndicatorTableVo;

import java.io.File;
import java.util.List;

/**
 * BCG 物理健康检测报告 PDF 生成工具 (详尽表格 + 双边框版)
 */
public class PdfReportGenerator {

    // 自定义颜色：主题绿(正常)、主题红(异常)、表头灰
    private static final DeviceRgb COLOR_NORMAL = new DeviceRgb(46, 204, 113); // 绿色
    private static final DeviceRgb COLOR_ABNORMAL = new DeviceRgb(255, 77, 79); // 红色
    private static final DeviceRgb BG_GRAY = new DeviceRgb(247, 248, 250); // 浅灰色

    /**
     * 生成高仿前端详情列表版 PDF 报告
     */
    public static boolean createBcgPdf(String destPath, BcgDataListVo recordInfo, List<BcgIndicatorTableVo> tableData, String advice) {
        try {
            // 1. 初始化文件路径与文档对象
            File file = new File(destPath);
            file.getParentFile().mkdirs();
            PdfWriter writer = new PdfWriter(destPath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(40, 40, 40, 40);

            // 2. 加载自带的官方中文字体
            PdfFont font = PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H", PdfFontFactory.EmbeddingStrategy.PREFER_NOT_EMBEDDED);
            document.setFont(font);

            // ======================== 绘制头部 ========================
            document.add(new Paragraph("BCG健康检测详细报告")
                    .setFontSize(22).setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(15));

            String subjectName = recordInfo.getSubjectName() != null ? recordInfo.getSubjectName() : "未知";
            String collectionTime = recordInfo.getCollectionTime() != null ? DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, recordInfo.getCollectionTime()) : "未知";

            document.add(new Paragraph("检测对象: " + subjectName + "        采集时间: " + collectionTime)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            // ======================== 绘制 4 列详尽数据表格 ========================
            float[] columnWidths = {20, 25, 35, 20};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            // 添加表头
            table.addHeaderCell(createHeaderCell("指标名称"));
            table.addHeaderCell(createHeaderCell("具体数值"));
            table.addHeaderCell(createHeaderCell("正常范围"));
            table.addHeaderCell(createHeaderCell("是否异常"));

            // 遍历渲染 6 行数据
            for (BcgIndicatorTableVo row : tableData) {
                boolean isNormal = "正常".equals(row.getStatus()) || "良好".equals(row.getStatus()) || "轻微".equals(row.getStatus());
                DeviceRgb statusColor = isNormal ? COLOR_NORMAL : COLOR_ABNORMAL;

                table.addCell(new Cell().add(new Paragraph(row.getIndicatorName())).setPadding(8).setVerticalAlignment(VerticalAlignment.MIDDLE));
                table.addCell(new Cell().add(new Paragraph(row.getActualValue()).setFontColor(statusColor)).setPadding(8).setVerticalAlignment(VerticalAlignment.MIDDLE));
                table.addCell(new Cell().add(new Paragraph(row.getNormalRange()).setFontColor(ColorConstants.DARK_GRAY)).setPadding(8).setVerticalAlignment(VerticalAlignment.MIDDLE));
                table.addCell(new Cell().add(new Paragraph(row.getStatus()).setFontColor(statusColor).setBold()).setPadding(8).setVerticalAlignment(VerticalAlignment.MIDDLE));
            }
            document.add(table);

            // ======================== 绘制第一个框：AI 智能分析建议 ========================
            document.add(new Paragraph("综合智能分析与建议")
                    .setFontSize(14).setBold()
                    .setMarginTop(25).setMarginBottom(10));

            Table adviceBox = new Table(1).setWidth(UnitValue.createPercentValue(100));
            Cell adviceCell = new Cell().add(new Paragraph(advice)) // 这里直接放入传入的 advice 字符串
                    .setPadding(15)
                    .setMinHeight(70)
                    .setBorder(new SolidBorder(ColorConstants.GRAY, 1));
            adviceBox.addCell(adviceCell);
            document.add(adviceBox);

            // ======================== 绘制第二个框：主治医生补充备注 ========================
            document.add(new Paragraph("医生备注")
                    .setFontSize(14).setBold()
                    .setMarginTop(20).setMarginBottom(10));

            // 提取实体类中的 remark 字段
            String docRemark = recordInfo.getRemark();
            if (docRemark == null || docRemark.trim().isEmpty()) {
                docRemark = "该检测报告暂无医生附加备注。";
            }

            Table remarkBox = new Table(1).setWidth(UnitValue.createPercentValue(100));
            Cell remarkCell = new Cell().add(new Paragraph(docRemark)) // 这里放入医生备注
                    .setPadding(15)
                    .setMinHeight(60) // 医生备注框稍微矮一点点
                    .setBorder(new SolidBorder(ColorConstants.GRAY, 1));
            remarkBox.addCell(remarkCell);
            document.add(remarkBox);

            // ======================== 结束文档 ========================
            document.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 辅助方法：生成表头单元格
    private static Cell createHeaderCell(String text) {
        return new Cell().add(new Paragraph(text).setBold())
                .setBackgroundColor(BG_GRAY)
                .setTextAlignment(TextAlignment.LEFT)
                .setPadding(10);
    }
}