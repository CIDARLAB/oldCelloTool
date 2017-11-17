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
package common.graph;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import common.CObject;
import common.CObjectCollection;
import common.Utils;

/**
 * @author: Vincent Mirian
 * 
 * @date: Nov 15, 2017
 *
 */
abstract public class AbstractGraph<V extends AbstractVertex<E>, E extends AbstractEdge<V>> extends CObject {

	private void init(){
		this.vertices = new CObjectCollection<V>();
		this.edges = new CObjectCollection<E>();		
	}
	
	public AbstractGraph(){
		init();
	}
	
	protected void addVertexToSrc(V v, E e) {
		e.setSrc(v);
	}
	
	protected void addVertexToDst(V v, E e){
		e.addDst(v);
	}
	
	protected void addEdgeToInEdge(V v, E e) {
		v.addInEdge(e);
	}
	
	protected void addEdgeToOutEdge(V v, E e){
		v.addOutEdge(e);
	}

	public abstract V createV(V other);
	
	public abstract E createE(E other);
	
	/*@SuppressWarnings("unchecked")
	private E createE(E other) {
		E rtn = null;
		try {
			Class<? extends EdgeTemplate<?>> Class = (Class<? extends EdgeTemplate<?>>) other.getClass();
			Constructor<? extends EdgeTemplate<?>> ClassConstructor;
			ClassConstructor = Class.getDeclaredConstructor(Class);
			rtn = (E) ClassConstructor.newInstance(other);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			ex.printStackTrace();
		}
		return rtn;
	}
	
	@SuppressWarnings("unchecked")
	private V createV(V other) {
		V rtn = null;
		try {
			Class<? extends VertexTemplate<?>> Class = (Class<? extends VertexTemplate<?>>) other.getClass();
			Constructor<? extends VertexTemplate<?>> ClassConstructor;
			ClassConstructor = Class.getDeclaredConstructor(Class);
			rtn = (V) ClassConstructor.newInstance(other);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			ex.printStackTrace();
		}
		return rtn;
	}*/
	
	public AbstractGraph(final AbstractGraph<V, E> other){
		super(other);
		init();
		Map<V, V> mapVertexOtherThis = new HashMap<V, V>();
		Map<E, E> mapEdgeOtherThis = new HashMap<E, E>();
		// copy Vertex
		for (int i = 0; i < other.getNumVertex(); i++){
			V v = other.getVertexAtIdx(i);
			V vertex = createV(v);
			vertex.clearInEdge();
			vertex.clearOutEdge();
			mapVertexOtherThis.put(v, vertex);
			this.addVertex(vertex);
		}
		// copy Edge
		for (int i = 0; i < other.getNumEdge(); i++){
			E e = other.getEdgeAtIdx(i);
			E edge = createE(e);
			mapEdgeOtherThis.put(e, edge);
			this.addEdge(edge);
		}
		// for each Vertex:
		for (int i = 0; i < other.getNumVertex(); i++){
			V v = other.getVertexAtIdx(i);
			V vertex = mapVertexOtherThis.get(v);
			assert (vertex != null);
			// set outEdge for Vertex
			for (int j = 0; j < v.getNumOutEdge(); j ++){
				E e = v.getOutEdgeAtIdx(j);
				E edge = mapEdgeOtherThis.get(e);
				assert (edge != null);
				//vertex.addOutEdge(edge);		
				addEdgeToOutEdge(vertex, edge);		
			}
			// set inEdge for Vertex
			for (int j = 0; j < v.getNumInEdge(); j ++){
				E e = v.getInEdgeAtIdx(j);
				E edge = mapEdgeOtherThis.get(e);
				assert (edge != null);
				//vertex.addInEdge(edge);
				addEdgeToInEdge(vertex, edge);
			}			
		}
		// for each Edge:
		for (int i = 0; i < other.getNumEdge(); i++){
			E e = other.getEdgeAtIdx(i);
			E edge = mapEdgeOtherThis.get(e);
			assert (edge != null);
			// set src for Edge
			{
				V v = e.getSrc();
				V vertex = mapVertexOtherThis.get(v);
				assert (vertex != null);
				//edge.setSrc(vertex);
				addVertexToSrc(vertex, edge);
			}
			// set dst for Edge
			{
				for (int j = 0; j < e.getNumDst(); j++) {
					V v = e.getDstAtIdx(j);
					V vertex = mapVertexOtherThis.get(v);
					assert (vertex != null);
					//edge.setDst(vertex);
					addVertexToDst(vertex, edge);					
				}
			}
		}
	}
	
	/*
	 * Vertices
	 */
	public void addVertex(final V vertex){
		if (vertex != null){
			vertices.add(vertex);
		}
	}
	
	public void removeVertex(final V vertex){
		if (vertex != null){
			vertices.remove(vertex);
		}
	}

	public V getVertexAtIdx(int index){
		V rtn = null;
		if (
				(index >= 0) &&
				(index < this.getNumVertex())
			){
			rtn = vertices.get(index);	
		}
		return rtn;
	}

	public V getVertexByName(final String name){
		V rtn = null;
		rtn = vertices.findCObjectByName(name);
		return rtn;
	}
	
	public int getNumVertex(){
		int rtn = vertices.size();
		return rtn;
	}

	/*
	 * Edges
	 */
	public void addEdge(final E edge){
		if (edge != null){
			edges.add(edge);
		}
	}
	
	public void removeEdge(final E edge){
		if (edge != null){
			edges.remove(edge);
		}
	}
	
