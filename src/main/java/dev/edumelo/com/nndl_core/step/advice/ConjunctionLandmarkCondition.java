package dev.edumelo.com.nndl_core.step.advice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dev.edumelo.com.nndl_core.action.landmark.Landmark;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkConditionAggregation;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkConditionAggregationType;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkFactory;
import dev.edumelo.com.nndl_core.exceptions.NndlParserException;
import dev.edumelo.com.nndl_core.nndl.NndlNode;
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

	public ConjunctionLandmarkCondition(NndlNode mappedAction, Map<String, StepElement> mappedElements) {
		this.landmarkConditions = getLandmarkConditions(mappedAction, mappedElements);
	}
	
	private List<Landmark> getLandmarkConditions(NndlNode mappedAction, Map<String, StepElement> mappedElements) {
		List<NndlNode> landmarkList = mappedAction.getListedValuesFromChild(TAG)
				.orElseThrow(() -> new NndlParserException("Conjuction landmark should have "+TAG+" tag", mappedAction));
		
		List<Landmark> landmarkConditions = new ArrayList<Landmark>();
		for (NndlNode landmark : landmarkList) {
			landmarkConditions.add(LandmarkFactory.createLandmark(landmark, mappedElements, getType()));
		}
		
		return landmarkConditions;
	}

	@Override
	public String toString() {
		return "ConjunctionLandmarkCondition [landmarkConditions=" + landmarkConditions + "]";
	}
}
