package dev.edumelo.com.nndl_core.step.advice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dev.edumelo.com.nndl_core.action.landmark.Landmark;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkConditionAggregation;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkConditionAggregationType;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkFactory;
import dev.edumelo.com.nndl_core.step.StepElement;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DisjunctionLandmarkCondition extends LandmarkConditionAggregation {
	private static final String TAG = "forkElement";
	private List<Landmark> landmarkConditions;
	
	@Override
	public LandmarkConditionAggregationType getType() {
		return LandmarkConditionAggregationType.DISJUNCTION;
	}
	
	public List<Landmark> getLandmarkConditions() {
		return landmarkConditions;
	}

	public DisjunctionLandmarkCondition(Map<String, ?> mappedAction, Map<String, StepElement> mappedElements) {
		this.landmarkConditions = getForkElements(mappedAction, mappedElements);
	}

	@SuppressWarnings("unchecked")
	private List<Landmark> getForkElements(Map<String, ?> mappedAction, Map<String, StepElement> mappedElements) {
		List<?> landmarkList = (List<?>) mappedAction.get(TAG);
//		Collection<Map<String, ?>> collection = (Collection<Map<String, ?>>) mappedAction.get(TAG);
//		
//		return collection.stream()
//				.map(m -> new ForkElement(mappedElements, m))
//				.collect(Collectors.toList());
//		Map<String, ?> landmarks = (Map<String, ?>) mappedAction.get(TAG);
//		
//		return landmarks.entrySet().stream()
//		  .map(e -> LandmarkFactory.createLandmark(e, mappedElements))
//		  .collect(Collectors.toList());
		List<Landmark> landmarkConditions = new ArrayList<Landmark>();
		for (Object landmark : landmarkList) {
			Map<String, ?> landmarkMap = (Map<String, ?>) landmark;
			landmarkConditions.add(LandmarkFactory.createLandmark(landmarkMap, mappedElements, getType()));
		}
		
		return landmarkConditions;
	}

	
}
