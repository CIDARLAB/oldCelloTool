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
package common.runtime;

import common.CObject;
import common.Utils;
import common.netlist.Netlist;
import common.runtime.environment.RuntimeEnv;

/**
 * @author: Vincent Mirian
 * 
 * @date: Nov 17, 2017
 *
 */
// Object that aggregates the netlist, the stage configuration, target data and RuntimeEnv
abstract public class RuntimeObject extends CObject{

	public RuntimeObject(final Netlist netlist, final RuntimeEnv runEnv) {
		super();
		Utils.isNullRuntimeException(netlist, "netlist");
		Utils.isNullRuntimeException(runEnv, "runEnv");
		this.setNetlist(netlist);
		this.setRuntimeEnv(runEnv);
	}
	
	protected Netlist getNetlist() {
		return this.netlist;
	}
	
	private void setNetlist(final Netlist netlist) {
		this.netlist = netlist;
	}

	protected RuntimeEnv getRuntimeEnv() {
		return this.runEnv;
	}
	
	private void setRuntimeEnv(final RuntimeEnv runEnv) {
		this.runEnv = runEnv;
	}

	abstract protected void preprocessing();
	abstract protected void run();
	abstract protected void postprocessing();
	abstract protected String getOutputNetlist();
	
	public void execute() {
		preprocessing();
		run();
		postprocessing();
	}

	private Netlist netlist;
	private RuntimeEnv runEnv;
}
