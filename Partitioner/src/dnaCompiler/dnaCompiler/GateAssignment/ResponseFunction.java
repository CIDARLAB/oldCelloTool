package dnaCompiler.GateAssignment;


import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import common.CObject;
import common.profile.ProfileUtils;

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
	
	private void parseResponseFunctionObj(JSONObject jObj) {
		JSONArray variableArr = (JSONArray) jObj.get("variables");
		JSONArray paramArr = (JSONArray) jObj.get("parameters");
		
		JSONObject variableObject = (JSONObject) variableArr.get(0); //single array present
		JSONArray paramArrObject = (JSONArray) paramArr;
		
		//read straight from variable object
		String gate_name = ProfileUtils.getString(jObj, "gate_name");
		String equation = ProfileUtils.getString(jObj, "equation");
		String var_name = ProfileUtils.getString(variableObject, "name");
		double off_thresh = (double) ProfileUtils.getDouble(variableObject, "off_threshold");
		double on_thresh = (double) ProfileUtils.getDouble(variableObject, "on_threshold");

		//set response function values identically to UCF
		//param object is an array of JSONobjects which are hashmaps
		double ymax = 0;
		double ymin = 0;
		double kd = 0;
		double hill_constant = 0;
		
		for(Object obj:paramArrObject) {
			JSONObject data_obj = (JSONObject) obj;
			String name = ProfileUtils.getString(data_obj, "name");
			Double value = ProfileUtils.getDouble(data_obj, "value");		
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
	
	public double computeOutput(double x) {
		//hardcoded transfer function until symbolic evaluation math library is imported in
		double output = 0;
		double denominator = 1.0 + Math.pow(x/this.K, n);
		output = (this.yMax - this.yMin)/denominator; 
		output += this.yMin;
		return output;
	}
	
	public List<Double> computeOutput(List<Double> x_vals) {
		List<Double> outputs = new ArrayList<Double>();
		for(double x:x_vals) {
			double out = computeOutput(x);
			outputs.add(out);
		}
		return outputs;
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
