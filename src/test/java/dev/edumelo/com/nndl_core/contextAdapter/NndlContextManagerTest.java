package dev.edumelo.com.nndl_core.contextAdapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import dev.edumelo.com.nndl_core.ExtractDataBind;
import dev.edumelo.com.nndl_core.exceptions.unchecked.NndlRuntimeException;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;
import dev.edumelo.com.nndl_core.webdriver.NndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.NndlElement;

class NndlContextManagerTest {

	@AfterEach
	void contextMustNotLeak() {
		assertThat(NndlContextManager.isContextBound()).isFalse();
	}

	@Test
	void actionLogsStayOnTheContextInOrder() {
		NndlContext context = NndlContext.of("flow");

		NndlContextManager.callWithContext(context, () -> {
			NndlContextManager.recordActionLog("Clicked on image post button");
			NndlContextManager.recordActionLog("   ");
			NndlContextManager.recordActionLog("File uploaded successfully: photo.png");
			return null;
		});

		assertThat(context.getLogs()).containsExactly(
				"Clicked on image post button",
				"File uploaded successfully: photo.png");
		assertThat(NndlContextManager.getLogs()).isEmpty();
	}

	@Test
	void contextIsVisibleInsideScopeOnly() {
		NndlContext context = NndlContext.of("myFlow");

		String name = NndlContextManager.callWithContext(context, NndlContextManager::retrieveNndlName);

		assertThat(name).isEqualTo("myFlow");
		assertThat(NndlContextManager.isContextBound()).isFalse();
		assertThat(NndlContextManager.retrieveNndlName()).isEmpty();
		assertThat(NndlContextManager.getWebDriverSessionId()).isNull();
	}

	@Test
	void contextIsNotVisibleWithoutBinding() {
		assertThatThrownBy(NndlContextManager::getAdapters)
				.isInstanceOf(NndlRuntimeException.class)
				.hasMessageContaining("No NNDL context is bound");
	}

	@Test
	void contextIsNotInheritedByPlainThreadsButCanBeRebound() throws Exception {
		NndlContext context = NndlContext.of("myFlow");
		AtomicReference<Boolean> unboundInChild = new AtomicReference<>();
		AtomicReference<String> nameInChild = new AtomicReference<>();

		NndlContextManager.runWithContext(context, () -> {
			Thread child = new Thread(() -> {
				unboundInChild.set(!NndlContextManager.isContextBound());
				NndlContextManager.runWithContext(context,
						() -> nameInChild.set(NndlContextManager.retrieveNndlName()));
			});
			child.start();
			try {
				child.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		});

		assertThat(unboundInChild.get()).isTrue();
		assertThat(nameInChild.get()).isEqualTo("myFlow");
	}

	@Test
	void contextIsReboundInCompletableFuture() {
		NndlContext context = NndlContext.of("myFlow");

		String name = NndlContextManager.callWithContext(context,
				() -> CompletableFuture.supplyAsync(
						() -> NndlContextManager.callWithContext(context, NndlContextManager::retrieveNndlName))
						.join());

		assertThat(name).isEqualTo("myFlow");
	}

	@Test
	void concurrentScopesKeepTheirOwnContext() {
		NndlContext first = NndlContext.of("first");
		NndlContext second = NndlContext.of("second");

		CompletableFuture<Void> firstRun = CompletableFuture.runAsync(() -> NndlContextManager
				.runWithContext(first, () -> NndlContextManager.addExtractedData(null, "firstAdapter", null)));
		CompletableFuture<Void> secondRun = CompletableFuture.runAsync(() -> NndlContextManager
				.runWithContext(second, () -> NndlContextManager.addExtractedData(null, "secondAdapter", null)));

		CompletableFuture.allOf(firstRun, secondRun).join();

		assertThat(first.getExtractedData()).containsOnlyKeys("firstAdapter");
		assertThat(second.getExtractedData()).containsOnlyKeys("secondAdapter");
	}

	@Test
	void extractedDataAccumulatesPerAdapter() {
		NndlContext context = NndlContext.of("myFlow", new StubExtractDataBindAdapter());

		NndlContextManager.runWithContext(context, () -> {
			NndlContextManager.addExtractedData(null, "adapter", null);
			NndlContextManager.addExtractedData(null, "adapter", null);
		});

		Map<String, List<ExtractDataBind>> extractedData = context.getExtractedData();
		assertThat(extractedData).containsOnlyKeys("adapter");
		assertThat(extractedData.get("adapter")).hasSize(2);
	}

	@Test
	void webDriverSessionIdIsStoredInTheCurrentContext() {
		NndlContext context = NndlContext.of("myFlow");

		NndlContextManager.runWithContext(context, () -> NndlContextManager.setWebDriverSessionId("session-1"));

		assertThat(context.getWebDriverSessionId()).isEqualTo("session-1");
		assertThat(NndlContextManager.getWebDriverSessionId()).isNull();
	}

	private static class StubExtractDataBindAdapter implements ExtractDataBindAdapter<StubExtractDataBind> {

		@Override
		public ExtractDataBind createFromElement(NndlWebDriver webDriver, NndlElement element) {
			return new StubExtractDataBind();
		}

	}

	private static class StubExtractDataBind implements ExtractDataBind {

		@Override
		public Advice suggestedAdvice() {
			return new ContinueAdvice();
		}

	}

}
