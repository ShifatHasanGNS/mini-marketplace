package com.marketplace.app.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {

        // Retrieve the HTTP status code set by the servlet container
        Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        // Retrieve any exception that was forwarded to /error
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

        // Retrieve the original request URI
        Object requestUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        int code = 500;
        if (statusCode != null) {
            code = Integer.parseInt(statusCode.toString());
        }

        // Resolve friendly title + message from the status code
        String title   = resolveTitle(code);
        String message = resolveMessage(code, exception, requestUri);
        String type    = resolveType(code);

        model.addAttribute("errorCode",    code);
        model.addAttribute("errorTitle",   title);
        model.addAttribute("errorMessage", message);
        model.addAttribute("errorType",    type);

        return "error";
    }

    //  Helper methods

    private String resolveTitle(int code) {
        if (code == 404) return "Page Not Found";
        if (code == 403) return "Access Denied";
        if (code == 401) return "Login Required";
        if (code == 400) return "Bad Request";
        if (code == 405) return "Method Not Allowed";
        if (code >= 500)  return "Server Error";
        return "Something Went Wrong";
    }

    private String resolveMessage(int code, Object exception, Object requestUri) {
        String uri = requestUri != null ? requestUri.toString() : "this page";

        if (code == 404) {
            return "We couldn't find the page you were looking for: \""
                    + uri + "\". It may have been moved or deleted.";
        }
        if (code == 403) {
            return "You don't have permission to access \"" + uri
                    + "\". Please log in with a suitable account.";
        }
        if (code == 401) {
            return "You need to be logged in to access \"" + uri + "\". Please log in first.";
        }
        if (code == 400) {
            return "The request to \"" + uri + "\" was invalid. Please check your input and try again.";
        }
        if (code == 405) {
            return "The HTTP method used is not supported for \"" + uri + "\".";
        }
        if (code >= 500) {
            // Show the exception message only if it is safe and available
            if (exception instanceof Throwable t && t.getMessage() != null) {
                return "An internal error occurred: " + t.getMessage();
            }
            return "An unexpected server error occurred. Please try again in a moment. "
                    + "If the issue persists, contact support.";
        }
        return "An error occurred while processing your request. Please try again.";
    }

    private String resolveType(int code) {
        HttpStatus status = null;
        try { status = HttpStatus.valueOf(code); } catch (Exception ignored) {}
        return status != null
                ? code + " " + status.getReasonPhrase()
                : String.valueOf(code);
    }
}
