package dev.edumelo.com.nndl_core.action;

import dev.edumelo.com.nndl_core.step.StepElement;
import lombok.Getter;

@Getter
public class StepElementIterationScope implements LoopIterationScope {
	private StepElement stepElement;
	
	@Override
	public LoopIterationScopeType getType() {
		return LoopIterationScopeType.PAGE_ELEMENT;
	}

	public StepElementIterationScope(StepElement stepElement) {
		this.stepElement = stepElement;
	}

}
