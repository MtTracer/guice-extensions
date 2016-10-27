package com.thirdpower.guice_extensions.provisionscope;

import java.util.ArrayDeque;
import java.util.Deque;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Key;
import com.google.inject.spi.ProvisionListener;

public class ScopingProvisionListener implements ProvisionListener {

	private static final ScopingProvisionListener INSTANCE = new ScopingProvisionListener();

	static ScopingProvisionListener get() {
		return INSTANCE;
	}

	private final ThreadLocal<Deque<Key<?>>> dependencyChain = ThreadLocal.withInitial(ArrayDeque<Key<?>>::new);
	private final ThreadLocal<Multimap<Key<?>, ScopeLeftListener>> listeners = ThreadLocal
			.withInitial(HashMultimap::create);

	@Override
	public <T> void onProvision(final ProvisionInvocation<T> provisionInvocation) {
		final Deque<Key<?>> threadLocalDependencyChain = dependencyChain.get();
		try {
			final Key<T> provisionKey = provisionInvocation.getBinding()
					.getKey();
			threadLocalDependencyChain.push(provisionKey);
			provisionInvocation.provision();
		} finally {
			final Key<?> keyOfFinishedProvision = threadLocalDependencyChain.pop();
			listeners.get()
					.removeAll(keyOfFinishedProvision)
					.forEach(ScopeLeftListener::onScopeLeft);
		}
	}

	interface ScopeLeftListener {
		void onScopeLeft();
	}

	void registerScopeLeftListener(final Key<?> key, final ScopeLeftListener listener) {
		listeners.get()
				.put(key, listener);
	}

	boolean isInScope(final Key<?> key) {
		return dependencyChain.get()
				.contains(key);
	}

}
