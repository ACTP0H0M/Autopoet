package autopoet;

public class Prichastie extends Word{
	
	private String padezh;
	private String chislo;
	private String rod;
	private boolean polnoe;
	
	public boolean isPolnoe() {
		return polnoe;
	}
	public void setPolnoe(boolean polnoe) {
		this.polnoe = polnoe;
	}
	public String getPadezh() {
		return padezh;
	}
	public void setPadezh(String padezh) {
		this.padezh = padezh;
	}
	public String getChislo() {
		return chislo;
	}
	public void setChislo(String chislo) {
		this.chislo = chislo;
	}
	public String getRod() {
		return rod;
	}
	public void setRod(String rod) {
		this.rod = rod;
	}
	
	

}
