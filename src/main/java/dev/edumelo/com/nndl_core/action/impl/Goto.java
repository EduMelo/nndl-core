package dev.edumelo.com.nndl_core.action.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;
import dev.edumelo.com.nndl_core.action.ActionModificator;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkConditionAction;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;

public class Goto extends LandmarkConditionAction {
	private static final String TAG = "gotoUrl";
	private URL url;
	
	public Goto(Map<String, ?> mappedAction, Map<String, StepElement> mappedElements) {
		super(mappedAction, mappedElements);
		this.url = getUrl(mappedAction);
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
	public Advice runNested(String sessionId, SeleniumSndlWebDriver remoteWebDriver,
			SeleniumSndlWebDriverWaiter webDriverWait, IterationContent rootElement) {	
		remoteWebDriver.getWebDriver().get(this.url.toExternalForm());
		return new ContinueAdvice();
	}
	
	@Override
	public Advice runAction(String sessionId, SeleniumSndlWebDriver remoteWebDriver,
			SeleniumSndlWebDriverWaiter webDriverWait) {	
		remoteWebDriver.getWebDriver().get(this.url.toExternalForm());
		
		setActionPerformed(true);
		return new ContinueAdvice();
	}

	private URL getUrl(Map<String, ?> mappedAction) {
		String urlString = (String) mappedAction.get(TAG);
		try {
			return new URL(urlString);
		} catch (MalformedURLException e) {
			throw new RuntimeException(String.format("Cannot create URL from the string: %s ", urlString));
		}
	}

	@Override
	public void runPreviousModification(ActionModificator modificiator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString() {
		return "Goto [url=" + url + "]";
	}
}
