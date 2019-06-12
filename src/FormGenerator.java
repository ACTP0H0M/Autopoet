package autopoet;

import java.util.Scanner;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FormGenerator {
	
	public int strofy;					//кол-во строф в стихотворении		
	public int strokVStrofe;			//кол-во строк в строфе
	public int[] slogovVStroke;			//вектор для количества слогов в каждой из строк
	public int[][] nomeraUdSlogov;		//матрица для обозначения ударных слогов
	public int razmer;					//зашифрованный стихотворный размер
	public int stopy;					//кол-во стоп (сильных слогов) в строке
	public int[] rifmy; 				//вектор с типами рифм (м/ж/дактилическая)
	public boolean[][] equalEnding;		//матрица, указывающая, рифмуются ли две строки в строфе
	int actualLine = 0;
	int actualBlock = 0;
	public ArrayList<Word> lastWords;	// to check rhyme
	public ArrayList<String> allWords = new ArrayList<String>();	// to check repeating
	public ArrayList<String> sentTempWords = new ArrayList<String>();	// to check repeating
	public HashMap<Character, Character> pokhSogl;	// хэш похожих согласных (?)
	public Set<Map.Entry<Character, Character>> keyset;
	
	// хэш с парными согласными или сочетаниями согласных, которые звучат одинаково
	public void initHash() {
		pokhSogl = new HashMap<Character, Character>();
		pokhSogl.put('б', 'п');
		pokhSogl.put('в', 'ф');
		pokhSogl.put('г', 'к');
		pokhSogl.put('д', 'т');
		pokhSogl.put('ж', 'ш');
		pokhSogl.put('з', 'с');
		pokhSogl.put('м', 'н');
		pokhSogl.put('р', 'л');
		keyset = pokhSogl.entrySet();
	}
	
	public void setSlogaVStroke(){
		//чтобы знать, сколько всего слогов в каждой из строк стихотворения
		int stroki = strofy*strokVStrofe;
		slogovVStroke = new int[stroki];
		//0 1 2 3 4 ...
		for(int i = 0; i < stroki; i++){
			//хорей
			if(razmer == 1){
				//рифма мужская
				if(rifmy[i%strokVStrofe] == 0)
					slogovVStroke[i] = 2 * stopy - 1;
				//рифма женская
				if(rifmy[i%strokVStrofe] == 1)
					slogovVStroke[i] = 2 * stopy;	
				//рифма дактилическая
				if(rifmy[i%strokVStrofe] == 2)
					slogovVStroke[i] = 2 * stopy + 1;
			}
			//ямб
			if(razmer == 2){
				//рифма мужская
				if(rifmy[i%strokVStrofe] == 0)
					slogovVStroke[i] = 2 * stopy;
				//рифма женская
				if(rifmy[i%strokVStrofe] == 1)
					slogovVStroke[i] = 2 * stopy + 1;	
				//рифма дактилическая
				if(rifmy[i%strokVStrofe] == 2)
					slogovVStroke[i] = 2 * stopy + 2;
			}
			//дактиль
			if(razmer == 3){
				//рифма мужская
				if(rifmy[i%strokVStrofe] == 0)
					slogovVStroke[i] = 3 * stopy - 2;
				//рифма женская
				if(rifmy[i%strokVStrofe] == 1)
					slogovVStroke[i] = 3 * stopy - 1;	
				//рифма дактилическая
				if(rifmy[i%strokVStrofe] == 2)
					slogovVStroke[i] = 3 * stopy;
			}
			//амфибрахий
			if(razmer == 4){
				//рифма мужская
				if(rifmy[i%strokVStrofe] == 0)
					slogovVStroke[i] = 3 * stopy - 1;
				//рифма женская
				if(rifmy[i%strokVStrofe] == 1)
					slogovVStroke[i] = 3 * stopy;	
				//рифма дактилическая
				if(rifmy[i%strokVStrofe] == 2)
					slogovVStroke[i] = 3 * stopy + 1;
			}
			//анапест
			if(razmer == 5){
				//рифма мужская
				if(rifmy[i%strokVStrofe] == 0)
					slogovVStroke[i] = 3 * stopy;
				//рифма женская
				if(rifmy[i%strokVStrofe] == 1)
					slogovVStroke[i] = 3 * stopy + 1;	
				//рифма дактилическая
				if(rifmy[i%strokVStrofe] == 2)
					slogovVStroke[i] = 3 * stopy + 2;
			}
			//пользовательский размер
			
			//тонические стихи
			
		}
		System.out.println("Вектор слогов в строке создан.");
	}

	public void setUdSloga() {
		nomeraUdSlogov = new int[strofy*strokVStrofe][stopy];
		//порядковые номера слогов в строке начинаются с 1
		for(int i = 0; i < strofy*strokVStrofe; i++){
			for(int j = 0; j < stopy; j++){
				if(razmer == 1){
					nomeraUdSlogov[i][j] = 2 * j + 1;
				}
				if(razmer == 2){
					nomeraUdSlogov[i][j] = 2 * j + 2;
				}
				if(razmer == 3){
					nomeraUdSlogov[i][j] = 3 * j + 1;
				}
				if(razmer == 4){
					nomeraUdSlogov[i][j] = 3 * j + 2;
				}
				if(razmer == 5){
					nomeraUdSlogov[i][j] = 3 * j + 3;
				}
				//other...

			}
		}
		System.out.println("Матрица ударных слогов вычислена.");
	}
	
	public void setRifmy(String r) {
		rifmy = new int[strokVStrofe];
		char[] letters = r.toCharArray();
		for(int i = 0; i < strokVStrofe; i++) {
			if(letters[i] == 'А' || letters[i] == 'Б' || letters[i] == 'В' || letters[i] == 'Г'){
				rifmy[i] = 0;
			}
			if(letters[i] == 'а' || letters[i] == 'б' || letters[i] == 'в' || letters[i] == 'г'){
				rifmy[i] = 1;
			}
			if(letters[i] == 'д' || letters[i] == 'е'){
				rifmy[i] = 2;
			}
		}
		System.out.println("Вектор с типами рифм записан.");
	}

	public void setEqualEnding(String r) {
		equalEnding = new boolean[strokVStrofe][strokVStrofe];
		char[] letters = r.toCharArray();
		for(int i = 0; i < strokVStrofe; i++){
			for(int j = 0; j < strokVStrofe; j++){
				if(letters[i] == letters[j])
					equalEnding[i][j] = true;
				else
					equalEnding[i][j] = false;
			}
		}
		System.out.println("Матрица рифмовки создана.");
	}

	public void composeBeta(ArrayList<Word> dict, ArrayList<Syntax> sdict, NeuralNet nn) {
		Scanner scanner = new Scanner(System.in, "UTF-8");
		System.out.println("Выберите стихотворный размер:");
		System.out.println("1 = хорей");
		System.out.println("2 = ямб");
		System.out.println("3 = дактиль");
		System.out.println("4 = амфибрахий");
		System.out.println("5 = анапест");
		System.out.println("6 = дольник");
		razmer = scanner.nextInt();
		System.out.println("Укажите длину стихотворения в строфах:");
		strofy = scanner.nextInt();
		System.out.println("Укажите количество стоп в строке:");
		stopy = scanner.nextInt();
		System.out.println("Введите схему рифмовки (используйте а/б/в/г для женских, А/Б/В/Г для мужских, д/е для дактилических рифм):");
		String a = scanner.next();
		System.out.println("Схема рифмовки: " + a);
		char[] b = a.toCharArray();
		strokVStrofe = b.length;
		setRifmy(a);
		setEqualEnding(a);
		setSlogaVStroke();
		setUdSloga();

            	
            	String stikh = "";

						if (razmer != 6) {
							for (int i = 0; i < strofy; i++) {
								String strofa = composeBlock(dict, sdict);
								stikh += strofa;
								actualBlock++;
							}
						} else {
							// compose Dol'nik
							for (int i = 0; i < strofy; i++) {
								String strofa = composeDolnik(dict, sdict);
								stikh += strofa;
								actualBlock++;
							}

						} 
					
				System.out.println("================================");
        		System.out.println(stikh);
        		System.out.println("================================");

        		/* do something with STIKH (like assessment with a neural net)
        		
        		// Обучение на основе синтаксических признаков! Нужна кодировка всех возможных форм слов в виде числа.
        		
        		// стринг со всеми словами стихотворения
        		String[] words = stikh.split("\\s+");
        		for (int i = 0; i < 40; i++) {
        			if (i < words.length) {
        				// удаление всех знаков препинания
						words[i].replaceAll(".", "");
						words[i].replaceAll("!", "");
						words[i].replaceAll("-", "");
						words[i].replaceAll(":", "");
						words[i].replaceAll(";", "");
						words[i].trim();
						// поиск каждого из слов в словаре
						for (Word w : dict) {
							if (w.getSlovo().equals(words[i])) {
								// входные данные - кодировка синтаксических признаков
								nn.inputLayer[i] = nn.encodeWord(w);
								break;
							}
						} 
					} else {
						break;
					}
        		}
        		
        		nn.forwardPass(nn.inputLayer, scanner);
        		nn.backprop();
        		
        		*/
		
	}
	
	// average number of syllables in a word	
	public double averageSylsAWord (ArrayList<Word> wd){
		int c = 0;
		int d = 0;
		double temp = 0;
		for (Word w : wd) {
			d++;
			c += w.getSlogov();
		}
		temp = (double) (c) / (double) (d);
		return temp;
	}

	// syllables in a block
	public int sylsABlock () {
		int temp = 0;
		for (int i = 0; i < strokVStrofe; i++) {
			temp += slogovVStroke[i];
		}
		return temp;
	}
	
	// содержит ли слово подходящее ударение?
	public boolean containsNeededStress (int firstSyllable, int syllableUnderStress, int line) {
		boolean contains = false;
		for (int i = 0; i < nomeraUdSlogov[line].length; i++) {
			if (firstSyllable + syllableUnderStress - 1 == nomeraUdSlogov[line][i]) {
				contains = true;
				break;
			}
		}
		// в конце строки ударение должно быть только на последний или предпоследний слог
		int lastStrSyl = nomeraUdSlogov[line].length - 1;
		if (endOfLine(line, firstSyllable)){
			if (firstSyllable + syllableUnderStress - 1 == nomeraUdSlogov[line][lastStrSyl]) {
				contains = true;
			} else {
				contains = false;
			}
		}
		return contains;
	}
	
	// исходная длина слова в слогах (обычно 5, в конце строки - меньше 4)
	public int lengthInSyllables (int firstSyllable, int line) {
		// returns 5 if not end of line
		int length = 5;
		if (slogovVStroke[line] - firstSyllable + 1 < 3) {
			length = slogovVStroke[line] - firstSyllable + 1;
		}
		return length;
	}
	
	// конец строки, если первый слог след. слова - третий с конца
	public boolean endOfLine (int line, int fs) {
		boolean endOfLine = false;
		if (slogovVStroke[line] - fs < 3) {
			endOfLine = true;
		}
		return endOfLine;
	}
	
	// возвращает список слов с совпадающими грамматическими признаками
	public ArrayList<Word> findSuitableWords (Parameter p, ArrayList<Word> wrds) {
		
		ArrayList<Word> candidates = new ArrayList<Word>();
		
		for (Word w : wrds) {
			if (w.getChastRechi().equals(p.getPriznaki()[0])) {

				switch (w.getChastRechi()) {

				case "сущ":

					Suschestvitelnoe susch = (Suschestvitelnoe) (w);
					if (susch.getPadezh().equals(p.getPriznaki()[1])
							&& susch.getRod().equals(p.getPriznaki()[2])
							&& susch.getChislo().equals(p.getPriznaki()[3])) {
						candidates.add(susch);
						
					}
					break;

				case "прил":

					Prilagatelnoe pril = (Prilagatelnoe) (w);
					String x;
					if (pril.isPolnoe())
						x = "да";
					else
						x = "нет";
					if (pril.getPadezh().equals(p.getPriznaki()[1])
							&& pril.getRod().equals(p.getPriznaki()[2])
							&& pril.getChislo().equals(p.getPriznaki()[3])
							&& x.equals(p.getPriznaki()[4])) {
						candidates.add(pril);
						
					}
					break;

				case "глаг":

					Glagol glag = (Glagol) (w);
					String x1;
					if (glag.isPerekhod())
						x1 = "да";
					else
						x1 = "нет";
					if (glag.getVremya().equals(p.getPriznaki()[1])
							&& glag.getChislo().equals(p.getPriznaki()[2])
							&& glag.getRod().equals(p.getPriznaki()[3])
							&& x1.equals(p.getPriznaki()[4])) {
						candidates.add(glag);
						
					}
					break;

				case "мест":

					Mestoimenie mest = (Mestoimenie) (w);
					if (mest.getRazryadGr().equals(p.getPriznaki()[1])
							&& mest.getRazryadZn().equals(p.getPriznaki()[2])
							&& mest.getPadezh().equals(p.getPriznaki()[3])
							&& mest.getRod().equals(p.getPriznaki()[4])
							&& mest.getChislo().equals(p.getPriznaki()[5])) {
						candidates.add(mest);
						
					}
					break;

				case "нар":

					Narechie nar = (Narechie) (w);
					candidates.add(nar);
				
					break;

				case "прич":

					Prichastie prich = (Prichastie) (w);
					String x2;
					if (prich.isPolnoe())
						x2 = "да";
					else
						x2 = "нет";
					if (prich.getPadezh().equals(p.getPriznaki()[1])
							&& prich.getRod().equals(p.getPriznaki()[2])
							&& prich.getChislo().equals(p.getPriznaki()[3])
							&& x2.equals(p.getPriznaki()[4])) {
						candidates.add(prich);
	
					}
					break;

				case "деепр":

					Deeprichastie deepr = (Deeprichastie) (w);
					candidates.add(deepr);
					
					break;

				case "союз":

					Soyuz sz = (Soyuz) (w);
					if (sz.getSlovo().equals(p.getPriznaki()[1])) {
						candidates.add(sz);
						
					}
					break;

				case "част":

					Chastitsa ch = (Chastitsa) (w);
					if (ch.getSlovo().equals(p.getPriznaki()[1])) {
						candidates.add(ch);
						
					}
					break;

				case "предл":

					Predlog pr = (Predlog) (w);
					if (pr.getSlovo().equals(p.getPriznaki()[1])) {
						candidates.add(pr);
					
					}
					break;
					
				case "фикс" :
					PhrasalWord pw = (PhrasalWord) (w);
					if (pw.getSlovo().equals(p.getPriznaki()[1])) {
						candidates.add(pw);
					}
					break;
				}
			}
		}
		return candidates;
	}
	
	// выбирает случайное слово из данного списка
	public Word randomWord (ArrayList<Word> wrds) {
		Word wrd = new Word();
		int anzahl = wrds.size();
		int nummer = (int) (Math.random() * anzahl);
		wrd = wrds.get(nummer);
		return wrd;
	}
	
	// сочиняет строфу
	/**
	 * @param dict
	 * @param sdict
	 * @return
	 */
	public String composeBlock (ArrayList<Word> dict, ArrayList<Syntax> sdict) {
		lastWords = new ArrayList<Word>();
		String block = "";
		int firstSyl = 1;
		SyntaxGenerator sg = new SyntaxGenerator();
		boolean blockEnd = false;
		double asaw = averageSylsAWord(dict);
		System.out.println("Слогов/слово: " + asaw);
		double awas = sg.averageWordsASentence(sdict);
		System.out.println("Слов/предложение: " + awas);
		
		while (!blockEnd) {
			int syls = sylsABlock();
			int words = (int) (syls / asaw);
			System.out.println("Прогнозируемое кол-во слов в строфе: " + words);
			// рассчитывается прмерное количество предложений в строфе
			int sent = (int) (words / awas) + 4;
			System.out.println("Прогнозируемое кол-во предложений в строфе: " + sent);
			// последняя строка последнего удачно написанного предложения
			int tempLine = 0;
			// последний слог в строке удачно написанного предложения
			int tempSyl = 1;
			// нет проблем с написанием предложения
			boolean sentError = false;
			
			long lastSentFails = 0;
			
			// итерация по запланированному количеству предложений
			for (int i = 0; i < sent; i++) {
				
				int n = i + 1;
				// System.out.println(">	Номер предложения: " + n + "/" + sent);
				// System.out.println("АКТУАЛЬНАЯ ВЕРСИЯ:");
				// System.out.println(block);
				// создать пустой стринг для предложения
				String satz = "";
				// выбрать синтаксис наугад
				Syntax s = sg.createSyntax(sdict);
				
				// System.out.println(">	Синтаксис выбран");
				// если не удалось заполнить синтаксис словами
				if (sentError) {
					
					// выйти из цикла, если слишком долго не находится конец стихотворения
					if (n >= sent - 2) {
						lastSentFails++;
						if (lastSentFails > 5000) {
							System.out.println("***Не удалось сгенерировать конец стихотворения***");
							break;
						}
					}
					
					// переставить заполняемую строку и слог в строке
					actualLine = tempLine;
					firstSyl = tempSyl;
					// System.out.println(">	Новая попытка составить предложение в строке " + actualLine + ", первый слог " + firstSyl);
				}
				sentError = false;
				ArrayList<Parameter> structure = s.getStructure();

				// итерация по синтаксической структуре
				for (Parameter p : structure) {
					// если на данной позиции не знак препинания
					if (!p.getPriznaki()[0].equals("знак")) {
						// найти слова, подходящие по грамматике (1)
						ArrayList<Word> candidates1 = findSuitableWords(p, dict);
						// клонировать этот список
						ArrayList<Word> candidates = (ArrayList<Word>) candidates1.clone();
						
						
						
						// в исходном списке кандидатов
						
						// отсеять из грамматически подходящих вариантов слова, не подходящие по ударению или не рифмующиеся в конце строки
						for (Word w : candidates1) {
							// если нет нужного ударения
							if (!containsNeededStress(firstSyl,
									w.getUdarniySlog(), actualLine)) {
								// удалить слово из первичного списка
								candidates.remove(w);
							}
							// remove repeating words
							if (isRepeated(w))
								candidates.remove(w);
							
							// если конец строки
							if (endOfLine(actualLine, firstSyl)) {
								// проверка длины слова для конца строки
								if(w.getSlogov() != slogovVStroke[actualLine] - firstSyl + 1){
									candidates.remove(w);
								}
								// для не первого слова в рифмовой группе: если не рифмуется, с чем должно
								if (!rifmovano(actualLine, w, rifmy[actualLine%strokVStrofe])) {
									// убрать из первичного списка
									candidates.remove(w);
								}
							} else {
								// don't allow the word to influence rhymes
								if(firstSyl + w.getSlogov() - 1 >= slogovVStroke[actualLine] - rifmy[actualLine%strokVStrofe])
									candidates.remove(w);
							}
							// если слово не сможет влезть в строку, удалить его из кандидатов
							if(w.getSlogov() > slogovVStroke[actualLine] - firstSyl + 1){
								candidates.remove(w);
							}
						}
						
						// Выбор слова
						
						// в конце строки
						if (endOfLine(actualLine, firstSyl)) {
							// если финальный список не пуст (подходит ритм и рифма)
							if (candidates.size() != 0) {
								// выбрать слово с наиболее высоким индексом рифмовки (ИР)
								Word chosen = bestRhyme(candidates);
								// приписать его к предложению с переносом строки
								satz += chosen.getSlovo() + "\n";
								// добавить выбранное слово во временный список использованных слов
								sentTempWords.add(chosen.getSlovo());
								// System.out.println("~~	Слово записано: " + chosen.getSlovo() + " (Строка " + actualLine + ", первый слог " + firstSyl + ")");
								actualLine++;
								// первый слог = 1
								firstSyl = 1;
								// добавить это слово в список последних слов в строке
								lastWords.add(chosen);
							// если кандидатов не нашлось
							} else {
								// не удалось написать предложение
								sentError = true;
								sentTempWords.clear();
								// System.out.println("~~	Не нашлось слов для составления предложения.");
								// зачеркнуть все записанные слова
								satz = "";
								// удалить последние слова в строках, которые принадлежали этому предложению
								ArrayList<Word> lastWords1 = (ArrayList<Word>) lastWords.clone();
								for (Word wrd : lastWords1) {
									if (lastWords.indexOf(wrd) >= tempLine%strokVStrofe)
										lastWords.remove(wrd);
								}
								break;
							}
						// в "теле" строки
						} else {
							if (candidates.size() != 0) {
								Word chosen = randomWord(candidates);
								satz += chosen.getSlovo() + " ";
								// добавить выбранное слово во временный список использованных слов
								sentTempWords.add(chosen.getSlovo());
								// System.out.println("##	Слово записано: " + chosen.getSlovo() + " (Строка " + actualLine + ", первый слог " + firstSyl + ")");
								firstSyl += chosen.getSlogov();
							} else {
								sentError = true;
								sentTempWords.clear();
								// System.out.println("##	Не нашлось слов для составления предложения.");
								satz = "";
								// удалить последние слова в строках, которые принадлежали этому предложению
								ArrayList<Word> lastWords1 = (ArrayList<Word>) lastWords.clone();
								for (Word wrd : lastWords1) {
									if (lastWords.indexOf(wrd) >= tempLine%strokVStrofe)
										lastWords.remove(wrd);
								}
								break;
							}
						}
						
					// если требуется знак препинания
					} else {
						// тупо его приписать
						// System.out.println(p.getPriznaki()[1]);
						satz += p.getPriznaki()[1];
					}
					
					// После заполнения последней ячейки синтаксиса в последнем предложении: проверить, дотянул ли до конца строфы
					if (i == sent - 1 && structure.lastIndexOf(p) == structure.size() - 1){
						if ( actualLine != strokVStrofe || firstSyl != 1){
							// System.out.println("*** Последнее предложение слишком короткое ***");
							
							//test
							sent++;
							
							sentError = true;
						}
					}
					
					// В последней строке выдаётся ошибка предложения, если оно не заканчивается в конце строфы
					if (actualLine == strokVStrofe && firstSyl == 1){
						if (structure.lastIndexOf(p) != structure.size() - 1){
							// System.out.println("+++ Последнее предложение слишком длинное +++");
							sentError = true;
						}
					}
					
					// НЕ ИСПОЛЬЗУЕТСЯ?
					if (sentError)
						// выйти из поиска слов для данного синтаксиса
						break;
					
					// System.out.println(">>	Ячейка синтаксиса заполнена.");
				}
				
				if (sentError) {
					// уменьшить индекс заполняемого предложения, начать заполнять заново
					i--;	
					continue;					
				} else {
					// если получилось написать предложение, сохранить концы
					tempLine = actualLine;
					tempSyl = firstSyl;
					System.out.println(">>	Предложение удачно написано. Текущая строка: " + tempLine + ". Слог: " + tempSyl);
					block += satz;
					// record in the list of words not to repeat
					recordUsedWords(satz);
					// System.out.println(satz);
					if ( actualLine == strokVStrofe && firstSyl == 1){
						System.out.println("*** Строфа сгенерирована с меньшим количеством предложений ***");
						break;
					}
				}				
				
			}
			blockEnd = true;
		}
		return block;
	}
	
	public String composeDolnik (ArrayList<Word> dict, ArrayList<Syntax> sdict) {
		lastWords = new ArrayList<Word>();
		String block = "";
		int sylsInLine = 0;	// stressed
		int firstSyl = 1;
		SyntaxGenerator sg = new SyntaxGenerator();
		boolean blockEnd = false;
		double asaw = averageSylsAWord(dict);
		System.out.println("Слогов/слово: " + asaw);
		double awas = sg.averageWordsASentence(sdict);
		System.out.println("Слов/предложение: " + awas);

		while (!blockEnd) {
			int syls = sylsABlock();
			int words = (int) (syls / asaw);
			System.out.println("Прогнозируемое кол-во слов в строфе: " + words);
			// рассчитывается прмерное количество предложений в строфе
			int sent = (int) (words / awas) + 4;
			System.out.println("Прогнозируемое кол-во предложений в строфе: " + sent);
			// последняя строка последнего удачно написанного предложения
			int tempLine = 0;
			// последний слог в строке удачно написанного предложения
			int tempSyl = 1;
			
			// last stressed syl
			int tempStress = 0;
			
			
			// нет проблем с написанием предложения
			boolean sentError = false;
			
			// итерация по запланированному количеству предложений
			for (int i = 0; i < sent; i++) {
				
				int n = i + 1;
				System.out.println(">	Номер предложения: " + n + "/" + sent);
				System.out.println("АКТУАЛЬНАЯ ВЕРСИЯ:");
				System.out.println(block);
				// создать пустой стринг для предложения
				String satz = "";
				// выбрать синтаксис наугад
				Syntax s = sg.createSyntax(sdict);
				
				System.out.println(">	Синтаксис выбран");
				// если не удалось заполнить синтаксис словами
				if (sentError) {
					// переставить заполняемую строку и слог в строке
					actualLine = tempLine;
					firstSyl = tempSyl;
					
					sylsInLine = tempStress;
					
					System.out.println(">	Новая попытка составить предложение в строке " + actualLine + ", первый слог " + firstSyl);
				}
				sentError = false;
				ArrayList<Parameter> structure = s.getStructure();

				// итерация по синтаксической структуре
				for (Parameter p : structure) {
					// если на данной позиции не знак препинания
					if (!p.getPriznaki()[0].equals("знак")) {
						// найти слова, подходящие по грамматике (1)
						ArrayList<Word> candidates1 = findSuitableWords(p, dict);
						// клонировать этот список
						ArrayList<Word> candidates = (ArrayList<Word>) candidates1.clone();
						
						
						
						// в исходном списке кандидатов
						
						// отсеять из грамматически подходящих вариантов слова, (не подходящие по ударению или) не рифмующиеся в конце строки
						for (Word w : candidates1) {
							
							// remove repeating words
							if (isRepeated(w))
								candidates.remove(w);
							
							// если конец строки
							// in Dol'nik other method!!!
							if (dolnikLineEnd(sylsInLine)) {

								// для не первого слова в рифмовой группе: если не рифмуется, с чем должно
								if (!rifmovano(actualLine, w, rifmy[actualLine%strokVStrofe])) {
									// убрать из первичного списка
									candidates.remove(w);
								}
							}

						}
						
						// Выбор слова
						
						// в конце строки
						if (dolnikLineEnd(sylsInLine)) {
							// если финальный список не пуст (подходит ритм и рифма)
							if (candidates.size() != 0) {
								// выбрать слово наугад
								Word chosen = randomWord(candidates);
								// приписать его к предложению с переносом строки
								satz += chosen.getSlovo() + "\n";
								System.out.println("~~	Слово записано: " + chosen.getSlovo() + " (Строка " + actualLine + ", первый слог " + firstSyl + ")");
								actualLine++;
								// первый слог = 1
								firstSyl = 1;
								
								// go to next line, no syllables are stressed there yet
								sylsInLine = 0;
								
								// добавить это слово в список последних слов в строке
								lastWords.add(chosen);
							// если кандидатов не нашлось
							} else {
								// не удалось написать предложение
								sentError = true;
								System.out.println("~~	Не нашлось слов для составления предложения.");
								// зачеркнуть все записанные слова
								satz = "";
								// удалить последние слова в строках, которые принадлежали этому предложению
								ArrayList<Word> lastWords1 = (ArrayList<Word>) lastWords.clone();
								for (Word wrd : lastWords1) {
									if (lastWords.indexOf(wrd) >= tempLine%strokVStrofe)
										lastWords.remove(wrd);
								}
								break;
							}
						// в "теле" строки
						} else {
							if (candidates.size() != 0) {
								Word chosen = randomWord(candidates);
								satz += chosen.getSlovo() + " ";
								System.out.println("##	Слово записано: " + chosen.getSlovo() + " (Строка " + actualLine + ", первый слог " + firstSyl + ")");
								firstSyl += chosen.getSlogov();
								
								// increment stressed syls according to rules
								if (chosen.getChastRechi().equals("част") || chosen.getChastRechi().equals("мест") || chosen.getChastRechi().equals("союз") || chosen.getChastRechi().equals("предл")) {
									// do nothing... I know, it is bullshit code...
								} else {
									sylsInLine++;
								}
								
							} else {
								sentError = true;
								System.out.println("##	Не нашлось слов для составления предложения.");
								satz = "";
								break;
							}
						}
						
					// если требуется знак препинания
					} else {
						// тупо его приписать
						System.out.println(p.getPriznaki()[1]);
						satz += p.getPriznaki()[1];
					}
					
					// После заполнения последней ячейки синтаксиса в последнем предложении: проверить, дотянул ли до конца строфы
					if (i == sent - 1 && structure.lastIndexOf(p) == structure.size() - 1){
						if ( actualLine != strokVStrofe || firstSyl != 1){
							System.out.println("*** Последнее предложение слишком короткое ***");
							
							//test
							sent++;
							
							sentError = true;
						}
					}
					
					// В последней строке выдаётся ошибка предложения, если оно не заканчивается в конце строфы
					if (actualLine == strokVStrofe && firstSyl == 1){
						if (structure.lastIndexOf(p) != structure.size() - 1){
							System.out.println("+++ Последнее предложение слишком длинное +++");
							sentError = true;
						}
					}
					
					// НЕ ИСПОЛЬЗУЕТСЯ?
					if (sentError)
						// выйти из поиска слов для данного синтаксиса
						break;
					
					System.out.println(">>	Ячейка синтаксиса заполнена.");
				}
				
				if (sentError) {
					// уменьшить индекс заполняемого предложения, начать заполнять заново
					i--;	
					continue;					
				} else {
					// если получилось написать предложение, сохранить концы
					tempLine = actualLine;
					tempSyl = firstSyl;
					tempStress = sylsInLine;
					System.out.println(">>	Предложение удачно написано. Текущая строка: " + tempLine + ". Слог: " + tempSyl);
					block += satz;
					// record in the list of words not to repeat
					recordUsedWords(satz);
					System.out.println(satz);
					if ( actualLine == strokVStrofe && firstSyl == 1){
						System.out.println("*** Строфа сгенерирована с меньшим количеством предложений ***");
						break;
					}
				}				
				
			}
			blockEnd = true;
		}
		
		
		
		return block;
	} 

	public boolean dolnikLineEnd (int stressedSyls) {
		if (stressedSyls == stopy - 1) {
			return true;
		} else
			return false;
	}
	
	
	public int sylsInBlockLeft (int line, int fs) {
		int sylsInBlock = 0;
		for (int i = 0; i < strokVStrofe; i++) {
			sylsInBlock += slogovVStroke[i];
		}
		int lineInBlock = line % strokVStrofe;
		int sumSyls = 0;
		for (int j = 0; j < lineInBlock; j++) {
			sumSyls += slogovVStroke[j];
		}
		sumSyls = sumSyls + fs - 1;
		return sylsInBlock - sumSyls;
	}
	
	/*
	 * Возвращает "верно", если слово не обязано ни с чем рифмоваться или является рифмой в схеме строфы
	 */
	public boolean rifmovano (int line, Word w, int tipRifmy) {
		// номер строки (от 0)
		int lineInBlock = actualLine % strokVStrofe;
		if (lineInBlock == 0) {
			// если первая строка в строфе - не надо ни с чем рифмовать
			return true;
		} else {
			// если строка не первая в строфе...
			boolean first = true;	// является ли данная строка тем не менее свободной в выборе окончания?
			int toRhymeWith = 0;	// индекс строки, с которой надо рифмовать
			// прочёсываем все предыдущие строки
			for (int i = lineInBlock - 1; i >= 0; i--) {
				// если одна из предыдущих строк - в паре с данной строкой...
				if (equalEnding[lineInBlock][i]) {
					//... то эта строка не свободна
					first = false;
					toRhymeWith = i;
					break;
				}
			}
			if (first) {
				// свободная строка не обязана ни с чем рифмоваться
				return true;
			} else {
				// несвободная строка
				if (rifma(w, lastWords.get(toRhymeWith), tipRifmy)) {
					// проверка на рифму (фонетически)
					return true;
				} else {
					return false;
				}	
			}
		}
	}
	
	// Проверяет два слова на рифму (согласно типу рифмы)
	public boolean rifma (Word w1, Word w2, int tr) {
		// ответ
		boolean r = false;
		// сами слова
		String word1 = w1.getSlovo();
		String word2 = w2.getSlovo();
		// количество слогов в словах 1 и 2
		int slogov1 = 0, slogov2 = 0;
		// разбить каждое слово на буквы
		char[] c1 = word1.toCharArray();
		char[] c2 = word2.toCharArray();
		// подсчёт количества слогов (= кол-во гласных букв)
		for (int i = 0; i < c1.length; i++) {
			if (vowel(c1[i]))
				slogov1++;
		}
		for (int i = 0; i < c2.length; i++) {
			if (vowel(c2[i]))
				slogov2++;
		}
		// векторы гласных букв для слов 1 и 2
		char[] glasn1 = new char[slogov1];
		char[] glasn2 = new char[slogov2];
		int index1 = 0;
		int index2 = 0;
		for (int i = 0; i < c1.length; i++) {
			if (vowel(c1[i])) {
				glasn1[index1] = c1[i];
				index1++;
			}	
		}
		for (int i = 0; i < c2.length; i++) {
			if (vowel(c2[i])) {
				glasn2[index2] = c2[i];
				index2++;
			}	
		}
		
		// сравнение согласных
		
		if (slogov1 != 0) {
			if (tr == 0) { // muzhskaya rifma
				// если последние гласные совпадают, то это мужская рифма... Какой наивный подход!
				char poslGlasn1 = glasn1[slogov1 - 1];
				char poslGlasn2 = glasn2[slogov2 - 1];
				if ((poslGlasn1 == poslGlasn2) || similarVowel(poslGlasn1, poslGlasn2, true, true)) {
					// one more condition - about the consonants...
					return true;
				}
			}
			if (tr == 1) { // zhenskaya rifma
				// последние гласные
				char poslGlasn1 = glasn1[slogov1 - 1];
				char poslGlasn2 = glasn2[slogov2 - 1];
				// если в обоих словах больше одного слога...
				if (slogov1 > 1 && slogov2 > 1) {
					//...то запиши также предпоследний слог
					char ppg1 = glasn1[slogov1 - 2];
					char ppg2 = glasn2[slogov2 - 2];
					// и если последние гласные похожи, а предпоследние совпадают, то это женская рифма
					if (similarVowel(poslGlasn1, poslGlasn2, false, false) || poslGlasn1 == poslGlasn2) {
						if (similarVowel(ppg1, ppg2, true, true) || ppg1 == ppg2)
							return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			}
			if (tr == 2) { // daktilicheskaya rifma
				char poslGlasn1;
				char poslGlasn2;
				if (slogov1 > 1 && slogov2 > 1) {
					poslGlasn1 = glasn1[slogov1 - 2];
					poslGlasn2 = glasn2[slogov2 - 2];
				} else {
					return false;
				}
				if (slogov1 > 2 && slogov2 > 2) {
					char ppg1 = glasn1[slogov1 - 3];
					char ppg2 = glasn2[slogov2 - 3];
					if (similarVowel(poslGlasn1, poslGlasn2, false, false) || poslGlasn1 == poslGlasn2) {
						if (similarVowel(ppg1, ppg2, true, true) || ppg1 == ppg2)
							return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			} 
		} else {
			r = false;
		}
		return r;
	}
	
	public boolean similarVowel (char v1, char v2, boolean ud1, boolean ud2) {
		if (ud1 && ud2) {
			String a = "уеыаоюэияё";
			String b = "юэияёуеыао";
			char[] ac = a.toCharArray();
			char[] bc = b.toCharArray();
			int index = 0;
			for (int j = 0; j < ac.length; j++) {
				if (ac[j] == v1)
					index = j;
			}
			if (bc[index] == v2)
				return true;
		}
		if (!ud1 && !ud2) {
			if ((v1 == 'у' && v2 == 'ю') || (v1 == 'ю' && v2 == 'у'))
				return true;
			if ((v1 == 'у' && v2 == 'ю') || (v1 == 'ю' && v2 == 'у'))
				return true;
			if ((v1 == 'е' && v2 == 'и') || (v1 == 'и' && v2 == 'е'))
				return true;
			if ((v1 == 'ы' && v2 == 'и') || (v1 == 'и' && v2 == 'ы'))
				return true;
			if ((v1 == 'а' && v2 == 'о') || (v1 == 'о' && v2 == 'а'))
				return true;
			if ((v1 == 'я' && v2 == 'а') || (v1 == 'а' && v2 == 'я'))
				return true;
			if ((v1 == 'я' && v2 == 'и') || (v1 == 'и' && v2 == 'я'))
				return true;
			if ((v1 == 'я' && v2 == 'ю') || (v1 == 'ю' && v2 == 'я'))
				return true;
			if ((v1 == 'е' && v2 == 'ю') || (v1 == 'ю' && v2 == 'е'))
				return true;
			if ((v1 == 'и' && v2 == 'ю') || (v1 == 'ю' && v2 == 'и'))
				return true;
		}
		return false;
	}
	
	public boolean vowel (char l) {
		boolean ans = false;
		String vowels = "уеыаоэяиюё";
		char[] v = vowels.toCharArray();
		for (int i = 0; i < 10; i++) {
			if (v[i] == l)
				return true;
		}
		return ans;	
	}
	
	public boolean isRepeated (Word wordToCheck) {
		boolean sovpad = false;
		String test = wordToCheck.getSlovo();
		float x = 0.7f;
		char[] testLetters = test.toCharArray();
		// big list
		ArrayList<String> wrds = new ArrayList<String>();
		for (String w : allWords)
			wrds.add(w);
		for (String w1 : sentTempWords)
			wrds.add(w1);	
		for (String wrd : wrds) {
			char[] wrdLetters = wrd.trim().toLowerCase().toCharArray();
			int testLength = testLetters.length;
			int sampleMatch = 0;
			int lower = 0;
			if (testLength >= wrdLetters.length) {
				lower = wrdLetters.length;
			} else {
				lower = testLength;
			}
			
			for (int i = 0; i < lower; i++) {
				if (testLetters[i] == wrdLetters[i])
					sampleMatch++;
			}
			if (testLength >= 3 && sampleMatch/testLength >= x) {
				sovpad = true;
				break;
			}
		}
		return sovpad;
		
		/*
		 * ПРОСТОЙ ВАРИАНТ
		if (allWords.contains(wordToCheck.getSlovo().toLowerCase()))
			return true;
		else
			return false;
		*/
	}
	
	public void recordUsedWords (String sentence) {
		String[] wrds = sentence.split("\\s+");
		for (int i = 0; i < wrds.length; i++) {
			wrds[i].trim().toLowerCase().replaceAll("\\W", "");
			if (!wrds[i].equals(""))
				allWords.add(wrds[i]);
		}
	}

	public Word bestRhyme (ArrayList<Word> candidates) {
		
		// номер строки (от 0)
		int lineInBlock = actualLine % strokVStrofe;
		if (lineInBlock == 0) {
			// если первая строка в строфе - не надо ни с чем рифмовать
			return randomWord(candidates);
		} else {
		// если строка не первая в строфе...
			boolean first = true;	// является ли данная строка тем не менее свободной в выборе окончания?
			int toRhymeWith = 0;	// индекс строки, с которой надо рифмовать
			// прочёсываем все предыдущие строки
			for (int i = lineInBlock - 1; i >= 0; i--) {
				// если одна из предыдущих строк - в паре с данной строкой...
				if (equalEnding[lineInBlock][i]) {
					//... то эта строка не свободна
					first = false;
					toRhymeWith = i;
					break;
				}
			}
			if (first) {
				// свободная строка не обязана ни с чем рифмоваться
				return randomWord(candidates);
			} else {
				// слово, которое лучше всего рифмуется (по согласным)
				Word best = new Word();
				// целевое слово
				Word target = lastWords.get(toRhymeWith);
				// вектор букв первого (целевого) слова
				char[] tokens1 = target.getSlovo().toCharArray();
				int irMax = 0;
				int ir = 0;
				// Для каждого слова в кандидатах по ассонансной рифме определить ИР, вернуть слово с наибольшим ИР
				for (Word w : candidates) {
					// индекс рифмованности
					ir = 0;
					char[] tokens2 = w.getSlovo().toCharArray();
					// dlina slov v bukvakh
					int l1 = tokens1.length;
					int l2 = tokens2.length;
					// dlina bolee korotkogo
					int kurz;
					if (l1 > l2) {
						kurz = l2;
					} else {
						kurz = l1;
					}
						// probegaet slovo target s kontsa
						for (int i = 1; i <= kurz; i++) {
							char bukva = tokens1[l1 - i];
							// если буква целевого слова не гласная...
							if (!vowel(bukva)) {
								// если буква не гласная...
								// если буква не произносится...
								if (bukva == 'ь' || bukva == 'ъ' || bukva == '_' || bukva == '~') {
									ir += 0;
								} else {
									// если буква - обычная согласная...
									if (tokens2[l2 - i] == bukva)
										ir += 10;
									if (similarConsonant(tokens2[l2 - i], bukva))
										ir += 8;
									try {
										if (tokens2[l2 - i + 1] == bukva)
											ir += 6;
									} catch (ArrayIndexOutOfBoundsException e) {
										// TODO: handle exception
									}
									try {
										if (similarConsonant(tokens2[l2 - i + 1], bukva))
											ir += 4;
									} catch (ArrayIndexOutOfBoundsException e) {
										// TODO: handle exception
									}
									try {
										if (tokens2[l2 - i - 1] == bukva)
											ir += 6;
									} catch (ArrayIndexOutOfBoundsException e) {
										// TODO: handle exception
									}
									try {
										if (similarConsonant(tokens2[l2 - i - 1], bukva))
											ir += 4;
									} catch (ArrayIndexOutOfBoundsException e) {
										// TODO: handle exception
									}
									try {
										if (tokens2[l2 - i + 2] == bukva)
											ir += 4;
									} catch (ArrayIndexOutOfBoundsException e) {
										// TODO: handle exception
									}
									try {
										if (similarConsonant(tokens2[l2 - i + 2], bukva))
											ir += 2;
									} catch (ArrayIndexOutOfBoundsException e) {
										// TODO: handle exception
									}
									try {
										if (tokens2[l2 - i - 2] == bukva)
											ir += 4;
									} catch (ArrayIndexOutOfBoundsException e) {
										// TODO: handle exception
									}
									try {
										if (similarConsonant(tokens2[l2 - i - 2], bukva))
											ir += 2;
									} catch (ArrayIndexOutOfBoundsException e) {
										// TODO: handle exception
									}
								}
							}
						}
						// если ИР больше максимального...
						if (ir >= irMax) {
							irMax = ir;
							best = w;
						} 
					}
				return best;
				}	
			}
		}
	
	public boolean similarConsonant (char c1, char c2) {
		boolean ans = false;
		char keyC2 = '#';
		for (Map.Entry<Character, Character> me : keyset) {
			char v = me.getValue();
			if (v == c2) {
				keyC2 = me.getKey();
				break;
			}
		}
		if (pokhSogl.containsKey(c1)) {
			if (pokhSogl.get(c1) == c2)
				ans = true;
		} else {
			if (keyC2 == c1) {
				ans = true;
			} else {
				ans = false;
			}
		}
		return ans;
	}
	
}
