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
package org.cellocad.technologymapping.common.netlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cellocad.common.CObjectCollection;
import org.cellocad.common.graph.graph.GraphTemplate;
import org.cellocad.common.netlist.Netlist;
import org.cellocad.common.netlist.NetlistEdge;
import org.cellocad.common.netlist.NetlistNode;
import org.cellocad.technologymapping.data.Gate;

/**
 * @author: Timothy Jones
 * @author: Vincent Mirian
 *
 * @date: Mar 17, 2018
 *
 */
public class TMNetlist extends GraphTemplate<TMNode,TMEdge>{

	/**
	 * Create an empty TMNetlist.
	 */
	public TMNetlist() {
		super();
	}

	/**
	 * Create a TMNetlist from another.
	 */
	public TMNetlist(final TMNetlist other) {
		super(other);
	}

	/**
	 * Create a TMNetlist from a Netlist.
	 */
	public TMNetlist(final Netlist netlist) {
		// CObject members
		this.setName(netlist.getName());
		this.setType(netlist.getType());
		this.setIdx(netlist.getIdx());

		Map<NetlistNode,TMNode> mapVertexOtherThis = new HashMap<>();
		Map<NetlistEdge,TMEdge> mapEdgeOtherThis = new HashMap<>();
		// copy Vertex
		for (int i = 0; i < netlist.getNumVertex(); i++) {
			NetlistNode v = netlist.getVertexAtIdx(i);
			TMNode vertex = createV(v);
			mapVertexOtherThis.put(v, vertex);
			this.addVertex(vertex);
		}
		for (int i = 0; i < netlist.getNumEdge(); i++) {
			NetlistEdge e = netlist.getEdgeAtIdx(i);
			TMEdge edge = createE(e);
			mapEdgeOtherThis.put(e, edge);
			this.addEdge(edge);
		}
		// for each Vertex:
		for (int i = 0; i < netlist.getNumVertex(); i++){
			NetlistNode v = netlist.getVertexAtIdx(i);
			TMNode vertex = mapVertexOtherThis.get(v);
			assert (vertex != null);
			// set outEdge for Vertex
			for (int j = 0; j < v.getNumOutEdge(); j ++){
				NetlistEdge e = v.getOutEdgeAtIdx(j);
				TMEdge edge = mapEdgeOtherThis.get(e);
				assert (edge != null);
				//vertex.addOutEdge(edge);
				addEdgeToOutEdge(vertex, edge);
			}
			// set inEdge for Vertex
			for (int j = 0; j < v.getNumInEdge(); j ++){
				NetlistEdge e = v.getInEdgeAtIdx(j);
				TMEdge edge = mapEdgeOtherThis.get(e);
				assert (edge != null);
				//vertex.addInEdge(edge);
				addEdgeToInEdge(vertex, edge);
			}
			if (vertex.getNodeType().equals("TopOutput")) {
				TMNode node = new TMNode();
				node.setNodeType("DummyOutput");
				TMEdge edge = new TMEdge(vertex,node);
				addEdgeToInEdge(node, edge);
				addEdgeToOutEdge(vertex, edge);
				this.addVertex(node);
				this.addEdge(edge);
			}
		}
		// for each Edge:
		for (int i = 0; i < netlist.getNumEdge(); i++){
			NetlistEdge e = netlist.getEdgeAtIdx(i);
			TMEdge edge = mapEdgeOtherThis.get(e);
			assert (edge != null);
			// set src for Edge
			{
				NetlistNode v = e.getSrc();
				TMNode vertex = mapVertexOtherThis.get(v);
				assert (vertex != null);
				//edge.setSrc(vertex);
				addVertexToSrc(vertex, edge);
			}
			// set dst for Edge
			{
				for (int j = 0; j < e.getNumDst(); j++) {
					NetlistNode v = e.getDstAtIdx(j);
					TMNode vertex = mapVertexOtherThis.get(v);
					assert (vertex != null);
					//edge.setDst(vertex);
					addVertexToDst(vertex, edge);
				}
			}
		}
	}

