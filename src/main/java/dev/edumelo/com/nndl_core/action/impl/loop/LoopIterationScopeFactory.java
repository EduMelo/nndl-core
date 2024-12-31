package dev.edumelo.com.nndl_core.action.impl.loop;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import dev.edumelo.com.nndl_core.exceptions.unchecked.NndlParserRuntimeException;
import dev.edumelo.com.nndl_core.nndl.NndlNode;
import dev.edumelo.com.nndl_core.step.StepElement;

public class LoopIterationScopeFactory {
	private static final String ITERATION_SCOPE_TYPE_TAG = "type";
	private static final String ITERATION_SCOPE_PAGE_ELEMENT_TAG = "pageElement";
	private static final String ITERATION_SCOPE_LIST_TAG = "list";

	public static LoopIterationScope create(NndlNode value, Map<String, StepElement> mappedElements) {
		Optional<LoopIterationScopeType> map = value
				.getScalarValueFromChild(ITERATION_SCOPE_TYPE_TAG)
				.map(LoopIterationScopeType::byTag);
		return map
				.map(type -> {
					switch (type) {
						case PAGE_ELEMENT:
							return createPageElementIterationScope(value, mappedElements);
						case LIST:
						default:
							return createListIterationScope(value);
					}
				})
				.orElse(createPageElementIterationScope(value, mappedElements));
	}

	private static LoopIterationScope createListIterationScope(NndlNode value) {
		List<String> list = value.extractScalarList(ITERATION_SCOPE_LIST_TAG);
		return new ListIterationScope(list);
	}

	private static LoopIterationScope createPageElementIterationScope(NndlNode value, Map<String, StepElement> mappedElements) {
		return value.getScalarValueFromChild(ITERATION_SCOPE_PAGE_ELEMENT_TAG)
				.map(mappedElements::get)
				.map(StepElementIterationScope::new)
				.orElseThrow(NndlParserRuntimeException.get("Action Loop should have "+ITERATION_SCOPE_PAGE_ELEMENT_TAG+" tag",
						value));	
	}

}
