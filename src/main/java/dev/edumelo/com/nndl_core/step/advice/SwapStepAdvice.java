package dev.edumelo.com.nndl_core.step.advice;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString
public class SwapStepAdvice extends DefaultAdvice {
	private String advisedStep;
	
	@Override
	public AdviceType getType() {
		return AdviceType.SWAP_STEP;
	}

}
