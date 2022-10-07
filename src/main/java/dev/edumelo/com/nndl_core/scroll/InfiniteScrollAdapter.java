package dev.edumelo.com.nndl_core.scroll;

import dev.edumelo.com.nndl_core.step.Step;
import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.StepRunner;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;

public class InfiniteScrollAdapter {
	private SeleniumSndlWebDriver webDriver;
    private SeleniumSndlWebDriverWaiter webDriverWait;
    private Step iterationStep;
    private StepRunner runner;
    private InfiniteScrollCondition infiniteScrollCondition;
    private IterationContent rootElement;
    private StepElement element;
    private String sessionId;
    private Boolean throwTimeout = true;
    private Integer elementTimeoutWait = 50;
	
    public InfiniteScrollAdapter(String sessionId, SeleniumSndlWebDriver webDriver,
    		SeleniumSndlWebDriverWaiter webDriverWait, Step iterationStep, StepRunner runner,
    		InfiniteScrollCondition infiniteScrollCondition, IterationContent rootElement,
    		StepElement element, Boolean throwTimeout, Integer elementTimeoutWait) {
    	this.sessionId = sessionId;
		this.webDriver = webDriver;
		this.webDriverWait = webDriverWait;
		this.iterationStep = iterationStep;
		this.runner = runner;
		this.infiniteScrollCondition = infiniteScrollCondition;
		this.rootElement = rootElement;
		this.element = element;
		if(throwTimeout != null) {
			this.throwTimeout = throwTimeout;			
		}
		if(elementTimeoutWait != null) {
			this.elementTimeoutWait = elementTimeoutWait;			
		}
	}
    
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public SeleniumSndlWebDriver getWebDriver() {
		return webDriver;
	}
	public SeleniumSndlWebDriverWaiter getWebDriverWait() {
		return webDriverWait;
	}
	public Step getIterationStep() {
		return iterationStep;
	}
	public StepRunner getRunner() {
		return runner;
	}
	public InfiniteScrollCondition getInfiniteScrollCondition() {
		return infiniteScrollCondition;
	}
	public IterationContent getRootElement() {
		return rootElement;
	}
	public StepElement getElement() {
		return element;
	}
	public Boolean isThrowTimeout() {
		return throwTimeout;
	}
	public Integer getElementTimeoutWait() {
		return elementTimeoutWait;
	}
	
	
}
