package dnaCompiler.GateAssignment;

import org.json.simple.JSONObject;

import common.CObject;
import common.profile.ProfileUtils;


public class Gate extends CObject{

	private String group;
	//private HashMap<String, String> parts; //{part_name:dna_sequence, ...}
	//also need response functions
	private ResponseFunction responseFunction;
	private String partsMapVariable; //'maps_to_variable' in 'gate_parts'
	private String promoter; //'output' promoter, i.e. promoter repressor binds too
	
	
	public Gate() {
		//default constructor
	}
	

	public Gate(JSONObject jObj) {
		this.parseGates(jObj);
	}
	
	private void parseGates(JSONObject jObj) {
		//does initial parsing of gate data		    
		String groupName = ProfileUtils.getString(jObj, "group_name");
		String gateName = ProfileUtils.getString(jObj, "gate_name");
		this.setGroup(groupName);
		this.setName(gateName); //set name in CObject
    }
	
	public Gate(String group_name) {
		this.group = group_name;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

//	public HashMap<String, String> getParts() {
//		return parts;
//	}
//
//	public void setParts(HashMap<String, String> parts) {
//		this.parts = parts;
//	}

	public ResponseFunction getResponseFunction() {
		return responseFunction;
	}

	public void setResponseFunction(ResponseFunction responseFunction) {
		this.responseFunction = responseFunction;
	}

	public String getPartsMapVariable() {
		return partsMapVariable;
	}

	public void setPartsMapVariable(String partsMapVariable) {
		this.partsMapVariable = partsMapVariable;
	}

	public String getPromoter() {
		return promoter;
	}

	public void setPromoter(String promoter) {
		this.promoter = promoter;
	}

	@Override
	public String toString() {
		return "Gate [group=" + group + ", responseFunction=" + responseFunction 
				+ ", partsMapVariable=" + partsMapVariable + ", promoter=" + promoter + "]";
	}
	
}
