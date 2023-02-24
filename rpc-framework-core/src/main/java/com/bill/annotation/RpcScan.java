package com.bill.annotation;

import com.bill.spring.CustomScannerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Import(CustomScannerRegistrar.class)
public @interface RpcScan {

    String[] basePackage();
}
