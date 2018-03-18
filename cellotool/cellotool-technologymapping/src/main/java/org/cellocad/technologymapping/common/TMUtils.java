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
package org.cellocad.technologymapping.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.cellocad.common.CObject;
import org.cellocad.common.CObjectCollection;
import org.cellocad.common.Pair;
import org.cellocad.common.Utils;
import org.cellocad.common.netlist.Netlist;
import org.cellocad.common.netlist.NetlistNode;
import org.cellocad.technologymapping.common.netlist.TMEdge;
import org.cellocad.technologymapping.common.netlist.TMNetlist;
import org.cellocad.technologymapping.common.netlist.TMNode;
import org.cellocad.technologymapping.data.Gate;
import org.cellocad.technologymapping.data.GateType;
import org.cellocad.technologymapping.data.Part;

/**
 * @author: Timothy Jones
 * 
 * @date: Mar 12, 2018
 *
 */
public class TMUtils{

	public static boolean isRaggedListException(List<List<?>> input) {
		boolean rtn = false;
		for (int i = 0; i < input.size(); i++) {
			for (int j = i+1; j < input.size(); j++) {
				if (input.get(i).size() != input.get(j).size()) {
					throw new RuntimeException("Vectors must be of equal length.");
				}
			}
		}
		return rtn;
	}

	/**
	 * Get all the input nodes in the netlist.
	 * 
	 * @param netlist the netlist from which to collect input nodes.
	 * @return the input nodes in the netlist.
	 */
	public static List<TMNode> getInputNodes(TMNetlist netlist) {
		List<TMNode> rtn = new ArrayList<>();
		int num = netlist.getNumVertex();
		for (int i = 0; i < num; i++) {
			TMNode node = netlist.getVertexAtIdx(i);
			if (node.getNodeType().equals("TopInput")) {
				rtn.add(node);
			}
		}
		return rtn;
	}

	/**
	 * Get all the output nodes in a netlist.
	 * 
	 * @param netlist the netlist from which to collect output nodes.
	 * @return the output nodes in the netlist.
	 */
	public static List<TMNode> getOutputNodes(TMNetlist netlist) {
		List<TMNode> rtn = new ArrayList<>();
		int num = netlist.getNumVertex();
		for (int i = 0; i < num; i++) {
			TMNode node = netlist.getVertexAtIdx(i);
			if (node.getNodeType().equals("TopOutput")) {
				rtn.add(node);
			}
		}
		return rtn;
	}

	/**
	 * Get all the logic nodes in the netlist.
	 * 
	 * @param netlist the netlist from which to collect logic nodes.
	 * @return the logic nodes in the netlist.
	 */
	public static List<TMNode> getLogicNodes(TMNetlist netlist) {
		List<TMNode> rtn = new ArrayList<>();
		int num = netlist.getNumVertex();
		for (int i = 0; i < num; i++) {
			TMNode node = netlist.getVertexAtIdx(i);
				if (!node.getNodeType().equals("TopOutput")
				&&
				!node.getNodeType().equals("TopInput")) {
				rtn.add(node);
			}
		}
		return rtn;
	}

	/**
	 * Get the set of groups present in a gate library.
	 * 
	 * @param gateLibrary the gate library from which to build the list of groups.
	 * @return the set of groups present in the library.
	 */
	public static Set<String> getGateGroups(Collection<Gate> gateLibrary) {
		Set<String> groups = new HashSet<>();
		for (Gate g : gateLibrary) {
			String group = g.getGroup();
			if (group != null) {groups.add(group);}
		}
		return groups;
	}

	/**
	 * Get a map of library gates to their type.
	 * 
	 * @param gateLibrary the gate library from which to build the type map.
	 * @return a map from gate type to a list of gates of that type.
	 */
	public static Map<String,List<Gate>> getGatesByType(Collection<Gate> gateLibrary) {
		Map<String,List<Gate>> map = new HashMap<>();
		for (Gate g : gateLibrary) {
			String type = GateType.values()[g.getType()].toString();
			if (!map.keySet().contains(type)) {
				map.put(type,new CObjectCollection<>());
			}
			map.get(type).add(g);
		}
		return map;
	}

