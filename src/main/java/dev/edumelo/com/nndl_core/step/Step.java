package dev.edumelo.com.nndl_core.step;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import dev.edumelo.com.nndl_core.action.Action;
import dev.edumelo.com.nndl_core.exceptions.unchecked.NndlParserRuntimeException;
import dev.edumelo.com.nndl_core.nndl.NndlNode;
import dev.edumelo.com.nndl_core.webdriver.SeleniumHubProperties;

public class Step {
	private static final String TAG = "steps";
	private static final String SUBSTEP_TAG = "subSteps";
	private static final String NAME_TAG = "name";
	
	private String name;
	private Map<String, StepElement> elements;
	private LinkedList<Action> actions;
	private Map<String, Step> subSteps;
	private boolean limitCountInvalidate = false;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Map<String, StepElement> getElements() {
		return elements;
	}
	public void setElements(Map<String, StepElement> elements) {
		this.elements = elements;
	}
	public LinkedList<Action> getActions() {
		return actions;
	}
	public void setActions(LinkedList<Action> actions) {
		this.actions = actions;
	}
	public Map<String, Step> getSubSteps() {
		return subSteps;
	}
	public void setSubSteps(Map<String, Step> subSteps) {
		this.subSteps = subSteps;
	}
	public boolean isLimitCountInvalidate() {
		return limitCountInvalidate;
	}
	public void setLimitCountInvalidate(boolean limitCountInvalidate) {
		this.limitCountInvalidate = limitCountInvalidate;
	}
	public static String getTag() {
		return TAG;
	}
	public static Object getSubstepTag() {
		return SUBSTEP_TAG;
	}

	public Step(SeleniumHubProperties seleniumHubProperties, NndlNode node) {
		List<NndlNode> listedElements = node.getListedValuesFromChild(StepElement.getTag()).orElse(new ArrayList<>());
		List<NndlNode> listedSubSteps = node.getListedValuesFromChild(SUBSTEP_TAG).orElse(null);
		List<NndlNode> listedActions = node.getListedValuesFromChild(Action.getActionTag())
				.orElseThrow(() -> new NndlParserRuntimeException("A steps tag should have a actions mark", node));

		this.name = node.getScalarValueFromChild(NAME_TAG).get();
		this.elements = extractElements(listedElements);
		this.subSteps = extractedSubSteps(seleniumHubProperties, listedSubSteps);
		this.actions = extractedActions(seleniumHubProperties, this.elements, subSteps, listedActions);
	}
	
	private Map<String, Step> extractedSubSteps(SeleniumHubProperties seleniumHubProperties, List<NndlNode> listedSubSteps) {
		if(listedSubSteps == null || listedSubSteps.size() == 0) {
			return null;
		}	
		
		return listedSubSteps.stream()
				.map(s -> new Step(seleniumHubProperties, s))
				.collect(Collectors.toMap(Step::getName, Function.identity()));
	}

	private Map<String, StepElement> extractElements(List<NndlNode> listedElements) {
		if(Objects.isNull(listedElements)) {
			return null;
		}
		return listedElements.stream()
				.map(StepElement::new)
				.collect(Collectors.toMap(StepElement::getName, Function.identity()));
	}

	private LinkedList<Action> extractedActions(SeleniumHubProperties seleniumHubProperties,
			Map<String, StepElement> mappedElements, Map<String, Step> mappedSubSteps,
			List<NndlNode> listedActions) {
		return listedActions.stream()
				.map(m -> Action.createAction(seleniumHubProperties, mappedElements, mappedSubSteps,
						m))
				.filter(Objects::nonNull)
				.sorted(Comparator.comparing(Action::getOrder))
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
}
