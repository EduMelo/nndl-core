package dev.edumelo.com.nndl_core.step.advice;

public class StopRunAdvice extends RunControllerAdvice {

	@Override
	public AdviceType getType() {
		return AdviceType.STOP_RUN;
	}

}
