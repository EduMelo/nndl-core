package dev.edumelo.com.nndl_core.nndl.dhall;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import dev.edumelo.com.nndl_core.exceptions.unchecked.NndlRuntimeException;

/**
 * Rewrites the JSON emitted by {@code dhall-to-json} into the loose map the
 * action classes already read. That binary drops union tags, so an action
 * kind is the payload record and {@code type} selects the legacy shape. A
 * landmark list is a fork when an entry has {@code fork}; otherwise it is a
 * conjunction, and each entry is a cooldown when it has {@code millis} or an
 * element landmark when it has {@code element}.
 */
public final class LegacyNndlEncoder {
	public EncodedNndl encode(Object json) {
		Map<String, Object> root = object(json, "recipe");
		Map<String, Object> legacy = new LinkedHashMap<>();
		putText(legacy, "entryStep", root.get("entryStep"));
		putList(legacy, "asynchronousSteps", texts(root.get("asynchronousSteps")));
		legacy.put("steps", steps(root.get("steps"), true));
		return new EncodedNndl(dump(legacy), missingStepNames(root));
	}

	private List<Map<String, Object>> steps(Object value, boolean withSubSteps) {
		List<Map<String, Object>> encoded = new ArrayList<>();
		if (value == null) {
			return encoded;
		}
		for (Object step : array(value, "steps")) {
			encoded.add(step(object(step, "step"), withSubSteps));
		}
		return encoded;
	}

	private Map<String, Object> step(Map<String, Object> step, boolean withSubSteps) {
		Map<String, Object> encoded = new LinkedHashMap<>();
		encoded.put("name", text(step.get("name"), "step.name"));
		putList(encoded, "elements", elements(step.get("elements")));
		encoded.put("actions", actions(step.get("actions")));
		if (withSubSteps) {
			List<Map<String, Object>> subSteps = steps(step.get("subSteps"), false);
			putList(encoded, "subSteps", subSteps);
		}
		return encoded;
	}

	private List<Map<String, Object>> elements(Object value) {
		List<Map<String, Object>> encoded = new ArrayList<>();
		if (value == null) {
			return encoded;
		}
		for (Object element : array(value, "elements")) {
			Map<String, Object> source = object(element, "element");
			Map<String, Object> legacy = new LinkedHashMap<>();
			legacy.put("name", text(source.get("name"), "element.name"));
			legacy.put("matchExp", text(source.get("matchExp"), "element.matchExp"));
			putTrue(legacy, "ignoreRoot", source.get("ignoreRoot"));
			putList(legacy, "shadowPath", texts(source.get("shadowPath")));
			encoded.add(legacy);
		}
		return encoded;
	}

	private List<Map<String, Object>> actions(Object value) {
		List<Map<String, Object>> encoded = new ArrayList<>();
		if (value == null) {
			return encoded;
		}
		for (Object action : array(value, "actions")) {
			encoded.add(action(object(action, "action")));
		}
		return encoded;
	}

