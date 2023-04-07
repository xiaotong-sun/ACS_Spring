package com.xiaotong.acs.domain;

public class Result {
    private Integer code;
    private Object data;
    private String msg;

    private static final int OK = 200;
    private static final int ERROR = 500;


    public Result() {

    }

    public Result(Integer code, Object data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public static Result ok() {
        return new Result(OK, null, null);
    }

    public static Result ok(Object data) {
        return new Result(OK, data, null);
    }

    public static Result error() {
        return new Result(ERROR, null, "Error");
    }

    public static Result error(String msg) {
        return new Result(ERROR, null, msg);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
