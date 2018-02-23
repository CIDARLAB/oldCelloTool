package dnaCompiler.GateAssignment;

import java.util.ArrayList;
import java.util.List;

import common.CObjectCollection;
import common.netlist.NetlistNode;

/**
 * This class provides several static methods to simulate various aspects of a circuit during gate assignments. Methods to 
 * determine whether the transfer functions of two gates match as well as score toxicity of a give set of assignments. 
 * @author jaipadmakumar
 *
 */
public class Simulator {
		
	public Simulator() {
		//default constructor
	}
	
	
	/**
	 * Sets the 'outputs' field of gate 'B'. Assuming the output of gate 'A' serves as input for gate 'B' (i.e. A feeds into B),
	 * calculate the output of B based on the output of A. 
	 * @param A first gate in sequence, must have 'outputs' field set
	 * @param B gate to set
	 * @return
	 */
	public static void simulateTransferFunction(Gate A, Gate B) {
		//TODO: this produced the WRONG output for a NOR gate, those need to have a sum of inputs
		//TODO: overload method with a third gate to sum inputs 

		B.setInputs(A.getOutputs()); //set B inputs to A outputs
		List<Double> B_outputs = B.getResponseFunction().computeOutput(A.getOutputs());
		B.setOutputs(B_outputs);
		
	}
	
	public static void simulateTransferFunction(List<Gate> input_gates, Gate B) {
		List<Double> summed_inputs = new ArrayList<Double>(); //if have a NOR gate, need to sum inputs
		for(int i=0; i<input_gates.get(0).getInputs().size();++i) {
			double input = 0;
			input += input_gates.get(0).getInputs().get(i) + input_gates.get(1).getInputs().get(i);
			summed_inputs.add(input);
		}
		
		B.setInputs(summed_inputs);
		List<Double> B_outputs = B.getResponseFunction().computeOutput(summed_inputs);
		B.setOutputs(B_outputs);
		
	}
	
	/**
	 * Checks gate outputs (RPUs) against logics specified in NetlistNode logic vector and verifies that the outputs are in the 
	 * correct threshold ranges given the response function. 
	 * @param node
	 */
	public static boolean validateOutputLogic(NetlistNode node, CObjectCollection<Gate> gateLib) {
		//cello specified thresholds are IL and IH --> that is, if logic state is 0, input must be above IH and 
		//if logic state is 1, input must be below IL, for a NOT gate
		//in other words, the IL and IH map to thresholds for the input state into the gate while the logics
		//vector holds the output states of the gate
		
		
		List<Integer> logicStates = node.getNetListData().getLogics();
		Gate node_gate = gateLib.findCObjectByName(node.getGate());
		double lowThresh = node_gate.getResponseFunction().getOffThreshold(); //inputs must be below this value for a logic state of 1
		double highThresh = node_gate.getResponseFunction().getOnThreshold(); //inputs must be above this value for a logic state of 0
		List<Double> inputs = node_gate.getInputs(); //input RPUs
		//boolean allThresholdsValid = true;
		
		for(int i=0; i<inputs.size(); ++i) {
			double input = inputs.get(i);
			if(logicStates.get(i) == 0) {
				if(input > highThresh) {
					return false;
				}
			}
			else {
				if(input < lowThresh) {
					return false;
				}
			}
		}
		
		return true;
		
	}
}





