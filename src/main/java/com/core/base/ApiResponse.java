package com.core.base;

/**
 * API格式封装
 * @author 星志
 *
 */
//通用controller层数据返回对象.
public class ApiResponse {
    private int code;
    private String message;
    private Object data;
    private boolean more;

    //自定义数据状态返回
    public ApiResponse(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    //无数据,成功返回
    public ApiResponse() {
        this.code = Status.SUCCESS.getCode();
        this.message = Status.SUCCESS.getStandardMessage();
    }
    //无数据,成功返回
    public ApiResponse(int code) {
        this.code = code;
    }

    //无数据,自定义code以及massage返回
    public static ApiResponse ofMessage(int code, String message) {
        return new ApiResponse(code, message, null);
    }

    //自定义数据,成功返回.
    public static ApiResponse ofSuccess(Object data) {
        return new ApiResponse(Status.SUCCESS.getCode(), Status.SUCCESS.getStandardMessage(), data);
    }

    //自定义传入枚举,无数返回
    public static ApiResponse ofStatus(Status status) {
        return new ApiResponse(status.getCode(), status.getStandardMessage(), null);
    }

    //使用枚举封装返回的数据code,以及massage.
    public enum Status {
        SUCCESS(200, "OK"),
        BAD_REQUEST(400, "Bad Request"),
        NOT_FOUND(404, "Not Found"),
        INTERNAL_SERVER_ERROR(500, "Unknown Internal Error"),
        NOT_VALID_PARAM(40005, "Not valid Params"),
        NOT_SUPPORTED_OPERATION(40006, "Operation not supported"),
        NOT_LOGIN(50000, "Not Login");

        private int code;
        private String standardMessage;

        Status(int code, String standardMessage) {
            this.code = code;
            this.standardMessage = standardMessage;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getStandardMessage() {
            return standardMessage;
        }

        public void setStandardMessage(String standardMessage) {
            this.standardMessage = standardMessage;
        }
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    //是否有其他信息
    public boolean isMore() {
        return more;
    }

    public void setMore(boolean more) {
        this.more = more;
    }
}
