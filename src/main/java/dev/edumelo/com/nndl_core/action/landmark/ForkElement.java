package dev.edumelo.com.nndl_core.action.landmark;

import java.util.Map;

import org.openqa.selenium.By;

import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.SwapStepAdvice;
import dev.edumelo.com.nndl_core.webdriver.NndlWebDriver;
import lombok.ToString;

@ToString
public class ForkElement implements Landmark {
	public static final String TAG = "element";
	public static final String FORK_TAG = "fork";
	private static final String TIMEOUT_TAG = "timeout";
	private static final Integer DEFAULT_TIMEOUT = 50;
	private StepElement element;
	private String fork;
	private Integer timeout;
	
	public ForkElement(Map<String, StepElement> mappedElements, Map<String, ?> mappedForkElement) {
		this.element = extractElement(mappedElements, mappedForkElement);
		this.fork = extractFork(mappedForkElement);
		this.timeout = extractTimeout(mappedForkElement);
	}
	
	@Override
	public By getLocator(NndlWebDriver remoteWebDriver) {
		return this.element.getLocator(remoteWebDriver);
	}

	@Override
	public Advice getLandMarkAdvice() {
		SwapStepAdvice advice = new SwapStepAdvice();
		advice.setAdvisedStep(fork);
		
		return advice;
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

	private String extractFork(Map<String, ?> mappedForkElement) {
		return (String) mappedForkElement.get(FORK_TAG);
	}

	private StepElement extractElement(Map<String, StepElement> mappedElements, Map<String, ?> mappedForkElement) {
		String elementKey = (String) mappedForkElement.get(TAG);
		return (StepElement) mappedElements.get(elementKey);
	}
}
