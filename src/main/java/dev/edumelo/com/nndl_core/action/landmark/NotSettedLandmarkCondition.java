package dev.edumelo.com.nndl_core.action.landmark;

import java.util.List;

public class NotSettedLandmarkCondition extends LandmarkConditionAggregation {

	@Override
	public List<Landmark> getLandmarkConditions() {
		return null;
	}

	@Override
	public LandmarkConditionAggregationType getType() {
		return LandmarkConditionAggregationType.LANDMARK_NOT_SETTED;
	}

}
