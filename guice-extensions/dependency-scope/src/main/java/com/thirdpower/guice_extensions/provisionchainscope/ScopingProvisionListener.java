package com.thirdpower.guice_extensions.provisionchainscope;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.Collectors;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Key;
import com.google.inject.spi.ProvisionListener;

public class ScopingProvisionListener implements ProvisionListener {

	private static final ScopingProvisionListener INSTANCE = new ScopingProvisionListener();

	static ScopingProvisionListener get() {
		return INSTANCE;
	}

	private final Deque<Key<?>> dependencyChain = new ArrayDeque<>();
	private final Multimap<Key<?>, DependencyScopeLeftListener> listeners = HashMultimap.create();

	@Override
	public <T> void onProvision(final ProvisionInvocation<T> provision) {
		System.out.println("in ScopingProvisionListener:onProvision of "
				+ provision.getBinding().getKey().getTypeLiteral().getRawType().getSimpleName());
		System.out.println("dependency chain: " + provision.getDependencyChain().stream()
				.map(das -> das.getDependency().getKey().getTypeLiteral().getRawType().getSimpleName())
				.collect(Collectors.toList()));

		try {
			final Key<T> provisionKey = provision.getBinding().getKey();
			dependencyChain.push(provisionKey);
			provision.provision();
		} finally {
			final Key<?> poppedKey = dependencyChain.pop();
			listeners.get(poppedKey).forEach(l -> l.onScopeLeft());
			listeners.removeAll(poppedKey);
		}
	}

	interface DependencyScopeLeftListener {
		void onScopeLeft();
	}

	void registerDependencyScopeLeftListener(final Key<?> key, final DependencyScopeLeftListener listener) {
		listeners.put(key, listener);
	}

	public boolean isDependencyScopeActive(final Key<?> dependencyKey) {
		return dependencyChain.contains(dependencyKey);
	}

}
