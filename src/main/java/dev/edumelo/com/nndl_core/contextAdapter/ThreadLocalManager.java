package dev.edumelo.com.nndl_core.contextAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;

import dev.edumelo.com.nndl_core.ExtractDataBind;
import dev.edumelo.com.nndl_core.action.impl.triggerer.ActionTrigger;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;
import dev.edumelo.com.nndl_core.step.advice.RunControllerAdvice;
import dev.edumelo.com.nndl_core.webdriver.BrowserArgumentsContextAdapter;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;

public class ThreadLocalManager {

	private static ThreadLocal<List<ContextAdapter>> contextAdapters = 
			ThreadLocal.withInitial(() -> new ArrayList<>());
	
	private static ThreadLocal<String> webDriversSessionIds = 
			ThreadLocal.withInitial(() -> null);
	
	private static ThreadLocal<Map<String, List<ExtractDataBind>>> extractedDataMap =
			ThreadLocal.withInitial(() -> new HashMap<>());
	
	public static void setAdapters(List<ContextAdapter> adapters) {
		contextAdapters.set(adapters);
	}
	
	public static List<ContextAdapter> getAdapters() {
		return contextAdapters.get();
	}
	
	public static void setWebDriverSessionId(String sessionId) {
		webDriversSessionIds.set(sessionId);
	}
	
	public static String getWebDriverSessionId() {
		return webDriversSessionIds.get();
	}
	
	public static Advice addExtractedData(SeleniumSndlWebDriver webDriver, String extractDataBindAdapterName, WebElement element) {
		List<ExtractDataBind> list = contextAdapters.get().stream()
			.filter(a -> a instanceof ExtractDataBindAdapter)
			.map(a -> (ExtractDataBindAdapter<?>) a)
			.map(e -> e.createFromElement(webDriver, element))
			.collect(Collectors.toList());
		
		String key = extractDataBindAdapterName;
		Map<String, List<ExtractDataBind>> map = extractedDataMap.get();
		List<ExtractDataBind> storedData = map.get(key);
		if(CollectionUtils.isEmpty(storedData)) {
			map.put(key, list);
		} else {
			storedData.addAll(list);
		}
		
		return list.stream()
				.map(ExtractDataBind::suggestedAdvice)
				.filter(a -> a instanceof RunControllerAdvice)
				.findFirst()
				.orElse(new ContinueAdvice());
	}
	
	public static Map<String, List<ExtractDataBind>> getExtractedData() {
		return extractedDataMap.get();
	}
	
	public static Map<String, String> getVariableSubstitutionMap() {
		return contextAdapters.get().stream()
				.filter(a -> a instanceof VariableSubstitutionContextAdapter)
				.map(a -> (VariableSubstitutionContextAdapter) a)
				.map(VariableSubstitutionContextAdapter::getSubstitutionMap)
				.reduce((m1, m2) -> {
					m1.putAll(m2);
					return m1;})
				.orElse(new HashMap<>());
	}
	
	public static void storeCookies(Object[] storerParams, Set<Cookie> cookies) {
		contextAdapters.get().stream()
		.filter(storer -> storer instanceof CookieStorerAdapter)
		.map(storer -> (CookieStorerAdapter) storer)
		.forEach(storer -> storer.storeCookies(storerParams, cookies));
	}
	
	public static Set<Cookie> retrieveCookies(Object... params) {
		return contextAdapters.get().stream()
				.filter(a -> a instanceof CookieRetrieverAdapter)
				.map(a -> (CookieRetrieverAdapter) a)
				.map(c -> c.getCookies(params))
				.reduce((s1, s2) -> {
					s1.addAll(s2);
					return s1;
				}).orElseThrow(() -> new RuntimeException("Cannot get any retrieve cookie adapter"));
	}
	
	public static void triggerAction(String triggerId, Object[] triggerParams) {
		if(triggerId == null) {
			return;
		}
		
		contextAdapters.get().stream()
			.filter(a -> a instanceof ActionTrigger)
			.map(a -> (ActionTrigger) a)
			.filter(a -> triggerId.equals(a.getTriggerId()))
			.map(a -> (ActionTrigger) a)
			.forEach(c -> c.triggerAction(triggerParams));
	}
	
	public static List<BrowserArgumentsContextAdapter> getBrowserArgumentsContextAdapter() {
		return contextAdapters.get()
				.stream()
				.filter(a -> a instanceof BrowserArgumentsContextAdapter)
				.map(a -> (BrowserArgumentsContextAdapter) a)
				.collect(Collectors.toList());
	}
	
	public static void expireSession() {
		contextAdapters.remove();
		webDriversSessionIds.remove();
		extractedDataMap.remove();
	}
	
}
