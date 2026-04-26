package com.pehrs.cb.config;

import freemarker.template.TemplateExceptionHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import freemarker.template.Configuration;

import java.util.TimeZone;

@org.springframework.context.annotation.Configuration
@AllArgsConstructor
public class FreemarkerConfiguration {

    @Bean("freemarkerConfig")
    public freemarker.template.Configuration freemarkerConfig() {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_34);

        // cfg.setDirectoryForTemplateLoading(new File("/where/you/store/templates"));
        cfg.setClassForTemplateLoading(this.getClass(), "/freemarker");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);
        cfg.setSQLDateAndTimeTimeZone(TimeZone.getDefault());
        return cfg;
    }

}
