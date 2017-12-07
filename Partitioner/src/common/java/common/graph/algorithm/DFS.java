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
package common.graph.algorithm;

import java.util.Stack;

import common.CObject;
import common.graph.AbstractEdge;
import common.graph.AbstractGraph;
import common.graph.AbstractVertex;
import common.graph.AbstractVertex.VertexDiscovery;
import common.graph.AbstractVertex.VertexType;

/**
 * @author: Vincent Mirian
 * 
 * @date: Nov 1, 2017
 *
 */
public class DFS<V extends AbstractVertex<E>, E extends AbstractEdge<V>, G extends AbstractGraph<V,E>> extends CObject{

	private void init() {
		DFS = new Stack<V>();
	}
	
	private void reset() {
		this.getDFS().clear();
	}
	
	private void doDFS() {
		G g = this.getGraph();
		Stack<V> stack = new Stack<V>();
		// initialize VertexDiscovery		
		// get Source Vertex
		for (int i = 0; i < g.getNumVertex(); i++) {
			V v = g.getVertexAtIdx(i);
			if (v.getVertexType() == VertexType.SOURCE) {
				this.getDFS().push(v);
				stack.push(v);
			}
			v.setVertexDiscovery(VertexDiscovery.UNVISITED);
		}
		// doDFS
		while(!stack.isEmpty()) {
			V v = stack.pop();
			// skip if SINK
			if (v.getVertexType() == VertexType.SINK) {
				v.setVertexDiscovery(VertexDiscovery.VISITED);
			}
			// skip if VISITED
			if (v.getVertexDiscovery() == VertexDiscovery.VISITED) {
				continue;
			}
			for (int i = 0; i < v.getNumOutEdge(); i++) {
				E e = (E) v.getOutEdgeAtIdx(i);
				for (int j = 0; j < e.getNumDst(); j++) {
					V dst = (V) e.getDstAtIdx(j);
					if (dst.getVertexDiscovery() == VertexDiscovery.UNVISITED) {
						this.getDFS().push(dst);
						stack.push(dst);
					}
				}
			}
			v.setVertexDiscovery(VertexDiscovery.VISITED);
		}
	}
	
	private Stack<V> getDFS() {
		return this.DFS;
	}
	
	public DFS() {
		init();
	}
	
	public DFS(final G g) {
		init();
		this.setGraph(g);
	}
	
	public void setGraph(final G g) {
		reset();
		this.graph = g;
		this.doDFS();
	}

	public G getGraph() {
		return this.graph;
	}
	
	public V getNextVertex() {
		V rtn = null;
		if (!this.getDFS().isEmpty())
			rtn = this.getDFS().pop();
		return rtn;
	}

	/*
	 * HashCode
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((DFS == null) ? 0 : DFS.hashCode());
		result = prime * result + ((graph == null) ? 0 : graph.hashCode());
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
		if (!(obj instanceof DFS<?,?,?>)) {
			return false;
		}
		DFS<?,?,?> other = (DFS<?,?,?>) obj;
		if (DFS == null) {
			if (other.DFS != null)
				return false;
		} else if (!DFS.equals(other.DFS))
			return false;
		if (graph == null) {
			if (other.graph != null)
				return false;
		} else if (!graph.equals(other.graph))
			return false;
		return true;
	}

	private G graph;
	private Stack<V> DFS;
}
