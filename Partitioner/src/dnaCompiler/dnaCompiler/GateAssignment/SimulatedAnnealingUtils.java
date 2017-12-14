package dnaCompiler.GateAssignment;

import java.util.ArrayList;

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
	
}
