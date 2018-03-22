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
package org.cellocad.common.stage;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.cellocad.common.Utils;
import org.cellocad.common.runtime.environment.RuntimeEnv;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author: Vincent Mirian
 *
 * @date: Nov 20, 2017
 *
 */
public class StageUtils {

	static public StageConfiguration getStageConfiguration(final RuntimeEnv runEnv, final String configFile){
		Utils.isNullRuntimeException(runEnv, "runEnv");
		StageConfiguration rtn = null;
		String configFilename = runEnv.getOptionValue(configFile);
		Reader configFileReader = null;
		JSONObject jsonTop = null;
		// Create File Reader
		try {
			configFileReader = new FileReader(configFilename);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Error with file: " + configFilename);
		}
		// Create JSON object from File Reader
		JSONParser parser = new JSONParser();
		try{
			jsonTop = (JSONObject) parser.parse(configFileReader);
		} catch (IOException e) {
			throw new RuntimeException("File IO Exception for: " + configFilename + ".");
		} catch (ParseException e) {
			throw new RuntimeException("Parser Exception for: " + configFilename + ".");
		}
		// Create TargetInfo object
		rtn = new StageConfiguration(jsonTop);
		try {
			configFileReader.close();
		} catch (IOException e) {
			throw new RuntimeException("Error with file: " + configFilename);
		}
		return rtn;
	}

}
