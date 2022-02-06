package dev.edumelo.com.nndl_core.scroll;

import java.util.Collection;

import dev.edumelo.com.nndl_core.ExtractDataBind;
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
    private Collection<ExtractDataBind> extractDataBindCollection;
    private IterationContent rootElement;
    private StepElement element;
	
    public InfiniteScrollAdapter(SeleniumSndlWebDriver webDriver, SeleniumSndlWebDriverWaiter webDriverWait,
			Step iterationStep, StepRunner runner, InfiniteScrollCondition infiniteScrollCondition,
			Collection<ExtractDataBind> extractDataBindCollection, IterationContent rootElement, StepElement element) {
		this.webDriver = webDriver;
		this.webDriverWait = webDriverWait;
		this.iterationStep = iterationStep;
		this.runner = runner;
		this.infiniteScrollCondition = infiniteScrollCondition;
		this.extractDataBindCollection = extractDataBindCollection;
		this.rootElement = rootElement;
		this.element = element;
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
	public Collection<ExtractDataBind> getExtractDataBindCollection() {
		return extractDataBindCollection;
	}
	public IterationContent getRootElement() {
		return rootElement;
	}
	public StepElement getElement() {
		return element;
	}
	
}
