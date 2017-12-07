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
package technologyMapping.algorithm.SimulatedAnnealing;

import common.CObject;
import technologyMapping.data.Gate;
import technologyMapping.netlist.TMNode;

/**
 * @author: Vincent Mirian
 * 
 * @date: Dec 6, 2017
 *
 */
public class GateAssignment extends CObject{

	public GateAssignment(){
		this.setTMNode(null);
		this.setGate(null);
		this.setNewGate(null);
	}
	
	public GateAssignment(final TMNode node, final Gate gate, final Gate newGate){
		this.setTMNode(node);
		this.setGate(gate);
		this.setNewGate(newGate);
	}
	
	/*
	 * null gate means not assigned
	 * null newGate means remove/no assignment
	 */
	
	public void setTMNode(final TMNode node){
		this.node = node;
	}
		
	public TMNode getTMNode(){
		return this.node;
	}
	
	public void setGate(final Gate gate){
		this.gate = gate;
	}
		
	public Gate getGate(){
		return this.gate;
	}
	
	public void setNewGate(final Gate newGate){
		this.newGate = newGate;
	}
		
	public Gate getNewGate(){
		return this.newGate;
	}

	/*
	 * Undo
	 */
	
	public void makeUndo(){
		Gate gate = this.getGate();
		Gate newGate = this.getNewGate();
		this.setGate(newGate);
		this.setNewGate(gate);
	}
	
	public GateAssignment createUndo(final GateAssignment ga){
		GateAssignment rtn = new GateAssignment(ga.getTMNode(), ga.getNewGate(), ga.getGate());
		return rtn;
	}
	
	private TMNode node;
	private Gate gate;
	private Gate newGate;
}
