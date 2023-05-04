package dev.edumelo.com.nndl_core.action.impl.loop;

import java.util.Map;

import dev.edumelo.com.nndl_core.step.StepElement;

import java.util.List;

public class LoopIterationScopeFactory {
	private static final String ITERATION_SCOPE_TYPE_TAG = "type";
	private static final String ITERATION_SCOPE_PAGE_ELEMENT_TAG = "pageElement";
	private static final String ITERATION_SCOPE_LIST_TAG = "list";

	public static LoopIterationScope create(Map<String, ?> value, Map<String, StepElement> mappedElements) {
		Object objectValue = (Object) value.get(ITERATION_SCOPE_TYPE_TAG);
		
		if(objectValue != null) {
			String typeValue = (String) value.get(ITERATION_SCOPE_TYPE_TAG);
			LoopIterationScopeType type = LoopIterationScopeType.byTag(typeValue);
			
			switch (type) {
			case PAGE_ELEMENT:
				return createPageElementIterationScope(value, mappedElements);
			case LIST:
			default:
				return createListIterationScope(value);
			}
		}
		
		return createPageElementIterationScope(value, mappedElements);
	}

	@SuppressWarnings("unchecked")
	private static LoopIterationScope createListIterationScope(Map<String, ?> value) {
		List<String> listValue = (java.util.List<String>) value.get(ITERATION_SCOPE_LIST_TAG);
		return new ListIterationScope(listValue);
	}

	private static LoopIterationScope createPageElementIterationScope(Map<String, ?> value, Map<String, StepElement> mappedElements) {
		String stepElementName = (String) value.get(ITERATION_SCOPE_PAGE_ELEMENT_TAG);
		StepElement stepElement = mappedElements.get(stepElementName);
		return new StepElementIterationScope(stepElement);		
	}

}
