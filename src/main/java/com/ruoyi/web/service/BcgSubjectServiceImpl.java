package com.ruoyi.web.service;

import com.ruoyi.web.controller.tool.BcgSubject;
import com.ruoyi.web.mapper.BcgDataMapper;
import com.ruoyi.web.mapper.BcgSubjectMapper;
import com.ruoyi.web.service.BcgSubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BcgSubjectServiceImpl implements BcgSubjectService {

    @Autowired
    private BcgSubjectMapper subjectMapper;
    @Autowired
    private BcgDataService bcgDataService;
    @Autowired
    private BcgDataMapper bcgDataMapper;
    @Override
    public Map<String, Object> getSubjectPage(int page, int size, String name) {
        int offset = (page - 1) * size;
        List<BcgSubject> list = subjectMapper.selectSubjectList(offset, size, name);
        int total = subjectMapper.countSubjectList(name);

        Map<String, Object> result = new HashMap<>();
        result.put("rows", list);
        result.put("total", total);
        return result;
    }


    @Override
    public boolean addSubject(BcgSubject subject) {
        // 1. 业务校验：姓名不能为空
        if (subject.getSubjectName() == null || subject.getSubjectName().trim().isEmpty()) {
            throw new com.ruoyi.common.exception.ServiceException("新增失败，检测对象姓名不能为空");
        }



        // 2. 执行插入
        return subjectMapper.insertSubject(subject) > 0;
    }

    @Override
    public boolean updateSubject(BcgSubject subject) {
        return subjectMapper.updateSubject(subject) > 0;
    }

    /**
     * 彻底级联删除人员及其所有数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // 🌟 2. 加上事务，保证同时删除
    public boolean deleteSubject(Long id) {
// 第一步：先清理关联的检测主记录
        bcgDataMapper.deleteBcgDataBySubjectId(id);

        // 第二步：最后把顶层的人员从数据库中彻底抹除
        boolean success = subjectMapper.deleteSubjectById(id) > 0;

        // 🌟 新增代码：人员和他的数据被删除了，立刻通知大屏刷新！
        if (success) {
            bcgDataService.pushGlobalDashboardData();
        }

        return success;
    }
}