package test.verilog;

import org.junit.Test;

import common.netlist.Netlist;
import common.stage.Stage;
import common.target.TargetConfiguration;
import common.target.TargetUtils;
import common.target.data.TargetData;
import common.target.data.TargetDataUtils;
import common.target.runtime.environment.TargetArgString;
import common.target.runtime.environment.TargetRuntimeEnv;
import logicSynthesis.runtime.LSRuntimeObject;
import test.common.TestUtils;

public class VerilogTest {

	@Test
	public void test() {
		String resourcesFilepath = TestUtils.getResourcesFilepath() + "/verilog/";
			
		String[] args = new String[] {"-verilogFile",resourcesFilepath + "andgate.v",
									  "-targetDataDir",resourcesFilepath,
									  "-targetDataFile","Eco1C1G1T1.UCF.json",
									  "-configDir",resourcesFilepath,
									  "-configFile","config.json"};
										  
		
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
	}

}
