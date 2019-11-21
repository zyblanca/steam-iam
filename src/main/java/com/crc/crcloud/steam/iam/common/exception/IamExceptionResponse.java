package com.crc.crcloud.steam.iam.common.exception;

/**
 * 测试用例 拦截异常抛出对象
 */
public class IamExceptionResponse {

    private Boolean failed;
    private Integer code;
    private String message;

    public IamExceptionResponse() {

    }

    public IamExceptionResponse(Boolean failed, Integer code, String message) {
        this.failed = failed;
        this.code = code;
        this.message = message;
    }

    public Boolean getFailed() {
        return failed;
    }

    public void setFailed(Boolean failed) {
        this.failed = failed;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
