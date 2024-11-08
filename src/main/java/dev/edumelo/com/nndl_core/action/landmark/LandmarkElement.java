package dev.edumelo.com.nndl_core.action.landmark;

import dev.edumelo.com.nndl_core.nndl.NndlNode;
import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;

public class LandmarkElement extends StepElement implements Landmark {
	private static final String TAG = "element";
	private static final String TIMEOUT_TAG = "timeout";
	private static final Integer DEFAULT_TIMEOUT = 50;
	private Integer timeout;
	
	public LandmarkElement(StepElement element, NndlNode landMarkNode) {
		super(element);
		setName(element.getName());
		setMatchExp(element.getMatchExp());
		this.timeout = extractTimeout(landMarkNode);
	}

	@Override
	public Advice getLandMarkAdvice() {
		return new ContinueAdvice();
	}
	
	@Override
	public Integer getTimeout() {
		return timeout;
	}
	
	private Integer extractTimeout(NndlNode mappedForkElement) {
		return mappedForkElement.getScalarValueFromChild(TIMEOUT_TAG, Integer.class).orElse(DEFAULT_TIMEOUT);
	}
	
	public static String getTag() {
		return TAG;
	}
}
