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
package target.runtime.environment;


import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import common.runtime.environment.RuntimeEnv;

/**
 * @author: Vincent Mirian
 * 
 * @date: Nov 20, 2017
 *
 */
public class RuntimeEnvTarget extends RuntimeEnv{

	public RuntimeEnvTarget(String[] args) {
		super(args);
	}

	@Override
	protected String getDefault(final String str){
		String rtn = null;
		switch (str) {
        case ArgStringTarget.OUTPUTDIR:  
        case ArgStringTarget.CELLODIR:
        	rtn = "./";
        	break;
		}
		return rtn;
	}

	@Override
	protected void setOptions() {
		super.setOptions();
		Options options = this.getOptions();
		options.addOption(this.getVerilogOption());
		options.addOption(this.getTargetFileOption());
		options.addOption(this.getTargetDirOption());
		options.addOption(this.getNetlistConstraintFileOption());
		options.addOption(this.getOutputDirOption());
		options.addOption(this.getCelloDirOption());
		options.addOption(this.getPythonDirOption());
	}
	
	/*
	 * Options
	 */
	protected Option getVerilogOption(){
		Option rtn = new Option( ArgStringTarget.VERILOG, true, ArgDescriptionTarget.VERILOG_DESCRIPTION);
		this.makeRequired(rtn);
		return rtn;
	}
	
	protected Option getTargetFileOption(){
		Option rtn = new Option( ArgStringTarget.TARGETFILE, true, ArgDescriptionTarget.TARGETFILE_DESCRIPTION);
		this.makeRequired(rtn);
		return rtn;
	}
	
	protected Option getTargetDirOption(){
		Option rtn = new Option( ArgStringTarget.TARGETDIR, true, ArgDescriptionTarget.TARGETDIR_DESCRIPTION);
		this.makeRequired(rtn);
		return rtn;
	}
	
	protected Option getNetlistConstraintFileOption(){
		Option rtn = new Option( ArgStringTarget.NETLISTCONSTRAINTFILE, true, ArgDescriptionTarget.NETLISTCONSTRAINTFILE_DESCRIPTION);
		return rtn;
	}
	
	protected Option getOutputDirOption(){
		Option rtn = new Option( ArgStringTarget.OUTPUTDIR, true, ArgDescriptionTarget.OUTPUTDIR_DESCRIPTION);
		return rtn;
	}
	
	protected Option getCelloDirOption(){
		Option rtn = new Option( ArgStringTarget.CELLODIR, true, ArgDescriptionTarget.CELLODIR_DESCRIPTION);
		return rtn;
	}
	
	protected Option getPythonDirOption(){
		Option rtn = new Option( ArgStringTarget.PYTHONDIR, true, ArgDescriptionTarget.PYTHONDIR_DESCRIPTION);
		return rtn;
	}
}
