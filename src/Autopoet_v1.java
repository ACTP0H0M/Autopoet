package autopoet;
import java.io.*;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.ArrayList;

public class Autopoet_v1 {
	
	static DataManager dm = new DataManager();
	static FormGenerator fg = new FormGenerator();
	static SyntaxGenerator sg = new SyntaxGenerator();
	static Scanner input = new Scanner(System.in, "Cp1251");
	static NeuralNet nn;
	static ArrayList<Word> dictionary = new ArrayList<Word>();
	static ArrayList<Word> paradigma = new ArrayList<Word>();
	static ArrayList<Syntax> syntaxDictionary = new ArrayList<Syntax>();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			PrintStream ps = new PrintStream(System.out, true, "UTF-8");
			System.setOut(ps);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			
		//Загрузка базы данных
		System.out.println("Добро пожаловать в АвтоПоэт!");
		System.out.println("Загрузка словаря с размеченными ударениями...");
		dictionary = dm.readDictionary();
		System.out.println("Словарных форм с ударениями: " + dictionary.size());
		System.out.println("Загрузка синтаксической базы данных...");
		syntaxDictionary = dm.readSyntaxDict();
		System.out.println("Синтаксических структур: " + syntaxDictionary.size());
		fg.initHash();
		// sg.initHash();
		
		try {
			 // создание файлового объекта
		      File file = new File("brain.ser");
		      // если файла ещё нет в файловой системе...
		      if (file.createNewFile()){
		    	double[][] first = new double[40][60];
		  		double[][] second = new double[60][15];
		  		double[][] third = new double[15][3];
		  		double eta = 0.5;
		  		nn = new NeuralNet(first, second, third, eta);
		        System.out.println("Новый файл с нейронной сетью создан");
		        nn.initNewNet();
		        nn.randomWeights();

		      }else{
		        System.out.println("Нейронная сеть найдена");
		        FileInputStream fileIn = new FileInputStream(file);
			       ObjectInputStream in = new ObjectInputStream(fileIn);
			       NeuralNet nn_read = (NeuralNet) in.readObject();
			       nn = new NeuralNet(nn_read.first, nn_read.second, nn_read.third, nn_read.eta);
			       System.out.println("Нейронная сеть загружена");
			       in.close();
			       fileIn.close();
		      }

	    	} catch (IOException e) {
		      e.printStackTrace();
		} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    

		
		System.out.println("Загружено.");
		
		//Индикатор завершения работы
		boolean exit = false;

		
		while(exit == false){
			int mode = 0;
			try {
				System.out.println(">>>>Выберите режим работы:<<<<");
				System.out.println("1 = ввод новых слов вручную");
				System.out.println("2 = сочинение стихов");
				System.out.println("3 = статистика");
				System.out.println("4 = ввод синтаксических структур вручную");
				System.out.println("5 = простановка ударений");
				System.out.println("6 = создание синтаксиса из предложения");
				System.out.println("7 = зачистка синтаксисов");
				System.out.println("0 = выход");
				mode = input.nextInt();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				mode = 0;
				System.out.println("Неправильный ввод. Перезапустите программу.");
				break;
			}
			switch(mode){
			case 1 : newWordInput(); break;
			case 2 : {fg.actualLine = 0;
						fg.actualBlock = 0;
						fg.composeBeta(dictionary, syntaxDictionary, nn); break;
			}
			case 3 : statistics(); break;
			case 4 : newSyntaxInput(); break;
			case 5 : stressInput(); break;
			case 6 : syntaxGen(); break;
			case 7 : syntaxCleanse(); break;
			case 0 : exit = true; break;
			default : System.out.println("Неверный ввод."); continue;
			}
		}
		
		//Обновление базы данных
		
		try {
			dm.updateDictionary(dictionary);
			dm.updateSyntaxDict(syntaxDictionary);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// serialize NeuralNet
		try {
	         FileOutputStream fileOut =
	         new FileOutputStream("brain.ser");
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(nn);
	         out.close();
	         fileOut.close();
	         System.out.printf("Нейронная сеть сохранена.");
	      } catch (IOException i) {
	         i.printStackTrace();
	      }
		
	}
	
