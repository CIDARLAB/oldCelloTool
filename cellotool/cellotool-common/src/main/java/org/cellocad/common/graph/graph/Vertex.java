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
package org.cellocad.common.graph.graph;

/**
 * @author: Vincent Mirian
 *
 * @date: Oct 26, 2017
 *
 */

public class Vertex extends VertexTemplate<Edge>{

	public Vertex(){
		super();
	}

	public Vertex(final Vertex other){
		super(other);
	}

	@Override
	protected void addMeToSrc(final Edge e) {
		e.setSrc(this);
	}

	@Override
	protected void addMeToDst(final Edge e){
		e.setDst(this);
	}

	@Override
	public Edge createT(final Edge e) {
		Edge rtn = null;
		rtn = new Edge(e);
		return rtn;
	}

}
