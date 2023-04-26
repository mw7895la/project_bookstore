package project.bookstore.web.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import project.bookstore.domain.exception.UserException;

import java.io.IOException;
import java.net.MalformedURLException;

@Slf4j
@ControllerAdvice
public class ControllerCustomAdvice {

    @ExceptionHandler
    public String userExceptionHandler(UserException e){
        log.error("Exception ", e);
        return "error-page/404";
    }

    @ExceptionHandler
    public String IOExceptionHandler(IOException e){
        log.error("Exception ", e);
        return "error-page/500";
    }

    @ExceptionHandler
    public String MalformedURLExceptionHandler(MalformedURLException e){
        log.error("Exception ", e);
        return "error-page/500";
    }
}
