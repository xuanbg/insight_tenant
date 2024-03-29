package com.insight.base.tenant.common.config;

import com.insight.utils.Json;
import com.insight.utils.ReplyHelper;
import com.insight.utils.pojo.base.BusinessException;
import com.insight.utils.pojo.base.Reply;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.UnexpectedTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LogLevel;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLSyntaxErrorException;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * @author 宣炳刚
 * @date 2023/1/4
 * @remark 全局异常捕获
 */
@ResponseStatus(HttpStatus.OK)
@ResponseBody
@ControllerAdvice
public class GlobalExceptionHandler implements ResponseBodyAdvice<Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 业务异常
     *
     * @param ex 业务异常
     * @return Reply
     */
    @ExceptionHandler(BusinessException.class)
    public Reply handleBusinessException(BusinessException ex) {
        String msg = ex.getMessage();
        logger(LogLevel.INFO, "业务发生异常, " + msg);

        return ReplyHelper.fail(ex.getCode(), msg);
    }

    /**
     * 处理缺少请求参数的异常
     *
     * @param ex 缺少请求参数
     * @return Reply
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Reply handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        String msg = "缺少请求参数, " + ex.getParameterName();
        logger(LogLevel.WARN, msg);

        return ReplyHelper.invalidParam(msg);
    }

    /**
     * 处理不合法的参数的异常
     *
     * @param ex 不合法的参数异常
     * @return Reply
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Reply handleIllegalArgumentException(IllegalArgumentException ex) {
        String msg = "不合法的参数, " + ex.getMessage();
        logger(LogLevel.WARN, msg);

        return ReplyHelper.invalidParam(msg);
    }

    /**
     * 处理参数绑定出现的异常
     *
     * @param ex 参数绑定错误异常
     * @return Reply
     */
    @ExceptionHandler(ServletRequestBindingException.class)
    public Reply handleServletRequestBindingException(ServletRequestBindingException ex) {
        String msg = "参数绑定错误, " + ex.getMessage();
        logger(LogLevel.WARN, msg);

        return ReplyHelper.invalidParam(msg);
    }

    /**
     * 处理参数解析失败的异常
     *
     * @param ex 参数解析失败异常
     * @return Reply
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Reply handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String msg = "参数解析失败, " + ex.getMessage();
        logger(LogLevel.WARN, msg);

        return ReplyHelper.invalidParam(msg);
    }

    /**
     * 参数验证失败的异常
     *
     * @param ex 参数验证失败异常
     * @return Reply
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Reply handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        FieldError error = ex.getBindingResult().getFieldError();
        if (error == null) {
            String msg = "参数解析失败, " + ex.getMessage();
            logger(LogLevel.WARN, msg);

            return ReplyHelper.invalidParam("参数解析失败");
        }

        String parameter = error.getField();
        String msg = "参数绑定失败, " + parameter;
        logger(LogLevel.WARN, msg);

        return ReplyHelper.invalidParam(msg);
    }

    /**
     * 参数绑定失败的异常
     *
     * @param ex 参数绑定失败异常
     * @return Reply
     */
    @ExceptionHandler(BindException.class)
    public Reply handleBindException(BindException ex) {
        FieldError error = ex.getBindingResult().getFieldError();
        if (error == null) {
            String msg = "参数绑定失败, " + ex.getMessage();
            logger(LogLevel.WARN, msg);

            return ReplyHelper.invalidParam("参数绑定失败");
        }

        String parameter = error.getField();
        String msg = "参数绑定失败, " + parameter;
        logger(LogLevel.WARN, msg);

        return ReplyHelper.invalidParam(msg);
    }

    /**
     * 参数类型不匹配的异常
     *
     * @param ex 参数类型不匹配异常
     * @return Reply
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public Reply handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        String msg = "不支持当前媒体类型, " + ex.getMessage();
        logger(LogLevel.WARN, msg);

        return ReplyHelper.invalidParam(msg);
    }

    /**
     * 非预期类型的异常
     *
     * @param ex 非预期类型异常
     * @return Reply
     */
    @ExceptionHandler(UnexpectedTypeException.class)
    public Reply handleUnexpectedTypeException(UnexpectedTypeException ex) {
        String msg = "参数类型不匹配, " + ex.getMessage();
        logger(LogLevel.WARN, msg);

        return ReplyHelper.invalidParam(msg);
    }

    /**
     * 服务调用异常
     *
     * @param ex 服务调用异常
     * @return Reply
     */
    @ExceptionHandler(FeignException.class)
    public Reply handleFeignException(FeignException ex) {
        String msg = "服务调用异常, " + ex.getMessage();
        String requestId = logger(LogLevel.ERROR, msg);

        return ReplyHelper.error(requestId);
    }

    /**
     * 数据库操作出现异常：插入、删除和修改数据的时候，违背数据完整性约束抛出的异常
     *
     * @param ex 违背数据完整性约异常
     * @return Reply
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Reply handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String msg = "数据库操作异常, " + ex.getCause().getMessage();
        String requestId = logger(LogLevel.ERROR, msg);

        return ReplyHelper.error(requestId);
    }

    /**
     * 数据库操作出现异常：插入、删除和修改数据的时候，违背数据完整性约束抛出的异常
     *
     * @param ex 违背数据完整性约异常
     * @return Reply
     */
    @ExceptionHandler(BadSqlGrammarException.class)
    public Reply handleBadSqlGrammarException(BadSqlGrammarException ex) {
        String msg = "数据库操作异常, " + ex.getCause().getMessage();
        String requestId = logger(LogLevel.ERROR, msg);

        return ReplyHelper.error(requestId);
    }

    /**
     * 数据库操作出现异常：插入、删除和修改数据的时候，违背数据完整性约束抛出的异常
     *
     * @param ex 违背数据完整性约异常
     * @return Reply
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Reply handleSqlIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException ex) {
        String msg = "数据库操作异常, " + ex.getCause().getMessage();
        String requestId = logger(LogLevel.ERROR, msg);

        return ReplyHelper.error(requestId);
    }

    /**
     * SQL语句执行错误抛出的异常
     *
     * @param ex SQL语句执行错误的异常
     * @return Reply
     */
    @ExceptionHandler(SQLSyntaxErrorException.class)
    public Reply handleSqlSyntaxErrorException(SQLSyntaxErrorException ex) {
        String msg = "数据库操作异常, " + ex.getCause().getMessage();
        String requestId = logger(LogLevel.ERROR, msg);

        return ReplyHelper.error(requestId);
    }

    /**
     * 空指针抛出的异常
     *
     * @param ex 空指针异常
     * @return Reply
     */
    @ExceptionHandler(NullPointerException.class)
    public Reply handleNullPointerException(NullPointerException ex) {
        String msg = "空指针异常, " + ex.getMessage();
        String requestId = logger(LogLevel.ERROR, msg);
        printStack(requestId, ex);

        return ReplyHelper.error(requestId);
    }

    /**
     * 时间/日期格式错误的异常
     *
     * @param ex 时间/日期格式错误的异常
     * @return Reply
     */
    @ExceptionHandler(DateTimeParseException.class)
    public Reply handleUnexpectedTypeException(DateTimeParseException ex) {
        String msg = "时间/日期格式错误, " + ex.getMessage();
        String requestId = logger(LogLevel.ERROR, msg);
        printStack(requestId, ex);

        return ReplyHelper.error(requestId, msg);
    }

    /**
     * 异步请求超时异常
     *
     * @param ex 异步请求超时异常
     * @return Reply
     */
    @ExceptionHandler(AsyncRequestTimeoutException.class)
    public Reply handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex) {
        String msg = "异步请求超时异常, " + ex.getMessage();
        String requestId = logger(LogLevel.ERROR, msg);
        printStack(requestId, ex);

        return ReplyHelper.error(requestId);
    }

    /**
     * 运行时异常
     *
     * @param ex 运行时异常
     * @return Reply
     */
    @ExceptionHandler(RuntimeException.class)
    public Reply handleRuntimeException(RuntimeException ex) {
        String msg = "运行时异常, " + ex.getMessage();
        String requestId = logger(LogLevel.ERROR, msg);
        printStack(requestId, ex);

        return ReplyHelper.error(requestId);
    }

    /**
     * 服务器异常
     *
     * @param ex 通用异常
     * @return Reply
     */
    @ExceptionHandler(Exception.class)
    public Reply handleException(Exception ex) {
        String msg = "服务器异常, " + ex.getMessage();
        String requestId = logger(LogLevel.ERROR, msg);
        printStack(requestId, ex);

        return ReplyHelper.error(requestId);
    }

    /**
     * 打印日志
     *
     * @param level   日志等级
     * @param message 错误信息
     * @return 请求ID
     */
    private String logger(LogLevel level, String message) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(requestAttributes).getRequest();
        String requestId = request.getHeader("requestId");
        switch (level) {
            case ERROR -> LOGGER.error("requestId: {}. 错误信息: {}", requestId, message);
            case WARN -> LOGGER.warn("requestId: {}. 警告信息: {}", requestId, message);
            default -> LOGGER.info("requestId: {}. 日志信息: {}", requestId, message);
        }

        return requestId;
    }

    /**
     * 打印异常堆栈
     *
     * @param requestId 请求ID
     * @param ex        Exception
     */
    private void printStack(String requestId, Exception ex) {
        String stackTrace = Json.toJson(ex.getStackTrace());
        LOGGER.error("requestId, {}. 异常堆栈, {}", requestId, stackTrace);
    }

    /**
     * 是否支持重写Body
     *
     * @param parameter 方法参数
     * @param converter 消息转换器
     * @return boolean
     */
    @Override
    public boolean supports(MethodParameter parameter, Class<? extends HttpMessageConverter<?>> converter) {
        return true;
    }

    /**
     * 重写Body
     *
     * @param object    Body对象
     * @param parameter 方法参数
     * @param type      媒体类型
     * @param converter 消息转换器
     * @param request   请求数据
     * @param response  响应数据
     * @return Object
     */
    @Override
    public Object beforeBodyWrite(Object object, MethodParameter parameter, MediaType type, Class<? extends HttpMessageConverter<?>> converter, ServerHttpRequest request, ServerHttpResponse response) {
        if (object instanceof Reply) {
            return object;
        }

        if (object instanceof String) {
            return ReplyHelper.success(object).toString();
        }

        return ReplyHelper.success(object);
    }
}
