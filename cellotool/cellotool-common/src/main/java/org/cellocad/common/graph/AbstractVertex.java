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
package org.cellocad.common.graph;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.cellocad.common.CObject;
import org.cellocad.common.Utils;

/**
 * @author: Vincent Mirian
 *
 * @date: Nov 15, 2017
 *
 */
abstract public class AbstractVertex <T extends AbstractEdge<?>> extends CObject {

	private void init(){
		this.inEdges = new ArrayList<T>();
		this.outEdges = new ArrayList<T>();
	}

	private void reset() {
		this.setVertexType(VertexType.NONE);
		this.setVertexColor(VertexColor.WHITE);
		this.setVertexDiscovery(VertexDiscovery.UNVISITED);
		this.inEdges.clear();
		this.outEdges.clear();
	}

	public AbstractVertex(){
		init();
		reset();
	}

	abstract protected void addMeToSrc(T e);
	abstract protected void addMeToDst(T e);

	public abstract T createT(T e);

	/*@SuppressWarnings("unchecked")
	private T createT(T other) {
		T rtn = null;
		try {
			Class<? extends AbstractEdge<?>> EdgeClass = (Class<? extends AbstractEdge<?>>) other.getClass();
			Constructor<? extends AbstractEdge<?>> EdgeClassConstructor;
			EdgeClassConstructor = EdgeClass.getDeclaredConstructor(EdgeClass);
			rtn = (T) EdgeClassConstructor.newInstance(other);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			ex.printStackTrace();
		}
		return rtn;
	}*/

	public AbstractVertex(final AbstractVertex<T> other){
		super(other);
		init();
		// outEdge
		for (int i = 0; i < other.getNumOutEdge(); i++) {
			T eOther = other.getOutEdgeAtIdx(i);
			T e = createT(eOther);
			this.addOutEdge(e);
			//e.setSrc(this);
			this.addMeToSrc(e);
		}
		// inEdge
		for (int i = 0; i < other.getNumInEdge(); i++) {
			T eOther = other.getInEdgeAtIdx(i);
			T e = createT(eOther);
			this.addInEdge(e);
			//e.setDst(this);
			this.addMeToDst(e);
		}
	}

	/*
	 * VertexType
	 */
	public enum VertexType {
		NONE, SOURCE, SINK
	}

	public void setVertexType(final VertexType vertexType) {
		this.vertexType = vertexType;
	}

	public VertexType getVertexType() {
		return this.vertexType;
	}

	/*
	 * VertexColor
	 */
	public enum VertexColor {
		BLACK, GREY, WHITE
	}

	public void setVertexColor(final VertexColor vertexColor) {
		this.vertexColor = vertexColor;
	}

	public VertexColor getVertexColor() {
		return this.vertexColor;
	}

	/*
	 * VertexDiscovery
	 */
	public enum VertexDiscovery {
		VISITED, TOUCHED, UNVISITED
	}

	public void setVertexDiscovery(final VertexDiscovery vertexDiscovery) {
		this.vertexDiscovery = vertexDiscovery;
	}

	public VertexDiscovery getVertexDiscovery() {
		return this.vertexDiscovery;
	}

	/*
	 * InEdge
	 */
	public void addInEdge(final T edge){
		if (edge != null){
			inEdges.add(edge);
		}
	}

	public void removeInEdge(final T edge){
		if (edge != null){
			inEdges.remove(edge);
		}
	}

	public T getInEdgeAtIdx(int index){
		T rtn = null;
		if (
				(index >= 0) &&
				(index < this.getNumInEdge())
				){
			rtn = inEdges.get(index);
		}
		return rtn;
	}

	public int getNumInEdge(){
		int rtn = inEdges.size();
		return rtn;
	}

	public void clearInEdge(){
		inEdges.clear();
	}

	/*
	 * OutEdge
	 */
	public void addOutEdge(final T edge){
		if (edge != null){
			outEdges.add(edge);
		}
	}

	public void removeOutEdge(final T edge){
		if (edge != null){
			outEdges.remove(edge);
		}
	}

	public T getOutEdgeAtIdx(int index){
		T rtn = null;
		if (
				(index >= 0) &&
				(index < this.getNumOutEdge())
				){
			rtn = outEdges.get(index);
		}
		return rtn;
	}

