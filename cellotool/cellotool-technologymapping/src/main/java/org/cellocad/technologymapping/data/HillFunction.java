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

/**
 * @author: Timothy Jones
 * 
 * @date: Mar 9, 2018
 *
 */
public class HillFunction extends Curve {
	private Double ymax;
	private Double ymin;
	private Double k;
	private Double n;
	
	@Override
	public Double apply(double input) {
		return this.getYmin()+(this.getYmax()-this.getYmin())/(1.0 + Math.pow(input/this.getK(),this.getN()));
	}

	/**
	 * @return the ymax
	 */
	public Double getYmax() {
		return ymax;
	}

	/**
	 * @param ymax the ymax to set
	 */
	public void setYmax(Double ymax) {
		this.ymax = ymax;
	}

	/**
	 * @return the ymin
	 */
	public Double getYmin() {
		return ymin;
	}

	/**
	 * @param ymin the ymin to set
	 */
	public void setYmin(Double ymin) {
		this.ymin = ymin;
	}

	/**
	 * @return the k
	 */
	public Double getK() {
		return k;
	}

	/**
	 * @param k the k to set
	 */
	public void setK(Double k) {
		this.k = k;
	}

	/**
	 * @return the n
	 */
	public Double getN() {
		return n;
	}

	/**
	 * @param n the n to set
	 */
	public void setN(Double n) {
		this.n = n;
	}

}
