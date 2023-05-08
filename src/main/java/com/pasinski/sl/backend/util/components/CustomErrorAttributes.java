package com.pasinski.sl.backend.util.components;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);

        String defaultMessage = "";
        String errorJson = errorAttributes.get("errors").toString();

        int index = errorJson.lastIndexOf("default message [");
        if (index != -1) {
            defaultMessage = errorJson.substring(index + "default message [".length(), errorJson.length() - 2);
        }

        errorAttributes.remove("timestamp");
        errorAttributes.remove("status");
        errorAttributes.remove("error");
        errorAttributes.remove("path");
        errorAttributes.remove("message");
        errorAttributes.remove("errors");

        errorAttributes.put("message", defaultMessage);

        return errorAttributes;
    }

}
