package com.thirdpower.guice_extensions.provisionscope;

import javax.inject.Inject;

public class B {

	private final C c;

	@Inject
	B(final C c) {
		this.c = c;
	}

	public C getC() {
		return c;
	}
}
