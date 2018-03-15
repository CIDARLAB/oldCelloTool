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
package org.cellocad.technologymapping.common.score;

import org.cellocad.technologymapping.common.techmap.TechNode;

/**
 * @author: Timothy Jones
 * 
 * @date: Mar 14, 2018
 *
 */
public class ScoreUtils{

	/**
	 * Return the lowest on by highest off ratio for a TechNode.
	 * 
	 * @param node the TechNode to score.
	 * @return the on off ratio.
	 */
	public static Double getOnOffRatio(TechNode node) {
		Double lowestOn = Double.MAX_VALUE;
        Double highestOff = Double.MIN_VALUE;
		
		for(int i = 0; i < node.getLogic().size(); ++i) {
			Double a = node.getActivity().get(i);
			
			if (node.getLogic().get(i) == true
				&&
				lowestOn > a) {
				lowestOn = a;
			} else if (node.getLogic().get(i) == false
					   &&
					   highestOff < a) {
				highestOff = a;
			}
		}
		
		return lowestOn/highestOff;
	}

}
