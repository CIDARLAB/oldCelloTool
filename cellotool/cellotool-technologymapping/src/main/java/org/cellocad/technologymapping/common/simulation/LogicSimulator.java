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
import java.util.Iterator;
import java.util.List;

import org.cellocad.common.Utils;
import org.cellocad.technologymapping.common.TMUtils;
import org.cellocad.technologymapping.common.graph.algorithm.UpstreamDFS;
import org.cellocad.technologymapping.common.netlist.TMEdge;
import org.cellocad.technologymapping.common.netlist.TMNetlist;
import org.cellocad.technologymapping.common.netlist.TMNode;

/**
 * @author: Timothy Jones
 *
 * @date: Mar 7, 2018
 *
 */
public class LogicSimulator extends Simulator{

	/**
	 * Create a new LogicSimulator.
	 */
	public LogicSimulator() {
		super();
	}

	/**
	 * Create a new LogicSimulator.
	 *
	 * @param netlist the TMNetlist on which to operate.
	 */
	public LogicSimulator(TMNetlist netlist) {
		super();
		Utils.isNullRuntimeException(netlist, "TMNetlist");
		this.setTMNetlist(netlist);
	}

	public void run() {
		computeBooleanLogic(this.getTMNetlist());
	}

	private static void computeBooleanLogic(TMNetlist netlist) {
		List<List<Boolean>> inputLogic = getInputLogic(TMUtils.getInputNodes(netlist).size());

		UpstreamDFS<TMNode,TMEdge,TMNetlist> dfs = new UpstreamDFS<>(netlist);
		Iterator<List<Boolean>> it = inputLogic.iterator();
		TMNode node = null;
		while ((node = dfs.getNextVertex()) != null) {
			if (node.getNodeType().equals("TopInput")) {
				node.setLogic(it.next());
			} else {
				List<List<Boolean>> inputs = new ArrayList<>();
				for (int i = 0; i < node.getNumInEdge(); i++) {
					TMEdge e = node.getInEdgeAtIdx(i);
					TMNode src = e.getSrc();
					inputs.add(src.getLogic());
				}

				List<Boolean> logic = null;

				if (node.getNodeType().equals("TopOutput")) {
					logic = getOutputLogic(inputs);
				} else {
					logic = getGateLogic(inputs,node.getNodeType());
				}
				node.setLogic(logic);
			}
		}
	}

	private static List<Boolean> getGateLogic(List<List<Boolean>> inputs, String gateType) {
		List<Boolean> rtn = null;
		switch (gateType) {
		case "NOT": {
			isWrongInputNumberException(inputs.size(),1,"NOT");
			rtn = computeLogicalNot(inputs.get(0));
			break;
		}
		case "AND": {
			isWrongInputNumberException(inputs.size(),2,"AND");
			rtn = computeLogicalAnd(inputs);
			break;
		}
		case "NAND": {
			isWrongInputNumberException(inputs.size(),2,"NAND");
			rtn = computeLogicalNot(computeLogicalAnd(inputs));
			break;
		}
		case "OR": {
			isWrongInputNumberException(inputs.size(),2,"OR");
			rtn = computeLogicalOr(inputs);
			break;
		}
		case "NOR": {
			isWrongInputNumberException(inputs.size(),2,"NOR");
			rtn = computeLogicalNot(computeLogicalOr(inputs));
			break;
		}
		case "XOR": {
			isWrongInputNumberException(inputs.size(),2,"XOR");
			rtn = computeLogicalXor(inputs);
			break;
		}
		case "XNOR": {
			isWrongInputNumberException(inputs.size(),2,"XNOR");
			rtn = computeLogicalNot(computeLogicalXor(inputs));
			break;
		}
		default: {
			throw new RuntimeException("Unknown gate type.");
		}
		}
		return rtn;
	}

	private static List<Boolean> computeLogicalNot(List<Boolean> input) {
		List<Boolean> rtn = new ArrayList<>();
		for (Boolean b : input) {
			rtn.add(!b);
		}
		return rtn;
	}

	private static List<Boolean> computeLogicalAnd(List<List<Boolean>> input) {
		isRaggedInputListException(input);
		List<Boolean> rtn = new ArrayList<>();
		for (int j = 0; j < input.get(0).size(); j++) {
			List<Boolean> col = getInputColumn(input,j);
			rtn.add(col.stream().reduce((a,b)-> a & b).orElse(true));
		}
		return rtn;
	}

	private static List<Boolean> computeLogicalOr(List<List<Boolean>> input) {
		isRaggedInputListException(input);
		List<Boolean> rtn = new ArrayList<>();
		for (int j = 0; j < input.get(0).size(); j++) {
			List<Boolean> col = getInputColumn(input,j);
			rtn.add(col.stream().reduce((a,b)-> a | b).orElse(false));
		}
		return rtn;
	}

	private static List<Boolean> computeLogicalXor(List<List<Boolean>> input) {
		isRaggedInputListException(input);
		List<Boolean> rtn = new ArrayList<>();
		for (int j = 0; j < input.get(0).size(); j++) {
			List<Boolean> col = getInputColumn(input,j);
			rtn.add(col.stream().reduce((a,b)-> a ^ b).orElse(false));
		}
		return rtn;
	}

	private static List<Boolean> getInputColumn(List<List<Boolean>> input, int i) {
		List<Boolean> rtn = new ArrayList<>();
		for (int j = 0; j < input.size(); j++) {
			rtn.add(input.get(j).get(i));
		}
		return rtn;
	}

	private static List<Boolean> getOutputLogic(List<List<Boolean>> input) {
		// 'output or', though this should probably get an explicit or gate in the logic synthesis stage
		return computeLogicalOr(input);
	}

	private static List<List<Boolean>> getInputLogic(int num) {
		List<List<Boolean>> rtn = new ArrayList<>();
		for (int i = 0; i < num; i++) {
			List<Boolean> logic = new ArrayList<>();
			for (int k = 0; k < Math.pow(2,num-1-i); k++) {
				for (int j = 0; j < Math.pow(2,i); j++) {
					logic.add(false);
				}
				for (int j = 0; j < Math.pow(2,i); j++) {
					logic.add(true);
				}
			}
			rtn.add(logic);
		}
		return rtn;
	}

	private static boolean isWrongInputNumberException(int a, int b, String type) {
		boolean rtn = false;
		rtn = (a != b);
		if (rtn) {
			throw new RuntimeException("Number of inputs to " + type + " gate must be " + b);
		}
		return rtn;
	}

	private static boolean isRaggedInputListException(List<List<Boolean>> input) {
		boolean rtn = false;
		for (int i = 0; i < input.size(); i++) {
			for (int j = i+1; j < input.size(); j++) {
				if (input.get(i).size() != input.get(j).size()) {
					throw new RuntimeException("Boolean input vectors must be of equal length.");
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
