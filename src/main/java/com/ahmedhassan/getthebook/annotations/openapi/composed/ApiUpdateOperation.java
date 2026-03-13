package com.ahmedhassan.getthebook.annotations.openapi.composed;

import com.ahmedhassan.getthebook.annotations.openapi.atomic.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiNotFoundResponse
@ApiUnauthorizedResponse
@ApiBadRequestResponse
@ApiInternalServerErrorResponse
public @interface ApiUpdateOperation {}