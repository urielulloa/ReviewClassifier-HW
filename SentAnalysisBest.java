/*
 * Yanfeng Jin (Tony) and Uriel Ulloa
 * All group members were present and contributing during all work on this project
 * We have neither given nor received any unauthorized aid in this assignment. 
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public final class SentAnalysisBest {

	final static File TRAINFOLDER = new File("db_txt_files");
	
	// Hashtables that store the time each word appears in positive and negative examples
	private static HashMap<String,Integer> positiveCounts = new HashMap<String,Integer>();
	private static HashMap<String,Integer> negativeCounts = new HashMap<String,Integer>();
	private static HashMap<String,Integer> positivePairs = new HashMap<String,Integer>();
	private static HashMap<String,Integer> negativePairs = new HashMap<String,Integer>();
	private static int posDocCount = 0;
	private static int negDocCount = 0;
	private static int totalPosWords = 0;
	private static int totalNegWords = 0;
	// Total number of positive/negative bigrams
	private static int totalPosPairs = 0;
	private static int totalNegPairs = 0;
	private static final double LAMBDA = 0.0001;
		
	public static void main(String[] args) throws IOException
	{	
		ArrayList<String> files = readFiles(TRAINFOLDER);		
		
		train(files);
		//if command line argument is "evaluate", runs evaluation mode
		if (args.length==1 && args[0].equals("evaluate")){
			evaluate();
		}
		else{//otherwise, runs interactive mode
			@SuppressWarnings("resource")
			Scanner scan = new Scanner(System.in);
			System.out.print("Text to classify>> ");
			String textToClassify = scan.nextLine();
			System.out.println("Result: "+classify(textToClassify));
		}
		
	}
	

	
	/*
	 * Takes as parameter the name of a folder and returns a list of filenames (Strings) 
	 * in the folder.
	 */
	public static ArrayList<String> readFiles(File folder){
		
		System.out.println("Populating list of files");
		//List to store filenames in folder
		ArrayList<String> filelist = new ArrayList<String>();
		
	
		for (File fileEntry : folder.listFiles()) {
	        String filename = fileEntry.getName();
	        filelist.add(filename);
	        }
	    
		return filelist;
	}
	
	

	
	/*
	 * TO DO
	 * Trainer: Reads text from data files in folder datafolder and stores counts 
	 * to be used to compute probabilities for the Bayesian formula.
	 * You may modify the method header (return type, parameters) as you see fit.
	 */
	public static void train(ArrayList<String> files) throws FileNotFoundException
	{
		// Go through every file in the training folder
		for (String fileName : files) {
			// Check if the filename is valid
			if (fileName.contains("-") && fileName.endsWith(".txt")) {
				// For negative examples
				if (fileName.split("-")[1].equals("1")) {
					negDocCount++;
					Scanner scanFile = new Scanner(new File("db_txt_files/"+fileName));
					
					String previousString = "";
					boolean isFirstWord = true;
					
					while (scanFile.hasNext()) {
						// Convert the string to lower case (assuming that case doesn't matter) 
						String nextString = scanFile.next().trim().toLowerCase();
						// Cleans up the strings that contain punctuations.
						String cleanString = nextString.replaceAll("[?:!.,;]*([a-z]+)[?:!.,;]*", "$1");
						
						// If the word is not first word, then construct the bigram with the previous word
						// Update the counts of the bigram in the hashtable
						if (isFirstWord == false) {
							String bigram = previousString + " " + cleanString;
							if (negativePairs.containsKey(bigram)){
								negativePairs.replace(bigram, negativePairs.get(bigram)+1);
							} else {
								negativePairs.put(bigram, 1);
							}
							
							if (!bigram.equals("")){
								totalNegPairs++;
							}
						}
						
						previousString = cleanString;
						isFirstWord = false;
						
						// Update the counts in the hashtable
						if (negativeCounts.containsKey(cleanString)){
							negativeCounts.replace(cleanString, negativeCounts.get(cleanString)+1);
						} else {
							negativeCounts.put(cleanString, 1);
						}
						
						if (!cleanString.equals("")){
							totalNegWords++;
						}
					}
					
				// for positive examples
				} else if (fileName.split("-")[1].equals("5")) {
					posDocCount ++;
					Scanner scanFile = new Scanner(new File("db_txt_files/"+fileName));
					String previousString = "";
					boolean isFirstWord = true;
					
					while (scanFile.hasNext()) {
						// Convert the string to lower case (assuming that case doesn't matter)
						String nextString = scanFile.next().trim().toLowerCase();
						// Cleans up the strings that contain punctuations.
						String cleanString = nextString.replaceAll("[?:!.,;]*([a-z]+)[?:!.,;]*", "$1");
						
						// If the word is not first word, then construct the bigram with the previous word
						// Update the counts of the bigram in the hashtable
						if (isFirstWord == false) {
							String bigram = previousString + " " + cleanString;
							if (positivePairs.containsKey(bigram)){
								positivePairs.replace(bigram, positivePairs.get(bigram)+1);
							} else {
								positivePairs.put(bigram, 1);
							}
							
							if (!bigram.equals("")){
								totalPosPairs++;
							}
						}
						
						previousString = cleanString;
						isFirstWord = false;
						
						
						
						// Update the counts of individual words in the hashtable
						if (positiveCounts.containsKey(cleanString)){
							positiveCounts.replace(cleanString, positiveCounts.get(cleanString)+1);
						} else {
							positiveCounts.put(cleanString, 1);
						}
						
						if (!cleanString.equals("")){
							totalPosWords++;
						}
					}
				}
			}	
		}
	}


	/*
	 * Classifier: Classifies the input text (type: String) as positive or negative
	 */
	public static String classify(String text)
	{
		String result="";
		
		// The probability that the document is positive/negative
		double positiveProb = (double)posDocCount / (posDocCount+negDocCount);
		double negativeProb = (double)negDocCount / (posDocCount+negDocCount);
		
		// The probability that the document is positive/negative given the features
		double positiveProbGivenF = positiveProb*positiveProduct(text);
		double negativeProbGivenF = negativeProb*negativeProduct(text);
		
		// If given the features, the document has a higher probability of being positive,
		// then return positive. Else, return negative.
		if (positiveProbGivenF > negativeProbGivenF) {
			result = "positive";
		} else {
			result = "negative";
		}
		
		return result;
	}
	
	// Calculates the product of the log of the conditional probabilities of all features for positive examples
	public static double positiveProduct (String text) {
		
		// Clean up the text, turn them all into lowercases and remove punctuations
		String cleanText = text.toLowerCase().replaceAll("[?:!.,;]*([a-z]+)[?:!.,;]*", "$1");
		String[] textArr = cleanText.split(" ");
				
		// The sum of all logarithms
		double sumLogs = 0;
		
		boolean isFirstWord = true;
		String previousWord = "";
		
		// For every word and bigram (feature) f, calculate log(Pr(f|Positive))
		for (String word : textArr) {
			if (!word.equals("")){
				// The number of occurence of the word in positive documents
				int positiveFreq = 0;
				if(positiveCounts.containsKey(word)) {
					positiveFreq = positiveCounts.get(word);
				}
				// Conditional probability
				double probWordGivenPos = (positiveFreq + LAMBDA) / (totalPosWords + (textArr.length*2-1)*LAMBDA);
				// Log base 2 of the probability
				double logProb = Math.log(probWordGivenPos) / Math.log(2);
				sumLogs += logProb;
				
				// Construct bigrams using the input text and calculate their probability
				if (isFirstWord == false) {
					String bigram = previousWord + " " + word;
					int posBigramFreq = 0;
					if (positivePairs.containsKey(bigram)) {
						posBigramFreq = positivePairs.get(bigram);
					}
					double probBigramGivenPos = (posBigramFreq + LAMBDA) / (totalPosPairs + (textArr.length*2-1) * LAMBDA);
					double logBigramProb = Math.log(probBigramGivenPos)/Math.log(2);
					sumLogs += logBigramProb;
					
				}
				previousWord = word;
				isFirstWord = false;
			}
		}
		return sumLogs;
	}
	
	// Calculates the product of the log of the conditional probabilities of all features for negative examples
	public static double negativeProduct (String text) {
		// Clean up the text, turn them all into lowercases and remove punctuations
		String cleanText = text.toLowerCase().replaceAll("[?:!.,;]*([a-z]+)[?:!.,;]*", "$1");
		String[] textArr = cleanText.split(" ");

		// The sum of all logarithms
		double sumLogs = 0;
		
		boolean isFirstWord = true;
		String previousWord = "";
		
		// For every word and bigram (feature) f, calculate log(Pr(f|Negative))
		for (String word : textArr) {
			if (!word.equals("")){
				// The number of occurence of the word in negative documents
				int negativeFreq = 0;
				if(negativeCounts.containsKey(word)) {
					negativeFreq = negativeCounts.get(word);
				}
				// Conditional probability
				double probWordGivenNeg = (negativeFreq + LAMBDA) / (totalNegWords + textArr.length*LAMBDA);
				// Log base 2 of the probability
				double logProb = Math.log(probWordGivenNeg) / Math.log(2);
				sumLogs += logProb;
				
				// Construct bigrams using the input text and calculate their probability				
				if (isFirstWord == false) {
					String bigram = previousWord + " " + word;
					int negBigramFreq = 0;
					if (negativePairs.containsKey(bigram)) {
						negBigramFreq = negativePairs.get(bigram);
					}
					double probBigramGivenNeg = (negBigramFreq + LAMBDA) / (totalNegPairs + (textArr.length*2-1) * LAMBDA);
					double logBigramProb = Math.log(probBigramGivenNeg)/Math.log(2);
					sumLogs += logBigramProb;
					
				}
				previousWord = word;
				isFirstWord = false;
				
				
				
			}
		}
		return sumLogs;
	}
	
	

	/*
	 * TO DO
	 * Classifier: Classifies all of the files in the input folder (type: File) as positive or negative
	 * You may modify the method header (return type, parameters) as you see fit.
	 */
	public static void evaluate() throws FileNotFoundException 
	{
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		
		System.out.print("Enter folder name of files to classify: ");
		String foldername = scan.nextLine();
		File folder = new File(foldername);
		
		ArrayList<String> filesToClassify = readFiles(folder);
		
		// Total classified as positive
		int totalClassifiedPos = 0;
		// Total classified as negative
		int totalClassifiedNeg = 0;
		// Total correctly classified as positive
		int correctlyClassifiedPos = 0;
		// Total correctly classified as negative
		int correctlyClassifiedNeg = 0;
		
		// For every file, classify it
		for (String fileName : filesToClassify) {
			// Validates the filename
			if (fileName.contains("-") && fileName.endsWith(".txt")) {
				// Store the score of the file if it's 1 or 5
				int score = -1;
				if (fileName.split("-")[1].equals("1")){
					score = 1;
				} else if (fileName.split("-")[1].equals("5")) {
					score = 5;
				}
				Scanner scanFile = new Scanner (new File(foldername+"/"+fileName));
				String fileText = "";
				// Stores the entire file in a string
				while (scanFile.hasNext()) {
					fileText += scanFile.nextLine() + " ";
				}
				String result = classify(fileText);
				// If the string is classified as positive
				if (result.equals("positive")){
					totalClassifiedPos ++;
					// If the string is correctly classified
					if (score == 5){
						correctlyClassifiedPos ++;
					} 
				} 
				
				// If the string is classified as negative
				else {
					totalClassifiedNeg ++;
					// If the string is correctly classified
					if (score == 1){
						correctlyClassifiedNeg ++;
					}
				}
				
			}
		}
		
		// Calculates the accuracy
		double accuracy = (double) (correctlyClassifiedPos + correctlyClassifiedNeg) / filesToClassify.size();
		// Calculates the precision for positive examples
		double precisionPos = (double) correctlyClassifiedPos/totalClassifiedPos;
		// Calculates the precision for negative examples
		double precisionNeg = (double) correctlyClassifiedNeg/totalClassifiedNeg;
		
		System.out.println("Accuracy: "+accuracy);
		System.out.println("Positive Precision: "+precisionPos);
		System.out.println("Negative Precision: "+precisionNeg);
		
	}
	
	

}
