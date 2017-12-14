package dnaCompiler.GateAssignment;

import org.json.simple.JSONObject;

import common.CObject;

public abstract class TransferFunction extends CObject{
	
	private double offThreshold;
	private double onThreshold;
	
	protected abstract void parseResponseFunctionObj(JSONObject jobj); //parse response function object given in the particular ucf
	
	
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

}
