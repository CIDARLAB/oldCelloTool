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
package common.stage.runtime.environment;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import common.runtime.environment.RuntimeEnv;

/**
 * @author: Vincent Mirian
 * 
 * @date: Nov 20, 2017
 *
 */
public class RuntimeEnvStage extends RuntimeEnv{

	public RuntimeEnvStage(String[] args) {
		super(args);
	}

	@Override
	protected void setOptions() {
		super.setOptions();
		Options options = this.getOptions();
		options.addOption(this.getInputNetlistOption());
		options.addOption(this.getConfigFileOption());
		options.addOption(this.getOutputNetlistOption());
	}

	/*
	 * Options
	 */
	protected Option getInputNetlistOption(){
		Option rtn = new Option( ArgStringStage.INPUTNETLIST, true, ArgDescriptionStage.INPUTNETLIST_DESCRIPTION);
		this.makeRequired(rtn);
		return rtn;
	}
	
	protected Option getConfigFileOption(){
		Option rtn = new Option( ArgStringStage.CONFIGFILE, true, ArgDescriptionStage.CONFIGFILE_DESCRIPTION);
		this.makeRequired(rtn);
		return rtn;
	}
	
	protected Option getOutputNetlistOption(){
		Option rtn = new Option( ArgStringStage.OUTPUTNETLIST, true, ArgDescriptionStage.OUTPUTNETLIST_DESCRIPTION);
		this.makeRequired(rtn);
		return rtn;
	}
	
}
