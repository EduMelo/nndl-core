package dev.edumelo.com.nndl_core.step;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.edumelo.com.nndl_core.ExtractDataBind;
import dev.edumelo.com.nndl_core.action.Action;
import dev.edumelo.com.nndl_core.action.requirementStatus.RequirementStatus;
import dev.edumelo.com.nndl_core.action.requirementStatus.RestartStepRequirementStatus;
import dev.edumelo.com.nndl_core.action.runner.ActionRunner;
import dev.edumelo.com.nndl_core.action.runner.AsynchronousActionRunner;
import dev.edumelo.com.nndl_core.contextAdapter.ThreadLocalManager;
import dev.edumelo.com.nndl_core.exceptions.checked.NndlFlowBreakerException;
import dev.edumelo.com.nndl_core.exceptions.checked.StepBreakerActionNotPerformed;
import dev.edumelo.com.nndl_core.exceptions.unchecked.RunBreakerActionNotPerformed;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.AdviceType;
import dev.edumelo.com.nndl_core.step.advice.SwapStepAdvice;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;

public class StepRunner {
	private static final Logger log = LoggerFactory.getLogger(StepRunner.class);
	
	private final SeleniumSndlWebDriver webDriver;
	private final SeleniumSndlWebDriverWaiter webDriverWait;
	private ActionRunner actionRunner;
	private Map<String, Step> steps;
	
	public StepRunner(SeleniumSndlWebDriver webDriver, SeleniumSndlWebDriverWaiter webDriverWait) {
		this.webDriver = webDriver;
		this.webDriverWait = webDriverWait;
		Collection<ExtractDataBind> extractDataBindCollection = new ArrayList<ExtractDataBind>();
		this.actionRunner = new ActionRunner(webDriver, webDriverWait, extractDataBindCollection);
	}

	public void runSteps(String entryStep, Map<String, Step> steps) {
		this.steps = steps;
		Step runningStep = steps.get(entryStep);			
		runStep(runningStep, null);
	}
	
	public int runStep(Step step, IterationContent rootElement) {
		LinkedList<Action> runningActions = step.getActions();
		int stepsRunned = 0;
		step.setLimitCountInvalidate(false);
		
		while (runningActions != null) {
			try {
				LinkedList<Action> nextRunningActions = runStepActions(actionRunner, runningActions, rootElement);
				
				if(!step.isLimitCountInvalidate()) {
					boolean stepRunned = runningActions.stream()
							.filter(Action::isLimitRequirement)
							.allMatch(Action::isActionPerformed);
					
					if(stepRunned) {
						stepsRunned++;
					}					
				}
				
				runningActions = nextRunningActions;
			} catch (StepBreakerActionNotPerformed e) {
				String msg = "Step breaker action not performed.";
				log.error(msg);
				String stepTreatmentName = e.getStepTreatment();
				if(stepTreatmentName != null) {
					Step stepTreatment = step.getSubSteps().get(stepTreatmentName);
					step.setLimitCountInvalidate(true);
					runningActions = stepTreatment.getActions();					
				} else {
					break;					
				}
			}		
		}
		return stepsRunned;
	}
	
	public LinkedList<Action> runStepActions(ActionRunner actionRunner,
			LinkedList<Action> runningActions, IterationContent rootElement) 
			throws StepBreakerActionNotPerformed, RunBreakerActionNotPerformed {
		
		int actionsRestarted = 0;
		for (Iterator<Action> iterator = runningActions.iterator(); iterator.hasNext();) {
			Action action = (Action) iterator.next();
			RequirementStatus requirementStatus = action.getRequirementStatus();			
			Exception caughtException = null;
			try {
				Advice advice = actionRunner.run(rootElement, action);
				if(advice.getType() == AdviceType.SWAP_STEP) {
					SwapStepAdvice parametrizedAdvice = (SwapStepAdvice) advice;
					return checkNextActions(parametrizedAdvice);
				}
				if(advice.getType() == AdviceType.STOP_RUN) {
					throw new RunStopperException();
				}
			} catch(Exception e) {
				String msgSufix = String.format("Action not performed. ");
				String msgPrefix = String.format("Action: %s, stackTrace: %s",
						action, e.getStackTrace());
				if(e instanceof RunStopperException) {
					throw (RunStopperException) e;
				}
				
				String msg = "";
				switch (requirementStatus.getType()) {
					case NON_REQUIRED:
						msg = "Non required action exception. ";
						log.info(msgSufix+msg+msgPrefix);
						break;
					case STEP_BREAKER:
						msg = "Step breaker action exception. ";
						log.error(msgSufix+msg+msgPrefix);
						caughtException = new StepBreakerActionNotPerformed(msg, action.getRelevantNode(), e,
								requirementStatus.getStepTreatment());
						break;
					case REQUIRED:
						msg = "Required action exception. action: "+action;
						log.error(msgSufix+msg+msgPrefix);
							caughtException = new RunBreakerActionNotPerformed(msg, action.getRelevantNode(), e);
						break;
					case RESTART_STEP:
						msg = "Restart step action exception. ";
						log.debug(msgSufix+msg+msgPrefix);
						RestartStepRequirementStatus restartRequirementStatus = (RestartStepRequirementStatus) requirementStatus;
						if(restartRequirementStatus.getRestartCount() >= actionsRestarted) {
							iterator = runningActions.iterator();
							actionsRestarted++;
						} else {
							caughtException = restartRequirementStatus.getFallBack();
						}
						break;
					default:
						break;
				}
 			}
			
			if(caughtException != null) {
				action.setActionPerformed(false);
				if(caughtException instanceof NndlFlowBreakerException) {
					ThreadLocalManager.storeSourceCode(action.getRelevantNode().getConcatenadedLines(),
							webDriver.getPageSource());
				}
				if(caughtException instanceof StepBreakerActionNotPerformed) {
					throw (StepBreakerActionNotPerformed) caughtException;
				}
				if(caughtException instanceof RunBreakerActionNotPerformed) {
					throw (RunBreakerActionNotPerformed) caughtException;
				}
			} 
		}
		return null;
	}

	private LinkedList<Action> checkNextActions(SwapStepAdvice parametrizedAdvice) {
		if(steps == null) {
			return null;
		}
		return steps.get(parametrizedAdvice.getAdvisedStep()).getActions();
	}
	
	public void runAsynchronousSteps(IterationContent rootElement, Map<String, Step> steps) {
		AsynchronousActionRunner actionRunner = new AsynchronousActionRunner(webDriver, webDriverWait);
		for (Step step : steps.values()) {
			LinkedList<Action> runningActions = step.getActions();
			for (Action action : runningActions) {
				actionRunner.run(rootElement, action);
			}
		}
	}
	
}
