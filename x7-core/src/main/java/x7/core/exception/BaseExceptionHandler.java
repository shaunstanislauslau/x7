package x7.core.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import x7.core.util.JsonX;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class BaseExceptionHandler {

    @ExceptionHandler( ConstraintViolationException.class )
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RemoteServiceException constraintViolationException(ConstraintViolationException ex) {
        Map<String,Object> map = new HashMap<>();
        map.put("code",500);
        map.put("detail",ex.getMessage());
        return new RemoteServiceException(JsonX.toJson(map));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RemoteServiceException IllegalArgumentException(IllegalArgumentException ex) {
        Map<String,Object> map = new HashMap<>();
        map.put("code",501);
        map.put("detail",ex.getMessage());
        return new RemoteServiceException(JsonX.toJson(map));
    }

    @ExceptionHandler( NoHandlerFoundException.class )
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public RemoteServiceException noHandlerFoundException(Exception ex) {
        Map<String,Object> map = new HashMap<>();
        map.put("code",404);
        map.put("detail",ex.getMessage());
        return new RemoteServiceException(JsonX.toJson(map));
    }


    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RemoteServiceException unknownException(Exception ex) {
        Map<String,Object> map = new HashMap<>();
        map.put("code",500);
        map.put("detail",ex.getMessage());
        return new RemoteServiceException(JsonX.toJson(map));
    }
}