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
package technologyMapping.algorithm.SimulatedAnnealing;

import java.util.HashMap;
import java.util.Map;

import common.CObjectCollection;
import common.Pair;
import common.netlist.NetlistNode;

import technologyMapping.algorithm.TMAlgorithm;
import technologyMapping.common.ActivitySimulator;
import technologyMapping.common.LogicSimulator;
import technologyMapping.common.TMUtils;
import technologyMapping.common.UCFReader;
import technologyMapping.data.Gate;
import technologyMapping.data.Part;
import technologyMapping.data.TechNode;

/**
 * @author: Timothy Jones
 * 
 * @date: Mar 6, 2018
 *
 */
public class SimulatedAnnealing extends TMAlgorithm{

	@Override
	protected void setDefaultParameterValues() {
		this.setNumTrajectories(50);
	}

	@Override
	protected void setParameterValues() {
		this.setPartLibrary(UCFReader.getParts(this.getTargetData()));
		this.setGateLibrary(UCFReader.getGates(this.getTargetData()));
		this.setInputLibrary(UCFReader.getInputSensors(this.getTargetData()));
		this.setOutputLibrary(UCFReader.getOutputReporters(this.getTargetData()));
		
		try {
			Pair<Boolean,Integer> traj = this.getAlgorithmProfile().getIntParameter("trajectories");
			if (traj.getFirst()) {this.setNumTrajectories(traj.getSecond());}
		} catch (NullPointerException e) {}

	}

	@Override
	protected void validateParameterValues() {
		
	}

	@Override
	protected void preprocessing() {
		// build TechNode map
		int num = this.getNetlist().getNumVertex();
		Map<String,TechNode> techNodeMap = new HashMap<>();
		for (int i = 0; i < this.getNetlist().getNumVertex(); i++) {
			NetlistNode node = this.getNetlist().getVertexAtIdx(i);
			node.setIdx(i);
			TechNode tn = new TechNode();
			tn.setIdx(i);
			tn.setName(node.getName());
			techNodeMap.put(node.getName(),tn);
		}
		this.setTechNodeMap(techNodeMap);

		LogicSimulator ls = new LogicSimulator(this.getNetlist(),techNodeMap);
		TMUtils.assignInputSensors(this.getNetlist(),this.getTechNodeMap(),this.getInputLibrary());
		TMUtils.assignOutputReporters(this.getNetlist(),this.getTechNodeMap(),this.getOutputLibrary());
		TMUtils.assignInputActivities(this.getNetlist(),this.getTechNodeMap(),UCFReader.getInputPromoterActivities(this.getTargetData()));
	}

	@Override
	protected void run() {
		Double score = 0.0;

		TMUtils.doRandomAssignment(this.getNetlist(),this.getTechNodeMap(),this.getGateLibrary());
		ActivitySimulator as = new ActivitySimulator(this.getNetlist(),this.getTechNodeMap());

		for(int j = 0; j < this.getNumTrajectories(); j++) {
		}
	}

	@Override
	protected void postprocessing() {
		// write technodemap to netlist
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

	private CObjectCollection<Part> partLibrary;
	private CObjectCollection<Gate> gateLibrary;
	private CObjectCollection<Gate> inputLibrary;
	private CObjectCollection<Gate> outputLibrary;
	private Map<String,TechNode> techNodeMap;

	private Integer numTrajectories;
	private Integer numSteps;
	
}
