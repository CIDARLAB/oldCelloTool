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
package target.ecoli.singlecell.plate;

import common.CObject;
import common.netlist.Netlist;
import common.runtime.environment.RuntimeEnv;
import common.stage.Stage;
import common.target.TargetConfiguration;
import common.target.TargetUtils;
import common.target.runtime.environment.TargetArgString;

/**
 * @author: Vincent Mirian
 * 
 * @date: Nov 20, 2017
 *
 */
public class Main extends CObject{

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RuntimeEnv runEnv = new RuntimeEnv(args);
		Netlist netlist = new Netlist();
	    TargetConfiguration targetCfg = TargetUtils.getTargetConfiguration(runEnv, TargetArgString.CONFIGFILE, TargetArgString.TARGETDATADIR);
	    Stage currentStage = null;
		// LogicSynthesis
	    currentStage = targetCfg.getStageByName("LogicSynthesis");
		//LogicSynthesis LS = new LogicSynthesis(netlist, runEnv);
		//LS.execute();
		// TechnologyMapping
	    currentStage = targetCfg.getStageByName("TechnologyMapping");
		//TechnologyMapping TM = new TechnologyMapping(netlist, runEnv);
		//TM.execute();
		// Eugene
	    currentStage = targetCfg.getStageByName("Eugene");
		//Eugene EU = new Eugene(netlist, runEnv);
		//EU.execute();
	    System.out.println(netlist.getName());
	    System.out.println(currentStage.getName());
	}
	
}
