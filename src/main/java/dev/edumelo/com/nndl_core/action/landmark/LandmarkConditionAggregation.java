package dev.edumelo.com.nndl_core.action.landmark;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import dev.edumelo.com.nndl_core.nndl.NndlNode;
import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.advice.ConjunctionLandmarkCondition;
import dev.edumelo.com.nndl_core.step.advice.DisjunctionLandmarkCondition;

public abstract class LandmarkConditionAggregation {
	public abstract List<Landmark> getLandmarkConditions();
	
	public static LandmarkConditionAggregation createLandmarkConditionAggregation(Map<String, StepElement> mappedElements,
			NndlNode mappedAction) {
		LandmarkConditionAggregationType landmarkConditionAggregationType = indentifyLandmarkConditionAggregationType(mappedAction);
		LandmarkConditionAggregation createdLandmarkConditionAggregation = null;
		
		switch(landmarkConditionAggregationType) {
			case CONJUNCTION:
				//track1
				createdLandmarkConditionAggregation = new ConjunctionLandmarkCondition(mappedAction, mappedElements);
				break;
			case DISJUNCTION:
				createdLandmarkConditionAggregation = new DisjunctionLandmarkCondition(mappedAction, mappedElements);
				break;
			case LANDMARK_NOT_SETTED:
				createdLandmarkConditionAggregation = new NotSettedLandmarkCondition();
				break;
			default:
				break;
		}
		
		return createdLandmarkConditionAggregation;
	}

	private static LandmarkConditionAggregationType indentifyLandmarkConditionAggregationType(NndlNode mappedAction) {
		
		return Arrays.stream(LandmarkConditionAggregationType.values())
				.filter(e -> mappedAction.hasValueFromChild(e.getLandMarkConditionAgregationTag()))
				.findFirst()
				.orElse(LandmarkConditionAggregationType.LANDMARK_NOT_SETTED);
		
	}
	
	public abstract LandmarkConditionAggregationType getType();

}
