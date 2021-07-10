package dev.edumelo.com.nndl_core.action.landmark;

public enum LandmarkConditionAggregationType {

	CONJUNCTION("landmarkConditions"),
	DISJUNCTION("forkElement"),
	LANDMARK_NOT_SETTED("");
	
	private String landMarkConditionAgregationTag;
	
	private LandmarkConditionAggregationType(String landMarkConditionAgregationTag) {
		this.landMarkConditionAgregationTag = landMarkConditionAgregationTag;
	}
	
	public String getLandMarkConditionAgregationTag() {
		return landMarkConditionAgregationTag;
	}
	
}
