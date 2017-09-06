package com.thirdpower.guiceextensions.manualscope;

import com.google.inject.AbstractModule;

public class ManualScopeModule extends AbstractModule {

	@Override
	public void configure() {
		final ManualScope manualScope = new ManualScope();
		bindScope(ManualScoped.class, manualScope);

		bind(ManualScope.class).toInstance(manualScope);
	}

}
