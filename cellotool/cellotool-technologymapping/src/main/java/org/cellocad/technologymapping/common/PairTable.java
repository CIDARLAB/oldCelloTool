/**
 * Copyright (C) 2018 Boston University (BU)
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
package org.cellocad.technologymapping.common;

import java.util.List;

import org.cellocad.common.CObject;
import org.cellocad.common.Pair;
import org.cellocad.common.Utils;

/**
 * @author: Timothy Jones
 * 
 * @date: Mar 15, 2018
 *
 */
public abstract class PairTable<L extends Number, R extends Number> extends CObject {

	private Pair<List<L>, List<R>> data;

	public PairTable() {
		super();
	}

	/**
	 * Create a new PairTable object.
	 *
	 * @param input the vector of inputs.
	 * @param growth the vector of growth values.
	 */
	public PairTable(final List<L> left, final List<R> right) {
		super();
		if (left.size() != right.size()) {
			throw new IllegalArgumentException("Vectors must be of equal length.");
		}
		this.setData(new Pair<List<L>, List<R>>(left,right));
	}

	/**
	 * Get the size of the table.
	 *
	 * @return the size.
	 */
	public int size() {
		return this.getData().getFirst().size();
	}

	/**
	 * Get a row in the toxicity table.
	 *
	 * @return the row at the specified index.
	 */
	public Pair<L,R> getRow(int i) {
		return new Pair<L,R>(this.getData().getFirst().get(i),
							 this.getData().getSecond().get(i));
	}
	
	/**
	 * @return the data
	 */
	private Pair<List<L>, List<R>> getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	private void setData(Pair<List<L>, List<R>> data) {
		this.data = data;
	}
	
	/*
	 * HashCode
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((data == null) ? 0 : data.hashCode());
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
		if (!(obj instanceof PairTable)) {
			return false;
		}
		PairTable<?,?> other = (PairTable<?,?>) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}

	@Override
	public String toString() {
		String rtn = "";
		String indentStr = "";
		rtn += "[";
		if (this.size() > 0) {
			rtn += Utils.getNewLine();
			indentStr += "table = ";
			indentStr += Utils.getNewLine();
			indentStr += "[";
			indentStr += Utils.getNewLine();
			for (int i = 0; i < this.size(); i++) {
				indentStr += Utils.getTabCharacter();
				Pair<L,R> p = this.getRow(i);
				indentStr += p.getFirst();
				indentStr += ",";
				indentStr += Utils.getTabCharacter();
				indentStr += p.getSecond();
				indentStr += Utils.getNewLine();
			}
			indentStr += "],";
			indentStr = Utils.addIndent(1, indentStr);
			rtn += indentStr;
		}
		rtn += Utils.getNewLine();
		// toString
		rtn += Utils.getTabCharacter();
		rtn += "toString() = ";
		rtn += Utils.getNewLine();
		indentStr = super.toString();
		indentStr = Utils.addIndent(1, indentStr);
		rtn += indentStr;
		rtn += ",";
		rtn += Utils.getNewLine();
		// end
		rtn += "]";
		return rtn;
	}

}
