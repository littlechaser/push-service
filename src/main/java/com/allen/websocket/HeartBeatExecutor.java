package com.allen.websocket;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * @author yang_tao@<yangtao.letzgo.com.cn>
 * @version 1.0
 * @date 2018-04-11 11:07
 */
@Component
public class HeartBeatExecutor implements ApplicationListener<ContextRefreshedEvent>, ThreadFactory {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (contextRefreshedEvent.getApplicationContext().getParent() == null) {
            ScheduledThreadPoolExecutor heartBeat = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), new HeartBeatExecutor());
            heartBeat.scheduleAtFixedRate(() -> {
                try {
                    TextWebSocketHandler.heartBeat();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, 10, 30, TimeUnit.MINUTES);
        }
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName("websocket客户端连接心跳检测线程");
        return thread;
    }
}
