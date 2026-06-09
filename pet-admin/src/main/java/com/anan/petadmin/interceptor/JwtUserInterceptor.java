package com.anan.petadmin.interceptor;

import com.anan.petadmin.context.BaseContext;
import com.anan.petadmin.properties.JwtProperties;
import com.anan.petadmin.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class JwtUserInterceptor implements HandlerInterceptor {
    @Resource
    private JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            // 当前拦截到的不是动态方法，直接放行
            return true;
        }

        // 1、从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getUserTokenName());

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 2、校验令牌
        try {
            log.info("jwt校验:{}", token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
            Long userId = Long.valueOf(claims.get("user_id").toString());
            log.info("当前员工id：{}", userId);
            BaseContext.setCurrentId(userId);
            // 3、通过，放行
            return true;
        } catch (Exception ex) {
            log.error("jwt校验失败:{}", ex.getMessage());
            // 4、不通过，响应 401 状态码
            response.setStatus(401);
            return false;
        }
    }
}
