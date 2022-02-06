package dev.edumelo.com.nndl_core.step.advice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dev.edumelo.com.nndl_core.action.landmark.Landmark;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkConditionAggregation;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkConditionAggregationType;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkFactory;
import dev.edumelo.com.nndl_core.step.StepElement;

public class ConjunctionLandmarkCondition extends LandmarkConditionAggregation {
	private static final String TAG = "landmarkConditions";
	private List<Landmark> landmarkConditions;
	
	@Override
	public LandmarkConditionAggregationType getType() {
		return LandmarkConditionAggregationType.CONJUNCTION;
	}
	
	public List<Landmark> getLandmarkConditions() {
		return landmarkConditions;
	}

	public ConjunctionLandmarkCondition(Map<String, ?> mappedAction, Map<String, StepElement> mappedElements) {
		this.landmarkConditions = getLandmarkConditions(mappedAction, mappedElements);
	}

	@SuppressWarnings("unchecked")
	private List<Landmark> getLandmarkConditions(Map<String, ?> mappedAction, Map<String, StepElement> mappedElements) {
		List<?> landmarkList = (List<?>) mappedAction.get(TAG);
//		Map<String, ?> landmarks = (Map<String, ?>)  landmarkList.get(0);
		
		List<Landmark> landmarkConditions = new ArrayList<Landmark>();
		for (Object landmark : landmarkList) {
			Map<String, ?> landmarkMap = (Map<String, ?>) landmark;
			landmarkConditions.add(LandmarkFactory.createLandmark(landmarkMap, mappedElements, getType()));
		}
		
		return landmarkConditions;
		
//		return landmarks.entrySet().stream()
//		  .map(e -> LandmarkFactory.createLandmark(e, mappedElements))
//		  .collect(Collectors.toList());
	}

	@Override
	public String toString() {
		return "ConjunctionLandmarkCondition [landmarkConditions=" + landmarkConditions + "]";
	}
}
