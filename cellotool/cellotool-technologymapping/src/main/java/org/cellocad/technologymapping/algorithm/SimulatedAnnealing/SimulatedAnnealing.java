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
import java.util.Map;
import java.util.Random;

import org.cellocad.common.CObjectCollection;
import org.cellocad.common.Pair;
import org.cellocad.common.netlist.NetlistNode;

import org.cellocad.technologymapping.algorithm.TMAlgorithm;
import org.cellocad.technologymapping.common.ActivitySimulator;
import org.cellocad.technologymapping.common.LogicSimulator;
import org.cellocad.technologymapping.common.TMUtils;
import org.cellocad.technologymapping.common.UCFReader;
import org.cellocad.technologymapping.data.Gate;
import org.cellocad.technologymapping.data.Part;
import org.cellocad.technologymapping.data.TechNode;

/**
 * @author: Timothy Jones
 * 
 * @date: Mar 12, 2018
 *
 */
// TODO: make a container for the netlist and tech node map, e.g. TechMap or Assignment?
// TODO: compute toxicity, check roadblocking
public class SimulatedAnnealing extends TMAlgorithm{

	@Override
	protected void setDefaultParameterValues() {
		this.setNumTrajectories(50);
		this.setNumSteps(50);
		this.setNumT0Steps(100);
		this.setMaxTemp(100.0);
		this.setMinTemp(0.001);
	}

	@Override
	protected void setParameterValues() {
		this.setPartLibrary(UCFReader.getParts(this.getTargetData()));
		this.setGateLibrary(UCFReader.getGates(this.getTargetData()));
		this.setInputLibrary(UCFReader.getInputSensors(this.getTargetData()));
		this.setOutputLibrary(UCFReader.getOutputReporters(this.getTargetData()));
		
		try {
			Pair<Boolean,Integer> param = this.getAlgorithmProfile().getIntParameter("paramectories");
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
		this.setTechNodeMap(TMUtils.buildTechNodeMap(this.getNetlist()));

		new LogicSimulator(this.getNetlist(),techNodeMap);
		TMUtils.assignInputSensors(this.getNetlist(),this.getTechNodeMap(),this.getInputLibrary());
		TMUtils.assignOutputReporters(this.getNetlist(),this.getTechNodeMap(),this.getOutputLibrary());
		TMUtils.assignInputActivities(this.getNetlist(),this.getTechNodeMap(),UCFReader.getInputPromoterActivities(this.getTargetData()));
	}

	@Override
	protected void run() {
		logInfo("begin simulated annealing");
		Random rand = new Random();

		Double logMaxTemp = Math.log10(this.getMaxTemp());
		Double logMinTemp = Math.log10(this.getMinTemp());
		Double logInc = (logMaxTemp - logMinTemp) / this.getNumSteps();

		List<Map<String,TechNode>> bestMaps = new ArrayList<>();
		
		Map<String,TechNode> map = null;
		for(int k = 0; k < this.getNumTrajectories(); k++) {
			logInfo("trajectory " + String.valueOf(k+1) + " of " + this.getNumTrajectories().toString());
			map = this.getTechNodeMap();
			TMUtils.doRandomAssignment(this.getNetlist(),map,this.getGateLibrary());
			new ActivitySimulator(this.getNetlist(),map);
			for (int j = 0; j < (this.getNumSteps() + this.getNumT0Steps()); j++) {
				Map<String,TechNode> tempMap = TMUtils.buildTechNodeMap(this.getNetlist(),map);
				
				Double logTemp = logMaxTemp - j * logInc;
                Double temp = Math.pow(10, logTemp);
				if (j >= this.getNumSteps()) {
                    temp = 0.0;
                }
				List<NetlistNode> logicNodes = TMUtils.getLogicNodes(this.getNetlist());
				
				// get a random gate
				Integer aIdx = rand.nextInt(logicNodes.size());
                Gate aGate = tempMap.get(logicNodes.get(aIdx).getName()).getGate();
				// get a second gate, either used or unused
                Gate bGate = TMUtils.getSwapOrSubGate(tempMap,this.getGateLibrary(),aGate);
                // 1. if second gate is used, swap
                if (TMUtils.hasGate(tempMap.values(), bGate)) {
					Integer bIdx = 0; //need to know the second gate index
					for(int i = 0; i < logicNodes.size(); i++) {
                        if (tempMap.get(logicNodes.get(i).getName())
							.getGate().getName().
							equals(bGate.getName())) {
                            bIdx = i;
							break;
                        }
                    }
					tempMap.get(logicNodes.get(aIdx).getName()).setGate(bGate);
					tempMap.get(logicNodes.get(bIdx).getName()).setGate(aGate);
				}
				// 2. if second gate is unused, substitute
                else {
					tempMap.get(logicNodes.get(aIdx).getName()).setGate(bGate);
				}
				new ActivitySimulator(this.getNetlist(),tempMap);

				Double probability = Math.exp( (TMUtils.getScore(this.getNetlist(),tempMap)
												-
												TMUtils.getScore(this.getNetlist(),map))
											   / temp ); // e^b
                Double ep = Math.random();

                if (ep < probability) {
					map = tempMap;
				}
			}
			bestMaps.add(map);
		}
		for (Map<String,TechNode> m : bestMaps) {
			if (TMUtils.getScore(this.getNetlist(),m) > TMUtils.getScore(this.getNetlist(),map)) {
				map = m;
			}
		}
		this.setTechNodeMap(map);
	}
	
	@Override
	protected void postprocessing() {
		logInfo("updating netlist");
		TMUtils.updateNetlist(this.getNetlist(),this.getTechNodeMap());
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
	 * @return the techNodeMap
	 */
	protected Map<String,TechNode> getTechNodeMap() {
		return techNodeMap;
	}

	/**
	 * @param techNodeMap the techNodeMap to set
	 */
	protected void setTechNodeMap(final Map<String,TechNode> techNodeMap) {
		this.techNodeMap = techNodeMap;
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
	private Map<String,TechNode> techNodeMap;

	private Integer numTrajectories;
	private Integer numSteps;
	private Integer numT0Steps;	

	private Double maxTemp;
	private Double minTemp;
	
}
