package com.thirdpower.guice_extensions.provisionchainscope;

import java.util.Random;

import javax.inject.Inject;

import com.google.common.base.Objects;

public class B {

	private final C c;
	private final long id;

	@Inject
	B(final C c) {
		System.out.println("in B()");

		this.id = new Random().nextLong();
		this.c = c;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("id", id).add("c", c).toString();
	}
}
