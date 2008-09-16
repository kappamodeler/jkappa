package KappaSimulationInterfaces;

import java.util.List;

public interface IRule {

	public String getName();
	// Return this Rule’s name

	public ISolution getLHS();
	// Returns Connected Components found at the left hand side of
	// this rule. There are usually 1 or 2 (rarely 3 but almost never 4 or more)
	// Connected Components at the LHS of a rule.

	public ISolution getRHS();
	// Returns the right hand side of this rule. This is simply a
	// Solution.

	public float getKinetics();
	// Returns kinetic rate of this Rule, a positive float number.
	// This can also be infinite (e.g. Double.POSITIVE INFINITY in Java).

	public Integer getAutomorphism();
	// Returns an integer denoting the automorphisms of
	// the LHS of this Rule.

	public Long getDBId();
	// Returns the identifier of this object in the database

	// TODO what is it?
	public List<IRule> getAbstractions();
	// Returns a list of other Rules that are the abstraction
	// of this Rule. These are the most generic refinements of this Rule.

	// TODO ???
	public void precompile();
	// This method needs to compute/keep a map from each Connected
	// Component on the LHS to a list of Agents in the Solution and their
	// roles.

	public List<IAgent> getAddedAgents();
	// Returns a list/map of Agents added by this Rule.

	public List<ISolution> getActions();
	// Returns a list/map keeping information about roles in a
	// role map and Actions to be performed on the agent pointed by the role
	// map upon the application of the rule. Note that each role may have
	// several
	// actions to execute. For instance in [! a(x1), b(x1)] the agents have to
	// be
	// created and then bound together.

	// TODO need to determine exact type
	public Long getNumberOfAgentsCreated();
	// Returns the number of Agents that this
	// Rule is creating (may be negative).

	// TODO need to determine exact type
	public int getRate();
	// Returns the rate which is indicating whether the rule has a
	// warning or not

	// TODO ???
	public List<Integer> getConstraints();
	// Returns the list of Constraints for application of the
	// Rule.

	public boolean isInfinite();
	// Returns true is this Rule has infinite rate, false otherwise.
	
	public IInjection getSomeInjection();
	// Returns one injection of rule 

	public void removeInjection(IInjection inj);
	// recalculate list of rule injections 

	public void recalcultateActivity();

	public void createInjection(List<IAgent> agents);
	
	public List<Object> getChangedStates();// returns lists of (agent, its state),
	//that have been changed after rule applying
	
}
