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
package test.verilog;

import org.junit.Test;

import common.netlist.Netlist;
import common.netlist.NetlistUtils;
import common.stage.Stage;
import common.target.TargetConfiguration;
import common.target.TargetUtils;
import common.target.data.TargetData;
import common.target.data.TargetDataUtils;
import common.target.runtime.environment.TargetArgString;
import common.target.runtime.environment.TargetRuntimeEnv;

import logicSynthesis.runtime.LSRuntimeObject;

import test.common.TestUtils;

/**
 * @author: Tim Jones
 * 
 * @date: Feb 23, 2018
 *
 */
public class VerilogTest{

	@Test
	public void test() {
		runLogicSynthesis("and_structural");
		runLogicSynthesis("and_behavioral");
		runLogicSynthesis("and_assign");
		runLogicSynthesis("counter");
	}

	private void runLogicSynthesis(String verilogFilePrefix) {
		String resourcesFilepath = TestUtils.getResourcesFilepath() + "/verilog/";

		String[] args = new String[] {"-verilogFile",resourcesFilepath + verilogFilePrefix + ".v",
									  "-targetDataDir",resourcesFilepath,
									  "-targetDataFile","Eco1C1G1T1.UCF.json",
									  "-configDir",resourcesFilepath,
									  "-configFile","config.json"};
										  
		
		Stage currentStage = null;
		// RuntimeEnv
	    TargetRuntimeEnv runEnv = new TargetRuntimeEnv(args);
		runEnv.setName("dnaCompiler");
		// VerilogFile
		String verilogFile = runEnv.getOptionValue(TargetArgString.VERILOG);
		// Netlist
		Netlist netlist = new Netlist();
		// TargetConfiguration
	    TargetConfiguration targetCfg = TargetUtils.getTargetConfiguration(runEnv, TargetArgString.TARGETCONFIGFILE, TargetArgString.TARGETCONFIGDIR);
		// get TargetData
		TargetData td = TargetDataUtils.getTargetTargetData(runEnv, TargetArgString.TARGETDATAFILE, TargetArgString.TARGETDATADIR);
		// Stages
		// LogicSynthesis
	    currentStage = targetCfg.getStageByName("LogicSynthesis");
		LSRuntimeObject LS = new LSRuntimeObject(verilogFile, currentStage.getStageConfiguration(), td, netlist, runEnv);
		LS.execute();
		NetlistUtils.writeJSONForNetlist(netlist, verilogFilePrefix + ".json");
	}

}
