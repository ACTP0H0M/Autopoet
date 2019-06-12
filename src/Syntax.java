package autopoet;
import java.util.ArrayList;

public class Syntax {
	
	private ArrayList<Parameter> structure = new ArrayList<Parameter>();
	private int originNumber;
	
	public int getOriginNumber() {
		return originNumber;
	}

	public void setOriginNumber(int originNumber) {
		this.originNumber = originNumber;
	}

	public void addParameter(Parameter p){
		structure.add(p);
	}
	
	public ArrayList<Parameter> getStructure(){
		return structure;
	}
	
	public void clearStructure() {
		structure.clear();
	}
	

}
