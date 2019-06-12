package autopoet;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;


public class NeuralNet implements java.io.Serializable {
	
	public double[] inputLayer = new double[40];
	public double[] hidden1 = new double[60];
	public double[] hidden2 = new double[15];
	public double[] outputLayer = new double[3];
	public double[][] first = new double[40][60];
	public double[][] second = new double[60][15];
	public double[][] third = new double[15][3];
	
	public double[] error = new double[3];
	
	public double eta = 20.0;	// learning rate
	
	public NeuralNet(double[][] first, double[][]second, double[][] third, double eta){
		this.first = first;
		this.second = second;
		this.third = third;
		this.eta = eta;
		System.out.println("Создание нейросети завершено.");
	}
	
	public void initNewNet() {
		inputLayer = new double[40];
		hidden1 = new double[60];
		hidden2 = new double[15];
		outputLayer = new double[3];
		first = new double[40][60];
		second = new double[60][15];
		third = new double[15][3];
		System.out.println("Нейросеть инициализирована.");
	}
	
	// resized to 0..1
	public double encodeWord(Word w) {
		// default value 0
		double ans = 0;
		int code = 0;
		String[] chasti_rechi = {"сущ", "прил", "глаг", "мест", "нар", "прич", "деепр", "част", "союз", "предл", "фикс"};
		String[] chisla = {"ед", "множ", "нет"};
		String[] roda = {"муж", "жен", "ср", "нет", "1лицо", "2лицо", "3лицо"};
		String[] padezhi = {"им", "вин", "дат", "род", "твор", "предл", "нет"};
		String[] vremena = {"прош", "прошсов", "наст", "повнак", "инф"};
		String[] razryadyGr = {"сущ", "прил", "нар"};
		String[] razryadyZn = {"личн", "возвр", "притяж", "вопр", "относ", "указ", "опред", "отриц", "неопр"};
		// polnoe/nepolnoe...
		String partOfSpeech = w.getChastRechi();
		int[] vector = {0,0,0,0,0,0,0,0};
		int psInd = getIndexOfString(partOfSpeech, chasti_rechi);
		vector[0] = psInd;
		switch(psInd){
		case 0 : {
			vector[1] = getIndexOfString(((Suschestvitelnoe) w).getChislo(), chisla);
			vector[2] = getIndexOfString(((Suschestvitelnoe) w).getRod(), roda);
			vector[3] = getIndexOfString(((Suschestvitelnoe) w).getPadezh(), padezhi);
		} break;
		case 1 : {
			vector[1] = getIndexOfString(((Prilagatelnoe) w).getChislo(), chisla);
			vector[2] = getIndexOfString(((Prilagatelnoe) w).getRod(), roda);
			vector[3] = getIndexOfString(((Prilagatelnoe) w).getPadezh(), padezhi);
			if (((Prilagatelnoe) w).isPolnoe()){
				vector[4] = 1;
			}
		} break;
		case 2 : {
			vector[1] = getIndexOfString(((Glagol) w).getChislo(), chisla);
			vector[2] = getIndexOfString(((Glagol) w).getRod(), roda);
			vector[5] = getIndexOfString(((Glagol) w).getVremya(), roda);
		} break;
		case 3 : {
			vector[1] = getIndexOfString(((Mestoimenie) w).getChislo(), chisla);
			vector[2] = getIndexOfString(((Mestoimenie) w).getRod(), roda);
			vector[3] = getIndexOfString(((Mestoimenie) w).getPadezh(), padezhi);
			vector[6] = getIndexOfString(((Mestoimenie) w).getRazryadGr(), roda);
			vector[7] = getIndexOfString(((Mestoimenie) w).getRazryadZn(), padezhi);
		} break;
		case 5 : {
			vector[1] = getIndexOfString(((Prichastie) w).getChislo(), chisla);
			vector[2] = getIndexOfString(((Prichastie) w).getRod(), roda);
			vector[3] = getIndexOfString(((Prichastie) w).getPadezh(), padezhi);
			if (((Prichastie) w).isPolnoe()){
				vector[4] = 1;
			}
		} break;
		}
		code = vector[0] * 10000000 + vector[1] * 1000000 + vector[2] * 100000 + vector[3] * 10000 + vector[4] * 1000 + vector[5] * 100 + vector[6] * 10 + vector[7];
		final int MAX_CODE = 100000000;
		ans = (double) (code) / (double) (MAX_CODE);
		return ans;
	}
	
