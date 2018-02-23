package dnaCompiler.GateAssignment;

import java.util.ArrayList;
import java.util.List;

import common.netlist.NetListData;
import common.netlist.NetlistNode;

public class SimulateAnnealingNodeData extends NetListData{
	
	
	private List<Integer> logics;
	private List<Double> inputs; //input RPUs
	private List<Double> outputs; //output RPUs, calculated from inputRPUs
	private Gate gateObj; //weird name b/c netlistNode already has field called 'gate' which is just a string
	
	
	public SimulateAnnealingNodeData() {
		this.logics = new ArrayList<Integer>();
		this.inputs = new ArrayList<Double>();
		this.outputs = new ArrayList<Double>();
		
	}	
	
	public List<Integer> getLogics() {
		return logics;
	}
	public void setLogics(List<Integer> logics) {
		this.logics = logics;
	}
	public List<Double> getInputs() {
		return inputs;
	}
	public void setInputs(List<Double> inputs) {
		this.inputs = inputs;
	}
	public List<Double> getOutputs() {
		return outputs;
	}
	public void setOutputs(List<Double> outputs) {
		this.outputs = outputs;
	}
	public Gate getGateObj() {
		return gateObj;
	}
	public void setGate(Gate gate) {
		this.gateObj = gate;
	}
	
	
	

}
