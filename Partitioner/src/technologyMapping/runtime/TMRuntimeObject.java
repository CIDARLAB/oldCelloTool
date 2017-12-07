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
package technologyMapping.runtime;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONObject;

import common.CObjectCollection;
import common.netlist.Netlist;
import common.netlist.NetlistEdge;
import common.netlist.NetlistNode;
import common.profile.AlgorithmProfile;
import common.runtime.RuntimeObject;
import common.runtime.environment.RuntimeEnv;
import common.stage.StageConfiguration;
import common.target.data.TargetData;
import technologyMapping.algorithm.TMAlgorithm;
import technologyMapping.algorithm.TMAlgorithmFactory;
import technologyMapping.data.Gate;
import technologyMapping.data.ResponseFunction;
import technologyMapping.netlist.TMEdge;
import technologyMapping.netlist.TMNetlist;
import technologyMapping.netlist.TMNode;

/**
 * @author: Vincent Mirian
 * 
 * @date: Dec 4, 2017
 *
 */
public class TMRuntimeObject extends RuntimeObject{

	public TMRuntimeObject(
			final StageConfiguration stageConfiguration,
			final TargetData targetData,
			final Netlist netlist,
			final RuntimeEnv runEnv
			) {
		super(stageConfiguration, targetData, netlist, runEnv);
	}
	
	protected CObjectCollection<Gate> getGates(){
		// get list of Gates
		CObjectCollection<Gate> rtn = new CObjectCollection<Gate>();
		for (int i = 0; i < this.getTargetData().getNumJSONObject("gates"); i++) {
			JSONObject jObj = this.getTargetData().getJSONObjectAtIdx("gates", i);
			Gate g = new Gate(jObj);
			rtn.add(g);
		}
		// get list of Response Function
		CObjectCollection<ResponseFunction> rf = new CObjectCollection<ResponseFunction>();
		for (int i = 0; i < this.getTargetData().getNumJSONObject("response_functions"); i++) {
			JSONObject jObj = this.getTargetData().getJSONObjectAtIdx("response_functions", i);
			ResponseFunction r = new ResponseFunction(jObj);
			rf.add(r);
		}
		// associate response function to gate
		for (int i = 0; i < rf.size(); i++) {
			ResponseFunction r = rf.get(i);
			Gate g = rtn.findCObjectByName(r.getGateName());
			if (g == null) {
				this.logWarn(r.getGateName() + " does not exists!");
			}
			else {
				g.setResponseFunction(r);
			}
		}
		return rtn;
	}

