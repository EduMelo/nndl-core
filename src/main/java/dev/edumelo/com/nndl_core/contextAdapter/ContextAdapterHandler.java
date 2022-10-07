package dev.edumelo.com.nndl_core.contextAdapter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.apache.commons.collections4.map.PassiveExpiringMap.ConstantTimeToLiveExpirationPolicy;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;

import dev.edumelo.com.nndl_core.ExtractDataBind;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;
import dev.edumelo.com.nndl_core.step.advice.RunControllerAdvice;

public class ContextAdapterHandler {

	private static PassiveExpiringMap<String, List<ContextAdapter>> adapters;
	private static PassiveExpiringMap<String, List<ExtractDataBind>> extractedData;

	public static void createContext(String createUuid, ContextAdapter... context) {
		ConstantTimeToLiveExpirationPolicy<String, List<ContextAdapter>> adapterExpirationPolicy =
				new ConstantTimeToLiveExpirationPolicy<>(30, TimeUnit.MINUTES);
		
		if(adapters == null) {
			adapters = new PassiveExpiringMap<>(adapterExpirationPolicy, new HashMap<>());
		}
		adapters.put(createUuid, Arrays.asList(context));
		
		ConstantTimeToLiveExpirationPolicy<String, List<ExtractDataBind>> extractedDataExpirationPolicy =
				new ConstantTimeToLiveExpirationPolicy<>(30, TimeUnit.MINUTES);
		if(extractedData == null) {
			extractedData = new PassiveExpiringMap<>(extractedDataExpirationPolicy, new HashMap<>());
		}
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

	public static Advice addExtractedData(String sessionId, String extractDataBindAdapterName,
			WebElement element) {
		List<ExtractDataBind> list = adapters.get(sessionId).stream()
			.filter(a -> a instanceof ExtractDataBindAdapter)
			.map(a -> (ExtractDataBindAdapter) a)
			.map(e -> e.createFromElement(element))
			.collect(Collectors.toList());
		
		String key = String.format("%s%s", sessionId, extractDataBindAdapterName);
		List<ExtractDataBind> storedData = extractedData.get(key);
		if(storedData == null || storedData.size() == 0) {
			extractedData.put(key, list);			
		} else {
			storedData.addAll(list);
		}
		
		return list
				.stream()
				.map(ExtractDataBind::suggestedAdvice)
				.filter(a -> a instanceof RunControllerAdvice)
				.findAny()
				.orElse(new ContinueAdvice());
	}
	
	public static Map<String, List<ExtractDataBind>> getExtractedData(String sessionId) {
		Map<String, List<ExtractDataBind>> result = new HashMap<>();
		for (Entry<String, List<ExtractDataBind>> entry : extractedData.entrySet()) {
			if(entry.getKey().startsWith(sessionId)) {
				result.put(entry.getKey().replace(sessionId, ""), entry.getValue());
			}
		}
		return result;
	}

	public static void expireSession(String sessionId) {
		adapters.remove(sessionId);
		MapIterator<String, List<ExtractDataBind>> iterator = extractedData.mapIterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			if(key.startsWith(sessionId)) {
				iterator.remove();
			}
		}
	}
	
}