	private Map<String, Object> action(Map<String, Object> action) {
		if (action.containsKey("order")) {
			throw new NndlRuntimeException("Actions run in the order they are listed. "
					+ "The order field is not part of the recipe.");
		}
		Map<String, Object> encoded = new LinkedHashMap<>();
		Map<String, Object> payload = object(action.get("kind"), "action.kind");
		String type = text(payload.get("type"), "action.kind.type");
		Map<String, Object> landmarkTarget = encoded;
		switch (type) {
			case "gotoUrl":
				encoded.put("gotoUrl", text(payload.get("url"), "gotoUrl.url"));
				break;
			case "storeCookies":
				landmarkTarget = nested(encoded, "storeCookies");
				landmarkTarget.put("storer", text(payload.get("storer"), "storeCookies.storer"));
				landmarkTarget.put("storerParams", texts(payload.get("storerParams")));
				break;
			case "loadCookies":
				Map<String, Object> loadCookies = nested(encoded, "loadCookies");
				loadCookies.put("retriever", text(payload.get("retriever"), "loadCookies.retriever"));
				loadCookies.put("retrieverParams", texts(payload.get("retrieverParams")));
				break;
			case "fillInput":
				encoded.put("fillInput", Boolean.TRUE);
				encoded.put("input", text(payload.get("element"), "fillInput.element"));
				encoded.put("value", text(payload.get("value"), "fillInput.value"));
				break;
			case "clearInput":
				encoded.put("clearInput", Boolean.TRUE);
				encoded.put("input", text(payload.get("element"), "clearInput.element"));
				break;
			case "elementClick":
				encoded.put("elementClick", text(payload.get("element"), "elementClick.element"));
				break;
			case "hover":
				encoded.put("hover", text(payload.get("element"), "hover.element"));
				break;
			case "sendKey":
				encoded.put("sendKey", text(payload.get("key"), "sendKey.key"));
				putText(encoded, "targetElement", payload.get("targetElement"));
				putTrue(encoded, "ignoreRoot", payload.get("ignoreRoot"));
				break;
			case "loop":
				encodeLoop(encoded, payload);
				break;
			case "extract":
				encoded.put("extract", text(payload.get("factory"), "extract.factory"));
				putText(encoded, "targetElement", payload.get("targetElement"));
				putTrue(encoded, "fromClipboard", payload.get("fromClipboard"));
				break;
			case "elementPaint":
				encoded.put("elementPaint", text(payload.get("element"), "elementPaint.element"));
				encoded.put("color", text(payload.get("color"), "elementPaint.color"));
				putTrue(encoded, "ignoreRoot", payload.get("ignoreRoot"));
				break;
			case "elementMark":
				encoded.put("elementMark", text(payload.get("element"), "elementMark.element"));
				encoded.put("mark", text(payload.get("mark"), "elementMark.mark"));
				putTrue(encoded, "ignoreRoot", payload.get("ignoreRoot"));
				break;
			case "actionTriggerer":
				Map<String, Object> trigger = nested(encoded, "actionTriggerer");
				trigger.put("id", text(payload.get("id"), "actionTriggerer.id"));
				trigger.put("triggerParam", texts(payload.get("triggerParam")));
				break;
			case "downloadFile":
				encoded.put("downloadFile", Boolean.TRUE);
				encoded.put("url", text(payload.get("url"), "downloadFile.url"));
				break;
			case "uploadFile":
				encoded.put("uploadFile", Boolean.TRUE);
				encoded.put("input", text(payload.get("input"), "uploadFile.input"));
				encoded.put("url", text(payload.get("url"), "uploadFile.url"));
				break;
			default:
				throw new NndlRuntimeException("Unknown action kind: " + type);
		}
		putNumber(encoded, "timeout", action.get("timeout"));
		putList(encoded, "waitCondition", texts(action.get("waitCondition")));
		putLandmarks(landmarkTarget, action.get("landmarks"));
		putRequirement(encoded, action.get("requirement"));
		putNumber(encoded, "onEach", action.get("onEach"));
		putTrue(encoded, "limitRequirement", action.get("limitRequirement"));
		putPosition(encoded, "positionBefore", action.get("positionBefore"));
		putPosition(encoded, "positionAfter", action.get("positionAfter"));
		putCondition(encoded, action.get("condition"));
		return encoded;
	}

	private void encodeLoop(Map<String, Object> encoded, Map<String, Object> payload) {
		Object observer = payload.get("observerClass");
		encoded.put("loop", observer == null ? "" : text(observer, "Loop.observerClass"));
		encoded.put("execute", text(payload.get("execute"), "Loop.execute"));
		encoded.put("iterationScope", iterationScope(payload.get("iterationScope")));
		putText(encoded, "fill", payload.get("fill"));
		putText(encoded, "executeConditions", payload.get("executeConditions"));
		if (Boolean.FALSE.equals(payload.get("autoScroll"))) {
			encoded.put("autoScroll", Boolean.FALSE);
		}
		putNumber(encoded, "scrollCount", payload.get("scrollCount"));
		putNumber(encoded, "limit", payload.get("limit"));
		putTrue(encoded, "ignoreMaxLoopCountException", payload.get("ignoreMaxLoopCountException"));
		if (payload.get("throwTimeout") instanceof Boolean throwTimeout) {
			encoded.put("throwTimeout", throwTimeout);
		}
		putNumber(encoded, "timeoutWait", payload.get("timeoutWait"));
	}

