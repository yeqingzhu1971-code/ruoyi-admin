package com.ruoyi.web.mapper;

import com.ruoyi.web.controller.tool.BcgSubject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface BcgSubjectMapper {
    /** 查询列表 */
    List<BcgSubject> selectSubjectList(@Param("offset") int offset, @Param("size") int size, 
                                       @Param("name") String name);

    /** 查询总数 */
    int countSubjectList(@Param("name") String name);

    /** 新增 */
    int insertSubject(BcgSubject subject);

    /** 修改 */
    int updateSubject(BcgSubject subject);

    /** 逻辑删除 */
    int deleteSubjectById(Long id);

    /**
     * 校验检测对象ID是否已存在
     * @param id 检测对象ID
     * @return 结果
     */
    public int countBySubjectId(@Param("id") Long id);
}