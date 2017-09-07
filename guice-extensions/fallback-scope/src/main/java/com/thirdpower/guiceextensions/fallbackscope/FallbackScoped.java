package com.thirdpower.guiceextensions.fallbackscope;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.ScopeAnnotation;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@ScopeAnnotation
public @interface FallbackScoped {
  public Class<? extends Annotation> value();
}
