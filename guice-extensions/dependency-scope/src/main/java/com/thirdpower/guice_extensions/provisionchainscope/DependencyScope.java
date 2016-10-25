package com.thirdpower.guice_extensions.provisionchainscope;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.TypeLiteral;

public class DependencyScope<D> implements Scope {

	private final Key<D> scopeDependency;
	private final boolean optional;

	public static <D> DependencyScope<D> of(final Class<D> dependency) {
		return new DependencyScope<>(Key.get(dependency), false);
	}

	public static <D> DependencyScope<D> ofOptional(final Class<D> dependency) {
		return new DependencyScope<>(Key.get(dependency), true);
	}

	public static <D> DependencyScope<D> of(final Key<D> dependency) {
		return new DependencyScope<>(dependency, false);
	}

	public static <D> DependencyScope<D> ofOptional(final Key<D> dependency) {
		return new DependencyScope<>(dependency, true);
	}

	public static <D> DependencyScope<D> of(final TypeLiteral<D> dependency) {
		return new DependencyScope<>(Key.get(dependency), false);
	}

	public static <D> DependencyScope<D> ofOptional(final TypeLiteral<D> dependency) {
		return new DependencyScope<>(Key.get(dependency), true);
	}

	DependencyScope(final Key<D> scopeDependency, final boolean optional) {
		this.scopeDependency = scopeDependency;
		this.optional = optional;
	}

	@Override
	public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {

		return new Provider<T>() {

			private T scopedInstance = null;

			@Override
			public T get() {
				System.out.println("depending on " + scopeDependency);

				final ScopingProvisionListener provisionListener = ScopingProvisionListener.get();

				if (!provisionListener.isDependencyScopeActive(scopeDependency)) {
					if (!optional) {
						final String msg = String.format(
								"Scope is not active. %s must be requested as direct or transitive dependency of %s.",
								key, scopeDependency);
						throw new IllegalStateException(msg);
					}

					scopedInstance = null;
					return unscoped.get();
				}

				if (null == scopedInstance) {
					final T unscopedInstance = unscoped.get();
					this.scopedInstance = unscopedInstance;
					provisionListener.registerDependencyScopeLeftListener(scopeDependency,
							() -> this.scopedInstance = null);
				}
				return scopedInstance;
			}

			@Override
			public String toString() {
				return "DependencyScoped provider of " + super.toString();
			}

		};
	}

}
