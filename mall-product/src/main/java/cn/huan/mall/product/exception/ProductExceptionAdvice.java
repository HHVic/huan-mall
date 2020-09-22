package cn.huan.mall.product.exception;

import cn.huan.common.exception.BaseExceptionEnum;
import cn.huan.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ProductExceptionAdvice {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R ValidateExceptionHandler(MethodArgumentNotValidException exception) {
        log.error("数据校验出现异常：{}，异常类型：{}", exception.getMessage(), exception.getClass());

        Map<String, String> map = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(item -> {
            //校验错误的字段
            String field = item.getField();
            //校验错误的提示信息
            String message = item.getDefaultMessage();
            map.put(field, message);
        });
        return R.error(BaseExceptionEnum.VALIDATE_ERROR.getCode(),
                BaseExceptionEnum.VALIDATE_ERROR.getMessage()).put("data", map);

    }

    //处理未知异常
    @ExceptionHandler(value = Throwable.class)
    public R exceptionHandler(Throwable throwable) {
        log.error("出现未知异常：{},异常类型：{}", throwable.getMessage(), throwable.getClass());
        throwable.printStackTrace();
        return R.error(BaseExceptionEnum.UNKNOWN_EXECPTION.getCode(),
                BaseExceptionEnum.UNKNOWN_EXECPTION.getMessage());
    }
}
