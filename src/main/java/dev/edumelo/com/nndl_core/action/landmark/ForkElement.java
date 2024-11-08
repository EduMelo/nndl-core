package dev.edumelo.com.nndl_core.action.landmark;

import java.util.Map;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import dev.edumelo.com.nndl_core.exceptions.NndlParserRuntimeException;
import dev.edumelo.com.nndl_core.nndl.NndlNode;
import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.SwapStepAdvice;
import dev.edumelo.com.nndl_core.webdriver.NndlWebDriver;

public class ForkElement implements Landmark {
	public static final String TAG = "element";
	public static final String FORK_TAG = "fork";
	private static final String TIMEOUT_TAG = "timeout";
	private static final Integer DEFAULT_TIMEOUT = 50;
	private StepElement element;
	private String fork;
	private Integer timeout;
	
	public ForkElement(Map<String, StepElement> mappedElements, NndlNode mappedForkElement) {
		this.element = extractElement(mappedElements, mappedForkElement);
		this.fork = extractFork(mappedForkElement);
		this.timeout = extractTimeout(mappedForkElement);
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
	
	@Override
	public ExpectedCondition<WebElement> visibilityOfElementLocated(NndlWebDriver remoteWebDriver){
		return this.element.visibilityOfElementLocated(remoteWebDriver);
	}

	private Integer extractTimeout(NndlNode mappedForkElement) {
		return mappedForkElement.getScalarValueFromChild(TIMEOUT_TAG, Integer.class).orElse(DEFAULT_TIMEOUT);
	}

	private String extractFork(NndlNode mappedForkElement) {
		return mappedForkElement.getScalarValueFromChild(FORK_TAG).orElse(null);
	}

	private StepElement extractElement(Map<String, StepElement> mappedElements, NndlNode mappedForkElement) {
		String elementKey = mappedForkElement.getScalarValueFromChild(TAG)
				.orElseThrow(() -> new NndlParserRuntimeException("Fork tag should have "+TAG+" tag", mappedForkElement));
		return (StepElement) mappedElements.get(elementKey);
	}

	@Override
	public String toString() {
		return "ForkElement [element=" + element + ", fork=" + fork + ", timeout=" + timeout + "]";
	}
}
