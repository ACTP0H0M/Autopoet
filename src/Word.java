package autopoet;

public class Word {
	
	private String chastRechi;
	private int udarniySlog;
	private String slovo;
	private int slogov;
	
	public int getSlogov() {
		return slogov;
	}
	public void setSlogov(int slogov) {
		this.slogov = slogov;
	}
	public String getSlovo() {
		return slovo;
	}
	public void setSlovo(String slovo) {
		this.slovo = slovo;
	}
	public String getChastRechi() {
		return chastRechi;
	}
	public void setChastRechi(String chastRechi) {
		this.chastRechi = chastRechi;
	}
	public int getUdarniySlog() {
		return udarniySlog;
	}
	public void setUdarniySlog(int udarniySlog) {
		this.udarniySlog = udarniySlog;
	}
	
	public void shortRepresentation() {
		String[] prizn = new String[6];
		switch(this.getChastRechi()){
		case "сущ" : {
			prizn[0] = "сущ";
			prizn[1] = ((Suschestvitelnoe) this).getPadezh();
			prizn[2] = ((Suschestvitelnoe) this).getRod();
			prizn[3] = ((Suschestvitelnoe) this).getChislo();
		} break;
		case "глаг" : {
			prizn[0] = "глаг";
			prizn[1] = ((Glagol) this).getVremya();
			prizn[2] = ((Glagol) this).getChislo();
			prizn[3] = ((Glagol) this).getRod();
			if(((Glagol) this).isPerekhod()){
				prizn[4] = "да";
			}else{
				prizn[4] = "нет";
			}
		} break;
		case "прил" : {
			prizn[0] = "прил";
			prizn[1] = ((Prilagatelnoe) this).getPadezh();
			prizn[2] = ((Prilagatelnoe) this).getRod();
			prizn[3] = ((Prilagatelnoe) this).getChislo();
			if(((Prilagatelnoe) this).isPolnoe()){
				prizn[4] = "да";
			}else{
				prizn[4] = "нет";
			}
		} break;
		case "мест" : {
			prizn[0] = "мест";
			prizn[1] = ((Mestoimenie) this).getRazryadGr();
			prizn[2] = ((Mestoimenie) this).getRazryadZn();
			prizn[3] = ((Mestoimenie) this).getPadezh();
			prizn[4] = ((Mestoimenie) this).getRod();
			prizn[5] = ((Mestoimenie) this).getChislo();
		} break;
		case "прич" : {
			prizn[0] = "прич";
			prizn[1] = ((Prichastie) this).getPadezh();
			prizn[2] = ((Prichastie) this).getRod();
			prizn[3] = ((Prichastie) this).getChislo();
			if(((Prichastie) this).isPolnoe()){
				prizn[4] = "да";
			}else{
				prizn[4] = "нет";
			}
		} break;
		case "нар" : {
			prizn[0] = "нар";
		} break;
		case "деепр" : {
			prizn[0] = "деепр";
		} break;
		case "союз" : {
			prizn[0] = "союз";
			prizn[1] = this.getSlovo();
		} break;
		case "част" : {
			prizn[0] = "част";
			prizn[1] = this.getSlovo();
		} break;
		case "предл" : {
			prizn[0] = "предл";
			prizn[1] = this.getSlovo();
		} break;
		default : System.out.println("ERROR");
		}
		for (int i = 0; i < 6; i++) {
			System.out.print(prizn[i] + "/");
		}
		System.out.println();
	}

}
