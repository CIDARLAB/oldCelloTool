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
import org.cellocad.technologymapping.common.TMUtils;
import org.cellocad.technologymapping.common.techmap.TechMap;
import org.cellocad.technologymapping.common.techmap.TechNode;
import org.cellocad.technologymapping.data.Toxicity;

/**
 * Gate Toxicity: 1=nontoxic growth, 0=no growth. Circuit Toxicity is
 * multiplicative for all gates.
 *
 * Toxicity data for a repressor takes the form of an array of OD
 * measurements, where each OD measurement corresponds to an input RPU
 * level. Toxicity evaluation is performed by a weighted average of
 * the nearest OD datapoints given the incoming RPU.
 *
 * In the Toxicity table, OD measurements are normalized by the
 * uninduced OD measurement for each repressor. Value of 1.0 means
 * non-toxic. Value below 1.0 indicates the degree of Toxicity. To
 * compute Circuit Toxicity for each row in the truth table, multiply
 * Toxicity values for reach repressor in that row of the truth table.
 *
 * @author: Timothy Jones
 * 
 * @date: Mar 16, 2018
 *
 */
// TODO: replace Lists with a matrix library?
public class ToxicitySimulator {

	private static Double MAX_TOXICITY = 1.00;
	private static Double MIN_TOXICITY = 0.01;

	/**
	 * Create a new ToxicitySimulator and assign activities.
	 * 
	 * @param techMap the TechMap on which to assign activities.
	 * @param netlist the netlist corresponding to the TechMap.
	 */
	public ToxicitySimulator(TechMap techMap, final Netlist netlist) {
		Utils.isNullRuntimeException(netlist, "netlist");
		Utils.isNullRuntimeException(techMap, "techMap");
		int num = netlist.getNumVertex();
		for (int i = 0; i < num; i++) {
			NetlistNode node = netlist.getVertexAtIdx(i);
			Utils.isNullRuntimeException(techMap.findTechNodeByName(node.getName()),
										 "TechNode for NetlistNode " + node.getName());
		}

		assignToxicity(techMap,netlist);
	}

	/**
	 * Assign toxicities for a TechMap.
	 * 
	 * @param techMap the TechMap on which to assign toxicities.
	 * @param netlist the netlist corresponding to the TechMap.
	 */
	private static void assignToxicity(TechMap techMap, Netlist netlist) {
		List<Double> outputToxicity = computeToxicity(techMap,netlist);

		List<NetlistNode> outNodes = TMUtils.getOutputNodes(netlist);
		for (NetlistNode node : outNodes) {
			TechNode tn = techMap.findTechNodeByName(node.getName());
			tn.setToxicity(outputToxicity);
		}
	}

	/**
	 * Compute toxicities for a TechMap.
	 * 
	 * @param techMap the TechMap on which to assign toxicities.
	 * @param netlist the netlist corresponding to the TechMap.
	 */
	private static List<Double> computeToxicity(TechMap techMap, Netlist netlist) {
		List<Double> rtn = new ArrayList<Double>();

		// TechNode out = techMap.findTechNodeByName(TMUtils.getOutputNodes(netlist).get(0).getName());
		List<NetlistNode> nodes = TMUtils.getLogicNodes(netlist);

		List<List<Double>> nodeToxicities = new ArrayList<>();
		for (NetlistNode node : nodes) {
			TechNode tn = techMap.findTechNodeByName(node.getName());
			List<Double> toxicity = computeNodeToxicity(tn,node,techMap);
			tn.setToxicity(toxicity);
			nodeToxicities.add(tn.getToxicity());
		}
		isRaggedListException(nodeToxicities);
		for (int i = 0; i < nodeToxicities.get(0).size(); i++) {
			Double colToxicity = 1.0;
			List<Double> col = getColumn(nodeToxicities,i);
			colToxicity = col.stream().reduce(1.0, (a, b) -> a * b);
			if(colToxicity < MIN_TOXICITY) {
				colToxicity = MIN_TOXICITY;
			}
			rtn.add(colToxicity);
		}

		// for (int i = 0; i < out.getToxicity().size(); i++) {
		//		Double rowToxicity = 1.0;
		//		for (NetlistNode node : nodes) {
		//			TechNode tn = techMap.findTechNodeByName(node.getName());
		//			List<Double> toxicity = computeNodeToxicity(tn,node,techMap);
		//			tn.setToxicity(toxicity);
		//			rowToxicity = rowToxicity * tn.getToxicity().get(i);
		//			if(rowToxicity < MIN_TOXICITY) {
		//			   rowToxicity = MIN_TOXICITY;
		//		   }
		//		}
		//		outputToxicity.add(rowToxicity);
		// }

		return rtn;
	}

