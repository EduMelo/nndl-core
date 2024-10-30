package dev.edumelo.com.nndl_core.action.impl;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import org.openqa.selenium.WebDriverException;

import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;
import dev.edumelo.com.nndl_core.action.ActionModificator;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkConditionAction;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumHubProperties;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;

public class Goto extends LandmarkConditionAction {
	private static final String TAG = "gotoUrl";
	private URL url;
	
	public Goto(SeleniumHubProperties seleniumHubProperties, Map<String, ?> mappedAction,
			Map<String, StepElement> mappedElements) {
		super(seleniumHubProperties, mappedAction, mappedElements);
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
	public Advice runNested(SeleniumSndlWebDriver remoteWebDriver, SeleniumSndlWebDriverWaiter webDriverWait,
			IterationContent rootElement) {	
		remoteWebDriver.getWebDriver().get(this.url.toExternalForm());
		return new ContinueAdvice();
	}
	
	@Override
	public Advice runAction(SeleniumSndlWebDriver remoteWebDriver, SeleniumSndlWebDriverWaiter webDriverWait) {
		//try-catch added to avoid the error described in https://github.com/SeleniumHQ/selenium/issues/12277
		try {
			remoteWebDriver.getWebDriver().get(this.url.toExternalForm());			
		} catch (WebDriverException ex) {
			if(!remoteWebDriver.getWebDriver().getCurrentUrl()
					.startsWith(this.url.toExternalForm())) {
				throw new RuntimeException("Could not access the url "+this.url.toExternalForm());
			}
		}
		
		setActionPerformed(false);
		return new ContinueAdvice();
	}

	private URL getUrl(Map<String, ?> mappedAction) {
		String urlString = (String) mappedAction.get(TAG);
		try {
			return new URI(urlString).toURL();
		} catch (MalformedURLException | URISyntaxException e) {
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
