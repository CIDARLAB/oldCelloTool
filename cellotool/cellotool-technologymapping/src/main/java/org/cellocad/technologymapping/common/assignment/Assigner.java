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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.cellocad.common.CObject;
import org.cellocad.common.CObjectCollection;
import org.cellocad.common.Utils;
import org.cellocad.technologymapping.common.netlist.TMNetlist;
import org.cellocad.technologymapping.common.netlist.TMNode;
import org.cellocad.technologymapping.data.Gate;

/**
 * @author: Timothy Jones
 *
 * @date: Mar 23, 2018
 *
 */
public class Assigner extends CObject{

	public Assigner() {
		super();
	}

	public Assigner(TMNetlist netlist) {
		this();
		this.setTMNetlist(netlist);
	}

	/**
	 * Do a random assignment of gates onto the TMNetlist.
	 */
	public void doRandomAssignment() {
		Utils.isNullRuntimeException(this.getGateLibrary(),"gateLibrary");
		Utils.isNullRuntimeException(this.getTMNetlist(),"tmNetlist");
		// Set<String> groups = TMUtils.getGateGroups(gateLibrary);

		// Map<String,List<Gate>> gatesByType = TMUtils.getGatesByType(gateLibrary);
		// Map<String,Iterator<Gate>> it = new HashMap<>();
		// for (String type : gatesByType.keySet()) {
		// it.put(type,gatesByType.get(type).iterator());
		// }
		Collections.shuffle(gateLibrary);
		Iterator<Gate> it = gateLibrary.iterator();

		int num = this.getTMNetlist().getNumVertex();
		for (int j = 0; j < num; j++) {
			TMNode node = this.getTMNetlist().getVertexAtIdx(j);
			String type = node.getNodeType();
			if (!type.equals("TopInput") && !type.equals("TopOutput")) {
				// if (!gatesByType.keySet().contains(type)) {
				// 	throw new RuntimeException("No gates of type " + type
				// 							   + " (node '" + node.getName() + "') exist in the library.");
				// }
				while (node.getGate() == null
						||
						node.getGate() == new Gate()) {
					if (!it.hasNext()) {
						throw new RuntimeException("Not enough gates in the library to cover the netlist.");
					}
					Gate g = it.next();
					if (!this.getTMNetlist().hasGatesOfGroup(g.getGroup())) {
						node.setGate(g);
					}
				}
			}
		}
	}

	/**
	 * Assign input sensors from a library to a netlist.
	 *
	 * @param inputLibrary the input library from which to make assignments.
	 */
	public void assignInputSensors(List<Gate> inputLibrary) {
		List<TMNode> nodes = this.getTMNetlist().getInputNodes();
		Iterator<Gate> it = inputLibrary.iterator();
		for (TMNode node : nodes) {
			if (!it.hasNext()) {
				throw new RuntimeException("Not input sensors in the library to cover the netlist inputs.");
			}
			Gate g = it.next();
			node.setGate(g);
		}
	}

	/**
	 * Assign output reporters from a library to a netlist.
	 *
	 * @param outputLibrary the output library from which to make assignments.
	 */
	public void assignOutputReporters(List<Gate> outputLibrary) {
		List<TMNode> nodes = this.getTMNetlist().getOutputNodes();
		Iterator<Gate> it = outputLibrary.iterator();
		for (TMNode node : nodes) {
			if (!it.hasNext()) {
				throw new RuntimeException("Not output reporters in the library to cover the netlist outputs.");
			}
			Gate g = it.next();
			node.setGate(g);
		}
	}

	public void assignRandomGate() {
		Random rand = new Random();
		List<TMNode> logicNodes = this.getTMNetlist().getLogicNodes();
		// get a random gate
		Integer aIdx = rand.nextInt(logicNodes.size());
		Gate aGate = logicNodes.get(aIdx).getGate();
		// get a second gate, either used or unused
		Gate bGate = getAssignableGate(aGate);

		// 1. if second gate is used, swap
		if (this.getTMNetlist().hasGate(bGate)) {
			Integer bIdx = 0; // need to know the second gate index
			for(int i = 0; i < logicNodes.size(); i++) {
				if (logicNodes.get(i).getGate().getName()
					.equals(bGate.getName())) {
					bIdx = i;
					break;
				}
			}
			// swap
			logicNodes.get(aIdx).setGate(bGate);
			logicNodes.get(bIdx).setGate(aGate);

		}
		// 2. if second gate is unused, substitute
		else {
			logicNodes.get(aIdx).setGate(bGate);
		}
	}

		/**
	 * Get a candidate gate for assignment.
	 *
	 * @param gate the Gate to be swapped or subsituted.
	 * @param netlist the TMNetlist to use when checking for gate validity.
	 * @param gateLibrary the gate library from which to pull candidates.
	 */
	private Gate getAssignableGate(Gate gate) {
		List<Gate> options = new ArrayList<>();
		for(Gate g : this.getGateLibrary()) {
			if(g.getName().equals(gate.getName())) {
				continue;
			}
			if(g.getGroup().equals(gate.getGroup())) {
				options.add(g);
			}
			if (!this.getTMNetlist().hasGatesOfGroup(g.getGroup())
				||
				this.getTMNetlist().hasGate(g)) {
				options.add(g);
			}
		}
		return options.get(new Random().nextInt(options.size()));
	}

	private TMNetlist tmNetlist;
	private CObjectCollection<Gate> gateLibrary;

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

}
