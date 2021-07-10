package dev.edumelo.com.nndl_core.step;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import dev.edumelo.com.nndl_core.action.Action;
import lombok.Data;

@Data
public class Step {
	private static final String TAG = "steps";
	private static final Object SUBSTEP_TAG = "subSteps";
	
	private String name;
	private Map<String, StepElement> elements;
	private LinkedList<Action> actions;
	private Map<String, Step> subSteps;
	private boolean limitCountInvalidate = false;
	
	public static String getTag() {
		return Step.TAG;
	}
	
	@SuppressWarnings("unchecked")
	public Step(Map<String, ?> mappedStep) {
		this.name = (String) mappedStep.get("name");
		this.elements = extractElements((ArrayList<Map<String, ?>>) mappedStep.get(StepElement.getTag()));
		this.subSteps = extractedSubSteps((ArrayList<Map<String, ?>>) mappedStep.get(SUBSTEP_TAG));
		this.actions = extractedActions(elements, subSteps, (ArrayList<Map<String, ?>>) mappedStep.get(Action.getActionTag()));
	}
	
	private Map<String, Step> extractedSubSteps(ArrayList<Map<String, ?>> listedSubSteps) {
		if(listedSubSteps == null || listedSubSteps.size() == 0) {
			return null;
		}
		
		return listedSubSteps.stream()
				.map(Step::new)
				.collect(Collectors.toMap(Step::getName, Function.identity()));
	}

	private Map<String, StepElement> extractElements(ArrayList<Map<String, ?>> listedElements) {
		if(Objects.isNull(listedElements)) {
			return null;
		}
		return listedElements.stream()
				.map(StepElement::new)
				.collect(Collectors.toMap(StepElement::getName, Function.identity()));
	}

	private LinkedList<Action> extractedActions(Map<String, StepElement> mappedElements, Map<String, Step> mappedSubSteps, ArrayList<Map<String, ?>> listedActions) {
		//TODO retirar filtro de não nulo
		return listedActions.stream()
				.map(m -> Action.createAction(mappedElements, mappedSubSteps, m))
				.filter(Objects::nonNull)
				.sorted(Comparator.comparing(Action::getOrder))
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
}
