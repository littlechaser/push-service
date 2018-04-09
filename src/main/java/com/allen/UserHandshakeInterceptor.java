package com.allen;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Map;

/**
 * @author yang_tao@<yangtao.letzgo.com.cn>
 * @version 1.0
 * @date 2018-04-08 16:19
 */
public class UserHandshakeInterceptor extends HttpSessionHandshakeInterceptor {
    public UserHandshakeInterceptor() {
        super();
    }

    public UserHandshakeInterceptor(Collection<String> attributeNames) {
        super(attributeNames);
    }

    @Override
    public Collection<String> getAttributeNames() {
        return super.getAttributeNames();
    }

    @Override
    public void setCopyAllAttributes(boolean copyAllAttributes) {
        super.setCopyAllAttributes(copyAllAttributes);
    }

    @Override
    public boolean isCopyAllAttributes() {
        return super.isCopyAllAttributes();
    }

    @Override
    public void setCopyHttpSessionId(boolean copyHttpSessionId) {
        super.setCopyHttpSessionId(copyHttpSessionId);
    }

    @Override
    public boolean isCopyHttpSessionId() {
        return super.isCopyHttpSessionId();
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        ServletServerHttpRequest serverHttpRequest = (ServletServerHttpRequest) request;
        HttpServletRequest httpRequest = serverHttpRequest.getServletRequest();
        final String username = httpRequest.getParameter(Constants.USER_NAME);
        if (StringUtils.isBlank(username)) {
            return false;
        }
        attributes.put(Constants.USER_NAME, username);
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
        super.afterHandshake(request, response, wsHandler, ex);
    }
}
