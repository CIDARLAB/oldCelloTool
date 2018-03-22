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
package org.cellocad.partition.runtime;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.cellocad.common.netlist.Netlist;
import org.cellocad.common.netlist.NetlistEdge;
import org.cellocad.common.netlist.NetlistNode;
import org.cellocad.common.profile.AlgorithmProfile;
import org.cellocad.common.runtime.RuntimeObject;
import org.cellocad.common.runtime.environment.RuntimeEnv;
import org.cellocad.common.stage.StageConfiguration;
import org.cellocad.common.target.data.TargetData;
import org.cellocad.partition.algorithm.PTAlgorithm;
import org.cellocad.partition.algorithm.PTAlgorithmFactory;
import org.cellocad.partition.common.Block;
import org.cellocad.partition.common.Partition;
import org.cellocad.partition.common.PartitionUtils;
import org.cellocad.partition.graph.PEdge;
import org.cellocad.partition.graph.PGraph;
import org.cellocad.partition.graph.PNode;
import org.cellocad.partition.profile.PartitionProfile;
import org.json.simple.JSONObject;

/**
 * @author: Vincent Mirian
 *
 * @date: Dec 5, 2017
 *
 */
public class PTRuntimeObject extends RuntimeObject{

	public PTRuntimeObject(
			final StageConfiguration stageConfiguration,
			final TargetData targetData,
			final Netlist netlist,
			final RuntimeEnv runEnv
			) {
		super(stageConfiguration, targetData, netlist, runEnv);
	}

	private PGraph convertNetlistToPGraph(final Map<PNode, NetlistNode> PGraphToNetlistNode, final Map<PEdge, NetlistEdge> PGraphToNetlistEdge){
		PGraph rtn = new PGraph();
		Map<NetlistNode, PNode> NetlistToPGraphVertex = new HashMap<NetlistNode, PNode>();
		Map<NetlistEdge, PEdge> NetlistToPGraphEdge = new HashMap<NetlistEdge, PEdge>();
		// convert Vertex to PNode
		for (int i = 0; i < this.getNetlist().getNumVertex(); i++){
			NetlistNode node = this.getNetlist().getVertexAtIdx(i);
			PNode pnode = new PNode();
			pnode.setName(node.getName());
			pnode.setType(node.getType());
			NetlistToPGraphVertex.put(node, pnode);
			PGraphToNetlistNode.put(pnode, node);
			rtn.addVertex(pnode);
		}
		// convert Edge to PEdge
		for (int i = 0; i < this.getNetlist().getNumEdge(); i++){
			NetlistEdge edge = this.getNetlist().getEdgeAtIdx(i);
			PEdge pedge = new PEdge();
			pedge.setName(edge.getName());
			pedge.setType(edge.getType());
			NetlistToPGraphEdge.put(edge, pedge);
			PGraphToNetlistEdge.put(pedge, edge);
			rtn.addEdge(pedge);
		}
		// for each PNode:
		for (int i = 0; i < this.getNetlist().getNumVertex(); i++){
			NetlistNode node = this.getNetlist().getVertexAtIdx(i);
			PNode pnode = NetlistToPGraphVertex.get(node);
			assert (pnode != null);
			// set outEdge for PNode
			for (int j = 0; j < node.getNumOutEdge(); j ++){
				NetlistEdge edge = node.getOutEdgeAtIdx(j);
				PEdge pedge = NetlistToPGraphEdge.get(edge);
				assert (pedge != null);
				pnode.addOutEdge(pedge);
			}
			// set inEdge for PNode
			for (int j = 0; j < node.getNumInEdge(); j ++){
				NetlistEdge edge = node.getInEdgeAtIdx(j);
				PEdge pedge = NetlistToPGraphEdge.get(edge);
				assert (pedge != null);
				pnode.addInEdge(pedge);
			}
		}
		// for each PEdge:
		for (int i = 0; i < this.getNetlist().getNumEdge(); i++){
			NetlistEdge edge = this.getNetlist().getEdgeAtIdx(i);
			PEdge pedge = NetlistToPGraphEdge.get(edge);
			assert (pedge != null);
			// set src for PEdge
			{
				NetlistNode node = edge.getSrc();
				PNode pnode = NetlistToPGraphVertex.get(node);
				assert (pnode != null);
				pedge.setSrc(pnode);
			}
			// set dst for PEdge
			{
				NetlistNode node = edge.getDst();
				PNode pnode = NetlistToPGraphVertex.get(node);
				assert (pnode != null);
				pedge.setDst(pnode);
			}
		}
		// set name
		rtn.setName(this.getNetlist().getName()+"_PGraph");
		assert(rtn.isValid());
		return rtn;
	}

	// attach partitionIDToLogicCircuit
	private void attachPartitionIDToNetlist(
			final PGraph G,
			final Map<PNode, NetlistNode> PGraphToNetlistNode,
			final Map<PEdge, NetlistEdge> PGraphToNetlistEdge
			){
		PNode pnode = null;
		NetlistNode node = null;
		Block block = null;
		int pID = -1;
		Iterator<Map.Entry<PNode, NetlistNode>> it = PGraphToNetlistNode.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<PNode, NetlistNode> pair = it.next();
			pnode = pair.getKey();
			node = pair.getValue();
			block = pnode.getMyBlock();
			pID = -1;
			if (block != null) {
				pID = block.getIdx();
			}
			node.setPartitionID(pID);
		}
	}

	protected PartitionProfile getPartitionProfile(TargetData td) {
		PartitionProfile rtn = null;
		JSONObject JObj = this.getTargetData().getJSONObjectAtIdx("PartitionProfile", 0);
		if (JObj != null) {
			JSONObject PPObj = (JSONObject)JObj.get("PartitionProfile");
			rtn = new PartitionProfile(PPObj);
		}
		else {
			throw new RuntimeException("PartitionProfile does not exist!");
		}
		return rtn;
	}

	@Override
	protected void run() {
		// map for conversion
		Map<PNode, NetlistNode> PGraphToNetlistNode = new HashMap<PNode, NetlistNode>();
		Map<PEdge, NetlistEdge> PGraphToNetlistEdge = new HashMap<PEdge, NetlistEdge>();
		// AlgorithmProfile
		AlgorithmProfile AProfile = this.getStageConfiguration().getAlgorithmProfile();
		// convert from NetlistToGraph
		PGraph G = this.convertNetlistToPGraph(PGraphToNetlistNode, PGraphToNetlistEdge);
		// create Partition from PartitionProfile
		Partition P = new Partition(this.getPartitionProfile(this.getTargetData()));
		P.setName(G.getName());
		// run Algorithm
		PTAlgorithmFactory PTAF = new PTAlgorithmFactory();
		PTAlgorithm algo = PTAF.getAlgorithm(AProfile);
		if (algo == null){
			throw new RuntimeException("Algorithm not found!");
		}
		algo.execute(G, P, AProfile, this.getRuntimeEnv());
		// write dot file for Partition
		PartitionUtils.writeDotFileForPartition(P, P.getName() + "_Partition");
		// attachPartitionIDToLogicCircuit
		this.attachPartitionIDToNetlist(G, PGraphToNetlistNode, PGraphToNetlistEdge);
	}
}