	static void syntaxCleanse() {
		Scanner input = new Scanner(System.in);
		System.out.println("Введите длину синтаксиса, который хотите проверить:");
		int dlina = input.nextInt();
		String[] partsOfSpeech = new String[dlina];
		System.out.println("Введите для каждой позиции часть речи или 'знак':");
		for (int i = 0; i < dlina; i++) {
			System.out.print(i + "/");
			String chast_rechi = input.next();
			partsOfSpeech[i] = chast_rechi;
		}
		Syntax search = new Syntax();
		boolean found = false;
		for (Syntax s: syntaxDictionary) {
			if (s.getStructure().size() != dlina)
				continue;
			int j = 0;
			for (Parameter p: s.getStructure()) {
				if (!p.getPriznaki()[0].equals(partsOfSpeech[j]))
					break;
				else
					j++;
				if (j == dlina) {
					search = s;
					found = true;
				}
			}
			if (found)
				break;
		}
		if (found == false)
			System.out.println("Синтаксис с данными параметрами не был найден.");
		else {
			System.out.println("Соответствует ли искомый синтаксис найденному? (д/н)");
			boolean passt = false;
			for (Parameter p: search.getStructure()) {
				p.printPriznaki();
			}
			String ans = input.next();
			if (ans.equals("д")) {
				passt = true;
			} else {
				passt = false;
			}
			if (passt) {
				System.out.println("Удалить данный синтаксис из базы данных? (д/н)");
				String ans1 = input.next();
				if (ans1.equals("д")) {
					syntaxDictionary.remove(search);
				}
			}
		}
	}
	
	
	static void syntaxGen() {
		if (paradigma.size() == 0) {
			System.out.println("Загрузка полной парадигмы русского языка...");
			paradigma = dm.readParadigma();
		}
		Scanner input = new Scanner(System.in);
		// Объект для синтаксиса
		Syntax syntax = new Syntax();
		// Ввод предложения (например, просто скопировать из инета)
		System.out.println("Введите предложение-образец:");
		String satz = input.nextLine();
		// Разделение по пробелам
		String[] bSatz = satz.split("\\s+");
		System.out.println("Количество слов в предложении: " + bSatz.length);
		int[] p = new int[bSatz.length];
		int punct = 0;
		for (int i = 0; i < bSatz.length; i++) {
			bSatz[i] = bSatz[i].trim();
			bSatz[i] = bSatz[i].toLowerCase();
			if (bSatz[i].contains(".")) {
				punct++;
				p[i] = 1;
			}
			if (bSatz[i].contains(",")) {
				punct++;
				p[i] = 1;
			}
			if (bSatz[i].contains("!")) {
				punct++;
				p[i] = 1;
			}
			if (bSatz[i].contains("?")) {
				punct++;
				p[i] = 1;
			}
			if (bSatz[i].contains(":")) {
				punct++;
				p[i] = 1;
			}
			if (bSatz[i].contains(";")) {
				punct++;
				p[i] = 1;
			}
			if (bSatz[i].contains("...")) {
				punct++;
				p[i] = 1;
			}
		}
		// Множество токенов
		String[] fSatz = new String[bSatz.length + punct];
		boolean[] isZnak = new boolean[bSatz.length + punct];
		int target = 0;
		for (int i = 0; i < bSatz.length; i++) {
			if (p[i] == 1) {
				target++;
				fSatz[target] = bSatz[i].substring(bSatz[i].toCharArray().length - 1);
				isZnak[target] = true;
				fSatz[target - 1] = bSatz[i].substring(0, bSatz[i].toCharArray().length - 1);
			} else {
				fSatz[target] = bSatz[i];
			}
			target++;
		}
		
		// После токенизации предложения можно начать распознание слов
		for (int i = 0; i < fSatz.length; i++) {
			Parameter par = new Parameter();
			String[] prizn = new String[7];
			prizn[6] = Integer.toString(i);
			// Если это знак препинания...
			if (isZnak[i]) {
				prizn[0] = "знак";
				prizn[1] = fSatz[i];
				par.setPriznaki(prizn);
				syntax.addParameter(par);
			} else {	// Если это слово...
				// ...находится в парадигме...
				boolean inDatabase = false;
				Word match = new Word();
				
				// От порядка поиска здесь будет зависеть, какой ввод будет предпочтительнее: ручной или автоматический
				
				for (Word word : dictionary) {
					if (fSatz[i].equals(word.getSlovo())) {
						inDatabase = true;
						match = word;
						break;
					}
				}
				for (Word word : paradigma) {
					if (fSatz[i].equals(word.getSlovo())) {
						inDatabase = true;
						match = word;
						break;
					}
				}
				
				
				if (inDatabase) {
					// ...то вывести его признаки на экран и попросить подтверждение
					System.out.println("Подтвердите признаки слова (д/н): " + match.getSlovo());
					match.shortRepresentation();
					// если характеристики подтверждены, заполнить параметр и добавить в синтаксис
					String otvet = input.next();
					if (otvet.equals("д")) {
						// перевести данные в форму вектора признаков для синтаксиса и записать
						switch(match.getChastRechi()){
						case "сущ" : {
							prizn[0] = "сущ";
							prizn[1] = ((Suschestvitelnoe) match).getPadezh();
							prizn[2] = ((Suschestvitelnoe) match).getRod();
							prizn[3] = ((Suschestvitelnoe) match).getChislo();
						} break;
						case "глаг" : {
							prizn[0] = "глаг";
							prizn[1] = ((Glagol) match).getVremya();
							prizn[2] = ((Glagol) match).getChislo();
							prizn[3] = ((Glagol) match).getRod();
							if(((Glagol) match).isPerekhod()){
								prizn[4] = "да";
							}else{
								prizn[4] = "нет";
							}
						} break;
						case "прил" : {
							prizn[0] = "прил";
							prizn[1] = ((Prilagatelnoe) match).getPadezh();
							prizn[2] = ((Prilagatelnoe) match).getRod();
							prizn[3] = ((Prilagatelnoe) match).getChislo();
							if(((Prilagatelnoe) match).isPolnoe()){
								prizn[4] = "да";
							}else{
								prizn[4] = "нет";
							}
						} break;
						case "мест" : {
							prizn[0] = "мест";
							prizn[1] = ((Mestoimenie) match).getRazryadGr();
							prizn[2] = ((Mestoimenie) match).getRazryadZn();
							prizn[3] = ((Mestoimenie) match).getPadezh();
							prizn[4] = ((Mestoimenie) match).getRod();
							prizn[5] = ((Mestoimenie) match).getChislo();
						} break;
						case "прич" : {
							prizn[0] = "прич";
							prizn[1] = ((Prichastie) match).getPadezh();
							prizn[2] = ((Prichastie) match).getRod();
							prizn[3] = ((Prichastie) match).getChislo();
							if(((Prichastie) match).isPolnoe()){
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
							prizn[1] = match.getSlovo();
						} break;
						case "част" : {
							prizn[0] = "част";
							prizn[1] = match.getSlovo();
						} break;
						case "предл" : {
							prizn[0] = "предл";
							prizn[1] = match.getSlovo();
						} break;
						default : System.out.println("ERROR");
						}
						par.setPriznaki(prizn);
						syntax.addParameter(par);
					} else {
						// если признаки слова неверные...
						System.out.println("Пожалуйста, введите признаки слова вручную.");
						System.out.println("Часть речи: ");
						String answer = input.next();
						switch(answer){
						case "сущ" : {
							prizn[0] = "сущ";
							System.out.println("Падеж: ");
							answer = input.next();
							prizn[1] = answer;
							System.out.println("Род: ");
							answer = input.next();
							prizn[2] = answer;
							System.out.println("Число: ");
							answer = input.next();
							prizn[3] = answer;
						} break;
						case "глаг" : {
							prizn[0] = "глаг";
							System.out.println("Время (+прошсов/пов/буд!!!): ");
							answer = input.next();
							prizn[1] = answer;
							System.out.println("Число: ");
							answer = input.next();
							prizn[2] = answer;
							System.out.println("Род/лицо: ");
							answer = input.next();
							prizn[3] = answer;
							System.out.println("Переходный? (да/нет): ");
							answer = input.next();
							prizn[4] = answer;
						} break;
						case "прил" : {
							prizn[0] = "прил";
							System.out.println("Падеж: ");
							answer = input.next();
							prizn[1] = answer;
							System.out.println("Род: ");
							answer = input.next();
							prizn[2] = answer;
							System.out.println("Число: ");
							answer = input.next();
							prizn[3] = answer;
							System.out.println("Полное? (да/нет): ");
							answer = input.next();
							prizn[4] = answer;
						} break;
						case "мест" : {
							prizn[0] = "мест";
							System.out.println("Грамматический разряд: ");
							answer = input.next();
							prizn[1] = answer;
							System.out.println("Разряд по значению: ");
							answer = input.next();
							prizn[2] = answer;
							System.out.println("Падеж: ");
							answer = input.next();
							prizn[3] = answer;
							System.out.println("Род: ");
							answer = input.next();
							prizn[4] = answer;
							System.out.println("Число: ");
							answer = input.next();
							prizn[5] = answer;
						} break;
						case "прич" : {
							prizn[0] = "прич";
							System.out.println("Падеж: ");
							answer = input.next();
							prizn[1] = answer;
							System.out.println("Род: ");
							answer = input.next();
							prizn[2] = answer;
							System.out.println("Число: ");
							answer = input.next();
							prizn[3] = answer;
							System.out.println("Полное? (да/нет): ");
							answer = input.next();
							prizn[4] = answer;
						} break;
						case "нар" : {
							prizn[0] = "нар";
						} break;
						case "деепр" : {
							prizn[0] = "деепр";
						} break;
						case "союз" : {
							prizn[0] = "союз";
							System.out.println("Ввведите само слово: ");
							answer = input.next();
							prizn[1] = answer;
						} break;
						case "част" : {
							prizn[0] = "част";
							System.out.println("Ввведите само слово: ");
							answer = input.next();
							prizn[1] = answer;
						} break;
						case "предл" : {
							prizn[0] = "предл";
							System.out.println("Ввведите само слово: ");
							answer = input.next();
							prizn[1] = answer;
						} break;
						case "фикс" : {
							PhrasalWord pw = new PhrasalWord();
							prizn[0] = "фикс";
							pw.setChastRechi("фикс");
							prizn[1] = fSatz[i];
							pw.setSlovo(fSatz[i]);
							System.out.println("Количество слогов:");
							answer = input.next();
							pw.setSlogov(Integer.parseInt(answer));
							prizn[2] = answer;
							System.out.println("Номер ударного слога:");
							answer = input.next();
							pw.setUdarniySlog(Integer.parseInt(answer));
							prizn[3] = answer;
							dictionary.add(pw);
						} break;
						default : System.out.println("ERROR");
						}
						par.setPriznaki(prizn);
						syntax.addParameter(par);
					}
					
				} else {	// если слова нет в базе данных вообще
					System.out.println("К сожалению, слово *" + fSatz[i] + "* не было найдено в базе данных. Пожалуйста, введите вручную.");
					System.out.println("Часть речи: ");
					String answer = input.next();
					switch(answer){
					case "сущ" : {
						prizn[0] = "сущ";
						System.out.println("Падеж: ");
						answer = input.next();
						prizn[1] = answer;
						System.out.println("Род: ");
						answer = input.next();
						prizn[2] = answer;
						System.out.println("Число: ");
						answer = input.next();
						prizn[3] = answer;
					} break;
					case "глаг" : {
						prizn[0] = "глаг";
						System.out.println("Время (+прошсов/пов/буд!!!): ");
						answer = input.next();
						prizn[1] = answer;
						System.out.println("Число: ");
						answer = input.next();
						prizn[2] = answer;
						System.out.println("Род/лицо: ");
						answer = input.next();
						prizn[3] = answer;
						System.out.println("Переходный? (да/нет): ");
						answer = input.next();
						prizn[4] = answer;
					} break;
					case "прил" : {
						prizn[0] = "прил";
						System.out.println("Падеж: ");
						answer = input.next();
						prizn[1] = answer;
						System.out.println("Род: ");
						answer = input.next();
						prizn[2] = answer;
						System.out.println("Число: ");
						answer = input.next();
						prizn[3] = answer;
						System.out.println("Полное? (да/нет): ");
						answer = input.next();
						prizn[4] = answer;
					} break;
					case "мест" : {
						prizn[0] = "мест";
						System.out.println("Грамматический разряд: ");
						answer = input.next();
						prizn[1] = answer;
						System.out.println("Разряд по значению: ");
						answer = input.next();
						prizn[2] = answer;
						System.out.println("Падеж: ");
						answer = input.next();
						prizn[3] = answer;
						System.out.println("Род: ");
						answer = input.next();
						prizn[4] = answer;
						System.out.println("Число: ");
						answer = input.next();
						prizn[5] = answer;
					} break;
					case "прич" : {
						prizn[0] = "прич";
						System.out.println("Падеж: ");
						answer = input.next();
						prizn[1] = answer;
						System.out.println("Род: ");
						answer = input.next();
						prizn[2] = answer;
						System.out.println("Число: ");
						answer = input.next();
						prizn[3] = answer;
						System.out.println("Полное? (да/нет): ");
						answer = input.next();
						prizn[4] = answer;
					} break;
					case "нар" : {
						prizn[0] = "нар";
					} break;
					case "деепр" : {
						prizn[0] = "деепр";
					} break;
					case "союз" : {
						prizn[0] = "союз";
						System.out.println("Ввведите само слово: ");
						answer = input.next();
						prizn[1] = answer;
					} break;
					case "част" : {
						prizn[0] = "част";
						System.out.println("Ввведите само слово: ");
						answer = input.next();
						prizn[1] = answer;
					} break;
					case "предл" : {
						prizn[0] = "предл";
						System.out.println("Ввведите само слово: ");
						answer = input.next();
						prizn[1] = answer;
					} break;
					case "знак" : {
						prizn[0] = "знак";
						System.out.println("Ввведите сам знак: ");
						answer = input.next();
						prizn[1] = answer;
					} break;
					case "фикс" : {
						PhrasalWord pw = new PhrasalWord();
						prizn[0] = "фикс";
						pw.setChastRechi("фикс");
						prizn[1] = fSatz[i];
						pw.setSlovo(fSatz[i]);
						System.out.println("Количество слогов:");
						answer = input.next();
						pw.setSlogov(Integer.parseInt(answer));
						prizn[2] = answer;
						System.out.println("Номер ударного слога:");
						answer = input.next();
						pw.setUdarniySlog(Integer.parseInt(answer));
						prizn[3] = answer;
						dictionary.add(pw);
					} break;
					default : System.out.println("ERROR");
					}
					par.setPriznaki(prizn);
					syntax.addParameter(par);
				}
				
			}
			
			// записать данные в объект Параметр, добавить Параметр в Синтаксис ????

			
		}
		
		syntaxDictionary.add(syntax);

	}
	
	static void newSyntaxInput() {
		Syntax syntax = new Syntax();
		Scanner input = new Scanner(System.in);
		System.out.println("Сколько синтаксических ячеек в предложении (слова и знаки препинания)?");
		int ans = input.nextInt();
		for (int i = 0; i < ans; i++) {
			Parameter par = new Parameter();
			String[] prizn = new String[7];
			prizn[6] = Integer.toString(i);
			System.out.println("Часть речи: ");
			String answer = input.next();
			switch(answer){
			case "сущ" : {
				prizn[0] = "сущ";
				System.out.println("Падеж: ");
				answer = input.next();
				prizn[1] = answer;
				System.out.println("Род: ");
				answer = input.next();
				prizn[2] = answer;
				System.out.println("Число: ");
				answer = input.next();
				prizn[3] = answer;
			} break;
			case "глаг" : {
				prizn[0] = "глаг";
				System.out.println("Время (+прошсов/пов/буд!!!): ");
				answer = input.next();
				prizn[1] = answer;
				System.out.println("Число: ");
				answer = input.next();
				prizn[2] = answer;
				System.out.println("Род/лицо: ");
				answer = input.next();
				prizn[3] = answer;
				System.out.println("Переходный? (да/нет): ");
				answer = input.next();
				prizn[4] = answer;
			} break;
			case "прил" : {
				prizn[0] = "прил";
				System.out.println("Падеж: ");
				answer = input.next();
				prizn[1] = answer;
				System.out.println("Род: ");
				answer = input.next();
				prizn[2] = answer;
				System.out.println("Число: ");
				answer = input.next();
				prizn[3] = answer;
				System.out.println("Полное? (да/нет): ");
				answer = input.next();
				prizn[4] = answer;
			} break;
			case "мест" : {
				prizn[0] = "мест";
				System.out.println("Грамматический разряд: ");
				answer = input.next();
				prizn[1] = answer;
				System.out.println("Разряд по значению: ");
				answer = input.next();
				prizn[2] = answer;
				System.out.println("Падеж: ");
				answer = input.next();
				prizn[3] = answer;
				System.out.println("Род: ");
				answer = input.next();
				prizn[4] = answer;
				System.out.println("Число: ");
				answer = input.next();
				prizn[5] = answer;
			} break;
			case "прич" : {
				prizn[0] = "прич";
				System.out.println("Падеж: ");
				answer = input.next();
				prizn[1] = answer;
				System.out.println("Род: ");
				answer = input.next();
				prizn[2] = answer;
				System.out.println("Число: ");
				answer = input.next();
				prizn[3] = answer;
				System.out.println("Полное? (да/нет): ");
				answer = input.next();
				prizn[4] = answer;
			} break;
			case "нар" : {
				prizn[0] = "нар";
			} break;
			case "деепр" : {
				prizn[0] = "деепр";
			} break;
			case "союз" : {
				prizn[0] = "союз";
				System.out.println("Ввведите само слово: ");
				answer = input.next();
				prizn[1] = answer;
			} break;
			case "част" : {
				prizn[0] = "част";
				System.out.println("Ввведите само слово: ");
				answer = input.next();
				prizn[1] = answer;
			} break;
			case "предл" : {
				prizn[0] = "предл";
				System.out.println("Ввведите само слово: ");
				answer = input.next();
				prizn[1] = answer;
			} break;
			case "знак" : {
				prizn[0] = "знак";
				System.out.println("Ввведите сам знак: ");
				answer = input.next();
				prizn[1] = answer;
			} break;
			case "фикс" : {
				PhrasalWord pw = new PhrasalWord();
				prizn[0] = "фикс";
				pw.setChastRechi("фикс");
				System.out.println("Ввведите само слово: ");
				answer = input.next();
				pw.setSlovo(answer);
				prizn[1] = answer;
				System.out.println("Количество слогов:");
				answer = input.next();
				pw.setSlogov(Integer.parseInt(answer));
				prizn[2] = answer;
				System.out.println("Номер ударного слога:");
				answer = input.next();
				pw.setUdarniySlog(Integer.parseInt(answer));
				prizn[3] = answer;
				dictionary.add(pw);
			} break;
			default : System.out.println("ERROR");
			}
			par.setPriznaki(prizn);
			syntax.addParameter(par);
		}
		
		syntaxDictionary.add(syntax);

		
	}
	
	static void statistics(){
		System.out.println("============================");
		System.out.println("СТАТИСТИКА:");
		System.out.println("Словарных форм: " + dictionary.size());
		System.out.println("Синтаксических структур: " + syntaxDictionary.size());
		double asaw = fg.averageSylsAWord(dictionary);
		System.out.println("Слогов/слово: " + asaw);
		SyntaxGenerator sg = new SyntaxGenerator();
		double awas = sg.averageWordsASentence(syntaxDictionary);
		System.out.println("Слов/предложение: " + awas);
		System.out.println("Распределение слов по ударениям:");
		dm.countWordsByStress(dictionary);
		dm.countWordsByForm(syntaxDictionary, dictionary, fg);
		System.out.println("============================");
	}
	
	static void newWordInput() {
		//Пока не будет введено новое слово, дальнейшую инормацию вводить нет смысла
		String word = "";
		
		boolean again = true;
		
		while (again) {
			System.out.println("Введите слово: ");
			//...запрашивается ввод...
			word = input.next();
			//...введённое слово ищется в словаре...
			for (Word w : dictionary) {
				if (w.getSlovo().equals(word)) {
					System.out.println("ПРЕДУПРЕЖДЕНИЕ: Данное слово уже находится в словаре.");
					w.shortRepresentation();
					System.out.println("Вы всё равно хотите его ввести? 1 = да, 0 = нет");
					if(input.nextInt()==1){
						again = false;
						break;
					}else{
						again = true;
						break;
					}
				}
				again = false;
			}

		}
		System.out.println("Количество слогов:");
		int syls = input.nextInt();
		System.out.println("Номер ударного слога: ");
		int syl = input.nextInt();
		System.out.println("Часть речи:");
		String partOfSpeech = input.next();
		
		switch(partOfSpeech){
		
		
		case "сущ" :
			
			//Выбор между вводом всех или отдельной формы существительного
			System.out.println("Для ввода всех форм существительного нажмите 0, для ввода отдельной формы (например для слов без множ/ед числа) - 1");
			
			int tmp = input.nextInt();
			
			if (tmp == 1) {
				Suschestvitelnoe s = new Suschestvitelnoe();
				s.setSlovo(word);
				s.setUdarniySlog(syl);
				s.setSlogov(syls);
				s.setChastRechi("сущ");
				System.out.println("Число:");
				s.setChislo(input.next());
				System.out.println("Падеж:");
				s.setPadezh(input.next());
				System.out.println("Род:");
				s.setRod(input.next());
				dictionary.add(s);
			} else {
				// для начала надо ввести род
				System.out.println("Род:");
				String genus = input.next();
				// есть ед. и множ. число
				for(int i = 0; i < 2; i++){
					// есть 6 падежей
					for(int j = 0; j < 6; j++){
						Suschestvitelnoe s = new Suschestvitelnoe();
						s.setChastRechi("сущ");
						// ед. число
						if(i == 0){
							s.setRod(genus);
							switch(j){
							case 0 :
								System.out.println("Ед.ч. им.п.: ");
								s.setSlovo(input.next());
								System.out.println("Количество слогов:");
								int syls0 = input.nextInt();
								System.out.println("Номер ударного слога: ");
								int syl0 = input.nextInt();
								s.setUdarniySlog(syl0);
								s.setSlogov(syls0);
								s.setChislo("ед");
								s.setPadezh("им");
								break;
							case 1 :
								System.out.println("Ед.ч. вин.п.: ");
								s.setSlovo(input.next());
								System.out.println("Количество слогов:");
								int syls1 = input.nextInt();
								System.out.println("Номер ударного слога: ");
								int syl1 = input.nextInt();
								s.setUdarniySlog(syl1);
								s.setSlogov(syls1);
								s.setChislo("ед");
								s.setPadezh("вин");
								break;
							case 2 :
								System.out.println("Ед.ч. род.п.: ");
								s.setSlovo(input.next());
								System.out.println("Количество слогов:");
								int syls2 = input.nextInt();
								System.out.println("Номер ударного слога: ");
								int syl2 = input.nextInt();
								s.setUdarniySlog(syl2);
								s.setSlogov(syls2);
								s.setChislo("ед");
								s.setPadezh("род");
								break;
							case 3 :
								System.out.println("Ед.ч. дат.п.: ");
								s.setSlovo(input.next());
								System.out.println("Количество слогов:");
								int syls3 = input.nextInt();
								System.out.println("Номер ударного слога: ");
								int syl3 = input.nextInt();
								s.setUdarniySlog(syl3);
								s.setSlogov(syls3);
								s.setChislo("ед");
								s.setPadezh("дат");
								break;
							case 4 :
								System.out.println("Ед.ч. твор.п.: ");
								s.setSlovo(input.next());
								System.out.println("Количество слогов:");
								int syls4 = input.nextInt();
								System.out.println("Номер ударного слога: ");
								int syl4 = input.nextInt();
								s.setUdarniySlog(syl4);
								s.setSlogov(syls4);
								s.setChislo("ед");
								s.setPadezh("твор");
								break;
							case 5 :
								System.out.println("Ед.ч. предл.п.: ");
								s.setSlovo(input.next());
								System.out.println("Количество слогов:");
								int syls5 = input.nextInt();
								System.out.println("Номер ударного слога: ");
								int syl5 = input.nextInt();
								s.setUdarniySlog(syl5);
								s.setSlogov(syls5);
								s.setChislo("ед");
								s.setPadezh("предл");
								break;
							}
						}
						// множ. число
						if(i == 1){
							s.setRod("нет");
							switch(j){
							case 0 :
								System.out.println("Множ.ч. им.п.: ");
								s.setSlovo(input.next());
								System.out.println("Количество слогов:");
								int syls0 = input.nextInt();
								System.out.println("Номер ударного слога: ");
								int syl0 = input.nextInt();
								s.setUdarniySlog(syl0);
								s.setSlogov(syls0);
								s.setChislo("множ");
								s.setPadezh("им");
								break;
							case 1 :
								System.out.println("Множ.ч. вин.п.: ");
								s.setSlovo(input.next());
								System.out.println("Количество слогов:");
								int syls1 = input.nextInt();
								System.out.println("Номер ударного слога: ");
								int syl1 = input.nextInt();
								s.setUdarniySlog(syl1);
								s.setSlogov(syls1);
								s.setChislo("множ");
								s.setPadezh("вин");
								break;
							case 2 :
								System.out.println("Множ.ч. род.п.: ");
								s.setSlovo(input.next());
								System.out.println("Количество слогов:");
								int syls2 = input.nextInt();
								System.out.println("Номер ударного слога: ");
								int syl2 = input.nextInt();
								s.setUdarniySlog(syl2);
								s.setSlogov(syls2);
								s.setChislo("множ");
								s.setPadezh("род");
								break;
							case 3 :
								System.out.println("Множ.ч. дат.п.: ");
								s.setSlovo(input.next());
								System.out.println("Количество слогов:");
								int syls3 = input.nextInt();
								System.out.println("Номер ударного слога: ");
								int syl3 = input.nextInt();
								s.setUdarniySlog(syl3);
								s.setSlogov(syls3);
								s.setChislo("множ");
								s.setPadezh("дат");
								break;
							case 4 :
								System.out.println("Множ.ч. твор.п.: ");
								s.setSlovo(input.next());
								System.out.println("Количество слогов:");
								int syls4 = input.nextInt();
								System.out.println("Номер ударного слога: ");
								int syl4 = input.nextInt();
								s.setUdarniySlog(syl4);
								s.setSlogov(syls4);
								s.setChislo("множ");
								s.setPadezh("твор");
								break;
							case 5 :
								System.out.println("Множ.ч. предл.п.: ");
								s.setSlovo(input.next());
								System.out.println("Количество слогов:");
								int syls5 = input.nextInt();
								System.out.println("Номер ударного слога: ");
								int syl5 = input.nextInt();
								s.setUdarniySlog(syl5);
								s.setSlogov(syls5);
								s.setChislo("множ");
								s.setPadezh("предл");
								break;
							}
						}
						dictionary.add(s);
					}
					
				}
			}
			
			break;
			
			
		case "прил" :
			
			// выбор режима ввода
			System.out.println("Для ввода всех (полных) форм прилагательного нажмите 0, для ввода отдельной формы (неполной) - 1");
			System.out.println("Ускоренный ввод - 2");
			
			int tmp1 = input.nextInt();
			
			if (tmp1 == 1) {
				Prilagatelnoe p = new Prilagatelnoe();
				p.setSlovo(word);
				p.setUdarniySlog(syl);
				p.setSlogov(syls);
				p.setChastRechi("прил");
				System.out.println("Число:");
				p.setChislo(input.next());
				System.out.println("Падеж:");
				p.setPadezh(input.next());
				System.out.println("Род:");
				p.setRod(input.next());
				System.out.println("Полное? (да/нет)");
				String answer = input.next();
				if (answer.equals("да"))
					p.setPolnoe(true);
				else if (answer.equals("нет"))
					p.setPolnoe(false);
				dictionary.add(p);
			} else if (tmp1 == 0) {
				// ед.ч. = 0, множ.ч. = 1
				for(int i = 0; i < 2; i++){
					// падежи
					for(int j = 0; j < 6; j++){
						// ед. число
						if(i == 0){
							// муж = 0, жен = 1, ср = 2
							for(int k = 0; k < 3; k++){
								Prilagatelnoe p = new Prilagatelnoe();
								p.setPolnoe(true);
								p.setChastRechi("прил");
								if(k == 0){
									p.setRod("муж");
									switch(j){
									case 0 :
										System.out.println("Ед.ч. им.п. муж.р. КОТ: ");
										p.setSlovo(input.next());
										System.out.println("Количество слогов:");
										int syls0 = input.nextInt();
										System.out.println("Номер ударного слога: ");
										int syl0 = input.nextInt();
										p.setUdarniySlog(syl0);
										p.setSlogov(syls0);
										p.setChislo("ед");
										p.setPadezh("им");
										break;
									case 1 :
										System.out.println("Ед.ч. вин.п. муж.р. КОТА: ");
										p.setSlovo(input.next());
										System.out.println("Количество слогов:");
										int syls1 = input.nextInt();
										System.out.println("Номер ударного слога: ");
										int syl1 = input.nextInt();
										p.setUdarniySlog(syl1);
										p.setSlogov(syls1);
										p.setChislo("ед");
										p.setPadezh("вин");
										break;
									case 2 :
										System.out.println("Ед.ч. род.п. муж.р. КОТА: ");
										p.setSlovo(input.next());
										System.out.println("Количество слогов:");
										int syls2 = input.nextInt();
										System.out.println("Номер ударного слога: ");
										int syl2 = input.nextInt();
										p.setUdarniySlog(syl2);
										p.setSlogov(syls2);
										p.setChislo("ед");
										p.setPadezh("род");
										break;
									case 3 :
										System.out.println("Ед.ч. дат.п. муж.р. КОТУ: ");
										p.setSlovo(input.next());
										System.out.println("Количество слогов:");
										int syls3 = input.nextInt();
										System.out.println("Номер ударного слога: ");
										int syl3 = input.nextInt();
										p.setUdarniySlog(syl3);
										p.setSlogov(syls3);
										p.setChislo("ед");
										p.setPadezh("дат");
										break;
									case 4 :
										System.out.println("Ед.ч. твор.п. муж.р. КОТОМ: ");
										p.setSlovo(input.next());
										System.out.println("Количество слогов:");
										int syls4 = input.nextInt();
										System.out.println("Номер ударного слога: ");
										int syl4 = input.nextInt();
										p.setUdarniySlog(syl4);
										p.setSlogov(syls4);
										p.setChislo("ед");
										p.setPadezh("твор");
										break;
									case 5 :
										System.out.println("Ед.ч. предл.п. муж.р. О КОТЕ: ");
										p.setSlovo(input.next());
										System.out.println("Количество слогов:");
										int syls5 = input.nextInt();
										System.out.println("Номер ударного слога: ");
										int syl5 = input.nextInt();
										p.setUdarniySlog(syl5);
										p.setSlogov(syls5);
										p.setChislo("ед");
										p.setPadezh("предл");
										break;
									}
								}
								if(k == 1){
									p.setRod("жен");
									switch(j){
									case 0 :
										System.out.println("Ед.ч. им.п. жен.р. КОШКА: ");
										p.setSlovo(input.next());
										System.out.println("Количество слогов:");
										int syls0 = input.nextInt();
										System.out.println("Номер ударного слога: ");
										int syl0 = input.nextInt();
										p.setUdarniySlog(syl0);
										p.setSlogov(syls0);
										p.setChislo("ед");
										p.setPadezh("им");
										break;
									case 1 :
										System.out.println("Ед.ч. вин.п. жен.р. КОШКУ: ");
										p.setSlovo(input.next());
										System.out.println("Количество слогов:");
										int syls1 = input.nextInt();
										System.out.println("Номер ударного слога: ");
										int syl1 = input.nextInt();
										p.setUdarniySlog(syl1);
										p.setSlogov(syls1);
										p.setChislo("ед");
										p.setPadezh("вин");
										break;
									case 2 :
										System.out.println("Ед.ч. род.п. жен.р. КОШКИ: ");
										p.setSlovo(input.next());
										System.out.println("Количество слогов:");
										int syls2 = input.nextInt();
										System.out.println("Номер ударного слога: ");
										int syl2 = input.nextInt();
										p.setUdarniySlog(syl2);
										p.setSlogov(syls2);
										p.setChislo("ед");
										p.setPadezh("род");
										break;
									case 3 :
										System.out.println("Ед.ч. дат.п. жен.р. КОШКЕ: ");
										p.setSlovo(input.next());
										System.out.println("Количество слогов:");
										int syls3 = input.nextInt();
										System.out.println("Номер ударного слога: ");
										int syl3 = input.nextInt();
										p.setUdarniySlog(syl3);
										p.setSlogov(syls3);
										p.setChislo("ед");
										p.setPadezh("дат");
										break;
									case 4 :
										System.out.println("Ед.ч. твор.п. муж.р. КОШКОЙ: ");
										p.setSlovo(input.next());
										System.out.println("Количество слогов:");
										int syls4 = input.nextInt();
										System.out.println("Номер ударного слога: ");
										int syl4 = input.nextInt();
										p.setUdarniySlog(syl4);
										p.setSlogov(syls4);
										p.setChislo("ед");
										p.setPadezh("твор");
										break;
									case 5 :
										System.out.println("Ед.ч. предл.п. муж.р. О КОШКЕ: ");
										p.setSlovo(input.next());
										System.out.println("Количество слогов:");
										int syls5 = input.nextInt();
										System.out.println("Номер ударного слога: ");
										int syl5 = input.nextInt();
										p.setUdarniySlog(syl5);
										p.setSlogov(syls5);
										p.setChislo("ед");
										p.setPadezh("предл");
										break;
									}
								}
								if(k == 2){
									p.setRod("ср");
									switch(j){
									case 0 :
										System.out.println("Ед.ч. им.п. ср.р. ОКНО: ");
										p.setSlovo(input.next());
										System.out.println("Количество слогов:");
										int syls0 = input.nextInt();
										System.out.println("Номер ударного слога: ");
										int syl0 = input.nextInt();
										p.setUdarniySlog(syl0);
										p.setSlogov(syls0);
										p.setChislo("ед");
										p.setPadezh("им");
										break;
									case 1 :
										System.out.println("Ед.ч. вин.п. ср.р. ОКНО: ");
										p.setSlovo(input.next());
										System.out.println("Количество слогов:");
										int syls1 = input.nextInt();
										System.out.println("Номер ударного слога: ");
										int syl1 = input.nextInt();
										p.setUdarniySlog(syl1);
										p.setSlogov(syls1);
										p.setChislo("ед");
										p.setPadezh("вин");
										break;
									case 2 :
										System.out.println("Ед.ч. род.п. ср.р. ОКНА: ");
										p.setSlovo(input.next());
										System.out.println("Количество слогов:");
										int syls2 = input.nextInt();
										System.out.println("Номер ударного слога: ");
										int syl2 = input.nextInt();
										p.setUdarniySlog(syl2);
										p.setSlogov(syls2);
										p.setChislo("ед");
										p.setPadezh("род");
										break;
									case 3 :
										System.out.println("Ед.ч. дат.п. ср.р. ОКНУ: ");
										p.setSlovo(input.next());
										System.out.println("Количество слогов:");
										int syls3 = input.nextInt();
										System.out.println("Номер ударного слога: ");
										int syl3 = input.nextInt();
										p.setUdarniySlog(syl3);
										p.setSlogov(syls3);
										p.setChislo("ед");
										p.setPadezh("дат");
										break;
									case 4 :
										System.out.println("Ед.ч. твор.п. ср.р. ОКНОМ: ");
										p.setSlovo(input.next());
										System.out.println("Количество слогов:");
										int syls4 = input.nextInt();
										System.out.println("Номер ударного слога: ");
										int syl4 = input.nextInt();
										p.setUdarniySlog(syl4);
										p.setSlogov(syls4);
										p.setChislo("ед");
										p.setPadezh("твор");
										break;
									case 5 :
										System.out.println("Ед.ч. предл.п. ср.р. НА ОКНЕ: ");
										p.setSlovo(input.next());
										System.out.println("Количество слогов:");
										int syls5 = input.nextInt();
										System.out.println("Номер ударного слога: ");
										int syl5 = input.nextInt();
										p.setUdarniySlog(syl5);
										p.setSlogov(syls5);
										p.setChislo("ед");
										p.setPadezh("предл");
										break;
									}
								}
								
								dictionary.add(p);
							}
						}
						
						// множ. число
						if(i == 1){
							Prilagatelnoe p = new Prilagatelnoe();
							p.setPolnoe(true);
							p.setChastRechi("прил");
							p.setRod("нет");
							switch(j){
							case 0 :
								System.out.println("Множ.ч. им.п. КОТЫ: ");
								p.setSlovo(input.next());
								System.out.println("Количество слогов:");
								int syls0 = input.nextInt();
								System.out.println("Номер ударного слога: ");
								int syl0 = input.nextInt();
								p.setUdarniySlog(syl0);
								p.setSlogov(syls0);
								p.setChislo("множ");
								p.setPadezh("им");
								break;
							case 1 :
								System.out.println("Множ.ч. вин.п. КОТОВ: ");
								p.setSlovo(input.next());
								System.out.println("Количество слогов:");
								int syls1 = input.nextInt();
								System.out.println("Номер ударного слога: ");
								int syl1 = input.nextInt();
								p.setUdarniySlog(syl1);
								p.setSlogov(syls1);
								p.setChislo("множ");
								p.setPadezh("вин");
								break;
							case 2 :
								System.out.println("Множ.ч. род.п. КОТОВ: ");
								p.setSlovo(input.next());
								System.out.println("Количество слогов:");
								int syls2 = input.nextInt();
								System.out.println("Номер ударного слога: ");
								int syl2 = input.nextInt();
								p.setUdarniySlog(syl2);
								p.setSlogov(syls2);
								p.setChislo("множ");
								p.setPadezh("род");
								break;
							case 3 :
								System.out.println("Множ.ч. дат.п. КОТАМ: ");
								p.setSlovo(input.next());
								System.out.println("Количество слогов:");
								int syls3 = input.nextInt();
								System.out.println("Номер ударного слога: ");
								int syl3 = input.nextInt();
								p.setUdarniySlog(syl3);
								p.setSlogov(syls3);
								p.setChislo("множ");
								p.setPadezh("дат");
								break;
							case 4 :
								System.out.println("Множ.ч. твор.п. КОТАМИ: ");
								p.setSlovo(input.next());
								System.out.println("Количество слогов:");
								int syls4 = input.nextInt();
								System.out.println("Номер ударного слога: ");
								int syl4 = input.nextInt();
								p.setUdarniySlog(syl4);
								p.setSlogov(syls4);
								p.setChislo("множ");
								p.setPadezh("твор");
								break;
							case 5 :
								System.out.println("Множ.ч. предл.п. О КОТАХ: ");
								p.setSlovo(input.next());
								System.out.println("Количество слогов:");
								int syls5 = input.nextInt();
								System.out.println("Номер ударного слога: ");
								int syl5 = input.nextInt();
								p.setUdarniySlog(syl5);
								p.setSlogov(syls5);
								p.setChislo("множ");
								p.setPadezh("предл");
								break;
							}
							
							dictionary.add(p);
						}
						
						
					}
				}
			} else if (tmp1 == 2) {
				Prilagatelnoe p = new Prilagatelnoe();
				System.out.println("Введите прилагательное в форме им.п. ед.ч. муж.р.:");
				String iskhodnoeSlovo = input.next();
				p.setSlovo(iskhodnoeSlovo);
				System.out.println("Количество слогов:");
				int slogov = input.nextInt();
				System.out.println("Номер ударного слога: ");
				int udarniySlog = input.nextInt();
				// Обрезаем последние две буквы, чтобы получить корень слова
				String koren = iskhodnoeSlovo.substring(0, iskhodnoeSlovo.length()-2);
				String poslGlasnaya = iskhodnoeSlovo.substring(iskhodnoeSlovo.length()-2, iskhodnoeSlovo.length()-1);
				
				if (poslGlasnaya.equals("о")) {
					
					Prilagatelnoe p00 = new Prilagatelnoe();
					p00.setChastRechi("прил");
					p00.setSlovo(iskhodnoeSlovo);
					p00.setUdarniySlog(udarniySlog);
					p00.setSlogov(slogov);
					p00.setChislo("ед");
					p00.setRod("муж");
					p00.setPadezh("им");
					p00.setPolnoe(true);
					dictionary.add(p00);
					
					Prilagatelnoe p01 = new Prilagatelnoe();
					p01.setChastRechi("прил");
					p01.setSlovo(koren + "ая");
					p01.setUdarniySlog(udarniySlog);
					p01.setSlogov(slogov + 1);
					p01.setChislo("ед");
					p01.setRod("жен");
					p01.setPadezh("им");
					p01.setPolnoe(true);
					dictionary.add(p01);
					
					Prilagatelnoe p02 = new Prilagatelnoe();
					p02.setChastRechi("прил");
					p02.setSlovo(koren + "ое");
					p02.setUdarniySlog(udarniySlog);
					p02.setSlogov(slogov + 1);
					p02.setChislo("ед");
					p02.setRod("ср");
					p02.setPadezh("им");
					p02.setPolnoe(true);
					dictionary.add(p02);
					
					Prilagatelnoe p03 = new Prilagatelnoe();
					p03.setChastRechi("прил");
					p03.setSlovo(koren + "ые");
					p03.setUdarniySlog(udarniySlog);
					p03.setSlogov(slogov + 1);
					p03.setChislo("множ");
					p03.setRod("нет");
					p03.setPadezh("им");
					p03.setPolnoe(true);
					dictionary.add(p03);
					
					Prilagatelnoe p10 = new Prilagatelnoe();
					p10.setChastRechi("прил");
					p10.setSlovo(koren + "ой");
					p10.setUdarniySlog(udarniySlog);
					p10.setSlogov(slogov);
					p10.setChislo("ед");
					p10.setRod("муж");
					p10.setPadezh("вин");
					p10.setPolnoe(true);
					dictionary.add(p10);
					
					Prilagatelnoe p11 = new Prilagatelnoe();
					p11.setChastRechi("прил");
					p11.setSlovo(koren + "ую");
					p11.setUdarniySlog(udarniySlog);
					p11.setSlogov(slogov + 1);
					p11.setChislo("ед");
					p11.setRod("жен");
					p11.setPadezh("вин");
					p11.setPolnoe(true);
					dictionary.add(p11);
					
					Prilagatelnoe p12 = new Prilagatelnoe();
					p12.setChastRechi("прил");
					p12.setSlovo(koren + "ое");
					p12.setUdarniySlog(udarniySlog);
					p12.setSlogov(slogov + 1);
					p12.setChislo("ед");
					p12.setRod("ср");
					p12.setPadezh("вин");
					p12.setPolnoe(true);
					dictionary.add(p12);
					
					Prilagatelnoe p13 = new Prilagatelnoe();
					p13.setChastRechi("прил");
					p13.setSlovo(koren + "ые");
					p13.setUdarniySlog(udarniySlog);
					p13.setSlogov(slogov + 1);
					p13.setChislo("множ");
					p13.setRod("нет");
					p13.setPadezh("вин");
					p13.setPolnoe(true);
					dictionary.add(p13);
					
					Prilagatelnoe p20 = new Prilagatelnoe();
					p20.setChastRechi("прил");
					p20.setSlovo(koren + "ого");
					p20.setUdarniySlog(udarniySlog);
					p20.setSlogov(slogov + 1);
					p20.setChislo("ед");
					p20.setRod("муж");
					p20.setPadezh("род");
					p20.setPolnoe(true);
					dictionary.add(p20);
					
					Prilagatelnoe p21 = new Prilagatelnoe();
					p21.setChastRechi("прил");
					p21.setSlovo(koren + "ой");
					p21.setUdarniySlog(udarniySlog);
					p21.setSlogov(slogov);
					p21.setChislo("ед");
					p21.setRod("жен");
					p21.setPadezh("род");
					p21.setPolnoe(true);
					dictionary.add(p21);
					
					Prilagatelnoe p22 = new Prilagatelnoe();
					p22.setChastRechi("прил");
					p22.setSlovo(koren + "ого");
					p22.setUdarniySlog(udarniySlog);
					p22.setSlogov(slogov + 1);
					p22.setChislo("ед");
					p22.setRod("ср");
					p22.setPadezh("род");
					p22.setPolnoe(true);
					dictionary.add(p22);
					
					Prilagatelnoe p23 = new Prilagatelnoe();
					p23.setChastRechi("прил");
					p23.setSlovo(koren + "ых");
					p23.setUdarniySlog(udarniySlog);
					p23.setSlogov(slogov);
					p23.setChislo("множ");
					p23.setRod("нет");
					p23.setPadezh("род");
					p23.setPolnoe(true);
					dictionary.add(p23);
					
					Prilagatelnoe p30 = new Prilagatelnoe();
					p30.setChastRechi("прил");
					p30.setSlovo(koren + "ому");
					p30.setUdarniySlog(udarniySlog);
					p30.setSlogov(slogov + 1);
					p30.setChislo("ед");
					p30.setRod("муж");
					p30.setPadezh("дат");
					p30.setPolnoe(true);
					dictionary.add(p30);
					
					Prilagatelnoe p31 = new Prilagatelnoe();
					p31.setChastRechi("прил");
					p31.setSlovo(koren + "ой");
					p31.setUdarniySlog(udarniySlog);
					p31.setSlogov(slogov);
					p31.setChislo("ед");
					p31.setRod("жен");
					p31.setPadezh("дат");
					p31.setPolnoe(true);
					dictionary.add(p31);
					
					Prilagatelnoe p32 = new Prilagatelnoe();
					p32.setChastRechi("прил");
					p32.setSlovo(koren + "ому");
					p32.setUdarniySlog(udarniySlog);
					p32.setSlogov(slogov + 1);
					p32.setChislo("ед");
					p32.setRod("ср");
					p32.setPadezh("дат");
					p32.setPolnoe(true);
					dictionary.add(p32);
					
					Prilagatelnoe p33 = new Prilagatelnoe();
					p33.setChastRechi("прил");
					p33.setSlovo(koren + "ым");
					p33.setUdarniySlog(udarniySlog);
					p33.setSlogov(slogov);
					p33.setChislo("множ");
					p33.setRod("нет");
					p33.setPadezh("дат");
					p33.setPolnoe(true);
					dictionary.add(p33);
					
					Prilagatelnoe p40 = new Prilagatelnoe();
					p40.setChastRechi("прил");
					p40.setSlovo(koren + "ым");
					p40.setUdarniySlog(udarniySlog);
					p40.setSlogov(slogov);
					p40.setChislo("ед");
					p40.setRod("муж");
					p40.setPadezh("твор");
					p40.setPolnoe(true);
					dictionary.add(p40);
					
					Prilagatelnoe p41 = new Prilagatelnoe();
					p41.setChastRechi("прил");
					p41.setSlovo(koren + "ой");
					p41.setUdarniySlog(udarniySlog);
					p41.setSlogov(slogov);
					p41.setChislo("ед");
					p41.setRod("жен");
					p41.setPadezh("твор");
					p41.setPolnoe(true);
					dictionary.add(p41);
					
					Prilagatelnoe p42 = new Prilagatelnoe();
					p42.setChastRechi("прил");
					p42.setSlovo(koren + "ым");
					p42.setUdarniySlog(udarniySlog);
					p42.setSlogov(slogov);
					p42.setChislo("ед");
					p42.setRod("ср");
					p42.setPadezh("твор");
					p42.setPolnoe(true);
					dictionary.add(p42);
					
					Prilagatelnoe p43 = new Prilagatelnoe();
					p43.setChastRechi("прил");
					p43.setSlovo(koren + "ыми");
					p43.setUdarniySlog(udarniySlog);
					p43.setSlogov(slogov + 1);
					p43.setChislo("множ");
					p43.setRod("нет");
					p43.setPadezh("твор");
					p43.setPolnoe(true);
					dictionary.add(p43);
					
					Prilagatelnoe p50 = new Prilagatelnoe();
					p50.setChastRechi("прил");
					p50.setSlovo(koren + "ом");
					p50.setUdarniySlog(udarniySlog);
					p50.setSlogov(slogov);
					p50.setChislo("ед");
					p50.setRod("муж");
					p50.setPadezh("предл");
					p50.setPolnoe(true);
					dictionary.add(p50);
					
					Prilagatelnoe p51 = new Prilagatelnoe();
					p51.setChastRechi("прил");
					p51.setSlovo(koren + "ой");
					p51.setUdarniySlog(udarniySlog);
					p51.setSlogov(slogov);
					p51.setChislo("ед");
					p51.setRod("жен");
					p51.setPadezh("предл");
					p51.setPolnoe(true);
					dictionary.add(p51);
					
					Prilagatelnoe p52 = new Prilagatelnoe();
					p52.setChastRechi("прил");
					p52.setSlovo(koren + "ом");
					p52.setUdarniySlog(udarniySlog);
					p52.setSlogov(slogov);
					p52.setChislo("ед");
					p52.setRod("ср");
					p52.setPadezh("предл");
					p52.setPolnoe(true);
					dictionary.add(p52);
					
					Prilagatelnoe p53 = new Prilagatelnoe();
					p53.setChastRechi("прил");
					p53.setSlovo(koren + "ых");
					p53.setUdarniySlog(udarniySlog);
					p53.setSlogov(slogov);
					p53.setChislo("множ");
					p53.setRod("нет");
					p53.setPadezh("предл");
					p53.setPolnoe(true);
					dictionary.add(p53);
					
				} else if (poslGlasnaya.equals("и")) {
					
					Prilagatelnoe p00 = new Prilagatelnoe();
					p00.setChastRechi("прил");
					p00.setSlovo(iskhodnoeSlovo);
					p00.setUdarniySlog(udarniySlog);
					p00.setSlogov(slogov);
					p00.setChislo("ед");
					p00.setRod("муж");
					p00.setPadezh("им");
					p00.setPolnoe(true);
					dictionary.add(p00);
					
					Prilagatelnoe p01 = new Prilagatelnoe();
					p01.setChastRechi("прил");
					p01.setSlovo(koren + "ая");
					p01.setUdarniySlog(udarniySlog);
					p01.setSlogov(slogov + 1);
					p01.setChislo("ед");
					p01.setRod("жен");
					p01.setPadezh("им");
					p01.setPolnoe(true);
					dictionary.add(p01);
					
					Prilagatelnoe p02 = new Prilagatelnoe();
					p02.setChastRechi("прил");
					p02.setSlovo(koren + "ое");
					p02.setUdarniySlog(udarniySlog);
					p02.setSlogov(slogov + 1);
					p02.setChislo("ед");
					p02.setRod("ср");
					p02.setPadezh("им");
					p02.setPolnoe(true);
					dictionary.add(p02);
					
					Prilagatelnoe p03 = new Prilagatelnoe();
					p03.setChastRechi("прил");
					p03.setSlovo(koren + "ие");
					p03.setUdarniySlog(udarniySlog);
					p03.setSlogov(slogov + 1);
					p03.setChislo("множ");
					p03.setRod("нет");
					p03.setPadezh("им");
					p03.setPolnoe(true);
					dictionary.add(p03);
					
					Prilagatelnoe p10 = new Prilagatelnoe();
					p10.setChastRechi("прил");
					p10.setSlovo(koren + "ий");
					p10.setUdarniySlog(udarniySlog);
					p10.setSlogov(slogov);
					p10.setChislo("ед");
					p10.setRod("муж");
					p10.setPadezh("вин");
					p10.setPolnoe(true);
					dictionary.add(p10);
					
					Prilagatelnoe p11 = new Prilagatelnoe();
					p11.setChastRechi("прил");
					p11.setSlovo(koren + "ую");
					p11.setUdarniySlog(udarniySlog);
					p11.setSlogov(slogov + 1);
					p11.setChislo("ед");
					p11.setRod("жен");
					p11.setPadezh("вин");
					p11.setPolnoe(true);
					dictionary.add(p11);
					
					Prilagatelnoe p12 = new Prilagatelnoe();
					p12.setChastRechi("прил");
					p12.setSlovo(koren + "ое");
					p12.setUdarniySlog(udarniySlog);
					p12.setSlogov(slogov + 1);
					p12.setChislo("ед");
					p12.setRod("ср");
					p12.setPadezh("вин");
					p12.setPolnoe(true);
					dictionary.add(p12);
					
					Prilagatelnoe p13 = new Prilagatelnoe();
					p13.setChastRechi("прил");
					p13.setSlovo(koren + "ие");
					p13.setUdarniySlog(udarniySlog);
					p13.setSlogov(slogov + 1);
					p13.setChislo("множ");
					p13.setRod("нет");
					p13.setPadezh("вин");
					p13.setPolnoe(true);
					dictionary.add(p13);
					
					Prilagatelnoe p20 = new Prilagatelnoe();
					p20.setChastRechi("прил");
					p20.setSlovo(koren + "ого");
					p20.setUdarniySlog(udarniySlog);
					p20.setSlogov(slogov + 1);
					p20.setChislo("ед");
					p20.setRod("муж");
					p20.setPadezh("род");
					p20.setPolnoe(true);
					dictionary.add(p20);
					
					Prilagatelnoe p21 = new Prilagatelnoe();
					p21.setChastRechi("прил");
					p21.setSlovo(koren + "ой");
					p21.setUdarniySlog(udarniySlog);
					p21.setSlogov(slogov);
					p21.setChislo("ед");
					p21.setRod("жен");
					p21.setPadezh("род");
					p21.setPolnoe(true);
					dictionary.add(p21);
					
					Prilagatelnoe p22 = new Prilagatelnoe();
					p22.setChastRechi("прил");
					p22.setSlovo(koren + "ого");
					p22.setUdarniySlog(udarniySlog);
					p22.setSlogov(slogov + 1);
					p22.setChislo("ед");
					p22.setRod("ср");
					p22.setPadezh("род");
					p22.setPolnoe(true);
					dictionary.add(p22);
					
					Prilagatelnoe p23 = new Prilagatelnoe();
					p23.setChastRechi("прил");
					p23.setSlovo(koren + "их");
					p23.setUdarniySlog(udarniySlog);
					p23.setSlogov(slogov);
					p23.setChislo("множ");
					p23.setRod("нет");
					p23.setPadezh("род");
					p23.setPolnoe(true);
					dictionary.add(p23);
					
					Prilagatelnoe p30 = new Prilagatelnoe();
					p30.setChastRechi("прил");
					p30.setSlovo(koren + "ому");
					p30.setUdarniySlog(udarniySlog);
					p30.setSlogov(slogov + 1);
					p30.setChislo("ед");
					p30.setRod("муж");
					p30.setPadezh("дат");
					p30.setPolnoe(true);
					dictionary.add(p30);
					
					Prilagatelnoe p31 = new Prilagatelnoe();
					p31.setChastRechi("прил");
					p31.setSlovo(koren + "ой");
					p31.setUdarniySlog(udarniySlog);
					p31.setSlogov(slogov);
					p31.setChislo("ед");
					p31.setRod("жен");
					p31.setPadezh("дат");
					p31.setPolnoe(true);
					dictionary.add(p31);
					
					Prilagatelnoe p32 = new Prilagatelnoe();
					p32.setChastRechi("прил");
					p32.setSlovo(koren + "ому");
					p32.setUdarniySlog(udarniySlog);
					p32.setSlogov(slogov + 1);
					p32.setChislo("ед");
					p32.setRod("ср");
					p32.setPadezh("дат");
					p32.setPolnoe(true);
					dictionary.add(p32);
					
					Prilagatelnoe p33 = new Prilagatelnoe();
					p33.setChastRechi("прил");
					p33.setSlovo(koren + "им");
					p33.setUdarniySlog(udarniySlog);
					p33.setSlogov(slogov);
					p33.setChislo("множ");
					p33.setRod("нет");
					p33.setPadezh("дат");
					p33.setPolnoe(true);
					dictionary.add(p33);
					
					Prilagatelnoe p40 = new Prilagatelnoe();
					p40.setChastRechi("прил");
					p40.setSlovo(koren + "им");
					p40.setUdarniySlog(udarniySlog);
					p40.setSlogov(slogov);
					p40.setChislo("ед");
					p40.setRod("муж");
					p40.setPadezh("твор");
					p40.setPolnoe(true);
					dictionary.add(p40);
					
					Prilagatelnoe p41 = new Prilagatelnoe();
					p41.setChastRechi("прил");
					p41.setSlovo(koren + "ой");
					p41.setUdarniySlog(udarniySlog);
					p41.setSlogov(slogov);
					p41.setChislo("ед");
					p41.setRod("жен");
					p41.setPadezh("твор");
					p41.setPolnoe(true);
					dictionary.add(p41);
					
					Prilagatelnoe p42 = new Prilagatelnoe();
					p42.setChastRechi("прил");
					p42.setSlovo(koren + "им");
					p42.setUdarniySlog(udarniySlog);
					p42.setSlogov(slogov);
					p42.setChislo("ед");
					p42.setRod("ср");
					p42.setPadezh("твор");
					p42.setPolnoe(true);
					dictionary.add(p42);
					
					Prilagatelnoe p43 = new Prilagatelnoe();
					p43.setChastRechi("прил");
					p43.setSlovo(koren + "ими");
					p43.setUdarniySlog(udarniySlog);
					p43.setSlogov(slogov + 1);
					p43.setChislo("множ");
					p43.setRod("нет");
					p43.setPadezh("твор");
					p43.setPolnoe(true);
					dictionary.add(p43);
					
					Prilagatelnoe p50 = new Prilagatelnoe();
					p50.setChastRechi("прил");
					p50.setSlovo(koren + "ом");
					p50.setUdarniySlog(udarniySlog);
					p50.setSlogov(slogov);
					p50.setChislo("ед");
					p50.setRod("муж");
					p50.setPadezh("предл");
					p50.setPolnoe(true);
					dictionary.add(p50);
					
					Prilagatelnoe p51 = new Prilagatelnoe();
					p51.setChastRechi("прил");
					p51.setSlovo(koren + "ой");
					p51.setUdarniySlog(udarniySlog);
					p51.setSlogov(slogov);
					p51.setChislo("ед");
					p51.setRod("жен");
					p51.setPadezh("предл");
					p51.setPolnoe(true);
					dictionary.add(p51);
					
					Prilagatelnoe p52 = new Prilagatelnoe();
					p52.setChastRechi("прил");
					p52.setSlovo(koren + "ом");
					p52.setUdarniySlog(udarniySlog);
					p52.setSlogov(slogov);
					p52.setChislo("ед");
					p52.setRod("ср");
					p52.setPadezh("предл");
					p52.setPolnoe(true);
					dictionary.add(p52);
					
					Prilagatelnoe p53 = new Prilagatelnoe();
					p53.setChastRechi("прил");
					p53.setSlovo(koren + "их");
					p53.setUdarniySlog(udarniySlog);
					p53.setSlogov(slogov);
					p53.setChislo("множ");
					p53.setRod("нет");
					p53.setPadezh("предл");
					p53.setPolnoe(true);
					dictionary.add(p53);
					
				} else if (poslGlasnaya.equals("ы")){
					
					Prilagatelnoe p00 = new Prilagatelnoe();
					p00.setChastRechi("прил");
					p00.setSlovo(iskhodnoeSlovo);
					p00.setUdarniySlog(udarniySlog);
					p00.setSlogov(slogov);
					p00.setChislo("ед");
					p00.setRod("муж");
					p00.setPadezh("им");
					p00.setPolnoe(true);
					dictionary.add(p00);
					
					Prilagatelnoe p01 = new Prilagatelnoe();
					p01.setChastRechi("прил");
					p01.setSlovo(koren + "ая");
					p01.setUdarniySlog(udarniySlog);
					p01.setSlogov(slogov + 1);
					p01.setChislo("ед");
					p01.setRod("жен");
					p01.setPadezh("им");
					p01.setPolnoe(true);
					dictionary.add(p01);
					
					Prilagatelnoe p02 = new Prilagatelnoe();
					p02.setChastRechi("прил");
					p02.setSlovo(koren + "ое");
					p02.setUdarniySlog(udarniySlog);
					p02.setSlogov(slogov + 1);
					p02.setChislo("ед");
					p02.setRod("ср");
					p02.setPadezh("им");
					p02.setPolnoe(true);
					dictionary.add(p02);
					
					Prilagatelnoe p03 = new Prilagatelnoe();
					p03.setChastRechi("прил");
					p03.setSlovo(koren + "ые");
					p03.setUdarniySlog(udarniySlog);
					p03.setSlogov(slogov + 1);
					p03.setChislo("множ");
					p03.setRod("нет");
					p03.setPadezh("им");
					p03.setPolnoe(true);
					dictionary.add(p03);
					
					Prilagatelnoe p10 = new Prilagatelnoe();
					p10.setChastRechi("прил");
					p10.setSlovo(koren + "ый");
					p10.setUdarniySlog(udarniySlog);
					p10.setSlogov(slogov);
					p10.setChislo("ед");
					p10.setRod("муж");
					p10.setPadezh("вин");
					p10.setPolnoe(true);
					dictionary.add(p10);
					
					Prilagatelnoe p11 = new Prilagatelnoe();
					p11.setChastRechi("прил");
					p11.setSlovo(koren + "ую");
					p11.setUdarniySlog(udarniySlog);
					p11.setSlogov(slogov + 1);
					p11.setChislo("ед");
					p11.setRod("жен");
					p11.setPadezh("вин");
					p11.setPolnoe(true);
					dictionary.add(p11);
					
					Prilagatelnoe p12 = new Prilagatelnoe();
					p12.setChastRechi("прил");
					p12.setSlovo(koren + "ое");
					p12.setUdarniySlog(udarniySlog);
					p12.setSlogov(slogov + 1);
					p12.setChislo("ед");
					p12.setRod("ср");
					p12.setPadezh("вин");
					p12.setPolnoe(true);
					dictionary.add(p12);
					
					Prilagatelnoe p13 = new Prilagatelnoe();
					p13.setChastRechi("прил");
					p13.setSlovo(koren + "ые");
					p13.setUdarniySlog(udarniySlog);
					p13.setSlogov(slogov + 1);
					p13.setChislo("множ");
					p13.setRod("нет");
					p13.setPadezh("вин");
					p13.setPolnoe(true);
					dictionary.add(p13);
					
					Prilagatelnoe p20 = new Prilagatelnoe();
					p20.setChastRechi("прил");
					p20.setSlovo(koren + "ого");
					p20.setUdarniySlog(udarniySlog);
					p20.setSlogov(slogov + 1);
					p20.setChislo("ед");
					p20.setRod("муж");
					p20.setPadezh("род");
					p20.setPolnoe(true);
					dictionary.add(p20);
					
					Prilagatelnoe p21 = new Prilagatelnoe();
					p21.setChastRechi("прил");
					p21.setSlovo(koren + "ой");
					p21.setUdarniySlog(udarniySlog);
					p21.setSlogov(slogov);
					p21.setChislo("ед");
					p21.setRod("жен");
					p21.setPadezh("род");
					p21.setPolnoe(true);
					dictionary.add(p21);
					
					Prilagatelnoe p22 = new Prilagatelnoe();
					p22.setChastRechi("прил");
					p22.setSlovo(koren + "ого");
					p22.setUdarniySlog(udarniySlog);
					p22.setSlogov(slogov + 1);
					p22.setChislo("ед");
					p22.setRod("ср");
					p22.setPadezh("род");
					p22.setPolnoe(true);
					dictionary.add(p22);
					
					Prilagatelnoe p23 = new Prilagatelnoe();
					p23.setChastRechi("прил");
					p23.setSlovo(koren + "ых");
					p23.setUdarniySlog(udarniySlog);
					p23.setSlogov(slogov);
					p23.setChislo("множ");
					p23.setRod("нет");
					p23.setPadezh("род");
					p23.setPolnoe(true);
					dictionary.add(p23);
					
					Prilagatelnoe p30 = new Prilagatelnoe();
					p30.setChastRechi("прил");
					p30.setSlovo(koren + "ому");
					p30.setUdarniySlog(udarniySlog);
					p30.setSlogov(slogov + 1);
					p30.setChislo("ед");
					p30.setRod("муж");
					p30.setPadezh("дат");
					p30.setPolnoe(true);
					dictionary.add(p30);
					
					Prilagatelnoe p31 = new Prilagatelnoe();
					p31.setChastRechi("прил");
					p31.setSlovo(koren + "ой");
					p31.setUdarniySlog(udarniySlog);
					p31.setSlogov(slogov);
					p31.setChislo("ед");
					p31.setRod("жен");
					p31.setPadezh("дат");
					p31.setPolnoe(true);
					dictionary.add(p31);
					
					Prilagatelnoe p32 = new Prilagatelnoe();
					p32.setChastRechi("прил");
					p32.setSlovo(koren + "ому");
					p32.setUdarniySlog(udarniySlog);
					p32.setSlogov(slogov + 1);
					p32.setChislo("ед");
					p32.setRod("ср");
					p32.setPadezh("дат");
					p32.setPolnoe(true);
					dictionary.add(p32);
					
					Prilagatelnoe p33 = new Prilagatelnoe();
					p33.setChastRechi("прил");
					p33.setSlovo(koren + "ым");
					p33.setUdarniySlog(udarniySlog);
					p33.setSlogov(slogov);
					p33.setChislo("множ");
					p33.setRod("нет");
					p33.setPadezh("дат");
					p33.setPolnoe(true);
					dictionary.add(p33);
					
					Prilagatelnoe p40 = new Prilagatelnoe();
					p40.setChastRechi("прил");
					p40.setSlovo(koren + "ым");
					p40.setUdarniySlog(udarniySlog);
					p40.setSlogov(slogov);
					p40.setChislo("ед");
					p40.setRod("муж");
					p40.setPadezh("твор");
					p40.setPolnoe(true);
					dictionary.add(p40);
					
					Prilagatelnoe p41 = new Prilagatelnoe();
					p41.setChastRechi("прил");
					p41.setSlovo(koren + "ой");
					p41.setUdarniySlog(udarniySlog);
					p41.setSlogov(slogov);
					p41.setChislo("ед");
					p41.setRod("жен");
					p41.setPadezh("твор");
					p41.setPolnoe(true);
					dictionary.add(p41);
					
					Prilagatelnoe p42 = new Prilagatelnoe();
					p42.setChastRechi("прил");
					p42.setSlovo(koren + "ым");
					p42.setUdarniySlog(udarniySlog);
					p42.setSlogov(slogov);
					p42.setChislo("ед");
					p42.setRod("ср");
					p42.setPadezh("твор");
					p42.setPolnoe(true);
					dictionary.add(p42);
					
					Prilagatelnoe p43 = new Prilagatelnoe();
					p43.setChastRechi("прил");
					p43.setSlovo(koren + "ыми");
					p43.setUdarniySlog(udarniySlog);
					p43.setSlogov(slogov + 1);
					p43.setChislo("множ");
					p43.setRod("нет");
					p43.setPadezh("твор");
					p43.setPolnoe(true);
					dictionary.add(p43);
					
					Prilagatelnoe p50 = new Prilagatelnoe();
					p50.setChastRechi("прил");
					p50.setSlovo(koren + "ом");
					p50.setUdarniySlog(udarniySlog);
					p50.setSlogov(slogov);
					p50.setChislo("ед");
					p50.setRod("муж");
					p50.setPadezh("предл");
					p50.setPolnoe(true);
					dictionary.add(p50);
					
					Prilagatelnoe p51 = new Prilagatelnoe();
					p51.setChastRechi("прил");
					p51.setSlovo(koren + "ой");
					p51.setUdarniySlog(udarniySlog);
					p51.setSlogov(slogov);
					p51.setChislo("ед");
					p51.setRod("жен");
					p51.setPadezh("предл");
					p51.setPolnoe(true);
					dictionary.add(p51);
					
					Prilagatelnoe p52 = new Prilagatelnoe();
					p52.setChastRechi("прил");
					p52.setSlovo(koren + "ом");
					p52.setUdarniySlog(udarniySlog);
					p52.setSlogov(slogov);
					p52.setChislo("ед");
					p52.setRod("ср");
					p52.setPadezh("предл");
					p52.setPolnoe(true);
					dictionary.add(p52);
					
					Prilagatelnoe p53 = new Prilagatelnoe();
					p53.setChastRechi("прил");
					p53.setSlovo(koren + "ых");
					p53.setUdarniySlog(udarniySlog);
					p53.setSlogov(slogov);
					p53.setChislo("множ");
					p53.setRod("нет");
					p53.setPadezh("предл");
					p53.setPolnoe(true);
					dictionary.add(p53);
					
				}
				
				
			}
			
			break;
			
			
		case "глаг" :
			
			//выбор режима ввода
			System.out.println("Для ввода всех  форм глагола нажмите 0, для ввода отдельных форм (прош.вр. сов.вид, пов.нак, буд.вр.) - 1");
			
			int tmp2 = input.nextInt();
			
			if (tmp2 == 1) {
				
				Glagol g = new Glagol();
				g.setSlovo(word);
				g.setUdarniySlog(syl);
				g.setSlogov(syls);
				g.setChastRechi("глаг");
				System.out.println("Возвратность? (да/нет)");
				String answer0 = input.next();
				if(answer0.equals("да")){
					System.out.println("Время (прошсов, буд) / наклонение (пов):");
					g.setVremya("в" + input.next());
				}
				else if(answer0.equals("нет")){
					System.out.println("Время (прошсов, буд) / наклонение (пов):");
					g.setVremya(input.next());
				}
				System.out.println("Число:");
				g.setChislo(input.next());
				System.out.println("Род/лицо:");
				g.setRod(input.next());
				System.out.println("Переходость? (да/нет)");
				String answer1 = input.next();
				if(answer1.equals("да"))
					g.setPerekhod(true);
				else if(answer1.equals("нет"))
					g.setPerekhod(false);
				dictionary.add(g);
				
			} else {
				
				// все формы глаголов (разумно, а не втупую!)
				
				System.out.println("Переходость? (да/нет)");
				String trans = input.next();
				boolean t = false;
				if(trans.equals("да"))
					t = true;
				else if(trans.equals("нет"))
					t = false;
				
				System.out.println("Возвратность? (да/нет)");
				String sich = input.next();
				boolean s = false;
				if(sich.equals("да"))
					s = true;
				else if(trans.equals("нет"))
					s = false;
				
				// Ввод возвратных глаголов
				if(s){
					
					// инфинитив
					Glagol g = new Glagol();	g.setChastRechi("глаг");
					System.out.println("Инфинитив: ");
					g.setSlovo(input.next());	g.setVremya("винф");		g.setChislo("нет");	g.setRod("нет");	g.setPerekhod(t);
					System.out.println("Количество слогов:");	g.setSlogov(input.nextInt());
					System.out.println("Номер ударного слога: ");	g.setUdarniySlog(input.nextInt());
					dictionary.add(g);
					
					// прош ед муж
					Glagol g1 = new Glagol();	g1.setChastRechi("глаг");
					System.out.println("Прош. вр. ед. ч. ОН ТРЯССЯ: "); String form1 = input.next();
					g1.setSlovo(form1);	g1.setVremya("впрош");		g1.setChislo("ед");	g1.setRod("муж");	g1.setPerekhod(t);
					System.out.println("Количество слогов:");	int slogov1 = input.nextInt(); g1.setSlogov(slogov1);
					System.out.println("Номер ударного слога: ");	int udslog1 = input.nextInt(); g1.setUdarniySlog(udslog1);
					dictionary.add(g1);
					
					// прош ед жен
					Glagol g2 = new Glagol();	g2.setChastRechi("глаг");
					System.out.println("Прош. вр. ед. ч. ОНА ТРЯСЛАСЬ: "); String form2 = input.next();
					g2.setSlovo(form2);	g2.setVremya("впрош");		g2.setChislo("ед");	g2.setRod("жен");	g2.setPerekhod(t);
					System.out.println("Количество слогов:");	int slogov2 = input.nextInt(); g2.setSlogov(slogov2);
					System.out.println("Номер ударного слога: ");	int udslog2 = input.nextInt(); g2.setUdarniySlog(udslog2);
					dictionary.add(g2);
					
					// прош ед ср
					Glagol g3 = new Glagol();	g3.setChastRechi("глаг");
					System.out.println("Прош. вр. ед. ч. ОНО ТРЯСЛОСЬ: "); String form3 = input.next();
					g3.setSlovo(form3);	g3.setVremya("впрош");		g3.setChislo("ед");	g3.setRod("ср");	g3.setPerekhod(t);
					System.out.println("Количество слогов:");	int slogov3 = input.nextInt(); g3.setSlogov(slogov3);
					System.out.println("Номер ударного слога: ");	int udslog3 = input.nextInt(); g3.setUdarniySlog(udslog3);
					dictionary.add(g3);
					
					// прош множ
					Glagol g4 = new Glagol();	g4.setChastRechi("глаг");
					System.out.println("Прош. вр. ед. ч. ОНИ ТРЯСЛИСЬ: "); String form4 = input.next();
					g4.setSlovo(form4);	g4.setVremya("впрош");		g4.setChislo("множ");	g4.setRod("нет");	g4.setPerekhod(t);
					System.out.println("Количество слогов:");	int slogov4 = input.nextInt(); g4.setSlogov(slogov4);
					System.out.println("Номер ударного слога: ");	int udslog4 = input.nextInt(); g4.setUdarniySlog(udslog4);
					dictionary.add(g4);
					
					// наст ед 1лицо
					Glagol g5 = new Glagol();	g5.setChastRechi("глаг");
					System.out.println("Наст. вр. ед. ч. 1 лицо Я ТРЯСУСЬ: ");
					g5.setSlovo(input.next());	g5.setVremya("внаст");		g5.setChislo("ед");	g5.setRod("1лицо");	g5.setPerekhod(t);
					System.out.println("Количество слогов:");	g5.setSlogov(input.nextInt());
					System.out.println("Номер ударного слога: ");	g5.setUdarniySlog(input.nextInt());
					dictionary.add(g5);
					
					// наст ед 2лицо
					Glagol g6 = new Glagol();	g6.setChastRechi("глаг");
					System.out.println("Наст. вр. ед. ч. 2 лицо ТЫ ТРЯСЁШЬСЯ: ");
					g6.setSlovo(input.next());	g6.setVremya("внаст");		g6.setChislo("ед");	g6.setRod("2лицо");	g6.setPerekhod(t);
					System.out.println("Количество слогов:");	g6.setSlogov(input.nextInt());
					System.out.println("Номер ударного слога: ");	g6.setUdarniySlog(input.nextInt());
					dictionary.add(g6);
					
					// наст ед 3лицо
					Glagol g7 = new Glagol();	g7.setChastRechi("глаг");
					System.out.println("Наст. вр. ед. ч. 3 лицо ОН/ОНА/ОНО ТРЯСЁТСЯ: ");
					g7.setSlovo(input.next());	g7.setVremya("внаст");		g7.setChislo("ед");	g7.setRod("3лицо");	g7.setPerekhod(t);
					System.out.println("Количество слогов:");	g7.setSlogov(input.nextInt());
					System.out.println("Номер ударного слога: ");	g7.setUdarniySlog(input.nextInt());
					dictionary.add(g7);
					
					// наст множ 1лицо
					Glagol g8 = new Glagol();	g8.setChastRechi("глаг");
					System.out.println("Наст. вр. множ. ч. 1 лицо МЫ ТРЯСЁМСЯ: ");
					g8.setSlovo(input.next());	g8.setVremya("наст");		g8.setChislo("множ");	g8.setRod("1лицо");	g8.setPerekhod(t);
					System.out.println("Количество слогов:");	g8.setSlogov(input.nextInt());
					System.out.println("Номер ударного слога: ");	g8.setUdarniySlog(input.nextInt());
					dictionary.add(g8);
					
					// наст множ 2лицо
					Glagol g9 = new Glagol();	g9.setChastRechi("глаг");
					System.out.println("Наст. вр. множ. ч. 2 лицо ВЫ ТРЯСЁТЕСЬ: ");
					g9.setSlovo(input.next());	g9.setVremya("внаст");		g9.setChislo("множ");	g9.setRod("2лицо");	g9.setPerekhod(t);
					System.out.println("Количество слогов:");	g9.setSlogov(input.nextInt());
					System.out.println("Номер ударного слога: ");	g9.setUdarniySlog(input.nextInt());
					dictionary.add(g9);
					
					// наст множ 3лицо
					Glagol g10 = new Glagol();	g10.setChastRechi("глаг");
					System.out.println("Наст. вр. множ. ч. 3 лицо ОНИ ТРЯСУТСЯ: ");
					g10.setSlovo(input.next());	g10.setVremya("внаст");		g10.setChislo("множ");	g10.setRod("3лицо");	g10.setPerekhod(t);
					System.out.println("Количество слогов:");	g10.setSlogov(input.nextInt());
					System.out.println("Номер ударного слога: ");	g10.setUdarniySlog(input.nextInt());
					dictionary.add(g10);
					
				} else {
				
				// инфинитив
				Glagol g = new Glagol();	g.setChastRechi("глаг");
				System.out.println("Инфинитив: ");
				g.setSlovo(input.next());	g.setVremya("инф");		g.setChislo("нет");	g.setRod("нет");	g.setPerekhod(t);
				System.out.println("Количество слогов:");	g.setSlogov(input.nextInt());
				System.out.println("Номер ударного слога: ");	g.setUdarniySlog(input.nextInt());
				dictionary.add(g);
				
				// прош ед муж
				Glagol g1 = new Glagol();	g1.setChastRechi("глаг");
				System.out.println("Прош. вр. ед. ч. ОН БЕЖАЛ: "); String form1 = input.next();
				g1.setSlovo(form1);	g1.setVremya("прош");		g1.setChislo("ед");	g1.setRod("муж");	g1.setPerekhod(t);
				System.out.println("Количество слогов:");	int slogov1 = input.nextInt(); g1.setSlogov(slogov1);
				System.out.println("Номер ударного слога: ");	int udslog1 = input.nextInt(); g1.setUdarniySlog(udslog1);
				dictionary.add(g1);
				
				// прош ед жен
				Glagol g2 = new Glagol();	g2.setChastRechi("глаг");
				g2.setSlovo(form1 + "а");	g2.setVremya("прош");		g2.setChislo("ед");	g2.setRod("жен");	g2.setPerekhod(t);
				g2.setSlogov(slogov1 + 1);
				g2.setUdarniySlog(udslog1);
				dictionary.add(g2);
				
				// прош ед ср
				Glagol g3 = new Glagol();	g3.setChastRechi("глаг");
				g3.setSlovo(form1 + "о");	g3.setVremya("прош");		g3.setChislo("ед");	g3.setRod("ср");	g3.setPerekhod(t);
				g3.setSlogov(slogov1 + 1);
				g3.setUdarniySlog(udslog1);
				dictionary.add(g3);
				
				// прош множ
				Glagol g4 = new Glagol();	g4.setChastRechi("глаг");
				g4.setSlovo(form1 + "и");	g4.setVremya("прош");		g4.setChislo("множ");	g4.setRod("нет");	g4.setPerekhod(t);
				g4.setSlogov(slogov1 + 1);
				g4.setUdarniySlog(udslog1);
				dictionary.add(g4);
				
				// наст ед 1лицо
				Glagol g5 = new Glagol();	g5.setChastRechi("глаг");
				System.out.println("Наст. вр. ед. ч. 1 лицо Я БЕГУ: ");
				g5.setSlovo(input.next());	g5.setVremya("наст");		g5.setChislo("ед");	g5.setRod("1лицо");	g5.setPerekhod(t);
				System.out.println("Количество слогов:");	g5.setSlogov(input.nextInt());
				System.out.println("Номер ударного слога: ");	g5.setUdarniySlog(input.nextInt());
				dictionary.add(g5);
				
				// наст ед 2лицо
				Glagol g6 = new Glagol();	g6.setChastRechi("глаг");
				System.out.println("Наст. вр. ед. ч. 2 лицо ТЫ БЕЖИШЬ: ");
				g6.setSlovo(input.next());	g6.setVremya("наст");		g6.setChislo("ед");	g6.setRod("2лицо");	g6.setPerekhod(t);
				System.out.println("Количество слогов:");	g6.setSlogov(input.nextInt());
				System.out.println("Номер ударного слога: ");	g6.setUdarniySlog(input.nextInt());
				dictionary.add(g6);
				
				// наст ед 3лицо
				Glagol g7 = new Glagol();	g7.setChastRechi("глаг");
				System.out.println("Наст. вр. ед. ч. 3 лицо ОН/ОНА/ОНО БЕЖИТ: ");
				g7.setSlovo(input.next());	g7.setVremya("наст");		g7.setChislo("ед");	g7.setRod("3лицо");	g7.setPerekhod(t);
				System.out.println("Количество слогов:");	g7.setSlogov(input.nextInt());
				System.out.println("Номер ударного слога: ");	g7.setUdarniySlog(input.nextInt());
				dictionary.add(g7);
				
				// наст множ 1лицо
				Glagol g8 = new Glagol();	g8.setChastRechi("глаг");
				System.out.println("Наст. вр. множ. ч. 1 лицо МЫ БЕЖИМ: ");
				g8.setSlovo(input.next());	g8.setVremya("наст");		g8.setChislo("множ");	g8.setRod("1лицо");	g8.setPerekhod(t);
				System.out.println("Количество слогов:");	g8.setSlogov(input.nextInt());
				System.out.println("Номер ударного слога: ");	g8.setUdarniySlog(input.nextInt());
				dictionary.add(g8);
				
				// наст множ 2лицо
				Glagol g9 = new Glagol();	g9.setChastRechi("глаг");
				System.out.println("Наст. вр. множ. ч. 2 лицо ВЫ БЕЖИТЕ: ");
				g9.setSlovo(input.next());	g9.setVremya("наст");		g9.setChislo("множ");	g9.setRod("2лицо");	g9.setPerekhod(t);
				System.out.println("Количество слогов:");	g9.setSlogov(input.nextInt());
				System.out.println("Номер ударного слога: ");	g9.setUdarniySlog(input.nextInt());
				dictionary.add(g9);
				
				// наст множ 3лицо
				Glagol g10 = new Glagol();	g10.setChastRechi("глаг");
				System.out.println("Наст. вр. множ. ч. 3 лицо ОНИ БЕГУТ: ");
				g10.setSlovo(input.next());	g10.setVremya("наст");		g10.setChislo("множ");	g10.setRod("3лицо");	g10.setPerekhod(t);
				System.out.println("Количество слогов:");	g10.setSlogov(input.nextInt());
				System.out.println("Номер ударного слога: ");	g10.setUdarniySlog(input.nextInt());
				dictionary.add(g10);
				}
				
			}
			
			
			break;
			
			
			
		case "мест" :
			Mestoimenie m = new Mestoimenie();
			m.setSlovo(word);
			m.setUdarniySlog(syl);
			m.setSlogov(syls);
			m.setChastRechi("мест");
			System.out.println("Число:");
			m.setChislo(input.next());
			System.out.println("Род:");
			m.setRod(input.next());
			System.out.println("Падеж:");
			m.setPadezh(input.next());
			System.out.println("Грамматический разряд:");
			m.setRazryadGr(input.next());
			System.out.println("Разряд по значению:");
			m.setRazryadZn(input.next());
			dictionary.add(m);
			break;
		case "нар" :
			Narechie n = new Narechie();
			n.setSlovo(word);
			n.setUdarniySlog(syl);
			n.setSlogov(syls);
			n.setChastRechi("нар");
			dictionary.add(n);
			break;
		case "прич" :
			
			// ед.ч. = 0, множ.ч. = 1
			for(int i = 0; i < 2; i++){
				// падежи
				for(int j = 0; j < 6; j++){
					// ед. число
					if(i == 0){
						// муж = 0, жен = 1, ср = 2
						for(int k = 0; k < 3; k++){
							Prichastie p = new Prichastie();
							p.setPolnoe(true);
							p.setChastRechi("прил");
							if(k == 0){
								p.setRod("муж");
								switch(j){
								case 0 :
									System.out.println("Ед.ч. им.п. муж.р. КОТ: ");
									p.setSlovo(input.next());
									System.out.println("Количество слогов:");
									int syls0 = input.nextInt();
									System.out.println("Номер ударного слога: ");
									int syl0 = input.nextInt();
									p.setUdarniySlog(syl0);
									p.setSlogov(syls0);
									p.setChislo("ед");
									p.setPadezh("им");
									break;
								case 1 :
									System.out.println("Ед.ч. вин.п. муж.р. КОТА: ");
									p.setSlovo(input.next());
									System.out.println("Количество слогов:");
									int syls1 = input.nextInt();
									System.out.println("Номер ударного слога: ");
									int syl1 = input.nextInt();
									p.setUdarniySlog(syl1);
									p.setSlogov(syls1);
									p.setChislo("ед");
									p.setPadezh("вин");
									break;
								case 2 :
									System.out.println("Ед.ч. род.п. муж.р. КОТА: ");
									p.setSlovo(input.next());
									System.out.println("Количество слогов:");
									int syls2 = input.nextInt();
									System.out.println("Номер ударного слога: ");
									int syl2 = input.nextInt();
									p.setUdarniySlog(syl2);
									p.setSlogov(syls2);
									p.setChislo("ед");
									p.setPadezh("род");
									break;
								case 3 :
									System.out.println("Ед.ч. дат.п. муж.р. КОТУ: ");
									p.setSlovo(input.next());
									System.out.println("Количество слогов:");
									int syls3 = input.nextInt();
									System.out.println("Номер ударного слога: ");
									int syl3 = input.nextInt();
									p.setUdarniySlog(syl3);
									p.setSlogov(syls3);
									p.setChislo("ед");
									p.setPadezh("дат");
									break;
								case 4 :
									System.out.println("Ед.ч. твор.п. муж.р. КОТОМ: ");
									p.setSlovo(input.next());
									System.out.println("Количество слогов:");
									int syls4 = input.nextInt();
									System.out.println("Номер ударного слога: ");
									int syl4 = input.nextInt();
									p.setUdarniySlog(syl4);
									p.setSlogov(syls4);
									p.setChislo("ед");
									p.setPadezh("твор");
									break;
								case 5 :
									System.out.println("Ед.ч. предл.п. муж.р. О КОТЕ: ");
									p.setSlovo(input.next());
									System.out.println("Количество слогов:");
									int syls5 = input.nextInt();
									System.out.println("Номер ударного слога: ");
									int syl5 = input.nextInt();
									p.setUdarniySlog(syl5);
									p.setSlogov(syls5);
									p.setChislo("ед");
									p.setPadezh("предл");
									break;
								}
							}
							if(k == 1){
								p.setRod("жен");
								switch(j){
								case 0 :
									System.out.println("Ед.ч. им.п. жен.р. КОШКА: ");
									p.setSlovo(input.next());
									System.out.println("Количество слогов:");
									int syls0 = input.nextInt();
									System.out.println("Номер ударного слога: ");
									int syl0 = input.nextInt();
									p.setUdarniySlog(syl0);
									p.setSlogov(syls0);
									p.setChislo("ед");
									p.setPadezh("им");
									break;
								case 1 :
									System.out.println("Ед.ч. вин.п. жен.р. КОШКУ: ");
									p.setSlovo(input.next());
									System.out.println("Количество слогов:");
									int syls1 = input.nextInt();
									System.out.println("Номер ударного слога: ");
									int syl1 = input.nextInt();
									p.setUdarniySlog(syl1);
									p.setSlogov(syls1);
									p.setChislo("ед");
									p.setPadezh("вин");
									break;
								case 2 :
									System.out.println("Ед.ч. род.п. жен.р. КОШКИ: ");
									p.setSlovo(input.next());
									System.out.println("Количество слогов:");
									int syls2 = input.nextInt();
									System.out.println("Номер ударного слога: ");
									int syl2 = input.nextInt();
									p.setUdarniySlog(syl2);
									p.setSlogov(syls2);
									p.setChislo("ед");
									p.setPadezh("род");
									break;
								case 3 :
									System.out.println("Ед.ч. дат.п. жен.р. КОШКЕ: ");
									p.setSlovo(input.next());
									System.out.println("Количество слогов:");
									int syls3 = input.nextInt();
									System.out.println("Номер ударного слога: ");
									int syl3 = input.nextInt();
									p.setUdarniySlog(syl3);
									p.setSlogov(syls3);
									p.setChislo("ед");
									p.setPadezh("дат");
									break;
								case 4 :
									System.out.println("Ед.ч. твор.п. муж.р. КОШКОЙ: ");
									p.setSlovo(input.next());
									System.out.println("Количество слогов:");
									int syls4 = input.nextInt();
									System.out.println("Номер ударного слога: ");
									int syl4 = input.nextInt();
									p.setUdarniySlog(syl4);
									p.setSlogov(syls4);
									p.setChislo("ед");
									p.setPadezh("твор");
									break;
								case 5 :
									System.out.println("Ед.ч. предл.п. муж.р. О КОШКЕ: ");
									p.setSlovo(input.next());
									System.out.println("Количество слогов:");
									int syls5 = input.nextInt();
									System.out.println("Номер ударного слога: ");
									int syl5 = input.nextInt();
									p.setUdarniySlog(syl5);
									p.setSlogov(syls5);
									p.setChislo("ед");
									p.setPadezh("предл");
									break;
								}
							}
							if(k == 2){
								p.setRod("ср");
								switch(j){
								case 0 :
									System.out.println("Ед.ч. им.п. ср.р. ОКНО: ");
									p.setSlovo(input.next());
									System.out.println("Количество слогов:");
									int syls0 = input.nextInt();
									System.out.println("Номер ударного слога: ");
									int syl0 = input.nextInt();
									p.setUdarniySlog(syl0);
									p.setSlogov(syls0);
									p.setChislo("ед");
									p.setPadezh("им");
									break;
								case 1 :
									System.out.println("Ед.ч. вин.п. ср.р. ОКНО: ");
									p.setSlovo(input.next());
									System.out.println("Количество слогов:");
									int syls1 = input.nextInt();
									System.out.println("Номер ударного слога: ");
									int syl1 = input.nextInt();
									p.setUdarniySlog(syl1);
									p.setSlogov(syls1);
									p.setChislo("ед");
									p.setPadezh("вин");
									break;
								case 2 :
									System.out.println("Ед.ч. род.п. ср.р. ОКНА: ");
									p.setSlovo(input.next());
									System.out.println("Количество слогов:");
									int syls2 = input.nextInt();
									System.out.println("Номер ударного слога: ");
									int syl2 = input.nextInt();
									p.setUdarniySlog(syl2);
									p.setSlogov(syls2);
									p.setChislo("ед");
									p.setPadezh("род");
									break;
								case 3 :
									System.out.println("Ед.ч. дат.п. ср.р. ОКНУ: ");
									p.setSlovo(input.next());
									System.out.println("Количество слогов:");
									int syls3 = input.nextInt();
									System.out.println("Номер ударного слога: ");
									int syl3 = input.nextInt();
									p.setUdarniySlog(syl3);
									p.setSlogov(syls3);
									p.setChislo("ед");
									p.setPadezh("дат");
									break;
								case 4 :
									System.out.println("Ед.ч. твор.п. ср.р. ОКНОМ: ");
									p.setSlovo(input.next());
									System.out.println("Количество слогов:");
									int syls4 = input.nextInt();
									System.out.println("Номер ударного слога: ");
									int syl4 = input.nextInt();
									p.setUdarniySlog(syl4);
									p.setSlogov(syls4);
									p.setChislo("ед");
									p.setPadezh("твор");
									break;
								case 5 :
									System.out.println("Ед.ч. предл.п. ср.р. НА ОКНЕ: ");
									p.setSlovo(input.next());
									System.out.println("Количество слогов:");
									int syls5 = input.nextInt();
									System.out.println("Номер ударного слога: ");
									int syl5 = input.nextInt();
									p.setUdarniySlog(syl5);
									p.setSlogov(syls5);
									p.setChislo("ед");
									p.setPadezh("предл");
									break;
								}
							}
							
							dictionary.add(p);
						}
					}
					
					// множ. число
					if(i == 1){
						Prichastie p = new Prichastie();
						p.setPolnoe(true);
						p.setChastRechi("прил");
						p.setRod("нет");
						switch(j){
						case 0 :
							System.out.println("Множ.ч. им.п. КОТЫ: ");
							p.setSlovo(input.next());
							System.out.println("Количество слогов:");
							int syls0 = input.nextInt();
							System.out.println("Номер ударного слога: ");
							int syl0 = input.nextInt();
							p.setUdarniySlog(syl0);
							p.setSlogov(syls0);
							p.setChislo("множ");
							p.setPadezh("им");
							break;
						case 1 :
							System.out.println("Множ.ч. вин.п. КОТОВ: ");
							p.setSlovo(input.next());
							System.out.println("Количество слогов:");
							int syls1 = input.nextInt();
							System.out.println("Номер ударного слога: ");
							int syl1 = input.nextInt();
							p.setUdarniySlog(syl1);
							p.setSlogov(syls1);
							p.setChislo("множ");
							p.setPadezh("вин");
							break;
						case 2 :
							System.out.println("Множ.ч. род.п. КОТОВ: ");
							p.setSlovo(input.next());
							System.out.println("Количество слогов:");
							int syls2 = input.nextInt();
							System.out.println("Номер ударного слога: ");
							int syl2 = input.nextInt();
							p.setUdarniySlog(syl2);
							p.setSlogov(syls2);
							p.setChislo("множ");
							p.setPadezh("род");
							break;
						case 3 :
							System.out.println("Множ.ч. дат.п. КОТАМ: ");
							p.setSlovo(input.next());
							System.out.println("Количество слогов:");
							int syls3 = input.nextInt();
							System.out.println("Номер ударного слога: ");
							int syl3 = input.nextInt();
							p.setUdarniySlog(syl3);
							p.setSlogov(syls3);
							p.setChislo("множ");
							p.setPadezh("дат");
							break;
						case 4 :
							System.out.println("Множ.ч. твор.п. КОТАМИ: ");
							p.setSlovo(input.next());
							System.out.println("Количество слогов:");
							int syls4 = input.nextInt();
							System.out.println("Номер ударного слога: ");
							int syl4 = input.nextInt();
							p.setUdarniySlog(syl4);
							p.setSlogov(syls4);
							p.setChislo("множ");
							p.setPadezh("твор");
							break;
						case 5 :
							System.out.println("Множ.ч. предл.п. О КОТАХ: ");
							p.setSlovo(input.next());
							System.out.println("Количество слогов:");
							int syls5 = input.nextInt();
							System.out.println("Номер ударного слога: ");
							int syl5 = input.nextInt();
							p.setUdarniySlog(syl5);
							p.setSlogov(syls5);
							p.setChislo("множ");
							p.setPadezh("предл");
							break;
						}
						
						dictionary.add(p);
					}
					
					
				}
			}
			
			break;
		case "деепр" :
			Deeprichastie d = new Deeprichastie();
			d.setSlovo(word);
			d.setUdarniySlog(syl);
			d.setSlogov(syls);
			d.setChastRechi("деепр");
			dictionary.add(d);
			break;
		case "союз" :
			Soyuz sz = new Soyuz();
			sz.setSlovo(word);
			sz.setUdarniySlog(syl);
			sz.setSlogov(syls);
			System.out.println("Разряд:");
			sz.setRazryad(input.next());
			sz.setChastRechi("союз");
			dictionary.add(sz);
			break;
		case "част" :
			Chastitsa ch = new Chastitsa();
			ch.setSlovo(word);
			ch.setUdarniySlog(syl);
			ch.setSlogov(syls);
			System.out.println("Разряд:");
			ch.setRazryad(input.next());
			ch.setChastRechi("част");
			dictionary.add(ch);
			break;
		case "предл" :
			Predlog pr = new Predlog();
			pr.setSlovo(word);
			pr.setUdarniySlog(syl);
			pr.setSlogov(syls);
			pr.setChastRechi("предл");
			dictionary.add(pr);
			break;
		case "фикс" :
			PhrasalWord pw = new PhrasalWord();
			pw.setSlovo(word);
			pw.setUdarniySlog(syl);
			pw.setSlogov(syls);
			pw.setChastRechi("фикс");
			dictionary.add(pw);
			break;
		}
	}	
	
	static void stressInput() {
		
		if (paradigma.size() == 0) {
			System.out.println("Загрузка полной парадигмы русского языка...");
			paradigma = dm.readParadigma();
		}
		// list of strings dict contains
		ArrayList<String> stringsInDict = new ArrayList<String>();
		System.out.println("Создание списка размеченных слов...");
		for (Word w : dictionary) {
			String str = w.getSlovo();
			stringsInDict.add(str);
		}
		ArrayList<Word> predlozhka = new ArrayList<Word>();
		System.out.println("Создание предложки...");
		int s = 0;
		int pr = 0;
		int gl = 0;
		int other = 0;
		int notMarkedWords = 0;
		for (Word w : paradigma) {
			if (!stringsInDict.contains(w.getSlovo())) {
				predlozhka.add(w);
				notMarkedWords++;
			}
		}
		System.out.println("Неразмеченных слов: " + notMarkedWords);
		boolean exitStressInput = false;
		int prPodryad = 0;
		while (!exitStressInput) {
			// print a random word with its part of speech (it's enough)
			Word wordToStress = new Word();
			int anzahl = predlozhka.size();
			int nummer = (int) (Math.random() * anzahl);
			wordToStress = predlozhka.get(nummer);
			// избегать чрезмерного колчества прилагательных и причастий!
			
			// Если выбранное на рандом слово - прил или прич...
			if (wordToStress.getChastRechi().equals("прил")) {
				//...поднять счётчик прил/прич
				prPodryad++;
			}
			
			// Если счётчик прил/прич = 2 или больше...
			if (prPodryad > 1) {
				while (true) {
					//...выбирать случайные слова
					nummer = (int) (Math.random() * anzahl);
					wordToStress = predlozhka.get(nummer);
					// если они всё ещё прил/прич, повтрить подбор
					if (wordToStress.getChastRechi().equals("прил"))
						continue;
					else {
						// до тех пор, пока не найдётся другая часть речи, тогда счётчик прил/прич обнуляется, поиск покидается с удовлетв. словом
						prPodryad = 0;
						break;
					}
				}	
			}	
			
			if (wordToStress.getSlovo().charAt(0) == '_')
				wordToStress.setSlovo(wordToStress.getSlovo().substring(1));
			System.out.println(wordToStress.getSlovo());
			System.out.println("[" + wordToStress.getChastRechi() + "]");
			wordToStress.shortRepresentation();
			System.out.println("Количество слогов (-1, чтобы пропустить слово):");
			int sl = input.nextInt();
			if (sl != -1) {
				wordToStress.setSlogov(sl);
				System.out.println("Номер ударного слога:");
				int usl = input.nextInt();
				wordToStress.setUdarniySlog(usl);
				dictionary.add(wordToStress);
			} else {
				continue;
			}
			// ask whether to continue or to exit
			System.out.println("Продолжить? (д/н)");
			String ans = input.next();
			if (!ans.equals("д")) {
				exitStressInput = true;
			}
		}
	}
}
