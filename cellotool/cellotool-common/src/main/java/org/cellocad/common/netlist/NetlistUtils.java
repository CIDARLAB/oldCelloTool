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
package org.cellocad.common.netlist;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import org.cellocad.common.Utils;
import org.cellocad.common.JSON.JSONUtils;
import org.cellocad.common.runtime.environment.RuntimeEnv;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author: Vincent Mirian
 * 
 * @date: Nov 21, 2017
 *
 */
public class NetlistUtils {

	static public Netlist getNetlist(final RuntimeEnv runEnv, final String inputNetlist){
		Utils.isNullRuntimeException(runEnv, "runEnv");
		Netlist rtn = null;
		String inputNetlistFilename = runEnv.getOptionValue(inputNetlist);
	    Reader inputNetlistReader = null;
		JSONObject jsonTop = null;
		// Create File Reader
		try {
			inputNetlistReader = new FileReader(inputNetlistFilename);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Error with file: " + inputNetlistFilename);
		}
		// Create JSON object from File Reader
		JSONParser parser = new JSONParser();
        try{
        	jsonTop = (JSONObject) parser.parse(inputNetlistReader);
	    } catch (IOException e) {
	        throw new RuntimeException("File IO Exception for: " + inputNetlistFilename + ".");
	    } catch (ParseException e) {
	        throw new RuntimeException("Parser Exception for: " + inputNetlistFilename + ".");
	    }
		// Create TargetInfo object
        rtn = new Netlist(jsonTop);
	    try {
	    	inputNetlistReader.close();
		} catch (IOException e) {
			throw new RuntimeException("Error with file: " + inputNetlistFilename);
		}
	    return rtn;
	}
	
	static public void writeJSONForNetlist(final Netlist netlist, final String filename){
		try {
			OutputStream outputStream = new FileOutputStream(filename);
			Writer outputStreamWriter = new OutputStreamWriter(outputStream);
			outputStreamWriter.write(JSONUtils.getStartEntryString());
			netlist.writeJSON(1, outputStreamWriter);
			outputStreamWriter.write(JSONUtils.getEndEntryString());
			outputStreamWriter.close();
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
