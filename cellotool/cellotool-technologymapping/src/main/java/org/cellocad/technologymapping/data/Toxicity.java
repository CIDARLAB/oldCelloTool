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
package org.cellocad.technologymapping.data;

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
public class Toxicity extends CObject {

	private Pair<List<Double>,List<Double>> toxicity;

	/**
	 * Create a new Toxicity object.
	 *
	 * @param input the vector of inputs.
	 * @param growth the vector of growth values.
	 */
	public Toxicity() {
		super();
	}

	/**
	 * Create a new Toxicity object.
	 *
	 * @param input the vector of inputs.
	 * @param growth the vector of growth values.
	 */
	public Toxicity(List<Double> input, List<Double> growth) {
		super();
		if (input.size() != growth.size()) {
			throw new IllegalArgumentException("Vectors must be of equal length.");
		}
		this.setToxicity(new Pair<List<Double>,List<Double>>(input,growth));
	}

	/**
	 * Get the size of the Toxicity table.
	 *
	 * @return the size.
	 */
	public int size() {
		return this.getToxicity().getFirst().size();
	}

	/**
	 * Get a row in the toxicity table.
	 *
	 * @return the row at the specified index.
	 */
	public Pair<Double,Double> getRow(int i) {
		return new Pair<Double,Double>(this.getToxicity().getFirst().get(i),
									   this.getToxicity().getSecond().get(i));
	}

	/**
	 * @return the toxicity
	 */
	private Pair<List<Double>,List<Double>> getToxicity() {
		return toxicity;
	}

	/**
	 * @param toxicity the toxicity to set
	 */
	private void setToxicity(final Pair<List<Double>,List<Double>> toxicity) {
		this.toxicity = toxicity;
	}

		/*
	 * HashCode
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((toxicity == null) ? 0 : toxicity.hashCode());
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
		if (!(obj instanceof Toxicity)) {
			return false;
		}
		Toxicity other = (Toxicity) obj;
		if (toxicity == null) {
			if (other.toxicity != null)
				return false;
		} else if (!toxicity.equals(other.toxicity))
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
				Pair<Double,Double> p = this.getRow(i);
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
