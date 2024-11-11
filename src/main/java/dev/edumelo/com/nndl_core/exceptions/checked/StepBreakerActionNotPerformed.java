package dev.edumelo.com.nndl_core.exceptions.checked;

import dev.edumelo.com.nndl_core.nndl.NndlNode;

public class StepBreakerActionNotPerformed extends NndlFlowBreakerException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6012955666080058202L;
	private String stepTreatment;
	
	public StepBreakerActionNotPerformed(String msg, NndlNode node) {
		super(msg, node);
	}
	
	public StepBreakerActionNotPerformed(String msg, NndlNode node, Exception e, String stepTreatment) {
		super(msg, node, e);
		this.stepTreatment = stepTreatment;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getStepTreatment() {
		return stepTreatment;
	}

}
