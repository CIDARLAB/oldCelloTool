package dnaCompiler.GateAssignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import common.CObject;
import common.CObjectCollection;
import common.algorithm.Algorithm;
import common.graph.AbstractVertex.VertexType;
import common.netlist.Netlist;
import common.netlist.NetlistNode;
import common.target.data.TargetData;

public class SimulatedAnnealing extends CObject{
	
	private Netlist netlist;
	private TargetData td;
	private CObjectCollection<Gate> gates;
	CObjectCollection<NetlistNode> nodes;
	CObjectCollection<LogicNode> logicNodes;
	

	public SimulatedAnnealing(Netlist netlist, TargetData td) {
		this.netlist = netlist;
		this.td = td;
	}
	
	public SimulatedAnnealing(CObjectCollection<LogicNode> logicNodes, TargetData td) {
		//this.nodes = nodes;
		this.td = td;
		this.logicNodes = logicNodes;
	}
	
	public void setDefaultParameterValues() {
		//get and set all the gate objects
		int numGateObjs = td.getNumJSONObject("gates");
		int numResponseFuncs = td.getNumJSONObject("response_functions");
		CObjectCollection<Gate> gates = new CObjectCollection<Gate>();
		List<ResponseFunction> responseFunctions = new ArrayList<ResponseFunction>();
		//TODO: number of gates and response funcs should be identical, build something more robust later
		
		for(int i=0; i<numGateObjs;++i) {
			Gate gate = new Gate(td.getJSONObjectAtIdx("gates", i));
			ResponseFunction responseFunc = new ResponseFunction(td.getJSONObjectAtIdx("response_functions", i));
			gates.add(gate);
			responseFunctions.add(responseFunc);
		}
		
		for(ResponseFunction rf:responseFunctions) {
			String matching_gateName = rf.getGateName();
			Gate matching_gate = gates.findCObjectByName(matching_gateName);
			matching_gate.setResponseFunction(rf);
		}

		this.gates = gates;
	}

	public void setParameterValues() {
		// TODO Auto-generated method stub
		
		computeNodeLogics();
	}

	
	public void validateParameterValues() {
		// TODO Auto-generated method stub
		
	}

	
	public void preprocessing() {
		// TODO Auto-generated method stub
		
	}

	
	public void run() {
		// TODO Auto-generated method stub
		
	}

	public void postprocessing() {
		// TODO Auto-generated method stub
		
	}
	
	
	private void computeNodeLogics() {
		//each logicNode has a vector of size 2^num_inputs representing all possible logic states (essentially columns of a truth table)
		
		List<LogicNode> inputNodes = new ArrayList<LogicNode>();
		for(LogicNode node:this.logicNodes) {
			if(node.getVertexType().equals(VertexType.SOURCE)) {
				inputNodes.add(node);
			}
		}
		

		setInputLogics(inputNodes);
	    System.out.println(inputNodes.get(0).getLogics());
	    System.out.println(inputNodes.get(1).getLogics());
	    
		
		
		//now compute actual node logics
	    
	    
		
		
	}
	
	private void setInputLogics(List<LogicNode> inputNodes) {
		//dump code here just to look a bit cleaner for now --> most of this code is taken from original cello and modified slightly
		//initialize logicVectors to appropriate sizes
				int n_inputs = inputNodes.size();
				int numTruthTableRows = (int) Math.pow(2, inputNodes.size());
				List<Integer> logicVector = new ArrayList<Integer>();
				
				for(LogicNode node:this.logicNodes) {
					node.setLogics(logicVector);
				}
				
				int[] n  = new int[n_inputs];
			    int[] Nr = new int[n_inputs];
			    for (int i = 0; i<n_inputs; ++i){
			        Nr[i] = 1;
			    }
			    
			    //System.out.println(inputNodes.size());
			    ArrayList<int[]> input_logics_set = new ArrayList<int[]>();
			    SimulatedAnnealingUtils.getLogicPermutation(input_logics_set, n, Nr, 0); //modifys input_logics_set in place
			    
			    //input_logics_set looks like: [0, 0], [0, 1], [1, 0], [1, 1] for a 2-input circuit
			    
			    for(int[] arr:input_logics_set) {
			    		System.out.println(Arrays.toString(arr));
			    }
				
				//set input logic states
			    for(int i=0; i<n_inputs; ++i) {
			    		//List<Integer> inputNodeLogics = inputNodes.get(i).getLogics(); //for some reason, does not work when add integers here, have to set them outside
			    		List<Integer> logicVector1 = new ArrayList<Integer>();
			    		for(int[] arr:input_logics_set) {
			    			//inputNodeLogics.add(new Integer(arr[i]));
			    			logicVector1.add(new Integer(arr[i]));
			    			//System.out.println(Arrays.toString(arr));
			    		}
			    		inputNodes.get(i).setLogics(logicVector1);
			    		
			    }
				
		
	}
	public Netlist getNetlist() {
		return netlist;
	}
	public void setNetlist(Netlist netlist) {
		this.netlist = netlist;
	}
	public TargetData getTd() {
		return td;
	}
	public void setTd(TargetData td) {
		this.td = td;
	}

}