	public E getEdgeAtIdx(int index){
		E rtn = null;
		if (
				(index >= 0) &&
				(index < this.getNumEdge())
			){
			rtn = edges.get(index);	
		} 
		return rtn;
	}

	public E getEdgeByName(final String name){
		E rtn = null;
		rtn = edges.findCObjectByName(name);
		return rtn;
	}
	
	public int getNumEdge(){
		int rtn = edges.size();
		return rtn;
	}

	/*
	 * is valid?
	 */
	@Override
	public boolean isValid(){
		boolean rtn = true;
		V v = null;
		E e = null;
		// parent is valid
		rtn = rtn && super.isValid();
		/*
		 *  for each vertex, ensure that:
		 *  1) vertex is valid
		 *  2) the in/out edges are in graph
		 */
		for (int i = 0; rtn && (i < vertices.size()); i++){
			v = this.getVertexAtIdx(i);
			// 1) vertex is valid
			rtn = rtn && v.isValid();
			// 2) the in/out edges are in graph
			for (int j = 0; j < v.getNumOutEdge(); j++){
				e = v.getOutEdgeAtIdx(j);
				rtn = rtn && edges.contains(e);
			}
			for (int j = 0; j < v.getNumInEdge(); j++){
				e = v.getInEdgeAtIdx(j);
				rtn = rtn && edges.contains(e);
			}
		}
		/*
		 *  for each edge, ensure that:
		 *  1) edge is valid
		 *  2) the src/dst vertices are in graph
		 */
		for (int i = 0; rtn && (i < edges.size()); i++){
			e = this.getEdgeAtIdx(i);
			// 1) edge is valid
			rtn = rtn && e.isValid();
			// 2) the src/dst vertices are in graph
			v = e.getSrc();
			rtn = rtn && vertices.contains(v);
			for (int j = 0; j < e.getNumDst(); j++) {
				v = e.getDstAtIdx(j);
				rtn = rtn && vertices.contains(v);	
			}
		}
		return rtn;
	}	

	/*
	 * dot file
	 */
	protected String getDotHeader(){
		String rtn = "";
		rtn += "digraph ";
		rtn += this.getName();
		rtn += " {";
		rtn += System.lineSeparator();
		return rtn;
	}
	protected String getDotFooter(){
		String rtn = "";
		rtn += "}";
		rtn += System.lineSeparator();
		return rtn;
	}
	
	public void printDot(final Writer os) throws IOException{
		os.write(this.getDotHeader());
		for (int i = 0; i < vertices.size(); i++){
			this.getVertexAtIdx(i).printDot(os);
		}
		for (int i = 0; i < edges.size(); i++){
			this.getEdgeAtIdx(i).printDot(os);
		}
		os.write(this.getDotFooter());		
	}

	/*
	 * HashCode
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((edges == null) ? 0 : edges.hashCode());
		result = prime * result + ((vertices == null) ? 0 : vertices.hashCode());
		return result;
	}

	/*
	 * Equals
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		if (!(obj instanceof AbstractGraph<?, ?>)) {
			return false;
		}
		AbstractGraph<?, ?> other = (AbstractGraph<?, ?>) obj;
		if (edges == null) {
			if (other.edges != null)
				return false;
		} else if (!edges.equals(other.edges))
			return false;
		if (vertices == null) {
			if (other.vertices != null)
				return false;
		} else if (!vertices.equals(other.vertices))
			return false;
		return true;
	}

	/*
	 * toString
	 */
	protected String getVerticesToString() {
		String rtn = "";
		for (int i = 0; i < this.getNumVertex(); i ++) {
			rtn = rtn + Utils.getTabCharacterRepeat(2);
			V vertex = this.getVertexAtIdx(i);
			rtn = rtn + vertex.getName();
			rtn = rtn + ",";
			rtn = rtn + Utils.getNewLine();
		}
		return rtn;
	}

	protected String getEdgesToString() {
		String rtn = "";
		for (int i = 0; i < this.getNumEdge(); i ++) {
			rtn = rtn + Utils.getTabCharacterRepeat(2);
			E edge = this.getEdgeAtIdx(i);
			rtn = rtn + edge.getName();
			rtn = rtn + ",";
			rtn = rtn + Utils.getNewLine();
		}
		return rtn;
	}
	
	@Override
	public String toString() {
		String rtn = "";
		String superStr = "";
		rtn = rtn + "[ ";
		rtn = rtn + Utils.getNewLine();
		// name
		rtn = rtn + this.getEntryToString("name", this.getName());
		// vertex
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "vertices = ";
		rtn = rtn + Utils.getNewLine();
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "{";
		rtn = rtn + Utils.getNewLine();
		rtn = rtn + this.getVerticesToString();
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "}";
		rtn = rtn + Utils.getNewLine();
		// edge
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "edges = ";
		rtn = rtn + Utils.getNewLine();
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "{";
		rtn = rtn + Utils.getNewLine();
		rtn = rtn + this.getEdgesToString();
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "}";
		rtn = rtn + Utils.getNewLine();		
		// toString
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "toString() = ";
		rtn = rtn + Utils.getNewLine();
		superStr = super.toString();
		superStr = Utils.addIndent(1, superStr);
		rtn = rtn + superStr;
		rtn = rtn + Utils.getNewLine();
		// end
		rtn = rtn + "]";
		return rtn;
	}

	private CObjectCollection<V> vertices;
	private CObjectCollection<E> edges;
}
