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
import dev.edumelo.com.nndl_core.action.impl.triggerer.ActionTrigger;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;
import dev.edumelo.com.nndl_core.step.advice.RunControllerAdvice;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;

public class ContextAdapterHandler {

	private static PassiveExpiringMap<String, String> sessionsIds;
	private static PassiveExpiringMap<String, List<ContextAdapter>> adapters;
	private static PassiveExpiringMap<String, List<ExtractDataBind>> extractedData;

	public static void createContext(String nndlSessionId, ContextAdapter... context) {
		ConstantTimeToLiveExpirationPolicy<String, List<ContextAdapter>> adapterExpirationPolicy =
				new ConstantTimeToLiveExpirationPolicy<>(30, TimeUnit.MINUTES);
		if(adapters == null) {
			adapters = new PassiveExpiringMap<>(adapterExpirationPolicy, new HashMap<>());
		}
		adapters.put(nndlSessionId, Arrays.asList(context));
		
		ConstantTimeToLiveExpirationPolicy<String, List<ExtractDataBind>> extractedDataExpirationPolicy =
				new ConstantTimeToLiveExpirationPolicy<>(30, TimeUnit.MINUTES);
		if(extractedData == null) {
			extractedData = new PassiveExpiringMap<>(extractedDataExpirationPolicy, new HashMap<>());
		}
		
		ConstantTimeToLiveExpirationPolicy<String, String> sessionIdsExpirationPolicy =
				new ConstantTimeToLiveExpirationPolicy<>(30, TimeUnit.MINUTES);
		if(sessionsIds == null) {
			sessionsIds = new PassiveExpiringMap<>(sessionIdsExpirationPolicy, new HashMap<>());
		}
	}
	
	public static void setWebDriverSessionId(String nndlRunnerSessionId, String webDriverSessionId) {
		sessionsIds.put(nndlRunnerSessionId, webDriverSessionId);
	}
	
	public static void storeCookies(String nndlSessionId, Object[] storerParams,
			Set<Cookie> cookies) {
		List<ContextAdapter> cookiestoreAdapters = adapters.get(nndlSessionId).stream()
		.filter(a -> a instanceof CookieStorerAdapter)
		.collect(Collectors.toList());
		
		if(CollectionUtils.isEmpty(cookiestoreAdapters)) {
			throw new RuntimeException("Cannot get any store cookie adapter");
		}
		
		cookiestoreAdapters.stream()
		.map(a -> (CookieStorerAdapter) a)
		.forEach(c->c.storeCookies(storerParams, cookies));
	}
	
	public static Advice addExtractedData(SeleniumSndlWebDriver webDriver, String nndlSessionId,
			String extractDataBindAdapterName, WebElement element) {
		List<ExtractDataBind> list = adapters.get(nndlSessionId).stream()
			.filter(a -> a instanceof ExtractDataBindAdapter)
			.map(a -> (ExtractDataBindAdapter<?>) a)
			.map(e -> e.createFromElement(webDriver, element))
			.collect(Collectors.toList());
		
		String key = String.format("%s%s", nndlSessionId, extractDataBindAdapterName);
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
	
	public static String getWebDriverSessionId(String nndlRunnerSessionId) {
		return sessionsIds.get(nndlRunnerSessionId);
	}

	public static Map<String, Object> getVariableSubstitutionMap(String nndlSessionId) {
		return adapters.get(nndlSessionId).stream()
				.filter(a -> a instanceof VariableSubstitutionContextAdapter)
				.map(a -> (VariableSubstitutionContextAdapter) a)
				.map(VariableSubstitutionContextAdapter::getSubstitutionMap)
				.reduce((m1, m2) -> {
					m1.putAll(m2);
					return m1;})
				.orElse(new HashMap<>());
	}
	
	public static Set<Cookie> retrieveCookies(String nndlSessionId, Object... params) {
		return adapters.get(nndlSessionId).stream()
				.filter(a -> a instanceof CookieRetrieverAdapter)
				.map(a -> (CookieRetrieverAdapter) a)
				.map(c -> c.getCookies(params))
				.reduce((s1, s2) -> {
					s1.addAll(s2);
					return s1;
				}).orElseThrow(() -> new RuntimeException("Cannot get any retrieve cookie adapter"));
	}

	public static void triggerAction(String nndlSessionId, String triggerId,
			Object[] triggerParams) {
		if(triggerId == null) {
			return;
		}
		
		List<ContextAdapter> actionTriggerAdapter = adapters.get(nndlSessionId).stream()
				.filter(a -> a instanceof ActionTrigger)
				.map(a -> (ActionTrigger) a)
				.filter(a -> triggerId.equals(a.getTriggerId()))
				.collect(Collectors.toList());
		
		if(CollectionUtils.isEmpty(actionTriggerAdapter)) {
			throw new RuntimeException("Cannot ge any trigger action adapter");
		}
		
		actionTriggerAdapter.stream()
		.map(a -> (ActionTrigger) a)
		.forEach(c -> c.triggerAction(triggerParams));
	}
	
	public static Map<String, List<ExtractDataBind>> getExtractedData(String nndlSessionId) {
		Map<String, List<ExtractDataBind>> result = new HashMap<>();
		for (Entry<String, List<ExtractDataBind>> entry : extractedData.entrySet()) {
			if(entry.getKey().startsWith(nndlSessionId)) {
				result.put(entry.getKey().replace(nndlSessionId, ""), entry.getValue());
			}
		}
		return result;
	}

	public static void expireSession(String nndlSessionId) {
		sessionsIds.remove(nndlSessionId);
		adapters.remove(nndlSessionId);
		MapIterator<String, List<ExtractDataBind>> iterator = extractedData.mapIterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			if(key.startsWith(nndlSessionId)) {
				iterator.remove();
			}
		}
	}
	
}
