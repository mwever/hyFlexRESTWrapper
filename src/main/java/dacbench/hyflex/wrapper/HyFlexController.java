package dacbench.hyflex.wrapper;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import AbstractClasses.ProblemDomain;
import AbstractClasses.ProblemDomain.HeuristicType;
import BinPacking.BinPacking;
import FlowShop.FlowShop;
import PersonnelScheduling.PersonnelScheduling;
import SAT.SAT;
import VRP.VRP;
import travelingSalesmanProblem.TSP;

@RestController
public class HyFlexController implements IHyFlexController {

	private static final Logger L = LoggerFactory.getLogger(HyFlexController.class);

	private static Map<String, ProblemDomain> problemDomainMap = new HashMap<>();

	public HyFlexController() {
		problemDomainMap.put("42", new SAT(42));
	}

	private ProblemDomain getProblemDomainOrFail(final String problemDomainID) {
		if (problemDomainMap.containsKey(problemDomainID)) {
			return problemDomainMap.get(problemDomainID);
		}
		throw new ProblemDomainNotFoundException("Problem domain with id " + problemDomainID + " was not found.");
	}

	@PutMapping(value = "/instantiate/{problemType}/{seed}")
	@Override
	public String instantiateProblemDomain(@PathVariable("problemType") final String problemType, @PathVariable("seed") final long seed) {
		String id = DigestUtils.sha256Hex(System.currentTimeMillis() + problemType + seed + "problemDomain");
		ProblemDomain pd = null;
		switch (problemType) {
		case "BinPacking":
			pd = new BinPacking(seed);
			break;
		case "FlowShop":
			pd = new FlowShop(seed);
			break;
		case "PersonnelScheduling":
			pd = new PersonnelScheduling(seed);
			break;
		case "SAT":
			pd = new SAT(seed);
			break;
		case "TSP":
			pd = new TSP(seed);
			break;
		case "VRP":
			pd = new VRP(seed);
			break;
		}

		if (pd == null) {
			return String.format("Problem domain %s not known.", problemType);
		} else {
			problemDomainMap.put(id, pd);
		}
		return id;
	}

	@PostMapping("/memorySize/{problemDomainID}/{size}")
	@Override
	public void setMemorySize(@PathVariable("problemDomainID") final String problemDomainID, @PathVariable("size") final int size) {
		L.debug("Set memory size for problem domain {} to {}", problemDomainID, size);
		this.getProblemDomainOrFail(problemDomainID).setMemorySize(size);
	}

	@GetMapping("/toString/{problemDomainID}")
	@Override
	public String toString(@PathVariable("problemDomainID") final String problemDomainID) {
		L.debug("Get string of problem domain {}", problemDomainID);
		return this.getProblemDomainOrFail(problemDomainID).toString();
	}

	@GetMapping("/instances/{problemDomainID}")
	@Override
	public int getNumberOfInstances(@PathVariable("problemDomainID") final String problemDomainID) {
		L.debug("Get number of instances of problem domain {}", problemDomainID);
		return this.getProblemDomainOrFail(problemDomainID).getNumberOfInstances();
	}

	@PostMapping("/instance/{problemDomainID}/{instanceID}")
	@Override
	public void loadInstance(@PathVariable("problemDomainID") final String problemDomainID, @PathVariable("instanceID") final int instanceID) {
		L.debug("Load instance {} for problem domain {}.", instanceID, problemDomainID);
		this.getProblemDomainOrFail(problemDomainID).loadInstance(instanceID);
	}

	@GetMapping("/search/depth/{problemDomainID}")
	@Override
	public double getDepthOfSearch(@PathVariable("problemDomainID") final String problemDomainID) {
		return this.getProblemDomainOrFail(problemDomainID).getDepthOfSearch();
	}

	@PostMapping("/search/depth/{problemDomainID}/{depthOfSearch}")
	@Override
	public void setDepthOfSearch(@PathVariable("problemDomainID") final String problemDomainID, @PathVariable("depthOfSearch") final double depthOfSearch) {
		this.getProblemDomainOrFail(problemDomainID).setDepthOfSearch(depthOfSearch);
	}

	@GetMapping("/mutationIntensity/{problemDomainID}")
	@Override
	public double getIntensivityOfMutation(@PathVariable("problemDomainID") final String problemDomainID) {
		return this.getProblemDomainOrFail(problemDomainID).getIntensityOfMutation();
	}

	@PostMapping("/mutationIntensity/{problemDomainID}/{intensityOfMutation}")
	@Override
	public void setIntensityOfMutation(@PathVariable("problemDomainID") final String problemDomainID, @PathVariable("intensityOfMutation") final double intensityOfMutation) {
		this.getProblemDomainOrFail(problemDomainID).setIntensityOfMutation(intensityOfMutation);
	}

	@GetMapping(value = "/heuristic/record/call/{problemDomainID}")
	@Override
	public int[] getHeuristicCallRecord(final @PathVariable("problemDomainID") String problemDomainID) {
		return this.getProblemDomainOrFail(problemDomainID).getHeuristicCallRecord();
	}

	@GetMapping("/heuristic/record/callTime/{problemDomainID}")
	@Override
	public int[] getHeuristicCallTimeRecord(@PathVariable("problemDomainID") final String problemDomainID) {
		return this.getProblemDomainOrFail(problemDomainID).getheuristicCallTimeRecord();
	}

