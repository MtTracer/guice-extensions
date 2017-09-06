package com.thirdpower.guiceextensions.provisionscope;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.inject.Provider;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;
import com.thirdpower.guiceextensions.provisionscope.ProvisionScope;
import com.thirdpower.guiceextensions.provisionscope.ProvisionScopeModule;

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

	private static final class AProvider implements Provider<A> {

		private final CountDownLatch barrier;
		private final Provider<B> bProvider;

		@Inject
		AProvider(final CountDownLatch barrier, final Provider<B> bProvider) {
			this.barrier = barrier;
			this.bProvider = bProvider;
		}

		@Override
		public A get() {
			try {
				barrier.countDown();
				barrier.await();
			} catch (final InterruptedException e) {
				Throwables.propagate(e);
			}
			return new A(bProvider.get(), bProvider.get(), bProvider.get());
		}

	}

	@Test
	public void testMultipleInjectorsMultipleThreads()
			throws InterruptedException, ExecutionException, TimeoutException {
		// TODO test is non-deterministic - bruteforce testing parallel
		// invocations

		final int numInjectors = 100;
		final int invocationsPerInjector = 5;
		final int nThreads = numInjectors * invocationsPerInjector;

		final CountDownLatch barrier = new CountDownLatch(nThreads + 1);

		final AbstractModule testModule = new AbstractModule() {
			@Override
			protected void configure() {
				bind(CountDownLatch.class).toInstance(barrier);
				bind(A.class).toProvider(AProvider.class);
				bind(B.class);
				bind(C.class).in(ProvisionScope.of(A.class));
			}
		};

		final ExecutorService threadPool = Executors.newFixedThreadPool(nThreads);
		final List<CompletableFuture<A>> futureAs = Lists.newArrayListWithCapacity(nThreads);
		for (int i = 0; i < numInjectors; i++) {
			final Injector injector = Guice.createInjector(new ProvisionScopeModule(), testModule);
			for (int j = 0; j < invocationsPerInjector; j++) {
				final CompletableFuture<A> futureA = CompletableFuture.supplyAsync(() -> injector.getInstance(A.class),
						threadPool);
				futureAs.add(futureA);
			}
		}

		// start porovisioning when all threads are waiting in AProvider and we
		// reached this point here
		barrier.countDown();
		final int numFutures = futureAs.size();
		CompletableFuture.allOf(futureAs.toArray(new CompletableFuture[numFutures]))
				.join();

		for (int i = 0; i < numFutures - 1; i++) {
			final A a1 = futureAs.get(i)
					.get();
			for (int j = i + 1; j < numFutures; j++) {
				final A a2 = futureAs.get(j)
						.get();
				assertInstancesDiffer(a1, a2);
			}
		}

	}

	private void assertInstancesDiffer(final A a2, final A a1) {
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
	public void testOutOfScopeWhenAnotherThreadIsInScope()
			throws InterruptedException, ExecutionException, TimeoutException, BrokenBarrierException {

		final CountDownLatch barrier = new CountDownLatch(2);

		final AbstractModule testModule = new AbstractModule() {
			@Override
			protected void configure() {
				bind(CountDownLatch.class).toInstance(barrier);
				bind(A.class).toProvider(AProvider.class);
				bind(B.class);
				bind(C.class).in(ProvisionScope.of(A.class));
			}
		};

		final Injector injector1 = Guice.createInjector(new ProvisionScopeModule(), testModule);
		final Injector injector2 = Guice.createInjector(new ProvisionScopeModule(), testModule);

		// let separate thread wait while provisioning A
		Executors.newSingleThreadExecutor()
				.submit(() -> injector1.getInstance(A.class));

		// creating a C in this thread must fail, because it's out of scope of
		// provisioning A
		// this test assures, that another thread being in-scope doesn't allow
		// an out-of-scope thread to create the scope-dependent instance
		ex.expect(ProvisionException.class);
		ex.reportMissingExceptionWithMessage(
				"Injection of C from injector2 must fail, because it is out of scope (no provisioning of A active in this thread).");
		try {
			injector2.getInstance(C.class);
		} finally {
			// release waiting thread and let it finish creating A
			barrier.countDown();
		}

	}
}
