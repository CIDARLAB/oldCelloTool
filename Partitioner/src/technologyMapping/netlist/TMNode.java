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
package technologyMapping.netlist;

import common.graph.graph.VertexTemplate;
import technologyMapping.data.Gate;

/**
 * @author: Vincent Mirian
 * 
 * @date: Dec 6, 2017
 *
 */
public class TMNode extends VertexTemplate<TMEdge>{

	private void setDefault() {
	}
	
	public TMNode(){
		super();
		this.setDefault();
	}

	public TMNode(final TMNode other){
		super(other);
		this.setDefault();
	}
	/*
	 * Inherit
	 */
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

	/*
	 * Gate
	 */
	public void setGate (final Gate gate) {
		this.gate = gate;
	}
	public Gate getGate() {
		return this.gate;
	}
	
	private Gate gate;
	
}
