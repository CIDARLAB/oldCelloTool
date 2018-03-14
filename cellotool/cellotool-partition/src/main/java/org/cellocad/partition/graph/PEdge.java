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

import org.cellocad.common.Utils;
import org.cellocad.common.capacity.Weight;
import org.cellocad.common.graph.graph.EdgeTemplate;

/**
 * @author: Vincent Mirian
 * 
 * @date: Oct 27, 2017
 *
 */
public class PEdge extends EdgeTemplate<PNode>{
	
	private void init() {
		myWeight = new Weight();
	}
	
	public PEdge(){
		super();
        this.setSrc(null);
        this.setDst(null);
        init();
	}
	
	public PEdge(final PNode Src, final PNode Dst) {
		this();
        this.setSrc(Src);
        this.setDst(Dst);
    }
	
	public PEdge(final PEdge other) {
		super(other);
        this.setSrc(other.getSrc());
        this.setDst(other.getDst());
        this.setMyWeight(other.getMyWeight());
    }

	/*
	 * Weight
	 */
	protected void setMyWeight(Weight w){
		this.myWeight = w;
	}
	
	public Weight getMyWeight(){
		return this.myWeight;
	}
	
	/*
	 * HashCode
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
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
		PEdge other = (PEdge) obj;
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
	@Override
	public String toString() {
		String rtn = "";
		String indentStr = "";
		rtn = rtn + "[ ";
		rtn = rtn + Utils.getNewLine();
		// name
		rtn = rtn + this.getEntryToString("name", this.getName());
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

	private Weight myWeight;
}
