package com.bill.spring;

import com.bill.annotation.RpcReference;
import com.bill.annotation.RpcService;
import com.bill.config.ServiceConfig;
import com.bill.factory.SingletonFactory;
import com.bill.provider.ServiceProvider;
import com.bill.provider.impl.ServiceProviderImpl;
import com.bill.proxy.ClientProxy;
import com.bill.remoting.transport.RequestTransport;
import com.bill.remoting.transport.netty.client.NettyClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;



/**
 * 在 bean 对象实例化 和 依赖注入完成之后 添加自定义逻辑
 */
@Slf4j
@Component
public class SpringBeanPostProcessor implements BeanPostProcessor {
    private final ServiceProvider serviceProvider;
    private final RequestTransport client;

    public SpringBeanPostProcessor() {
        serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
        client = new NettyClient();
    }

    /**
     * 在bean对象实例化、依赖注入完成之后，显示调用初始化方法之前
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            log.info("[{}] is annotated with [{}]", bean.getClass().getSimpleName(), RpcService.class.getCanonicalName());
            RpcService service = bean.getClass().getAnnotation(RpcService.class);
            ServiceConfig serviceConfig = ServiceConfig.builder()
                    .version(service.version())
                    .group(service.group())
                    .service(bean).build();
            serviceProvider.publishService(serviceConfig);
        }
        return bean;
    }

    /**
     * 实例化和依赖注入完成之后，初始化完毕时执行
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //1. 获取bean中所有的声明的字段
        Field[] declaredFields = bean.getClass().getDeclaredFields();
        for (var field : declaredFields) {
            //获取字段上的指定注解  如果没有注释则返回null
            RpcReference annotation = field.getAnnotation(RpcReference.class);
            if(annotation==null) continue;
            //根据注解中的信息去创建config
            ServiceConfig config = ServiceConfig.builder().group(annotation.group())
                    .version(annotation.version()).build();
            //使用代理类创建客户端
            ClientProxy clientProxy = new ClientProxy(client, config);
            //根据字段类型返回实例
            Object proxy = clientProxy.getProxy(field.getType());
            field.setAccessible(true);//禁用检查 提高性能
            try {
                //为bean 设置新值 即通过代理类创建的实例
                field.set(bean,proxy);
            } catch (IllegalAccessException e) {
                throw new RuntimeException();
            }
        }
        return bean;
    }
}
