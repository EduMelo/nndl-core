package dev.edumelo.com.nndl_core.step;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import dev.edumelo.com.nndl_core.exceptions.unchecked.NndlParserRuntimeException;
import dev.edumelo.com.nndl_core.nndl.NndlNode;
import dev.edumelo.com.nndl_core.webdriver.NndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.NndlElement;
import dev.edumelo.com.nndl_core.webdriver.NndlElementCondition;
import dev.edumelo.com.nndl_core.webdriver.NndlElementsCondition;
import dev.edumelo.com.nndl_core.webdriver.NndlLocator;
import dev.edumelo.com.nndl_core.webdriver.NndlSearchContext;

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
	
	public NndlElementCondition elementToBeClickable(NndlWebDriver remoteWebDriver) {
		return elementMatching(remoteWebDriver, true, true);
	}
	
	public NndlElementCondition nestedElementToBeClickable(NndlWebDriver remoteWebDriver, NndlElement rootElement) {
		if(containsShadowRoot()) {
			NndlSearchContext shadowRoot = shadowRootOf(rootElement);
			return () -> matching(shadowRoot.find(NndlLocator.css(getMatchExpressions()[1])), true, true);
		}
		NndlElement element = rootElement.find(NndlLocator.css(getMatchExpressions()[0]));
		return () -> matching(element, true, true);
	}
	
	public NndlElementCondition visibilityOfElementLocated(NndlWebDriver remoteWebDriver){
		return elementMatching(remoteWebDriver, true, false);
	}
	
	public NndlElementCondition presenceOfElementLocated(NndlWebDriver remoteWebDriver){
		return elementMatching(remoteWebDriver, false, false);
	}
	
	public NndlElementsCondition presenceOfAllElementsLocatedBy(NndlWebDriver remoteWebDriver){
		if(containsShadowRoot()) {
			NndlSearchContext shadowRoot = shadowRootOf(remoteWebDriver);
			return () -> {
				List<NndlElement> elements = shadowRoot.findAll(NndlLocator.css(getMatchExpressions()[1]));
				return elements.isEmpty() ? null : elements;
			};
		}
		NndlLocator locator = NndlLocator.css(getMatchExpressions()[0]);
		return () -> {
			List<NndlElement> elements = remoteWebDriver.findAll(locator);
			return elements.isEmpty() ? null : elements;
		};
	}
	
	public NndlElementsCondition presenceOfNestedElementsLocatedBy(
			NndlWebDriver seleniumWebDriver, NndlElement rootElement) {
		NndlLocator locator = NndlLocator.css(getMatchExpressions()[0]);
		if(containsShadowRoot()) {
			NndlSearchContext shadowRoot = shadowRootOf(rootElement);
			return () -> {
				List<NndlElement> allChildren = shadowRoot.findAll(locator);
				return allChildren.isEmpty() ? null : allChildren;
			};
		}
		return () -> {
			List<NndlElement> allChildren = rootElement.findAll(locator);
			return allChildren.isEmpty() ? null : allChildren;
		};
	}

	private NndlElementCondition elementMatching(NndlWebDriver remoteWebDriver, boolean displayed, boolean enabled) {
		if(containsShadowRoot()) {
			NndlSearchContext shadowRoot = shadowRootOf(remoteWebDriver);
			NndlLocator locator = NndlLocator.css(getMatchExpressions()[1]);
			return () -> matching(shadowRoot.find(locator), displayed, enabled);
		}
		NndlLocator locator = NndlLocator.css(getMatchExpressions()[0]);
		return () -> {
			List<NndlElement> elements = remoteWebDriver.findAll(locator);
			return elements.isEmpty() ? null : matching(elements.get(0), displayed, enabled);
		};
	}

	private NndlSearchContext shadowRootOf(NndlSearchContext root) {
		NndlElement shadowHost = root.find(NndlLocator.css(getMatchExpressions()[0]));
		return shadowHost.shadowRoot();
	}

	private NndlElement matching(NndlElement element, boolean displayed, boolean enabled) {
		if(element == null) {
			return null;
		}
		if(displayed && !element.isDisplayed()) {
			return null;
		}
		if(enabled && !element.isEnabled()) {
			return null;
		}
		return element;
	}
	
	@Override
	public String toString() {
		return "StepElement [name=" + name + ", matchExp=" + matchExp + ", ignoreRoot=" + ignoreRoot + "]";
	}

}
