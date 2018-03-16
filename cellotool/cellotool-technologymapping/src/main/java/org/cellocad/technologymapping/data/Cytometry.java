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

import java.util.Map;
import java.util.Set;

import org.cellocad.common.CObject;

/**
 * @author: Timothy Jones
 * 
 * @date: Mar 15, 2018
 *
 */
public class Cytometry extends CObject{

	private Map<Double, Histogram> data;

	public Cytometry(Map<Double,Histogram> data) {
		super();
		this.setData(data);
	}

	public Set<Double> getInputs() {
		return data.keySet();
	}

	public Histogram getHistogramOfInput(Double input) {
		return this.getData().get(input);
	}

	public Histogram interpolate(Double input) {
		Histogram rtn = null;
		return rtn;
	}

	/**
	 * @return the data
	 */
	private Map<Double, Histogram> getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	private void setData(Map<Double, Histogram> data) {
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
		if (!(obj instanceof Cytometry)) {
			return false;
		}
		Cytometry other = (Cytometry) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}
	
}
