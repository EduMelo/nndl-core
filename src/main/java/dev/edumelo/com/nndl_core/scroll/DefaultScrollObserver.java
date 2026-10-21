package dev.edumelo.com.nndl_core.scroll;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static dev.edumelo.com.nndl_core.action.landmark.LandmarkAchievementStrategy.*;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkStrategies;
import dev.edumelo.com.nndl_core.step.Step;
import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.StepRunner;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.NndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.NndlWebDriverWaiter;
import dev.edumelo.com.nndl_core.exceptions.unchecked.NndlStaleElementException;
import dev.edumelo.com.nndl_core.exceptions.unchecked.NndlTimeoutException;

public class DefaultScrollObserver implements ScrollObserver {
	
	private static final Logger log = LoggerFactory.getLogger(DefaultScrollObserver.class);
	
	private final NndlWebDriver webDriver;
    private final NndlWebDriverWaiter webDriverWait;
    private Step iterationStep;
    private StepRunner runner;
    private InfiniteScrollCondition infiniteScrollCondition;
    private IterationContent rootElement;
    private StepElement element;
    private String iterationScope;
    private String sessionId;
    private Boolean throwTimeout;
    private Integer elementTimeoutWait;
     
    public DefaultScrollObserver(InfiniteScrollAdapter adapter) {
        this.webDriver = adapter.getWebDriver();
        this.webDriverWait = adapter.getWebDriverWait();
        this.iterationStep = adapter.getIterationStep();
        this.infiniteScrollCondition = adapter.getInfiniteScrollCondition();
        this.rootElement = adapter.getRootElement();
        this.element = adapter.getElement();
        this.runner = adapter.getRunner();
        this.throwTimeout = adapter.isThrowTimeout();
        this.elementTimeoutWait = adapter.getElementTimeoutWait();
    }
    
    public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public Step getIterationStep() {
		return iterationStep;
	}
	public void setIterationStep(Step iterationStep) {
		this.iterationStep = iterationStep;
	}
	public StepRunner getRunner() {
		return runner;
	}
	public void setRunner(StepRunner runner) {
		this.runner = runner;
	}
	public InfiniteScrollCondition getInfiniteScrollCondition() {
		return infiniteScrollCondition;
	}
	public void setInfiniteScrollCondition(InfiniteScrollCondition infiniteScrollCondition) {
		this.infiniteScrollCondition = infiniteScrollCondition;
	}
	public IterationContent getRootElement() {
		return rootElement;
	}
	public void setRootElement(IterationContent rootElement) {
		this.rootElement = rootElement;
	}
	public StepElement getElement() {
		return element;
	}
	public void setElement(StepElement element) {
		this.element = element;
	}
	public String getIterationScope() {
		return iterationScope;
	}
	public void setIterationScope(String iterationScope) {
		this.iterationScope = iterationScope;
	}
	public NndlWebDriver getWebDriver() {
		return webDriver;
	}
	public NndlWebDriverWaiter getWebDriverWait() {
		return webDriverWait;
	}

	public int execute(NndlWebDriver webDriver, int loopCount)
			throws ScrollContinueException, ScrollStopException {
    	try {
    		if(rootElement != null) {
    			return webDriverWait.untilElements(Duration.ofSeconds(elementTimeoutWait),
    					element.presenceOfNestedElementsLocatedBy(webDriver,
    							rootElement.getRootElement())).stream()
    					.filter(e -> infiniteScrollCondition.checkCondition(e))
    					.map(e -> new IterationContent(e, loopCount))
    					.map(e -> runner.runStep(iterationStep, e))
    					.reduce(0, Integer::sum);
    		} else {
    			return webDriverWait.untilElements(Duration.ofSeconds(elementTimeoutWait),
    					element.allLandmarkAchiveable(webDriver, new LandmarkStrategies(IS_PRESENT))).stream()
    					.filter(e -> infiniteScrollCondition.checkCondition(e))
    					.map(e -> new IterationContent(e, loopCount))
    					.map(e -> runner.runStep(iterationStep, e))
    					.reduce(0, Integer::sum);
    		}  				    		
    	} catch (NndlStaleElementException e) {
    		String msg = "DefaultInfiniteScrollObserver execution error";
    		log.error(msg);
    	} catch (NndlTimeoutException e) {
    		log.error("Timeout to locate element: "+element);
    		if(throwTimeout) {
    			throw e;
    		}
    	}
		return 0;
	}

}
