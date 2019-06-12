package autopoet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SyntaxGenerator {
	
	public Integer index = 0;

	
/*	// Цепь Маркова для моделирования синтаксиса
	int groups = 2;
	double[][] markovChainGroups = new double[groups+2][groups+2];	// 1-4
	
	public void initMarkov() {
		// Цепь Маркова для групп:
		for (int i = 0; i < groups+2; i++) {
			for (int j = 0; j < groups+2; j++) {
				markovChainGroups[i][j] = 0;
			}
		}
		// ВЕРОЯТНОСТИ ПЕРЕХОДА:
		markovChainGroups[0][1] = 0.6;
		markovChainGroups[0][2] = 0.4;
		markovChainGroups[1][2] = 1.0;
		markovChainGroups[2][1] = 1.0;
		markovChainGroups[1][3] = 1.0;
		markovChainGroups[2][3] = 1.0;
		
		
	}*/
	
	// В перспективе синтаксисы будут не выбираться из базы данных, а создаваться по некоторому набору правил.
	public Syntax createSyntax (ArrayList<Syntax> sdict) {
		Syntax s = new Syntax();
		
		// Вариант с последовательным построением предложения
		// s = generateSyntax();
		
		// Вариант с выбором синтаксиса из базы данных
		int anzahl = sdict.size();
		int nummer = (int) (Math.random() * anzahl);
		s = sdict.get(nummer);
		
		return s;
	}
	
	public double averageWordsASentence (ArrayList<Syntax> sdict) {
		double awas = 0;
		int wordsTotal = 0;
		int syntaxTotal = sdict.size();
		for (Syntax s : sdict){
			for (Parameter p : s.getStructure()){
				if (!p.getPriznaki()[0].equals("знак")){
					wordsTotal++;
				}
			}
		}
		awas = (double) (wordsTotal) / (double) (syntaxTotal);
		return awas;
	}
	
	public Syntax generateSyntax() {
		
		Syntax s = new Syntax();
		
		// equals 0 if not used
		// equals 1 if used
		int[] imennieGruppi = new int[2];
		int[] glagolnieGruppi = new int[3];
		int[] chastiPredlozheniya = new int[3];
		
		// zapolnenie chasti 1
		double[] prob_ig_gg = {0.5, 0.5};
		ArrayList<Parameter> g1 = gruppa(randomChoice(prob_ig_gg));
		
		
		

		
		
		
		return s;
	}
	
	int randomChoice (double[] veroyatnosti) {
		int ans = 0;
		double rnd = Math.random();
		double sum1 = 0;
		double sum2 = 0;
		for (int i = 0; i < veroyatnosti.length; i++) {
			if (i != 0)
				sum1 += veroyatnosti[i-1];
			sum2 += veroyatnosti[i];
			if (rnd >= sum1 && rnd < sum2) {
				ans = i;
				break;
			}
		}
		return ans;
	}
	
	String[] vektorPriznakov (String chlenPredlozheniya, String[] soglasovanie) {
		String[] priznaki = new String[7];
		// ВНИМАНИЕ: повышение индекса всегда на 1!
		// Это повышение должно быть модифицировано при генерации группы, если требуется другой индекс
		if (chlenPredlozheniya.equals("Опр")) {
			// Прилагательные обычно должны согласоваться с существительными
			// в роде, числе и падеже
			priznaki[0] = index.toString();
			priznaki[1] = "прил";
			priznaki[2] = soglasovanie[2];
			priznaki[3] = soglasovanie[3];
			priznaki[4] = soglasovanie[4];
			priznaki[5] = "да";
		}
		
		return priznaki;
	}
	
	// IG = 0, GG = 1
	ArrayList<Parameter> gruppa (int tip) {
		ArrayList<Parameter> gruppa = new ArrayList<Parameter>();
		if (tip == 0) {
			gruppa = imennayaGruppa();
		}
		if (tip == 1) {
			gruppa = glagolnayaGruppa();
		}
		return gruppa;
	}
	
	ArrayList<Parameter> imennayaGruppa () {
		ArrayList<Parameter> gruppa = new ArrayList<Parameter>();
		// П или Опр(П)+П
		double[] prob1 = {0.4, 0.6};
		int t = randomChoice(prob1);
		// P
		if (t == 0) {
			Parameter param = new Parameter();
			double[] prob2 = {0.6, 0.4};
			int t2 = randomChoice(prob2);
			// susch
			if (t2 == 0) {
				param.setOnePriznak(0, index.toString());
				index++;
				param.setOnePriznak(1, "сущ");
				param.setOnePriznak(2, "им");
				// rod / mnozh.ch.
				double[] prob3 = {0.25, 0.25, 0.25, 0.25};
				int t3 = randomChoice(prob3);
				// muzh.
				if (t3 == 0) {
					param.setOnePriznak(3, "муж");
					param.setOnePriznak(4, "ед");
				}
				// zhen.
				if (t3 == 1) {
					param.setOnePriznak(3, "жен");
					param.setOnePriznak(4, "ед");
				}
				// sr.
				if (t3 == 2) {
					param.setOnePriznak(3, "ср");
					param.setOnePriznak(4, "ед");
				}
				// mnozh.
				if (t3 == 3) {
					param.setOnePriznak(3, "нет");
					param.setOnePriznak(4, "множ");
				}
			}
			// lichn. mest
			if (t2 == 1)
			{
				param.setOnePriznak(0, index.toString());
				index++;
				param.setOnePriznak(1, "мест");
				param.setOnePriznak(2, "сущ");
				param.setOnePriznak(4, "им");
				// разряд по значению (личн1, личн2, личн3)
				double[] prob4 = {0.4, 0.2, 0.4};
				int t4 = randomChoice(prob4);
				// ед/множ для 1 и 2 лица
				double[] prob5 = {0.5, 0.5};
				int t5 = randomChoice(prob5);
				// личн1
				if (t4 == 0) {
					param.setOnePriznak(3, "личн1");
					param.setOnePriznak(5, "1лицо");
					if (t5 == 0)
						param.setOnePriznak(6, "ед");
					else
						param.setOnePriznak(6, "множ");
				}
				// личн2
				if (t4 == 1) {
					param.setOnePriznak(3, "личн2");
					param.setOnePriznak(5, "2лицо");
					if (t5 == 0)
						param.setOnePriznak(6, "ед");
					else
						param.setOnePriznak(6, "множ");
				}
				// личн3
				if (t4 == 2) {
					param.setOnePriznak(3, "личн3");
					// rod / mnozh.ch.
					double[] prob3 = {0.25, 0.25, 0.25, 0.25};
					int t3 = randomChoice(prob3);
					// muzh.
					if (t3 == 0) {
						param.setOnePriznak(5, "муж");
						param.setOnePriznak(6, "ед");
					}
					// zhen.
					if (t3 == 1) {
						param.setOnePriznak(5, "жен");
						param.setOnePriznak(6, "ед");
					}
					// sr.
					if (t3 == 2) {
						param.setOnePriznak(5, "ср");
						param.setOnePriznak(6, "ед");
					}
					// mnozh.
					if (t3 == 3) {
						param.setOnePriznak(5, "нет");
						param.setOnePriznak(6, "множ");
					}
				}
				
			}
			gruppa.add(param);
		} // конец П
		
		// Oprp + P
		if (t == 1) {
			// Сначала создаётся подлежащее, затем определение к нему (используя функию векторПризнаков)
			Parameter param = new Parameter();
			// susch
			index++;
			param.setOnePriznak(0, index.toString());
			index--;
			param.setOnePriznak(1, "сущ");
			param.setOnePriznak(2, "им");
			// rod / mnozh.ch.
			double[] prob3 = {0.25, 0.25, 0.25, 0.25};
			int t3 = randomChoice(prob3);
			// muzh.
			if (t3 == 0) {
				param.setOnePriznak(3, "муж");
				param.setOnePriznak(4, "ед");
			}
			// zhen.
			if (t3 == 1) {
				param.setOnePriznak(3, "жен");
				param.setOnePriznak(4, "ед");
			}
			// sr.
			if (t3 == 2) {
				param.setOnePriznak(3, "ср");
				param.setOnePriznak(4, "ед");
			}
			// mnozh.
			if (t3 == 3) {
				param.setOnePriznak(3, "нет");
				param.setOnePriznak(4, "множ");
			}
			// Подлежащее сконфигурировано
			// Генерация подходящего определения
			Parameter param_opr = new Parameter();
			param_opr.setPriznaki(vektorPriznakov("Опр", param.getPriznaki()));
			// Определение сконфигурировано
			gruppa.add(param_opr);
			gruppa.add(param);
			// Замена индекса: "перепрыгнуть" через П.
			index += 2;
		} // конец Опр(П) + П
		
		// Дп или Опр + Дп
		// Либо с предлогом, либо в род.п. (метафора!)
		
		return gruppa;
	}
	
	ArrayList<Parameter> glagolnayaGruppa () {
		ArrayList<Parameter> gruppa = new ArrayList<Parameter>();

		return gruppa;
	}

}
