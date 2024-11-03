package dev.edumelo.com.nndl_core.step.advice;

import java.util.List;
import java.util.Map;

import dev.edumelo.com.nndl_core.action.landmark.Landmark;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkConditionAggregation;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkConditionAggregationType;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkFactory;
import dev.edumelo.com.nndl_core.nndl.NndlNode;
import dev.edumelo.com.nndl_core.step.StepElement;

public class DisjunctionLandmarkCondition extends LandmarkConditionAggregation {
	private static final String TAG = "forkElement";
	private List<Landmark> landmarkConditions;
	
	@Override
	public LandmarkConditionAggregationType getType() {
		return LandmarkConditionAggregationType.DISJUNCTION;
	}

	public static String getTag() {
		return TAG;
	}
	public List<Landmark> getLandmarkConditions() {
		return landmarkConditions;
	}

	public DisjunctionLandmarkCondition(NndlNode mappedAction, Map<String, StepElement> mappedElements) {
		this.landmarkConditions = getForkElements(mappedAction, mappedElements);
	}

	private List<Landmark> getForkElements(NndlNode mappedAction, Map<String, StepElement> mappedElements) {
		return mappedAction.getListedValuesFromChild(TAG)
			.get()
			.stream()
			.map(n -> LandmarkFactory.createLandmark(n, mappedElements, getType()))
			.toList();
	}

	@Override
	public String toString() {
		return "DisjunctionLandmarkCondition [landmarkConditions=" + landmarkConditions + "]";
	}

}
