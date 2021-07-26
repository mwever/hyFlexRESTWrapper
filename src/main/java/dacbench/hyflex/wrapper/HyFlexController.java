package dacbench.hyflex.wrapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
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

	private Writer writer;

	private final String fileName = "1episode-" + System.currentTimeMillis() + "-%s.txt";
	private int counter = 0;

	class TimeDateWriter extends FileWriter {
		public TimeDateWriter(final File file) throws IOException {
			super(file);
		}

		@Override
		public void write(final String text) throws IOException {
			super.write(System.currentTimeMillis() + "\t" + text);
		}
	}

	public HyFlexController() throws IOException {
		this.writer = new TimeDateWriter(new File(String.format(this.fileName, this.counter)));
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
		try {
			this.writer.write(String.format("%s %s %s\n", "instantiateProblemDomain", problemType, seed));
			this.writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		try {
			this.writer.write(String.format("%s %s\n", "setMemorySize", size));
			this.writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		L.debug("Set memory size for problem domain {} to {}", problemDomainID, size);
		this.getProblemDomainOrFail(problemDomainID).setMemorySize(size);
	}

	@GetMapping("/toString/{problemDomainID}")
	@Override
	public String toString(@PathVariable("problemDomainID") final String problemDomainID) {
		try {
			this.writer.write(String.format("%s", "toString") + "\n");
			this.writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		L.debug("Get string of problem domain {}", problemDomainID);
		return this.getProblemDomainOrFail(problemDomainID).toString();
	}

	@GetMapping("/instances/{problemDomainID}")
	@Override
	public int getNumberOfInstances(@PathVariable("problemDomainID") final String problemDomainID) {
		try {
			this.writer.write(String.format("%s\n", "getNumberOfInstances"));
			this.writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		L.debug("Get number of instances of problem domain {}", problemDomainID);
		return this.getProblemDomainOrFail(problemDomainID).getNumberOfInstances();
	}

	@PostMapping("/instance/{problemDomainID}/{instanceID}")
	@Override
	public void loadInstance(@PathVariable("problemDomainID") final String problemDomainID, @PathVariable("instanceID") final int instanceID) {
		try {
			this.writer.write(String.format("%s %s\n", "loadInstance", instanceID));
			this.writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		L.debug("Load instance {} for problem domain {}.", instanceID, problemDomainID);
		this.getProblemDomainOrFail(problemDomainID).loadInstance(instanceID);
	}

	@GetMapping("/search/depth/{problemDomainID}")
	@Override
	public double getDepthOfSearch(@PathVariable("problemDomainID") final String problemDomainID) {
		try {
			this.writer.write(String.format("%s\n", "getDepthOfsearch"));
			this.writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this.getProblemDomainOrFail(problemDomainID).getDepthOfSearch();
	}

	@PostMapping("/search/depth/{problemDomainID}/{depthOfSearch}")
	@Override
	public void setDepthOfSearch(@PathVariable("problemDomainID") final String problemDomainID, @PathVariable("depthOfSearch") final double depthOfSearch) {
		try {
			this.writer.write(String.format("%s %s\n", "setdepthOfSearch", depthOfSearch));
			this.writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.getProblemDomainOrFail(problemDomainID).setDepthOfSearch(depthOfSearch);
	}

	@GetMapping("/mutationIntensity/{problemDomainID}")
	@Override
	public double getIntensivityOfMutation(@PathVariable("problemDomainID") final String problemDomainID) {
		try {
			this.writer.write(String.format("%s\n", "getIntensitivityOfMutation"));
			this.writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this.getProblemDomainOrFail(problemDomainID).getIntensityOfMutation();
	}

	@PostMapping("/mutationIntensity/{problemDomainID}/{intensityOfMutation}")
	@Override
	public void setIntensityOfMutation(@PathVariable("problemDomainID") final String problemDomainID, @PathVariable("intensityOfMutation") final double intensityOfMutation) {
		try {
			this.writer.write(String.format("%s %s\n", "setIntensitivityOfMutation", intensityOfMutation));
			this.writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.getProblemDomainOrFail(problemDomainID).setIntensityOfMutation(intensityOfMutation);
	}

	@GetMapping(value = "/heuristic/record/call/{problemDomainID}")
	@Override
	public int[] getHeuristicCallRecord(final @PathVariable("problemDomainID") String problemDomainID) {
		try {
			this.writer.write(String.format("%s\n", "getHeuristicCallRecord"));
			this.writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this.getProblemDomainOrFail(problemDomainID).getHeuristicCallRecord();
	}

	@GetMapping("/heuristic/record/callTime/{problemDomainID}")
	@Override
	public int[] getHeuristicCallTimeRecord(@PathVariable("problemDomainID") final String problemDomainID) {
		try {
			this.writer.write(String.format("%s\n", "getHeuristicCallTimeRecord"));
			this.writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this.getProblemDomainOrFail(problemDomainID).getheuristicCallTimeRecord();
	}

	@GetMapping("/heuristic/{problemDomainID}/{heuristicType}")
	@Override
	public int[] getHeuristicsOfType(@PathVariable("problemDomainID") final String problemDomainID, @PathVariable("heuristicType") final String heuristicType) {
		try {
			this.writer.write(String.format("%s %s\n", "getHeuristicOfType", heuristicType));
			this.writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int[] heuristicsOfType = this.getProblemDomainOrFail(problemDomainID).getHeuristicsOfType(HeuristicType.valueOf(heuristicType));
		if (heuristicsOfType == null) {
			return new int[] {};
		} else {
			return heuristicsOfType;
		}
	}

	@GetMapping("/heuristic/mutationIntensity/{problemDomainID}")
	@Override
	public int[] getHeuristicsThatUseIntensityOfMutation(@PathVariable("problemDomainID") final String problemDomainID) {
		try {
			this.writer.write(String.format("%s\n", "getHeuristicsThatUseIntensityOfMutation"));
			this.writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this.getProblemDomainOrFail(problemDomainID).getHeuristicsThatUseIntensityOfMutation();
	}

	@GetMapping("/heuristic/depth/{problemDomainID}")
	@Override
	public int[] getHeuristicsThatUseDepthOfSearch(@PathVariable("problemDomainID") final String problemDomainID) {
		try {
			this.writer.write(String.format("%s\n", "getHeuristicsThatUseDepthOfSearch"));
			this.writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this.getProblemDomainOrFail(problemDomainID).getHeuristicsThatUseDepthOfSearch();
	}

	@GetMapping("/heuristic/num/{problemDomainID}")
	@Override
	public int getNumberOfHeuristics(@PathVariable("problemDomainID") final String problemDomainID) {
		try {
			this.writer.write(String.format("%s\n", "getNumberOfHeuristics"));
			this.writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this.getProblemDomainOrFail(problemDomainID).getNumberOfHeuristics();
	}

	@PostMapping("/heuristic/apply/{problemDomainID}/{heuristicID}/{solutionSourceIndex}/{solutionDestinationIndex}")
	@Override
	public double applyHeuristic(@PathVariable("problemDomainID") final String problemDomainID, @PathVariable("heuristicID") final int heuristicID, @PathVariable("solutionSourceIndex") final int solutionSourceIndex,
			@PathVariable("solutionDestinationIndex") final int solutionDestinationIndex) {
		try {
			this.writer.write(String.format("%s %s %s %s\n", "applyHeuristic", heuristicID, solutionSourceIndex, solutionDestinationIndex));
			this.writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this.getProblemDomainOrFail(problemDomainID).applyHeuristic(heuristicID, solutionSourceIndex, solutionDestinationIndex);
	}

	@PostMapping("/heuristic/apply/{problemDomainID}/{heuristicID}/{solutionSourceIndex1}/{solutionSourceIndex2}/{solutionDestinationIndex}")
	@Override
	public double applyHeuristic2(@PathVariable("problemDomainID") final String problemDomainID, @PathVariable("heuristicID") final int heuristicID, @PathVariable("solutionSourceIndex1") final int solutionSourceIndex1,
			@PathVariable("solutionSourceIndex2") final int solutionSourceIndex2, @PathVariable("solutionDestinationIndex") final int solutionDestinationIndex) {
		try {
			this.writer.write(String.format("%s %s %s %s %s\n", "applyHeuristic", heuristicID, solutionSourceIndex1, solutionSourceIndex2, solutionDestinationIndex));
			this.writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this.getProblemDomainOrFail(problemDomainID).applyHeuristic(heuristicID, solutionSourceIndex1, solutionSourceIndex2, solutionDestinationIndex);
	}

	@PutMapping("/solution/init/{problemDomainID}/{index}")
	@Override
	public void initialiseSolution(@PathVariable("problemDomainID") final String problemDomainID, @PathVariable("index") final int index) {
		try {
			this.writer.write(String.format("%s %s\n", "initialiseSolution", index));
			this.writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.getProblemDomainOrFail(problemDomainID).initialiseSolution(index);
	}

	@GetMapping("/solution/best/toString/{problemDomainID}")
	@Override
	public String getBestSolutionToString(@PathVariable("problemDomainID") final String problemDomainID) {
		try {
			this.writer.write(String.format("%s %s\n", "getBestSolutionToString"));
			this.writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this.getProblemDomainOrFail(problemDomainID).bestSolutionToString();
	}

	@GetMapping("/solution/best/value/{problemDomainID}")
	@Override
	public double getBestSolutionValue(@PathVariable("problemDomainID") final String problemDomainID) {
		try {
			this.writer.write(String.format("%s %s\n", "getBestSolutionValue"));
			this.writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this.getProblemDomainOrFail(problemDomainID).getBestSolutionValue();
	}

	@PostMapping("/solution/copy/{problemDomainID}/{solutionSourceIndex}/{solutionDestinationIndex}")
	@Override
	public void copySolution(@PathVariable("problemDomainID") final String problemDomainID, @PathVariable("solutionSourceIndex") final int solutionSourceIndex, @PathVariable("solutionDestinationIndex") final int solutionDestinationIndex) {
		try {
			this.writer.write(String.format("%s %s %s\n", "copySolution", solutionSourceIndex, solutionDestinationIndex));
			this.writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.getProblemDomainOrFail(problemDomainID).copySolution(solutionSourceIndex, solutionDestinationIndex);
	}

	@GetMapping("/solution/toString/{problemDomainID}/{solutionIndex}")
	@Override
	public String getSolutionToString(@PathVariable("problemDomainID") final String problemDomainID, @PathVariable("solutionIndex") final int solutionIndex) {
		try {
			this.writer.write(String.format("%s %s\n", "getSolutionToString", solutionIndex));
			this.writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this.getProblemDomainOrFail(problemDomainID).solutionToString(solutionIndex);
	}

	@GetMapping("/solution/functionValue/{problemDomainID}/{solutionIndex}")
	@Override
	public double getFunctionValue(@PathVariable("problemDomainID") final String problemDomainID, @PathVariable("solutionIndex") final int solutionIndex) {
		try {
			this.writer.write(String.format("%s %s\n", "getFunctionValue", solutionIndex));
			this.writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this.getProblemDomainOrFail(problemDomainID).getFunctionValue(solutionIndex);
	}

	@GetMapping("/solution/compare/{problemDomainID}/{solutionIndex1}/{solutionIndex2}")
	@Override
	public boolean compareSolutions(@PathVariable("problemDomainID") final String problemDomainID, @PathVariable("solutionIndex1") final int solutionIndex1, @PathVariable("solutionIndex2") final int solutionIndex2) {
		try {
			this.writer.write(String.format("%s %s\n", "compareSolutions", solutionIndex1, solutionIndex2));
			this.writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this.getProblemDomainOrFail(problemDomainID).compareSolutions(solutionIndex1, solutionIndex2);
	}

	@GetMapping("/changelog")
	public void changeLogFile() throws IOException {
		this.counter++;
		this.writer.flush();
		this.writer.close();
		this.writer = new TimeDateWriter(new File(String.format(this.fileName, this.counter)));
	}

}
