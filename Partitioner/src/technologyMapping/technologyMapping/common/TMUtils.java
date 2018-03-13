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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import common.CObject;
import common.CObjectCollection;
import common.Pair;
import common.Utils;
import common.netlist.Netlist;
import common.netlist.NetlistNode;

import technologyMapping.data.Gate;
import technologyMapping.data.GateType;
import technologyMapping.data.Part;
import technologyMapping.data.TechNode;

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
	 * Get all the output nodes in the netlist.
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
	 * Check if any gates of a particular group have been assigned.
	 * 
	 * @param techNodes the collection of TechNodes to check for group assignments.
	 * @param group the group for which to check.
	 * @return true if there are gates in the netlist of the specified group.
	 */
	public static Boolean hasGatesOfGroup(Collection<TechNode> techNodes, String group) {
		for (TechNode tn : techNodes) {
			Gate g = tn.getGate();
			if (g == null)
				continue;
			String str = g.getGroup();
			if (str == null)
				continue;
			if (str.equals(group))
				return true;
		}
		return false;
	}

	/**
	 * Check if a particular gate exists in a collection of tech nodes.
	 * 
	 * @param techNodes the collection of tech nodes in which to check for the gate.
	 * @param gate the gate for which to check.
	 * @return true if the tech nodes contain the given gate.
	 */
	public static Boolean hasGate(Collection<TechNode> techNodes, Gate gate) {
		for (TechNode tn : techNodes) {
			if (tn.getGate().equals(gate))
				return true;
		}
		return false;
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
	 * @param netlist the netlist on which to assign input activities.
	 * @param techNodeMap the map from netlist node name to TechNode object.
	 * @param inputActivityReference the map from input name to reference low-high activity pair.
	 */
	public static void assignInputActivities(Netlist netlist,
											 Map<String,TechNode> techNodeMap,
											 Map<String,Pair<Double,Double>> inputActivityReference) {
		List<NetlistNode> nodes = TMUtils.getInputNodes(netlist);
		for (NetlistNode node : nodes) {
			Pair<Double,Double> inputRef = inputActivityReference.get(node.getGate());
			Utils.isNullRuntimeException(inputRef, "Input activity reference for " + node.getGate());
			TechNode tn = techNodeMap.get(node.getName());
			tn.setActivity(getInputActivity(tn.getLogic(),inputRef));
		}
	}

	/**
	 * Do a random assignment of gates onto the tech node map. Don't update the netlist.
	 * 
	 * @param netlist the netlist on which to make the assignment.
	 * @param techNodeMap the map from netlist node name to TechNode object.
	 * @param gateLibrary the gate library from which to make assignments.
	 */
	public static void doRandomAssignment(final Netlist netlist, Map<String,TechNode> techNodeMap, List<Gate> gateLibrary) {
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
				while (techNodeMap.get(node.getName()).getGate() == null
					   ||
					   techNodeMap.get(node.getName()).getGate() == new Gate()) {
					if (!it.hasNext()) {
						throw new RuntimeException("Not enough gates in the library to cover the netlist.");
					}
					Gate g = it.next();
					if (!TMUtils.hasGatesOfGroup(techNodeMap.values(),g.getGroup())) {
						techNodeMap.get(node.getName()).setGate(g);
					}
				}
			}
		}
	}
	
	/**
	 * Assign input sensors from a library to a netlist.
	 * 
	 * @param netlist the netlist on which to make the assignment.
	 * @param techNodeMap the map from netlist node name to TechNode object.
	 * @param inputLibrary the input library from which to make assignments.
	 */
	public static void assignInputSensors(Netlist netlist, Map<String,TechNode> techNodeMap, List<Gate> inputLibrary) {
		List<NetlistNode> nodes = TMUtils.getInputNodes(netlist);
		Iterator<Gate> it = inputLibrary.iterator();
		for (NetlistNode node : nodes) {
			if (!it.hasNext()) {
				throw new RuntimeException("Not input sensors in the library to cover the netlist inputs.");
			}
			Gate g = it.next();
			TechNode tn = techNodeMap.get(node.getName());
			tn.setGate(g);
			setNodeGate(node,tn);
		}
	}

	/**
	 * Assign output reporters from a library to a netlist.
	 * 
	 * @param netlist the netlist on which to make the assignment.
	 * @param techNodeMap the map from netlist node name to TechNode object.
	 * @param outputLibrary the output library from which to make assignments.
	 */
	public static void assignOutputReporters(Netlist netlist, Map<String,TechNode> techNodeMap, List<Gate> outputLibrary) {
		List<NetlistNode> nodes = TMUtils.getOutputNodes(netlist);
		Iterator<Gate> it = outputLibrary.iterator();
		for (NetlistNode node : nodes) {
			if (!it.hasNext()) {
				throw new RuntimeException("Not output reporters in the library to cover the netlist outputs.");
			}
			Gate g = it.next();
			TechNode tn = techNodeMap.get(node.getName());
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
	 * Build an empty TechNode map for a given netlist.
	 * 
	 * @param netlist the netlist from which to build the map.
	 * @return the TechNode map.
	 */
	public static Map<String,TechNode> buildTechNodeMap(Netlist netlist) {
		int num = netlist.getNumVertex();
		Map<String,TechNode> map = new HashMap<>();
		for (int i = 0; i < num; i++) {
			NetlistNode node = netlist.getVertexAtIdx(i);
			TechNode tn = new TechNode();
			tn.setIdx(node.getIdx());
			tn.setName(node.getName());
			map.put(node.getName(),tn);
		}
		return map;
	}

	/**
	 * Build a TechNode map for a given netlist using an existing map.
	 * 
	 * @param netlist the netlist from which to build the map.
	 * @return the TechNode map.
	 */
	public static Map<String,TechNode> buildTechNodeMap(Netlist netlist, Map<String,TechNode> techNodeMap) {
		Map<String,TechNode> map = buildTechNodeMap(netlist);
		int num = netlist.getNumVertex();
		for (int i = 0; i < num; i++) {
			NetlistNode node = netlist.getVertexAtIdx(i);
			TechNode tn = null;
			TechNode old = techNodeMap.get(node.getName());
			if (old != null) {
				tn = new TechNode(old);
			} else {
				tn = new TechNode();
				tn.setIdx(node.getIdx());
				tn.setName(node.getName());
			}
			map.put(node.getName(),tn);
		}
		return map;
	}

	/**
	 * Evaluate the score for a given assignment.
	 * 
	 * @param netlist the netlist from which to build the map.
	 * @return the TechNode map.
	 */
	public static Double getScore(final Netlist netlist, Map<String,TechNode> techNodeMap) {
		Double lowestOn = Double.MAX_VALUE;
        Double highestOff = Double.MIN_VALUE;
		Double worst = Double.MAX_VALUE;

		List<NetlistNode> nodes = getOutputNodes(netlist);
		List<Double> scores = new ArrayList<>();
		for(int j = 0; j < nodes.size(); j++) {// if multiple outputs, average _scores
			TechNode tn = techNodeMap.get(nodes.get(j).getName());

			for(int i = 0; i < tn.getLogic().size(); ++i) { // for each row in the truth table...
				Double a = tn.getActivity().get(i);

				if (tn.getLogic().get(i) == true
					&&
					lowestOn > a) {
					lowestOn = a;
				} else if (tn.getLogic().get(i) == false
						   &&
						   highestOff < a) {
					highestOff = a;
				}
			}
			Double score = lowestOn/highestOff;
			scores.add(score);
			if(score < worst) {
                worst = score;
            }
        }
		return worst;	
	}

	/**
	 * Update a netlist to reflect a gate assignment in a TechNode map.
	 * 
	 * @param netlist the netlist to update.
	 * @param techNodeMap the TechNode map from which to copy the assignment.
	 */
	public static void updateNetlist(Netlist netlist, Map<String,TechNode> techNodeMap) {
		int num = netlist.getNumVertex();
		for (int i = 0; i < num; i++) {
			NetlistNode node = netlist.getVertexAtIdx(i);
			String name = node.getName();
			TechNode tn = techNodeMap.get(name);
			if (techNodeMap.keySet().contains(name)) {
				setNodeGate(node,tn);
			}
		}
	}

	/**
	 * Get a candidate gate for assignment.
	 * 
	 * @param techNodeMap the TechNode map to use when checking for gate validity.
	 * @param gateLibrary the gate library from which to pull candidates.
	 * @param gate the gate to be swapped or subsituted
	 */
	public static Gate getSwapOrSubGate(Map<String,TechNode> techNodeMap, Collection<Gate> gateLibrary, Gate gate) {
		List<Gate> options = new ArrayList<>();
		for(Gate g : gateLibrary) {
			if(g.getName().equals(gate.getName())) {
                continue;
            }
			if(g.getGroup().equals(gate.getGroup())) {
                options.add(g);
            }
			if (!TMUtils.hasGatesOfGroup(techNodeMap.values(), g.getGroup())
				||
				TMUtils.hasGate(techNodeMap.values(), g)) {
                options.add(g);
            }
		}
		return options.get(new Random().nextInt(options.size()));
	}
		
}
