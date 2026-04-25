package com.ruoyi.web.controller.common;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.controller.tool.DTO.BcgDataSearchDto;
import com.ruoyi.web.controller.tool.VO.BcgHealthTrendVo;
import com.ruoyi.web.service.BcgDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 四大健康维度深度分析专属 Controller
 */
@RestController
@RequestMapping("/bcg/health-analysis")
public class BcgHealthAnalysisController extends BaseController {

    @Autowired
    private BcgDataService bcgDataService;

    /**
     * 获取指定对象在任意时间段内的单维度健康变化趋势
     */
    @GetMapping("/trend/{subjectId}")
    public AjaxResult getHealthTrend(
            @PathVariable("subjectId") Long subjectId,
            @RequestParam("type") Integer type,  //  前端必须传这个参数
            BcgDataSearchDto searchDto) {

        // 1. 参数防御
        if (type == null || type < 1 || type > 4) {
            return AjaxResult.error("请传入正确的指标类型参数(type: 1=心脏, 2=心理, 3=起居, 4=睡眠)");
        }

        searchDto.setSubjectId(subjectId);

        // 2. 调取核心评估引擎
        BcgHealthTrendVo trendData = bcgDataService.getHealthAssessmentTrend(searchDto, type);


        return AjaxResult.success("获取多维健康评估趋势成功", trendData);
    }
}