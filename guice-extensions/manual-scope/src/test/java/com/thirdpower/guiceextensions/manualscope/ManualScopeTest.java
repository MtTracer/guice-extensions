package com.thirdpower.guiceextensions.manualscope;

import static com.google.common.truth.Truth.assertThat;

import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class ManualScopeTest {

	private static final AtomicInteger COUNT = new AtomicInteger();

	@Before
	public void init() {
		COUNT.set(0);
	}

	@Test
	public void testEnter() throws Exception {
		final Injector injector = Guice.createInjector(new ManualScopeModule(), new TestModule());

		final ManualScope manualScope = injector.getInstance(ManualScope.class);

		manualScope.enter();
		try {
			final TestInjectable injectable1 = injector.getInstance(TestInjectable.class);
			assertThat(injectable1.getDependencyCount()).isEqualTo(0);
			final TestInjectable injectable2 = injector.getInstance(TestInjectable.class);
			assertThat(injectable2.getDependencyCount()).isEqualTo(0);
		} finally {
			manualScope.exit();
		}

		manualScope.enter();
		try {
			final TestInjectable injectable3 = injector.getInstance(TestInjectable.class);
			assertThat(injectable3.getDependencyCount()).isEqualTo(1);
			final TestInjectable injectable4 = injector.getInstance(TestInjectable.class);
			assertThat(injectable4.getDependencyCount()).isEqualTo(1);
		} finally {
			manualScope.exit();
		}
	}

	@Test
	public void testSeedClass() throws Exception {
		final Injector injector = Guice.createInjector(new ManualScopeModule(), new TestModule());

		final ManualScope manualScope = injector.getInstance(ManualScope.class);

		manualScope.enter();
		try {
			new TestDependency();
			new TestDependency();
			new TestDependency();
			final TestDependency testDependency = new TestDependency();
			manualScope.seed(TestDependency.class, testDependency);

			final TestInjectable injectable1 = injector.getInstance(TestInjectable.class);
			assertThat(injectable1.getDependencyCount()).isEqualTo(3);
			final TestInjectable injectable2 = injector.getInstance(TestInjectable.class);
			assertThat(injectable2.getDependencyCount()).isEqualTo(3);
		} finally {
			manualScope.exit();
		}

		new TestDependency();
		new TestDependency();

		manualScope.enter();
		try {
			final TestDependency testDependency = new TestDependency();
			manualScope.seed(TestDependency.class, testDependency);

			final TestInjectable injectable3 = injector.getInstance(TestInjectable.class);
			assertThat(injectable3.getDependencyCount()).isEqualTo(6);
			final TestInjectable injectable4 = injector.getInstance(TestInjectable.class);
			assertThat(injectable4.getDependencyCount()).isEqualTo(6);
		} finally {
			manualScope.exit();
		}
	}

	private static final class TestInjectable {

		private final TestDependency dependency;

		@Inject
		TestInjectable(final TestDependency dependency) {
			this.dependency = dependency;
		}

		public int getDependencyCount() {
			return dependency.getCount();
		}
	}

	private static final class TestDependency {

		private final int count;

		@Inject
		TestDependency() {
			this.count = COUNT.getAndIncrement();
		}

		public int getCount() {
			return count;
		}
	}

	private static final class TestModule extends AbstractModule {

		@Override
		protected void configure() {
			bind(TestInjectable.class);
			bind(TestDependency.class).in(ManualScoped.class);
		}

	}

}
