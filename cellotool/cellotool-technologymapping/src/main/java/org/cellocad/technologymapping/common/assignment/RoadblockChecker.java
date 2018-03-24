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
package org.cellocad.technologymapping.common.assignment;

import java.util.Collection;

import org.cellocad.common.CObject;
import org.cellocad.common.CObjectCollection;
import org.cellocad.technologymapping.common.netlist.TMEdge;
import org.cellocad.technologymapping.common.netlist.TMNetlist;
import org.cellocad.technologymapping.common.netlist.TMNode;
import org.cellocad.technologymapping.data.Gate;

/**
 * @author: Timothy Jones
 *
 * @date: Mar 23, 2018
 *
 */
public class RoadblockChecker extends CObject{

	/**
	 * {@inheritDoc}
	 *
	 * @see CObject#RoadblockChecker()
	 */
	public RoadblockChecker() {
		super();
	}

	/**
	 * Create a new RoadblockChecker to act upon the given netlist.
	 *
	 * @param netlist the netlist to check.
	 */
	public RoadblockChecker(TMNetlist netlist) {
		this();
		this.setTMNetlist(netlist);
	}

	/**
	 * Get the total number of roadblocks for the assignment.
	 *
	 * @return the number of roadblocks.
	 */
	public Integer getNumRoadblocks() {
		int rtn = 0;
		int num = this.getTMNetlist().getNumVertex();
		for (int i = 0; i < num; i++) {
			TMNode node = this.getTMNetlist().getVertexAtIdx(i);
			if (this.getNumRoadblocks(node) > 0) {
				rtn++;
			}
		}
		return rtn;
	}

	/**
	 * Get the number of roadblocks for a particular TMNode.
	 *
	 * @return the number of roadblocks.
	 */
	private Integer getNumRoadblocks(TMNode node) {
		int rtn = 0;
		Integer numInputRoadblocks = 0;
		Integer numLogicRoadblocks = 0;
		for (int i = 0; i < node.getNumInEdge(); i++) {
			TMEdge e = node.getInEdgeAtIdx(i);
			TMNode src = e.getSrc();

			if (this.getInputRoadblocks().contains(src.getGate().getName())) {
				numInputRoadblocks++;
			}
			if (this.getLogicRoadblocks().contains(src.getGate().getPromoter())) {
				numLogicRoadblocks++;
			}
		}
		int total = numInputRoadblocks + numLogicRoadblocks;

		if(numLogicRoadblocks > 0 && total > 1) {
			rtn++;
		}

		return rtn;
	}

	private TMNetlist tmNetlist;
	private CObjectCollection<Gate> gateLibrary;
	private Collection<String> logicRoadblocks;
	private Collection<String> inputRoadblocks;

	/**
	 * @return the tmNetlist
	 */
	public TMNetlist getTMNetlist() {
		return tmNetlist;
	}

	/**
	 * @param tmNetlist the tmNetlist to set
	 */
	public void setTMNetlist(final TMNetlist tmNetlist) {
		this.tmNetlist = tmNetlist;
	}

	/**
	 * @return the gateLibrary
	 */
	public CObjectCollection<Gate> getGateLibrary() {
		return gateLibrary;
	}

	/**
	 * @param gateLibrary the gateLibrary to set
	 */
	public void setGateLibrary(final CObjectCollection<Gate> gateLibrary) {
		this.gateLibrary = gateLibrary;
	}

	/**
	 * @return the logicRoadblocks
	 */
	public Collection<String> getLogicRoadblocks() {
		return logicRoadblocks;
	}

	/**
	 * @param logicRoadblocks the logicRoadblocks to set
	 */
	public void setLogicRoadblocks(Collection<String> logicRoadblocks) {
		this.logicRoadblocks = logicRoadblocks;
	}

	/**
	 * @return the inputRoadblocks
	 */
	public Collection<String> getInputRoadblocks() {
		return inputRoadblocks;
	}

	/**
	 * @param inputRoadblocks the inputRoadblocks to set
	 */
	public void setInputRoadblocks(Collection<String> inputRoadblocks) {
		this.inputRoadblocks = inputRoadblocks;
	}

}
