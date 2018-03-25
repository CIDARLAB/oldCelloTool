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
import java.util.List;

import org.cellocad.common.graph.graph.VertexTemplate;
import org.cellocad.common.netlist.NetlistNode;
import org.cellocad.technologymapping.data.Gate;

/**
 * @author: Timothy Jones
 *
 * @date: Mar 17, 2018
 *
 */
public class TMNode extends VertexTemplate<TMEdge>{

	private void setDefault() {
		this.setPartitionID(-1);
		this.setNodeType("");
		this.setGate(null);
		this.setToxicity(new ArrayList<>());
		this.setActivity(new ArrayList<>());
	}

	public TMNode(){
		super();
		this.setDefault();
	}

	public TMNode(final TMNode other){
		super(other);
		this.setPartitionID(other.getPartitionID());
		this.setNodeType(new String(other.getNodeType()));
		if (other.getGate() != null) {
			this.setGate(other.getGate());
		}
		this.setToxicity(new ArrayList<>(other.getToxicity()));
		this.setActivity(new ArrayList<>(other.getActivity()));
		this.setVertexType(other.getVertexType());
	}

	public TMNode(final NetlistNode node){
		super();
		this.setDefault();
		this.setName(node.getName());
		this.setType(node.getType());
		this.setIdx(node.getIdx());
		this.setPartitionID(node.getPartitionID());
		this.setNodeType(node.getNodeType());
		this.setVertexType(node.getVertexType());
	}

	@Override
	protected void addMeToSrc(final TMEdge e) {
		e.setSrc(this);
	}

	@Override
	protected void addMeToDst(final TMEdge e) {
		e.setDst(this);
	}

	@Override
	public TMEdge createT(final TMEdge e) {
		TMEdge rtn = null;
		rtn = new TMEdge(e);
		return rtn;
	}

	/**
	 * @return the partitionID
	 */
	public Integer getPartitionID() {
		return partitionID;
	}

	/**
	 * @param partitionID the partitionID to set
	 */
	public void setPartitionID(Integer partitionID) {
		this.partitionID = partitionID;
	}

	/**
	 * @return the nodeType
	 */
	public String getNodeType() {
		return nodeType;
	}

	/**
	 * @param nodeType the nodeType to set
	 */
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	/**
	 * @return the gate
	 */
	public Gate getGate() {
		return gate;
	}

	/**
	 * @param gate the gate to set
	 */
	public void setGate(final Gate gate) {
		this.gate = gate;
	}

	/**
	 * @return the toxicity
	 */
	public List<Double> getToxicity() {
		return toxicity;
	}

	/**
	 * @param toxicity the toxicity to set
	 */
	public void setToxicity(final List<Double> toxicity) {
		this.toxicity = toxicity;
	}

	/**
	 * @return the activity
	 */
	public List<Double> getActivity() {
		return activity;
	}

	/**
	 * @param activity the activity to set
	 */
	public void setActivity(List<Double> activity) {
		this.activity = activity;
	}

	private Integer partitionID;
	private String nodeType;
	private Gate gate;
	private List<Double> toxicity;
	private List<Double> activity;

}
