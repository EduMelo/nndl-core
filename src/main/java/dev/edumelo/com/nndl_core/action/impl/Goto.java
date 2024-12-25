package dev.edumelo.com.nndl_core.action.impl;

import java.net.URL;
import java.util.Map;

import org.openqa.selenium.WebDriverException;

import dev.edumelo.com.nndl_core.action.ActionModificator;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkAchievementStrategy;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkConditionAction;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkStrategies;
import dev.edumelo.com.nndl_core.exceptions.unchecked.NndlParserRuntimeException;
import dev.edumelo.com.nndl_core.nndl.NndlNode;
import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;
import dev.edumelo.com.nndl_core.utils.UrlUtils;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumHubProperties;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;

public class Goto extends LandmarkConditionAction {
	private static final String TAG = "gotoUrl";
	private URL url;
	private NndlNode relevantNode;
	
	public Goto(SeleniumHubProperties seleniumHubProperties, NndlNode mappedAction, Map<String, StepElement> mappedElements) {
		super(seleniumHubProperties, mappedAction, mappedElements);
		this.url = getUrl(mappedAction);
		setLandMarkConditionAgregation(mappedAction, mappedElements);
		this.relevantNode = mappedAction;
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
	public NndlNode getRelevantNode() {
		return this.relevantNode;
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
	
	@Override
	public LandmarkStrategies getDefaultWaitCondition() {
		return new LandmarkStrategies(LandmarkAchievementStrategy.NONE);
	}
	
	@Override
	public StepElement getRelevantElment() {
		return null;
	}

	@Override
	public void runPreviousModification(ActionModificator modificiator) {
		// TODO Auto-generated method stub
		
	}

	private URL getUrl(NndlNode mappedAction) {
		return mappedAction.getScalarValueFromChild(TAG)
				.flatMap(UrlUtils::createUrl)
				.orElseThrow(NndlParserRuntimeException.get("Goto action should have "+TAG+" tag.", mappedAction));
	}
	
	@Override
	public String toString() {
		return org.apache.commons.lang.builder.ToStringBuilder.reflectionToString(this,
				org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
