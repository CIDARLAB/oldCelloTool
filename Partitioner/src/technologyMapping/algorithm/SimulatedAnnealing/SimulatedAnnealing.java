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
package technologyMapping.algorithm.SimulatedAnnealing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import common.graph.AbstractVertex.VertexType;
import technologyMapping.algorithm.TMAlgorithm;
import technologyMapping.data.Gate;
import technologyMapping.netlist.TMNetlist;
import technologyMapping.netlist.TMNode;

/**
 * @author: Vincent Mirian
 * 
 * @date: Dec 4, 2017
 *
 */

public class SimulatedAnnealing extends TMAlgorithm{

	protected boolean doAssignments(List<GateAssignment> gateAssignments) {
		boolean rtn = true;
		GateAssignment ga = null;
		// unassign
		Iterator<GateAssignment> gaIter = null;
		gaIter = gateAssignments.iterator();
		while (rtn &&
				(gaIter.hasNext())){
			ga = gaIter.next();
			if (ga == null) {
				continue;
			}
			rtn = rtn && this.doUnassignment(ga);
		}
		// assign
		gaIter = gateAssignments.iterator();
		while (rtn &&
				(gaIter.hasNext())){
			ga = gaIter.next();
			if (ga == null) {
				continue;
			}
			rtn = rtn && this.doAssignment(ga);
		}
		return rtn;
	}

	protected boolean doSingleAssignment(GateAssignment gateAssignment) {
		boolean rtn = true;
		rtn = rtn && this.doUnassignment(gateAssignment);
		rtn = rtn && this.doAssignment(gateAssignment);
		return rtn;
	} 

	protected boolean doUnassignment(GateAssignment gateAssignment) {
		boolean rtn = false;
		List<String> groupList = this.getGroupList();
		TMNode tmNode = gateAssignment.getTMNode();
		Gate gate = gateAssignment.getGate();
		rtn = (tmNode.getGate() == gate);
		if (rtn) {
			// if assigned
			if (gate != null) {
				// remove assignment
				groupList.add(gate.getGroup());
				tmNode.setGate(null);
			}
		}
		return rtn;
	}
	
	protected boolean doAssignment(GateAssignment gateAssignment) {
		boolean rtn = true;
		List<String> groupList = this.getGroupList();
		TMNode tmNode = gateAssignment.getTMNode();
		Gate newGate = gateAssignment.getNewGate();
		if (rtn) {
			// assign
			{
				tmNode.setGate(newGate);
				assert(groupList.remove(newGate.getGroup()));
			}
		}
		return rtn;
	}

	protected GateAssignment getFirstAssignment() {
		GateAssignment rtn = null;
		TMNetlist netlist = this.getNetlist();
		TMNode nodeA = netlist.getVertexAtIdx(this.getRNG().nextInt(netlist.getNumVertex()));
		rtn = new GateAssignment(nodeA, nodeA.getGate(), null);
		return rtn;
	}

	protected GateAssignment getSecondAssignment(GateAssignment other) {
		GateAssignment rtn = null;
		Map<String, List <Gate> > groupGates = this.getGroupGates();
		List<String> groupList = this.getGroupList();
		TMNetlist netlist = this.getNetlist();
		Random rng = this.getRNG();
		Gate gate = null;
		int random = this.getRNG().nextInt(2);
		// getFromUnassigned
		if ((random == 0) && (!groupList.isEmpty())) {
			String key = (String) groupList.get(rng.nextInt(groupList.size()));
			List <Gate> gates = groupGates.get(key);
			gate = gates.get(rng.nextInt(gates.size()));
		}
		// getFromAssigned
		else {
			TMNode nodeB = netlist.getVertexAtIdx(this.getRNG().nextInt(netlist.getNumVertex()));
			rtn = new GateAssignment(nodeB, nodeB.getGate(), other.getGate());
			gate = nodeB.getGate();
		}
		other.setNewGate(gate);
		return rtn;
	}
	
