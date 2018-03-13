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
package technologyMapping.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import common.Utils;
import common.netlist.Netlist;
import common.netlist.NetlistEdge;
import common.netlist.NetlistNode;

import technologyMapping.common.graph.algorithm.UpstreamDFS;
import technologyMapping.data.Gate;
import technologyMapping.data.TechNode;

/**
 * @author: Timothy Jones
 * 
 * @date: Mar 9, 2018
 *
 */
public class ActivitySimulator {

	public ActivitySimulator(final Netlist netlist, Map<String,TechNode> techNodeMap) {
		Utils.isNullRuntimeException(netlist, "netlist");
		Utils.isNullRuntimeException(techNodeMap, "techNodeMap");
		int num = netlist.getNumVertex();
		for (int i = 0; i < num; i++) {
			NetlistNode node = netlist.getVertexAtIdx(i);
			if (!techNodeMap.keySet().contains(node.getName())) {
				throw new RuntimeException("Netlist node '" + node.getName() + "' is not in the tech node map.");
			}
			Utils.isNullRuntimeException(techNodeMap.get(node.getName()), "TechNode for node " + node.getName());
		}
		computeActivity(netlist,techNodeMap);
	}

	private void computeActivity(Netlist netlist, Map<String,TechNode> techNodeMap) {
		UpstreamDFS<NetlistNode,NetlistEdge,Netlist> dfs = new UpstreamDFS<>(netlist);
		NetlistNode node = null;
		while ((node = dfs.getNextVertex()) != null) {
			TechNode tn = techNodeMap.get(node.getName());
			Gate g = tn.getGate();
			Utils.isNullRuntimeException(tn.getLogic(), "gate logic");
			String type = node.getNodeType();
			if (!type.equals("TopInput")) {
				List<List<Double>> inputs = new ArrayList<>();
				for (int i = 0; i < node.getNumInEdge(); i++) {
					NetlistEdge e = node.getInEdgeAtIdx(i);
					NetlistNode src = e.getSrc();
					inputs.add(techNodeMap.get(src.getName()).getActivity());
				}

				if (node.getNodeType().equals("TopOutput")) {
					tn.setActivity(getOutputActivity(inputs,g));
				} else {
					tn.setActivity(getGateActivity(inputs,g));
				}
			}
		}
	}

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

	private static List<Double> getOutputActivity(List<List<Double>> input, Gate gate) {
		return getGateActivity(input,gate);
	}

	private static List<Double> getInputColumn(List<List<Double>> input, int i) {
		List<Double> rtn = new ArrayList<>();
		for (int j = 0; j < input.size(); j++) {
			rtn.add(input.get(j).get(i));
		}
		return rtn;
	}
	
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
