package dev.edumelo.com.nndl_core.action.landmark;

import java.util.List;
import java.util.Optional;

import dev.edumelo.com.nndl_core.nndl.NndlNode;
import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;

public class LandmarkElement extends StepElement implements Landmark {
	private static final String TAG = "element";
	private static final String TIMEOUT_TAG = "timeout";
	private static final Integer DEFAULT_TIMEOUT = 50;
	private Integer timeout;
	private LandmarkStrategies strategy;
	
	public LandmarkElement(StepElement element, NndlNode landMarkNode) {
		//track1
		super(element);
		setName(element.getName());
		setMatchExp(element.getMatchExp());
		this.timeout = extractTimeout(landMarkNode);
		this.strategy = extractStrategy(landMarkNode);
	}

	@Override
	public Advice getLandMarkAdvice() {
		return new ContinueAdvice();
	}
	
	@Override
	public LandmarkStrategies getStrategy() {
		return strategy;
	}

	public void setStrategy(LandmarkStrategies strategy) {
		this.strategy = strategy;
	}

	@Override
	public Integer getTimeout() {
		return timeout;
	}
	
	private Integer extractTimeout(NndlNode mappedForkElement) {
		return mappedForkElement.getScalarValueFromChild(TIMEOUT_TAG, Integer.class).orElse(DEFAULT_TIMEOUT);
	}
	
	private LandmarkStrategies extractStrategy(NndlNode landMarkNode) {
		LandmarkAchievementStrategy[] strategies = landMarkNode.getValueFromChild(LandmarkStrategies.WAIT_CONDITION_TAG)
				.flatMap(NndlNode::getListedValues)
				.stream()
				.flatMap(List::stream)
				.map(NndlNode::getScalarValue)
				.flatMap(Optional::stream)
				.map(LandmarkAchievementStrategy::getFromTag)
				.toArray(LandmarkAchievementStrategy[]::new);
		return new LandmarkStrategies(strategies);
	}
	
	public static String getTag() {
		return TAG;
	}
}
