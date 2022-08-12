package qa.common;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomerException.class)
    public CommonResult<String> exceptionHandler(CustomerException ex) {
        log.error("进入业务异常->" + ex.getRetMessage() + ex.getMessage());
        return CommonResult.failed(ex.getRetMessage());
    }
}
