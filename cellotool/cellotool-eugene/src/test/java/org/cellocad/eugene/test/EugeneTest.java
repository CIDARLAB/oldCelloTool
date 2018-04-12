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
package org.cellocad.eugene.test;

import java.io.File;

import org.cellocad.common.Utils;
import org.cellocad.common.netlist.Netlist;
import org.cellocad.common.netlist.NetlistUtils;
import org.cellocad.common.runtime.environment.RuntimeEnv;
import org.cellocad.common.stage.StageConfiguration;
import org.cellocad.common.stage.StageUtils;
import org.cellocad.common.target.data.TargetData;
import org.cellocad.common.target.data.TargetDataUtils;
import org.cellocad.eugene.runtime.EURuntimeObject;
import org.cellocad.eugene.runtime.environment.EUArgString;
import org.cellocad.eugene.runtime.environment.EURuntimeEnv;
import org.cellocad.eugene.test.common.TestUtils;
import org.junit.Test;

/**
 * @author: Timothy Jones
 *
 * @date: Feb 28, 2018
 *
 */
public class EugeneTest{

	@Test
	public void test() {
		String resourcesFilepath = TestUtils.getResourcesFilepath() + Utils.getFileSeparator();

		String tempDir = TestUtils.createTempDirectory().toString();

		String[] args = new String[] {
			"-inputNetlist",resourcesFilepath + Utils.getFileSeparator() + "technologymapping_netlist.json",
			"-outputNetlist",tempDir + Utils.getFileSeparator() + "eugene_netlist.json",
			"-targetDataDir",resourcesFilepath,
			"-targetDataFile","Eco1C1G1T0-synbiohub.UCF.json",
			"-configFile",resourcesFilepath + Utils.getFileSeparator() + "eugene.json",
			"-outputDir",tempDir
		};

		RuntimeEnv runEnv = new EURuntimeEnv(args);
		runEnv.setName("Eugene");
		// Read Netlist
		Netlist netlist = NetlistUtils.getNetlist(runEnv, EUArgString.INPUTNETLIST);
		// get StageConfiguration
		StageConfiguration sc = StageUtils.getStageConfiguration(runEnv, EUArgString.CONFIGFILE);
		// get TargetData
		TargetData td = TargetDataUtils.getTargetTargetData(runEnv, EUArgString.TARGETDATAFILE, EUArgString.TARGETDATADIR);
		// Execute
		EURuntimeObject EU = new EURuntimeObject(sc, td, netlist, runEnv);
		EU.setName("Eugene");
		EU.execute();
		// Write Netlist
		NetlistUtils.writeJSONForNetlist(netlist, runEnv.getOptionValue("outputDir")
				+ Utils.getFileSeparator()
				+ "eugene_netlist.json");
		Utils.deleteDirectory(new File(tempDir));
	}

}
