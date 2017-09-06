package com.thirdpower.guiceextensions.provisionscope;

import javax.inject.Inject;

public class A implements IA {

	private final B b1;
	private final B b2;
	private final B b3;

	@Inject
	A(final B b1, final B b2, final B b3) {
		this.b1 = b1;
		this.b2 = b2;
		this.b3 = b3;
	}

	@Override
	public B getB1() {
		return b1;
	}

	@Override
	public B getB2() {
		return b2;
	}

	@Override
	public B getB3() {
		return b3;
	}

}
