package com.thirdpower.guiceextensions.provisionscope;

import javax.inject.Inject;

public class CircularC extends C {
	private final IA a;

	@Inject
	CircularC(final IA a) {
		super();
		this.a = a;
	}
}
