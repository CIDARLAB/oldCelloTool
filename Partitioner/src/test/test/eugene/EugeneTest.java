/**
 * Copyright (C) 2017 Boston University (BU)
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
package test.eugene;

import org.junit.Test;

import common.CObject;
import common.CObjectCollection;
import common.graph.AbstractVertex.VertexType;
import common.netlist.Netlist;
import common.netlist.NetlistEdge;
import common.netlist.NetlistNode;
import common.netlist.NetlistUtils;
import common.stage.Stage;
import common.target.TargetConfiguration;
import common.target.TargetUtils;
import common.target.data.TargetData;
import common.target.data.TargetDataUtils;
import common.target.runtime.environment.TargetArgString;
import common.target.runtime.environment.TargetRuntimeEnv;

import eugene.runtime.EURuntimeObject;
import eugene.data.PartType;

import test.common.TestUtils;

/**
 * @author: Timothy Jones
 * 
 * @date: Feb 28, 2018
 *
 */
public class EugeneTest{

	@Test
	public void test() {
		String resourcesFilepath = TestUtils.getResourcesFilepath() + "/eugene/";

		String[] args = new String[] {"-verilogFile","foo.v",
									  "-targetDataDir",resourcesFilepath,
									  "-targetDataFile","Eco1C1G1T0-synbiohub.UCF.json",
									  "-configDir",resourcesFilepath,
									  "-configFile","config.json"};
		
		Stage currentStage = null;
		// RuntimeEnv
	    TargetRuntimeEnv runEnv = new TargetRuntimeEnv(args);
		runEnv.setName("dnaCompiler");
		// Netlist
		Netlist netlist = generateTestNetlist();
		// TargetConfiguration
	    TargetConfiguration targetCfg = TargetUtils.getTargetConfiguration(runEnv, TargetArgString.TARGETCONFIGFILE, TargetArgString.TARGETCONFIGDIR);
		// get TargetData
		TargetData td = TargetDataUtils.getTargetTargetData(runEnv, TargetArgString.TARGETDATAFILE, TargetArgString.TARGETDATADIR);
		// Stages
		// Eugene
	    currentStage = targetCfg.getStageByName("Eugene");
		EURuntimeObject EU = new EURuntimeObject(currentStage.getStageConfiguration(), td, netlist, runEnv);
		EU.execute();
		NetlistUtils.writeJSONForNetlist(netlist, "eugenetest.json");
	}

	public Netlist generateTestNetlist() {
		Netlist netlist = new Netlist();
		
		netlist.setName("eugene");
		
		NetlistNode in1 = new NetlistNode();
		NetlistNode in2 = new NetlistNode();
		NetlistNode A = new NetlistNode();
		NetlistNode B = new NetlistNode();
		NetlistNode C = new NetlistNode();
		NetlistNode out = new NetlistNode();
		
		NetlistEdge e1 = new NetlistEdge(in1, A);
		NetlistEdge e2 = new NetlistEdge(in2, B);
		NetlistEdge e3 = new NetlistEdge(A, C);
		NetlistEdge e4 = new NetlistEdge(B, C);
		NetlistEdge e5 = new NetlistEdge(C, out);

		e1.setName("e1");
		e2.setName("e2");
		e3.setName("e3");
		e4.setName("e4");
		e5.setName("e5");
		
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

		in1.setNodeType("TopInput");
		in2.setNodeType("TopInput");
		A.setNodeType("NOT");
		B.setNodeType("NOT");
		C.setNodeType("NOR");
		out.setNodeType("TopOutput");
		
		in1.setName("in1");
		in2.setName("in2");
		A.setName("A");
		B.setName("B");
		C.setName("C");
		out.setName("out");

		in1.setGate("pTac");
		in2.setGate("pTet");
		A.setGate("A1_AmtR");
		B.setGate("S2_SrpR");
		C.setGate("P3_PhlF");
		out.setGate("YFP");

		CObject pTac = new CObject("pTac",PartType.PROMOTER.ordinal(),0);
		CObject pTet = new CObject("pTet",PartType.PROMOTER.ordinal(),1);
		CObject BydvJ = new CObject("BydvJ",PartType.RIBOZYME.ordinal(),2);
		CObject A1 = new CObject("A1",PartType.RBS.ordinal(),3);
		CObject AmtR = new CObject("AmtR",PartType.CDS.ordinal(),4);
		CObject L3S2P55 = new CObject("L3S2P55",PartType.TERMINATOR.ordinal(),5);
		CObject RiboJ10 = new CObject("RiboJ10",PartType.RIBOZYME.ordinal(),6);
		CObject S2 = new CObject("S2",PartType.RBS.ordinal(),7);
		CObject SrpR = new CObject("SrpR",PartType.CDS.ordinal(),8);
		CObject ECK120029600 = new CObject("ECK120029600",PartType.TERMINATOR.ordinal(),9);
		CObject RiboJ53 = new CObject("RiboJ53",PartType.RIBOZYME.ordinal(),10);
		CObject P3 = new CObject("P3",PartType.RBS.ordinal(),11);
		CObject PhlF = new CObject("PhlF",PartType.CDS.ordinal(),12);
		CObject ECK120033737 = new CObject("ECK120033737",PartType.TERMINATOR.ordinal(),13);
		CObject pPhlF = new CObject("pPhlF",PartType.PROMOTER.ordinal(),14);
		CObject pSrpR = new CObject("pSrpR",PartType.PROMOTER.ordinal(),15);
		CObject pAmtR = new CObject("pAmtR",PartType.PROMOTER.ordinal(),16);
		
		CObjectCollection<CObject> parts = null;
		parts = new CObjectCollection<CObject>();
		parts.add(pTac);
		in1.setParts(parts);
		parts = new CObjectCollection<CObject>();
		parts.add(pTet);
		in2.setParts(parts);		
		parts = new CObjectCollection<CObject>();
		parts.add(BydvJ);
		parts.add(A1);
		parts.add(AmtR);
		parts.add(L3S2P55);
		parts.add(pAmtR);
		A.setParts(parts);
		parts = new CObjectCollection<CObject>();
		parts.add(RiboJ10);
		parts.add(S2);
		parts.add(SrpR);
		parts.add(ECK120029600);
		parts.add(pSrpR);
		B.setParts(parts);
		parts = new CObjectCollection<CObject>();
		parts.add(RiboJ53);
		parts.add(P3);
		parts.add(PhlF);
		parts.add(ECK120033737);
		parts.add(pPhlF);
		C.setParts(parts);		

		in1.setVertexType(VertexType.SOURCE);
		in2.setVertexType(VertexType.SOURCE);
		out.setVertexType(VertexType.SINK);

		netlist.addVertex(in1);
		netlist.addVertex(in2);
		netlist.addVertex(A);
		netlist.addVertex(B);
		netlist.addVertex(C);
		netlist.addVertex(out);
		
		netlist.addEdge(e1);
		netlist.addEdge(e2);
		netlist.addEdge(e3);
		netlist.addEdge(e4);
		netlist.addEdge(e5);

		return netlist;
	}

}
