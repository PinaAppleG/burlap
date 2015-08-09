package burlap.behavior.stochasticgames.agents.interfacing.singleagent;

import java.util.List;

import burlap.oomdp.auxiliary.DomainGenerator;
import burlap.oomdp.core.Attribute;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.ObjectClass;
import burlap.oomdp.core.PropositionalFunction;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.Action;
import burlap.oomdp.singleagent.SADomain;
import burlap.oomdp.stochasticgames.SGAgentType;
import burlap.oomdp.stochasticgames.SGDomain;
import burlap.oomdp.stochasticgames.SGAgentAction;


/**
 * This domain generator is used to produce single agent domain version of a stochastic games domain for an agent of a given type
 * (specified by an {@link burlap.oomdp.stochasticgames.SGAgentType} object or for a given list of stochastic games agent actions ({@link burlap.oomdp.stochasticgames.SGAgentAction}).
 * Each of the stochastic game agent actions is converted into a single agent {@link burlap.oomdp.singleagent.Action} object with the same
 * action name and parametrization. The created {@link burlap.oomdp.singleagent.SADomain}'s {@link burlap.oomdp.singleagent.Action} objects maintain the action specification of
 * the input {@link burlap.oomdp.stochasticgames.SGDomain}'s {@link burlap.oomdp.stochasticgames.SGAgentAction} (that is, their name and parameter types), but
 * the {@link burlap.oomdp.singleagent.Action#performAction(burlap.oomdp.core.states.State, String[])} and {@link burlap.oomdp.singleagent.Action#getTransitions(burlap.oomdp.core.states.State, String[])}
 * methods are undefined since the transition dynamics would depend on the action selection of other agents, which is unknown. Instead, actions can only
 * be executed through the {@link burlap.oomdp.singleagent.Action#performInEnvironment(burlap.oomdp.singleagent.environment.Environment, String[])} method only
 * in which the specified {@link burlap.oomdp.singleagent.environment.Environment} handles the decisions of the other agents. For example, this domain
 * can typically be paired with the {@link LearningAgentToSGAgentInterface}, which will handle these calls
 * indirectly by simultaneously acting as a stochastic game {@link burlap.oomdp.stochasticgames.SGAgent}.
 * <p/>
 * Note: subsequent calls to the {@link #generateDomain()} method
 * will not produce new single agent domain objects and will always return the same reference.
 * @author James MacGlashan
 *
 */
public class SGToSADomain implements DomainGenerator {

	/**
	 * The single agent domain object that will be returned
	 */
	protected SADomain					domainWrapper;

	
	
	/**
	 * Initializes.
	 * @param srcDomain the source stochastic games domain
	 * @param asAgentType the {@link burlap.oomdp.stochasticgames.SGAgentType} object specifying the actions that should be created in the single agent domain.
	 */
	public SGToSADomain(SGDomain srcDomain, SGAgentType asAgentType){
		this(srcDomain, asAgentType.actions);
	}
	
	
	/**
	 * Initializes.
	 * @param srcDomain the source stochastic games domain
	 * @param useableActions the stochastic game actions for which single agent actions should be created created in the single agent domain.
	 */
	public SGToSADomain(SGDomain srcDomain, List<SGAgentAction> useableActions){
		
		this.domainWrapper = new SADomain();
		
		for(Attribute a : srcDomain.getAttributes()){
			this.domainWrapper.addAttribute(a);
		}
		
		for(ObjectClass c : srcDomain.getObjectClasses()){
			this.domainWrapper.addObjectClass(c);
		}
		
		for(PropositionalFunction pf : srcDomain.getPropFunctions()){
			this.domainWrapper.addPropositionalFunction(pf);
		}
		
		for(SGAgentAction sa : useableActions){
			new SAActionWrapper(sa);
		}
		
	}
	
	@Override
	public Domain generateDomain() {
		return this.domainWrapper;
	}
	
	
	/**
	 * A single agent action wrapper for a stochastic game action. Calling this action will cause it to call the corresponding single interface to inform it
	 * of the action selection. The constructed action will have the same name and object parametrization specification as the source stochastic game
	 * {@link burlap.oomdp.stochasticgames.SGAgentAction} object.
	 * @author James MacGlashan
	 *
	 */
	protected class SAActionWrapper extends Action{

		/**
		 * Initializes for a given stochastic games action.
		 * @param srcAction the source stochastic games {@link burlap.oomdp.stochasticgames.SGAgentAction} object.
		 */
		public SAActionWrapper(SGAgentAction srcAction){
			super(srcAction.actionName, SGToSADomain.this.domainWrapper, srcAction.parameterTypes, srcAction.parameterOrderGroups);
		}
		
		@Override
		protected State performActionHelper(State s, String[] params) {
			throw new RuntimeException("The SGToSADomain Action instances cannot execute the performAction method, because the transition dynamics depend on the other agent decisions which are unknown. Instead, use the performInEnvironment method or use these action indirectly with a LearningAgentToSGAgentInterface instance.");
		}
		
	}

}