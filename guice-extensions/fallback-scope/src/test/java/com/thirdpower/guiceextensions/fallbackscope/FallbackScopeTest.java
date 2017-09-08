package com.thirdpower.guiceextensions.fallbackscope;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Singleton;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.thirdpower.guiceextensions.manualscope.ManualScope;
import com.thirdpower.guiceextensions.manualscope.ManualScopeModule;
import com.thirdpower.guiceextensions.manualscope.ManualScoped;

public class FallbackScopeTest {

  @Test
  public void testFallbackScoping() {
    Module testModule = new AbstractModule() {
      
      @Override
      protected void configure() {
        install(new ManualScopeModule());
        FallbackScopeBinder<Injectable> fallbackBinder = FallbackScopeBinder.newFallbackBinder(binder(), Key.get(Injectable.class));
        fallbackBinder.addBinding(ManualScoped.class, () -> new Injectable("manual"));
        fallbackBinder.addBinding(Singleton.class, () -> new Injectable("singleton"));
      }
    };
    
    Injector injector = Guice.createInjector(testModule);
    ManualScope manualScope = injector.getInstance(ManualScope.class);
    manualScope.enter();
    Injectable instance1 = injector.getInstance(Injectable.class);
    assertThat(instance1.getName()).isEqualTo("manual");
    Injectable instance2 = injector.getInstance(Injectable.class);
    assertThat(instance2.getName()).isEqualTo("manual");
    manualScope.exit();
    
    Injectable instance3 = injector.getInstance(Injectable.class);
    assertThat(instance3.getName()).isEqualTo("singleton");
    Injectable instance4 = injector.getInstance(Injectable.class);
    assertThat(instance4.getName()).isEqualTo("singleton");
    
    manualScope.enter();
    Key<Injectable> fallbackKey = FallbackScopeBinder.getFallbackKey(Key.get(Injectable.class), ManualScoped.class);
    manualScope.seed(fallbackKey, new Injectable("seeded"));
    Injectable instance5 = injector.getInstance(Injectable.class);
    assertThat(instance5.getName()).isEqualTo("seeded");
    Injectable instance6 = injector.getInstance(Injectable.class);
    assertThat(instance6.getName()).isEqualTo("seeded");
    
    manualScope.exit();
  }
  
  private static final class Injectable {
    private String name;

    Injectable(String name) {
      this.name = name;
    }
    
    public String getName() {
      return name;
    }
  }
}
