package com.allen.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class TextWebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(textWebSocketHandler(), "/websocket").addInterceptors(new UserHandshakeInterceptor()).setAllowedOrigins("*");
        webSocketHandlerRegistry.addHandler(textWebSocketHandler(), "/sockjs/websocket").addInterceptors(new UserHandshakeInterceptor()).setAllowedOrigins("*").withSockJS();
    }

    @Bean
    public TextWebSocketHandler textWebSocketHandler() {
        return new TextWebSocketHandler();
    }
}
