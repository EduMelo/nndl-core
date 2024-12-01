package dev.edumelo.com.nndl_core.action.impl;

import static dev.edumelo.com.nndl_core.action.ElementWaitCondition.NONE;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.openqa.selenium.Cookie;

import dev.edumelo.com.nndl_core.action.ActionModificator;
import dev.edumelo.com.nndl_core.action.ElementWaitCondition;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkConditionAction;
import dev.edumelo.com.nndl_core.contextAdapter.ThreadLocalManager;
import dev.edumelo.com.nndl_core.exceptions.unchecked.NndlParserRuntimeException;
import dev.edumelo.com.nndl_core.nndl.NndlNode;
import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumHubProperties;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;

public class LoadCookies extends LandmarkConditionAction {
	
	private static final String TAG = "loadCookies";
	private static final String RETRIEVER_PARAMS_TAG = "retrieverParams";
	private String[] retrieverParams;
	private NndlNode relevantNode;

	public LoadCookies(SeleniumHubProperties seleniumHubProperties, NndlNode mappedAction,
			Map<String, StepElement> mappedElements) {
		super(seleniumHubProperties, mappedAction, mappedElements);
		NndlNode mappedLoadCookies = mappedAction.getValueFromChild(TAG).orElseThrow(() -> new NndlParserRuntimeException(
				"LoadCookies action should have "+TAG+" tag.", mappedAction));
		this.retrieverParams = getRetrieverParams(mappedLoadCookies);
		this.relevantNode = mappedAction;
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
	public NndlNode getRelevantNode() {
		return this.relevantNode;
	}
	
	@Override
	public ElementWaitCondition getDefaultWaitCondition() {
		return NONE;
	}
	
	@Override
	public StepElement getRelevantElment() {
		return null;
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

	private String[] getRetrieverParams(NndlNode mappedLoadCookies) {
		return mappedLoadCookies
				.getListedValuesFromChild(RETRIEVER_PARAMS_TAG)
				.get()
				.stream()
				.map(n -> n.getScalarValue())
				.map(Optional::get)
				.toArray(String[]::new);
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
