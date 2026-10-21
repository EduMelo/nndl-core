package dev.edumelo.com.nndl_core.contextAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;

import dev.edumelo.com.nndl_core.ExtractDataBind;
import dev.edumelo.com.nndl_core.action.impl.triggerer.ActionTrigger;
import dev.edumelo.com.nndl_core.exceptions.unchecked.NndlRuntimeException;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;
import dev.edumelo.com.nndl_core.step.advice.RunControllerAdvice;
import dev.edumelo.com.nndl_core.webdriver.BrowserArgumentsContextAdapter;
import dev.edumelo.com.nndl_core.webdriver.SeleniumNndlWebDriver;

/**
 * Gives access to the {@link NndlContext} of the current run.
 * <p>
 * The context is stored in a {@link ScopedValue}: it is inherited by the code
 * called from within {@link #callWithContext(NndlContext, Supplier)} and
 * discarded when the scope ends, with no need to clean up thread locals.
 * <p>
 * Scoped values are not inherited by threads started by the scoped code, so
 * asynchronous work must explicitly re-bind the context with
 * {@link #runWithContext(NndlContext, Runnable)}.
 */
public final class NndlContextManager {

	private static final ScopedValue<NndlContext> CONTEXT = ScopedValue.newInstance();

	private NndlContextManager() {
	}

	public static <T> T callWithContext(NndlContext context, Supplier<T> task) {
		if (context == null) {
			return task.get();
		}
		return ScopedValue.where(CONTEXT, context).call(task::get);
	}

	public static void runWithContext(NndlContext context, Runnable task) {
		if (context == null) {
			task.run();
			return;
		}
		ScopedValue.where(CONTEXT, context).run(task);
	}

	public static boolean isContextBound() {
		return CONTEXT.isBound();
	}

	public static NndlContext getContext() {
		if (!CONTEXT.isBound()) {
			throw new NndlRuntimeException("No NNDL context is bound to the current scope.");
		}
		return CONTEXT.get();
	}

	public static NndlContext getContextOrNull() {
		return CONTEXT.isBound() ? CONTEXT.get() : null;
	}

	public static void setWebDriverSessionId(String sessionId) {
		getContext().setWebDriverSessionId(sessionId);
	}

	public static String getWebDriverSessionId() {
		NndlContext context = getContextOrNull();
		return context == null ? null : context.getWebDriverSessionId();
	}

	public static String retrieveNndlName() {
		NndlContext context = getContextOrNull();
		return context == null ? "" : context.getNndlName();
	}

	public static List<ContextAdapter> getAdapters() {
		return getContext().getAdapters();
	}

	public static Map<String, List<ExtractDataBind>> getExtractedData() {
		return getContext().getExtractedData();
	}

	public static Advice addExtractedData(SeleniumNndlWebDriver webDriver, String extractDataBindAdapterName,
			WebElement element) {
		List<ExtractDataBind> list = getAdapters().stream()
			.filter(a -> a instanceof ExtractDataBindAdapter)
			.map(a -> (ExtractDataBindAdapter<?>) a)
			.map(e -> e.createFromElement(webDriver, element))
			.collect(Collectors.toList());

		Map<String, List<ExtractDataBind>> map = getExtractedData();
		List<ExtractDataBind> storedData = map.computeIfAbsent(extractDataBindAdapterName,
				key -> Collections.synchronizedList(new ArrayList<>()));
		storedData.addAll(list);

		return list.stream()
				.map(ExtractDataBind::suggestedAdvice)
				.filter(a -> a instanceof RunControllerAdvice)
				.findFirst()
				.orElse(new ContinueAdvice());
	}

	public static Map<String, String> getVariableSubstitutionMap() {
		return getAdapters().stream()
				.filter(a -> a instanceof VariableSubstitutionContextAdapter)
				.map(a -> (VariableSubstitutionContextAdapter) a)
				.map(VariableSubstitutionContextAdapter::getSubstitutionMap)
				.reduce((m1, m2) -> {
					m1.putAll(m2);
					return m1;})
				.orElse(new HashMap<>());
	}

	public static void storeCookies(Object[] storerParams, Set<Cookie> cookies) {
		getAdapters().stream()
		.filter(storer -> storer instanceof CookieStorerAdapter)
		.map(storer -> (CookieStorerAdapter) storer)
		.forEach(storer -> storer.storeCookies(storerParams, cookies));
	}

	public static Set<Cookie> retrieveCookies(Object... params) {
		return getAdapters().stream()
				.filter(a -> a instanceof CookieRetrieverAdapter)
				.map(a -> (CookieRetrieverAdapter) a)
				.map(c -> c.getCookies(params))
				.reduce((s1, s2) -> {
					s1.addAll(s2);
					return s1;
				}).orElseThrow(() -> new NndlRuntimeException("Cannot get any retrieve cookie adapter"));
	}

	public static void storeSourceCode(String node, String sourceCode) {
		getAdapters().stream()
		.filter(storer -> storer instanceof SourceCodeStorerAdapter)
		.map(s -> (SourceCodeStorerAdapter) s)
		.forEach(storer -> storer.storeSourceCode(node, sourceCode));
	}

	public static void triggerAction(String triggerId, Object[] triggerParams) {
		if (triggerId == null) {
			return;
		}

		getAdapters().stream()
			.filter(a -> a instanceof ActionTrigger)
			.map(a -> (ActionTrigger) a)
			.filter(a -> triggerId.equals(a.getTriggerId()))
			.map(a -> (ActionTrigger) a)
			.forEach(c -> c.triggerAction(triggerParams));
	}

	public static List<BrowserArgumentsContextAdapter> getBrowserArgumentsContextAdapter() {
		return getAdapters()
				.stream()
				.filter(a -> a instanceof BrowserArgumentsContextAdapter)
				.map(a -> (BrowserArgumentsContextAdapter) a)
				.collect(Collectors.toList());
	}

}
