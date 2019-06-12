package autopoet;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class DataManager {
	
	/*
	 * Dictionary: слово/количество слогов/ударный слог/часть речи/...
	 */
	
	public ArrayList<Word> readParadigma() {
		ArrayList<Word> parad = new ArrayList<Word>();
		Charset charset = Charset.forName("UTF-8");
		Path source = Paths.get("morph.txt");
		try (BufferedReader wlreader = Files.newBufferedReader(source, charset)){
			String line = null;
			while((line = wlreader.readLine()) != null) {	//what should be done with every line
				if (!line.equals("_")) {
					String[] blocks = line.split("_Z_");
					// variables
					String word = "";
					// first block - the word, remove __
					if (blocks[0].length() > 2 && blocks[0].charAt(0) == '_' && blocks[0].charAt(1) == '_') {
						word = blocks[0].substring(2).replaceAll("_", " ");
					} else
						word = blocks[0];
					String[] properties = blocks[1].split("_");
					// parse the properties to the old dict system...
					switch(properties[0]) {
					case "сущ" :
						if (properties.length > 4) {
							Suschestvitelnoe s = new Suschestvitelnoe();
							s.setSlovo(word);
							s.setChastRechi("сущ");
							s.setChislo(filter(properties[2]));
							s.setPadezh(filter(properties[4]));
							s.setRod(properties[3]);
							parad.add(s);
						}
						break;
					case "прл" :
						Prilagatelnoe p = new Prilagatelnoe();
						p.setSlovo(word);
						p.setChastRechi("прил");
						if (properties[1].equals("ед")) {
							p.setChislo(filter(properties[1]));
							p.setPadezh(filter(properties[3]));
							p.setRod(properties[2]);
						}
						if (properties[1].equals("мн")) {
							p.setChislo(filter(properties[1]));
							p.setPadezh(filter(properties[2]));
							p.setRod("нет");
						}
						if (properties[1].equals("крат")) {
							p.setChislo(filter(properties[2]));
							p.setPadezh("им");
							p.setPolnoe(false);
							if (properties.length == 4) {
								p.setRod(properties[3]);
							} else {
								p.setRod("нет");
							}
						} else {
							p.setPolnoe(true);
						}
						if (properties[1].equals("прев")) {
							if (properties[2].equals("ед")) {
								p.setChislo(filter(properties[2]));
								p.setPadezh(filter(properties[4]));
								p.setRod(properties[3]);
							}
							if (properties[2].equals("мн")) {
								p.setChislo(filter(properties[2]));
								p.setPadezh(filter(properties[3]));
								p.setRod("нет");
							}
						}
						parad.add(p);
						break;
					case "гл" :
						// слово/кол-во слогов/ударный слог/часть речи/время/число/род/переходность
						//   0          1            2          3        4     5    6       7
						Glagol g = new Glagol();
						g.setSlovo(word);
						g.setChastRechi("глаг");
						if (!properties[3].equals("инф")) {
							if (properties[1].equals("несов"))
								g.setVremya(properties[3]);
							if (properties[3].equals("прош") && properties[1].equals("сов")) {
								g.setVremya("прошсов");
							}
							if (properties[3].equals("буд"))
								g.setVremya("буд");
							if (properties[3].equals("пов"))
								g.setVremya("пов");
							g.setChislo(filter(properties[4]));
							if (properties[3].equals("воз")) {
								if (properties[1].equals("несов"))
									g.setVremya(properties[4]);
								if (properties[4].equals("прош") && properties[1].equals("сов")) {
									g.setVremya("прошсов");
								}
								if(properties[4].equals("инф")) {
									g.setChislo("нет");
									g.setVremya("инф");
								}
							}	
						} else {
							g.setChislo("нет");
							g.setVremya("инф");
						}
						

						if (properties.length == 6)
							g.setRod(filter(properties[5]));
						else {
							if (properties[3].equals("пов")) {
								g.setChislo(filter(properties[4]));
								g.setRod("2лицо");
							} else if (properties[3].equals("воз")){
								if (properties[4].equals("инф")) {
									g.setChislo("нет");
									g.setVremya("инф");
								} else {
									g.setChislo(filter(properties[5]));
									if (properties.length == 7)
										g.setRod(properties[6]);
									else
										g.setRod("нет");
								}
							} else { 
								g.setRod("нет");
							}
								
						}
						if (properties[2].equals("перех") || (properties[2].equals("пер/не")))
							g.setPerekhod(true);
						else 
							g.setPerekhod(false);
						parad.add(g);
						break;
					
						/*
						 * Ввод местоимений исключительно вручную!
						 */
						
					case "нар" :
						// слово/кол-во слогов/ударный слог/часть речи
						//   0         1            2           3
						Narechie n = new Narechie();
						n.setSlovo(word);
						n.setChastRechi("нар");
						parad.add(n);
						break;
					case "прч" :
						// слово/кол-во слогов/ударный слог/часть речи/число/падеж/род/полное
						//   0         1            2          3         4     5    6     7
						Prilagatelnoe prich = new Prilagatelnoe();
						prich.setSlovo(word);
						prich.setChastRechi("прил");
						
						if (properties[4].equals("ед")) {
							prich.setChislo(filter(properties[4]));
							prich.setPadezh(filter(properties[6]));
							prich.setRod(properties[5]);
						}
						if (properties[4].equals("мн")) {
							prich.setChislo(filter(properties[4]));
							prich.setPadezh(filter(properties[5]));
							prich.setRod("нет");
						}
						if (properties[3].equals("страд") || properties[3].equals("воз")) {
							if (properties[5].equals("ед")) {
								prich.setChislo(filter(properties[5]));
								prich.setPadezh(filter(properties[7]));
								prich.setRod(properties[6]);
							}
							if (properties[5].equals("мн")) {
								prich.setChislo(filter(properties[5]));
								prich.setPadezh(filter(properties[6]));
								prich.setRod("нет");
							}
						}
						if (properties[1].equals("крат")) {
							prich.setPolnoe(false);
							if (properties[6].equals("ед")) {
								prich.setChislo(filter(properties[6]));
								prich.setPadezh("им");
								prich.setRod(properties[7]);
							}
							if (properties[6].equals("мн")) {
								prich.setChislo(filter(properties[6]));
								prich.setPadezh("им");
								prich.setRod("нет");
							}
						} else
							prich.setPolnoe(true);
						parad.add(prich);
						break;
					case "дееп" :
						Deeprichastie d = new Deeprichastie();
						d.setSlovo(word);
						d.setChastRechi("деепр");
						parad.add(d);
						break;
						// Ввод союзов, частиц, предлогов только вручную
					default : continue;
					}	
				}
			}
		} catch (IOException e) {
			System.err.println(e);
		}	
		return parad;
	}
	
	// returns the dictionary to be used further (with stress)
	public ArrayList<Word> readDictionary() {
		ArrayList<Word> oldDict = new ArrayList<Word>();
		Charset charset = Charset.forName("UTF-8");
		Path source = Paths.get("dict.txt");
		try (BufferedReader wlreader = Files.newBufferedReader(source, charset)){
			String line = null;
			while((line = wlreader.readLine()) != null) {	//what should be done with every line
				String[] blocks = line.split("/");			// "/" is the separator
				String word = blocks[0].trim();
				switch(blocks[3]){
				case "сущ" :
					// слово/кол-во слогов/ударный слог/часть речи/число/падеж/род
					//   0         1            2          3         4     5    6
					Suschestvitelnoe s = new Suschestvitelnoe();
					s.setSlovo(word);
					s.setUdarniySlog(Integer.parseInt(blocks[2]));
					s.setSlogov(Integer.parseInt(blocks[1]));
					s.setChastRechi("сущ");
					s.setChislo(blocks[4]);
					s.setPadezh(blocks[5]);
					s.setRod(blocks[6]);
					oldDict.add(s);
					break;
				case "прил" :
					// как в сущ, 7 = полное/неполное
					Prilagatelnoe p = new Prilagatelnoe();
					p.setSlovo(word);
					p.setUdarniySlog(Integer.parseInt(blocks[2]));
					p.setSlogov(Integer.parseInt(blocks[1]));
					p.setChastRechi("прил");
					p.setChislo(blocks[4]);
					p.setPadezh(blocks[5]);
					p.setRod(blocks[6]);
					String answer = blocks[7];
					if(answer.equals("да"))
						p.setPolnoe(true);
					else if(answer.equals("нет"))
						p.setPolnoe(false);
					oldDict.add(p);
					break;
				case "глаг" :
					// слово/кол-во слогов/ударный слог/часть речи/время/число/род/переходность
					//   0          1            2          3        4     5    6       7
					Glagol g = new Glagol();
					g.setSlovo(word);
					g.setUdarniySlog(Integer.parseInt(blocks[2]));
					g.setSlogov(Integer.parseInt(blocks[1]));
					g.setChastRechi("глаг");
					g.setVremya(blocks[4]);
					g.setChislo(blocks[5]);
					g.setRod(blocks[6]);
					String answer1 = blocks[7];
					if(answer1.equals("да"))
						g.setPerekhod(true);
					else if(answer1.equals("нет"))
						g.setPerekhod(false);
					oldDict.add(g);
					break;
				case "мест" :
					// слово/кол-во слогов/ударный слог/часть речи/число/род/падеж/грамм. разряд/разряд по значению
					//   0         1            2            3       4    5     6        7               8
					Mestoimenie m = new Mestoimenie();
					m.setSlovo(word);
					m.setUdarniySlog(Integer.parseInt(blocks[2]));
					m.setSlogov(Integer.parseInt(blocks[1]));
					m.setChastRechi("мест");
					m.setChislo(blocks[4]);
					m.setRod(blocks[5]);
					m.setPadezh(blocks[6]);
					m.setRazryadGr(blocks[7]);
					m.setRazryadZn(blocks[8]);
					oldDict.add(m);
					break;
				case "нар" :
					// слово/кол-во слогов/ударный слог/часть речи
					//   0         1            2           3
					Narechie n = new Narechie();
					n.setSlovo(word);
					n.setUdarniySlog(Integer.parseInt(blocks[2]));
					n.setSlogov(Integer.parseInt(blocks[1]));
					n.setChastRechi("нар");
					oldDict.add(n);
					break;
				case "прич" :
					// слово/кол-во слогов/ударный слог/часть речи/число/падеж/род/полное
					//   0         1            2          3         4     5    6     7
					Prichastie prich = new Prichastie();
					prich.setSlovo(word);
					prich.setUdarniySlog(Integer.parseInt(blocks[2]));
					prich.setSlogov(Integer.parseInt(blocks[1]));
					prich.setChastRechi("прич");
					prich.setChislo(blocks[4]);
					prich.setPadezh(blocks[5]);
					prich.setRod(blocks[6]);
					String answer2 = blocks[7];
					if(answer2.equals("да"))
						prich.setPolnoe(true);
					else if(answer2.equals("нет"))
						prich.setPolnoe(false);
					oldDict.add(prich);
					break;
				case "деепр" :
					Deeprichastie d = new Deeprichastie();
					d.setSlovo(word);
					d.setUdarniySlog(Integer.parseInt(blocks[2]));
					d.setSlogov(Integer.parseInt(blocks[1]));
					d.setChastRechi("деепр");
					oldDict.add(d);
					break;
				case "союз" :
					//4 razryad
					Soyuz sz = new Soyuz();
					sz.setSlovo(word);
					sz.setUdarniySlog(Integer.parseInt(blocks[2]));
					sz.setSlogov(Integer.parseInt(blocks[1]));
					sz.setRazryad(blocks[4]);
					sz.setChastRechi("союз");
					oldDict.add(sz);
					break;
				case "част" :
					Chastitsa ch = new Chastitsa();
					ch.setSlovo(word);
					ch.setUdarniySlog(Integer.parseInt(blocks[2]));
					ch.setSlogov(Integer.parseInt(blocks[1]));
					ch.setRazryad(blocks[4]);
					ch.setChastRechi("част");
					oldDict.add(ch);
					break;
				case "предл" :
					Predlog pr = new Predlog();
					pr.setSlovo(word);
					pr.setUdarniySlog(Integer.parseInt(blocks[2]));
					pr.setSlogov(Integer.parseInt(blocks[1]));
					pr.setChastRechi("предл");
					oldDict.add(pr);
					break;
				case "фикс" :
					PhrasalWord pw = new PhrasalWord();
					pw.setSlovo(word);
					pw.setUdarniySlog(Integer.parseInt(blocks[2]));
					pw.setSlogov(Integer.parseInt(blocks[1]));
					pw.setChastRechi("фикс");
					oldDict.add(pw);
					break;
				}
			}
		}catch (IOException e) {
			System.err.println(e);
		}
		return oldDict;
	}
	
	public void updateDictionary(ArrayList<Word> newDict) throws IOException{
		new PrintWriter("dict.txt").close();
		// new BufferedWriter(new FileWriter("dict.txt", true)))
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream("dict.txt"), Charset.forName("UTF-8"));
		try(PrintWriter out = new PrintWriter(outputStreamWriter)) {
			for(Word word : newDict){
				String line = "";
				switch(word.getChastRechi()){
				case "сущ" : 
					// слово/кол-во слогов/ударный слог/часть речи/число/падеж/род
					line = word.getSlovo()+"/"+ word.getSlogov()+"/"+word.getUdarniySlog()+"/"+ word.getChastRechi()+"/"
							+((Suschestvitelnoe) word).getChislo()+"/"+((Suschestvitelnoe) word).getPadezh()
								+"/"+((Suschestvitelnoe) word).getRod();
					out.println(line);
					break;
				case "прил" :
					boolean yn = ((Prilagatelnoe) word).isPolnoe();
					String ny = "";
					if(yn)
						ny = "да";
					else
						ny = "нет";
					line = word.getSlovo()+"/"+ word.getSlogov()+"/"+word.getUdarniySlog()+"/"+ word.getChastRechi()+"/"
							+((Prilagatelnoe) word).getChislo()+"/"+((Prilagatelnoe) word).getPadezh()
								+"/"+((Prilagatelnoe) word).getRod()+"/"+ny;
					out.println(line);
					break;
				case "глаг" : 
					// слово/кол-во слогов/ударный слог/часть речи/время/число/род/переходность
					boolean yn1 = ((Glagol) word).isPerekhod();
					String ny1 = "";
					if(yn1)
						ny1 = "да";
					else
						ny1 = "нет";
					line = word.getSlovo()+"/"+ word.getSlogov()+"/"+word.getUdarniySlog()+"/"+ word.getChastRechi()+"/"
							+((Glagol) word).getVremya()+"/"+((Glagol) word).getChislo()+"/"+((Glagol) word).getRod()+"/"+ny1;
					out.println(line);
					break;
				case "мест" : 
					// слово/кол-во слогов/ударный слог/часть речи/число/род/падеж/грамм. разряд/разряд по значению
					line = word.getSlovo()+"/"+ word.getSlogov()+"/"+word.getUdarniySlog()+"/"+ word.getChastRechi()+"/"
							+((Mestoimenie) word).getChislo()+"/"+((Mestoimenie) word).getRod()+"/"+((Mestoimenie) word).getPadezh()
								+"/"+((Mestoimenie) word).getRazryadGr()+"/"+((Mestoimenie) word).getRazryadZn();
					out.println(line);
					break;
				case "нар" :
					// слово/кол-во слогов/ударный слог/часть речи
					line = word.getSlovo()+"/"+ word.getSlogov()+"/"+word.getUdarniySlog()+"/"+ word.getChastRechi();
					out.println(line);
					break;
				case "прич" :
					// слово/кол-во слогов/ударный слог/часть речи/число/падеж/род
					boolean yn2 = ((Prichastie) word).isPolnoe();
					String ny2 = "";
					if(yn2)
						ny2 = "да";
					else
						ny2 = "нет";
					line = word.getSlovo()+"/"+ word.getSlogov()+"/"+word.getUdarniySlog()+"/"+ word.getChastRechi()+"/"
							+((Prichastie) word).getChislo()+"/"+((Prichastie) word).getPadezh()
								+"/"+((Prichastie) word).getRod()+"/"+ny2;
					out.println(line);
					break;
				case "деепр" :
					// слово/кол-во слогов/ударный слог/часть речи
					line = word.getSlovo()+"/"+ word.getSlogov()+"/"+word.getUdarniySlog()+"/"+ word.getChastRechi();
					out.println(line);
					break;
				case "союз" :
					//4 razryad
					line = word.getSlovo()+"/"+ word.getSlogov()+"/"+word.getUdarniySlog()+"/"+ word.getChastRechi()+"/"+((Soyuz) word).getRazryad();
					out.println(line);
					break;
				case "част" :
					line = word.getSlovo()+"/"+ word.getSlogov()+"/"+word.getUdarniySlog()+"/"+ word.getChastRechi()+"/"+((Chastitsa) word).getRazryad();
					out.println(line);
					break;
				case "предл" :
					line = word.getSlovo()+"/"+ word.getSlogov()+"/"+word.getUdarniySlog()+"/"+ word.getChastRechi();
					out.println(line);
					break;
				case "фикс" :
					line = word.getSlovo()+"/"+ word.getSlogov()+"/"+word.getUdarniySlog()+"/"+word.getChastRechi();
					out.println(line);
					break;
				}	
			}
			System.out.println("Словарь обновлён.");
		}
	}

	public ArrayList<Syntax> readSyntaxDict() {
		ArrayList<Syntax> oldSyntaxDict = new ArrayList<Syntax>();
		Charset charset = Charset.forName("UTF-8");
		Path source = Paths.get("syntax.txt");
		// "C:\\Users\\Michael\\workspace\\Autopoet\\syntax.txt"
		try (BufferedReader wlreader = Files.newBufferedReader(source, charset)){
			String line = null;
			Syntax s = new Syntax();
			int zeros = 0;
			while((line = wlreader.readLine()) != null) {	//what should be done with every line
				String[] blocks = line.split("/");			// "/" is the separator				
				Parameter p = new Parameter();
				String[] infos = new String[7];
				String number = blocks[0].replace("\uFEFF", "");
				
				if(number.equals("0")){
					zeros++;
					if (zeros > 1){
						oldSyntaxDict.add(s);
						s = new Syntax();
					}
				}
				String first = blocks[1].replace("\uFEFF", "");
				infos[6] = number;
				if(first.equals("сущ")||first.equals("прил")||first.equals("прич")){
					infos[1] = blocks[2];
					infos[2] = blocks[3];
					infos[3] = blocks[4];
					if(!first.equals("сущ")){
						infos[4] = blocks[5];
					}
				}
				if(first.equals("союз")||first.equals("част")||first.equals("предл")||first.equals("знак")){
						infos[1] = blocks[2];
				}
				if(first.equals("глаг")){
					infos[1] = blocks[2];
					infos[2] = blocks[3];
					infos[3] = blocks[4];
					infos[4] = blocks[5];
				}
				if(first.equals("мест")){
					infos[1] = blocks[2];
					infos[2] = blocks[3];
					infos[3] = blocks[4];
					infos[4] = blocks[5];
					infos[5] = blocks[6];
				}
				if(first.equals("фикс")){
					infos[1] = blocks[2];
					infos[2] = blocks[3];
					infos[3] = blocks[4];
				}
				infos[0] = first;
				p.setPriznaki(infos);
				s.addParameter(p);
			}
			//for the last line
			oldSyntaxDict.add(s);
		}catch (IOException e) {
			System.err.println(e);
		}
		return oldSyntaxDict;
	}
	
	public void updateSyntaxDict(ArrayList<Syntax> newSyntaxDict) throws IOException{
		new PrintWriter("syntax.txt").close();
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream("syntax.txt"), Charset.forName("UTF-8"));
		try(PrintWriter out = new PrintWriter(outputStreamWriter)) {
			int sn = 0;
			for(Syntax s : newSyntaxDict){
				
				for(Parameter p : s.getStructure()){					
					String line = p.getPriznaki()[6] + "/";
					for(int i = 0; i < 6; i++){
						line += p.getPriznaki()[i];
						if(i != 5){
							line += "/";
						}
					}
					out.println(line);
				}
			}
			System.out.println("База синтаксических структур обновлена.");
		}
	}

	public String filter(String in) {
		String out = in;
		return out.replace("-е", "лицо").replace("мн", "множ").replace("тв", "твор").replace("пр", "предл");
	}
	
	public void countWordsByStress(ArrayList<Word> slovar) {
		System.out.println("Кол-во слогов по горизонтали, ударный слог по вертикали:");
		System.out.println("        0       1       2       3       4       5       6       7");
		int[][] matrix = new int[8][8];
		for (Word w : slovar) {
			int slogov = w.getSlogov();
			int udSlog = w.getUdarniySlog();
			if (slogov < 8 && udSlog < 8)
				matrix[udSlog][slogov]++;
		}
		for (int i = 0; i < 8; i++) {
			System.out.print(i + "\t");
			for (int j = 0; j < 8; j++) {
				System.out.print(matrix[i][j] + "\t");
			}
			System.out.print("\n");
		}
	}
	
	public void countWordsByForm(ArrayList<Syntax> sd, ArrayList<Word> dict, FormGenerator fg){
		System.out.println("Подсчёт распределения слов по грамматическим признакам:");
		ArrayList<String[]> combos = new ArrayList<String[]>();
		ArrayList<Integer> numS = new ArrayList<Integer>();
		ArrayList<Integer> numD = new ArrayList<Integer>();
		Charset charset = Charset.forName("UTF-8");
		Path source = Paths.get("syntax.txt");
		// "C:\\Users\\Michael\\workspace\\Autopoet\\syntax.txt"
		try (BufferedReader wlreader = Files.newBufferedReader(source, charset)){
			String line = null;
			while((line = wlreader.readLine()) != null) {	//what should be done with every line
				String[] blocks = line.split("/");			// "/" is the separator		
				Parameter p = new Parameter();
				// вектор стрингов, в котором есть вся информация о классификации слова по грамматическим признакам
				String[] infos = new String[6];
				
				// сокращение части речи
				String first = blocks[1].replace("\uFEFF", "");
				
				if (first.equals("знак") || first.equals("нар") || first.equals("деепр") || first.equals("фикс") || first.equals("част") || first.equals("союз")|| first.equals("предл"))
					continue;
					
				// в зависимости от части речи, не считывай все признаки (многие из них нулевые)
				if(first.equals("сущ")||first.equals("прил")||first.equals("прич")){
					infos[1] = blocks[2];
					infos[2] = blocks[3];
					infos[3] = blocks[4];
					if(!first.equals("сущ")){
						infos[4] = blocks[5];
					}
				}
				if(first.equals("глаг")){
					infos[1] = blocks[2];
					infos[2] = blocks[3];
					infos[3] = filter(blocks[4]);
					infos[4] = blocks[5];
				}
				if(first.equals("мест")){
					infos[1] = blocks[2];
					infos[2] = blocks[3];
					infos[3] = blocks[4];
					infos[4] = blocks[5];
					infos[5] = blocks[6];
				}
				if(first.equals("фикс")){
					infos[1] = blocks[2];
					infos[2] = blocks[3];
					infos[3] = blocks[4];
				}
				infos[0] = first;
				p.setPriznaki(infos);
				
				// Если список комбинаций ещё не содержит данной...
				if (!combos.contains(infos)) {
					// ...добавить данную комбинацию и поставить счётчик 1
					combos.add(infos);
					Integer count = 1;
					numS.add(count);
					// Также посчитать, сколько подходящих по грамматике слов есть в словаре
					int gramMatches = fg.findSuitableWords(p, dict).size();
					numD.add(gramMatches);
				}
				
				// Если в списке комбинаций уже есть данная комбинация...
				if (combos.contains(infos)) {
					int n = combos.indexOf(infos);
					// ...считать старые показания счётчика
					int oldCount = numS.get(n).intValue();
					// увеличить на 1
					Integer newCount = oldCount++;
					// изменить показания счётчика
					numS.set(n, newCount);
				}
			}
		}catch (IOException e) {
			System.err.println(e);
		}

		// вывод кол-ва слов с определёнными комбинациями грамматических признаков
		ArrayList<String> lineReps = new ArrayList<String>();
		System.out.println("Вектор грамматических признаков         Синтаксис         Словарь");
		for (String[] str : combos) {
			String lineRep = "";
			for (int i = 0; i < 6; i++) {
				lineRep += str[i] + "/";
			}
			if (!lineReps.contains(lineRep))
				lineReps.add(lineRep);
			else
				continue;
			int ind = combos.indexOf(str);
			System.out.println(lineRep + "\t" + numS.get(ind).intValue() + "\t" + numD.get(ind).intValue());
		}	
		
	}

	public double min(double[] numbers) {
		double ans = numbers[0];
		for(int i = 1; i < numbers.length; i++){
			if(numbers[i-1] >= numbers[i]){
				ans = numbers[i];
			}
		}
		return ans;
	}
	
	public int indexOfDouble(double d, double[] array) {
		int index = -1;
		for (int i = 0; i < array.length; i++) {
			if (array[i] == d) {
				index = i;
				break;
			}
		}
		return index;
	}
	
	public boolean enthaelt(ArrayList<String[]> list, String[] string) {
		boolean ans = false;
		for (String[] line : list) {
			if (!string.equals(null) && !line.equals(null)) {
				if (equalStringVectors(string, line)) {
					ans = true;
					break;
				} 
			}
		}
		return ans;
	}
	
	public boolean equalStringVectors(String[] one, String[] two) {
		boolean ans = false;

			int t = 0;
			for (int i = 0; i < one.length; i++) {
				if (one[i].equals(two[i]))
					t++;
				else
					break;
			}
			if (t == one.length)
				ans = true;
			else
				ans = false;

		return ans;
	}
	
}