	/**
	 * Create a TMNetlist from a Netlist, try to populate the gates from a Gate library.
	 */
	public TMNetlist(final Netlist netlist, final CObjectCollection<Gate> gateLibrary) {
		this(netlist);
		for (int i = 0; i < this.getNumVertex(); i++) {
			NetlistNode v = netlist.getVertexAtIdx(i);
			TMNode vertex = this.getVertexAtIdx(i);

			String g = v.getGate();
			Gate gate = null;
			if (g != null) {
				gate = gateLibrary.findCObjectByName(g);
			}
			if (gate != null) {
				vertex.setGate(gate);
			}
		}
	}

	@Override
	public TMNode createV(final TMNode other) {
		TMNode rtn = new TMNode(other);
		return rtn;
	}

	public TMNode createV(final NetlistNode other) {
		TMNode rtn = new TMNode(other);
		return rtn;
	}

	@Override
	public TMEdge createE(final TMEdge other) {
		TMEdge rtn = new TMEdge(other);
		return rtn;
	}

	public TMEdge createE(final NetlistEdge other) {
		TMEdge rtn = new TMEdge(other);
		return rtn;
	}

	/**
	 * Check whether a given gate exists in the TMNetlist.
	 *
	 * @param gate the gate the check for.
	 * @return whether the gate exists.
	 */
	public Boolean hasGate(Gate gate) {
		for (int i = 0; i < this.getNumVertex(); i++) {
			TMNode v = this.getVertexAtIdx(i);
			if (v.getGate().equals(gate)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether a given gate exists in the TMNetlist.
	 *
	 * @param gate the gate the check for.
	 * @return whether the gate exists.
	 */
	public Boolean hasGate(String gate) {
		for (int i = 0; i < this.getNumVertex(); i++) {
			TMNode v = this.getVertexAtIdx(i);
			if (v.getGate().getName().equals(gate)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if any gates of a particular group have been assigned.
	 *
	 * @param techNodes the collection of TMNode objects to check for group assignments.
	 * @param group the group for which to check.
	 * @return true if there are gates in the netlist of the specified group.
	 */
	public Boolean hasGatesOfGroup(String group) {
		for (int i = 0; i < this.getNumVertex(); i++) {
			TMNode v = this.getVertexAtIdx(i);
			Gate g = v.getGate();
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
	 * Get all the input nodes in the netlist.
	 *
	 * @return the input nodes in the netlist.
	 */
	public List<TMNode> getInputNodes() {
		List<TMNode> rtn = new ArrayList<>();
		int num = this.getNumVertex();
		for (int i = 0; i < num; i++) {
			TMNode node = this.getVertexAtIdx(i);
			if (node.getNodeType().equals("TopInput")) {
				rtn.add(node);
			}
		}
		return rtn;
	}

	/**
	 * Get all the output nodes in a netlist.
	 *
	 * @return the output nodes in the netlist.
	 */
	public List<TMNode> getOutputNodes() {
		List<TMNode> rtn = new ArrayList<>();
		int num = this.getNumVertex();
		for (int i = 0; i < num; i++) {
			TMNode node = this.getVertexAtIdx(i);
			if (node.getNodeType().equals("TopOutput")) {
				rtn.add(node);
			}
		}
		return rtn;
	}

	/**
	 * Get all the logic nodes in the netlist.
	 *
	 * @return the logic nodes in the netlist.
	 */
	public List<TMNode> getLogicNodes() {
		List<TMNode> rtn = new ArrayList<>();
		int num = this.getNumVertex();
		for (int i = 0; i < num; i++) {
			TMNode node = this.getVertexAtIdx(i);
			if (!node.getNodeType().equals("TopOutput")
				&&
				!node.getNodeType().equals("DummyOutput")
				&&
				!node.getNodeType().equals("TopInput")) {
				rtn.add(node);
			}
		}
		return rtn;
	}

}
