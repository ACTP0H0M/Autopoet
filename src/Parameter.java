package autopoet;

public class Parameter {
	
	private String[] priznaki = new String[7];

	public String[] getPriznaki() {
		return priznaki;
	}

	public void setPriznaki(String[] priznaki) {
		this.priznaki = priznaki;
	}
	
	public void setOnePriznak(int index, String priznak) {
		this.priznaki[index] = priznak;
	}
	
	public void printPriznaki() {
		for (int i = 0; i < priznaki.length; i++) {
			System.out.print(priznaki[i] + "/");
		}
		System.out.print("\n");
	}

}
