package dev.edumelo.com.nndl_core.action;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import dev.edumelo.com.nndl_core.DataBindExtractor;
import dev.edumelo.com.nndl_core.ExtractDataBind;
import dev.edumelo.com.nndl_core.ExtractDataBindCreator;
import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;
import lombok.ToString;

@ToString
public class Fill extends Action implements DataBindExtractor {
	private static final String TAG = "fill";
	private static final String TARGET_TAG = "targertElement";
	private ExtractDataBindCreator extractor;
	private List<ExtractDataBind> extractDataBindList;
	private StepElement targetElement;
	
	public Fill(Map<String, ?> mappedAction, Map<String, StepElement> mappedElements) {
		extractor = getExtractoClass(mappedAction);
		targetElement = getTargetElement(mappedAction, mappedElements);
		this.extractDataBindList = new ArrayList<ExtractDataBind>();
	}

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public boolean isIgnoreRoot() {
		return false;
	}

	@Override
	public void runPreviousModification(ActionModificator modificiator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Advice runNested(SeleniumSndlWebDriver webDriver, SeleniumSndlWebDriverWaiter webDriverWait,
			IterationContent rootElement) throws ActionException {
		return runElement(rootElement.getRootElement());
	}

	@Override
	public Advice runAction(SeleniumSndlWebDriver webDriver, SeleniumSndlWebDriverWaiter webDriverWait)
			throws ActionException {
		//TODO parametrized duration
		WebElement target =  webDriverWait.getWebDriverWaiter().withTimeout(Duration.ofSeconds(50)).until(ExpectedConditions.elementToBeClickable(
				targetElement.getLocator(webDriver)));
		
		return runElement(target);
	}
	
	public Advice runElement(WebElement element) {
		extractDataBindList.add(extractor.createFromElement(element));
		return new ContinueAdvice();
	}

	@Override
	public Collection<ExtractDataBind> getExtractDataBind() {
		return extractDataBindList;
	}
	
	private ExtractDataBindCreator getExtractoClass(Map<String, ?> mappedAction) {
		String value = (String) mappedAction.get(TAG);
		try {
			return (ExtractDataBindCreator) Class.forName(value).getConstructor().newInstance();
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Extractor class could not be instantiate", e );
		}
	}
	
	private StepElement getTargetElement(Map<String, ?> mappedAction, Map<String, StepElement> mappedElements) {
		Object elementNameObject = mappedAction.get(TARGET_TAG);
		if(elementNameObject != null) {
			String elementName = (String) elementNameObject;
			return mappedElements.get(elementName);
		}
		return null;
	}

}
