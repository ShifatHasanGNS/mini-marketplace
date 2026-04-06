package com.marketplace.app.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.dao.DataAccessException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;


@ControllerAdvice
public class GlobalExceptionHandler {
    //  403 – Access denied (not authorised to view the resource)
    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handleAccessDenied(AccessDeniedException ex) {
        return buildErrorView(
                403,
                "Access Denied",
                "You do not have permission to access this page. "
                        + "Please log in with an account that has the required role.",
                "403 Forbidden"
        );
    }

    //  Database / data-access errors
    @ExceptionHandler(DataAccessException.class)
    public ModelAndView handleDatabaseError(DataAccessException ex) {
        return buildErrorView(
                500,
                "Database Error",
                "We could not complete your request due to a database problem. "
                        + "Please try again in a moment. If the issue continues, contact support.",
                "Database Error"
        );
    }

    //  Bad / unreadable request body
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ModelAndView handleBadRequest(HttpMessageNotReadableException ex) {
        return buildErrorView(
                400,
                "Bad Request",
                "The request could not be understood by the server. "
                        + "Please check your input and try again.",
                "400 Bad Request"
        );
    }

    //  General runtime errors (product not found, invalid state, etc.)
    @ExceptionHandler(RuntimeException.class)
    public ModelAndView handleRuntime(RuntimeException ex) {
        String message = ex.getMessage() != null
                ? ex.getMessage()
                : "An unexpected error occurred. Please try again.";
        return buildErrorView(
                500,
                "Unexpected Error",
                message,
                ex.getClass().getSimpleName()
        );
    }

    //  Catch-all for anything else (checked exceptions, etc.)
    @ExceptionHandler(Exception.class)
    public ModelAndView handleGeneral(Exception ex) {
        String message = ex.getMessage() != null
                ? ex.getMessage()
                : "Something went wrong on our end. Please try again.";
        return buildErrorView(
                500,
                "Something Went Wrong",
                message,
                ex.getClass().getSimpleName()
        );
    }

    //  Shared helper – populates the model for error.html
    private ModelAndView buildErrorView(int code, String title, String message, String type) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("errorCode",    code);
        mav.addObject("errorTitle",   title);
        mav.addObject("errorMessage", message);
        mav.addObject("errorType",    type);
        return mav;
    }
}
