package com.thirdpower.guice_extensions.provisionchainscope;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

@Retention(RUNTIME)
@Target({ TYPE, METHOD })
@BindingAnnotation
public @interface Dependency {
	Class<?> value();

}
