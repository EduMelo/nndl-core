package dev.edumelo.com.nndl_core.contextAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import dev.edumelo.com.nndl_core.ExtractDataBind;

/**
 * Ambient state shared by every component of a single NNDL run.
 * <p>
 * A context instance is bound to a {@link java.lang.ScopedValue} by
 * {@link NndlContextManager}, so it is visible only while the run executes.
 * The extracted data map is intentionally mutable because actions accumulate
 * results into it while the run progresses.
 */
public final class NndlContext {

	private final List<ContextAdapter> adapters;
	private final Map<String, List<ExtractDataBind>> extractedData;
	private final String nndlName;
	private final List<String> logs = Collections.synchronizedList(new ArrayList<>());
	private volatile String webDriverSessionId;

	public NndlContext(String nndlName, List<ContextAdapter> adapters,
			Map<String, List<ExtractDataBind>> extractedData) {
		this.nndlName = nndlName == null ? "" : nndlName;
		this.adapters = adapters == null ? List.of() : List.copyOf(adapters);
		this.extractedData = extractedData == null ? new ConcurrentHashMap<>()
				: new ConcurrentHashMap<>(extractedData);
	}

	public static NndlContext of(String nndlName, ContextAdapter... adapters) {
		List<ContextAdapter> adapterList = adapters == null ? List.of()
				: Arrays.stream(adapters).filter(Objects::nonNull).toList();
		return new NndlContext(nndlName, adapterList, null);
	}

	public List<ContextAdapter> getAdapters() {
		return adapters;
	}

	public Map<String, List<ExtractDataBind>> getExtractedData() {
		return extractedData;
	}

	public String getNndlName() {
		return nndlName;
	}

	public String getWebDriverSessionId() {
		return webDriverSessionId;
	}

	public void setWebDriverSessionId(String webDriverSessionId) {
		this.webDriverSessionId = webDriverSessionId;
	}

	public void addLog(String message) {
		logs.add(message);
	}

	public List<String> getLogs() {
		return List.copyOf(logs);
	}

}
