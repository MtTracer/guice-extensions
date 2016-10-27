package com.thirdpower.guice_extensions.provisionscope;

import static com.google.common.truth.Truth.assertThat;

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
	public void testDependencyScope_of() {

		final AbstractModule testModule = new AbstractModule() {
			@Override
			protected void configure() {
				bind(A.class);
				bind(B.class);
				bind(C.class).in(ProvisionScope.of(A.class));
			}
		};
		final Injector injector = Guice.createInjector(new ProvisionScopeModule(), testModule);
		final IA a1 = injector.getInstance(A.class);
		final IA a2 = injector.getInstance(A.class);

		assertThat(a1).isNotSameAs(a2);

		final B a1_b1 = a1.getB1();
		final B a1_b2 = a1.getB2();
		final B a1_b3 = a1.getB3();

		assertThat(a1_b1).isNotSameAs(a1_b2);
		assertThat(a1_b1).isNotSameAs(a1_b3);
		assertThat(a1_b2).isNotSameAs(a1_b3);

		final B a2_b1 = a2.getB1();
		final B a2_b2 = a2.getB2();
		final B a2_b3 = a2.getB3();

		assertThat(a2_b1).isNotSameAs(a2_b2);
		assertThat(a2_b1).isNotSameAs(a2_b3);
		assertThat(a2_b2).isNotSameAs(a2_b3);

		assertThat(a1_b1).isNotSameAs(a2_b1);
		assertThat(a1_b1).isNotSameAs(a2_b2);
		assertThat(a1_b1).isNotSameAs(a2_b3);
		assertThat(a1_b2).isNotSameAs(a2_b1);
		assertThat(a1_b2).isNotSameAs(a2_b2);
		assertThat(a1_b2).isNotSameAs(a2_b3);
		assertThat(a1_b3).isNotSameAs(a2_b1);
		assertThat(a1_b3).isNotSameAs(a2_b2);
		assertThat(a1_b3).isNotSameAs(a2_b3);

		assertThat(a1_b1.getC()).isSameAs(a1_b2.getC());
		assertThat(a1_b2.getC()).isSameAs(a1_b3.getC());

		assertThat(a2_b1.getC()).isSameAs(a2_b2.getC());
		assertThat(a2_b2.getC()).isSameAs(a2_b3.getC());

		assertThat(a1_b1.getC()).isNotSameAs(a2_b1.getC());

	}

	@Test
	public void testDependencyScope_of_OutOfScope() {

		ex.expect(ProvisionException.class);
		ex.reportMissingExceptionWithMessage(
				"Injection of B must fail, due to provisioning of contained C is dependent of A.");

		final AbstractModule testModule = new AbstractModule() {
			@Override
			protected void configure() {
				bind(A.class);
				bind(B.class);
				bind(C.class).in(ProvisionScope.of(A.class));
			}
		};
		final Injector injector = Guice.createInjector(new ProvisionScopeModule(), testModule);
		injector.getInstance(B.class);

	}

	@Test
	public void testDependencyScope_ofOptional() {

		final AbstractModule testModule = new AbstractModule() {
			@Override
			protected void configure() {
				bind(A.class);
				bind(B.class);
				bind(C.class).in(ProvisionScope.ofOptional(A.class));
			}
		};
		final Injector injector = Guice.createInjector(new ProvisionScopeModule(), testModule);
		final B b1 = injector.getInstance(B.class);
		final B b2 = injector.getInstance(B.class);
		final C c1 = injector.getInstance(C.class);
		final C c2 = injector.getInstance(C.class);

		assertThat(b1).isNotSameAs(b2);
		assertThat(b1.getC()).isNotSameAs(b2.getC());
		assertThat(b1.getC()).isNotSameAs(c1);
		assertThat(b1.getC()).isNotSameAs(c2);
		assertThat(b2.getC()).isNotSameAs(c1);
		assertThat(b2.getC()).isNotSameAs(c2);
		assertThat(c1).isNotSameAs(c2);

		final IA a1 = injector.getInstance(A.class);
		final IA a2 = injector.getInstance(A.class);

		assertThat(a1).isNotSameAs(a2);

		final B a1_b1 = a1.getB1();
		final B a1_b2 = a1.getB2();
		final B a1_b3 = a1.getB3();

		assertThat(a1_b1).isNotSameAs(a1_b2);
		assertThat(a1_b1).isNotSameAs(a1_b3);
		assertThat(a1_b2).isNotSameAs(a1_b3);

		final B a2_b1 = a2.getB1();
		final B a2_b2 = a2.getB2();
		final B a2_b3 = a2.getB3();

		assertThat(a2_b1).isNotSameAs(a2_b2);
		assertThat(a2_b1).isNotSameAs(a2_b3);
		assertThat(a2_b2).isNotSameAs(a2_b3);

		assertThat(a1_b1).isNotSameAs(a2_b1);
		assertThat(a1_b1).isNotSameAs(a2_b2);
		assertThat(a1_b1).isNotSameAs(a2_b3);
		assertThat(a1_b2).isNotSameAs(a2_b1);
		assertThat(a1_b2).isNotSameAs(a2_b2);
		assertThat(a1_b2).isNotSameAs(a2_b3);
		assertThat(a1_b3).isNotSameAs(a2_b1);
		assertThat(a1_b3).isNotSameAs(a2_b2);
		assertThat(a1_b3).isNotSameAs(a2_b3);

		assertThat(a1_b1.getC()).isSameAs(a1_b2.getC());
		assertThat(a1_b2.getC()).isSameAs(a1_b3.getC());

		assertThat(a2_b1.getC()).isSameAs(a2_b2.getC());
		assertThat(a2_b2.getC()).isSameAs(a2_b3.getC());

		assertThat(a1_b1.getC()).isNotSameAs(a2_b1.getC());

	}

	@Test
	public void testCircular() {
		final AbstractModule testModule = new AbstractModule() {
			@Override
			protected void configure() {
				bind(IA.class).to(A.class);
				bind(B.class);
				bind(C.class).to(CircularC.class)
						.in(ProvisionScope.of(A.class));
			}
		};
		final Injector injector = Guice.createInjector(new ProvisionScopeModule(), testModule);
		final IA a1 = injector.getInstance(A.class);
		final IA a2 = injector.getInstance(A.class);

		assertThat(a1).isNotSameAs(a2);

		final B a1_b1 = a1.getB1();
		final B a1_b2 = a1.getB2();
		final B a1_b3 = a1.getB3();

		assertThat(a1_b1).isNotSameAs(a1_b2);
		assertThat(a1_b1).isNotSameAs(a1_b3);
		assertThat(a1_b2).isNotSameAs(a1_b3);

		final B a2_b1 = a2.getB1();
		final B a2_b2 = a2.getB2();
		final B a2_b3 = a2.getB3();

		assertThat(a2_b1).isNotSameAs(a2_b2);
		assertThat(a2_b1).isNotSameAs(a2_b3);
		assertThat(a2_b2).isNotSameAs(a2_b3);

		assertThat(a1_b1).isNotSameAs(a2_b1);
		assertThat(a1_b1).isNotSameAs(a2_b2);
		assertThat(a1_b1).isNotSameAs(a2_b3);
		assertThat(a1_b2).isNotSameAs(a2_b1);
		assertThat(a1_b2).isNotSameAs(a2_b2);
		assertThat(a1_b2).isNotSameAs(a2_b3);
		assertThat(a1_b3).isNotSameAs(a2_b1);
		assertThat(a1_b3).isNotSameAs(a2_b2);
		assertThat(a1_b3).isNotSameAs(a2_b3);

		assertThat(a1_b1.getC()).isSameAs(a1_b2.getC());
		assertThat(a1_b2.getC()).isSameAs(a1_b3.getC());

		assertThat(a2_b1.getC()).isSameAs(a2_b2.getC());
		assertThat(a2_b2.getC()).isSameAs(a2_b3.getC());

		assertThat(a1_b1.getC()).isNotSameAs(a2_b1.getC());
	}
}
