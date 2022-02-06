package dev.edumelo.com.nndl_core.action;

import java.util.Arrays;
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

@SuppressWarnings("unchecked")
public class StoreCookies extends LandmarkConditionAction {
	private static final String TAG = "storeCookies";
	private static final String STORER_TAG = "storer";
	private static final String STORER_PARAMS_TAG = "storerParams";
	private static final String USER_HANDLE_TAG = "userHandle";
	private Class<CookiesStorer> storerClass;
	private String[] storerParams;
	private String userHandle;
	
	public StoreCookies(Map<String, ?> mappedAction, Map<String, StepElement> mappedElements) {
		Map<String, ?> mappedStoreCookies = (Map<String, ?>) mappedAction.get(TAG);
		
		this.storerClass = getCookieStorerClass(mappedStoreCookies);
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
	public Advice runNested(SeleniumSndlWebDriver remoteWebDriver, SeleniumSndlWebDriverWaiter webDriverWait, IterationContent rootElement) {
		return runElement(remoteWebDriver);
	}
	
	@Override
	public Advice runAction(SeleniumSndlWebDriver remoteWebDriver, SeleniumSndlWebDriverWaiter webDriverWait) {
		return runElement(remoteWebDriver);
	}
	
	public Advice runElement(SeleniumSndlWebDriver remoteWebDriver) {
		CookiesStorer storer;
		try {
			storer = storerClass.getConstructor().newInstance();
		} catch (ReflectiveOperationException | IllegalArgumentException | SecurityException e) {
			String message = "Error while instantiating Storer class";
			//XXX Retornar
//			log.error(message);
			throw new RuntimeException(message, e);
		}
		
		Set<Cookie> cookies = remoteWebDriver.getWebDriver().manage().getCookies();
		storer.storeCookies((Object[]) storerParams, cookies);
		
		setActionPerformed(true);
		return new ContinueAdvice();
	}
	
	private String getUserHandle(Map<String, ?> mappedStoreCookies) {
		return (String) mappedStoreCookies.get(USER_HANDLE_TAG);
	}

	private String[] getStorerParams(Map<String, ?> mappedLoadCookies) {
		return ((List<String>) mappedLoadCookies.get(STORER_PARAMS_TAG)).toArray(new String[0]);
	}
	
	private Class<CookiesStorer> getCookieStorerClass(Map<String, ?> mappedLoadCookies) {		
		String className = (String) mappedLoadCookies.get(STORER_TAG);
		
		try {
			return (Class<CookiesStorer>) Class.forName(className);
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

	@Override
	public String toString() {
		return "StoreCookies [storerClass=" + storerClass + ", storerParams=" + Arrays.toString(storerParams)
				+ ", userHandle=" + userHandle + "]";
	}
}
