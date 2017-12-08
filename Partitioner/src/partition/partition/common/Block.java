/**
 * Copyright (C) 2017 Massachusetts Institute of Technology (MIT)
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
package partition.common;

import java.io.IOException;
import java.io.Writer;

import partition.graph.PEdge;
import partition.graph.PGraph;
import partition.graph.PNode;
import partition.profile.BlockProfile;
import common.CObject;
import common.CObjectCollection;
import common.Utils;
import common.capacity.Capacity;
import common.capacity.CapacityCollection;
import common.capacity.Weight;

/**
 * @author: Vincent Mirian
 * 
 * @date: Oct 26, 2017
 *
 */
public class Block extends CapacityCollection<BlockProfile> {

	private void init(){
		this.nodes = new CObjectCollection<PNode>();
	}
	
	public Block(BlockProfile BP, CObjectCollection<Capacity> capacity, CObjectCollection<CObject> capacityUnits){
		super(BP, capacity);
		init();
		Utils.isNullRuntimeException(capacityUnits, "CapacityUnits");
		myWeight = new Weight(capacityUnits, capacityUnits);
	}
	
	/*
	 * myWeight
	 */	
	private Weight getMyWeight() {
		return this.myWeight;
	}
	
	/*
	 * Evaluate
	 */	
	public boolean canFit () {
		boolean rtn = false;
		rtn = this.canFit(this.getMyWeight());
		return rtn;
	}
	
	public boolean isOverflow () {
		boolean rtn = false;
		rtn = this.isOverflow(this.getMyWeight());
		return rtn;
	}
	
	public boolean isUnderflow () {
		boolean rtn = false;
		rtn = this.isUnderflow(this.getMyWeight());
		return rtn;
	}
	
	@Override
	public boolean canFit (final Weight wObj) {
		boolean rtn = false;
		if (wObj != null) {
			Weight wObjTemp = new Weight(wObj);
			wObjTemp.inc(this.getMyWeight());
			rtn = super.canFit(wObjTemp);
		}
		return rtn;
	}

	@Override
	public boolean isOverflow (final Weight wObj) {
		boolean rtn = false;
		if (wObj != null) {
			Weight wObjTemp = new Weight(wObj);
			wObjTemp.inc(this.getMyWeight());
			rtn = super.isOverflow(wObjTemp);
		}
		return rtn;
	}

	@Override
	public boolean isUnderflow (final Weight wObj) {
		boolean rtn = false;
		if (wObj != null) {
			Weight wObjTemp = new Weight(wObj);
			wObjTemp.inc(this.getMyWeight());
			rtn = super.isUnderflow(wObjTemp);
		}
		return rtn;
	}
	
	/*
	 * PNode
	 */
	public void addPNode(final PNode node){
		if (node != null){
			nodes.add(node);
			myWeight.inc(node.getMyWeight());
		}
	}
	
	public void removePNode(final PNode node){
		if ((node != null) && this.PNodeExists(node)) {
			nodes.remove(node);
			myWeight.dec(node.getMyWeight());
		}
	}
	
	public PNode getPNodeAtIdx(int index){
		PNode rtn = null;
		if (
				(index >= 0) &&
				(index < this.getNumPNode())
			){
			rtn = nodes.get(index);	
		} 
		return rtn;
	}
	
	public int getNumPNode(){
		int rtn = nodes.size();
		return rtn;
	}
	
	private boolean PNodeExists(final PNode node){
		boolean rtn = (node != null) && (nodes.contains(node));
		return rtn;
	}

	/*
	 * dot file
	 */
	public PGraph convertToPGraph(){
		PGraph rtn = new PGraph();
		for (int i = 0; i < this.getNumPNode(); i++){
			PNode node = this.getPNodeAtIdx(i);
			rtn.addVertex(node);
			// outedges
			for (int j = 0; j < node.getNumOutEdge(); j ++){
				PEdge edge = node.getOutEdgeAtIdx(j);
				PNode other = edge.getDst();
				if (this.PNodeExists(other)){
					rtn.addEdge(edge);
				}
			}
			// inedges
			for (int j = 0; j < node.getNumInEdge(); j ++){
				PEdge edge = node.getInEdgeAtIdx(j);
				PNode other = edge.getSrc();
				if (this.PNodeExists(other)){
					rtn.addEdge(edge);
				}
			}
		}
		return rtn;
	}
	
	public void printDot(final Writer os) throws IOException{
		PGraph g = this.convertToPGraph();
		g.printDot(os);
	}
	
	/*
	 * HashCode
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((nodes == null) ? 0 : nodes.hashCode());
		result = prime * result + ((myWeight == null) ? 0 : myWeight.hashCode());
		return result;
	}

	/*
	 * Equals
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Block other = (Block) obj;
		if (nodes == null) {
			if (other.nodes != null)
				return false;
		} else if (!nodes.equals(other.nodes))
			return false;
		if (myWeight == null) {
			if (other.myWeight != null)
				return false;
		} else if (!myWeight.equals(other.myWeight))
			return false;
		return true;
	}

	/*
	 * toString
	 */
	protected String getNodesToString() {
		String rtn = "";
		for (int i = 0; i < this.getNumPNode(); i ++) {
			rtn = rtn + Utils.getTabCharacterRepeat(2);
			PNode node = this.getPNodeAtIdx(i);
			rtn = rtn + node.getName();
			rtn = rtn + ",";
			rtn = rtn + Utils.getNewLine();
		}
		return rtn;
	}
	
	@Override
	public String toString() {
		String rtn = "";
		String indentStr = "";
		rtn = rtn + "[ ";
		rtn = rtn + Utils.getNewLine();
		// name
		rtn = rtn + this.getEntryToString("name", this.getName());
		// nodes
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "nodes = ";
		rtn = rtn + Utils.getNewLine();
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "{";
		rtn = rtn + Utils.getNewLine();
		rtn = rtn + this.getNodesToString();
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "}";
		rtn = rtn + Utils.getNewLine();	
		// Weight
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "myWeight = ";
		rtn = rtn + Utils.getNewLine();
		indentStr = this.getMyWeight().toString();
		indentStr = Utils.addIndent(1, indentStr);
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + Utils.getNewLine();		
		// toString
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "toString() = ";
		rtn = rtn + Utils.getNewLine();
		indentStr = super.toString();
		indentStr = Utils.addIndent(1, indentStr);
		rtn = rtn + indentStr;
		rtn = rtn + ",";
		rtn = rtn + Utils.getNewLine();
		// end
		rtn = rtn + "]";
		return rtn;
	}
	
	private CObjectCollection<PNode> nodes;
	private Weight myWeight;
}
