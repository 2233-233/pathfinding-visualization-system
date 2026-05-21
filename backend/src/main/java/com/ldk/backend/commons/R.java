package com.ldk.backend.commons;

import lombok.Data;

@Data
public class R< T > implements java.io.Serializable{
    private static final long serialVersionUID = 1L;

    // 状态码
    private Integer code;

    // 响应消息
    private String msg;

    // 响应数据
    private T data;

    // ========== 状态码常量 ==========

    // 成功状态码
    public static final Integer SUCCESS = 200;
    public static final Integer CREATED = 201;

    // 客户端错误
    public static final Integer BAD_REQUEST = 400;
    public static final Integer UNAUTHORIZED = 401;
    public static final Integer FORBIDDEN = 403;
    public static final Integer NOT_FOUND = 404;
    public static final Integer VALIDATION_ERROR = 422;

    // 服务器错误
    public static final Integer INTERNAL_SERVER_ERROR = 500;
    public static final Integer SERVICE_UNAVAILABLE = 503;

    // ========== 消息常量 ==========
    public static final String SUCCESS_MSG = "操作成功";
    public static final String ERROR_MSG = "操作失败";
    public static final String UNAUTHORIZED_MSG = "未授权访问";
    public static final String FORBIDDEN_MSG = "权限不足";
    public static final String NOT_FOUND_MSG = "资源不存在";

    // ========== 构造方法 ==========

    private R() {
        // 私有构造，强制使用静态方法
    }

    private R(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    // ========== 通用成功方法 ==========

    /**
     * 成功响应（无数据）
     */
    public static <T> R<T> ok() {
        return new R<>(SUCCESS, SUCCESS_MSG, null);
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> R<T> ok(T data) {
        return new R<>(SUCCESS, SUCCESS_MSG, data);
    }

    /**
     * 成功响应（带自定义消息和数据）
     */
    public static <T> R<T> ok(String msg, T data) {
        return new R<>(SUCCESS, msg, data);
    }

    /**
     * 创建成功（用于新增操作）
     */
    public static <T> R<T> created(T data) {
        return new R<>(CREATED, "创建成功", data);
    }

    // ========== 通用失败方法 ==========

    /**
     * 失败响应
     */
    public static <T> R<T> error() {
        return new R<>(INTERNAL_SERVER_ERROR, ERROR_MSG, null);
    }

    /**
     * 失败响应（带消息）
     */
    public static <T> R<T> error(String msg) {
        return new R<>(INTERNAL_SERVER_ERROR, msg, null);
    }

    /**
     * 失败响应（带状态码和消息）
     */
    public static <T> R<T> error(Integer code, String msg) {
        return new R<>(code, msg, null);
    }

    /**
     * 失败响应（带状态码、消息和数据）
     */
    public static <T> R<T> error(Integer code, String msg, T data) {
        return new R<>(code, msg, data);
    }

    // ========== 特定错误类型 ==========

    /**
     * 参数验证错误
     */
    public static <T> R<T> validationError(String msg) {
        return new R<>(VALIDATION_ERROR, msg, null);
    }

    /**
     * 未授权错误
     */
    public static <T> R<T> unauthorized() {
        return new R<>(UNAUTHORIZED, UNAUTHORIZED_MSG, null);
    }

    public static <T> R<T> unauthorized(String msg) {
        return new R<>(UNAUTHORIZED, msg, null);
    }

    /**
     * 权限不足错误
     */
    public static <T> R<T> forbidden() {
        return new R<>(FORBIDDEN, FORBIDDEN_MSG, null);
    }

    public static <T> R<T> forbidden(String msg) {
        return new R<>(FORBIDDEN, msg, null);
    }

    /**
     * 资源不存在错误
     */
    public static <T> R<T> notFound() {
        return new R<>(NOT_FOUND, NOT_FOUND_MSG, null);
    }

    public static <T> R<T> notFound(String msg) {
        return new R<>(NOT_FOUND, msg, null);
    }

    // ========== 教育平台特定方法 ==========

    /**
     * 算法执行成功（用于算法可视化模块）
     */
    public static <T> R<T> algorithmSuccess(T data) {
        return new R<>(SUCCESS, "算法执行成功", data);
    }

    /**
     * 算法执行失败
     */
    public static <T> R<T> algorithmError(String msg) {
        return new R<>(INTERNAL_SERVER_ERROR, "算法执行失败：" + msg, null);
    }

    /**
     * 题目学习记录保存成功
     */
    public static <T> R<T> learningRecordSuccess(T data) {
        return new R<>(SUCCESS, "学习记录保存成功", data);
    }

    /**
     * 实验记录保存成功
     */
    public static <T> R<T> experimentRecordSuccess(T data) {
        return new R<>(SUCCESS, "实验记录保存成功", data);
    }

    /**
     * 用户登录成功
     */
    public static <T> R<T> loginSuccess(T data) {
        return new R<>(SUCCESS, "登录成功", data);
    }

    /**
     * 用户登录失败
     */
    public static <T> R<T> loginError(String msg) {
        return new R<>(UNAUTHORIZED, "登录失败：" + msg, null);
    }

    /**
     * 用户注册成功
     */
    public static <T> R<T> registerSuccess(T data) {
        return new R<>(CREATED, "注册成功", data);
    }

    /**
     * 用户注册失败
     */
    public static <T> R<T> registerError(String msg) {
        return new R<>(BAD_REQUEST, "注册失败：" + msg, null);
    }

    // ========== 便捷判断方法 ==========

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return SUCCESS.equals(this.code) || CREATED.equals(this.code);
    }

    /**
     * 判断是否失败
     */
    public boolean isError() {
        return !isSuccess();
    }

    /**
     * 链式设置数据（用于Builder模式）
     */
    public R<T> data(T data) {
        this.data = data;
        return this;
    }

    /**
     * 链式设置消息
     */
    public R<T> msg(String msg) {
        this.msg = msg;
        return this;
    }
}
