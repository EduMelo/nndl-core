package dev.edumelo.com.nndl_core.contextAdapter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.Cookie;

public class ContextAdapterHandler {

	private static Map<String, List<ContextAdapter>> adapters;

	public static void createContext(String createUuid, ContextAdapter... context) {
		if(adapters == null) {
			adapters = new HashMap<>();
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
	
	public static Set<Cookie> retrieveCookies(String sessionId) {
		return adapters.get(sessionId).stream()
				.filter(a -> a instanceof CookieRetrieverAdapter)
				.map(a -> (CookieRetrieverAdapter) a)
				.map(CookieRetrieverAdapter::getCookies)
				.reduce((s1, s2) -> {
					s1.addAll(s2);
					return s1;
				}).orElse(new HashSet<>());
	}
	
}
