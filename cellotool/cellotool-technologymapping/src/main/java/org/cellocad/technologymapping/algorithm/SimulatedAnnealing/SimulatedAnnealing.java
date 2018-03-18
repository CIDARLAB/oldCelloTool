/**
 * Copyright (C) 2018 Boston University (BU)
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.cellocad.technologymapping.algorithm.SimulatedAnnealing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.cellocad.common.CObjectCollection;
import org.cellocad.common.Pair;
import org.cellocad.common.netlist.NetlistNode;
import org.cellocad.technologymapping.algorithm.TMAlgorithm;
import org.cellocad.technologymapping.common.TMUtils;
import org.cellocad.technologymapping.common.TargetDataReader;
import org.cellocad.technologymapping.common.netlist.TMNetlist;
import org.cellocad.technologymapping.common.netlist.TMNode;
import org.cellocad.technologymapping.common.score.ScoreUtils;
import org.cellocad.technologymapping.common.simulation.ActivitySimulator;
import org.cellocad.technologymapping.common.simulation.LogicSimulator;
import org.cellocad.technologymapping.common.simulation.ToxicitySimulator;
import org.cellocad.technologymapping.data.Gate;
import org.cellocad.technologymapping.data.Part;

/**
 * @author: Timothy Jones
 * 
 * @date: Mar 12, 2018
 *
 */
// TODO: make techmap inherit from graph template, don't pass netlist around
public class SimulatedAnnealing extends TMAlgorithm{

	@Override
	protected void setDefaultParameterValues() {
		this.setPartLibrary(TargetDataReader.getParts(this.getTargetData()));
		this.setGateLibrary(TargetDataReader.getGates(this.getTargetData()));
		this.setInputLibrary(TargetDataReader.getInputSensors(this.getTargetData()));
		this.setOutputLibrary(TargetDataReader.getOutputReporters(this.getTargetData()));
		this.setLogicRoadblocks(TargetDataReader.getLogicRoadblocks(this.getTargetData()));
		this.setInputRoadblocks(TargetDataReader.getInputRoadblocks(this.getTargetData()));

		this.setNumTrajectories(50);
		this.setNumSteps(500);
		this.setNumT0Steps(100);
		this.setMaxTemp(100.0);
		this.setMinTemp(0.001);
		this.setCheckToxicity(true);
		this.setToxicityThreshold(0.75);
		this.setCheckRoadblocks(true);
	}

	@Override
	protected void setParameterValues() {
		try {
			Pair<Boolean,Integer> param = this.getAlgorithmProfile().getIntParameter("trajectories");
			if (param.getFirst()) {this.setNumTrajectories(param.getSecond());}
		} catch (NullPointerException e) {}
		try {
			Pair<Boolean,Integer> param = this.getAlgorithmProfile().getIntParameter("steps");
			if (param.getFirst()) {this.setNumSteps(param.getSecond());}
		} catch (NullPointerException e) {}
		try {
			Pair<Boolean,Integer> param = this.getAlgorithmProfile().getIntParameter("t0steps");
			if (param.getFirst()) {this.setNumT0Steps(param.getSecond());}
		} catch (NullPointerException e) {}
		try {
			Pair<Boolean,Double> param = this.getAlgorithmProfile().getDoubleParameter("maxtemp");
			if (param.getFirst()) {this.setMaxTemp(param.getSecond());}
		} catch (NullPointerException e) {}
		try {
			Pair<Boolean,Double> param = this.getAlgorithmProfile().getDoubleParameter("mintemp");
			if (param.getFirst()) {this.setMinTemp(param.getSecond());}
		} catch (NullPointerException e) {}
		try {
			Pair<Boolean,Boolean> param = this.getAlgorithmProfile().getBooleanParameter("check_toxicity");
			if (param.getFirst()) {this.setCheckToxicity(param.getSecond());}
		} catch (NullPointerException e) {}
		try {
			Pair<Boolean,Double> param = this.getAlgorithmProfile().getDoubleParameter("toxicity_threshold");
			if (param.getFirst()) {this.setToxicityThreshold(param.getSecond());}
		} catch (NullPointerException e) {}
		try {
			Pair<Boolean,Boolean> param = this.getAlgorithmProfile().getBooleanParameter("check_roadblock");
			if (param.getFirst()) {this.setCheckToxicity(param.getSecond());}
		} catch (NullPointerException e) {}
	}

