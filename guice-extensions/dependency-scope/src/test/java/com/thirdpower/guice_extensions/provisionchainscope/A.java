package com.thirdpower.guice_extensions.provisionchainscope;

import java.util.Random;

import javax.inject.Inject;

import com.google.common.base.Objects;

public class A {

	private final B b1;
	private final B b2;
	private final B b3;
	private final long id;

	@Inject
	A(final B b1, final B b2, final B b3) {
		System.out.println("in A()");
		this.id = new Random().nextLong();
		this.b1 = b1;
		this.b2 = b2;
		this.b3 = b3;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("id", id).add("b1", b1).add("b2", b2).add("b3", b3).toString();
	}
}