	/**
	 * Compute and assign toxicity for an individual TechNode.
	 * 
	 * @param techNode the TechNode to which to assign toxicity.
	 */
	private static List<Double> computeNodeToxicity(final TechNode techNode, final NetlistNode node, final TechMap techMap) {
		List<Double> rtn = new ArrayList<>();
		List<Double> inputActivity = collectInputActivities(node,techMap);
		if (techNode.getGate().getToxicity() == null) {
			for (int i = 0; i < inputActivity.size(); ++i) {
				rtn.add(1.0);
			}
		} else {
			Toxicity t = techNode.getGate().getToxicity();
			int minIdx = t.argMinFirst();
			int maxIdx = t.argMaxFirst();
			for (int i = 0; i < inputActivity.size(); ++i) {
				Double a = inputActivity.get(i);
				Double score = 1.0;
				if (a < t.getRow(minIdx).getFirst()) {
					score = t.getRow(minIdx).getSecond();
				} else if (a > t.getRow(maxIdx).getFirst()) {
					score = t.getRow(maxIdx).getSecond();
				} else {
					// assume unsorted toxicity table
					int supIdx = t.argSupremumFirst(a);

					Double supActivity = Math.log10(t.getRow(supIdx).getFirst());
					Double supToxicity = t.getRow(supIdx).getSecond();

					int infIdx = t.argInfimumFirst(a);
					Double infActivity = Math.log10(t.getRow(infIdx).getFirst());
					Double infToxicity = t.getRow(infIdx).getSecond();

					Double weight = (Math.log10(a) - infActivity) / (supActivity - infActivity);
					Double weightedAvg = (infToxicity * (1 - weight)) + (supToxicity * weight);

					score = weightedAvg;
				}

				if (score > MAX_TOXICITY)
					score = MAX_TOXICITY;
				if (score < MIN_TOXICITY)
					score = MIN_TOXICITY;

				rtn.add(score);
			}
		}
		return rtn;
	}

	/**
	 * Compute and assign toxicity for an individual TechNode.
	 * 
	 * @param techNode the TechNode to which to assign toxicity.
	 */
	private static List<Double> collectInputActivities(NetlistNode node, TechMap techMap) {
		List<Double> rtn = new ArrayList<>();

		List<List<Double>> activities = new ArrayList<>();
		int num = node.getNumInEdge();
		for (int i = 0; i < num; i++) {
			NetlistEdge e = node.getInEdgeAtIdx(i);
			NetlistNode src = e.getSrc();
			activities.add(techMap.findTechNodeByName(src.getName()).getActivity());
		}
		isRaggedListException(activities);

		for (int i = 0; i < activities.get(0).size(); i++) {
			List<Double> col = getColumn(activities,i);
			rtn.add(col.stream().mapToDouble(Double::doubleValue).sum());
		}
		return rtn;
	}

	/**
	 * Get a column from a matrix (List of Lists).
	 * 
	 * @param input the set of inputs.
	 * @param i the column to select.
	 */
	private static List<Double> getColumn(List<List<Double>> input, int i) {
		List<Double> rtn = new ArrayList<>();
		for (int j = 0; j < input.size(); j++) {
			rtn.add(input.get(j).get(i));
		}
		return rtn;
	}

	/**
	 * Throw an exception if the toxicity list is ragged.
	 * 
	 * @param input the set of inputs to check.
	 * @return false if the inputs are not ragged.
	 */
	private static boolean isRaggedListException(List<List<Double>> input) {
		boolean rtn = false;
		for (int i = 0; i < input.size(); i++) {
			for (int j = i+1; j < input.size(); j++) {
				if (input.get(i).size() != input.get(j).size()) {
					throw new RuntimeException("Toxicity vectors must be of equal length.");
				}
			}
		}
		return rtn;
	}

}
