package dev.edumelo.com.nndl_core.step;

import lombok.Getter;

@Getter
public class StepBreakerActionNotPerformed extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6012955666080058202L;
	private String stepTreatment;
	
	public StepBreakerActionNotPerformed(String msg, Exception e, String stepTreatment) {
		super(msg, e);
		this.stepTreatment = stepTreatment;
	}

	public StepBreakerActionNotPerformed(String msg) {
		super(msg);
	}

}
