package dev.edumelo.com.nndl_core.action.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.Cookie;

import dev.edumelo.com.nndl_core.action.ActionModificator;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkConditionAction;
import dev.edumelo.com.nndl_core.contextAdapter.ContextAdapterHandler;
import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumHubProperties;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;

@SuppressWarnings("unchecked")
public class StoreCookies extends LandmarkConditionAction {
	
	private static final String TAG = "storeCookies";
	private static final String STORER_PARAMS_TAG = "storerParams";
	private static final String USER_HANDLE_TAG = "userHandle";
	private String[] storerParams;
	private String userHandle;
	
	public StoreCookies(SeleniumHubProperties seleniumHubProperties, Map<String, ?> mappedAction,
			Map<String, StepElement> mappedElements) {
		super(seleniumHubProperties, mappedAction, mappedElements);
		Map<String, ?> mappedStoreCookies = (Map<String, ?>) mappedAction.get(TAG);
		
		this.storerParams = getStorerParams(mappedStoreCookies);
		this.userHandle = getUserHandle(mappedStoreCookies);
		setLandMarkConditionAgregation((Map<String, ?>) mappedAction.get(TAG), mappedElements);
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
	public Advice runNested(String sessionId, SeleniumSndlWebDriver remoteWebDriver,
			SeleniumSndlWebDriverWaiter webDriverWait, IterationContent rootElement) {
		return runElement(sessionId, remoteWebDriver);
	}
	
	@Override
	public Advice runAction(String sessionId, SeleniumSndlWebDriver remoteWebDriver,
			SeleniumSndlWebDriverWaiter webDriverWait) {
		return runElement(sessionId, remoteWebDriver);
	}
	
	public Advice runElement(String sessionId, SeleniumSndlWebDriver remoteWebDriver) {
		Set<Cookie> cookies = remoteWebDriver.getWebDriver().manage().getCookies();
		ContextAdapterHandler.storeCookies(sessionId, (Object[]) storerParams, cookies);
		
		setActionPerformed(true);
		return new ContinueAdvice();
	}
	
	private String getUserHandle(Map<String, ?> mappedStoreCookies) {
		return (String) mappedStoreCookies.get(USER_HANDLE_TAG);
	}

	private String[] getStorerParams(Map<String, ?> mappedLoadCookies) {
		return ((List<String>) mappedLoadCookies.get(STORER_PARAMS_TAG)).toArray(new String[0]);
	}

	@Override
	public void runPreviousModification(ActionModificator modificiator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString() {
		return "StoreCookies [storerParams=" + Arrays.toString(storerParams)
				+ ", userHandle=" + userHandle + "]";
	}
}
