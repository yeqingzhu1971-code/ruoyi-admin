package com.ruoyi.web.controller.common;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.controller.tool.BcgSubject;
import com.ruoyi.web.service.BcgSubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/subject")
public class BcgSubjectController {

    @Autowired
    private BcgSubjectService subjectService;

    /**
     * 获取检测对象列表
     */
    @GetMapping("/list")
    public AjaxResult list(@RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "10") int size,
                           String subjectName) {
        Map<String, Object> data = subjectService.getSubjectPage(page, size, subjectName);
        return AjaxResult.success()
                .put("rows", data.get("rows"))
                .put("total", data.get("total"));
    }

    /**
     * 新增检测对象
     */
    @PostMapping("/add")
    public AjaxResult add(@RequestBody BcgSubject subject) {
        return subjectService.addSubject(subject) ? AjaxResult.success("添加成功") : AjaxResult.error("添加失败");
    }

    /**
     * 修改检测对象
     */
    @PutMapping("/update")
    public AjaxResult update(@RequestBody BcgSubject subject) {
        return subjectService.updateSubject(subject) ? AjaxResult.success("修改成功") : AjaxResult.error("修改失败");
    }

    /**
     * 删除检测对象
     */
    @DeleteMapping("/{id}")
    public AjaxResult delete(@PathVariable Long id) {
        return subjectService.deleteSubject(id) ? AjaxResult.success("删除成功") : AjaxResult.error("删除失败");
    }


}