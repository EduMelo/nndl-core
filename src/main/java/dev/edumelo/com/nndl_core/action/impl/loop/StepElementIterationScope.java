package dev.edumelo.com.nndl_core.action.impl.loop;

import dev.edumelo.com.nndl_core.step.StepElement;

public class StepElementIterationScope implements LoopIterationScope {
	private StepElement stepElement;
	
	@Override
	public LoopIterationScopeType getType() {
		return LoopIterationScopeType.PAGE_ELEMENT;
	}

	public StepElement getStepElement() {
		return stepElement;
	}

	public StepElementIterationScope(StepElement stepElement) {
		this.stepElement = stepElement;
	}

}
