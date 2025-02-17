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
import org.cellocad.common.profile.ProfileObject;
import org.cellocad.common.profile.ProfileUtils;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * @author: Vincent Mirian
 *
 * @date: Nov 20, 2017
 *
 */
public class Stage extends ProfileObject{

	private void init() {
	}

	public Stage(final JsonObject JObj, final String TargetConfigurationDir){
		super(JObj);
		init();
		parse(JObj, TargetConfigurationDir);
	}

	public Stage(final JsonObject JObj){
		this(JObj, "");
	}
	/*
	 * Parse
	 */
	private void parseStageConfiguration(final JsonObject JObj, final String TargetConfigurationDir){
		// parse StageConfiguration
		Reader configFileReader = null;
		JsonObject jsonObj = null;
		String configFilename = null;
		// type
		String type = ProfileUtils.getString(JObj, "configuration_type");
		if (type == null) {
			throw new RuntimeException("'configuration_type' missing for stage configuration " + this.getName() + ".");
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
			JsonParser parser = new JsonParser();
			try{
				jsonObj = parser.parse(configFileReader).getAsJsonObject();
			} catch (JsonIOException e) {
				throw new RuntimeException("File IO Exception for: " + configFilename + ".");
			} catch (JsonSyntaxException e) {
				throw new RuntimeException("Parser Exception for: " + configFilename + ".");
			}
		}
		else if (type.equalsIgnoreCase("data")) {
			// configData
			jsonObj = ProfileUtils.getJsonElement(JObj, "configuration_data").getAsJsonObject();
		}
		else {
			throw new RuntimeException("Value of type unknown for stage configuration " + this.getName() + ".");
		}
		// Create StageConfiguration object
		this.setStageConfiguration(new StageConfiguration(jsonObj));
		//close file
		if (type.equalsIgnoreCase("file")) {
			try {
				configFileReader.close();
			} catch (IOException e) {
				throw new RuntimeException("Error with file: " + configFilename);
			}
		}
	}

	private void parse(final JsonObject JObj, final String TargetConfigurationDir){
		this.parseStageConfiguration(JObj, TargetConfigurationDir);
	}

	/*
	 * Getter and Setter
	 */
	public StageConfiguration getStageConfiguration() {
		return this.stageConfiguration;
	}

	private void setStageConfiguration(final StageConfiguration sc) {
		this.stageConfiguration = sc;
	}

	StageConfiguration stageConfiguration;
}
