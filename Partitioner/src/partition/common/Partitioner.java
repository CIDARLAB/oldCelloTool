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
package partition.common;

import java.util.HashMap;
import java.util.Map;

import partition.algorithm.PAlgorithm;
import partition.algorithm.PAlgorithmFactory;
import partition.graph.PEdge;
import partition.graph.PGraph;
import partition.graph.PNode;
import partition.profile.AlgorithmProfile;
import partition.profile.PartitionerProfile;
import common.CObject;
import common.graph.graph.Edge;
import common.graph.graph.Graph;
import common.graph.graph.Vertex;
import common.runtime.environment.RuntimeEnv;

/**
 * @author: Vincent Mirian
 * 
 * @date: Oct 27, 2017
 *
 */
public class Partitioner extends CObject {

	// TODO: change graph to Logic Circuit Object received from Cello
	public Partitioner(final Graph graph, final PartitionerProfile PProfile, final RuntimeEnv runtimeEnv){
		this.setLogicCircuit(graph);
		this.setPProfile(PProfile);
		this.setRuntimeEnv(runtimeEnv);
		if ((graph == null) || (PProfile == null)){
	    	throw new RuntimeException("Logic Circuit or PartitionProfile not specified!");
		}
	}

	// TODO: attach partitionIDToLogicCircuit
	private void attachPartitionIDToLogicCircuit(final PGraph G, final Map<PNode, Vertex> PGraphToLCVertex, final Map<PEdge, Edge> PGraphToLCEdge){
		
	}
		
	private PGraph convertLogicCircuitToPGraph(final Map<PNode, Vertex> PGraphToLCVertex, final Map<PEdge, Edge> PGraphToLCEdge){
		PGraph rtn = new PGraph();
		Map<Vertex, PNode> LCToPGraphVertex = new HashMap<Vertex, PNode>();
		Map<Edge, PEdge> LCToPGraphEdge = new HashMap<Edge, PEdge>();
		// convert Vertex to PNode
		for (int i = 0; i < this.getLogicCircuit().getNumVertex(); i++){
			Vertex v = this.getLogicCircuit().getVertexAtIdx(i);
			PNode node = new PNode();
			node.setName(v.getName());
			node.setType(v.getType());
			LCToPGraphVertex.put(v, node);
			PGraphToLCVertex.put(node, v);
			rtn.addVertex(node);
		}
		// convert Edge to PEdge
		for (int i = 0; i < this.getLogicCircuit().getNumEdge(); i++){
			Edge e = this.getLogicCircuit().getEdgeAtIdx(i);
			PEdge edge = new PEdge();
			edge.setName(e.getName());
			edge.setType(e.getType());
			LCToPGraphEdge.put(e, edge);
			PGraphToLCEdge.put(edge, e);
			rtn.addEdge(edge);	
		}
		// for each PNode:
		for (int i = 0; i < this.getLogicCircuit().getNumVertex(); i++){
			Vertex v = this.getLogicCircuit().getVertexAtIdx(i);
			PNode node = LCToPGraphVertex.get(v);
			assert (node != null);
			// set outEdge for PNode
			for (int j = 0; j < v.getNumOutEdge(); j ++){
				Edge e = v.getOutEdgeAtIdx(j);
				PEdge edge = LCToPGraphEdge.get(e);
				assert (edge != null);
				node.addOutEdge(edge);				
			}
			// set inEdge for PNode
			for (int j = 0; j < v.getNumInEdge(); j ++){
				Edge e = v.getInEdgeAtIdx(j);
				PEdge edge = LCToPGraphEdge.get(e);
				assert (edge != null);
				node.addInEdge(edge);				
			}			
		}
		// for each PEdge:
		for (int i = 0; i < this.getLogicCircuit().getNumEdge(); i++){
			Edge e = this.getLogicCircuit().getEdgeAtIdx(i);
			PEdge edge = LCToPGraphEdge.get(e);
			assert (edge != null);
			// set src for PEdge
			{
				Vertex v = e.getSrc();
				PNode node = LCToPGraphVertex.get(v);
				assert (node != null);
				edge.setSrc(node);
			}
			// set dst for PEdge
			{
				Vertex v = e.getDst();
				PNode node = LCToPGraphVertex.get(v);
				assert (node != null);
				edge.setDst(node);
			}
		}
		// set name
		rtn.setName(this.getLogicCircuit().getName()+"_PGraph");
		assert(rtn.isValid());
		return rtn;
	}
	
	public void run(){
		// map for conversion
		Map<PNode, Vertex> PGraphToLCVertex = new HashMap<PNode, Vertex>();
		Map<PEdge, Edge> PGraphToLCEdge = new HashMap<PEdge, Edge>();
		// AlgorithmProfile
		AlgorithmProfile AProfile = this.getPProfile().getAProfile();
		// convert from LogicCircuitToGraph
		PGraph G = this.convertLogicCircuitToPGraph(PGraphToLCVertex, PGraphToLCEdge);
		// create Partition from PartitionProfile
		Partition P = new Partition(this.getPProfile().getPProfile());
		P.setName(G.getName());
		// run Algorithm
		PAlgorithmFactory PAF = new PAlgorithmFactory();
		PAlgorithm algo = PAF.getAlgorithm(AProfile);
		if (algo == null){
	    	throw new RuntimeException("Algorithm not found!");
		}
		algo.execute(G, P, AProfile, this.getRuntimeEnv());
		// write dot file for Partition
		PartitionUtils.writeDotFileForPartition(P, P.getName() + "_Partition");
		// attachPartitionIDToLogicCircuit
		this.attachPartitionIDToLogicCircuit(G, PGraphToLCVertex, PGraphToLCEdge);
	}

	private void setLogicCircuit(final Graph g) {
		this.graph = g;
	}

	private void setPProfile(final PartitionerProfile PP) {
		this.PProfile = PP;
	}

	private void setRuntimeEnv(final RuntimeEnv runtimeEnv) {
		this.runtimeEnv = runtimeEnv;
	}
	
	protected Graph getLogicCircuit(){
		return this.graph;
	}
	
	protected PartitionerProfile getPProfile(){
		return this.PProfile;
	}
	
	protected RuntimeEnv getRuntimeEnv(){
		return this.runtimeEnv;
	}
	
	/*
	 * HashCode
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((PProfile == null) ? 0 : PProfile.hashCode());
		result = prime * result + ((graph == null) ? 0 : graph.hashCode());
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
		Partitioner other = (Partitioner) obj;
		if (PProfile == null) {
			if (other.PProfile != null)
				return false;
		} else if (!PProfile.equals(other.PProfile))
			return false;
		if (graph == null) {
			if (other.graph != null)
				return false;
		} else if (!graph.equals(other.graph))
			return false;
		return true;
	}

	// TODO: change graph to Logic Circuit Object received from Cello
	private Graph graph;
	private PartitionerProfile PProfile;
	private RuntimeEnv runtimeEnv;
}
