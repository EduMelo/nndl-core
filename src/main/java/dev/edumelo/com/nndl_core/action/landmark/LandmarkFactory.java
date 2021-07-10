package dev.edumelo.com.nndl_core.action.landmark;

import java.util.Arrays;
import java.util.Map;

import dev.edumelo.com.nndl_core.step.StepElement;

//XXX Retornar
//@Slf4j
public class LandmarkFactory {

	public static Landmark createLandmark(Map<String, ?> landmarkMap, Map<String, StepElement> mappedElements, 
			LandmarkConditionAggregationType landmarkConditionAggregationType) {
		LandmarkEnum type = getType(landmarkMap);
		
		switch (type) {
		case ELEMENT:
			String elementName = (String) landmarkMap.get(LandmarkElement.getTag());
			StepElement element = mappedElements.get(elementName);
			if(landmarkConditionAggregationType.equals(LandmarkConditionAggregationType.CONJUNCTION)) {
				return new LandmarkElement(element, landmarkMap);				
			} else {
				return new ForkElement(mappedElements, landmarkMap);
			}
		case COOLDOWN:
			Integer cooldown = (Integer) landmarkMap.get(LandmarkCoolDown.getTag());
			return new LandmarkCoolDown(cooldown);
		default:
			break;
		}
		return null;
	}

	private static LandmarkEnum getType(Map<String, ?> landmarkMap) {
		//XXX Retornar
//		log.debug(String.format("getType. LandmarkMap: {}", landmarkMap));
		
		return Arrays.asList(LandmarkEnum.values()).stream()
				.filter(l -> landmarkMap.containsKey(l.getTag()))
				.findFirst()
				.orElseThrow();
	}
	
}
