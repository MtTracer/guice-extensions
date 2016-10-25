package com.thirdpower.guice_extensions.provisionchainscope;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;

/**
 * Unit test for simple App.
 */
public class ProvisionScopeTest {

	@Rule
	public ExpectedException ex = ExpectedException.none();

	@Test
	public void test() {

		final AbstractModule testModule = new AbstractModule() {
			@Override
			protected void configure() {
				bind(A.class);
				bind(B.class);
				bind(C.class).in(DependencyScope.of(A.class));
			}
		};
		final Injector injector = Guice.createInjector(new ProvisionScopeModule(), testModule);
		final A a = injector.getInstance(A.class);
		final A a2 = injector.getInstance(A.class);

		System.out.println(a);
		System.out.println(a2);
	}

	@Test
	public void test2() {

		ex.expect(ProvisionException.class);
		ex.reportMissingExceptionWithMessage(
				"Injection of B must fail, due to provisioning of contained C is dependent of A.");

		final AbstractModule testModule = new AbstractModule() {
			@Override
			protected void configure() {
				bind(A.class);
				bind(B.class);
				bind(C.class).in(DependencyScope.of(A.class));
			}
		};
		final Injector injector = Guice.createInjector(new ProvisionScopeModule(), testModule);
		final B b1 = injector.getInstance(B.class);
		final B b2 = injector.getInstance(B.class);

		System.out.println(b1);
		System.out.println(b2);

	}
}
