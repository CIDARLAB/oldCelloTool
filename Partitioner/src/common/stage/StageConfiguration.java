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
package common.stage;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import common.Utils;
import common.profile.AlgorithmProfile;
import common.profile.ProfileObject;
import common.profile.ProfileUtils;

/**
 * @author: Vincent Mirian
 * 
 * @date: Nov 20, 2017
 *
 */
public class StageConfiguration extends ProfileObject{

	private void init() {
	}
	
	public StageConfiguration(final JSONObject JObj, final String TargetConfigurationDir){
		super(JObj);
		init();
		parse(JObj, TargetConfigurationDir);
	}
	
	public StageConfiguration(final JSONObject JObj){
		this(JObj, "");
	}

	/*
	 * Parse
	 */
	private void parse(final JSONObject JObj, final String TargetConfigurationDir){
		String type = ProfileUtils.getString(JObj, "type");
	    Reader configFileReader = null;
		JSONObject jsonObj = null;
		String configFilename = null;
		// type
		if (type == null) {
			throw new RuntimeException("'type' missing for stage configuration " + this.getName() + ".");
		}
		// case check
		if (type.equalsIgnoreCase("file")) {
			// configData
			configFilename = ProfileUtils.getString(JObj, "configuration_data");
			if (configFilename == null) {
				throw new RuntimeException("'configuration_data' missing for stage configuration " + this.getName() + ".");
			}
			// Create File Reader
			try {
				configFileReader = new FileReader(TargetConfigurationDir + Utils.getFileSeparator() + configFilename);
			} catch (FileNotFoundException e) {
				throw new RuntimeException("Error with file: " + configFilename);
			}
			// Create JSON object from File Reader
			JSONParser parser = new JSONParser();
	        try{
	        	jsonObj = (JSONObject) parser.parse(configFileReader);
		    } catch (IOException e) {
		        throw new RuntimeException("File IO Exception for: " + configFilename + ".");
		    } catch (ParseException e) {
		        throw new RuntimeException("Parser Exception for: " + configFilename + ".");
		    }
		}
		else if (type.equalsIgnoreCase("data")) {
			// configData
			jsonObj = (JSONObject) ProfileUtils.getObject(JObj, "configuration_data");
		}
		else {
			throw new RuntimeException("Value of type unknown for stage configuration " + this.getName() + ".");
		}
		// Create AProfile object
		this.AProfile = new AlgorithmProfile(jsonObj);
		//close file
		if (type.equalsIgnoreCase("file")) {
		    try {
		    	configFileReader.close();
			} catch (IOException e) {
				throw new RuntimeException("Error with file: " + configFilename);
			}
		}
	}

	/*
	 * Getter and Setter
	 */
	public AlgorithmProfile getAlgorithmProfile() {
		return this.AProfile;
	}
	
	private AlgorithmProfile AProfile;
}
