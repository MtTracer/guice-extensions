package com.thirdpower.guiceextensions.fallbackscope;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Delayed;

import javax.inject.Provider;

import com.google.auto.value.AutoAnnotation;
import com.google.inject.Binder;
import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.OutOfScopeException;
import com.google.inject.ProvisionException;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.spi.Message;

public class FallbackScopeBinder<T> implements Module {


  private Binder binder;
  private Key<T> key;

  private List<Class<? extends Annotation>> fallbackScopes = new ArrayList<>();

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

  public FallbackScopeBinder<T> addBinding(Class<? extends Annotation> scopeAnnotation) {
    fallbackScopes.add(scopeAnnotation);
    Key<T> key = getBindingKey(scopeAnnotation);
    binder.bind(key).in(scopeAnnotation);
    return this;
  }
  
  public FallbackScopeBinder<T> addBinding(Class<? extends Annotation> scopeAnnotation, Provider<T> provider) {
    fallbackScopes.add(scopeAnnotation);
    Key<T> key = getBindingKey(scopeAnnotation);
    binder.bind(key).toProvider(provider).in(scopeAnnotation);
    return this;
  }

  @Override
  public void configure(Binder binder) {
    com.google.inject.Provider<Injector> injectorProvider = binder.getProvider(Injector.class);
    binder.bind(key)
      .toProvider(new FallbackProvider(injectorProvider));
  }

  public static final <T> Key<T> getFallbackKey(Key<T> originalKey, Class<? extends Annotation> scopeAnnotation) {
    FallbackScoped scopeBindingAnnotation = fallback(scopeAnnotation);
    return Key.get(originalKey.getTypeLiteral(), scopeBindingAnnotation);
  }
  
  private Key<T> getBindingKey(Class<? extends Annotation> scopeAnnotation) {
    return getFallbackKey(key, scopeAnnotation);
  }
  
  @AutoAnnotation
  private static FallbackScoped fallback(Class<? extends Annotation> value) {
    return new AutoAnnotation_FallbackScopeBinder_fallback(value);
  }

  private final class FallbackProvider implements Provider<T> {

    private com.google.inject.Provider<Injector> injectorProvider;

    public FallbackProvider(com.google.inject.Provider<Injector> injectorProvider) {
      this.injectorProvider = injectorProvider;
    }

    @Override
    public T get() {
      Injector injector = injectorProvider.get();
      for (Class<? extends Annotation> fallbackScope : fallbackScopes) {
        Key<T> fallbackKey = getBindingKey(fallbackScope);
        try {
          return injector.getInstance(fallbackKey);
        } catch (ProvisionException e) {
          if(!(e.getCause() instanceof OutOfScopeException)) {
            throw e;
          }
          // try next
        }
      }

      Message msg = new Message("No instance found for key " + key);
      throw new ConfigurationException(Collections.singletonList(msg));
    }

  }
}
