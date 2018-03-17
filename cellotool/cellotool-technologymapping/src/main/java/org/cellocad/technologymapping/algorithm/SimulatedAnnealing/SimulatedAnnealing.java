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
import java.util.List;
import java.util.Random;

import org.cellocad.common.CObjectCollection;
import org.cellocad.common.Pair;
import org.cellocad.common.netlist.NetlistNode;
import org.cellocad.technologymapping.algorithm.TMAlgorithm;
import org.cellocad.technologymapping.common.TMUtils;
import org.cellocad.technologymapping.common.TargetDataReader;
import org.cellocad.technologymapping.common.simulation.ActivitySimulator;
import org.cellocad.technologymapping.common.simulation.LogicSimulator;
import org.cellocad.technologymapping.common.simulation.ToxicitySimulator;
import org.cellocad.technologymapping.common.techmap.TechMap;
import org.cellocad.technologymapping.common.techmap.TechNode;
import org.cellocad.technologymapping.data.Gate;
import org.cellocad.technologymapping.data.Part;

/**
 * @author: Timothy Jones
 * 
 * @date: Mar 12, 2018
 *
 */
// TODO: check toxicity, roadblocking in assignment
public class SimulatedAnnealing extends TMAlgorithm{

	@Override
	protected void setDefaultParameterValues() {
		this.setNumTrajectories(50);
		this.setNumSteps(500);
		this.setNumT0Steps(100);
		this.setMaxTemp(100.0);
		this.setMinTemp(0.001);
	}

	@Override
	protected void setParameterValues() {
		this.setPartLibrary(TargetDataReader.getParts(this.getTargetData()));
		this.setGateLibrary(TargetDataReader.getGates(this.getTargetData()));
		this.setInputLibrary(TargetDataReader.getInputSensors(this.getTargetData()));
		this.setOutputLibrary(TargetDataReader.getOutputReporters(this.getTargetData()));

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
	}

	@Override
	protected void preprocessing() {
		int num = this.getNetlist().getNumVertex();
		for (int i = 0; i < num; i++) {
			NetlistNode node = this.getNetlist().getVertexAtIdx(i);
			node.setIdx(i);
		}
		this.setTechMap(new TechMap(this.getNetlist()));

		new LogicSimulator(this.getTechMap(),this.getNetlist());
		TMUtils.assignInputSensors(this.getTechMap(),this.getNetlist(),this.getInputLibrary());
		TMUtils.assignOutputReporters(this.getTechMap(),this.getNetlist(),this.getOutputLibrary());

		TMUtils.initInputActivities(this.getTechMap(),this.getNetlist(),TargetDataReader.getInputPromoterActivities(this.getTargetData()));
		TMUtils.initOutputToxicity(this.getTechMap(),this.getNetlist());
	}

	@Override
	protected void run() {
		logInfo("begin simulated annealing");
		Random rand = new Random();

		Double logMaxTemp = Math.log10(this.getMaxTemp());
		Double logMinTemp = Math.log10(this.getMinTemp());
		Double logInc = (logMaxTemp - logMinTemp) / this.getNumSteps();

		List<TechMap> bestMaps = new ArrayList<>();

		TechMap map = null;

		List<NetlistNode> logicNodes = TMUtils.getLogicNodes(this.getNetlist());
		for(int k = 0; k < this.getNumTrajectories(); k++) {
			logInfo("trajectory " + String.valueOf(k+1) + " of " + this.getNumTrajectories().toString());
			map = new TechMap(this.getTechMap());
			TMUtils.doRandomAssignment(map,this.getNetlist(),this.getGateLibrary());
			new ActivitySimulator(map,this.getNetlist());
			new ToxicitySimulator(map,this.getNetlist());
			for (int j = 0; j < (this.getNumSteps() + this.getNumT0Steps()); j++) {
				TechMap tempMap = new TechMap(map);

				Double logTemp = logMaxTemp - j * logInc;
				Double temperature = Math.pow(10, logTemp);
				if (j >= this.getNumSteps()) {
					temperature = 0.0;
				}

				// get a random gate
				Integer aIdx = rand.nextInt(logicNodes.size());
				Gate aGate = tempMap.findTechNodeByName(logicNodes.get(aIdx).getName()).getGate();
				// get a second gate, either used or unused
				Gate bGate = TMUtils.getSwapOrSubGate(tempMap,this.getGateLibrary(),aGate);

				// 1. if second gate is used, swap
				if (tempMap.hasGate(bGate)) {
					Integer bIdx = 0; //need to know the second gate index
					for(int i = 0; i < logicNodes.size(); i++) {
						if (tempMap.findTechNodeByName(logicNodes.get(i).getName())
							.getGate().getName().
							equals(bGate.getName())) {
							bIdx = i;
							break;
						}
					}
					tempMap.findTechNodeByName(logicNodes.get(aIdx).getName()).setGate(bGate);
					tempMap.findTechNodeByName(logicNodes.get(bIdx).getName()).setGate(aGate);
				}
				// 2. if second gate is unused, substitute
				else {
					tempMap.findTechNodeByName(logicNodes.get(aIdx).getName()).setGate(bGate);
				}
				new ActivitySimulator(tempMap,this.getNetlist());
				new ToxicitySimulator(tempMap,this.getNetlist());

				Double probability = Math.exp( (tempMap.getScore()
												-
												map.getScore())
											   / temperature ); // e^b
				Double ep = Math.random();

				if (ep < probability) {
					map = tempMap;
				}
			}
			bestMaps.add(map);
		}
		for (TechMap m : bestMaps) {
			if (m.getScore() > map.getScore()) {
				map = m;
			}
		}
		this.setTechMap(map);
		logInfo("top score: " + this.getTechMap().getScore());

		for (NetlistNode node : TMUtils.getLogicNodes(this.getNetlist())) {
			TechNode tn = this.getTechMap().findTechNodeByName(node.getName());
			String msg = "";
			msg += "NetlistNode ";
			msg += node.getName();
			msg += " (" + node.getNodeType() + ")";
			msg += " was assigned gate ";
			msg += tn.getGate().getName();
			logInfo(msg);
			msg  = "  logic output: ";
			msg += tn.getLogic();
			logInfo(msg);
			msg  = "  promoter activity: ";
			msg += tn.getActivity();
			logInfo(msg);
			msg  = "  toxicity: ";
			msg += tn.getToxicity();
			logInfo(msg);
		}
	}

	@Override
	protected void postprocessing() {
		logInfo("updating netlist");
		TMUtils.updateNetlist(this.getNetlist(),this.getTechMap());
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
	 * @return the techMap
	 */
	protected TechMap getTechMap() {
		return techMap;
	}

	/**
	 * @param techMap the techMap to set
	 */
	protected void setTechMap(final TechMap techMap) {
		this.techMap = techMap;
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

	private CObjectCollection<Part> partLibrary;
	private CObjectCollection<Gate> gateLibrary;
	private CObjectCollection<Gate> inputLibrary;
	private CObjectCollection<Gate> outputLibrary;
	private TechMap techMap;

	private Integer numTrajectories;
	private Integer numSteps;
	private Integer numT0Steps;

	private Double maxTemp;
	private Double minTemp;

}
