package dev.edumelo.com.nndl_core.action.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.openqa.selenium.remote.RemoteWebDriver;

import dev.edumelo.com.nndl_core.action.Action;
import dev.edumelo.com.nndl_core.action.ActionException;
import dev.edumelo.com.nndl_core.action.ActionModificator;
import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;

public class DownloadFile extends Action {
	private static final String TAG = "downloadFile";
	private static final String URL_TAG = "url";
	private static final String FILENAME_TAG = "fileName";
	private URL url;
	private String fileName;
	
	public DownloadFile(Map<String, ?> mappedAction, Map<String, StepElement> mappedElements) {
		super(mappedAction);
		this.url = getUrl(mappedAction);
		this.fileName = getFileName(mappedAction);
	}

	public DownloadFile(Map<String, ?> mappedAction) {
		super(mappedAction);
		this.url = getUrl(mappedAction);
	}

	private URL getUrl(Map<String, ?> mappedAction) {
		String urlString = (String) mappedAction.get(URL_TAG);
		try {
			return new URL(urlString);
		} catch (MalformedURLException e) {
			throw new RuntimeException(String.format("Cannot create URL from the string: %s ",
					urlString));
		}
	}
	
	private String getFileName(Map<String, ?> mappedAction) {
		return (String) mappedAction.get(FILENAME_TAG);
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
	public void runPreviousModification(ActionModificator modificiator) {
		// TODO Auto-generated method stub

	}

	@Override
	public Advice runNested(String sessionId, SeleniumSndlWebDriver webDriver,
			SeleniumSndlWebDriverWaiter webDriverWait, IterationContent rootElement)
					throws ActionException {
		RemoteWebDriver driver = webDriver.getWebDriver();
		
		String code = "var a = document.createElement('a');"+"\n"+
				String.format("a.href = '%s';", url)+"\n"+
				String.format("a.download = '%s';", fileName)+"\n"+
				"document.body.appendChild(a);"+"\n"+
				"a.click();";
		
		driver.executeScript(code);
		setActionPerformed(true);
		return new ContinueAdvice();
	}

	@Override
	public Advice runAction(String sessionId, SeleniumSndlWebDriver webDriver,
			SeleniumSndlWebDriverWaiter webDriverWait) throws ActionException {
		RemoteWebDriver driver = webDriver.getWebDriver();
		
		String code = "var a = document.createElement('a');"+"\n"+
				String.format("a.href = '%s';", url)+"\n"+
				"a.download = '';"+"\n"+
				"document.body.appendChild(a);"+"\n"+
				"a.click();";
		
		driver.executeScript(code);
		setActionPerformed(true);
		return new ContinueAdvice();
	}

}
