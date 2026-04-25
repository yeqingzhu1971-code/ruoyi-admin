/*
package com.ruoyi.web.controller.tool;

import lombok.Data;

*/
/**
 * 统一返回结果
 * @param <T> 响应数据的类型
 *//*

@Data
public class Result<T> {
    private int code;      // 状态码：200 成功，500 失败
    private String msg;    // 提示信息
    private T data;        // 返回的具体数据

    */
/**
     * 成功返回 - 携带数据
     *//*

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMsg("操作成功");
        r.setData(data);
        return r;
    }

    */
/**
     * 成功返回 - 不带数据 (用于新增、修改、删除)
     *//*

    public static <T> Result<T> success() {
        return success(null);
    }

    */
/**
     * 失败返回 - 自定义错误信息
     * @param message 具体的失败原因
     *//*

    public static <T> Result<T> error(String message) {
        Result<T> r = new Result<>();
        r.setCode(500);
        r.setMsg(message);
        r.setData(null);
        return r;
    }

    */
/**
     * 失败返回 - 默认错误信息
     *//*

    public static <T> Result<T> error() {
        return error("操作失败");
    }
}
*/
package com.ruoyi.web.controller.tool;

import java.util.HashMap;
import java.util.Map;


public class Result<T> {
    private int code;
    private String msg;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }


    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMsg("操作成功");
        r.setData(data);
        return r;
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> error(String message) {
        Result<T> r = new Result<>();
        r.setCode(500);
        r.setMsg(message);
        return r;
    }

    // --- 新增：专门给分页列表用的方法，让它返回 Map 以实现平铺 ---
    public static Map<String, Object> page(Map<String, Object> pageData) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("msg", "查询成功");
        map.put("rows", pageData.get("rows"));
        map.put("total", pageData.get("total"));
        return map;
    }
}