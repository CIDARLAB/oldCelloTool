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
package org.cellocad.eugene.test.common;

import java.io.File;
import java.io.IOException;

/**
 * @author: Vincent Mirian
 *
 * @date: Dec 8, 2017
 *
 */
public class TestUtils {

	static public String getFilepath(){
		String rtn = "";
		rtn = TestUtils.class.getClassLoader().getResource(".").getPath();
		return rtn;
	}

	static public String getResourcesFilepath(){
		String rtn = "";
		rtn += TestUtils.getFilepath();
		return rtn;
	}

	static public File createTempDirectory(){
		File file = null;
		try {
			file = File.createTempFile("cellotool_", "_tmp");
			file.delete();
			file.mkdirs();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return file;
	}

}
