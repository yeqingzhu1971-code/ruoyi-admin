package com.ruoyi.web.controller.common;

import com.ruoyi.common.core.domain.AjaxResult; // 1. 引入若依官方返回类
import com.ruoyi.web.controller.tool.BcgDevice;
import com.ruoyi.web.service.BcgDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 设备管理 具体操作增添改删
 */
@RestController
@RequestMapping("/device")
public class BcgDeviceController {

    @Autowired
    private BcgDeviceService deviceService;

    /**
     * 获取简单列表（用于下拉框等不需要分页的场景）
     */
    /*@GetMapping("/device/list")
    public AjaxResult getDeviceListSimple(
            @RequestParam(required = false) String deviceCode,
            @RequestParam(required = false) String deviceName) {
        // 直接返回 data 字段包裹的数据
        return AjaxResult.success(deviceService.getDevicePage(1, 1000, deviceCode, deviceName));
    }*/

    /**
     * 1. 获取列表 (带分页)
     * 注意：为了适配若依前端表格，需要将 rows 和 total 从 data 中“平铺”出来
     */
    @GetMapping("/list")
    public AjaxResult list(@RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "10") int size,
                           String deviceCode, String deviceName) {
        Map<String, Object> data = deviceService.getDevicePage(page, size, deviceCode, deviceName);

        // 若依写法：success() 返回一个 map，通过 put 把分页必须的字段塞进去
        return AjaxResult.success()
                .put("rows", data.get("rows"))
                .put("total", data.get("total"));
    }

    /**
     * 2. 新增设备
     */
    @PostMapping("/add")
    public AjaxResult add(@RequestBody BcgDevice device) {
        boolean success = deviceService.addDevice(device);
        return success ? AjaxResult.success("新增成功") : AjaxResult.error("新增失败");
    }

    /**
     * 3. 设备编辑
     */
    @PutMapping("/update")
    public AjaxResult update(@RequestBody BcgDevice device) {
        boolean success = deviceService.updateDevice(device);
        return success ? AjaxResult.success("修改成功") : AjaxResult.error("修改失败");
    }

    /**
     * 4. 行内删除
     */
    @DeleteMapping("/{id}")
    public AjaxResult delete(@PathVariable Long id) {
        boolean success = deviceService.deleteDevice(id);
        return success ? AjaxResult.success("删除成功") : AjaxResult.error("删除失败");
    }
}