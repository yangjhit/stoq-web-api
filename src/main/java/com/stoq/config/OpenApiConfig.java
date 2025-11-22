package com.stoq.config;

import io.swagger.v3.oas.models.PathItem.HttpMethod;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.ParameterIn;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.customizers.OpenApiCustomiser;

@Configuration
public class OpenApiConfig {

    /**
     * 为所有接口添加 Accept-Language 头部说明，默认 en
     */
    @Bean
    public OpenApiCustomiser acceptLanguageHeaderCustomiser() {
        return openApi -> {
            // 定义全局可复用的 Accept-Language 参数
            Parameter langHeader = new Parameter()
                    .in(ParameterIn.HEADER.toString())
                    .name("Accept-Language")
                    .description("Response language (default: en). Examples: en, zh-CN")
                    .required(false);
            openApi.getComponents().addParameters("AcceptLanguageHeader", langHeader);

            openApi.getPaths().values().forEach(pathItem ->
                    pathItem.readOperationsMap().forEach((method, operation) -> {
                        boolean exists = operation.getParameters() != null &&
                                operation.getParameters().stream()
                                        .anyMatch(p -> "Accept-Language".equalsIgnoreCase(p.getName()) && "header".equalsIgnoreCase(p.getIn()));
                        if (!exists) {
                            // 引用全局参数，避免重复定义
                            operation.addParametersItem(new Parameter().$ref("#/components/parameters/AcceptLanguageHeader"));
                        }
                    })
            );
        };
    }
}
