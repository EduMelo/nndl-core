package dev.edumelo.com.nndl_core.step.advice;

import java.util.Objects;

public class SwapStepAdvice extends RunControllerAdvice {
	private String advisedStep;
	
	@Override
	public AdviceType getType() {
		return AdviceType.SWAP_STEP;
	}

	public String getAdvisedStep() {
		return advisedStep;
	}
	public void setAdvisedStep(String advisedStep) {
		this.advisedStep = advisedStep;
	}

	@Override
	public int hashCode() {
		return Objects.hash(advisedStep);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SwapStepAdvice other = (SwapStepAdvice) obj;
		return Objects.equals(advisedStep, other.advisedStep);
	}

	@Override
	public String toString() {
		return "SwapStepAdvice [advisedStep=" + advisedStep + "]";
	}

}
