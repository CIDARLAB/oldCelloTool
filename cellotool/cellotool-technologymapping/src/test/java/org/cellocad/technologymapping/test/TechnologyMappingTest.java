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
package org.cellocad.technologymapping.test;

import java.io.File;

import org.cellocad.common.Utils;
import org.cellocad.common.graph.AbstractVertex.VertexType;
import org.cellocad.common.netlist.Netlist;
import org.cellocad.common.netlist.NetlistEdge;
import org.cellocad.common.netlist.NetlistNode;
import org.cellocad.common.netlist.NetlistUtils;
import org.cellocad.common.stage.Stage;
import org.cellocad.common.target.TargetConfiguration;
import org.cellocad.common.target.TargetUtils;
import org.cellocad.common.target.data.TargetData;
import org.cellocad.common.target.data.TargetDataUtils;
import org.cellocad.common.target.runtime.environment.TargetArgString;
import org.cellocad.common.target.runtime.environment.TargetRuntimeEnv;
import org.cellocad.technologymapping.runtime.TMRuntimeObject;
import org.cellocad.technologymapping.test.common.TestUtils;
import org.junit.Test;

/**
 * @author: Timothy Jones
 * 
 * @date: Mar 6, 2018
 *
 */
public class TechnologyMappingTest{

	@Test
	public void test() {
		String resourcesFilepath = TestUtils.getResourcesFilepath()	+ Utils.getFileSeparator();

		String tempDir = TestUtils.createTempDirectory().toString();

		String[] args = new String[] {"-verilogFile","foo.v",
									  "-targetDataDir",resourcesFilepath,
									  "-targetDataFile","Eco1C1G1T0-synbiohub.UCF.json",
									  "-configDir",resourcesFilepath,
									  "-configFile","config.json",
									  "-outputDir",tempDir};

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
		// TechnologyMapping
		currentStage = targetCfg.getStageByName("TechnologyMapping");
		TMRuntimeObject TM = new TMRuntimeObject(currentStage.getStageConfiguration(), td, netlist, runEnv);
		TM.execute();
		NetlistUtils.writeJSONForNetlist(netlist, runEnv.getOptionValue("outputDir")
										 + Utils.getFileSeparator()
										 + "technologymapping_netlist.json");
		Utils.deleteDirectory(new File(tempDir));
	}

	public Netlist generateTestNetlist() {
		Netlist netlist = new Netlist();

		netlist.setName("technologyMapping");

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
