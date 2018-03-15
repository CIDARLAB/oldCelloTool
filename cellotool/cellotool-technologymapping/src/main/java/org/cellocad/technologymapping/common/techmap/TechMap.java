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
package org.cellocad.technologymapping.common.techmap;

import java.util.ArrayList;
import java.util.List;

import org.cellocad.common.CObject;
import org.cellocad.common.CObjectCollection;
import org.cellocad.common.netlist.Netlist;
import org.cellocad.common.netlist.NetlistNode;
import org.cellocad.technologymapping.common.score.ScoreUtils;
import org.cellocad.technologymapping.common.techmap.TechNode.TechNodeType;
import org.cellocad.technologymapping.data.Gate;

/**
 * A technology mapping to a netlist.
 *
 * @author: Timothy Jones
 * 
 * @date: Mar 14, 2018
 *
 */
public class TechMap extends CObject{

	public TechMap() {
		super();
	}

	/**
	 * Create an empty TechMap from the given netlist.
	 * 
	 * @param netlist the netlist from which to build the TechMap.
	 */
	public TechMap(Netlist netlist) {
		this();
		this.setNodes(this.buildEmptyNodes(netlist));
	}

	/**
	 * Create a TechMap from the given netlist, using a gate library to attach Gates to TechNodes.
	 * 
	 * @param netlist the netlist from which to build the TechMap.
	 * @param gateLibrary the Gate library.
	 */
	public TechMap(Netlist netlist, CObjectCollection<Gate> gateLibrary) {
		this(netlist);
		this.setGatesFromLibrary(netlist,this.getNodes(),gateLibrary);
	}

	/**
	 * Create a new TechMap from the given TechMap.
	 * 
	 * @param techMap the TechMap to copy.
	 */
	public TechMap(final TechMap other) {
		this();
		this.setNodes(this.cloneTechNodes(other));
	}

	/**
	 * Get the number of TechNodes.
	 * 
	 * @return the number of TechNodes in the TechMap.
	 */
	public int getNumTechNode() {
		return this.getNodes().size();
	}

	/**
	 * Get a TechNode by index.
	 * 
	 * @param idx the index.
	 * @return the TechNode found, null otherwise.
	 */
	public TechNode getTechNodeAtIdx(int idx) {
		return this.getNodes().findCObjectByIdx(idx);
	}

	/**
	 * Find a TechNode by name.
	 * 
	 * @param name the name to search for.
	 * @return the TechNode found, null otherwise.
	 */
	public TechNode findTechNodeByName(String name) {
		return this.getNodes().findCObjectByName(name);
	}

	/**
	 * Evaluate the score for a given assignment.
	 * 
	 * @return the score.
	 */
	public Double getScore() {
		Double worst = Double.MAX_VALUE;
		List<TechNode> nodes = this.getOutputNodes();
		List<Double> scores = new ArrayList<>();
		for(TechNode tn : nodes) {
			Double score = ScoreUtils.getOnOffRatio(tn);
			scores.add(score);
			if(score < worst) {
                worst = score;
            }
        }
		return worst;	
	}

	/**
	 * Check whether a given gate exists in the TechMap.
	 * 
	 * @param gate the gate the check for.
	 * @return whether the gate exists.
	 */
	public Boolean hasGate(Gate gate) {
		for (TechNode tn : this.getNodes()) {
			if (tn.getGate().equals(gate)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if any gates of a particular group have been assigned.
	 * 
	 * @param techNodes the collection of TechNodes to check for group assignments.
	 * @param group the group for which to check.
	 * @return true if there are gates in the netlist of the specified group.
	 */
	public Boolean hasGatesOfGroup(String group) {
		for (TechNode tn : this.getNodes()) {
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
	 * Get the output nodes.
	 * 
	 * @return the output nodes.
	 */
	private CObjectCollection<TechNode> getOutputNodes() {
		CObjectCollection<TechNode> nodes = new CObjectCollection<>();
		for (TechNode tn : this.getNodes()) {
			if (tn.getTechNodeType() == TechNodeType.SINK) {
				nodes.add(tn);
			}
		}
		return nodes;
	}

	/**
	 * Build an empty set of TechNodes for a given netlist.
	 * 
	 * @param netlist the netlist from which to build the map.
	 * @return the TechNode map.
	 */
	private CObjectCollection<TechNode> buildEmptyNodes(Netlist netlist) {
		int num = netlist.getNumVertex();
		CObjectCollection<TechNode> nodes = new CObjectCollection<>();
		for (int i = 0; i < num; i++) {
			NetlistNode node = netlist.getVertexAtIdx(i);
			TechNode tn = new TechNode(node.getName(),node.getType(),node.getIdx());
			if (node.getNodeType().equals("TopInput")) {
				tn.setType(TechNodeType.SOURCE);
			} else if (node.getNodeType().equals("TopOutput")) {
				tn.setType(TechNodeType.SINK);
			} else {
				tn.setType(TechNodeType.NONE);
			}
			nodes.add(tn);
		}
		return nodes;
	}

	/**
	 * Try to update the TechNode gate assignments based on the netlist and the given gate library.
	 * 
	 * @param netlist the netlist from which to get gate assignments.
	 * @param nodes the TechNodes to update.
	 * @param gateLibrary the gate library.
	 */
	private void setGatesFromLibrary(Netlist netlist, CObjectCollection<TechNode> nodes, CObjectCollection<Gate> gateLibrary) {
		int num = netlist.getNumVertex();
		for (int i = 0; i < num; i++) {
			NetlistNode node = netlist.getVertexAtIdx(i);
			TechNode tn = nodes.findCObjectByName(node.getName());
			String name = node.getGate();
			if (name != null && tn != null) {
				Gate gate = gateLibrary.findCObjectByName(node.getGate());
				tn.setGate(gate);
			}
		}
	}

	/**
	 * Build a collection of TechNodes from the given TechMap.
	 * 
	 * @param other the TechMap from which to build the node map.
	 * @return the TechNode collection.
	 */
	private CObjectCollection<TechNode> cloneTechNodes(final TechMap other) {
		CObjectCollection<TechNode> nodes = new CObjectCollection<>();
		int num = other.getNumTechNode();
		for (int i = 0; i < num; i++) {
			TechNode old = other.getTechNodeAtIdx(i);
			TechNode tn = null;
			if (old != null) {
				tn = new TechNode(old);
			} else {
				tn = new TechNode();
			}
			nodes.add(tn);
		}
		return nodes;
	}
	
	/**
	 * @return the nodes
	 */
	private CObjectCollection<TechNode> getNodes() {
		return nodes;
	}

	/**
	 * @param nodes the nodes to set
	 */
	private void setNodes(final CObjectCollection<TechNode> nodes) {
		this.nodes = nodes;
	}
	
	private CObjectCollection<TechNode> nodes;

	
}
