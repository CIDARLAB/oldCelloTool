package dnaCompiler.GateAssignment;

import java.util.List;

import common.netlist.NetlistNode;


/**
 * Calculates the boolean logic a gate calculates w/ a given list of inputs. 
 * Inputs should be given as a list of input bits (if NOT gate list w/ single element)
 * @author jaipadmakumar
 *
 */
public class BooleanLogicCalculator {
	
	public static Integer computeLogic(Gate gate, List<Integer> inputs) {
		Integer output;
		
		if(gate.getNodeType() == "NOT") {
			output = computeNOT(inputs.get(0));	
		}
		else if(gate.getNodeType() == "NOR") {
			output = computeNOR(inputs);
		}
		
		else { //unrecognized NodeType
			throw new IllegalArgumentException();
		}
		
		return output;
		
	}
	
	public static Integer computeLogic(NetlistNode node, List<Integer> inputs) {
		Integer output;
		
		if(node.getNodeType() == "NOT") {
			output = computeNOT(inputs.get(0));	
		}
		else if(node.getNodeType() == "NOR") {
			output = computeNOR(inputs);
		}
		
		else if(node.getNodeType() == "OUTPUT") {
			output = inputs.get(0); //just a buffer gate for now
		}
		
		else { //unrecognized NodeType
			throw new IllegalArgumentException("Unrecognized node type");
		}
		
		return output;
	}
	
	public static Integer computeNOT(Integer i) {
		//takes an integer 1 or 0 and returns opposite value
		Integer out;
		if(i == 0) {
			out = 1;
		}
		else if(i==1) {
			out = 0;
		}
		else {
			throw new IllegalArgumentException("Input integer must be 0 or 1 and is not");
		}
		
		return out;
	}
	
	public static Integer computeNOR(List<Integer> inputs) {
		//returns 0 if any input is 1
		Integer out = 1;
		for(Integer input:inputs) {
			if(input ==1 ) {
				out = 0;
				return out;
			}
		}
		
		//sanity check on inputs
		for(Integer input:inputs) {
			if(input > 1 || input < 0) {
				throw new IllegalArgumentException("Input integers must be 0 or 1, at least of one them is not");
			}
		}
		return out;
		
	}

}
