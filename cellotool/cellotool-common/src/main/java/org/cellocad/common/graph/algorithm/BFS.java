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
package org.cellocad.common.graph.algorithm;

import java.util.LinkedList;
import java.util.Queue;

import org.cellocad.common.CObject;
import org.cellocad.common.graph.AbstractEdge;
import org.cellocad.common.graph.AbstractGraph;
import org.cellocad.common.graph.AbstractVertex;
import org.cellocad.common.graph.AbstractVertex.VertexDiscovery;
import org.cellocad.common.graph.AbstractVertex.VertexType;

/**
 * @author: Vincent Mirian
 * 
 * @date: Nov 1, 2017
 *
 */
public class BFS<V extends AbstractVertex<E>, E extends AbstractEdge<V>, G extends AbstractGraph<V,E>> extends CObject{

	private void init() {
		BFS = new LinkedList<V>();
	}
	
	private void reset() {
		this.getBFS().clear();
	}
	
	private void doBFS() {
		G g = this.getGraph();
		Queue<V> q = new LinkedList<V>();
		// initialize VertexDiscovery		
		// get Source Vertex
		for (int i = 0; i < g.getNumVertex(); i++) {
			V v = g.getVertexAtIdx(i);
			if (v.getVertexType() == VertexType.SOURCE) {
				this.getBFS().add(v);
				q.add(v);
			}
			v.setVertexDiscovery(VertexDiscovery.UNVISITED);
		}
		// doBFS
		while(!q.isEmpty()) {
			V v = q.remove();
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
						this.getBFS().add(dst);
						q.add(dst);
					}	
				}
			}
			v.setVertexDiscovery(VertexDiscovery.VISITED);
		}
	}
	
	private Queue<V> getBFS() {
		return this.BFS;
	}
	
	public BFS() {
		init();
	}
	
	public BFS(final G g) {
		init();
		this.setGraph(g);
	}
	
	public void setGraph(final G g) {
		reset();
		this.graph = g;
		this.doBFS();
	}

	public G getGraph() {
		return this.graph;
	}
	
	public V getNextVertex() {
		V rtn = null;
		if (!this.getBFS().isEmpty())
			rtn = this.getBFS().remove();
		return rtn;
	}

	/*
	 * HashCode
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((BFS == null) ? 0 : BFS.hashCode());
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
		if (!(obj instanceof BFS<?,?,?>)) {
			return false;
		}
		BFS<?,?,?> other = (BFS<?,?,?>) obj;
		if (BFS == null) {
			if (other.BFS != null)
				return false;
		} else if (!BFS.equals(other.BFS))
			return false;
		if (graph == null) {
			if (other.graph != null)
				return false;
		} else if (!graph.equals(other.graph))
			return false;
		return true;
	}


	private G graph;
	private Queue<V> BFS;
}
