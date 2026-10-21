package dev.edumelo.com.nndl_core;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import dev.edumelo.com.nndl_core.contextAdapter.NndlContextManager;
import dev.edumelo.com.nndl_core.nndl.Nndl;
import dev.edumelo.com.nndl_core.webdriver.NndlBrowserSession;
import dev.edumelo.com.nndl_core.webdriver.NndlCookie;
import dev.edumelo.com.nndl_core.webdriver.NndlElement;
import dev.edumelo.com.nndl_core.webdriver.NndlElementCondition;
import dev.edumelo.com.nndl_core.webdriver.NndlElementsCondition;
import dev.edumelo.com.nndl_core.webdriver.NndlLocator;
import dev.edumelo.com.nndl_core.webdriver.NndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.NndlWebDriverWaiter;
import dev.edumelo.com.nndl_core.webdriver.SeleniumHubProperties;

class NndlRunnerAsyncTest {

	private static final String MAIN_URL = "https://files.example/inbox";
	private static final String WATCH_URL = "https://files.example/status";
	private static final String WATCH_AGAIN_URL = "https://files.example/status/again";
	private static final String PING_URL = "https://files.example/ping";

	@Test
	void asynchronousStepUsesItsOwnSessionAndFinishesBeforeQuit() throws Exception {
		CountDownLatch watchEntered = new CountDownLatch(1);
		CountDownLatch mainMayReleaseWatch = new CountDownLatch(1);
		List<String> events = new CopyOnWriteArrayList<>();
		ScriptedSession session = new ScriptedSession(events, watchEntered, mainMayReleaseWatch);

		run("""
				entryStep: main
				asynchronousSteps:
				  - watch
				steps:
				  - name: main
				    actions:
				      - gotoUrl: "%s"
				  - name: watch
				    actions:
				      - gotoUrl: "%s"
				      - gotoUrl: "%s"
				""".formatted(MAIN_URL, WATCH_URL, WATCH_AGAIN_URL), session);

		RecordingDriver main = session.opened.get(0);
		RecordingDriver watch = session.opened.get(1);
		assertThat(session.opened).hasSize(2);
		assertThat(watch.urls).containsExactly(WATCH_URL, WATCH_AGAIN_URL);
		assertThat(main.urls).containsExactly(MAIN_URL);
		assertThat(watch.actionThread).isNotSameAs(Thread.currentThread());
		assertThat(watch.quitThread).isSameAs(Thread.currentThread());
		assertThat(main.quitThread).isSameAs(Thread.currentThread());
		assertThat(watch.quitCount).isEqualTo(1);
		assertThat(main.quitCount).isEqualTo(1);
		assertThat(events).containsSubsequence(
				"watch blocked",
				"main visited",
				"watch released",
				"async-1 quit",
				"main quit");
	}

	@Test
	void eachAsynchronousStepOpensASeparateSession() {
		List<String> events = new CopyOnWriteArrayList<>();
		ScriptedSession session = new ScriptedSession(events, new CountDownLatch(0), new CountDownLatch(0));

		run("""
				entryStep: main
				asynchronousSteps:
				  - watch
				  - ping
				steps:
				  - name: main
				    actions:
				      - gotoUrl: "%s"
				  - name: watch
				    actions:
				      - gotoUrl: "%s"
				  - name: ping
				    actions:
				      - gotoUrl: "%s"
				""".formatted(MAIN_URL, WATCH_URL, PING_URL), session);

		assertThat(session.opened).hasSize(3);
		RecordingDriver main = session.driverFor(MAIN_URL);
		RecordingDriver watch = session.driverFor(WATCH_URL);
		RecordingDriver ping = session.driverFor(PING_URL);
		assertThat(main).isNotSameAs(watch).isNotSameAs(ping);
		assertThat(watch).isNotSameAs(ping);
		assertThat(session.opened).allSatisfy(driver -> assertThat(driver.quitCount).isEqualTo(1));
	}

	private static void run(String yaml, ScriptedSession session) {
		Nndl nndl = new Nndl();
		nndl.setName("async");
		nndl.setValue(yaml);
		nndl.loadNndlMap();
		new NndlRunner(session).runFromStack(nndl);
	}

