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
import org.cellocad.technologymapping.common.techmap.TechMap;
import org.cellocad.technologymapping.common.techmap.TechNode;
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

	/**
	 * Get all the input nodes in the netlist.
	 * 
	 * @param netlist the netlist from which to collect input nodes.
	 * @return the input nodes in the netlist.
	 */
	public static List<NetlistNode> getInputNodes(Netlist netlist) {
		List<NetlistNode> rtn = new CObjectCollection<>();
		int num = netlist.getNumVertex();
		for (int i = 0; i < num; i++) {
			NetlistNode node = netlist.getVertexAtIdx(i);
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
	public static List<NetlistNode> getOutputNodes(Netlist netlist) {
		List<NetlistNode> rtn = new CObjectCollection<>();
		int num = netlist.getNumVertex();
		for (int i = 0; i < num; i++) {
			NetlistNode node = netlist.getVertexAtIdx(i);
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
	public static List<NetlistNode> getLogicNodes(Netlist netlist) {
		List<NetlistNode> rtn = new CObjectCollection<>();
		int num = netlist.getNumVertex();
		for (int i = 0; i < num; i++) {
			NetlistNode node = netlist.getVertexAtIdx(i);
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
	public static void initInputActivities(TechMap techMap,
										   Netlist netlist,
										   Map<String,Pair<Double,Double>> inputActivityReference) {
		List<NetlistNode> nodes = TMUtils.getInputNodes(netlist);
		for (NetlistNode node : nodes) {
			Pair<Double,Double> inputRef = inputActivityReference.get(node.getGate());
			Utils.isNullRuntimeException(inputRef, "Input activity reference for " + node.getGate());
			TechNode tn = techMap.findTechNodeByName(node.getName());
			tn.setActivity(getInputActivity(tn.getLogic(),inputRef));
		}
	}

	/**
	 * Initialize toxicity at the output nodes.
	 * 
	 * @param techMap the TechMap on which to assign output toxicity.
	 * @param netlist the Netlist corresponding to the TechMap.
	 */
	public static void initOutputToxicity(TechMap techMap, Netlist netlist) {
		List<NetlistNode> nodes = TMUtils.getOutputNodes(netlist);
		for (NetlistNode node : nodes) {
			TechNode tn = techMap.findTechNodeByName(node.getName());
			if (tn != null) {
				List<Boolean> logic = tn.getLogic();
				if (logic != null) {
					List<Double> toxicity = Collections.nCopies(logic.size(),1.0);
					tn.setToxicity(toxicity);
				}
			}
		}
	}

	/**
	 * Find the minimum growth (highest toxicity) for a TechNode
	 * 
	 * @param techNode the TechNode to search.
	 */
	public static Double minGrowth(TechNode techNode) {
		List<Double> toxicity = techNode.getToxicity();
		Double rtn = null;
		if (toxicity != null)
			rtn = Collections.min(techNode.getToxicity());
		return rtn;
	}

	/**
	 * Find the minimum growth (highest toxicity) for a TechNode
	 * 
	 * @param techNode the TechNode to search.
	 */
	public static Double minGrowth(TechMap techMap, Netlist netlist) {
		Double rtn = null;

		List<NetlistNode> nodes = TMUtils.getOutputNodes(netlist);
		for (NetlistNode node : nodes) {
			TechNode tn = techMap.findTechNodeByName(node.getName());
			if (tn != null)
				rtn = minGrowth(tn);
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
	public static void doRandomAssignment(TechMap techMap, final Netlist netlist, List<Gate> gateLibrary) {
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
			NetlistNode node = netlist.getVertexAtIdx(j);
			String type = node.getNodeType();
			if (!type.equals("TopInput") && !type.equals("TopOutput")) {
				// if (!gatesByType.keySet().contains(type)) {
				// 	throw new RuntimeException("No gates of type " + type
				// 							   + " (node '" + node.getName() + "') exist in the library.");
				// }
				while (techMap.findTechNodeByName(node.getName()).getGate() == null
					   ||
					   techMap.findTechNodeByName(node.getName()).getGate() == new Gate()) {
					if (!it.hasNext()) {
						throw new RuntimeException("Not enough gates in the library to cover the netlist.");
					}
					Gate g = it.next();
					if (!techMap.hasGatesOfGroup(g.getGroup())) {
						techMap.findTechNodeByName(node.getName()).setGate(g);
					}
				}
			}
		}
	}
	
	/**
	 * Assign input sensors from a library to a netlist.
	 * 
	 * @param techMap the TechMap on which to assign input sensors.
	 * @param netlist the netlist on which to assign input sensors.
	 * @param inputLibrary the input library from which to make assignments.
	 */
	public static void assignInputSensors(TechMap techMap, Netlist netlist, List<Gate> inputLibrary) {
		List<NetlistNode> nodes = TMUtils.getInputNodes(netlist);
		Iterator<Gate> it = inputLibrary.iterator();
		for (NetlistNode node : nodes) {
			if (!it.hasNext()) {
				throw new RuntimeException("Not input sensors in the library to cover the netlist inputs.");
			}
			Gate g = it.next();
			TechNode tn = techMap.findTechNodeByName(node.getName());
			Utils.isNullRuntimeException(tn,"TechNode for gate NetlistNode '" + node.getName());
			tn.setGate(g);
			setNodeGate(node,tn);
		}
	}

	/**
	 * Assign output reporters from a library to a netlist.
	 * 
	 * @param techMap the TechMap on which to assign output reporters.
	 * @param netlist the Netlist on which to assign output reporters.
	 * @param outputLibrary the output library from which to make assignments.
	 */
	public static void assignOutputReporters(TechMap techMap, Netlist netlist, List<Gate> outputLibrary) {
		List<NetlistNode> nodes = TMUtils.getOutputNodes(netlist);
		Iterator<Gate> it = outputLibrary.iterator();
		for (NetlistNode node : nodes) {
			if (!it.hasNext()) {
				throw new RuntimeException("Not output reporters in the library to cover the netlist outputs.");
			}
			Gate g = it.next();
			TechNode tn = techMap.findTechNodeByName(node.getName());
			Utils.isNullRuntimeException(tn,"TechNode for gate NetlistNode '" + node.getName());
			tn.setGate(g);
			setNodeGate(node,tn);
		}
	}

	/**
	 * Assign gate and parts to a NetlistNode and corresponding TechNode.
	 * 
	 * @param node the NetlistNode to which to assign the gate.
	 * @param the techNode to which to assign the gate. 
	 * @param gate the gate to assign.
	 */
	public static void setNodeGate(NetlistNode node, TechNode techNode) {
		node.setGate(techNode.getGate().getName());
		CObjectCollection<CObject> parts = new CObjectCollection<>();
		for (Part p : techNode.getGate().getParts()) {
			parts.add((CObject) p);
		}
		node.setParts(parts);
	}
	
	/**
	 * Update a netlist to reflect a gate assignment in a TechNode map.
	 * 
	 * @param netlist the netlist to update.
	 * @param techMap the TechMap from which to copy the assignment.
	 */
	public static void updateNetlist(Netlist netlist, final TechMap techMap) {
		int num = netlist.getNumVertex();
		for (int i = 0; i < num; i++) {
			NetlistNode node = netlist.getVertexAtIdx(i);
			String name = node.getName();
			TechNode tn = techMap.findTechNodeByName(name);
			if (tn != null) {
				setNodeGate(node,tn);
			}
		}
	}

	/**
	 * Get a candidate gate for assignment.
	 * 
	 * @param techMap the TechMap to use when checking for gate validity.
	 * @param gateLibrary the gate library from which to pull candidates.
	 * @param gate the gate to be swapped or subsituted
	 */
	public static Gate getSwapOrSubGate(TechMap techMap, Collection<Gate> gateLibrary, Gate gate) {
		List<Gate> options = new ArrayList<>();
		for(Gate g : gateLibrary) {
			if(g.getName().equals(gate.getName())) {
                continue;
            }
			if(g.getGroup().equals(gate.getGroup())) {
                options.add(g);
            }
			if (!techMap.hasGatesOfGroup(g.getGroup())
				||
				techMap.hasGate(g)) {
                options.add(g);
            }
		}
		return options.get(new Random().nextInt(options.size()));
	}
		
}
