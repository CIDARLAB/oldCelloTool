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
	
	public SimulatedAnnealing(CObjectCollection<NetlistNode> nodes, TargetData td) {
		this.nodes = nodes;
		this.td = td;
		//this.logicNodes = logicNodes;
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
		//computes and sets all node logics
		computeNodeLogics();
	}

	
	public void validateParameterValues() {
		// TODO Auto-generated method stub
		for(NetlistNode node:this.nodes) {
			
		}
		
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
		
		List<NetlistNode> inputNodes = new ArrayList<NetlistNode>();
		for(NetlistNode node:this.nodes) {
			if(node.getVertexType().equals(VertexType.SOURCE)) {
				inputNodes.add(node);
			}
		}
		

		setInputLogics(inputNodes);
	    System.out.println(inputNodes.get(0).getNetListData().getLogics());
	    System.out.println(inputNodes.get(1).getNetListData().getLogics());
	    
		
		
		//now compute actual node logics --> have to iterate through gates in order so that all parents of node have logics set 
	    //before getting to given node
	    // i.e. if for example AND gate of NOT/NOR, if NOR comes first in list will be wrong b/c NOR gate doesn't
	    // won't have parent NOT gate logics set yet --> in other words, this only works if netlist is constructed properly
	    // and is not well designed = would need to sort NetlistNodes by distance to input to make this more robust
	    for(NetlistNode node:this.nodes) {
			if(!node.getVertexType().equals(VertexType.SOURCE)) {
				computeNodeLogic(node);
			}
			
			System.out.println("Node: " + node.getName() + ", logics: " + node.getNetListData().getLogics());
	    }
	    
	    
	    
	    
		
		
	}
	
	private void setInputLogics(List<NetlistNode> inputNodes) {
		//dump code here just to look a bit cleaner for now --> most of this code is taken from original cello and modified slightly
		//initialize logicVectors to appropriate sizes
				int n_inputs = inputNodes.size();
				int numTruthTableRows = (int) Math.pow(2, inputNodes.size());
				List<Integer> logicVector = new ArrayList<Integer>();
				
				for(NetlistNode node:this.nodes) {
					node.getNetListData().setLogics(logicVector);
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
			    		inputNodes.get(i).getNetListData().setLogics(logicVector1);
			    		
			    }
			    
	}
	
	private void computeNodeLogic(NetlistNode node) {
		//requires nodes at incoming edges to have logic already set
		List<NetlistNode> inNodes = new ArrayList<NetlistNode>();
		List<List<Integer>> inNodeLogics = new ArrayList<List<Integer>>();
		List<Integer> nodeLogic = new ArrayList<Integer>();
		for(int i=0; i<node.getNumInEdge();++i) {
			NetlistNode incomingNode = node.getInEdgeAtIdx(i).getSrc();
			inNodeLogics.add(incomingNode.getNetListData().getLogics());
		}
		
		//if inNodes.size == 1 --> have a NOT gate, if inNodes.size == 2 --> have a NOR gate
		int logicsVectorSize = inNodeLogics.get(0).size();
		
		for(int i=0; i<logicsVectorSize;++i) {
			List<Integer> logicInput = new ArrayList<Integer>(); //inputs for a function
			for(List<Integer> inNode_logics:inNodeLogics) {
				logicInput.add(inNode_logics.get(i));
			}
			Integer output = BooleanLogicCalculator.computeLogic(node, logicInput);
			nodeLogic.add(output);
		}
		node.getNetListData().setLogics(nodeLogic);
		
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