	public int getNumOutEdge(){
		int rtn = outEdges.size();
		return rtn;
	}

	public void clearOutEdge(){
		outEdges.clear();
	}

	/*
	 * dot file
	 */
	protected String getData(){
		String rtn = "";
		rtn += this.getName();
		rtn += " [shape=circle, label=";
		rtn += this.getName();
		rtn += "]";
		rtn += Utils.getNewLine();
		return rtn;
	}

	public void printDot(final Writer os) throws IOException{
		os.write(this.getData());
	}

	/*
	 * HashCode
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((inEdges == null) ? 0 : inEdges.hashCode());
		result = prime * result + ((outEdges == null) ? 0 : outEdges.hashCode());
		result = prime * result + ((vertexColor == null) ? 0 : vertexColor.hashCode());
		result = prime * result + ((vertexDiscovery == null) ? 0 : vertexDiscovery.hashCode());
		result = prime * result + ((vertexType == null) ? 0 : vertexType.hashCode());
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
		if (!(obj instanceof AbstractVertex<?>)) {
			return false;
		}
		AbstractVertex<?> other = (AbstractVertex<?>) obj;
		if (inEdges == null) {
			if (other.inEdges != null)
				return false;
		} else if (!inEdges.equals(other.inEdges))
			return false;
		if (outEdges == null) {
			if (other.outEdges != null)
				return false;
		} else if (!outEdges.equals(other.outEdges))
			return false;
		if (vertexColor != other.vertexColor)
			return false;
		if (vertexDiscovery != other.vertexDiscovery)
			return false;
		if (vertexType != other.vertexType)
			return false;
		return true;
	}

	/*
	 * toString
	 */
	protected String getEdgeTemplateToString(T edge) {
		String rtn = "";
		rtn = rtn + "[";
		rtn = rtn + " name = ";
		rtn = rtn + edge.getName();
		rtn = rtn + ",";
		rtn = rtn + " src = ";
		rtn = rtn + edge.getSrc().getName();
		rtn = rtn + ",";
		rtn = rtn + " dst = ";
		for (int i = 0; i < edge.getNumDst(); i ++) {
			rtn = rtn + edge.getDstAtIdx(i).getName();
			rtn = rtn + ", ";
		}
		rtn = rtn + "]";
		return rtn;
	}

	protected String getInEdgesToString() {
		String rtn = "";
		//inEdge
		for (int i = 0; i < this.getNumInEdge(); i ++) {
			rtn = rtn + Utils.getTabCharacterRepeat(2);
			T edge = this.getInEdgeAtIdx(i);
			rtn = rtn + this.getEdgeTemplateToString(edge);
			rtn = rtn + ",";
			rtn = rtn + Utils.getNewLine();
		}
		return rtn;
	}

	protected String getOutEdgesToString() {
		String rtn = "";
		//outEdge
		for (int i = 0; i < this.getNumOutEdge(); i ++) {
			rtn = rtn + Utils.getTabCharacterRepeat(2);
			T edge = this.getOutEdgeAtIdx(i);
			rtn = rtn + this.getEdgeTemplateToString(edge);
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
		// vertexType
		rtn = rtn + this.getEntryToString("vertexType", vertexType.toString());
		// vertexColor
		rtn = rtn + this.getEntryToString("vertexColor", vertexColor.toString());
		// vertexDiscovery
		rtn = rtn + this.getEntryToString("vertexDiscovery", vertexDiscovery.toString());
		// inEdges
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "inEdges = ";
		rtn = rtn + Utils.getNewLine();
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "{";
		rtn = rtn + Utils.getNewLine();
		rtn = rtn + this.getInEdgesToString();
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "}";
		rtn = rtn + Utils.getNewLine();
		// outEdges
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "outEdges = ";
		rtn = rtn + Utils.getNewLine();
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "{";
		rtn = rtn + Utils.getNewLine();
		rtn = rtn + this.getOutEdgesToString();
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

	private VertexType vertexType;
	private VertexColor vertexColor;
	private VertexDiscovery vertexDiscovery;
	private List<T> inEdges;
	private List<T> outEdges;
}
