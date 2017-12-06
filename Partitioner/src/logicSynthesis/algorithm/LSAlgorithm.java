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
package logicSynthesis.algorithm;

import common.Utils;
import common.algorithm.Algorithm;
import common.netlist.Netlist;
import common.profile.AlgorithmProfile;
import common.runtime.environment.RuntimeEnv;
import common.target.data.TargetData;

/**
 * @author: Vincent Mirian
 * 
 * @date: Nov 21, 2017
 *
 */
public abstract class LSAlgorithm extends Algorithm{

	public void execute(
			final String verilogFile,
			final Netlist netlist,
			final TargetData targetData,
			final AlgorithmProfile AProfile,
			final RuntimeEnv runtimeEnv
			){
		Utils.isNullRuntimeException(verilogFile, "verilogFile");
		Utils.isNullRuntimeException(netlist, "netlist");
		Utils.isNullRuntimeException(targetData, "targetData");
		Utils.isNullRuntimeException(AProfile, "AProfile");
		Utils.isNullRuntimeException(runtimeEnv, "runtimeEnv");
		// init
		this.setVerilogFile(verilogFile);
		this.setNetlist(netlist);
		this.setTargetData(targetData);
		this.setAlgorithmProfile(AProfile);
		this.setRuntimeEnv(runtimeEnv);
		// execute
		this.setDefaultParameterValues();
		this.setParameterValues();
		this.validateParameterValues();
		this.preprocessing();
		this.run();
		this.postprocessing();
	}
	
	/*
	 * Getter and Setter
	 */
	private void setVerilogFile (final String verilogFile) {
		this.verilogFile = verilogFile;
	}
	protected String getVerilogFile() {
		return this.verilogFile;
	}

	private void setNetlist (final Netlist netlist) {
		this.netlist = netlist;
	}
	protected Netlist getNetlist() {
		return this.netlist;
	}

	private void setTargetData (final TargetData targetData) {
		this.targetData = targetData;
	}
	protected TargetData getTargetData() {
		return this.targetData;
	}

	private void setAlgorithmProfile (final AlgorithmProfile AProfile) {
		this.AProfile = AProfile;
	}
	protected AlgorithmProfile getAlgorithmProfile() {
		return this.AProfile;
	}

	private void setRuntimeEnv (final RuntimeEnv runtimeEnv) {
		this.runtimeEnv = runtimeEnv;
	}
	protected RuntimeEnv getRuntimeEnv() {
		return this.runtimeEnv;
	}

	private String verilogFile;
	private Netlist netlist;
	private TargetData targetData;
	private AlgorithmProfile AProfile;
	private RuntimeEnv runtimeEnv;
}
