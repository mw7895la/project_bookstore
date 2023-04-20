package project.bookstore.web.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
public class ExceptionController {
    public static final String ERROR_EXCEPTION = "javax.servlet.error.exception";
    public static final String ERROR_EXCEPTION_TYPE = "javax.servlet.error.exception_type";
    public static final String ERROR_MESSAGE = "javax.servlet.error.message";
    public static final String ERROR_REQUEST_URI = "javax.servlet.error.request_uri";
    public static final String ERROR_SERVLET_NAME = "javax.servlet.error.servlet_name";
    public static final String ERROR_STATUS_CODE = "javax.servlet.error.status_code";

    @GetMapping("/error-page/404")
    public String error400(HttpServletRequest request) {
        log.info("400 error");
        printErrorInfo(request);
        return "error-page/404";
    }

    @RequestMapping(value = "/error-page/404", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> errorPage404Api(HttpServletRequest request, HttpServletResponse response) {

        log.info("API errorPage 404");

        Map<String, Object> result = new HashMap<>();
        Exception ex = (Exception) request.getAttribute(ERROR_EXCEPTION);
        result.put("status", request.getAttribute(ERROR_STATUS_CODE));
        result.put("message", ex.getMessage());

        //Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    private void printErrorInfo(HttpServletRequest request) {
        log.info("ERROR_EXCEPTION : {}",request.getAttribute(ERROR_EXCEPTION));
        log.info("ERROR_EXCEPTION_TYPE : {}", request.getAttribute(ERROR_EXCEPTION_TYPE));
        log.info("ERROR_MESSAGE : {}", request.getAttribute(ERROR_MESSAGE));
        log.info("ERROR_REQUEST_URI : {}", request.getAttribute(ERROR_REQUEST_URI));
        log.info("ERROR_SERVLET_NAME : {}", request.getAttribute(ERROR_SERVLET_NAME));
        log.info("ERROR_STATUS_CODE : {}", request.getAttribute(ERROR_STATUS_CODE));

        log.info("dispatchType ={}", request.getDispatcherType());
    }
}
