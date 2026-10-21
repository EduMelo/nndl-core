package dev.edumelo.com.nndl_core.step;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;

import dev.edumelo.com.nndl_core.action.landmark.LandmarkStrategies;
import dev.edumelo.com.nndl_core.exceptions.unchecked.NndlParserRuntimeException;
import dev.edumelo.com.nndl_core.nndl.NndlNode;
import dev.edumelo.com.nndl_core.webdriver.NndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.NndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.NndlElement;
import dev.edumelo.com.nndl_core.webdriver.NndlElementCondition;
import dev.edumelo.com.nndl_core.webdriver.NndlElementsCondition;
import dev.edumelo.com.nndl_core.webdriver.NndlLocator;
import dev.edumelo.com.nndl_core.webdriver.NndlSearchContext;

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
		nndlElement.extractScalarList(SHADOW_PATH_TAG).forEach(this::addShadowPath);
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
	
	private NndlSearchContext findRoot(NndlWebDriver remoteWebDriver) {
		return findRoot((NndlSearchContext) remoteWebDriver);
	}

	private NndlSearchContext findRoot(NndlSearchContext root) {
		for (String path : shadowPath) {
			NndlElement shadowHost = root.find(NndlLocator.css(path));
			root = shadowHost.shadowRoot();				
		}
		return root;
	}
	
	public NndlElementCondition landmarkAchiveable(NndlWebDriver remoteWebDriver,
			LandmarkStrategies strategies){
		NndlSearchContext root = null;
		if(containsShadowRoot()) {
			root = findRoot(remoteWebDriver);
		} else {
			root = remoteWebDriver;
		}
		return strategies.getConditionIfAchieved(root, matchExp);
	}
	
	public NndlElementCondition landmarkAchiveable(NndlWebDriver remoteWebDriver,
			LandmarkStrategies strategies, NndlElement rootElement){
		NndlSearchContext root = null;
		if(containsShadowRoot()) {
			root = findRoot(rootElement);
		} else {
			root = rootElement;
		}
		return strategies.getConditionIfAchieved(root, matchExp);
	}
	
	public NndlElementsCondition allLandmarkAchiveable(NndlWebDriver remoteWebDriver,
			LandmarkStrategies strategies){
		NndlSearchContext root = null;
		if(containsShadowRoot()) {
			root = findRoot(remoteWebDriver);
		} else {
			root = remoteWebDriver;
		}
		return strategies.getConditionIfAllAchieved(root, matchExp);
	}
	
//	public NndlElementsCondition presenceOfAllElementsLocatedBy(NndlWebDriver remoteWebDriver){
//		if(containsShadowRoot()) {
//			NndlSearchContext root = findRoot(remoteWebDriver);
//			return getMultPresenceCondition(root);
//		} else {
//			By locator = NndlLocator.css(this.matchExp);
//			return ExpectedConditions.presenceOfAllElementsLocatedBy(locator);			
//		}
//	}
	
	public NndlElementsCondition presenceOfNestedElementsLocatedBy(
			NndlWebDriver seleniumWebDriver, NndlElement rootElement) {
		NndlLocator locator = NndlLocator.css(this.matchExp);
		if(containsShadowRoot()) {
			NndlSearchContext root = findRoot(seleniumWebDriver);
			return () -> {
				List<NndlElement> allChildren = root.findAll(locator);
				return allChildren.isEmpty() ? null : allChildren;
			};
		} else {
			return () -> {
				List<NndlElement> allChildren = rootElement.findAll(locator);
				return allChildren.isEmpty() ? null : allChildren;
			};
		}
	  }
	
	@Override
	public String toString() {
		return "StepElement [name=" + name + ", matchExp=" + matchExp + ", ignoreRoot=" + ignoreRoot + "]";
	}

}
