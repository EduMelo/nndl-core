package dev.edumelo.com.nndl_core.nndl.dhall;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import dev.edumelo.com.nndl_core.exceptions.unchecked.NndlRuntimeException;
import dev.edumelo.com.nndl_core.nndl.NndlNode;
import dev.edumelo.com.nndl_core.nndl.YamlNndlLoader;

class LegacyNndlEncoderTest {

	@Test
	void encodesTheShapesEachActionClassReads() {
		EncodedNndl encoded = new LegacyNndlEncoder().encode(Json.parse(recipe()));
		String yaml = encoded.yaml();

		assertThat(yaml).contains("entryStep").contains("#root h2").contains("targetElement");
		assertThat(yaml).doesNotContain("targertElement");
		assertThat(yaml).doesNotContain("limitRequirement").doesNotContain("fromClipboard")
				.doesNotContain("ignoreRoot").doesNotContain("waitCondition").doesNotContain("order");

		NndlNode root = new YamlNndlLoader().load("recipe", yaml);
		root.setVariableSubstitutionMap(Map.of());
		List<NndlNode> steps = root.getValueFromChild("steps").orElseThrow().getListedValues().orElseThrow();
		NndlNode dolar = steps.get(0).getValueFromChild("elements").orElseThrow().getListedValues().orElseThrow().get(0);
		assertThat(dolar.getScalarValueFromChild("matchExp")).contains("#root h2");

		NndlNode extract = action(steps.get(0), 1);
		assertThat(extract.getScalarValueFromChild("targetElement")).contains("dolar");
		assertThat(extract.hasValueFromChild("targertElement")).isFalse();

		NndlNode fill = action(steps.get(1), 0);
		assertThat(fill.getScalarValueFromChild("fillInput")).contains("true");
		assertThat(fill.getScalarValueFromChild("input")).contains("inputLogin");
		assertThat(fill.getScalarValueFromChild("value")).contains("${userHandle}");

		NndlNode storeAction = action(steps.get(2), 0);
		NndlNode store = storeAction.getValueFromChild("storeCookies").orElseThrow();
		assertThat(store.getListedValuesFromChild("landmarkConditions")).isPresent();
		assertThat(storeAction.getValueFromChild("landmarkConditions")).isEmpty();

		NndlNode loadAction = action(steps.get(3), 0);
		NndlNode load = loadAction.getValueFromChild("loadCookies").orElseThrow();
		assertThat(load.getValueFromChild("landmarkConditions")).isEmpty();
		assertThat(loadAction.getListedValuesFromChild("landmarkConditions")).isPresent();
	}

	@Test
	void rejectsAnOrderField() {
		String json = """
				{"steps":[{"name":"main","actions":[
				  {"order":1,"kind":{"type":"gotoUrl","url":"https://example.test"}}
				]}]}
				""";

		assertThatThrownBy(() -> new LegacyNndlEncoder().encode(Json.parse(json)))
				.isInstanceOf(NndlRuntimeException.class)
				.hasMessageContaining("order");
	}

	@Test
	void keepsTheLogBesideTheAction() {
		String json = """
				{"steps":[{"name":"oldPoster","actions":[
				  {"kind":{"type":"elementClick","element":"imagePost"},
				   "log":"Clicked on image post button"},
				  {"kind":{"type":"uploadFile","input":"fileInput","url":"${img}"},
				   "log":"File uploaded successfully: ${img}"}
				]}]}
				""";

		String yaml = new LegacyNndlEncoder().encode(Json.parse(json)).yaml();

		assertThat(yaml).contains("elementClick: imagePost")
				.contains("log: Clicked on image post button")
				.contains("uploadFile: true")
				.contains("input: fileInput")
				.contains("File uploaded successfully: ${img}");
	}

	@Test
	void reportsForkToAnUnknownStep() {
		String json = """
				{"entryStep":"main","steps":[{"name":"main","actions":[
				  {"kind":{"type":"gotoUrl","url":"https://example.test"},
				   "landmarks":[{"element":"a","fork":"loginSuccessfull"}]}
				]}]}
				""";

		EncodedNndl encoded = new LegacyNndlEncoder().encode(Json.parse(json));

		assertThat(encoded.missingStepNames()).containsExactly("loginSuccessfull");
	}

	@Test
	void reportsLoopExecuteToAnUnknownSubstep() {
		String json = """
				{"steps":[{"name":"main","subSteps":[{"name":"row"}],"actions":[
				  {"kind":{"type":"loop","execute":"missingChild",
				    "iterationScope":{"element":"row"},"autoScroll":true,
				    "ignoreMaxLoopCountException":false}}
				]}]}
				""";

		EncodedNndl encoded = new LegacyNndlEncoder().encode(Json.parse(json));

		assertThat(encoded.missingStepNames()).containsExactly("missingChild");
		assertThat(encoded.yaml()).contains("type: pageElement").contains("pageElement: row")
				.contains("execute: missingChild").doesNotContain("autoScroll");
	}

	@Test
	void distinguishesActionsThatSharePayloadFields() {
		String json = """
				{"steps":[{"name":"main","actions":[
				  {"kind":{"type":"downloadFile","url":"https://files.test"}},
				  {"kind":{"type":"elementClick","element":"submitButton"}},
				  {"kind":{"type":"hover","element":"menu"}},
				  {"kind":{"type":"clearInput","element":"search"}}
				]}]}
				""";

		String yaml = new LegacyNndlEncoder().encode(Json.parse(json)).yaml();

		assertThat(yaml).contains("downloadFile: true").contains("url: https://files.test")
				.contains("elementClick: submitButton").contains("hover: menu")
				.contains("clearInput: true").contains("input: search")
				.doesNotContain("gotoUrl");
	}

	private static NndlNode action(NndlNode step, int index) {
		return step.getValueFromChild("actions").orElseThrow().getListedValues().orElseThrow().get(index);
	}

	private static String recipe() {
		return """
				{
				  "entryStep": "main",
				  "asynchronousSteps": [],
				  "steps": [
				    {
				      "name": "main",
				      "elements": [
				        {"name": "dolar", "matchExp": "#root h2", "ignoreRoot": false, "shadowPath": []}
				      ],
				      "actions": [
				        {
				          "kind": {"type": "gotoUrl", "url": "https://example.test"},
				          "landmarks": [{"element": "dolar", "waitCondition": []}],
				          "limitRequirement": false,
				          "waitCondition": []
				        },
				        {
				          "kind": {"type": "extract", "factory": "dev.example.ValueFactory", "targetElement": "dolar", "fromClipboard": false},
				          "limitRequirement": false,
				          "waitCondition": []
				        }
				      ]
				    },
				    {
				      "name": "fill",
				      "actions": [
				        {
				          "kind": {"type": "fillInput", "element": "inputLogin", "value": "${userHandle}"}
				        }
				      ]
				    },
				    {
				      "name": "store",
				      "actions": [
				        {
				          "kind": {
				            "type": "storeCookies", "storer": "dev.example.Storer", "storerParams": ["${userHandle}"]
				          },
				          "landmarks": [{"element": "loginMark", "waitCondition": []}]
				        }
				      ]
				    },
				    {
				      "name": "load",
				      "actions": [
				        {
				          "kind": {
				            "type": "loadCookies", "retriever": "dev.example.Retriever", "retrieverParams": ["${userHandle}"]
				          },
				          "landmarks": [{"millis": 15000}]
				        }
				      ]
				    }
				  ]
				}
				""";
	}
}
