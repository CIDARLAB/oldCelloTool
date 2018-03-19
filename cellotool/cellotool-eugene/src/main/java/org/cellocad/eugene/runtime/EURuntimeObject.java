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
package org.cellocad.eugene.runtime;

import org.cellocad.common.netlist.Netlist;
import org.cellocad.common.profile.AlgorithmProfile;
import org.cellocad.common.runtime.RuntimeObject;
import org.cellocad.common.runtime.environment.RuntimeEnv;
import org.cellocad.common.stage.StageConfiguration;
import org.cellocad.common.target.data.TargetData;
import org.cellocad.eugene.algorithm.EUAlgorithm;
import org.cellocad.eugene.algorithm.EUAlgorithmFactory;

/**
 * @author: Timothy Jones
 * 
 * @date: Dec 6, 2017
 *
 */
public class EURuntimeObject extends RuntimeObject{

	public EURuntimeObject(
			final StageConfiguration stageConfiguration,
			final TargetData targetData,
			final Netlist netlist,
			final RuntimeEnv runEnv
			) {
		super(stageConfiguration, targetData, netlist, runEnv);
	}

	@Override
	protected void run() {
		// AlgorithmProfile
		AlgorithmProfile AProfile = this.getStageConfiguration().getAlgorithmProfile();
		// run Algorithm
		EUAlgorithmFactory eAF = new EUAlgorithmFactory();
		EUAlgorithm algo = eAF.getAlgorithm(AProfile);
		if (algo == null){
			throw new RuntimeException("Algorithm not found!");
		}
		algo.execute(this.getNetlist(), this.getTargetData(), AProfile, this.getRuntimeEnv());
	}
}
