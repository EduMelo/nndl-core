package dev.edumelo.com.nndl_core.nndl.dhall;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.Test;

import dev.edumelo.com.nndl_core.exceptions.unchecked.NndlParserRuntimeException;
import dev.edumelo.com.nndl_core.exceptions.unchecked.NndlRuntimeException;
import dev.edumelo.com.nndl_core.nndl.Nndl;
import dev.edumelo.com.nndl_core.step.Step;
import dev.edumelo.com.nndl_core.webdriver.SeleniumHubProperties;

class DhallNndlLoaderTest {

	@Test
	void missingBinaryExplainsHowToInstall() {
		DhallToJson dhallToJson = new DhallToJson("dhall-to-json-not-installed");

		assertThatThrownBy(() -> dhallToJson.convert(Path.of("nndl_examples/test.dhall")))
				.isInstanceOf(NndlRuntimeException.class)
				.hasMessageContaining("dhall-json");
	}

	@Test
	void loadsTheCurrencyExample() {
		Nndl nndl = Nndl.loadDhall(Path.of("nndl_examples/test.dhall"));
		nndl.loadSteps(Map.of());
		Step main = nndl.getStepMap(new SeleniumHubProperties()).get("main");

		assertThat(nndl.getEntryStep()).isEqualTo("main");
		assertThat(nndl.getMissingStepNames()).isEmpty();
		assertThat(main.getElements().get("dolar").getMatchExp()).isEqualTo("#root h2");
		assertThat(main.getActions()).hasSize(2);
		assertThat(main.getActions().get(0).getTag()).isEqualTo("gotoUrl");
		assertThat(main.getActions().get(1).getTag()).isEqualTo("extract");
	}

	@Test
	void rejectsAnOrderField() {
		Nndl nndl = new Nndl();
		nndl.setName("recipe");
		nndl.setValue("""
				entryStep: main
				steps:
				  - name: main
				    actions:
				      - order: 2
				        gotoUrl: https://second.example
				      - gotoUrl: https://first.example
				""");
		nndl.loadNndlMap();
		nndl.loadSteps(Map.of());

		assertThatThrownBy(() -> nndl.getStepMap(new SeleniumHubProperties()))
				.isInstanceOf(NndlParserRuntimeException.class)
				.hasMessageContaining("order");
	}

	@Test
	void loadsTheScrollFormAndTransferExamples() {
		Nndl scroll = load("nndl_examples/scroll.dhall");
		scroll.loadSteps(Map.of());
		Step results = scroll.getStepMap(new SeleniumHubProperties()).get("results");

		assertThat(scroll.getEntryStep()).isEqualTo("results");
		assertThat(scroll.getMissingStepNames()).isEmpty();
		assertThat(results.getActions()).extracting(action -> action.getTag())
				.containsExactly("gotoUrl", "loop");
		assertThat(results.getSubSteps().get("readCard").getActions()).hasSize(2);

		Nndl editor = load("nndl_examples/editor.dhall");
		editor.loadSteps(Map.of("query", "notebook"));
		Step search = editor.getStepMap(new SeleniumHubProperties()).get("search");

		assertThat(editor.getMissingStepNames()).isEmpty();
		assertThat(search.getActions()).extracting(action -> action.getTag())
				.containsExactly("gotoUrl", "input", "input", "sendKey", "hover", "elementMark",
						"elementPaint", "elementClick");
		assertThat(search.getActions().get(2).toString()).contains("value=notebook");

		Nndl transfer = load("nndl_examples/transfer.dhall");
		transfer.loadSteps(Map.of());
		SeleniumHubProperties hub = new SeleniumHubProperties();

		assertThat(transfer.getEntryStep()).isEqualTo("fetch");
		assertThat(transfer.getMissingStepNames()).isEmpty();
		assertThat(transfer.getStepMap(hub)).containsKeys("fetch", "done", "retry").doesNotContainKey("watch");
		assertThat(transfer.getAsyncStepMap(hub)).containsOnlyKeys("watch");
		assertThat(transfer.getStepMap(hub).get("fetch").getActions())
				.extracting(action -> action.getTag())
				.containsExactly("gotoUrl", "downloadFile", "uploadFile", "actionTriggerer", "elementClick");
	}

	private static Nndl load(String path) {
		return Nndl.loadDhall(Path.of(path));
	}

	@Test
	void substitutesLoginVariablesAndReportsTheMissingFork() {
		Nndl nndl = Nndl.loadDhall(Path.of("nndl_examples/test2.dhall"));
		assertThat(nndl.getMissingStepNames()).contains("loginSuccessfull");

		nndl.loadSteps(Map.of("userHandle", "ada", "userPassword", "secret"));
		Step login = nndl.getStepMap(new SeleniumHubProperties()).get("login");

		assertThat(login.getActions().get(1).toString()).contains("value=ada").doesNotContain("${userHandle}");
		assertThat(login.getActions().get(2).toString()).contains("value=secret").doesNotContain("${userPassword}");
	}
}
