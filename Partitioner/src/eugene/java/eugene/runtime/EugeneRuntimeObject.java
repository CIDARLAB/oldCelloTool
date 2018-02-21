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
package eugene.runtime;

import common.netlist.Netlist;
import common.profile.AlgorithmProfile;
import common.runtime.RuntimeObject;
import common.runtime.environment.RuntimeEnv;
import common.stage.StageConfiguration;
import common.target.data.TargetData;
import eugene.algorithm.EugeneAlgorithm;
import eugene.algorithm.EugeneAlgorithmFactory;

/**
 * @author: Timothy Jones
 * 
 * @date: Dec 6, 2017
 *
 */
public class EugeneRuntimeObject extends RuntimeObject{

	public EugeneRuntimeObject(
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
		EugeneAlgorithmFactory eAF = new EugeneAlgorithmFactory();
		EugeneAlgorithm algo = eAF.getAlgorithm(AProfile);
		if (algo == null){
	    	throw new RuntimeException("Algorithm not found!");
		}
		algo.execute(this.getNetlist(), this.getTargetData(), AProfile, this.getRuntimeEnv());
	}
}
