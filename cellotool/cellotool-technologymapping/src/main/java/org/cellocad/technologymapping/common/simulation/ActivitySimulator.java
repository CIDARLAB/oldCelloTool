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
import java.util.Map;

import org.cellocad.common.Pair;
import org.cellocad.common.Utils;
import org.cellocad.technologymapping.common.graph.algorithm.UpstreamDFS;
import org.cellocad.technologymapping.common.netlist.TMEdge;
import org.cellocad.technologymapping.common.netlist.TMNetlist;
import org.cellocad.technologymapping.common.netlist.TMNode;
import org.cellocad.technologymapping.data.Gate;

/**
 * @author: Timothy Jones
 *
 * @date: Mar 9, 2018
 *
 */
public class ActivitySimulator extends Simulator{

	/**
	 * Create a new ActivitySimulator.
	 */
	public ActivitySimulator() {
		super();
	}

	/**
	 * Create a new ActivitySimulator.
	 *
	 * @param netlist the TMNetlist on which to operate.
	 */
	public ActivitySimulator(TMNetlist netlist) {
		super();
		Utils.isNullRuntimeException(netlist, "TMNetlist");
		this.setTMNetlist(netlist);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see Simulator#run()
	 */
	public void run() {
		computeActivity(this.getTMNetlist());
	}

	/**
	 * Set activities to all input nodes based on their boolean logic values.
	 *
	 * @param reference the map from input name to reference low-high activity pair.
	 */
	public void initInputActivities(Map<String,Pair<Double,Double>> reference) {
		List<TMNode> nodes = this.getTMNetlist().getInputNodes();
		for (TMNode node : nodes) {
			Pair<Double,Double> inputRef = reference.get(node.getGate().getName());
			Utils.isNullRuntimeException(inputRef, "Input activity reference for " + node.getGate().getName());
			List<Boolean> logic = node.getLogic();
			node.setActivity(getInputActivity(logic,inputRef));
		}
	}

	/**
	 * Get input promoter activities from a logic stream and a low-high reference pair.
	 *
	 * @param logic the boolean list from which to derive input activities.
	 * @param reference the activity values for false and true boolean states.
	 * @return the list of promoter activities.
	 */
	private List<Double> getInputActivity(List<Boolean> logic, Pair<Double,Double> reference) {
		List<Double> activity = new ArrayList<>();
		for (Boolean b : logic) {
			if (b) {
				activity.add(reference.getSecond());
			} else {
				activity.add(reference.getFirst());
			}
		}
		return activity;
	}

	/**
	 * Compute and assign promoter activities.
	 *
	 * @param netlist the TMNetlist on which to assign activities.
	 */
	private static void computeActivity(TMNetlist netlist) {
		UpstreamDFS<TMNode,TMEdge,TMNetlist> dfs = new UpstreamDFS<>(netlist);
		TMNode node = null;

		while ((node = dfs.getNextVertex()) != null) {
			Gate g = node.getGate();
			String type = node.getNodeType();
			if (!type.equals("TopInput")) {
				List<List<Double>> inputs = new ArrayList<>();
				for (int i = 0; i < node.getNumInEdge(); i++) {
					TMEdge e = node.getInEdgeAtIdx(i);
					TMNode src = e.getSrc();
					Utils.isNullRuntimeException(src.getActivity(), "Input activity");
					inputs.add(src.getActivity());
				}

				List<Double> activity = null;
				if (node.getNodeType().equals("TopOutput")) {
					activity = getOutputActivity(inputs,g);
				} else {
					activity = getGateActivity(inputs,g);
				}
				node.setActivity(activity);
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

	/**
	 * @return the netlist
	 */
	public TMNetlist getTMNetlist() {
		return tmNetlist;
	}

	/**
	 * @param netlist the netlist to set
	 */
	public void setTMNetlist(TMNetlist netlist) {
		this.tmNetlist = netlist;
	}

	private TMNetlist tmNetlist;

}
