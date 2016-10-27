package com.thirdpower.guice_extensions.provisionscope;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

public class ProvisionScope<D> implements Scope {

	private final Key<D> scopeActivationKey;
	private final boolean allowOutOfScopeProvisioning;

	public static <D> ProvisionScope<D> of(final Class<D> dependency) {
		return new ProvisionScope<>(Key.get(dependency), false);
	}

	public static <D> ProvisionScope<D> ofOptional(final Class<D> dependency) {
		return new ProvisionScope<>(Key.get(dependency), true);
	}

	public static <D> ProvisionScope<D> of(final Key<D> dependency) {
		return new ProvisionScope<>(dependency, false);
	}

	public static <D> ProvisionScope<D> ofOptional(final Key<D> dependency) {
		return new ProvisionScope<>(dependency, true);
	}

	public static <D> ProvisionScope<D> of(final TypeLiteral<D> dependency) {
		return new ProvisionScope<>(Key.get(dependency), false);
	}

	public static <D> ProvisionScope<D> ofOptional(final TypeLiteral<D> dependency) {
		return new ProvisionScope<>(Key.get(dependency), true);
	}

	/**
	 *
	 * @param scopeActivationKey
	 *            {@link Key} whose provisioning starts the this scope
	 * @param allowOutOfScopeProvisioning
	 *            <code>true</code> if the binding with this scope is allowed to
	 *            be provisioned out of scope, <code>false</code> if the binding
	 *            with this scope is only allowed to be provisioned as direct or
	 *            transitive dependency of the given scopeActivationKey
	 */
	ProvisionScope(final Key<D> scopeActivationKey, final boolean allowOutOfScopeProvisioning) {
		this.scopeActivationKey = scopeActivationKey;
		this.allowOutOfScopeProvisioning = allowOutOfScopeProvisioning;
	}

	@Override
	public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {

		return new Provider<T>() {

			private final ThreadLocal<T> scopedInstance = new ThreadLocal<>();
			private final ScopingProvisionListener provisionListener = ScopingProvisionListener.get();

			@Override
			public T get() {

				if (!provisionListener.isInScope(scopeActivationKey)) {
					if (!allowOutOfScopeProvisioning) {
						final String msg = String.format(
								"Out of scope. %s must be provisioned as direct or transitive dependency of %s.", key,
								scopeActivationKey);
						throw new IllegalStateException(msg);
					}

					scopedInstance.remove();
					return unscoped.get();
				}

				final T threadLocalScopedInstance = scopedInstance.get();
				if (null != threadLocalScopedInstance) {
					return threadLocalScopedInstance;
				}

				final T unscopedInstance = unscoped.get();
				if (!Scopes.isCircularProxy(unscopedInstance)) {
					scopedInstance.set(unscopedInstance);
					provisionListener.registerScopeLeftListener(scopeActivationKey, scopedInstance::remove);
				}
				return unscopedInstance;

			}

			@Override
			public String toString() {
				final StringBuilder str = new StringBuilder(unscoped.toString()).append("[in ");
				if (allowOutOfScopeProvisioning) {
					str.append("optional ");
				}
				return str.append("ProvisionScope of ")
						.append(scopeActivationKey)
						.append("]")
						.toString();
			}

		};
	}

}
