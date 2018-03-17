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
package org.cellocad.technologymapping.common.simulation;

import java.util.ArrayList;
import java.util.List;

import org.cellocad.common.Utils;
import org.cellocad.common.netlist.Netlist;
import org.cellocad.common.netlist.NetlistEdge;
import org.cellocad.common.netlist.NetlistNode;
import org.cellocad.technologymapping.common.graph.algorithm.UpstreamDFS;
import org.cellocad.technologymapping.common.techmap.TechMap;
import org.cellocad.technologymapping.common.techmap.TechNode;
import org.cellocad.technologymapping.data.Gate;

/**
 * @author: Timothy Jones
 * 
 * @date: Mar 9, 2018
 *
 */
public class ActivitySimulator {

	/**
	 * Create a new ActivitySimulator and assign activities.
	 * 
	 * @param techMap the TechMap on which to assign activities.
	 * @param netlist the netlist corresponding to the TechMap.
	 */
	public ActivitySimulator(TechMap techMap, final Netlist netlist) {
		Utils.isNullRuntimeException(netlist, "netlist");
		Utils.isNullRuntimeException(techMap, "techMap");
		int num = netlist.getNumVertex();
		for (int i = 0; i < num; i++) {
			NetlistNode node = netlist.getVertexAtIdx(i);
			Utils.isNullRuntimeException(techMap.findTechNodeByName(node.getName()), "TechNode for NetlistNode " + node.getName());
		}
		computeActivity(techMap,netlist);
	}

	/**
	 * Compute and assign promoter activities.
	 * 
	 * @param techMap the TechMap on which to assign activities.
	 * @param netlist the netlist corresponding to the TechMap.
	 */
	private static void computeActivity(TechMap techMap, Netlist netlist) {
		UpstreamDFS<NetlistNode,NetlistEdge,Netlist> dfs = new UpstreamDFS<>(netlist);
		NetlistNode node = null;
		while ((node = dfs.getNextVertex()) != null) {
			TechNode tn = techMap.findTechNodeByName(node.getName());
			Gate g = tn.getGate();
			Utils.isNullRuntimeException(tn.getLogic(), "gate logic");
			String type = node.getNodeType();
			if (!type.equals("TopInput")) {
				List<List<Double>> inputs = new ArrayList<>();
				for (int i = 0; i < node.getNumInEdge(); i++) {
					NetlistEdge e = node.getInEdgeAtIdx(i);
					NetlistNode src = e.getSrc();
					inputs.add(techMap.findTechNodeByName(src.getName()).getActivity());
				}

				if (node.getNodeType().equals("TopOutput")) {
					tn.setActivity(getOutputActivity(inputs,g));
				} else {
					tn.setActivity(getGateActivity(inputs,g));
				}
			}
		}
	}

	/**
	 * Get the activity for a logic gate given some input.
	 * 
	 * @param input the inputs to the gate.
	 * @param gate the gate for which to compute input.
	 */
	private static List<Double> getGateActivity(List<List<Double>> input, Gate gate) {
		isRaggedInputListException(input);
		Utils.isNullRuntimeException(gate.getResponseFunction(),"gate response function");
		List<Double> rtn = new ArrayList<>();
		Double x = 0.0;
		for (int i = 0; i < input.get(0).size(); i++) {
			List<Double> col = getInputColumn(input,i);
			x = col.stream().mapToDouble(Double::doubleValue).sum();
			rtn.add(gate.getResponseFunction().apply(x));
		}
		return rtn;
	}

	/**
	 * Get the activity for an output gate.
	 * 
	 * @param input the inputs to the gate.
	 * @param gate the gate for which to compute input.
	 */
	private static List<Double> getOutputActivity(List<List<Double>> input, Gate gate) {
		return getGateActivity(input,gate);
	}

	/**
	 * Get a column of inputs from a matrix.
	 * 
	 * @param input the set of inputs.
	 * @param i the column to select.
	 */
	private static List<Double> getInputColumn(List<List<Double>> input, int i) {
		List<Double> rtn = new ArrayList<>();
		for (int j = 0; j < input.size(); j++) {
			rtn.add(input.get(j).get(i));
		}
		return rtn;
	}
	
	/**
	 * Throw an exception if the input list is ragged.
	 * 
	 * @param input the set of inputs to check.
	 * @return false if the inputs are not ragged.
	 */
	private static boolean isRaggedInputListException(List<List<Double>> input) {
		boolean rtn = false;
		for (int i = 0; i < input.size(); i++) {
			for (int j = i+1; j < input.size(); j++) {
				if (input.get(i).size() != input.get(j).size()) {
					throw new RuntimeException("Activity input vectors must be of equal length.");
				}
			}
		}
		return rtn;
	}

}