	private int getIndexOfString(String arg, String[] array) {
		int index = -1;
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(arg)) {
				index = i;
				break;
			}
		}
		return index;
	}
	
	public void randomWeights() {
		// random weights
				for (int i = 0; i < 60; i++) {	// target
					for (int j = 0; j < 40; j++) {	// source
						first[j][i] = Math.random();
					}
				}
				for (int i = 0; i < 15; i++) {	// target
					for (int j = 0; j < 60; j++) {	// source
						second[j][i] = Math.random();
					}
				}
				for (int i = 0; i < 3; i++) {	// target
					for (int j = 0; j < 15; j++) {	// source
						third[j][i] = Math.random();
					}
				}
			System.out.println("Случайные веса нейронных связей присвоены.");
	}
	
	public double sigmoid(double z) {
		double s = 1 / (1 - Math.exp(-z));
		return s;
	}
	
	public void forwardPass(double[] words, Scanner input) {
		System.out.println("Forward pass...");
		inputLayer = words;
		for (int i = 0; i < 60; i++) {	// target
			double z = 0;
			for (int j = 0; j < 40; j++) {	// source
				z += inputLayer[j] * first[j][i];
			}
			hidden1[i] = sigmoid(z);
		}
		for (int i = 0; i < 15; i++) {	// target
			double z = 0;
			for (int j = 0; j < 60; j++) {	// source
				z += hidden1[j] * second[j][i];
			}
			hidden2[i] = sigmoid(z);
		}
		for (int i = 0; i < 3; i++) {	// target
			double z = 0;
			for (int j = 0; j < 15; j++) {	// source
				z += hidden2[j] * third[j][i];
			}
			outputLayer[i] = 10 * sigmoid(z);
		}

		System.out.println("Прямой просчёт окончен.");
		System.out.println("Эстетика: " + outputLayer[0]);
		System.out.println("Читабельность: " + outputLayer[1]);
		System.out.println("Эмоциональность: " + outputLayer[2]);
		System.out.println("Введите свою оценку (0-10):");
		int ans;
		System.out.println("Эстетика: ");
		ans = input.nextInt();
		error[0] = outputLayer[0] - ans;
		System.out.println("Читабельность: ");
		ans = input.nextInt();
		error[1] = outputLayer[1] - ans;
		System.out.println("Эмоциональность: ");
		ans = input.nextInt();
		error[2] = outputLayer[2] - ans;
		
	}
	
	public void backprop() {
		// don't forget to divide errors by 10
		// cost function, euclidean
		double cost = 0.05 * Math.sqrt(error[0] * error[0] + error[1] * error[1] + error[2] * error[2]);
		System.out.println("Backpropagation... Cost function = " + cost);
		for (int i = 0; i < 15; i++) {
			for (int j = 0; j < 3; j++) {
				third[i][j] -= eta * hidden2[i] * deltaThird(j);
				System.out.println(third[i][j]);
			}
		}
		for (int i = 0; i < 60; i++) {
			for (int j = 0; j < 15; j++) {
				second[i][j] -= eta * hidden1[i] * deltaSecond(j);
			}
		}
		for (int i = 0; i < 40; i++) {
			for (int j = 0; j < 60; j++) {
				first[i][j] -= eta * inputLayer[i] * deltaFirst(j);
			}
		}
		System.out.println("Обратный просчёт окончен.");
	}
	
	public double deltaThird(int index) {
		double d = error[index] * outputLayer[index] * (1 - outputLayer[index]/10) / 10;
		return d;
	}
	
	public double deltaSecond(int index) {
		double sumFactor = 0;
		for (int i = 0; i < 3; i++) {
			sumFactor += deltaThird(i) * third[index][i];
		}
		double d = sumFactor * hidden2[index] * (1 - hidden2[index]);
		return d;
	}
	
	public double deltaFirst(int index) {
		double sumFactor = 0;
		for (int i = 0; i < 15; i++) {
			sumFactor += deltaSecond(i) * second[index][i];
		}
		double d = sumFactor * hidden1[index] * (1 - hidden1[index]);
		return d;
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
	
	public double max(double[] numbers) {
		double ans = numbers[0];
		for(int i = 1; i < numbers.length; i++){
			if(numbers[i-1] <= numbers[i]){
				ans = numbers[i];
			}
		}
		return ans;
	}
	
	
}
