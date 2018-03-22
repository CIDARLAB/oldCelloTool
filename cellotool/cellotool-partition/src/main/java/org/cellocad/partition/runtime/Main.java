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

import org.cellocad.common.CObject;
import org.cellocad.common.netlist.Netlist;
import org.cellocad.common.netlist.NetlistUtils;
import org.cellocad.common.runtime.environment.RuntimeEnv;
import org.cellocad.common.stage.StageConfiguration;
import org.cellocad.common.stage.StageUtils;
import org.cellocad.common.target.data.TargetData;
import org.cellocad.common.target.data.TargetDataUtils;
import org.cellocad.partition.runtime.environment.PTArgString;
import org.cellocad.partition.runtime.environment.PTRuntimeEnv;

/**
 * @author: Vincent Mirian
 *
 * @date: Nov 17, 2017
 *
 */
public class Main extends CObject{

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RuntimeEnv runEnv = new PTRuntimeEnv(args);
		runEnv.setName("Partitioner");
		// Read Netlist
		Netlist netlist = NetlistUtils.getNetlist(runEnv, PTArgString.INPUTNETLIST);
		// get StageConfiguration
		StageConfiguration sc = StageUtils.getStageConfiguration(runEnv, PTArgString.CONFIGFILE);
		// get TargetData
		TargetData td = TargetDataUtils.getTargetTargetData(runEnv, PTArgString.TARGETDATAFILE, PTArgString.TARGETDATADIR);
		// Execute
		PTRuntimeObject PT = new PTRuntimeObject(sc, td, netlist, runEnv);
		PT.setName("Partitioner");
		PT.execute();
		// Write Netlist
		String outputFilename = runEnv.getOptionValue(PTArgString.OUTPUTNETLIST);
		NetlistUtils.writeJSONForNetlist(netlist, outputFilename);
	}
}