	@Override
	protected void validateParameterValues() {
		if (this.getNumTrajectories() < 1) {
			throw new RuntimeException("Invalid number of trajectories.");
		}
		if (this.getNumSteps() < 1) {
			throw new RuntimeException("Invalid number of steps.");
		}
		if (this.getNumSteps() < 0) {
			throw new RuntimeException("Invalid number of t0 steps.");
		}
		if (this.getMinTemp() <= 0) {
			throw new RuntimeException("Invalid minimum temperature.");
		}
	}

	@Override
	protected void preprocessing() {
		// initialize NetlistNode indices
		int num = this.getNetlist().getNumVertex();
		for (int i = 0; i < num; i++) {
			NetlistNode node = this.getNetlist().getVertexAtIdx(i);
			node.setIdx(i);
		}

		// build initial TechMap
		this.setTMNetlist(new TMNetlist(this.getNetlist()));

		// assign logic
		LogicSimulator ls = new LogicSimulator(this.getTMNetlist());
		ls.run();

		// assign input and output components
		TMUtils.assignInputSensors(this.getTMNetlist(),this.getInputLibrary());
		TMUtils.assignOutputReporters(this.getTMNetlist(),this.getOutputLibrary());

		// initialize promoter activity and toxicity
		TMUtils.initInputActivities(this.getTMNetlist(),TargetDataReader.getInputPromoterActivities(this.getTargetData()));
		TMUtils.initOutputToxicity(this.getTMNetlist());
	}

	@Override
	protected void run() {
		logInfo("begin simulated annealing");
		Random rand = new Random();

		Double logMaxTemp = Math.log10(this.getMaxTemp());
		Double logMinTemp = Math.log10(this.getMinTemp());
		Double logInc = (logMaxTemp - logMinTemp) / this.getNumSteps();

		List<TMNetlist> bestAssignments = new ArrayList<>();

		TMNetlist netlist = null;

		ActivitySimulator as = new ActivitySimulator();
		ToxicitySimulator ts = new ToxicitySimulator();

		for(int k = 0; k < this.getNumTrajectories(); k++) {
			logInfo("trajectory " + String.valueOf(k+1) + " of " + this.getNumTrajectories().toString());

			netlist = new TMNetlist(this.getTMNetlist());
			TMUtils.doRandomAssignment(netlist,this.getGateLibrary());

			as.setTMNetlist(netlist);
			as.run();

			ts.setTMNetlist(netlist);
			ts.run();

			for (int j = 0; j < (this.getNumSteps() + this.getNumT0Steps()); j++) {
				TMNetlist tmpNetlist = new TMNetlist(netlist);
				List<TMNode> logicNodes = TMUtils.getLogicNodes(tmpNetlist);

				Double logTemp = logMaxTemp - j * logInc;
				Double temperature = Math.pow(10, logTemp);
				if (j >= this.getNumSteps()) {
					temperature = 0.0;
				}

				// get a random gate
				Integer aIdx = rand.nextInt(logicNodes.size());
				Gate aGate = logicNodes.get(aIdx).getGate();
				// get a second gate, either used or unused
				Gate bGate = TMUtils.getAssignableGate(aGate,tmpNetlist,this.getGateLibrary());

				// 1. if second gate is used, swap
				if (tmpNetlist.hasGate(bGate)) {
					Integer bIdx = 0; // need to know the second gate index
					for(int i = 0; i < logicNodes.size(); i++) {
						if (logicNodes.get(i).getGate().getName()
							.equals(bGate.getName())) {
							bIdx = i;
							break;
						}
					}
					// swap
					logicNodes.get(aIdx).setGate(bGate);
					logicNodes.get(bIdx).setGate(aGate);

				}
				// 2. if second gate is unused, substitute
				else {
					logicNodes.get(aIdx).setGate(bGate);
				}

				as.setTMNetlist(tmpNetlist);
				as.run();

				ts.setTMNetlist(tmpNetlist);
				ts.run();

				// roadblock check
				if (this.getCheckRoadblocks()) {
					Integer tmpRb = TMUtils.getNumRoadblocks(tmpNetlist,this.getLogicRoadblocks(),this.getInputRoadblocks());
					Integer rb = TMUtils.getNumRoadblocks(netlist,this.getLogicRoadblocks(),this.getInputRoadblocks());

					if(tmpRb > rb) {
						continue;
					}
					else if(tmpRb < rb) {
						netlist = tmpNetlist;
						continue; // accept, but don't proceed to evaluate based on score
					}
				}

				// toxicity check
				Double growth = TMUtils.minGrowth(netlist);
				Double tempGrowth = TMUtils.minGrowth(tmpNetlist);

				if (this.getCheckToxicity()) {
					if (growth < this.getToxicityThreshold()) {
						if (tempGrowth > growth) { // accept
							netlist = tmpNetlist;
							continue;
						} else { // reject
							continue;
						}
					} else {
						if (tempGrowth < this.getToxicityThreshold()) {
							continue; // reject
						}
					}
				}

				// simulated annealing accept or reject
				Double probability = Math.exp( (ScoreUtils.getScore(tmpNetlist)
												-
												ScoreUtils.getScore(netlist))
											   / temperature ); // e^b
				Double ep = Math.random();

				if (ep < probability) {
					Integer finalBlocks = TMUtils.getNumRoadblocks(tmpNetlist,
																   this.getLogicRoadblocks(),
																   this.getInputRoadblocks());
					if ((!this.getCheckRoadblocks() || finalBlocks == 0)
						&&
						(!this.getCheckToxicity() || TMUtils.minGrowth(tmpNetlist) > this.getToxicityThreshold()))
						{
							netlist = tmpNetlist;
						}
				}
			}
			bestAssignments.add(netlist);
		}

		// pick highest scoring assignment from all trajectories
		for (TMNetlist l : bestAssignments) {
			if (ScoreUtils.getScore(l) > ScoreUtils.getScore(netlist)) {
				netlist = l;
			}
		}

		this.setTMNetlist(netlist);
	}

