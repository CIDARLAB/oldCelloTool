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
package logicSynthesis.runtime;

import common.netlist.Netlist;
import common.netlist.NetlistUtils;
import common.runtime.environment.RuntimeEnv;
import common.stage.StageConfiguration;
import common.stage.StageUtils;
import common.target.data.TargetData;
import common.target.data.TargetDataUtils;
import logicSynthesis.runtime.environment.LSArgString;
import logicSynthesis.runtime.environment.LSRuntimeEnv;

/**
 * @author: Vincent Mirian
 * 
 * @date: Nov 20, 2017
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RuntimeEnv runEnv = new LSRuntimeEnv(args);
		runEnv.setName("LogicSynthesis");
		// Read Netlist
		Netlist netlist = new Netlist(); //NetlistUtils.getNetlist(runEnv, LSArgString.INPUTNETLIST);
		// get StageConfiguration
		StageConfiguration sc = StageUtils.getStageConfiguration(runEnv, LSArgString.CONFIGFILE);
		// get TargetData
		TargetData td = TargetDataUtils.getTargetTargetData(runEnv, LSArgString.TARGETDATAFILE, LSArgString.TARGETDATADIR);
		// Execute
		LSRuntimeObject LS = new LSRuntimeObject(sc, td, netlist, runEnv);
		LS.execute();
		// Write Netlist
		String outputFilename = runEnv.getOptionValue(LSArgString.OUTPUTNETLIST);
		NetlistUtils.writeJSONForNetlist(netlist, outputFilename);
	}
	
}
