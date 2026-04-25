package com.ruoyi.web.service; //

import com.ruoyi.web.controller.tool.BcgDevice;
import com.ruoyi.web.mapper.BcgDeviceMapper;
import com.ruoyi.web.service.BcgDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BcgDeviceServiceImpl implements BcgDeviceService {

    @Autowired
    private BcgDeviceMapper deviceMapper;

    /**
     * 分页查询
     */
    @Override
    public Map<String, Object> getDevicePage(int page, int size, String code, String name) {
        int offset = (page - 1) * size;
        List<BcgDevice> list = deviceMapper.selectDeviceList(offset, size, code, name);
        //System.out.println(list);
        int total = deviceMapper.countDeviceList(code, name);

        Map<String, Object> result = new HashMap<>();
        result.put("rows", list);
        result.put("total", total);
        return result;
    }

    /**
     * 新增设备
     */
    @Override
    public boolean addDevice(BcgDevice device) {
        // 1. 唯一性校验
        int count = deviceMapper.countByDeviceCode(device.getDeviceCode());
        if (count > 0) {

            throw new com.ruoyi.common.exception.ServiceException("设备编号 " + device.getDeviceCode() + " 已存在，请更换！");
        }

        // 2. 校验通过再执行插入
        int rows = deviceMapper.insertDevice(device);
        return rows > 0;
    }

    /**
     * 修改设备
     */
    @Override
    public boolean updateDevice(BcgDevice device) {
        // 确保传入的对象包含 ID
        if (device.getId() == null) {
            return false;
        }
        int rows = deviceMapper.updateDevice(device);
        return rows > 0;
    }

    /**
     * 逻辑删除设备
     */
    @Override
    public boolean deleteDevice(Long id) {
        // 调用 mapper 执行逻辑删除 (is_deleted = 1)
        int rows = deviceMapper.deleteDeviceById(id);
        return rows > 0;
    }
}