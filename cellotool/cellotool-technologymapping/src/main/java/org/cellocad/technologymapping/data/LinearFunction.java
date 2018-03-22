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
public class LinearFunction extends Curve {
	private Double slope;
	private Double offset;

	public LinearFunction() {
		this.setSlope(1.0);
		this.setOffset(0.0);
	}

	public LinearFunction(Double slope, Double offset) {
		this.setSlope(slope);
		this.setOffset(offset);
	}

	@Override
	public Double apply(double input) {
		return this.getSlope()*input + this.getOffset();
	}

	/**
	 * @return the slope
	 */
	public Double getSlope() {
		return slope;
	}

	/**
	 * @param slope the slope to set
	 */
	public void setSlope(Double slope) {
		this.slope = slope;
	}

	/**
	 * @return the offset
	 */
	public Double getOffset() {
		return offset;
	}

	/**
	 * @param offset the offset to set
	 */
	public void setOffset(Double offset) {
		this.offset = offset;
	}

}
