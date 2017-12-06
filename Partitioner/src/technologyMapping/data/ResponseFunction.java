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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import common.CObject;

/**
 * @author: Vincent Mirian
 * 
 * @date: Dec 6, 2017
 *
 */
public class ResponseFunction extends CObject{
	private String gateName;
	private String equation;
	private String variableName;
	private double offThreshold;
	private double onThreshold;
	private double yMax;
	private double yMin;
	private double K; //kD
	private double n; //hill constant
	
	public ResponseFunction() {
		//default constructor
	}

	public ResponseFunction(JSONObject jobj) {
		this.parseResponseFunctionObj(jobj);
	}
	
	private void parseResponseFunctionObj(JSONObject jobj) {
		String equation = (String) jobj.get("equation");
		JSONArray variableArr = (JSONArray) jobj.get("variables");
		JSONArray paramArr = (JSONArray) jobj.get("parameters");
		
		JSONObject variableObject = (JSONObject) variableArr.get(0); //single array present
		JSONArray paramArrObject = (JSONArray) paramArr;
		
		//read straight from variable object
		String gate_name = (String) variableObject.get("gate_name");
		String var_name = (String) variableObject.get("name");
		double off_thresh = (double) variableObject.get("off_threshold");
		double on_thresh = (double) variableObject.get("on_threshold");

		//set response function values identically to UCF
		//param object is an array of JSONobjects which are hashmaps
		double ymax = 0;
		double ymin = 0;
		double kd = 0;
		double hill_constant = 0;
		
		for(Object obj:paramArrObject) {
			JSONObject data_obj = (JSONObject) obj;
			String name = (String) data_obj.get("name");
			Double value = (Double) data_obj.get("value");
			
			if(name.equals("ymax")) {
				ymax = value;
			}
			else if(name.equals("ymin")) {
				ymin = value;
			}
			else if(name.equals("K")) {
				kd = value;
			}
			else if(name.equals("n")) {
				hill_constant = value;
			}
		}

		this.setGateName(gate_name);
		this.setEquation(equation);
		this.setOffThreshold(off_thresh);
		this.setOnThreshold(on_thresh);
		this.setVariableName(var_name);
		this.setyMax(ymax);
		this.setyMin(ymin);
		this.setK(kd);
		this.setN(hill_constant);
	}

	public String getGateName() {
		return gateName;
	}

	public void setGateName(String gateName) {
		this.gateName = gateName;
	}
	
	public String getEquation() {
		return equation;
	}

	public void setEquation(String equation) {
		this.equation = equation;
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	public double getOffThreshold() {
		return offThreshold;
	}

	public void setOffThreshold(double offThreshold) {
		this.offThreshold = offThreshold;
	}

	public double getOnThreshold() {
		return onThreshold;
	}

	public void setOnThreshold(double onThreshold) {
		this.onThreshold = onThreshold;
	}

	public double getyMax() {
		return yMax;
	}

	public void setyMax(double yMax) {
		this.yMax = yMax;
	}

	public double getyMin() {
		return yMin;
	}

	public void setyMin(double yMin) {
		this.yMin = yMin;
	}

	public double getK() {
		return K;
	}

	public void setK(double k) {
		K = k;
	}

	public double getN() {
		return n;
	}

	public void setN(double n) {
		this.n = n;
	}
}
