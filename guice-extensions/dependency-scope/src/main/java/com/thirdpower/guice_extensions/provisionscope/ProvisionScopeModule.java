package com.thirdpower.guice_extensions.provisionscope;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

public class ProvisionScopeModule extends AbstractModule {

	@Override
	protected void configure() {
		final ScopingProvisionListener provisionListener = ScopingProvisionListener.get();
		bindListener(Matchers.any(), provisionListener);
	}

}
