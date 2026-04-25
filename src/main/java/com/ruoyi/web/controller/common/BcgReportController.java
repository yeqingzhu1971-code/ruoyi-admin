package com.ruoyi.web.controller.common;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.web.controller.tool.DTO.BcgDataSearchDto;
import com.ruoyi.web.controller.tool.VO.BcgDataListVo;
import com.ruoyi.web.controller.tool.VO.BcgReportListVo; // 🌟 引入全新的 VO
import com.ruoyi.web.mapper.BcgDataMapper;
import com.ruoyi.web.service.BcgDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.ruoyi.framework.datasource.DynamicDataSourceContextHolder.log;

/**
 * BCG 报告管理专属接口
 */
@RestController
@RequestMapping("/bcg/report")
public class BcgReportController extends BaseController {

    @Autowired
    private BcgDataService bcgDataService;
    @Autowired
    private BcgDataMapper bcgDataMapper;

    /**
     * 1. 获取报告管理列表 (带分页和条件查询)
     * 接口路径：GET /bcg/report/list
     */
    @GetMapping("/list")
    public TableDataInfo list(BcgDataSearchDto searchDto) {
        startPage(); // 开启若依自动分页逻辑
        // 🌟 接收专门的 ReportListVo，拒绝任何冗余字段
        List<BcgReportListVo> list = bcgDataService.getReportList(searchDto);
        return getDataTable(list);
    }

    /**
     * 2. 生成 PDF 健康报告
     * 接口路径：POST /bcg/report/generate/{id}
     */
    @PostMapping("/generate/{id}")
    public AjaxResult generate(@PathVariable("id") Long id) {
        boolean success = bcgDataService.generateReport(id);
        return success ? AjaxResult.success("报告生成成功！") : AjaxResult.error("报告生成失败！");
    }

    /**
     * 3. 获取已生成报告的下载/预览路径
     * 接口路径：GET /bcg/report/download/{id}
     */
    @GetMapping("/download/{id}")
    public AjaxResult download(@PathVariable("id") Long id) {

        BcgDataListVo record = bcgDataMapper.selectBcgDataForReportById(id);
        log.info(record.toString());

        if (record == null || record.getReportFileUrl() == null) {
            return AjaxResult.error("报告文件不存在或尚未生成！");
        }
        return AjaxResult.success("获取成功", record.getReportFileUrl());
    }
}