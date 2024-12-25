package dev.edumelo.com.nndl_core.step;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

import dev.edumelo.com.nndl_core.action.landmark.LandmarkStrategies;
import dev.edumelo.com.nndl_core.exceptions.unchecked.NndlParserRuntimeException;
import dev.edumelo.com.nndl_core.nndl.NndlNode;
import dev.edumelo.com.nndl_core.webdriver.NndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;

public class StepElement {
	public static final String TAG = "elements";

	private static final String IGNORE_ROOT_TAG = "ignoreRoot";
	private static final String MATCH_EXP_TAG = "matchExp";
	private static final String NAME_TAG = "name";
	private static final String SHADOW_PATH_TAG = "shadowPath";
	
	private String name;
	private String matchExp;
	private boolean ignoreRoot; 
	private List<String> shadowPath;

	@SuppressWarnings("unchecked")
	public StepElement(Map<String, ?> mappedElement) {
		this.name = (String) mappedElement.get(NAME_TAG);
		this.matchExp = (String) mappedElement.get(MATCH_EXP_TAG);
		this.ignoreRoot = getIgnoreRoot(mappedElement);
		this.shadowPath = (List<String>) mappedElement.get(SHADOW_PATH_TAG);
	}
	
	public StepElement(NndlNode nndlElement) {
		//track2
		this.name = nndlElement.getScalarValueFromChild(NAME_TAG)
				.orElseThrow(NndlParserRuntimeException.get("StepElement should have "+NAME_TAG+" tag.", nndlElement));
		this.matchExp = nndlElement.getScalarValueFromChild(MATCH_EXP_TAG).get();
		this.ignoreRoot = nndlElement.getScalarValueFromChild(IGNORE_ROOT_TAG, Boolean.class).orElse(false);		
		nndlElement.getValueFromChild(SHADOW_PATH_TAG)
				.flatMap(NndlNode::getListedValues)
				.stream()
				.flatMap(List::stream)
				.map(NndlNode::getScalarValue)
				.flatMap(Optional::stream)
				.forEach(this::addShadowPath);
	}
	
	public StepElement(StepElement stepElement) {
		//track1
		this.name = stepElement.name;
		this.matchExp = stepElement.matchExp;
		this.shadowPath = stepElement.shadowPath;
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
	public List<String> getShadowPath() {
		return shadowPath;
	}
	public void setShadowPath(List<String> shadowPath) {
		this.shadowPath = shadowPath;
	}
	public void addShadowPath(String shadoPathPart) {
		if(this.shadowPath == null) {
			this.shadowPath = new ArrayList<>();
		}
		this.shadowPath.add(shadoPathPart);
	}

	private boolean getIgnoreRoot(Map<String, ?> mappedElement) {
		Object ignorRootValue = mappedElement.get(IGNORE_ROOT_TAG);
		if(Objects.isNull(ignorRootValue)) {
			return false;
		}
		return (boolean) ignorRootValue;
	}
	
	private boolean containsShadowRoot() {
		return CollectionUtils.isNotEmpty(this.shadowPath);
	}
	
	private SearchContext findRoot(NndlWebDriver remoteWebDriver) {
		if(!(remoteWebDriver instanceof SeleniumSndlWebDriver)) {
			throw new RuntimeException("remoteWebDriver is not a SeleniumSndlWebDriver");
		}
		SeleniumSndlWebDriver seleniumSndlWebDriver = (SeleniumSndlWebDriver) remoteWebDriver;
		RemoteWebDriver webDriver = seleniumSndlWebDriver.getWebDriver();
		SearchContext root = webDriver;
		return findRoot(root);
	}

	private SearchContext findRoot(SearchContext root) {
		for (String path : shadowPath) {
			WebElement shadowHost = root.findElement(By.cssSelector(path));
			root = shadowHost.getShadowRoot();				
		}
		return root;
	}
	
	public ExpectedCondition<WebElement> landmarkAchiveable(NndlWebDriver remoteWebDriver,
			LandmarkStrategies strategies){
		SearchContext root = null;
		if(containsShadowRoot()) {
			root = findRoot(remoteWebDriver);
		} else {
			SeleniumSndlWebDriver seleniumSndlWebDriver = (SeleniumSndlWebDriver) remoteWebDriver;
			root = seleniumSndlWebDriver.getWebDriver();
		}
		return strategies.getConditionIfAchieved(root, matchExp);
	}
	
	public ExpectedCondition<WebElement> landmarkAchiveable(NndlWebDriver remoteWebDriver,
			LandmarkStrategies strategies, WebElement rootElement){
		SearchContext root = null;
		if(containsShadowRoot()) {
			root = findRoot(rootElement);
		} else {
			root = rootElement;
		}
		return strategies.getConditionIfAchieved(root, matchExp);
	}
	
	public ExpectedCondition<List<WebElement>> allLandmarkAchiveable(NndlWebDriver remoteWebDriver,
			LandmarkStrategies strategies){
		SearchContext root = null;
		if(containsShadowRoot()) {
			root = findRoot(remoteWebDriver);
		} else {
			SeleniumSndlWebDriver seleniumSndlWebDriver = (SeleniumSndlWebDriver) remoteWebDriver;
			root = seleniumSndlWebDriver.getWebDriver();
		}
		return strategies.getConditionIfAllAchieved(root, matchExp);
	}
	
//	public ExpectedCondition<List<WebElement>> presenceOfAllElementsLocatedBy(NndlWebDriver remoteWebDriver){
//		if(containsShadowRoot()) {
//			SearchContext root = findRoot(remoteWebDriver);
//			return getMultPresenceCondition(root);
//		} else {
//			By locator = By.cssSelector(this.matchExp);
//			return ExpectedConditions.presenceOfAllElementsLocatedBy(locator);			
//		}
//	}
	
	public ExpectedCondition<List<WebElement>> presenceOfNestedElementsLocatedBy(
			SeleniumSndlWebDriver seleniumWebDriver, WebElement rootElement) {
		By locator = By.cssSelector(this.matchExp);
		if(containsShadowRoot()) {
			SearchContext root = findRoot(seleniumWebDriver);
			return new ExpectedCondition<List<WebElement>>() {
				@Override
				public List<WebElement> apply(WebDriver driver) {
					List<WebElement> allChildren = root.findElements(locator);
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
