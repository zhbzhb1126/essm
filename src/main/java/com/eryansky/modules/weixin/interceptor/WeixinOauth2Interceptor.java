/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.weixin.interceptor;

import com.eryansky.fastweixin.company.api.QYUserAPI;
import com.eryansky.fastweixin.company.api.config.QYAPIConfig;
import com.eryansky.fastweixin.company.api.response.GetOauthUserInfoResponse;
import com.eryansky.common.spring.SpringContextHolder;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.web.springmvc.SpringMVCHolder;
import com.google.common.collect.Lists;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.modules.sys.entity.User;
import com.eryansky.modules.sys.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * 微信Outho2认证拦截器
 * 优先级：注解>数据库权限配置
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-01-21 12:23
 */
public class WeixinOauth2Interceptor extends HandlerInterceptorAdapter {


    protected Logger logger = LoggerFactory.getLogger(getClass());

    private static QYAPIConfig qyapiConfig = SpringContextHolder.getBean(QYAPIConfig.class);

    /**
     * 登录验证地址
     */
    private String redirectURL = "/";
    /**
     * 需要拦截的资源
     */
    private List<String> includeUrls = Lists.newArrayList();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        String code = request.getParameter("code");
        String state = request.getParameter("state");
        if (StringUtils.isNotBlank(code) && StringUtils.isNotBlank(state)) {
            String requestUrl = request.getRequestURI();
            requestUrl = requestUrl.replaceAll("//", "/");

            boolean flag = false;
            if (Collections3.isNotEmpty(includeUrls)) {
                for (String excludeUrl : includeUrls) {
                    flag = StringUtils.simpleWildcardMatch(request.getContextPath() + excludeUrl, requestUrl);
                    if (flag) {
                        break;
                    }
                }
            }

            if (flag) {
                QYUserAPI qyUserAPI = new QYUserAPI(qyapiConfig);
                try {
                    GetOauthUserInfoResponse getOauthUserInfoResponse = qyUserAPI.getOauthUserInfo(code);
                    SessionInfo sessionInfo = SecurityUtils.getSessionInfo(request.getSession().getId());
                    if (sessionInfo == null) {
                        User user = UserUtils.getUserByLoginName(getOauthUserInfoResponse.getUserid());
                        if (user != null) {
                            SecurityUtils.putUserToSession(SpringMVCHolder.getRequest(), user);//模拟自动登录
                            logger.info("{} 自动登录", getOauthUserInfoResponse.getUserid());
                        } else {
                            logger.warn("{} not exist", getOauthUserInfoResponse.getUserid());
                        }
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        if (e != null) {

        }
    }

    public String getRedirectURL() {
        return redirectURL;
    }

    public void setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
    }

    public List<String> getIncludeUrls() {
        return includeUrls;
    }

    public void setIncludeUrls(List<String> includeUrls) {
        this.includeUrls = includeUrls;
    }
}
