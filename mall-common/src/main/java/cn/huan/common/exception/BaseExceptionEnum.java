package cn.huan.common.exception;

public enum BaseExceptionEnum {
    UNKNOWN_EXECPTION(10000,"未知服务异常"),

    VALIDATE_ERROR(10001,"参数校验错误"),
    ;


    private int code;
    private String message;

    BaseExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}