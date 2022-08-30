package dev.edumelo.com.nndl_core;

import java.util.List;
import java.util.Map;

public class NndlResult {
	private Map<String, List<ExtractDataBind>> mapResult;

	public NndlResult(Map<String, List<ExtractDataBind>> mapResult) {
		this.mapResult = mapResult;
	}

	@SuppressWarnings("unchecked")
	public <T extends ExtractDataBind> List<T> getResult(Class<T> clazz) {
		return (List<T>) mapResult.get(clazz.getName());
	}

}
