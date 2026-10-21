package dev.edumelo.com.nndl_core;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import dev.edumelo.com.nndl_core.contextAdapter.ExtractDataBindAdapter;

public class NndlResult {
	private Map<String, List<ExtractDataBind>> mapResult;
	private List<String> logs;

	public NndlResult(Map<String, List<ExtractDataBind>> mapResult) {
		this(mapResult, List.of());
	}

	public NndlResult(Map<String, List<ExtractDataBind>> mapResult, List<String> logs) {
		this.mapResult = mapResult;
		this.logs = logs == null ? List.of() : List.copyOf(logs);
	}

	public List<String> getLogs() {
		return logs;
	}

	@SuppressWarnings("unchecked")
	public <U extends ExtractDataBind, T extends ExtractDataBindAdapter<U>> List<U> getResult(Class<T> clazz) {
		return (List<U>) mapResult.get(clazz.getName());
	}

	@SuppressWarnings("unchecked")
	public <U extends ExtractDataBind, T extends ExtractDataBindAdapter<U>> U getSingleResult(Class<T> clazz) {
		List<U> list = (List<U>) mapResult.get(clazz.getName());
		if(CollectionUtils.isNotEmpty(list)) {
			return list.get(0);
		}
		return null;
	}
}
