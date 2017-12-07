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
package technologyMapping.data;

import org.json.simple.JSONObject;

import common.CObject;
import common.profile.ProfileUtils;

/**
 * @author: Vincent Mirian
 * 
 * @date: Dec 6, 2017
 *
 */
public class Gate extends CObject{

	private String group;
	//private HashMap<String, String> parts; //{part_name:dna_sequence, ...}
	//also need response functions
	private ResponseFunction responseFunction;
	private String partsMapVariable; //'maps_to_variable' in 'gate_parts'
	private String promoter; //'output' promoter, i.e. promoter repressor binds too
	
	
	public Gate() {
		//default constructor
	}
	

	public Gate(JSONObject jObj) {
		this.parseGates(jObj);
	}
	
	private void parseGates(JSONObject jObj) {
		//does initial parsing of gate data		    
		String groupName = ProfileUtils.getString(jObj, "group_name");
		String gateName = ProfileUtils.getString(jObj, "gate_name");
		this.setGroup(groupName);
		this.setName(gateName); //set name in CObject
    }
	
	public Gate(String group_name) {
		this.group = group_name;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

//	public HashMap<String, String> getParts() {
//		return parts;
//	}
//
//	public void setParts(HashMap<String, String> parts) {
//		this.parts = parts;
//	}

	public ResponseFunction getResponseFunction() {
		return responseFunction;
	}

	public void setResponseFunction(ResponseFunction responseFunction) {
		this.responseFunction = responseFunction;
	}

	public String getPartsMapVariable() {
		return partsMapVariable;
	}

	public void setPartsMapVariable(String partsMapVariable) {
		this.partsMapVariable = partsMapVariable;
	}

	public String getPromoter() {
		return promoter;
	}

	public void setPromoter(String promoter) {
		this.promoter = promoter;
	}

	@Override
	public String toString() {
		return "Gate [group=" + group + ", responseFunction=" + responseFunction 
				+ ", partsMapVariable=" + partsMapVariable + ", promoter=" + promoter + "]";
	}
	
}