	private Map<String, Object> iterationScope(Object value) {
		Map<String, Object> payload = object(value, "iterationScope");
		Map<String, Object> encoded = new LinkedHashMap<>();
		if (payload.containsKey("values")) {
			encoded.put("type", "list");
			encoded.put("list", texts(payload.get("values")));
			return encoded;
		}
		if (payload.containsKey("element")) {
			encoded.put("type", "pageElement");
			encoded.put("pageElement", text(payload.get("element"), "iterationScope.element"));
			return encoded;
		}
		throw new NndlRuntimeException("iterationScope must name an element or a list of values.");
	}

	private void putLandmarks(Map<String, Object> target, Object value) {
		if (value == null) {
			return;
		}
		List<Object> items = array(value, "landmarks");
		if (items.isEmpty()) {
			return;
		}
		if (forks(items)) {
			target.put("forkElement", forkList(items));
		} else {
			target.put("landmarkConditions", landmarkList(items));
		}
	}

	private boolean forks(List<Object> items) {
		for (Object item : items) {
			if (object(item, "landmark").containsKey("fork")) {
				return true;
			}
		}
		return false;
	}

	private List<Map<String, Object>> landmarkList(Object value) {
		List<Map<String, Object>> encoded = new ArrayList<>();
		for (Object landmark : array(value, "landmarks")) {
			Map<String, Object> payload = object(landmark, "landmark");
			Map<String, Object> legacy = new LinkedHashMap<>();
			if (payload.containsKey("millis") && !payload.containsKey("element")) {
				legacy.put("cooldown", number(payload.get("millis"), "cooldown.millis"));
			} else if (payload.containsKey("element")) {
				legacy.put("element", text(payload.get("element"), "landmark.element"));
				putNumber(legacy, "timeout", payload.get("timeout"));
				putList(legacy, "waitCondition", texts(payload.get("waitCondition")));
			} else {
				throw new NndlRuntimeException("A landmark needs an element or a cooldown.");
			}
			encoded.add(legacy);
		}
		return encoded;
	}

	private List<Map<String, Object>> forkList(Object value) {
		List<Map<String, Object>> encoded = new ArrayList<>();
		for (Object fork : array(value, "forks")) {
			Map<String, Object> payload = object(fork, "fork");
			Map<String, Object> legacy = new LinkedHashMap<>();
			legacy.put("element", text(payload.get("element"), "fork.element"));
			legacy.put("fork", text(payload.get("fork"), "fork.fork"));
			putNumber(legacy, "timeout", payload.get("timeout"));
			putList(legacy, "waitCondition", texts(payload.get("waitCondition")));
			encoded.add(legacy);
		}
		return encoded;
	}

	private void putRequirement(Map<String, Object> target, Object value) {
		if (value == null) {
			return;
		}
		Map<String, Object> source = object(value, "requirement");
		Map<String, Object> requirement = new LinkedHashMap<>();
		requirement.put("type", text(source.get("type"), "requirement.type"));
		putNumber(requirement, "retries", source.get("retries"));
		putText(requirement, "stepTreatment", source.get("stepTreatment"));
		putNumber(requirement, "restartCount", source.get("restartCount"));
		putText(requirement, "fallback", source.get("fallback"));
		target.put("requirementStatus", requirement);
	}

	private void putPosition(Map<String, Object> target, String key, Object value) {
		if (value == null) {
			return;
		}
		Map<String, Object> source = object(value, key);
		Map<String, Object> position = new LinkedHashMap<>();
		putNumber(position, "x", source.get("x"));
		putNumber(position, "y", source.get("y"));
		if (!position.isEmpty()) {
			target.put(key, position);
		}
	}

	private void putCondition(Map<String, Object> target, Object value) {
		if (value == null) {
			return;
		}
		Map<String, Object> source = object(value, "condition");
		Map<String, Object> condition = new LinkedHashMap<>();
		condition.put("class", text(source.get("className"), "condition.className"));
		target.put("condition", condition);
	}

	private List<String> missingStepNames(Map<String, Object> root) {
		List<Object> steps = root.get("steps") == null ? List.of() : array(root.get("steps"), "steps");
		Set<String> names = stepNames(steps);
		List<String> missing = new ArrayList<>();
		if (root.get("entryStep") != null && !names.contains(text(root.get("entryStep"), "entryStep"))) {
			missing.add(text(root.get("entryStep"), "entryStep"));
		}
		collectMissing(steps, names, missing);
		return List.copyOf(new LinkedHashSet<>(missing));
	}

