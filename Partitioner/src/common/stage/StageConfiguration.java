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

import org.json.simple.JSONObject;

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
		this.type = "";
		this.configurationFilename = "";
	}
	
	public StageConfiguration(final JSONObject JObj){
		super(JObj);
		init();
		parse(JObj);
	}

	/*
	 * Parse
	 */
	private void parseType(final JSONObject JObj){
		String type = ProfileUtils.getString(JObj, "type");
		this.setConfigurationType(type);
	}

	private void parseConfigurationFilename(final JSONObject JObj){
		String cf = ProfileUtils.getString(JObj, "configuration_filename");
		this.setConfigurationFilename(cf);
	}

	private void parse(final JSONObject JObj){
    	this.parseType(JObj);
    	this.parseConfigurationFilename(JObj);
	}

	/*
	 * Getter and Setter
	 */
	public String getConfigurationType() {
		return this.type;
	}
	
	private void setConfigurationType(final String type) {
		this.type = type;
	}
	
	public String getConfigurationFilename() {
		return this.configurationFilename;
	}
	
	private void setConfigurationFilename(final String filename) {
		this.configurationFilename = filename;
	}

	String type;
	String configurationFilename;
}
