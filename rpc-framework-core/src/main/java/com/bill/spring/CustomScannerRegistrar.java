package com.bill.spring;




import com.bill.annotation.RpcScan;
import com.bill.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.stereotype.Component;

/**
 * 扫描并且过滤具体注解
 */
@Slf4j
public class CustomScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {
    private static final String SPRING_BEAN_BASE_PACKAGE = "com.bill";
    private static final String BASE_PACKAGE_ATTRIBUTE_NAME = "basePackage";
    private ResourceLoader resourceLoader;//to read file in spring

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    //todo
    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        try {
            //解析注解 获取信息
            AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(RpcScan.class.getName()));
            //获取扫描的包
            String[] basePackage = new String[0];
            if (annotationAttributes != null && !annotationAttributes.isEmpty()) {
                //根据名称获取对应的值
                basePackage = annotationAttributes.getStringArray(BASE_PACKAGE_ATTRIBUTE_NAME);
            } else {
                //获取目标类的包
                basePackage = new String[]{((StandardAnnotationMetadata) annotationMetadata).getIntrospectedClass().getPackage().getName()};
            }
            // Scan the RpcService annotation
            CustomScanner rpcServiceScanner = new CustomScanner(registry, RpcService.class);
            // Scan the Component annotation
            CustomScanner springBeanScanner = new CustomScanner(registry, Component.class);
            if (resourceLoader != null) {
                rpcServiceScanner.setResourceLoader(resourceLoader);
                springBeanScanner.setResourceLoader(resourceLoader);
            }
            int springBeanAmount = springBeanScanner.scan(SPRING_BEAN_BASE_PACKAGE);
            log.info("springBeanScanner扫描的数量 [{}]", springBeanAmount);
            int rpcServiceCount = rpcServiceScanner.scan(basePackage);
            log.info("rpcServiceScanner扫描的数量 [{}]", rpcServiceCount);
        } catch (Exception e) {
            log.error("error");
            e.printStackTrace();
        }
    }
}
