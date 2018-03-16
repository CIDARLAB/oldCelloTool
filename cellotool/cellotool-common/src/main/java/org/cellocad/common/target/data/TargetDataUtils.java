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
package org.cellocad.common.target.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.cellocad.common.Utils;
import org.cellocad.common.runtime.environment.RuntimeEnv;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author: Vincent Mirian
 * 
 * @date: Nov 22, 2017
 *
 */
public class TargetDataUtils {

	static public TargetData getTargetTargetData(final RuntimeEnv runEnv, final String targetDataFile, final String targetDataDir){
		Utils.isNullRuntimeException(runEnv, "runEnv");
		Utils.isNullRuntimeException(targetDataFile, "targetDataFile");
		Utils.isNullRuntimeException(targetDataDir, "targetDataDir");
		TargetData rtn = null;
		// get Target File
		String targetFilename = runEnv.getOptionValue(targetDataFile);
		String targetDir = runEnv.getOptionValue(targetDataDir);
	    File targetFile = new File(targetDir + Utils.getFileSeparator() + targetFilename);
	    Reader targetReader = null;
	    JSONArray jsonTop = null;
		// Create File Reader
		try {
			targetReader = new FileReader(targetFile);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Error with file: " + targetFile);
		}
		// Create JSON object from File Reader
		JSONParser parser = new JSONParser();
        try{
        	jsonTop = (JSONArray) parser.parse(targetReader);
	    } catch (IOException e) {
	        throw new RuntimeException("File IO Exception for: " + targetFile + ".");
	    } catch (ParseException e) {
	        throw new RuntimeException("Parser Exception for: " + targetFile + ".");
	    }
		// Create TargetInfo object
	    rtn = new TargetData(jsonTop, targetDataDir);
	    try {
			targetReader.close();
		} catch (IOException e) {
			throw new RuntimeException("Error with file: " + targetFile);
		}
	    return rtn;
	}	
}
