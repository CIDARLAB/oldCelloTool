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
package org.cellocad.common.algorithm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.common.CObject;

/**
 * @author: Vincent Mirian
 *
 * @date: Nov 17, 2017
 *
 */
public abstract class Algorithm extends CObject{

	abstract protected void setDefaultParameterValues();
	abstract protected void setParameterValues();
	abstract protected void validateParameterValues();
	abstract protected void preprocessing();
	abstract protected void run();
	abstract protected void postprocessing();

	protected void logTrace(String str) {
		this.getLogger().trace(str);
	}

	protected void logDebug(String str) {
		this.getLogger().debug(str);
	}

	protected void logInfo(String str) {
		this.getLogger().info(str);
	}

	protected void logWarn(String str) {
		this.getLogger().warn(str);
	}

	protected void logError(String str) {
		this.getLogger().error(str);
	}

	protected void logFatal(String str) {
		this.getLogger().fatal(str);
	}

	protected Logger getLogger() {
		return Algorithm.logger;
	}

	private static final Logger logger = LogManager.getLogger(Algorithm.class.getSimpleName());

}
