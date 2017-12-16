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
package dnaCompiler.runtime;

import java.util.ArrayList;
import java.util.List;

import common.CObjectCollection;
import common.graph.AbstractVertex.VertexType;
import common.netlist.NetListData;
import common.netlist.Netlist;
import common.netlist.NetlistEdge;
import common.netlist.NetlistNode;
import common.target.TargetConfiguration;
import common.target.TargetUtils;
import common.target.data.TargetData;
import common.target.data.TargetDataUtils;
import common.target.runtime.environment.TargetArgString;
import common.target.runtime.environment.TargetRuntimeEnv;
import dnaCompiler.GateAssignment.Gate;
import dnaCompiler.GateAssignment.LogicNode;
import dnaCompiler.GateAssignment.ResponseFunction;
import dnaCompiler.GateAssignment.SimulateAnnealingNodeData;
import dnaCompiler.GateAssignment.SimulatedAnnealing;

/**
 * @author: Vincent Mirian
 * 
 * @date: Dec 7, 2017
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	    //Stage currentStage = null;
		// RuntimeEnv
	    TargetRuntimeEnv runEnv = new TargetRuntimeEnv(args);
		runEnv.setName("dnaCompiler");
		// VerilogFile
		//String verilogFile = runEnv.getOptionValue(TargetArgString.VERILOG);
		// Netlist
		//hardcoding NAND circuit composed of NOT/NORs as test case
		Netlist netlist = new Netlist();
		NetlistNode in1 = new NetlistNode();
		NetlistNode in2 = new NetlistNode();
		NetlistNode A = new NetlistNode();
		NetlistNode B = new NetlistNode();
		NetlistNode C = new NetlistNode();
		NetlistNode out = new NetlistNode();
		
//		LogicNode in1 = new LogicNode();
//		LogicNode in2 = new LogicNode();
//		LogicNode A = new LogicNode();
//		LogicNode B = new LogicNode();
//		LogicNode C = new LogicNode();
//		LogicNode out  = new LogicNode();
//		NetlistNode jai = new NetlistNode();
//		
		NetlistEdge e1 = new NetlistEdge(in1, A);
		NetlistEdge e2 = new NetlistEdge(in2, B);
		NetlistEdge e3 = new NetlistEdge(A, C);
		NetlistEdge e4 = new NetlistEdge(B, C);
		NetlistEdge e5 = new NetlistEdge(C, out);
		
		in1.addOutEdge(e1);
		in2.addOutEdge(e2);
		A.addInEdge(e1);
		A.addOutEdge(e3);
		B.addInEdge(e2);
		B.addOutEdge(e4);
		C.addInEdge(e3);
		C.addInEdge(e4);
		C.addOutEdge(e5);
		out.addInEdge(e5);
		
		in1.setNodeType("INPUT");
		in2.setNodeType("INPUT");
		A.setNodeType("NOT");
		B.setNodeType("NOT");
		C.setNodeType("NOR");
		out.setNodeType("OUTPUT");
		
		in1.setName("in1");
		in2.setName("in2");
		A.setName("A");
		B.setName("B");
		C.setName("C");
		out.setName("out");
		
		
		in1.setVertexType(VertexType.SOURCE);
		in2.setVertexType(VertexType.SOURCE);
		out.setVertexType(VertexType.SINK);
		
		in1.setNetListData(new NetListData());
		in2.setNetListData(new NetListData());
		A.setNetListData(new NetListData());
		B.setNetListData(new NetListData());
		C.setNetListData(new NetListData());
		//C.setNetListData(new SimulateAnnealingNodeData());
		out.setNetListData(new NetListData());		
		
		CObjectCollection<NetlistNode> netListNodes = new CObjectCollection<NetlistNode>();
		CObjectCollection<LogicNode> logicNodes = new CObjectCollection<LogicNode>();
		netListNodes.add(in1);
		netListNodes.add(in2);
		netListNodes.add(A);
		netListNodes.add(B);
		netListNodes.add(C);
		netListNodes.add(out);
		
//		logicNodes.add(in1);
//		logicNodes.add(in2);
//		logicNodes.add(A);
//		logicNodes.add(B);
//		logicNodes.add(C);
//		logicNodes.add(out);
		

		
		// TargetConfiguration
	    //TargetConfiguration targetCfg = TargetUtils.getTargetConfiguration(runEnv, TargetArgString.TARGETCONFIGFILE, TargetArgString.TARGETCONFIGDIR);
		// get TargetData
		TargetData td = TargetDataUtils.getTargetTargetData(runEnv, TargetArgString.TARGETDATAFILE, TargetArgString.TARGETDATADIR);
		
		runGateAssignment(netlist, td);
		SimulatedAnnealing algorithm = new SimulatedAnnealing(netListNodes, td);	
		algorithm.setDefaultParameterValues();
		algorithm.setParameterValues();
		algorithm.validateParameterValues();
		algorithm.preprocessing();
		algorithm.run();
		algorithm.postprocessing();
		
		
		/*
		// Stages
		// LogicSynthesis
	    currentStage = targetCfg.getStageByName("LogicSynthesis");
		LSRuntimeObject LS = new LSRuntimeObject(verilogFile, currentStage.getStageConfiguration(), td, netlist, runEnv);
		LS.execute();
		// Partition
	    currentStage = targetCfg.getStageByName("Partition");
		PTRuntimeObject PT = new PTRuntimeObject(currentStage.getStageConfiguration(), td, netlist, runEnv);
		PT.execute();
		// LogicOptomization
		// TechnologyMapping
	    currentStage = targetCfg.getStageByName("TechnologyMapping");
		TMRuntimeObject TM = new TMRuntimeObject(currentStage.getStageConfiguration(), td, netlist, runEnv);
		TM.execute();
		// Eugene
	    currentStage = targetCfg.getStageByName("Eugene");
		//Eugene EU = new Eugene(netlist, runEnv);
		//EU.execute();
		 * 
		 */
	}
	
	private static void runGateAssignment(Netlist netlist, TargetData td) {
		
//		//get and set all the gate objects
//		int numGateObjs = td.getNumJSONObject("gates");
//		int numResponseFuncs = td.getNumJSONObject("response functions");
//		CObjectCollection<Gate> gates = new CObjectCollection<Gate>();
//		List<ResponseFunction> responseFunctions = new ArrayList<ResponseFunction>();
//		
//		//TODO: number of gates and response funcs should be identical, build something more robust later
//		
//		for(int i=0; i<numGateObjs;++i) {
//			Gate gate = new Gate(td.getJSONObjectAtIdx("gates", i));
//			ResponseFunction responseFunc = new ResponseFunction(td.getJSONObjectAtIdx("response functions", i));
//			gates.add(gate);
//			responseFunctions.add(responseFunc);
//		}
//		
//		for(ResponseFunction rf:responseFunctions) {
//			String matching_gateName = rf.getGateName();
//			Gate matching_gate = gates.findCObjectByName(matching_gateName);
//			matching_gate.setResponseFunction(rf);
//		}
//		
		
		
		
	}
	
	

}
