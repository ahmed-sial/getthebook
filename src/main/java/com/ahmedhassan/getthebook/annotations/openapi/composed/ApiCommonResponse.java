package com.ahmedhassan.getthebook.annotations.openapi.composed;

import com.ahmedhassan.getthebook.annotations.openapi.atomic.ApiBadRequestResponse;
import com.ahmedhassan.getthebook.annotations.openapi.atomic.ApiInternalServerErrorResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiBadRequestResponse
@ApiInternalServerErrorResponse
public @interface ApiCommonResponse {}