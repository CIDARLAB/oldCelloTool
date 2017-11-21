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
package common.target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import common.Utils;
import common.runtime.environment.RuntimeEnv;
import common.target.runtime.environment.TargetArgString;

/**
 * @author: Vincent Mirian
 * 
 * @date: Nov 20, 2017
 *
 */
public class TargetUtils {

	static public TargetConfiguration getTargetInfo(RuntimeEnv runEnv){
		Utils.isNullRuntimeException(runEnv, "runEnv");
		TargetConfiguration rtn = null;
		// get Target File
		String targetFilename = runEnv.getOptionValue(TargetArgString.TARGETDATAFILE);
		String targetDir = runEnv.getOptionValue(TargetArgString.TARGETDIR);
	    File targetFile = new File(targetDir + Utils.getFileSeparator() + targetFilename);
	    Reader targetReader = null;
		JSONObject jsonTop = null;
		// Create File Reader
		try {
			targetReader = new FileReader(targetFile);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Error with file: " + targetFile);
		}
		// Create JSON object from File Reader
		JSONParser parser = new JSONParser();
        try{
        	jsonTop = (JSONObject) parser.parse(targetReader);
	    } catch (IOException e) {
	        throw new RuntimeException("File IO Exception for: " + targetFile + ".");
	    } catch (ParseException e) {
	        throw new RuntimeException("Parser Exception for: " + targetFile + ".");
	    }
		// Create TargetInfo object
	    rtn = new TargetConfiguration(jsonTop);
	    try {
			targetReader.close();
		} catch (IOException e) {
			throw new RuntimeException("Error with file: " + targetFile);
		}
	    return rtn;
	}
	
}
