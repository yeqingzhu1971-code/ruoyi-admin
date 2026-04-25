package com.ruoyi.web.mapper;


import com.ruoyi.web.controller.tool.BcgDevice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface BcgDeviceMapper {
    // 1. 分页查询设备
    List<BcgDevice> selectDeviceList(@Param("offset") int offset,
                                     @Param("size") int size,
                                     @Param("deviceCode") String deviceCode,
                                     @Param("deviceName") String deviceName);

    // 2. 统计总条数（分页必备）
    int countDeviceList(@Param("deviceCode") String deviceCode,
                        @Param("deviceName") String deviceName);

    // 3. 新增设备
    int insertDevice(BcgDevice device);

    // 4. 修改设备
    int updateDevice(BcgDevice device);

    // 5. 逻辑删除设备
    int deleteDeviceById(Long id);

    /**
     * 根据设备编号查询数量
     */
    int countByDeviceCode(@Param("deviceCode") String deviceCode);
}