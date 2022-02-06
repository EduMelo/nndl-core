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
import dev.edumelo.com.nndl_core.action.ActionRunner;
import dev.edumelo.com.nndl_core.action.AsynchronousActionRunner;
import dev.edumelo.com.nndl_core.action.requirementStatus.RequirementStatus;
import dev.edumelo.com.nndl_core.action.requirementStatus.RestartStepRequirementStatus;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.AdviceType;
import dev.edumelo.com.nndl_core.step.advice.SwapStepAdvice;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class StepRunner {
	
	private static final Logger log = LoggerFactory.getLogger(StepRunner.class);
	
	private final SeleniumSndlWebDriver webDriver;
	private final SeleniumSndlWebDriverWaiter webDriverWait;
	private ActionRunner actionRunner;
	private Map<String, Step> steps;
	
	private Collection extractDataBindList;
	
	public StepRunner(SeleniumSndlWebDriver webDriver, SeleniumSndlWebDriverWaiter webDriverWait, Collection extractDataBindList) {
		this.webDriver = webDriver;
		this.webDriverWait = webDriverWait;
		Collection<ExtractDataBind> extractDataBindCollection = new ArrayList<ExtractDataBind>();
		this.extractDataBindList = extractDataBindList;
		this.actionRunner = new ActionRunner(webDriver, webDriverWait, extractDataBindCollection);
	}

	public Collection getExtractDataBindList() {
		return extractDataBindList;
	}

	public Collection<ExtractDataBind> runSteps(String entryStep, Map<String, Step> steps, Collection extractDataBindList) {
		this.steps = steps;
		extractDataBindList = new ArrayList<ExtractDataBind>();
		Step runningStep = steps.get(entryStep);			
		runStep(runningStep, null);
		
		return extractDataBindList;
		
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
			
			Collection<ExtractDataBind> extractData = actionRunner.getExtractDataBindList();
			if(extractDataBindList != null && extractData != null) {
				extractDataBindList.addAll(extractData);			
			}
		}
		return stepsRunned;
	}
	
	public LinkedList<Action> runStepActions(ActionRunner actionRunner, LinkedList<Action> runningActions, IterationContent rootElement) 
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
			} catch(Exception e) {
				String msg = String.format("Action not performed. Action: %s", action);
				log.error(msg);
				switch (requirementStatus.getType()) {
					case NON_REQUIRED:
						break;
					case STEP_BREAKER:
						caughtException = new StepBreakerActionNotPerformed(msg, e, requirementStatus.getStepTreatment());
						break;
					case REQUIRED:
						caughtException = new RunBreakerActionNotPerformed(msg, e);
						break;
					case RESTART_STEP:
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
