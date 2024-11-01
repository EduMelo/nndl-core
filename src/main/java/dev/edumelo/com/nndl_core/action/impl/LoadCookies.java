package dev.edumelo.com.nndl_core.action.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.Cookie;

import dev.edumelo.com.nndl_core.action.ActionModificator;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkConditionAction;
import dev.edumelo.com.nndl_core.contextAdapter.ThreadLocalManager;
import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumHubProperties;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;

@SuppressWarnings("unchecked")
public class LoadCookies extends LandmarkConditionAction {
	
	private static final String TAG = "loadCookies";
	private static final Object RETRIEVER_PARAMS_TAG = "retrieverParams";
	private String[] retrieverParams;

	public LoadCookies(SeleniumHubProperties seleniumHubProperties, Map<String, ?> mappedAction,
			Map<String, StepElement> mappedElements) {
		super(seleniumHubProperties, mappedAction, mappedElements);
		Map<String, ?> mappedLoadCookies = (Map<String, ?>) mappedAction.get(TAG);	
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
	public Advice runNested(SeleniumSndlWebDriver remoteWebDriver, SeleniumSndlWebDriverWaiter webDriverWait,
			IterationContent rootElement) {
		return runElement(remoteWebDriver, webDriverWait);
	}
	
	@Override
	public Advice runAction(SeleniumSndlWebDriver remoteWebDriver, SeleniumSndlWebDriverWaiter webDriverWait) {	
		return runElement(remoteWebDriver, webDriverWait);
	}
	
	public Advice runElement(SeleniumSndlWebDriver remoteWebDriver, SeleniumSndlWebDriverWaiter webDriverWait) {
		Set<Cookie> cookies = ThreadLocalManager.retrieveCookies((Object[]) retrieverParams);
		cookies.stream().forEach(remoteWebDriver.getWebDriver().manage()::addCookie);
		remoteWebDriver.getWebDriver().navigate().refresh();
		
		setActionPerformed(true);
		return new ContinueAdvice();
	}

	private String[] getRetrieverParams(Map<String, ?> mappedLoadCookies) {
		return ((List<String>) mappedLoadCookies.get(RETRIEVER_PARAMS_TAG)).toArray(new String[0]);
	}

	@Override
	public void runPreviousModification(ActionModificator modificiator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString() {
		return "LoadCookies [retrieverParams="
				+ Arrays.toString(retrieverParams) + "]";
	}
}