	/**
	 * Check if there are nodes in the netlist without a gate assignment.
	 * 
	 * @param netlist the netlist to check for assignments.
	 * @return true if there are nodes without an assignment.
	 */
	public static Boolean hasUnassignedNodes(Netlist netlist) {
		int num = netlist.getNumVertex();
		for (int i = 0; i < num; i++) {
			NetlistNode node = netlist.getVertexAtIdx(i);
			String gate = node.getGate();
			if (gate == null) {
				return true;
			}
			if (gate.equals("")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Assign promoter activities to a list of boolean values from a low-high reference pair.
	 * 
	 * @param logic the boolean list to which to assign activities.
	 * @param inputActivitiy the activity values for false and true boolean states.
	 * @return the list of promoter activities.
	 */
	protected static List<Double> getInputActivity(List<Boolean> logic, Pair<Double,Double> inputActivity) {
		List<Double> activity = new ArrayList<>();
		for (Boolean b : logic) {
			if (b) {
				activity.add(inputActivity.getSecond());
			} else {
				activity.add(inputActivity.getFirst());
			}
		}
		return activity;
	}

	/**
	 * Set activities to all input nodes based on their boolean logic values.
	 * 
	 * @param techMap the TechMap on which to assign input activities.
	 * @param netlist the Netlist corresponding to the TechMap.
	 * @param inputActivityReference the map from input name to reference low-high activity pair.
	 */
	public static void initInputActivities(TMNetlist netlist,
										   Map<String,Pair<Double,Double>> inputActivityReference) {
		List<TMNode> nodes = TMUtils.getInputNodes(netlist);
		for (TMNode node : nodes) {
			Pair<Double,Double> inputRef = inputActivityReference.get(node.getGate().getName());
			Utils.isNullRuntimeException(inputRef, "Input activity reference for " + node.getGate().getName());
			List<Boolean> logic = node.getLogic();
			node.setActivity(getInputActivity(logic,inputRef));
		}
	}

	/**
	 * Initialize toxicity at the output nodes.
	 * 
	 * @param netlist the TMNetlist on which to assign output toxicity.
	 */
	public static void initOutputToxicity(TMNetlist netlist) {
		List<TMNode> nodes = TMUtils.getOutputNodes(netlist);
		for (TMNode node : nodes) {
			List<Boolean> logic = node.getLogic();
			List<Double> toxicity = Collections.nCopies(logic.size(),1.0);
			node.setToxicity(toxicity);
		}
	}

	/**
	 * Find the minimum growth (highest toxicity) for a TMNode.
	 * 
	 * @param techNode the TechNode to search.
	 */
	public static Double minGrowth(TMNode node) {
		Double rtn = null;
		List<Double> toxicity = node.getToxicity();
		if (toxicity != null)
			rtn = Collections.min(toxicity);
		return rtn;
	}

	/**
	 * Find the minimum growth (highest toxicity) for a TMNetlist.
	 * 
	 * @param netlist the TMNetlist to search.
	 */
	public static Double minGrowth(TMNetlist netlist) {
		Double rtn = 1.0;

		List<TMNode> nodes = TMUtils.getOutputNodes(netlist);
		for (TMNode node : nodes) {
			Double growth = minGrowth(node);
			if (growth < rtn)
				rtn = growth;
		}
		return rtn;
	}

	/**
	 * Get the total number of roadblocks for the assignment.
	 * 
	 * @return the number of roadblocks.
	 */
	public static Integer getNumRoadblocks(TMNetlist netlist,
										   Collection<String> logicRoadblocks,
										   Collection<String> inputRoadblocks) {
		int rtn = 0;
		int num = netlist.getNumVertex();
		for (int i = 0; i < num; i++) {
			TMNode node = netlist.getVertexAtIdx(i);
			if (getNumRoadblocks(node,logicRoadblocks,inputRoadblocks) > 0) {
				rtn++;
			}
		}
		return rtn;
	}

	/**
	 * Get the number of roadblocks for a particular TMNode.
	 * 
	 * @return the number of roadblocks.
	 */
	private static Integer getNumRoadblocks(TMNode node,
											Collection<String> logicRoadblocks,
											Collection<String> inputRoadblocks) {
		int rtn = 0;
		Integer numInputRoadblocks = 0;
		Integer numLogicRoadblocks = 0;
		for (int i = 0; i < node.getNumInEdge(); i++) {
			TMEdge e = node.getInEdgeAtIdx(i);
			TMNode src = e.getSrc();

			if (inputRoadblocks.contains(src.getGate().getName())) {
				numInputRoadblocks++;
			}
			if (logicRoadblocks.contains(src.getGate().getPromoter())) {
					numLogicRoadblocks++;
			}
		}
		int total = numInputRoadblocks + numLogicRoadblocks;

		if(numLogicRoadblocks > 0 && total > 1) {
			rtn++;
		}

		return rtn;
	}

	/**
	 * Do a random assignment of gates onto the tech node map. Don't update the netlist.
	 * 
	 * @param techMap the techMap on which to make the assignment.
	 * @param netlist the netlist corresponding to the TechMap.
	 * @param gateLibrary the gate library from which to make assignments.
	 */
	public static void doRandomAssignment(TMNetlist netlist, List<Gate> gateLibrary) {
		// Set<String> groups = TMUtils.getGateGroups(gateLibrary);

		// Map<String,List<Gate>> gatesByType = TMUtils.getGatesByType(gateLibrary);
		// Map<String,Iterator<Gate>> it = new HashMap<>();
		// for (String type : gatesByType.keySet()) {
			// it.put(type,gatesByType.get(type).iterator());
		// }
		Collections.shuffle(gateLibrary);
		Iterator<Gate> it = gateLibrary.iterator();

		int num = netlist.getNumVertex();
		for (int j = 0; j < num; j++) {
			TMNode node = netlist.getVertexAtIdx(j);
			String type = node.getNodeType();
			if (!type.equals("TopInput") && !type.equals("TopOutput")) {
				// if (!gatesByType.keySet().contains(type)) {
				// 	throw new RuntimeException("No gates of type " + type
				// 							   + " (node '" + node.getName() + "') exist in the library.");
				// }
				while (node.getGate() == null
					   ||
					   node.getGate() == new Gate()) {
					if (!it.hasNext()) {
						throw new RuntimeException("Not enough gates in the library to cover the netlist.");
					}
					Gate g = it.next();
					if (!netlist.hasGatesOfGroup(g.getGroup())) {
						node.setGate(g);
					}
				}
			}
		}
	}

	/**
	 * Assign input sensors from a library to a netlist.
	 * 
	 * @param netlist the TMNetlist on which to assign input sensors.
	 * @param inputLibrary the input library from which to make assignments.
	 */
	public static void assignInputSensors(TMNetlist netlist, List<Gate> inputLibrary) {
		List<TMNode> nodes = TMUtils.getInputNodes(netlist);
		Iterator<Gate> it = inputLibrary.iterator();
		for (TMNode node : nodes) {
			if (!it.hasNext()) {
				throw new RuntimeException("Not input sensors in the library to cover the netlist inputs.");
			}
			Gate g = it.next();
			node.setGate(g);
		}
	}

	/**
	 * Assign output reporters from a library to a netlist.
	 * 
	 * @param netlist the TMNetlist on which to assign output reporters.
	 * @param outputLibrary the output library from which to make assignments.
	 */
	public static void assignOutputReporters(TMNetlist netlist, List<Gate> outputLibrary) {
		List<TMNode> nodes = TMUtils.getOutputNodes(netlist);
		Iterator<Gate> it = outputLibrary.iterator();
		for (TMNode node : nodes) {
			if (!it.hasNext()) {
				throw new RuntimeException("Not output reporters in the library to cover the netlist outputs.");
			}
			Gate g = it.next();
			node.setGate(g);
		}
	}
	
	/**
	 * Update a netlist to reflect a gate assignment in a TechNode map.
	 * 
	 * @param netlist the netlist to update.
	 * @param techMap the TechMap from which to copy the assignment.
	 */
	public static void updateNetlist(Netlist netlist, final TMNetlist tmNetlist) {
		for (int i = 0; i < netlist.getNumVertex(); i++) {
			NetlistNode node = netlist.getVertexAtIdx(i);
			TMNode n = tmNetlist.getVertexByName(node.getName());
			if (n != null) {
				node.setGate(n.getGate().getName());
				CObjectCollection<CObject> parts = new CObjectCollection<>();
				for (Part p : n.getGate().getParts()) {
					parts.add((CObject) p);
				}
				node.setParts(parts);
			}
		}
	}

	/**
	 * Get a candidate gate for assignment.
	 * 
	 * @param gate the Gate to be swapped or subsituted.
	 * @param netlist the TMNetlist to use when checking for gate validity.
	 * @param gateLibrary the gate library from which to pull candidates.
	 */
	public static Gate getAssignableGate(Gate gate, TMNetlist netlist, Collection<Gate> gateLibrary) {
		List<Gate> options = new ArrayList<>();
		for(Gate g : gateLibrary) {
			if(g.getName().equals(gate.getName())) {
				continue;
			}
			if(g.getGroup().equals(gate.getGroup())) {
				options.add(g);
			}
			if (!netlist.hasGatesOfGroup(g.getGroup())
				||
				netlist.hasGate(g)) {
				options.add(g);
			}
		}
		return options.get(new Random().nextInt(options.size()));
	}

}
