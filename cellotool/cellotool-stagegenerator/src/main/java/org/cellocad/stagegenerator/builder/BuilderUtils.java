/**
 * Copyright (C) 2018 Boston University (BU)
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
package org.cellocad.stagegenerator.builder;

import org.cellocad.common.Utils;

/**
 * Utilities for Builder objects.
 *
 * @author: Timothy Jones
 *
 * @date: Mar 21, 2018
 *
 */

public class BuilderUtils{

	public static String instantiateByNew(String left, String name, String right, String... args) {
		Utils.isNullRuntimeException(left,"left");
		Utils.isNullRuntimeException(name,"name");
		Utils.isNullRuntimeException(right,"right");
		String rtn = "";
		rtn += left + " " + name + " = new ";
		rtn += methodCall(right, args);
		return rtn;
	}

	public static String instantiateByCall(String left, String name, String right, String... args) {
		Utils.isNullRuntimeException(left,"left");
		Utils.isNullRuntimeException(name,"name");
		Utils.isNullRuntimeException(right,"right");
		String rtn = "";
		rtn += left + " " + name + " = ";
		rtn += methodCall(right, args);
		return rtn;
	}

	public static String methodCall(String name, String... args) {
		Utils.isNullRuntimeException(name,"name");
		String rtn = "";
		rtn += name + "(";
		rtn += String.join(", ",args);
		rtn += ")";
		return rtn;
	}

}
