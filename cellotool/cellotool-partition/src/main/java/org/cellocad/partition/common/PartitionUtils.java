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
package org.cellocad.partition.common;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.cellocad.common.Utils;


/**
 * @author: Vincent Mirian
 *
 * @date: Oct 31, 2017
 *
 */
public class PartitionUtils {

	static public void writeDotFileForPartition(final Partition P, final String filename){
		try {
			String dotFilename = filename + ".dot";
			OutputStream outputStream = new FileOutputStream(dotFilename);
			Writer outputStreamWriter = new OutputStreamWriter(outputStream);
			P.printDot(outputStreamWriter);
			outputStreamWriter.close();
			outputStream.close();
			// Unix-based only
			Utils.executeDot2PDFShellFilename(dotFilename);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static public void writeDotFileForPartition(final Partition P){
		String name = P.getName();
		if (name.isEmpty()){
			name = Utils.getTimeString();
		}
		PartitionUtils.writeDotFileForPartition(P, name);
	}

}
