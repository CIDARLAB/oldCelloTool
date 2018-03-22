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
package org.cellocad.logicsynthesis.runtime;

import org.cellocad.common.netlist.Netlist;
import org.cellocad.common.profile.AlgorithmProfile;
import org.cellocad.common.runtime.RuntimeObject;
import org.cellocad.common.runtime.environment.RuntimeEnv;
import org.cellocad.common.stage.StageConfiguration;
import org.cellocad.common.target.data.TargetData;
import org.cellocad.logicsynthesis.algorithm.LSAlgorithm;
import org.cellocad.logicsynthesis.algorithm.LSAlgorithmFactory;

/**
 * @author: Vincent Mirian
 *
 * @date: Nov 21, 2017
 *
 */
public class LSRuntimeObject extends RuntimeObject{

	public LSRuntimeObject(
			final StageConfiguration stageConfiguration,
			final TargetData targetData,
			final Netlist netlist,
			final RuntimeEnv runEnv
			) {
		super(stageConfiguration, targetData, netlist, runEnv);
	}

	public LSRuntimeObject(
			final String verilogFile,
			final StageConfiguration stageConfiguration,
			final TargetData targetData,
			final Netlist netlist,
			final RuntimeEnv runEnv
			) {
		super(stageConfiguration, targetData, netlist, runEnv);
		this.setVerilogFile(verilogFile);
	}

	/*
	 * getter and setter
	 */
	public void setVerilogFile (final String verilogFile) {
		this.verilogFile = verilogFile;
	}
	private String getVerilogFile() {
		return this.verilogFile;
	}

	@Override
	protected void run() {
		// get verilog file
		String verilogFile = this.getVerilogFile();
		if (verilogFile == null){
			throw new RuntimeException("VerilogFile is missing!");
		}
		// AlgorithmProfile
		AlgorithmProfile AProfile = this.getStageConfiguration().getAlgorithmProfile();
		// run Algorithm
		LSAlgorithmFactory LSAF = new LSAlgorithmFactory();
		LSAlgorithm algo = LSAF.getAlgorithm(AProfile);
		if (algo == null){
			throw new RuntimeException("Algorithm not found!");
		}
		algo.execute(verilogFile, this.getNetlist(), this.getTargetData(), AProfile, this.getRuntimeEnv());
	}

	private String verilogFile;
}
