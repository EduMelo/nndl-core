package dev.edumelo.com.nndl_core.action;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.Cookie;

import dev.edumelo.com.nndl_core.action.landmark.LandmarkConditionAction;
import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;
import lombok.ToString;

//XXX Retornar
//@Slf4j
@ToString
@SuppressWarnings("unchecked")
public class LoadCookies extends LandmarkConditionAction {
	private static final String TAG = "loadCookies";
	private static final String RETRIEVER_TAG = "retriever";
	private static final Object RETRIEVER_PARAMS_TAG = "retrieverParams";
	private Class<LoadCookiesRetriever> retrieverClass;
	private String[] retrieverParams;

	public LoadCookies(Map<String, ?> mappedAction, Map<String, StepElement> mappedElements) {
		Map<String, ?> mappedLoadCookies = (Map<String, ?>) mappedAction.get(TAG);	
		this.retrieverClass = getCookieRetrieverClass(mappedLoadCookies);
		this.retrieverParams = getRetrieverParams(mappedLoadCookies);
		setLandMarkConditionAgregation(mappedAction, mappedElements);
	}
	
	@Override
	public String getTag() {
		return TAG;
	}
	
	@Override
	public boolean isIgnoreRoot() {
		return true;
	}
	
	@Override
	public Advice runNested(SeleniumSndlWebDriver remoteWebDriver, SeleniumSndlWebDriverWaiter webDriverWait, IterationContent rootElement) {
		return runElement(remoteWebDriver, webDriverWait);
	}
	
	@Override
	public Advice runAction(SeleniumSndlWebDriver remoteWebDriver, SeleniumSndlWebDriverWaiter webDriverWait) {	
		return runElement(remoteWebDriver, webDriverWait);
	}
	
	public Advice runElement(SeleniumSndlWebDriver remoteWebDriver, SeleniumSndlWebDriverWaiter webDriverWait) {
		LoadCookiesRetriever retriever;
		try {
			retriever = retrieverClass.getConstructor().newInstance();
		} catch (ReflectiveOperationException | IllegalArgumentException | SecurityException e) {
			String message = "Error while instantiating Retriever class";
			//XXX Retornar
//			log.error(message);
			throw new RuntimeException(message, e);
		}
		
		Set<Cookie> cookies = retriever.getCookies((Object[]) retrieverParams);
		cookies.stream().forEach(remoteWebDriver.getWebDriver().manage()::addCookie);
		remoteWebDriver.getWebDriver().navigate().refresh();
		
		setActionPerformed(true);
		return new ContinueAdvice();
	}

	private String[] getRetrieverParams(Map<String, ?> mappedLoadCookies) {
		return ((List<String>) mappedLoadCookies.get(RETRIEVER_PARAMS_TAG)).toArray(new String[0]);
	}

	private Class<LoadCookiesRetriever> getCookieRetrieverClass(Map<String, ?> mappedLoadCookies) {		
		String className = (String) mappedLoadCookies.get(RETRIEVER_TAG);
		
		try {
			return (Class<LoadCookiesRetriever>) Class.forName(className);
		} catch (ClassNotFoundException e) {
			String message = String.format("Cannot found class by the name: %s", className);
			//XXX Retornar
//			log.error(message);
			throw new RuntimeException(message);
		}
	}

	@Override
	public void runPreviousModification(ActionModificator modificiator) {
		// TODO Auto-generated method stub
		
	}

}