	private static final class ScriptedSession implements NndlBrowserSession {
		private final SeleniumHubProperties properties = new SeleniumHubProperties();
		private final List<RecordingDriver> opened = new ArrayList<>();
		private final List<String> events;
		private final CountDownLatch watchEntered;
		private final CountDownLatch mainMayReleaseWatch;

		private ScriptedSession(List<String> events, CountDownLatch watchEntered,
				CountDownLatch mainMayReleaseWatch) {
			this.events = events;
			this.watchEntered = watchEntered;
			this.mainMayReleaseWatch = mainMayReleaseWatch;
		}

		@Override
		public SeleniumHubProperties properties() {
			return properties;
		}

		@Override
		public NndlWebDriver open() {
			String name = opened.isEmpty() ? "main" : "async-" + opened.size();
			RecordingDriver driver = new RecordingDriver(name, events, watchEntered, mainMayReleaseWatch, this);
			opened.add(driver);
			return driver;
		}

		@Override
		public NndlWebDriverWaiter waiter(NndlWebDriver driver) {
			return new IdleWaiter();
		}

		private RecordingDriver driverFor(String url) {
			return opened.stream()
					.filter(driver -> driver.urls.contains(url))
					.findFirst()
					.orElseThrow();
		}
	}

	private static final class RecordingDriver implements NndlWebDriver {
		private final String name;
		private final List<String> events;
		private final List<String> urls = new ArrayList<>();
		private final CountDownLatch watchEntered;
		private final CountDownLatch mainMayReleaseWatch;
		private final ScriptedSession session;
		private Thread actionThread;
		private Thread quitThread;
		private int quitCount;

		private RecordingDriver(String name, List<String> events, CountDownLatch watchEntered,
				CountDownLatch mainMayReleaseWatch, ScriptedSession session) {
			this.name = name;
			this.events = events;
			this.watchEntered = watchEntered;
			this.mainMayReleaseWatch = mainMayReleaseWatch;
			this.session = session;
		}

		@Override
		public void open() {
		}

		@Override
		public void quit() {
			quitThread = Thread.currentThread();
			quitCount++;
			events.add(name + " quit");
		}

		@Override
		public String getSessionId() {
			return name;
		}

		@Override
		public void get(String url) {
			urls.add(url);
			if (WATCH_URL.equals(url)) {
				actionThread = Thread.currentThread();
				events.add("watch blocked");
				watchEntered.countDown();
				await(mainMayReleaseWatch);
				events.add("watch released");
				return;
			}
			if (MAIN_URL.equals(url)) {
				await(watchEntered);
				assertThat(session.opened.get(0).quitCount).isZero();
				assertThat(session.opened.get(1).quitCount).isZero();
				assertThat(NndlContextManager.getWebDriverSessionId()).isEqualTo(session.opened.get(0).name);
				events.add("main visited");
				mainMayReleaseWatch.countDown();
			}
		}

		@Override
		public String getCurrentUrl() {
			return urls.isEmpty() ? "" : urls.get(urls.size() - 1);
		}

		@Override
		public String getPageSource() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void refresh() {
			throw new UnsupportedOperationException();
		}

		@Override
		public NndlElement activeElement() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object executeScript(String script, Object... args) {
			throw new UnsupportedOperationException();
		}

		@Override
		public List<String> getWindowHandles() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void switchToWindow(String handle) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void closeWindow() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Set<NndlCookie> getCookies() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addCookie(NndlCookie cookie) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void hover(NndlElement element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public NndlElement find(NndlLocator locator) {
			throw new UnsupportedOperationException();
		}

		@Override
		public List<NndlElement> findAll(NndlLocator locator) {
			throw new UnsupportedOperationException();
		}

		private static void await(CountDownLatch latch) {
			try {
				assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new IllegalStateException(e);
			}
		}
	}

	private static final class IdleWaiter implements NndlWebDriverWaiter {
		@Override
		public NndlElement untilElement(Duration timeout, NndlElementCondition condition) {
			throw new UnsupportedOperationException();
		}

		@Override
		public List<NndlElement> untilElements(Duration timeout, NndlElementsCondition condition) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void untilScriptReturns(Duration timeout, String script) {
			throw new UnsupportedOperationException();
		}
	}
}
