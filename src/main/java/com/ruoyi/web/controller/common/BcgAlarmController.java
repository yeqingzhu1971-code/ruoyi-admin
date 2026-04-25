package com.ruoyi.web.controller.common;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.web.controller.tool.DTO.BcgAlarmSearchDto;
import com.ruoyi.web.controller.tool.VO.BcgAlarmListVo;
import com.ruoyi.web.service.BcgDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * BCG 检测告警管理专属接口
 */
@RestController
@RequestMapping("/bcg/warn")
public class BcgAlarmController extends BaseController {

    @Autowired
    private BcgDataService bcgDataService;

    /**
     * 获取检测告警分页列表
     * 接口路径：GET /bcg/alarm/list
     */
    @GetMapping("/list")
    public TableDataInfo list(BcgAlarmSearchDto searchDto) {
        startPage(); // 开启自动分页
        List<BcgAlarmListVo> list = bcgDataService.getAlarmList(searchDto);
        return getDataTable(list);
    }
}