	protected void initialAssignment() {
		Map<String, List <Gate> > groupGates = this.getGroupGates();
		List<String> groupList = this.getGroupList();
		Random rng = this.getRNG();
		// tmNodes
		TMNetlist netlist = this.getNetlist();
		List<TMNode> tmNodes = new ArrayList<TMNode>();
		for (int i = 0; i < netlist.getNumVertex(); i ++) {
			TMNode tmNode = netlist.getVertexAtIdx(i);
			if (tmNode.getVertexType() == VertexType.NONE) {
				tmNodes.add(tmNode);
			}
		}
		// initial random
		while(
				(tmNodes.size() != 0) && 
				(groupList.size() != 0)
				) {
			TMNode tmNode = tmNodes.get(rng.nextInt(tmNodes.size()));
			String key = (String) groupList.get(rng.nextInt(groupList.size()));
			List <Gate> gates = groupGates.get(key);
			Gate gate = gates.get(rng.nextInt(gates.size()));
			this.doSingleAssignment(new GateAssignment(tmNode, tmNode.getGate(), gate));
			// remove
			tmNodes.remove(tmNode);
		}
		if (tmNodes.size() != 0) {
			this.logWarn("Not all Nodes assigned!");
		}
	}
	
	protected void doIterations() {
		// TODO: numIterations
		/*TMNetlist netlist = this.getNetlist();
		int numIterations = 100;
		Double MAXTEMP = 100.0;
		Double MINTEMP = 0.001;
		Integer STEPS = numIterations;
		Double LOGMAX = Math.log10(MAXTEMP);
		Double LOGMIN = Math.log10(MINTEMP);
		Double LOGINC = (LOGMAX - LOGMIN) / STEPS;
		Integer T0_STEPS = 100;
		CObjectCollection<GateAssignment> gas = new CObjectCollection<GateAssignment>();
		for (int i = 0; i < STEPS + T0_STEPS; ++i) {
			Double log_temperature = LOGMAX - i * LOGINC;
			Double temperature = Math.pow(10, log_temperature);
			if (i >= STEPS) {
				temperature = 0.0;
		    }
		    gas.clear();
            // get GateAssignment A
            GateAssignment gaA = this.getFirstAssignment();
            // get GateAssignment B
            GateAssignment gaB = this.getSecondAssignment(gaA); //TMNode nodeA = netlist.getVertexAtIdx(this.getRNG().nextInt(netlist.getNumVertex()));
            // do Assignments
            gas.add(gaA);
            gas.add(gaB);
            this.doAssignments(gas);
            // evaluate
            // compare
            // undo?
        }*/
	}
	
	@Override
	protected void setDefaultParameterValues() {
		
	}

	@Override
	protected void setParameterValues() {
		
	}

	@Override
	protected void validateParameterValues() {
		
	}

	@Override
	protected void preprocessing() {
		this.setRNG(new Random());
		this.setGroupList(new ArrayList<String>());
		// group to Gates mapping
		Map<String, List <Gate> > groupGates = new HashMap<String, List <Gate>>();
		String groupName = null;
		for (int i = 0; i < this.getGates().size(); i ++) {
			Gate g = this.getGates().get(i);
			groupName = g.getGroup();
			List <Gate> gates = groupGates.get(groupName);
			if (gates == null) {
				gates = new ArrayList<Gate>();
				groupGates.put(groupName, gates);
			}
			gates.add(g);
		}
		this.setGroupGates(groupGates);
		// unassigned
		List<String> groupList = this.getGroupList();
	    Iterator<Entry<String, List<Gate>>> it = groupGates.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String, List<Gate>> pair = it.next();
	        groupList.add(pair.getKey());
	    }
	}

	@Override
	protected void run() {
		this.getGroupGates().clear();
		this.getGroupList().clear();
		// TODO: numPasses
		int NumPasses = 100;
		for (int i = 0; i < NumPasses; i++) {
			// initial random
			this.initialAssignment();
			// TODO
			/*
			 * 
	            Evaluate.evaluateCircuit(lc, get_gate_library(), get_options());
	            for (Gate g : lc.get_logic_gates()) {
	                Evaluate.evaluateGate(g, get_options());
	            }
	            Toxicity.evaluateCircuitToxicity(lc, get_gate_library());

			 */
			this.doIterations();			
		}
	}

	@Override
	protected void postprocessing() {
		
	}
	
	/*
	 * groupGates
	 */
	protected Map<String, List <Gate> > getGroupGates(){
		return this.groupGates;
	}
	protected void setGroupGates(final Map<String, List <Gate> > groupGates){
		this.groupGates = groupGates;
	}
	
	private Map<String, List <Gate> > groupGates;
	
	/*
	 * groupList (Unassigned)
	 */
	protected List<String> getGroupList(){
		return this.groupList;
	}
	protected void setGroupList(final List<String> groupList){
		this.groupList = groupList;
	}
	
	private List <String> groupList;
	
	/*
	 * rng
	 */
	protected Random getRNG(){
		return this.rng;
	}
	protected void setRNG(final Random rng){
		this.rng = rng;
	}
	
	private Random rng;
}
