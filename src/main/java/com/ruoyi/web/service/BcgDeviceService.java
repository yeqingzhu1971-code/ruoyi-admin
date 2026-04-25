package com.ruoyi.web.service;

import com.ruoyi.web.controller.tool.BcgDevice;

import java.util.Map;

/**
 * 设备管理 业务接口
 */
public interface BcgDeviceService {

    /**
     * 分页查询设备列表
     * @param page 当前页码
     * @param size 每页条数
     * @param deviceCode 设备编号（可选搜索条件）
     * @param deviceName 设备名称（可选搜索条件）
     * @return 包含列表数据 rows 和总条数 total 的 Map
     */
    Map<String, Object> getDevicePage(int page, int size, String deviceCode, String deviceName);

    /**
     * 新增设备
     * @param device 设备实体信息
     * @return 成功或失败的布尔值
     */
    boolean addDevice(BcgDevice device);

    /**
     * 修改设备信息
     * @param device 包含 ID 的设备实体信息
     * @return 成功或失败的布尔值
     */
    boolean updateDevice(BcgDevice device);

    /**
     * 逻辑删除设备
     * @param id 设备主键 ID
     * @return 成功或失败的布尔值
     */
    boolean deleteDevice(Long id);
}