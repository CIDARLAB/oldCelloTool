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
package org.cellocad.partition.algorithm.KL;

import org.cellocad.common.graph.graph.Edge;
import org.cellocad.common.graph.graph.Graph;
import org.cellocad.common.graph.graph.Vertex;
import org.cellocad.partition.algorithm.PTAlgorithm;

/**
 * @author: Vincent Mirian
 * 
 * @date: Oct 26, 2017
 *
 */
public class KL extends PTAlgorithm{

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Vertex u = new Vertex();
		Vertex v = new Vertex();
		Edge E = new Edge(u,v);
		u.addOutEdge(E);
		v.addInEdge(E);
		Graph G = new Graph();
		G.addEdge(E);
		G.addVertex(u);
		G.addVertex(v);
	}

	@Override
	public void setDefaultParameterValues() {
		
	}

	@Override
	public void setParameterValues() {
		
	}

	@Override
	public void validateParameterValues() {
		
	}

	@Override
	public void preprocessing() {
		
	}

	@Override
	public void run() {
		
	}

	@Override
	public void postprocessing() {
		
	}

}
