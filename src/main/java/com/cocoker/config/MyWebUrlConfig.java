package com.cocoker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2019/4/19 11:06 AM
 * @Version: 1.0
 */
@Configuration
public class MyWebUrlConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/btjq").setViewName("btjq");
        registry.addViewController("/zsjc").setViewName("zsjc");
        registry.addViewController("/tgjc").setViewName("tgjc");
        registry.addViewController("/tousu").setViewName("wxtousu/index");
        registry.addViewController("/tsInfo").setViewName("wxtousu/jump4");
        registry.addViewController("/lljc").setViewName("wxtousu/110");
        registry.addViewController("/commissionredpack").setViewName("commissionredpack");
//        registry.addViewController("/ranking").setViewName("ranking");
//        registry.addViewController("/zddetail").setViewName("zddetail");
//        registry.addViewController("/onecom").soneCommissionwName("allcommission");

        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }
}
