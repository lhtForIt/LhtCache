package com.lht.lhtcache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * plugin entrypoint
 *
 * @author Leo
 * @date 2024/06/21
 */
@Component
public class LhtApplicationListener implements ApplicationListener<ApplicationEvent> {

    @Autowired
    List<LhtPlugin> plugins;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        //这两个和bean的init和destory方法类似，批量这么写不用给每个bean定义生命周期方法，方便
        if (event instanceof ApplicationReadyEvent ready) {
            plugins.stream().forEach(d->{
                d.init();
                d.startup();
            });
        } else if (event instanceof ContextClosedEvent close) {
            plugins.stream().forEach(LhtPlugin::shutdown);
        }
    }
}
