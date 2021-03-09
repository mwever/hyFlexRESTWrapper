package dacbench.hyflex.wrapper;
public interface IHyFlexController {

	public String instantiateProblemDomain(String problemType, long seed);

	public int[] getHeuristicCallRecord(String problemDomainID);

	public int[] getHeuristicCallTimeRecord(String problemDomain);

	public double getDepthOfSearch(String problemDomainID);

	public void setDepthOfSearch(String problemDomainID, double depthOfSearch);

	public double getIntensivityOfMutation(String problemDomainID);

	public void setIntensityOfMutation(String problemDomainID, double intensityOfMutation);

	public int[] getHeuristicsOfType(String problemDomainID, final String heuristicType);

	public int[] getHeuristicsThatUseIntensityOfMutation(String problemDomainID);

	public int[] getHeuristicsThatUseDepthOfSearch(String problemDomainID);

	public void setMemorySize(String problemDomainID, int size);

	public void initialiseSolution(String problemDomainID, int index);

	public int getNumberOfHeuristics(String problemDomainID);

	public double applyHeuristic(String problemDomainID, int heuristicID, int solutionSourceIndex, int solutionDestinationIndex);

	public double applyHeuristic2(String problemDomainID, int heuristicID, int solutionSourceIndex1, int solutionSourceIndex2, int solutionDestinationIndex);

	public String toString(String problemDomainID);

	public int getNumberOfInstances(String problemDomainID);

	public String getBestSolutionToString(String problemDomainID);

	public double getBestSolutionValue(String problemDomainID);

	public void copySolution(String problemDomainID, int solutionSourceIndex, int solutionDestinationINdex);

	public String getSolutionToString(String problemDomainID, int solutionIndex);

	public double getFunctionValue(String problemDomainID, int solutionIndex);

	public boolean compareSolutions(String problemDomainID, int solutionINdex1, int solutionIndex2);

	public void loadInstance(String problemDomainID, int instanceID);
}
