package dev.edumelo.com.nndl_core.action.landmark;

import java.util.Arrays;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.edumelo.com.nndl_core.exceptions.unchecked.NndlParserRuntimeException;
import dev.edumelo.com.nndl_core.nndl.NndlNode;
import dev.edumelo.com.nndl_core.step.StepElement;

public class LandmarkFactory {
	
	public static final Logger log = LoggerFactory.getLogger(Landmark.class);
	
	public static Landmark createLandmark(NndlNode landmarkMap, Map<String, StepElement> mappedElements, 
			LandmarkConditionAggregationType landmarkConditionAggregationType) {
		LandmarkEnum type = getType(landmarkMap);
		
		switch (type) {
		case ELEMENT:
			NndlNode elementNode = landmarkMap.getValueFromChild(LandmarkElement.getTag())
			.orElseThrow(() -> new NndlParserRuntimeException("Landkmark tag should have "+LandmarkElement.getTag()
				+" tag", landmarkMap));
			String elementName = elementNode.getScalarValue()
					.orElseThrow(() -> new NndlParserRuntimeException("Element tag should have name tag",
							landmarkMap));;
			StepElement element = mappedElements.get(elementName);
			if(landmarkConditionAggregationType.equals(LandmarkConditionAggregationType.CONJUNCTION)) {
				//track1
				return new LandmarkElement(element, landmarkMap);				
			} else {
				return new ForkElement(mappedElements, landmarkMap);
			}
		case COOLDOWN:
			Integer cooldown = landmarkMap.getScalarValueFromChild(LandmarkCoolDown.getTag(), Integer.class)
				.orElse(null);
			return new LandmarkCoolDown(cooldown);
		default:
			break;
		}
		return null;
	}

	private static LandmarkEnum getType(NndlNode landmarkMap) {
		log.debug(String.format("getType. LandmarkMap: {}", landmarkMap));
		
		return Arrays.asList(LandmarkEnum.values()).stream()
				.filter(l -> landmarkMap.hasValueFromChild(l.getTag()))
				.findFirst()
				.orElseThrow();
	}
	
}
