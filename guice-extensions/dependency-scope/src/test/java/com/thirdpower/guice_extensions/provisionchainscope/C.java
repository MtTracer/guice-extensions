package com.thirdpower.guice_extensions.provisionchainscope;

import java.util.Random;

import javax.inject.Inject;

import com.google.common.base.Objects;

public class C {

	private final long id;

	@Inject
	C() {
		System.out.println("in C()");

		this.id = new Random().nextLong();
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("id", id).toString();
	}
}
