package com.ruoyi.web.service;

import com.ruoyi.web.controller.tool.BcgSubject;
import java.util.Map;

public interface BcgSubjectService {
    Map<String, Object> getSubjectPage(int page, int size, String name);
    boolean addSubject(BcgSubject subject);
    boolean updateSubject(BcgSubject subject);
    boolean deleteSubject(Long id);
}