	private void collectMissing(List<Object> steps, Set<String> siblingNames, List<String> missing) {
		for (Object stepValue : steps) {
			Map<String, Object> step = object(stepValue, "step");
			List<Object> subSteps = step.get("subSteps") == null ? List.of() : array(step.get("subSteps"), "subSteps");
			Set<String> subNames = stepNames(subSteps);
			Object actions = step.get("actions");
			if (actions != null) {
				for (Object actionValue : array(actions, "actions")) {
					Map<String, Object> action = object(actionValue, "action");
					collectForks(action.get("landmarks"), siblingNames, missing);
					collectLoop(action.get("kind"), subNames, missing);
				}
			}
			if (!subSteps.isEmpty()) {
				collectMissing(subSteps, subNames, missing);
			}
		}
	}

	private void collectForks(Object landmarks, Set<String> siblingNames, List<String> missing) {
		if (landmarks == null) {
			return;
		}
		for (Object landmark : array(landmarks, "landmarks")) {
			Map<String, Object> payload = object(landmark, "landmark");
			if (!payload.containsKey("fork")) {
				continue;
			}
			String target = text(payload.get("fork"), "fork.fork");
			if (!siblingNames.contains(target)) {
				missing.add(target);
			}
		}
	}

	private void collectLoop(Object kind, Set<String> subNames, List<String> missing) {
		if (kind == null) {
			return;
		}
		Map<String, Object> payload = object(kind, "action.kind");
		if (!"loop".equals(payload.get("type"))) {
			return;
		}
		String execute = text(payload.get("execute"), "loop.execute");
		if (!subNames.contains(execute)) {
			missing.add(execute);
		}
	}

	private Set<String> stepNames(List<Object> steps) {
		Set<String> names = new LinkedHashSet<>();
		for (Object step : steps) {
			names.add(text(object(step, "step").get("name"), "step.name"));
		}
		return names;
	}

	private Map<String, Object> nested(Map<String, Object> parent, String key) {
		Map<String, Object> nested = new LinkedHashMap<>();
		parent.put(key, nested);
		return nested;
	}

	private void putText(Map<String, Object> target, String key, Object value) {
		if (value != null) {
			target.put(key, text(value, key));
		}
	}

	private void putNumber(Map<String, Object> target, String key, Object value) {
		if (value != null) {
			target.put(key, number(value, key));
		}
	}

	private void putTrue(Map<String, Object> target, String key, Object value) {
		if (Boolean.TRUE.equals(value)) {
			target.put(key, Boolean.TRUE);
		}
	}

	private void putList(Map<String, Object> target, String key, List<?> value) {
		if (value != null && !value.isEmpty()) {
			target.put(key, value);
		}
	}

	private List<String> texts(Object value) {
		List<String> texts = new ArrayList<>();
		if (value == null) {
			return texts;
		}
		for (Object item : array(value, "list")) {
			texts.add(text(item, "text"));
		}
		return texts;
	}

	private String dump(Map<String, Object> root) {
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		options.setPrettyFlow(true);
		return new Yaml(options).dump(root);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> object(Object value, String where) {
		if (value instanceof Map<?, ?> map) {
			return (Map<String, Object>) map;
		}
		throw new NndlRuntimeException(where + " must be an object.");
	}

	@SuppressWarnings("unchecked")
	private List<Object> array(Object value, String where) {
		if (value instanceof List<?> list) {
			return (List<Object>) list;
		}
		throw new NndlRuntimeException(where + " must be a list.");
	}

	private String text(Object value, String where) {
		if (value instanceof String text) {
			return text;
		}
		throw new NndlRuntimeException(where + " must be text.");
	}

	private Number number(Object value, String where) {
		if (value instanceof Number number) {
			if (number instanceof Double || number instanceof Float) {
				return number;
			}
			long exact = number.longValue();
			if (exact >= Integer.MIN_VALUE && exact <= Integer.MAX_VALUE) {
				return Integer.valueOf((int) exact);
			}
			return Long.valueOf(exact);
		}
		throw new NndlRuntimeException(where + " must be a number.");
	}
}