	@Override
	protected void postprocessing() {
		logInfo("top score: " + ScoreUtils.getScore(this.getTMNetlist()));

		for (int i = 0; i < this.getTMNetlist().getNumVertex(); i++) {
			TMNode node = this.getTMNetlist().getVertexAtIdx(i);
			String msg = "";
			msg += "NetlistNode ";
			msg += node.getName();
			msg += " (" + node.getNodeType() + ")";
			msg += " was assigned gate ";
			msg += node.getGate().getName();
			logInfo(msg);
			msg  = "  logic output: ";
			msg += node.getLogic();
			logInfo(msg);
			msg  = "  promoter activity: ";
			msg += node.getActivity();
			logInfo(msg);
			msg  = "  toxicity: ";
			msg += node.getToxicity();
			logInfo(msg);
		}

		logInfo("updating netlist");
		TMUtils.updateNetlist(this.getNetlist(),this.getTMNetlist());
	}

	/* Getter & Setter */

	/**
	 * @return the parts
	 */
	protected CObjectCollection<Part> getPartLibrary() {
		return partLibrary;
	}

	/**
	 * @param parts the parts to set
	 */
	protected void setPartLibrary(final CObjectCollection<Part> parts) {
		this.partLibrary = parts;
	}

	/**
	 * @return the gates
	 */
	protected CObjectCollection<Gate> getGateLibrary() {
		return gateLibrary;
	}

	/**
	 * @param gates the gates to set
	 */
	protected void setGateLibrary(final CObjectCollection<Gate> gates) {
		this.gateLibrary = gates;
	}

	/**
	 * @return the tmNetlist
	 */
	public TMNetlist getTMNetlist() {
		return tmNetlist;
	}

	/**
	 * @param tmNetlist the tmNetlist to set
	 */
	public void setTMNetlist(TMNetlist tmNetlist) {
		this.tmNetlist = tmNetlist;
	}

	/**
	 * @return the trajectories
	 */
	protected Integer getNumTrajectories() {
		return numTrajectories;
	}

