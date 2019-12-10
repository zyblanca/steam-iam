package com.crc.crcloud.steam.iam.common.config;

import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.common.exception.IamExceptionResponse;
import com.netflix.client.ClientException;
import feign.RetryableException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;

@ControllerAdvice
@Slf4j
public class IamControllerExceptionHandler {

    /**
     * 业务异常统一返回code
     */
    private static final Integer BUSINESS_ERROR_CODE = 30000;


    @Autowired
    private MessageSource messageSource;

    /**
     * 拦截处理 Valid 异常
     *
     * @param exception 异常
     * @return ExceptionResponse
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<IamExceptionResponse> process(MethodArgumentNotValidException exception) {
        String message = null;
        String code = "error.methodArgument.notValid";
        try {
            code = exception.getBindingResult().getAllErrors().get(0).getDefaultMessage();
            message = messageSource.getMessage(code, null, locale());
        } catch (Exception e) {
            log.trace("exception process get massage exception {}", exception, e);
        }
        return new ResponseEntity<>(new IamExceptionResponse(true, BUSINESS_ERROR_CODE, message != null ? message : code),
                HttpStatus.OK);
    }


    @ExceptionHandler(IamAppCommException.class)
    public ResponseEntity<IamExceptionResponse> process(IamAppCommException exception) {
        log.info("exception info {}", exception.getTrace());
        String message = null;
        try {
            message = messageSource.getMessage(exception.getCode(), exception.getParameters(), locale());
        } catch (Exception e) {
            log.trace("exception message {}", exception, e);
        }
        return new ResponseEntity<>(
                new IamExceptionResponse(true, BUSINESS_ERROR_CODE, message != null ? message : exception.getMessage()),
                HttpStatus.OK);
    }

    @ExceptionHandler(ClientException.class)
    public ResponseEntity<IamExceptionResponse> process(ClientException exception) {
        log.error("调用远程服务异常", exception);

        return new ResponseEntity<>(
                new IamExceptionResponse(true, BUSINESS_ERROR_CODE, "远程服务异常，请稍后再试"),
                HttpStatus.OK);
    }


    @ExceptionHandler(RetryableException.class)
    public ResponseEntity<IamExceptionResponse> process(RetryableException exception) {
        log.error("调用远程服务异常", exception);

        return new ResponseEntity<>(
                new IamExceptionResponse(true, BUSINESS_ERROR_CODE, "远程服务异常，请稍后再试"),
                HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<IamExceptionResponse> process(Exception exception) {
        log.error("拦截未指定的异常", exception);

        return new ResponseEntity<>(
                new IamExceptionResponse(true, BUSINESS_ERROR_CODE, "服务器异常，请联系开发人员"),
                HttpStatus.OK);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<IamExceptionResponse> process(HttpRequestMethodNotSupportedException exception) {
        log.info("exception info {}", exception.getMessage());
        return new ResponseEntity<>(
                new IamExceptionResponse(true, BUSINESS_ERROR_CODE, "请求方式异常"),
                HttpStatus.OK);
    }
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<IamExceptionResponse> process(MissingServletRequestParameterException exception) {
        log.info("exception info {}", exception.getMessage());
        return new ResponseEntity<>(
                new IamExceptionResponse(true, BUSINESS_ERROR_CODE, "缺少必须参数"),
                HttpStatus.OK);
    }

    /**
     * 返回用户的语言类型
     *
     * @return Locale
     */
    private Locale locale() {
        CustomUserDetails details = DetailsHelper.getUserDetails();
        Locale locale = Locale.SIMPLIFIED_CHINESE;
        if (details != null && "en_US".equals(details.getLanguage())) {
            locale = Locale.US;
        }
        return locale;
    }
}
