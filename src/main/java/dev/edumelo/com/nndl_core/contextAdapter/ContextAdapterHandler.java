package dev.edumelo.com.nndl_core.contextAdapter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.apache.commons.collections4.map.PassiveExpiringMap.ConstantTimeToLiveExpirationPolicy;
import org.openqa.selenium.Cookie;

public class ContextAdapterHandler {

	private static PassiveExpiringMap<String, List<ContextAdapter>> adapters;

	public static void createContext(String createUuid, ContextAdapter... context) {
		ConstantTimeToLiveExpirationPolicy<String, List<ContextAdapter>> expirationPolicy =
				new ConstantTimeToLiveExpirationPolicy<>(30, TimeUnit.MINUTES);
		
		if(adapters == null) {
			adapters = new PassiveExpiringMap<>(expirationPolicy, new HashMap<>());
		}
		adapters.put(createUuid, Arrays.asList(context));
	}

	public static Map<String, Object> getVariableSubstitutionMap(String sessionId) {
		return adapters.get(sessionId).stream()
				.filter(a -> a instanceof VariableSubstitutionContextAdapter)
				.map(a -> (VariableSubstitutionContextAdapter) a)
				.map(VariableSubstitutionContextAdapter::getSubstitutionMap)
				.reduce((m1, m2) -> {
					m1.putAll(m2);
					return m1;})
				.orElse(new HashMap<>());
	}
	
	public static Set<Cookie> retrieveCookies(String sessionId, Object... params) {
		return adapters.get(sessionId).stream()
				.filter(a -> a instanceof CookieRetrieverAdapter)
				.map(a -> (CookieRetrieverAdapter) a)
				.map(c -> c.getCookies(params))
				.reduce((s1, s2) -> {
					s1.addAll(s2);
					return s1;
				}).orElseThrow(() -> new RuntimeException("Cannot get any retrieve cookie adapter"));
	}

	public static void storeCookies(String sessionId, Object[] storerParams, Set<Cookie> cookies) {
		List<ContextAdapter> cookiestoreAdapters = adapters.get(sessionId).stream()
		.filter(a -> a instanceof CookieStorerAdapter)
		.collect(Collectors.toList());
		
		if(CollectionUtils.isEmpty(cookiestoreAdapters)) {
			throw new RuntimeException("Cannot get any store cookie adapter");
		}
		
		cookiestoreAdapters.stream()
		.map(a -> (CookieStorerAdapter) a)
		.forEach(c->c.storeCookies(storerParams, cookies));
	}
	
}
