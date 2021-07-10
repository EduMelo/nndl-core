package dev.edumelo.com.nndl_core.action.landmark;

import java.util.Map;

import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;

public class LandmarkElement extends StepElement implements Landmark {
	private static final String TAG = "element";
	private static final String TIMEOUT_TAG = "timeout";
	private static final Integer DEFAULT_TIMEOUT = 50;
	private Integer timeout;
	
	public LandmarkElement(StepElement element, Map<String, ?> mappedElement) {
		super(mappedElement);
		setName(element.getName());
		setMatchExp(element.getMatchExp());
		this.timeout = extractTimeout(mappedElement);
	}

	@Override
	public Advice getLandMarkAdvice() {
		return new ContinueAdvice();
	}
	
	@Override
	public Integer getTimeout() {
		return timeout;
	}
	
	private Integer extractTimeout(Map<String, ?> mappedForkElement) {
		Object mappedTimeout = mappedForkElement.get(TIMEOUT_TAG);
		if(mappedTimeout != null) {
			return (Integer) mappedTimeout;
		}
		return DEFAULT_TIMEOUT;
	}
	
	public static String getTag() {
		return TAG;
	}
}
