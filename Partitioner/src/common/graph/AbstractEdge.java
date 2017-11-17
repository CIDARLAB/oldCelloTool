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

import common.CObject;
import common.CObjectCollection;
import common.Utils;

/**
 * @author: Vincent Mirian
 * 
 * @date: Nov 15, 2017
 *
 */
 
abstract public class AbstractEdge <T extends AbstractVertex<?>> extends CObject {

	private void init() {
		this.dst = new CObjectCollection<T>();
	}
	
	public AbstractEdge(){
		this.init();
        this.setSrc(null);
	}
	
	public AbstractEdge(final T Src) {
		this();
        this.setSrc(Src);
    }
	
	public AbstractEdge(final AbstractEdge<T> other) {
		super(other);
		this.init();
        this.setSrc(other.getSrc());
        this.getMyDst().addAll(other.getMyDst());
    }

	public void setSrc(final T Src){
		this.src = Src;
	}
		
	public T getSrc(){
		return this.src;
	}
	
	public void addDst(final T Dst){
		if (Dst != null) {
			this.getMyDst().add(Dst);	
		}
	}
	
	public T getDstAtIdx(int index){
		T rtn = null;
		if (
				(index >= 0) &&
				(index < this.getNumDst())
			){
			rtn = this.getMyDst().get(index);	
		}
		return rtn;
	}
	
	public int getNumDst(){
		int rtn = this.getMyDst().size();
		return rtn;
	}

	/*
	 * dot file
	 */
	protected String getData(){
		String rtn = "";
		T src = this.getSrc();
		int numDst = this.getNumDst();
		if (src != null && numDst > 0) {
			for (int i = 0; i < numDst; i ++) {
				rtn += src.getName();
				rtn += " -> ";
				rtn += this.getDstAtIdx(i).getName();
				rtn += Utils.getNewLine();
			}
		}
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
		result = prime * result;
		for (int i = 0; i < this.getNumDst(); i ++) {
			T dst = this.getDstAtIdx(i);
			result = prime * result + ((dst == null) ? 0 : dst.getName().hashCode());	
		}
		result = prime * result + ((src == null) ? 0 : src.getName().hashCode());
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
		if (!(obj instanceof AbstractEdge<?>)) {
			return false;
		}
		AbstractEdge<?> other = (AbstractEdge<?>) obj;
		if (this.getMyDst() != other.getMyDst())
			return false;
		if (this.getSrc() != other.getSrc())
			return false;
		for (int i = 0; i < this.getNumDst(); i ++) {
			AbstractVertex<?> dst = this.getDstAtIdx(i);
			if (dst == null) {
				if (other.getDstAtIdx(i) != null)
					return false;
			} else {
				if (other.getDstAtIdx(i) != null) {
					if (!dst.getName().equals(other.getDstAtIdx(i).getName()))
						return false;
				}
				else
					return false;
			}	
		}
		if (src == null) {
			if (other.src != null)
				return false;
		} else {
			if (other.src != null) {
				if (!src.getName().equals(other.src.getName()))
					return false;
			}
			else
				return false;
		}
		return true;
	}

	/*
	 * toString
	 */
	protected String getDstToString(){
		String rtn = "";
		int numDst = this.getNumDst();
		for (int i = 0; i < numDst; i ++) {
			rtn = rtn + Utils.getTabCharacterRepeat(2);
			rtn = rtn + this.getDstAtIdx(i).getName();
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
		// src
		rtn = rtn + this.getEntryToString("src", src.getName());
		// dst
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "dst = ";
		rtn = rtn + Utils.getNewLine();
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "{";
		rtn = rtn + Utils.getNewLine();
		rtn = rtn + this.getDstToString();
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
		rtn = rtn + ",";
		rtn = rtn + Utils.getNewLine();
		// end
		rtn = rtn + "]";
		return rtn;
	}


	/*
	 * dst
	 */
	protected CObjectCollection<T> getMyDst(){
		return this.dst;
	}

	/*
	 * Members of class
	 */
	private T src;
	private CObjectCollection<T> dst;
}