	@GetMapping("/heuristic/{problemDomainID}/{heuristicType}")
	@Override
	public int[] getHeuristicsOfType(@PathVariable("problemDomainID") final String problemDomainID, @PathVariable("heuristicType") final String heuristicType) {
		return this.getProblemDomainOrFail(problemDomainID).getHeuristicsOfType(HeuristicType.valueOf(heuristicType));
	}

	@GetMapping("/heuristic/mutationIntensity/{problemDomainID}")
	@Override
	public int[] getHeuristicsThatUseIntensityOfMutation(@PathVariable("problemDomainID") final String problemDomainID) {
		return this.getProblemDomainOrFail(problemDomainID).getHeuristicsThatUseIntensityOfMutation();
	}

	@GetMapping("/heuristic/depth/{problemDomainID}")
	@Override
	public int[] getHeuristicsThatUseDepthOfSearch(@PathVariable("problemDomainID") final String problemDomainID) {
		return this.getProblemDomainOrFail(problemDomainID).getHeuristicsThatUseDepthOfSearch();
	}

	@GetMapping("/heuristic/num/{problemDomainID}")
	@Override
	public int getNumberOfHeuristics(@PathVariable("problemDomainID") final String problemDomainID) {
		return this.getProblemDomainOrFail(problemDomainID).getNumberOfHeuristics();
	}

	@PostMapping("/heuristic/apply/{problemDomainID}/{heuristicID}/{solutionSourceIndex}/{solutionDestinationIndex}")
	@Override
	public double applyHeuristic(@PathVariable("problemDomainID") final String problemDomainID, @PathVariable("heuristicID") final int heuristicID, @PathVariable("solutionSourceIndex") final int solutionSourceIndex,
			@PathVariable("solutionDestinationIndex") final int solutionDestinationIndex) {
		return this.getProblemDomainOrFail(problemDomainID).applyHeuristic(heuristicID, solutionSourceIndex, solutionDestinationIndex);
	}

	@PostMapping("/heuristic/apply/{problemDomainID}/{heuristicID}/{solutionSourceIndex1}/{solutionSourceIndex2}/{solutionDestinationIndex}")
	@Override
	public double applyHeuristic2(@PathVariable("problemDomainID") final String problemDomainID, @PathVariable("heuristicID") final int heuristicID, @PathVariable("solutionSourceIndex1") final int solutionSourceIndex1,
			@PathVariable("solutionSourceIndex2") final int solutionSourceIndex2, @PathVariable("solutionDestinationIndex") final int solutionDestinationIndex) {
		return this.getProblemDomainOrFail(problemDomainID).applyHeuristic(heuristicID, solutionSourceIndex1, solutionSourceIndex2, solutionDestinationIndex);
	}

	@PutMapping("/solution/init/{problemDomainID}/{index}")
	@Override
	public void initialiseSolution(@PathVariable("problemDomainID") final String problemDomainID, @PathVariable("index") final int index) {
		this.getProblemDomainOrFail(problemDomainID).initialiseSolution(index);
	}

	@GetMapping("/solution/best/toString/{problemDomainID}")
	@Override
	public String getBestSolutionToString(@PathVariable("problemDomainID") final String problemDomainID) {
		return this.getProblemDomainOrFail(problemDomainID).bestSolutionToString();
	}

	@GetMapping("/solution/best/value/{problemDomainID}")
	@Override
	public double getBestSolutionValue(@PathVariable("problemDomainID") final String problemDomainID) {
		return this.getProblemDomainOrFail(problemDomainID).getBestSolutionValue();
	}

	@PostMapping("/solution/copy/{problemDomainID}/{solutionSourceIndex}/{solutionDestinationIndex}")
	@Override
	public void copySolution(@PathVariable("problemDomainID") final String problemDomainID, @PathVariable("solutionSourceIndex") final int solutionSourceIndex, @PathVariable("solutionDestinationIndex") final int solutionDestinationIndex) {
		this.getProblemDomainOrFail(problemDomainID).copySolution(solutionSourceIndex, solutionDestinationIndex);
	}

	@GetMapping("/solution/toString/{problemDomainID}/{solutionIndex}")
	@Override
	public String getSolutionToString(@PathVariable("problemDomainID") final String problemDomainID, @PathVariable("solutionIndex") final int solutionIndex) {
		return this.getProblemDomainOrFail(problemDomainID).solutionToString(solutionIndex);
	}

	@GetMapping("/solution/functionValue/{problemDomainID}/{solutionIndex}")
	@Override
	public double getFunctionValue(@PathVariable("problemDomainID") final String problemDomainID, @PathVariable("solutionIndex") final int solutionIndex) {
		return this.getProblemDomainOrFail(problemDomainID).getFunctionValue(solutionIndex);
	}

	@GetMapping("/solution/compare/{problemDomainID}/{solutionIndex1}/{solutionIndex2}")
	@Override
	public boolean compareSolutions(@PathVariable("problemDomainID") final String problemDomainID, @PathVariable("solutionIndex1") final int solutionIndex1, @PathVariable("solutionIndex2") final int solutionIndex2) {
		return this.getProblemDomainOrFail(problemDomainID).compareSolutions(solutionIndex1, solutionIndex2);
	}

}
