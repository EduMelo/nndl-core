package dev.edumelo.com.nndl_core.action.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import dev.edumelo.com.nndl_core.action.Action;
import dev.edumelo.com.nndl_core.action.ActionException;
import dev.edumelo.com.nndl_core.action.ActionModificator;
import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumHubProperties;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;

public class UploadFile extends Action {
	private static final String TAG = "uploadFile";
	private static final String INPUT_TAG = "input";
	private static final String URL_TAG = "url";
	private StepElement inputElement;
	private URL url;

	public UploadFile(SeleniumHubProperties seleniumHubProperties, Map<String, ?> mappedAction,
			Map<String, StepElement> mappedElements) {
		super(seleniumHubProperties, mappedAction, mappedElements);
		this.inputElement = getElement(mappedAction, mappedElements);
		this.url = getUrl(mappedAction);
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
		return;
	}

	@Override
	public Advice runNested(String sessionId, SeleniumSndlWebDriver webDriver,
			SeleniumSndlWebDriverWaiter webDriverWait, IterationContent rootElement)
					throws ActionException {
		return runElement(webDriver, webDriverWait);
	}

	@Override
	public Advice runAction(String sessionId, SeleniumSndlWebDriver webDriver,
			SeleniumSndlWebDriverWaiter webDriverWait) throws ActionException {
		return runElement(webDriver, webDriverWait);
	}
	
	private Advice runElement(SeleniumSndlWebDriver webDriver,
			SeleniumSndlWebDriverWaiter webDriverWait) {
		RemoteWebDriver driver = webDriver.getWebDriver();
		
		driver.executeScript("window.open()");
		List<String> tabs = new ArrayList<>(driver.getWindowHandles());
		driver.switchTo().window(tabs.get(tabs.size()-1));
		driver.get("chrome://downloads/");
		
		String code = "var a = document.createElement('a');"+"\n"+
				String.format("a.href = '%s';", url)+"\n"+
				"a.download = '';"+"\n"+
				"document.body.appendChild(a);"+"\n"+
				"a.click();";
		
		driver.executeScript(code);
		
		String waitCode = "var items = document.querySelector('downloads-manager')"+"\n"+
				".shadowRoot.getElementById('downloadsList').items;"+"\n"+
				"if (items.every(e => e.state === 'COMPLETE'))"+"\n"+
				"{return items.map(e => e.fileUrl || e.file_url);}";
		
		webDriverWait.getWebDriverWaiter().withTimeout(getTimeoutSeconds())
		.until(ExpectedConditions.jsReturnsValue(waitCode));
		
		driver.close();
		driver.switchTo().window(tabs.get(0));
		
		WebElement input =  webDriverWait.getWebDriverWaiter().withTimeout(getTimeoutSeconds())
				.until(ExpectedConditions.presenceOfElementLocated(
				inputElement.getLocator(webDriver)));
		
		String[] fileNameArray = url.toString().split("/");
		String fileName = fileNameArray[fileNameArray.length-1];
		String fileLocation = String.format("%s%s", SeleniumHubProperties.getDownloadDirectory(),
				fileName);
		input.sendKeys(fileLocation);
		
		setActionPerformed(true);
		return new ContinueAdvice();
	}

	private StepElement getElement(Map<String, ?> mappedAction,
			Map<String, StepElement> mappedElements) {
		String elementKey = (String) mappedAction.get(INPUT_TAG);
		return mappedElements.get(elementKey);
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

}
