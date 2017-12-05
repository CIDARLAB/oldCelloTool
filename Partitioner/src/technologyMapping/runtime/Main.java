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

import common.netlist.Netlist;
import common.netlist.NetlistUtils;
import common.runtime.environment.RuntimeEnv;
import common.stage.StageConfiguration;
import common.stage.StageUtils;
import common.target.data.TargetData;
import common.target.data.TargetDataUtils;
import technologyMapping.runtime.environment.TMArgString;
import technologyMapping.runtime.environment.TMRuntimeEnv;

/**
 * @author: Vincent Mirian
 * 
 * @date: Dec 4, 2017
 *
 */
public class Main {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RuntimeEnv runEnv = new TMRuntimeEnv(args);
		runEnv.setName("TechnologyMapping");
		// Read Netlist
		Netlist netlist = NetlistUtils.getNetlist(runEnv, TMArgString.INPUTNETLIST);
		// get StageConfiguration
		StageConfiguration sc = StageUtils.getStageConfiguration(runEnv, TMArgString.CONFIGFILE);
		// get TargetData
		TargetData td = TargetDataUtils.getTargetTargetData(runEnv, TMArgString.TARGETDATAFILE, TMArgString.TARGETDATADIR);
		// Execute
		TMRuntimeObject TM = new TMRuntimeObject(sc, td, netlist, runEnv);
		TM.setName("TechnologyMapping");
		TM.execute();
		// Write Netlist
		String outputFilename = runEnv.getOptionValue(TMArgString.OUTPUTNETLIST);
		NetlistUtils.writeJSONForNetlist(netlist, outputFilename);
	}
}
