package dev.edumelo.com.nndl_core.action.impl;

import java.net.URL;
import java.util.List;
import java.util.Map;


import dev.edumelo.com.nndl_core.action.Action;
import dev.edumelo.com.nndl_core.action.ActionModificator;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkAchievementStrategy;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkStrategies;
import dev.edumelo.com.nndl_core.exceptions.checked.NndlActionException;
import dev.edumelo.com.nndl_core.exceptions.unchecked.NndlParserRuntimeException;
import dev.edumelo.com.nndl_core.nndl.NndlNode;
import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;
import dev.edumelo.com.nndl_core.utils.UrlUtils;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumHubProperties;
import dev.edumelo.com.nndl_core.webdriver.NndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.NndlWebDriverWaiter;

public class DownloadFile extends Action {
	private static final String TAG = "downloadFile";
	private static final String URL_TAG = "url";
	private URL url;
	private NndlNode relevantNode;
	
	public DownloadFile(SeleniumHubProperties seleniumHubProperties, NndlNode mappedAction, Map<String, StepElement> mappedElements) {
		super(seleniumHubProperties, mappedAction, mappedElements);
		this.url = getUrl(mappedAction);
		this.relevantNode = mappedAction;
	}

	private URL getUrl(NndlNode mappedAction) {
		return mappedAction
				.getScalarValueFromChild(URL_TAG)
				.flatMap(UrlUtils::createUrl)
				.orElseThrow(NndlParserRuntimeException
				.get("DownloadFile Action should have "+URL_TAG+" tag.", mappedAction));
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
	public void runPreviousModification(ActionModificator modificiator) {
		return;
	}

	@Override
	public Advice runNested(NndlWebDriver webDriver, NndlWebDriverWaiter webDriverWait,
			IterationContent rootElement) throws NndlActionException {
		return runElment(webDriver, webDriverWait);
	}

	@Override
	public Advice runAction(NndlWebDriver webDriver, NndlWebDriverWaiter webDriverWait)
			throws NndlActionException {
		return runElment(webDriver, webDriverWait);
	}
	
	@Override
	public LandmarkStrategies getDefaultWaitCondition() {
		return new LandmarkStrategies(LandmarkAchievementStrategy.NONE);
	}
	
	@Override
	public StepElement getRelevantElment() {
		return null;
	}

	private Advice runElment(NndlWebDriver webDriver,
			NndlWebDriverWaiter webDriverWait) {
		webDriver.executeScript("window.open()");
		List<String> tabs = webDriver.getWindowHandles();
		webDriver.switchToWindow(tabs.get(tabs.size()-1));
		webDriver.get("chrome://downloads/");
		
		String code = "var a = document.createElement('a');"+"\n"+
				String.format("a.href = '%s';", url)+"\n"+
				"a.download = '';"+"\n"+
				"document.body.appendChild(a);"+"\n"+
				"a.click();";
		
		webDriver.executeScript(code);
		
		String waitCode = "var items = document.querySelector('downloads-manager')"+"\n"+
				".shadowRoot.getElementById('downloadsList').items;"+"\n"+
				"if (items.every(e => e.state === 'COMPLETE'))"+"\n"+
				"{return items.map(e => e.fileUrl || e.file_url);}";
		
		webDriverWait.untilScriptReturns(getTimeoutSeconds(), waitCode);
		
		webDriver.closeWindow();
		webDriver.switchToWindow(tabs.get(0));
		
		setActionPerformed(true);
		return new ContinueAdvice();
	}
	
	@Override
	public String toString() {
		return org.apache.commons.lang.builder.ToStringBuilder.reflectionToString(this,
				org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
