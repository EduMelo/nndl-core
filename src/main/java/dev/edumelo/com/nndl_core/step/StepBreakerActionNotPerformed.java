package dev.edumelo.com.nndl_core.step;

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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getStepTreatment() {
		return stepTreatment;
	}

	public StepBreakerActionNotPerformed(String msg) {
		super(msg);
	}

}
