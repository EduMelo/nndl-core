package dev.edumelo.com.nndl_core.step;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

import dev.edumelo.com.nndl_core.exceptions.unchecked.NndlParserRuntimeException;
import dev.edumelo.com.nndl_core.nndl.NndlNode;
import dev.edumelo.com.nndl_core.webdriver.NndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;

public class StepElement2 {
	private static final String SHADOW_ROOT_TAG = "\\$\\{shadowRoot\\}";
	private static final String IGNORE_ROOT_TAG = "ignoreRoot";
	private static final String MATCH_EXP_TAG = "matchExp";
	private static final String NAME_TAG = "name";
	private static final String TAG = "elements";
	
	private String name;
	private String matchExp;
	private boolean ignoreRoot; 

	public StepElement2(Map<String, ?> mappedElement) {
		this.name = (String) mappedElement.get(NAME_TAG);
		this.matchExp = (String) mappedElement.get(MATCH_EXP_TAG);
		this.ignoreRoot = getIgnoreRoot(mappedElement);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMatchExp() {
		return matchExp;
	}
	public void setMatchExp(String matchExp) {
		this.matchExp = matchExp;
	}
	public boolean isIgnoreRoot() {
		return ignoreRoot;
	}
	public void setIgnoreRoot(boolean ignoreRoot) {
		this.ignoreRoot = ignoreRoot;
	}
	public static String getIgnoreRootTag() {
		return IGNORE_ROOT_TAG;
	}
	public static String getMatchExpTag() {
		return MATCH_EXP_TAG;
	}
	public static String getNameTag() {
		return NAME_TAG;
	}
	public static String getTag() {
		return TAG;
	}

	private boolean getIgnoreRoot(Map<String, ?> mappedElement) {
		Object ignorRootValue = mappedElement.get(IGNORE_ROOT_TAG);
		if(Objects.isNull(ignorRootValue)) {
			return false;
		}
		return (boolean) ignorRootValue;
	}
	
	public StepElement2(NndlNode stepElement) {
		setName(stepElement.getScalarValueFromChild(NAME_TAG)
				.orElseThrow(NndlParserRuntimeException.get("StepElement should have "+NAME_TAG+" tag.", stepElement)));
		setMatchExp(stepElement.getScalarValueFromChild(MATCH_EXP_TAG).get());
	}
	
	public StepElement2(StepElement2 stepElement) {
		setName(stepElement.name);
		setMatchExp(stepElement.matchExp);
	}
	
	private boolean containsShadowRoot() {
		Pattern pattern = Pattern.compile(SHADOW_ROOT_TAG);
		Matcher matcher = pattern.matcher(this.matchExp);
		return matcher.find();
	}
	
	private String[] getMatchExpressions() {
		return this.matchExp.split(SHADOW_ROOT_TAG);
	}
	
	public ExpectedCondition<WebElement> elementToBeClickable(NndlWebDriver remoteWebDriver) {
		if(containsShadowRoot()) {
			String[] matchExpressions = getMatchExpressions();
			SeleniumSndlWebDriver seleniumSndlWebDriver = (SeleniumSndlWebDriver) remoteWebDriver;
			RemoteWebDriver webDriver = seleniumSndlWebDriver.getWebDriver();
			WebElement shadowHost = webDriver.findElement(By.cssSelector(matchExpressions[0]));
			SearchContext shadowRoot = shadowHost.getShadowRoot();
			return driver -> {
				WebElement element = shadowRoot.findElement(By.cssSelector(matchExpressions[1]));
				return (element != null && element.isDisplayed() && element.isEnabled()) ? element : null;
			};
		} else {
			By locator = By.cssSelector(getMatchExpressions()[0]);
			return ExpectedConditions.elementToBeClickable(locator);			
		}
	}
	
	public ExpectedCondition<WebElement> nestedElementToBeClickable(NndlWebDriver remoteWebDriver, WebElement rootElement) {
		if(containsShadowRoot()) {
			String[] matchExpressions = getMatchExpressions();
			WebElement shadowHost = rootElement.findElement(By.cssSelector(matchExpressions[0]));
			SearchContext shadowRoot = shadowHost.getShadowRoot(); 
			return driver -> {
				WebElement element = shadowRoot.findElement(By.cssSelector(matchExpressions[1]));
				return (element != null && element.isDisplayed() && element.isEnabled()) ? element : null;
			};
		} else {
			By locator = By.cssSelector(getMatchExpressions()[0]);
			return ExpectedConditions.elementToBeClickable(rootElement.findElement(locator));			
		}
	}
	
	public ExpectedCondition<WebElement> visibilityOfElementLocated(NndlWebDriver remoteWebDriver){
		if(containsShadowRoot()) {
			if(!(remoteWebDriver instanceof SeleniumSndlWebDriver)) {
				throw new RuntimeException();
			}
			String[] matchExpressions = getMatchExpressions();
			SeleniumSndlWebDriver seleniumSndlWebDriver = (SeleniumSndlWebDriver) remoteWebDriver;
			RemoteWebDriver webDriver = seleniumSndlWebDriver.getWebDriver();
			WebElement shadowHost = webDriver.findElement(By.cssSelector(matchExpressions[0]));
			SearchContext shadowRoot = shadowHost.getShadowRoot();
			return driver -> {
				WebElement element = shadowRoot.findElement(By.cssSelector(matchExpressions[1]));
				return (element != null && element.isDisplayed()) ? element : null;
			};
		} else {
			By locator = By.cssSelector(getMatchExpressions()[0]);
			return ExpectedConditions.visibilityOfElementLocated(locator);			
		}
	}
	
	public ExpectedCondition<WebElement> presenceOfElementLocated(NndlWebDriver remoteWebDriver){
		if(containsShadowRoot()) {
			if(!(remoteWebDriver instanceof SeleniumSndlWebDriver)) {
				throw new RuntimeException();
			}
			String[] matchExpressions = getMatchExpressions();
			SeleniumSndlWebDriver seleniumSndlWebDriver = (SeleniumSndlWebDriver) remoteWebDriver;
			RemoteWebDriver webDriver = seleniumSndlWebDriver.getWebDriver();
			WebElement shadowHost = webDriver.findElement(By.cssSelector(matchExpressions[0]));
			SearchContext shadowRoot = shadowHost.getShadowRoot();
			return driver -> {
				return shadowRoot.findElement(By.cssSelector(matchExpressions[1]));
			};
		} else {
			By locator = By.cssSelector(getMatchExpressions()[0]);
			return ExpectedConditions.presenceOfElementLocated(locator);			
		}
	}
	
	public ExpectedCondition<List<WebElement>> presenceOfAllElementsLocatedBy(NndlWebDriver remoteWebDriver){
		if(containsShadowRoot()) {
			if(!(remoteWebDriver instanceof SeleniumSndlWebDriver)) {
				throw new RuntimeException();
			}
			String[] matchExpressions = getMatchExpressions();
			SeleniumSndlWebDriver seleniumSndlWebDriver = (SeleniumSndlWebDriver) remoteWebDriver;
			RemoteWebDriver webDriver = seleniumSndlWebDriver.getWebDriver();
			WebElement shadowHost = webDriver.findElement(By.cssSelector(matchExpressions[0]));
			SearchContext shadowRoot = shadowHost.getShadowRoot();
			return driver -> {
				List<WebElement> elements = shadowRoot.findElements(By.cssSelector(matchExpressions[1]));
				return !elements.isEmpty() ? elements : null;
			};
		} else {
			By locator = By.cssSelector(getMatchExpressions()[0]);
			return ExpectedConditions.presenceOfAllElementsLocatedBy(locator);			
		}
	}
	
	public ExpectedCondition<List<WebElement>> presenceOfNestedElementsLocatedBy(
			SeleniumSndlWebDriver seleniumWebDriver, WebElement rootElement) {
		By locator = By.cssSelector(getMatchExpressions()[0]);
		if(containsShadowRoot()) {
			String[] matchExpressions = getMatchExpressions();
			WebElement shadowHost = rootElement.findElement(By.cssSelector(matchExpressions[0]));
			SearchContext shadowRoot = shadowHost.getShadowRoot(); 
			return new ExpectedCondition<List<WebElement>>() {
				@Override
				public List<WebElement> apply(WebDriver driver) {
					List<WebElement> allChildren = shadowRoot.findElements(locator);
					return allChildren.isEmpty() ? null : allChildren;
				}
			};
		} else {
			return new ExpectedCondition<List<WebElement>>() {
				@Override
				public List<WebElement> apply(WebDriver driver) {
					List<WebElement> allChildren = rootElement.findElements(locator);
					return allChildren.isEmpty() ? null : allChildren;
				}
			};
		}
	  }
	
	@Override
	public String toString() {
		return "StepElement [name=" + name + ", matchExp=" + matchExp + ", ignoreRoot=" + ignoreRoot + "]";
	}

}
