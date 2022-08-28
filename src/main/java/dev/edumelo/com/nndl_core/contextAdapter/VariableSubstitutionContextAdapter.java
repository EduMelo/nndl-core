package dev.edumelo.com.nndl_core.contextAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class VariableSubstitutionContextAdapter implements ContextAdapter {
	private Map<String, Object> substitutionMap;
	
	public VariableSubstitutionContextAdapter() {};

	public VariableSubstitutionContextAdapter(Map<String, Object> substitutionMap) {
		this.substitutionMap = substitutionMap;
	}

	public Map<String, Object> getSubstitutionMap() {
		return substitutionMap;
	}
	
	public VariableSubstitutionContextAdapter add(Entry<String, Object> entry) {
		if(this.substitutionMap == null) {
			this.substitutionMap = new HashMap<>();
		}
		
		this.substitutionMap.put(entry.getKey(), entry.getValue());
		return this;		
	}

}
