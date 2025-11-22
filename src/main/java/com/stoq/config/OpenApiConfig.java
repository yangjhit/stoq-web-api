package com.stoq.config;

import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
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

    /**
     * 定义全局 JWT 安全方案 (Authorization: Bearer <token>)
     */
    @Bean
    public OpenApiCustomiser securitySchemeCustomiser() {
        return openApi -> {
            Components components = openApi.getComponents();
            if (components.getSecuritySchemes() == null || !components.getSecuritySchemes().containsKey("bearerAuth")) {
                components.addSecuritySchemes("bearerAuth", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT Authorization header using the Bearer scheme. Example: \"Bearer eyJhbGciOi...\""));
            }

            // 将安全需求应用到所有已有的操作（已标注 security 的不重复添加）
            openApi.getPaths().values().forEach(pathItem ->
                    pathItem.readOperations().forEach(operation -> {
                        boolean hasSecurity = operation.getSecurity() != null && !operation.getSecurity().isEmpty();
                        if (!hasSecurity) {
                            operation.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
                        }
                    })
            );
        };
    }
}
