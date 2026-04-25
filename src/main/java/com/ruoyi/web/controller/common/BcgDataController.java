package com.ruoyi.web.controller.common;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.web.controller.tool.BcgDataRecord;
import com.ruoyi.web.controller.tool.DTO.BcgDataSearchDto;
import com.ruoyi.web.controller.tool.VO.BcgDataListVo;
import com.ruoyi.web.service.BcgDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/bcg/data")
public class BcgDataController extends BaseController {

    @Autowired
    private BcgDataService bcgDataService;

    /**
     * 数据上传接口
     * 对应前端：设备(deviceId)、检测对象(subjectId)、采集时间(collectionTime)、采样率(sampleRate)、文件(file)
     */
    @PostMapping("/upload")
    public AjaxResult upload(MultipartFile file, BcgDataRecord record) {
        try {
            // 校验文件
            if (file == null || file.isEmpty()) {
                return AjaxResult.error("请上传CSV数据文件");
            }
            // 校验业务参数
            if (record.getDeviceId() == null || record.getSubjectId() == null) {
                return AjaxResult.error("设备或检测对象不能为空");
            }

            // 存储文件 + Map统计
            bcgDataService.importBcgData(file, record);

            return AjaxResult.success("数据上传解析成功");
        } catch (Exception e) {
            logger.error("BCG上传解析失败", e);
            return AjaxResult.error("系统错误：" + e.getMessage());
        }
    }


    /** * 功能一：单纯的数据呈现
     * 对应前端：进入页面时的默认加载
     */
    @GetMapping("/list")
    public TableDataInfo list() {
        startPage();
        List<BcgDataListVo> list = bcgDataService.selectAllBcgData();
        return getDataTable(list);
    }


    /**
     * 删除单个BCG数据
     */
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable("id") Long id) {
        return toAjax(bcgDataService.deleteBcgDataById(id));
    }


   /* *//**
     * 条件搜索
     * 接收参数：BcgDataSearchDto
     *//*
    @GetMapping("/search")
    public TableDataInfo search(BcgDataSearchDto searchDto) {
        startPage();
        List<BcgDataListVo> list = bcgDataService.searchBcgData(searchDto);

        // 3. 封装成若依的标准表格返回对象
        TableDataInfo rspData = getDataTable(list);

        // 4. 核心逻辑：如果查询结果为空，修改返回的 msg 提示信息
        if (list == null || list.isEmpty()) {
            rspData.setMsg("未找到匹配的信息，请检查搜索条件是否正确");
        } else {
            rspData.setMsg("查询成功");
        }

        return rspData;

    }*/


    /**
     * 条件搜索 (增加分页支持)
     * 逻辑：如果找到则返回分页后的 TableDataInfo；如果找不到，返回 404 错误
     */
    /*@GetMapping("/search")
    public Object search(BcgDataSearchDto searchDto) {
        // 1. 显式开启分页 (通过请求中的 pageNum 和 pageSize)
        startPage();

        // 2. 执行搜索查询
        List<BcgDataListVo> list = bcgDataService.searchBcgData(searchDto);



        // 3. 封装成若依的标准分页返回对象 (自动包含 total 和 rows)
        TableDataInfo rspData = getDataTable(list);

        if (list == null || list.isEmpty()) {

            System.out.println("空");
            return rspData;
        }
        System.out.println("不为空");
        rspData.setMsg("查询成功");
        return rspData;
    }*/
    /**
     * 条件搜索 (逻辑微调版)
     * 1. 不传任何搜索参数 -> 返回空数组 (code: 200, rows: [])
     * 2. 传了参数但数据库没搜到 -> 返回 404 错误
     */
    @GetMapping("/search")
    public Object search(BcgDataSearchDto searchDto) {
        // 第一阶段：前置校验 - 是否传入了任何有效的搜索维度
        if (isSearchDtoEmpty(searchDto)) {
            startPage();
            List<BcgDataListVo> list = bcgDataService.selectAllBcgData();
            TableDataInfo rspData = getDataTable(list);
            return rspData;
        }

        // 第二阶段：执行真正的搜索逻辑
        startPage();
        List<BcgDataListVo> list = bcgDataService.searchBcgData(searchDto);

        // 第三阶段：结果反馈
        if (list == null || list.isEmpty()) {
            // 只有当用户确实传了参数，但库里没有时，才报 404
            return AjaxResult.error(404, "未找到对应的匹配信息，请检查搜索条件");
        }

        // 正常搜到结果
        TableDataInfo rspData = getDataTable(list);
        rspData.setMsg("查询成功");
        return rspData;
    }


    /**
     * 获取数据详情
     * 对应前端：进入详情页时自动调用
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        BcgDataListVo detail = bcgDataService.selectBcgDataById(id);
        if (detail == null) {
            return AjaxResult.error(404, "未找到该条记录的详细信息");
        }
        return AjaxResult.success(detail);
    }

    /**
     * 保存备注信息
     *
     */
    @Log(title = "BCG数据管理", businessType = BusinessType.UPDATE)
    @PutMapping("/edit")
    public AjaxResult edit(@RequestBody BcgDataRecord record) {
        if (record.getId() == null) {
            return AjaxResult.error("数据ID不能为空");
        }
        return toAjax(bcgDataService.updateBcgData(record));
    }



    /**
     * 判断搜索条件是否全部为空 (排除分页参数)
     */
    private boolean isSearchDtoEmpty(BcgDataSearchDto dto) {
        return dto.getSubjectId() == null
                && (dto.getSubjectName() == null || dto.getSubjectName().isEmpty())
                && dto.getStatus() == null
                && (dto.getBeginTime() == null || dto.getBeginTime().isEmpty())
                && (dto.getEndTime() == null || dto.getEndTime().isEmpty());
    }



}