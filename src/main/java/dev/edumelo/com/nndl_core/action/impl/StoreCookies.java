package dev.edumelo.com.nndl_core.action.impl;

import static dev.edumelo.com.nndl_core.action.ElementWaitCondition.NONE;

import java.util.List;
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

public class StoreCookies extends LandmarkConditionAction {
	
	private static final String TAG = "storeCookies";
	private static final String STORER_PARAMS_TAG = "storerParams";
	private String[] storerParams;
	private NndlNode relevantNode;
	
	public StoreCookies(SeleniumHubProperties seleniumHubProperties, NndlNode mappedAction,
			Map<String, StepElement> mappedElements) {
		super(seleniumHubProperties, mappedAction, mappedElements);
		NndlNode mappedStoreCookies = mappedAction.getValueFromChild(TAG)
				.orElseThrow(() -> new NndlParserRuntimeException("StoreCookies tag should have "+TAG+" tag.", mappedAction));
		
		this.storerParams = getStorerParams(mappedStoreCookies);
		this.relevantNode = mappedAction;
		setLandMarkConditionAgregation(mappedStoreCookies, mappedElements);
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
		return runElement(remoteWebDriver);
	}
	
	@Override
	public Advice runAction(SeleniumSndlWebDriver remoteWebDriver, SeleniumSndlWebDriverWaiter webDriverWait) {
		return runElement(remoteWebDriver);
	}
	
	public Advice runElement(SeleniumSndlWebDriver remoteWebDriver) {
		Set<Cookie> cookies = remoteWebDriver.getWebDriver().manage().getCookies();
		ThreadLocalManager.storeCookies((Object[]) storerParams, cookies);
		
		setActionPerformed(true);
		return new ContinueAdvice();
	}

	private String[] getStorerParams(NndlNode mappedLoadCookies) {
		List<NndlNode> storerParamsList = mappedLoadCookies
			.getListedValuesFromChild(STORER_PARAMS_TAG)
			.orElseThrow(NndlParserRuntimeException.get("Action StoreCookies should have "+STORER_PARAMS_TAG+" tag.",
					mappedLoadCookies));
		return storerParamsList.stream()
			.map(n -> n.getScalarValue())
			.flatMap(Optional::stream)
			.toArray(String[]::new);
	}

	@Override
	public void runPreviousModification(ActionModificator modificiator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString() {
		return org.apache.commons.lang.builder.ToStringBuilder.reflectionToString(this,
				org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
