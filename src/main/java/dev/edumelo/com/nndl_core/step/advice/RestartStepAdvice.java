package dev.edumelo.com.nndl_core.step.advice;

public class RestartStepAdvice extends DefaultAdvice {

	@Override
	public AdviceType getType() {
		return AdviceType.RESTART_STEP;
	}

}
