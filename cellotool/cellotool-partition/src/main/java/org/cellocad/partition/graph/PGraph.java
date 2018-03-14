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
package org.cellocad.partition.graph;

import org.cellocad.common.graph.graph.GraphTemplate;

/**
 * @author: Vincent Mirian
 * 
 * @date: Oct 30, 2017
 *
 */
public class PGraph extends GraphTemplate<PNode, PEdge> {

	public PGraph() {
		super();
	}
	
	public PGraph(final PGraph other) {
		super();
	}
	
	@Override
	public PNode createV(final PNode other) {
		PNode rtn = null;
		rtn = new PNode(other);
		return rtn;
	}

	@Override
	public PEdge createE(final PEdge other) {
		PEdge rtn = null;
		rtn = new PEdge(other);
		return rtn;
	}
}
