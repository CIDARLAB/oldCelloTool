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

import common.netlist.Netlist;
import common.stage.Stage;
import common.target.TargetConfiguration;
import common.target.TargetUtils;
import common.target.data.TargetData;
import common.target.data.TargetDataUtils;
import common.target.runtime.environment.TargetArgString;
import common.target.runtime.environment.TargetRuntimeEnv;
import logicSynthesis.runtime.LSRuntimeObject;
import technologyMapping.runtime.TMRuntimeObject;

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
		// TechnologyMapping
	    currentStage = targetCfg.getStageByName("TechnologyMapping");
		TMRuntimeObject TM = new TMRuntimeObject(currentStage.getStageConfiguration(), td, netlist, runEnv);
		TM.execute();
		// Eugene
	    currentStage = targetCfg.getStageByName("Eugene");
		//Eugene EU = new Eugene(netlist, runEnv);
		//EU.execute();
	}

}