	private TMNetlist convertNetlistToTMNetlist(
			final Map<TMNode, NetlistNode> TMNetlistToNetlistNode,
			final Map<TMEdge, NetlistEdge> TMNetlistToNetlistEdge
			) {
		TMNetlist rtn = new TMNetlist();
		Map<NetlistNode, TMNode> NetlistToTMNetlistVertex = new HashMap<NetlistNode, TMNode>();
		Map<NetlistEdge, TMEdge> NetlistToTMNetlistEdge = new HashMap<NetlistEdge, TMEdge>();
		// convert Vertex to PNode
		for (int i = 0; i < this.getNetlist().getNumVertex(); i++){
			NetlistNode node = this.getNetlist().getVertexAtIdx(i);
			TMNode tmNode = new TMNode();
			tmNode.setName(node.getName());
			tmNode.setType(node.getType());
			NetlistToTMNetlistVertex.put(node, tmNode);
			TMNetlistToNetlistNode.put(tmNode, node);
			rtn.addVertex(tmNode);
		}
		// convert Edge to PEdge
		for (int i = 0; i < this.getNetlist().getNumEdge(); i++){
			NetlistEdge edge = this.getNetlist().getEdgeAtIdx(i);
			TMEdge tmEdge = new TMEdge();
			tmEdge.setName(edge.getName());
			tmEdge.setType(edge.getType());
			NetlistToTMNetlistEdge.put(edge, tmEdge);
			TMNetlistToNetlistEdge.put(tmEdge, edge);
			rtn.addEdge(tmEdge);	
		}
		// for each PNode:
		for (int i = 0; i < this.getNetlist().getNumVertex(); i++){
			NetlistNode node = this.getNetlist().getVertexAtIdx(i);
			TMNode tmNode = NetlistToTMNetlistVertex.get(node);
			assert (tmNode != null);
			// set outEdge for PNode
			for (int j = 0; j < node.getNumOutEdge(); j ++){
				NetlistEdge edge = node.getOutEdgeAtIdx(j);
				TMEdge tmEdge = NetlistToTMNetlistEdge.get(edge);
				assert (tmEdge != null);
				tmNode.addOutEdge(tmEdge);
			}
			// set inEdge for PNode
			for (int j = 0; j < node.getNumInEdge(); j ++){
				NetlistEdge edge = node.getInEdgeAtIdx(j);
				TMEdge tmEdge = NetlistToTMNetlistEdge.get(edge);
				assert (tmEdge != null);
				tmNode.addInEdge(tmEdge);				
			}			
		}
		// for each PEdge:
		for (int i = 0; i < this.getNetlist().getNumEdge(); i++){
			NetlistEdge edge = this.getNetlist().getEdgeAtIdx(i);
			TMEdge tmEdge = NetlistToTMNetlistEdge.get(edge);
			assert (tmEdge != null);
			// set src for PEdge
			{
				NetlistNode node = edge.getSrc();
				TMNode tmNode = NetlistToTMNetlistVertex.get(node);
				assert (tmNode != null);
				tmEdge.setSrc(tmNode);
			}
			// set dst for PEdge
			{
				NetlistNode node = edge.getDst();
				TMNode tmNode = NetlistToTMNetlistVertex.get(node);
				assert (tmNode != null);
				tmEdge.setDst(tmNode);
			}
		}
		// set name
		rtn.setName(this.getNetlist().getName()+"_PGraph");
		assert(rtn.isValid());
		return rtn;
	}

	// attach partitionIDToLogicCircuit
	private void attachGateToNetlist(
			final TMNetlist G,
			final Map<TMNode, NetlistNode> TMNetlistToNetlistNode,
			final Map<TMEdge, NetlistEdge> TMNetlistToNetlistEdge
			){
		TMNode tmNode = null;
		NetlistNode node = null;
		Gate g = null;
		Iterator<Map.Entry<TMNode, NetlistNode>> it = TMNetlistToNetlistNode.entrySet().iterator();
		while (it.hasNext()) {
		    Map.Entry<TMNode, NetlistNode> pair = it.next();
		    tmNode = pair.getKey();
		    node = pair.getValue();
		    g = tmNode.getGate();
		    if (g == null) {
		    	this.logWarn(node.getName() + " has no gate asssigned!");
		    }
		    else {
			    node.setGate(g.getName());
		    }
		}
	}
	
	@Override
	protected void run() {
		// map for conversion
		Map<TMNode, NetlistNode> TMNetlistToNetlistNode = new HashMap<TMNode, NetlistNode>();
		Map<TMEdge, NetlistEdge> TMNetlistToNetlistEdge = new HashMap<TMEdge, NetlistEdge>();
		// AlgorithmProfile
		AlgorithmProfile AProfile = this.getStageConfiguration().getAlgorithmProfile();
		// convert from NetlistToGraph
		TMNetlist G = this.convertNetlistToTMNetlist(TMNetlistToNetlistNode, TMNetlistToNetlistEdge);
		// get list of Gates
		CObjectCollection<Gate> gates = this.getGates();
		// run Algorithm
		TMAlgorithmFactory TMAF = new TMAlgorithmFactory();
		TMAlgorithm algo = TMAF.getAlgorithm(AProfile);
		if (algo == null){
	    	throw new RuntimeException("Algorithm not found!");
		}
		algo.execute(gates, G, this.getTargetData(), AProfile, this.getRuntimeEnv());
		// attachPartitionIDToLogicCircuit
		this.attachGateToNetlist(G, TMNetlistToNetlistNode, TMNetlistToNetlistEdge);
	}

}
