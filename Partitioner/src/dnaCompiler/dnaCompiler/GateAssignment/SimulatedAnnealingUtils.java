package dnaCompiler.GateAssignment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import common.netlist.NetlistNode;

public class SimulatedAnnealingUtils {
	
	
	
	 /**
     * for a two-input circuit, save input_logics_set as
     * [0, 0], [0, 1], [1, 0], [1, 1];
     *
     */
	//copied from original cello
	public static void getLogicPermutation(ArrayList<int[]> input_logics_set, int[] n, int[] Nr, int idx) {
        if (idx == n.length){
            int[] input_logics = new int[n.length];
            System.arraycopy(n, 0, input_logics, 0, n.length);
            input_logics_set.add(input_logics);
            return;
        }

        for(int i=0; i<=Nr[idx]; ++i){
            n[idx] = i;
            getLogicPermutation(input_logics_set, n, Nr, idx+1);
        }
    }
	
	public static List<Gate> drawNRandomGates(List<Gate> listToDrawFrom, int n){
		 List<Gate> copy = new ArrayList<Gate>(listToDrawFrom);
		 Collections.shuffle(copy);
		 return copy.subList(0, n);
		 
	}
	
	public static List<Gate> drawNRandomGates(List<Gate> listToDrawFrom, int n, int seed){
		 List<Gate> copy = new ArrayList<Gate>(listToDrawFrom);
		 Collections.shuffle(copy, new Random(seed));
		 return copy.subList(0, n);
		 
	}
	
	public static List<NetlistNode> drawNRandomNodes(List<NetlistNode> listToDrawFrom, int n){
		List<NetlistNode> copy = new ArrayList<NetlistNode>(listToDrawFrom);
		Collections.shuffle(copy);
		return copy.subList(0, n);
	}
	
	
}
