package com.thirdpower.guiceextensions.fallbackscope;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.annotation.Annotation;

import com.google.auto.value.AutoAnnotation;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Scope;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.internal.Scoping;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

public class FallbackScopeBinder<T> implements Module {


  private Binder binder;
  private Key<T> key;

  public static <T> FallbackScopeBinder<T> newFallbackBinder(Binder binder, Key<T> key) {
    binder = binder.skipSources(FallbackScopeBinder.class);
    FallbackScopeBinder<T> result = new FallbackScopeBinder<T>(binder, key);
    binder.install(result);
    return result;
  }

  public FallbackScopeBinder(Binder binder, Key<T> key) {
    this.binder = checkNotNull(binder);
    this.key = checkNotNull(key);
  }
  
  public LinkedBindingBuilder<T> addBinding(Class<? extends Annotation> scopeAnnotation) {
    Key<T> key = getBindingKey(scopeAnnotation);
    return binder.bind(key);
  }
  
  
  private Key<T> getBindingKey(Class<? extends Annotation> scoping) {
    FallbackScoped scopeBindingAnnotation = fallback(scoping);
    return Key.get(key.getTypeLiteral(), scopeBindingAnnotation);
  }

  @Override
  public void configure(Binder binder) {
    binder.install(this);
  }

  @AutoAnnotation
  private static FallbackScoped fallback(Class<? extends Annotation> value) {
    return new AutoAnnotation_FallbackScopeBinder_fallback(value);
  }

}