	/**
	 * @param trajectories the trajectories to set
	 */
	protected void setNumTrajectories(final Integer trajectories) {
		this.numTrajectories = trajectories;
	}

	protected Integer getNumSteps() {
		return numSteps;
	}

	protected void setNumSteps(final Integer numSteps) {
		this.numSteps = numSteps;
	}

	/**
	 * @return the inputLibrary
	 */
	protected CObjectCollection<Gate> getInputLibrary() {
		return inputLibrary;
	}

	/**
	 * @param inputLibrary the inputLibrary to set
	 */
	protected void setInputLibrary(final CObjectCollection<Gate> inputLibrary) {
		this.inputLibrary = inputLibrary;
	}

	/**
	 * @return the outputLibrary
	 */
	protected CObjectCollection<Gate> getOutputLibrary() {
		return outputLibrary;
	}

	/**
	 * @param outputLibrary the outputLibrary to set
	 */
	protected void setOutputLibrary(final CObjectCollection<Gate> outputLibrary) {
		this.outputLibrary = outputLibrary;
	}

	protected Double getMaxTemp() {
		return maxTemp;
	}

	protected void setMaxTemp(final Double maxTemp) {
		this.maxTemp = maxTemp;
	}

	protected Double getMinTemp() {
		return minTemp;
	}

	protected void setMinTemp(final Double minTemp) {
		this.minTemp = minTemp;
	}

	protected Integer getNumT0Steps() {
		return numT0Steps;
	}

	protected void setNumT0Steps(final Integer numT0Steps) {
		this.numT0Steps = numT0Steps;
	}

	/**
	 * @return whether to perform toxicity checks during assignment
	 */
	protected Boolean getCheckToxicity() {
		return checkToxicity;
	}

	/**
	 * @param checkToxicity whether to perform toxicity checks during assignment
	 */
	protected void setCheckToxicity(final Boolean checkToxicity) {
		this.checkToxicity = checkToxicity;
	}

	/**
	 * @return the toxicity (growth) threshold, below which a gate assignment should not be considered
	 */
	protected Double getToxicityThreshold() {
		return toxicityThreshold;
	}

	/**
	 * @param toxicityThreshold the toxicity threshold
	 */
	protected void setToxicityThreshold(final Double toxicityThreshold) {
		this.toxicityThreshold = toxicityThreshold;
	}

	/**
	 * @return the checkRoadblocks
	 */
	protected Boolean getCheckRoadblocks() {
		return checkRoadblocks;
	}

	/**
	 * @param checkRoadblocks the checkRoadblocks to set
	 */
	protected void setCheckRoadblocks(final Boolean checkRoadblocks) {
		this.checkRoadblocks = checkRoadblocks;
	}

	/**
	 * @return the inputRoadblocks
	 */
	public Collection<String> getInputRoadblocks() {
		return inputRoadblocks;
	}

	/**
	 * @param inputRoadblocks the inputRoadblocks to set
	 */
	public void setInputRoadblocks(Collection<String> inputRoadblocks) {
		this.inputRoadblocks = inputRoadblocks;
	}

	/**
	 * @return the logicRoadblocks
	 */
	public Collection<String> getLogicRoadblocks() {
		return logicRoadblocks;
	}

	/**
	 * @param logicRoadblocks the logicRoadblocks to set
	 */
	public void setLogicRoadblocks(Collection<String> logicRoadblocks) {
		this.logicRoadblocks = logicRoadblocks;
	}

	private CObjectCollection<Part> partLibrary;
	private CObjectCollection<Gate> gateLibrary;
	private CObjectCollection<Gate> inputLibrary;
	private CObjectCollection<Gate> outputLibrary;
	private TMNetlist tmNetlist;

	// annealing algorithm
	private Integer numTrajectories;
	private Integer numSteps;
	private Integer numT0Steps;
	private Double maxTemp;
	private Double minTemp;

	// toxicity
	private Boolean checkToxicity;
	private Double toxicityThreshold;

	// roadblocks
	private Boolean checkRoadblocks;
	private Collection<String> inputRoadblocks;
	private Collection<String> logicRoadblocks;

}
