package com.logistics.handler;

import com.alibaba.fastjson.JSON;
import com.logistics.entity.Result;
import com.logistics.utils.WebUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class MyAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        Result error = Result.error("权限不足");
        error.setCode(403);
        String json = JSON.toJSON(error).toString();
        WebUtils.renderString(response,json);
    }
}
