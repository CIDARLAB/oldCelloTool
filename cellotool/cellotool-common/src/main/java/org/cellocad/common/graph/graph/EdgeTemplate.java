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

import org.cellocad.common.Utils;
import org.cellocad.common.graph.AbstractEdge;

/**
 * @author: Vincent Mirian
 *
 * @date: Nov 2, 2017
 *
 */
public class EdgeTemplate<T extends VertexTemplate<?>> extends AbstractEdge<T>{

	public EdgeTemplate(){
		super();
		this.setSrc(null);
		this.setDst(null);
	}

	public EdgeTemplate(final T Src) {
		super(Src);
		this.setDst(null);
	}

	public EdgeTemplate(final EdgeTemplate<T> other) {
		super(other);
	}

	public void setDst(final T Dst){
		this.addDst(Dst);
	}

	public T getDst(){
		return this.getDstAtIdx(0);
	}

	@Override
	public void addDst(final T Dst){
		if (Dst != null) {
			if (this.getNumDst() == 0) {
				this.getMyDst().add(Dst);
			}
			else {
				this.getMyDst().set(0, Dst);
			}
			assert(this.getNumDst() == 1);
		}
	}
	/*
	 * toString
	 */
	@Override
	public String toString() {
		String rtn = "";
		String superStr = "";
		rtn = rtn + "[ ";
		rtn = rtn + Utils.getNewLine();
		// name
		rtn = rtn + this.getEntryToString("name", this.getName());
		// src
		rtn = rtn + this.getEntryToString("src", this.getSrc().getName());
		// dst
		rtn = rtn + this.getEntryToString("dst", this.getDst().getName());
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
}
