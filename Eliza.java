
//////////////////// ALL ASSIGNMENTS INCLUDE THIS SECTION /////////////////////
//
// Title:            Eliza
// Files:            Eliza.java
// Semester:         Fall 2018
//
// Author:           Mihir Khatri
// Email:            mkhatri@wisc.edu
// CS Login:         khatri
// Lecturer's Name:  Marc Renault
// 
//
///////////////////////////// CREDIT OUTSIDE HELP /////////////////////////////
//
// Students who get help from sources other than their partner must fully 
// acknowledge and credit those sources of help here.  Instructors and TAs do 
// not need to be credited here, but tutors, friends, relatives, roommates 
// strangers, etc do.
//
// Persons:          none
// Online Sources:   none
//
/////////////////////////////// 80 COLUMNS WIDE ///////////////////////////////

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * The Eliza class holds the user input and response formation for a system that
 * collects user input and responds appropriately. Eliza is based off of a
 * computer program written at MIT in the 1960's by Joseph Weizenbaum. Eliza
 * uses keyword matching to respond to users in a way that displays interest in
 * the users and continues the conversation until instructed otherwise.
 */
public class Eliza {

	/*
	 * This method does input and output with the user. It calls supporting methods
	 * to read and write files and process each user input.
	 * 
	 * @param args (unused)
	 */
	public static void main(String[] args) {
		Scanner scnr = new Scanner(System.in);
		Random randGen = new Random(Config.SEED);// creating the random
		ArrayList<String> answer = new ArrayList<String>();

		String file = "Eliza";// the file needed

		// write the code to ask who you want to chat with and read the name from the
		// (arguments make a loop)
		//System.out.println(args.length > 0);
		if (args.length > 1)
			;
		// Milestone 3
		// How the program starts depends on the command-line arguments.
		// Command-line arguments can be names of therapists for example:
		// Eliza Joe Laura
		// If no command-line arguments then the therapists name is Eliza
		//System.out.println(args.length > 0);
		if (args.length > 0) {
			if (args.length > 1) {
				System.out.println("Would you like to speak with Eliza, Joe, or Laura?");
				file = scnr.next();
				answer.add("Would you like to speak with Eliza, Joe, or Laura?");
			} else {
				file = args[0];

				file += Config.RESPONSE_FILE_EXTENSION;
				answer.add(scnr.next());

			}
		}
			ArrayList<ArrayList<String>> table = loadResponseTable(file + Config.RESPONSE_FILE_EXTENSION);
			System.out.println("Hi I'm Eliza, what is your name?");
			answer.add("Hi I'm Eliza, what is your name?");
			String name = scnr.nextLine();
			answer.add(name);

			System.out.println("Nice to meet you " + name + ". What is on your mind?\r\n");

			String input = "";

			boolean conversation = true;
			while (conversation) {

				input = scnr.nextLine();// reading the replies
				answer.add(input);
				String[] reply = prepareInput(input);
				if (reply == null) {
					break;
				}

				String response = prepareResponse(reply, randGen, table);// prepare the response
				System.out.println(response);
				answer.add(response);

			}
			String location = "";// code to save the conversation
			boolean end = false;
			System.out.println("Goodbye " + name);
			answer.add("Goodbye " + name);
			while (!end) {
				System.out.println("Would you like to have a record of our conversation (y/n):");
				if (scnr.next().equals("y")) {
					System.out.println("Enter filename:");
					location = scnr.next();
					try {
						saveDialog(answer, location);// saving at the desired location
						break;
					} catch (IOException e) {
						System.out.println("Unable to save conversation to: ");
						continue;
					}
				}
			
		}
		System.out.println("Bye!");
		
	}

	/**
	 * This method processes the user input, returning an ArrayList containing
	 * Strings, where each String is a phrase from the user's input. This is done by
	 * removing leading and trailing whitespace, making the user's input all lower
	 * case, then going through each character of the user's input. When going
	 * through each character this keeps all digits, alphabetic characters and '
	 * (single quote). The characters ? ! , . signal the end of a phrase, and
	 * possibly the beginning of the next phrase, but are not included in the
	 * result. All other characters such as ( ) - " ] etc. should be replaced with a
	 * space. This method makes sure that every phrase has some visible characters
	 * but no leading or trailing whitespace and only a single space between words
	 * of a phrase. If userInput is null then return null, if no characters then
	 * return a 0 length list, otherwise return a list of phrases. Empty phrases and
	 * phrases with just invalid/whitespace characters should NOT be added to the
	 * list.
	 * 
	 * Example userInput: "Hi, I am! a big-fun robot!!!" Example returned: "hi", "i
	 * am", "a big fun robot"
	 * 
	 * @param userInput text the user typed
	 * @return the phrases from the user's input
	 */
	public static ArrayList<String> separatePhrases(String userInput) {
		if (userInput == null) {// the conditions to check
			return null;
		} else if (userInput.length() == 0) {
			return new ArrayList<String>();
		}

		for (int i = 0; i < userInput.length(); i++) {

			if (userInput.charAt(i) == ',' || userInput.charAt(i) == '!' || userInput.charAt(i) == '.'
					|| userInput.charAt(i) == '?' || userInput.charAt(i) == '\n') {
				userInput = userInput.substring(0, i) + "." + userInput.substring(i + 1);// checks for the characters
																							// required

			} else if (!((userInput.charAt(i) == '\'') || (userInput.charAt(i) == ' ')
					|| (userInput.charAt(i) >= '0' && userInput.charAt(i) <= '9')
					|| ('a' <= userInput.charAt(i) && userInput.charAt(i) <= 'z')
					|| ('A' <= userInput.charAt(i) && userInput.charAt(i) <= 'Z'))) {
				userInput = userInput.substring(0, i) + " " + userInput.substring(i + 1);// stores the required substring

			}
		}
		String[] reply = userInput.split("\\.+");// uses split method
		ArrayList<String> answer = new ArrayList<String>();// the arraylist wanted
		for (int j = 0; j < reply.length; j++) {
			String userString = assemblePhrase(reply[j].trim().toLowerCase().split("\\s+ "));
			if (userString.length() == 0) {
				continue;
			}

			answer.add(userString);

		}
		return answer;
	}

	/**
	 * Checks whether any of the phrases in the parameter match a quit word from
	 * Config.QUIT_WORDS. Note: complete phrases are matched, not individual words
	 * within a phrase.
	 * 
	 * @param phrases List of user phrases
	 * @return true if any phrase matches a quit word, otherwise false
	 */
	public static boolean foundQuitWord(ArrayList<String> phrases) {
		String[] answer;
		for (String str : phrases) {
			answer = str.split(" ");
			for (int j = 0; j < answer.length; j++) {
				for (int i = 0; i < Config.QUIT_WORDS.length; i++) {
					if (answer[j].equals(Config.QUIT_WORDS[i])) {// checks if the reply has a quit word
						return true;
					}
				}
			}
		}
		return false;

	}

	/**
	 * Iterates through the phrases of the user's input, finding the longest phrase
	 * to which to respond. If two phrases are the same length, returns whichever
	 * has the lower index in the list. If phrases parameter is null or size 0 then
	 * return null.
	 * 
	 * @param phrases List of user phrases
	 * @return the selected phrase
	 */
	public static String selectPhrase(ArrayList<String> phrases) {
		if (phrases == null || phrases.size() == 0) {// the condition
			return null;
		}

		int length = phrases.get(0).length();
		int index = 0;
		int max = 0;
		for (String str : phrases) {

			if (str.length() > length) {// finding the max length
				length = str.length();
				max = index;
			}
			index += 1;

		}
		return phrases.get(max);
	}

	/**
	 * Looks for a replacement word for the word parameter and if found, returns the
	 * replacement word. Otherwise if the word parameter is not found then the word
	 * parameter itself is returned. The wordMap parameter contains rows of match
	 * and replacement strings. On a row, the element at the 0 index is the word to
	 * match and if it matches return the string at index 1 in the same row. Some
	 * example word maps that will be passed in are Config.INPUT_WORD_MAP and
	 * Config.PRONOUN_MAP.
	 * 
	 * If word is null return null. If wordMap is null or wordMap length is 0 simply
	 * return word parameter. For this implementation it is reasonable to assume
	 * that if wordMap length is >= 1 then the number of elements in each row is at
	 * least 2.
	 * 
	 * @param word    The word to look for in the map
	 * @param wordMap The map of words to look in
	 * @return the replacement string if the word parameter is found in the wordMap
	 *         otherwise the word parameter itself.
	 */
	public static String replaceWord(String word, String[][] wordMap) {
		if (word == null) {
			return null;
		}
		if (wordMap == null || wordMap.length == 0) {
			return word;
		}
		for (int i = 0; i < wordMap.length; i++) {
			for (int j = 0; j < wordMap[i].length; j++) {
				if (wordMap[i][0] == word) {// compares with word in the first column
					return wordMap[i][1];// replaces with the word in the second column
				}
			}
		}
		return word;
	}

	/**
	 * Concatenates the elements in words parameter into a string with a single
	 * space between each array element. Does not change any of the strings in the
	 * words array. There are no leading or trailing spaces in the returned string.
	 * 
	 * @param words a list of words
	 * @return a string containing all the words with a space between each.
	 */
	public static String assemblePhrase(String[] words) {
		String ret = "";
		for (int i = 0; i < words.length; i++) {
			ret += words[i] + " ";// creates space between the each element

		}
		ret = ret.trim();
		return ret;
	}

	/**
	 * Replaces words in phrase parameter if matching words are found in the mapWord
	 * parameter. A word at a time from phrase parameter is looked for in wordMap
	 * which may result in more than one word. For example: i'm => i am Uses the
	 * replaceWord and assemblePhrase methods. Example wordMaps are
	 * Config.PRONOUN_MAP and Config.INPUT_WORD_MAP. If wordMap is null then phrase
	 * parameter is returned. Note: there will Not be a case where a mapping will
	 * itself be a key to another entry. In other words, only one pass through
	 * swapWords will ever be necessary.
	 * 
	 * @param phrase  The given phrase which contains words to swap
	 * @param wordMap Pairs of corresponding match & replacement words
	 * @return The reassembled phrase
	 */
	public static String swapWords(String phrase, String[][] wordMap) {
		if (wordMap == null) {
			return phrase;
		}
		String[] value = phrase.trim().split(" ");
		String response = "";
		for (int i = 0; i < value.length; i++) {
			for (int j = 0; j < wordMap.length; j++) {
				if (wordMap[j][0].equals(value[i])) {// comparing to the word
					value[i] = wordMap[j][1];// swapping the word
				}

			}
			response += value[i] + " ";
		}
		response = response.trim();
		return response;
	}

	/**
	 * This prepares the user input. First, it separates input into phrases (using
	 * separatePhrases). If a phrase is a quit word (foundQuitWord) then return
	 * null. Otherwise, select a phrase (selectPhrase), swap input words (swapWords
	 * with Config.INPUT_WORD_MAP) and return an array with each word its own
	 * element in the array.
	 * 
	 * @param input The input from the user
	 * @return words from the selected phrase
	 */
	public static String[] prepareInput(String input) {
		ArrayList<String> userInput = separatePhrases(input);
		if (foundQuitWord(userInput)) {
			System.out.println("Quit word found");
			return null;
		}

		String value = "";
		for (int i = 0; i < userInput.size(); i++) {
			userInput.set(i, swapWords(userInput.get(i), Config.INPUT_WORD_MAP));// setting the i'th element with the
																					// required ones
			value += userInput.get(i) + " ";
		}
		return value.trim().split(" ");

	}

	/**
	 * Reads a file that contains keywords and responses. A line contains either a
	 * list of keywords or response, any blank lines are ignored. All leading and
	 * trailing whitespace on a line is ignored. A keyword line begins with
	 * "keywords" with all the following tokens on the line, the keywords. Each line
	 * that follows a keyword line that is not blank is a possible response for the
	 * keywords. For example (the numbers are for our description purposes here and
	 * are not in the file):
	 * 
	 * 1 keywords computer 2 Do computers worry you? 3 Why do you mention computers?
	 * 4 5 keywords i dreamed 6 Really, <3>? 7 Have you ever fantasized <3> while
	 * you were awake? 8 9 Have you ever dreamed <3> before?
	 *
	 * In line 1 is a single keyword "computer" followed by two possible responses
	 * on lines 2 and 3. Line 4 and 8 are ignored since they are blank (contain only
	 * whitespace). Line 5 begins new keywords that are the words "i" and "dreamed".
	 * This keyword list is followed by three possible responses on lines 6, 7 and
	 * 9.
	 * 
	 * The keywords and associated responses are each stored in their own ArrayList.
	 * The response table is an ArrayList of the keyword and responses lists. For
	 * every keywords list there is an associated response list. They are added in
	 * pairs into the list that is returned. There will always be an even number of
	 * items in the returned list.
	 * 
	 * Note that in the event an IOException occurs when trying to read the file
	 * then an error message "Error reading <fileName>", where <fileName> is the
	 * parameter, is printed and a non-null reference is returned, which may or may
	 * not have any elements in it.
	 * 
	 * @param fileName The name of the file to read
	 * @return The response table
	 * @throws FileNotFoundException
	 */
	public static ArrayList<ArrayList<String>> loadResponseTable(String fileName) {
		Scanner input = null;
		ArrayList<ArrayList<String>> returnValue = new ArrayList<ArrayList<String>>();
		FileInputStream fileStream = null;
		try {// File input stream

			fileStream = new FileInputStream(fileName);
			input = new Scanner(fileStream);
		} catch (FileNotFoundException e) {// deals with the exceptions
			System.out.println("Error reading ");

			return null;
		} catch (IOException e) {
			System.out.println("Error reading the file");
			try {
				fileStream.close();
			} catch (IOException E) {
				System.out.println("fail to close" + fileName);
			}
			return null;
		}
		while (input.hasNextLine()) {// does as instructed as long as there are lines

			if (input.hasNext()) {
				String wanted = input.next();
				if (wanted.equals("keywords")) {
					ArrayList<String> e = new ArrayList<String>();
					e.add(input.nextLine().trim());
					ArrayList<String> ep = new ArrayList<String>();
					returnValue.add(e);
					returnValue.add(ep);

				} else {
					returnValue.get((returnValue.size() - 1)).add(wanted + input.nextLine());// gets the index required
																								// and adds to it
				}
			} else {
				input.nextLine();
			}

		}
		try {
			fileStream.close();// closing the file
		} catch (IOException E) {
			System.out.println("fail to close" + fileName);
		}
		return returnValue;
	}

	/**
	 * Checks to see if the keywords match the sentence. In other words, checks to
	 * see that all the words in the keyword list are in the sentence and in the
	 * same order. If all the keywords match then this method returns an array with
	 * the unmatched words before, between and after the keywords. If the keywords
	 * do not match then null is returned.
	 * 
	 * When the phrase contains elements before, between, and after the keywords,
	 * each set of the three is returned in its own element String[] keywords =
	 * {"i", "dreamed"}; String[] phrase = {"do", "you", "know", that", "i", "have",
	 * "dreamed", "of", "being", "an", "astronaut"};
	 * 
	 * toReturn[0] = "do you know that" toReturn[1] = "have" toReturn[2] = "of being
	 * an astronaut"
	 * 
	 * In an example where there is a single keyword, the resulting List's first
	 * element will be the the pre-sequence element and the second element will be
	 * everything after the keyword, in the phrase String[] keywords = {"always"};
	 * String[] phrase = {"I", "always", "knew"};
	 * 
	 * toReturn[0] = "I" toReturn[1] = "knew"
	 * 
	 * In an example where a keyword is not in the phrase in the correct order, null
	 * is returned. String[] keywords = {"computer"}; String[] phrase = {"My","dog",
	 * "is", "lost"};
	 * 
	 * return null
	 * 
	 * @param keywords The words to match, in order, in the sentence.
	 * @param phrase   Each word in the sentence.
	 * @return The unmatched words before, between and after the keywords or null if
	 *         the keywords are not all matched in order in the phrase.
	 */
	public static String[] findKeyWordsInPhrase(ArrayList<String> keywords, String[] phrase) {
		// see the algorithm presentation linked in Eliza.pdf.
		ArrayList<Integer> currPos = new ArrayList<Integer>();
		int length = 0;
		for (int i = 0; i < keywords.size(); i++) {
			for (int j = 0; j < phrase.length; j++) {
				if (keywords.get(i).equals(phrase[j])) {
					length++;
					currPos.add(j);
				}

			}
		}
		if (length != keywords.size()) {
			String[] retphrase = new String[] { "" };
			for (int i = 0; i < phrase.length; i++) {
				retphrase[0] += phrase[i] + " ";
			}
			retphrase[0] = retphrase[0].trim();
			return retphrase;
		}

		if (sort(currPos, 0, currPos.size() - 1)) {
			return null;
		}
		String[] ret = new String[length + 1];
		String temp = "";
		int counter = 0;
		for (int i = 0; i < phrase.length; i++) {

			if (counter < length && i == currPos.get(counter)) {
				ret[counter] = temp.trim();
				temp = "";
				counter++;

			} else {
				temp += phrase[i] + " ";
			}

		}
		ret[length] = temp.trim();
		return ret;
	}

	public static boolean sort(ArrayList<Integer> al, int low, int high) {//
		int povit = al.get(low);
		int l = low;
		int h = high;
		for (int i = 0; i < al.size() - 1; i++) {
			for (int j = i + 1; j < al.size(); j++) {
				if (al.get(i) > al.get(j)) {
					return true;
				}

			}

		}
		return false;
	}

	/**
	 * Selects a randomly generated response within the list of possible responses
	 * using the provided random number generator where the number generated
	 * corresponds to the index of the selected response. Use Random nextInt(
	 * responseList.size()) to generate the random number. If responseList is null
	 * or 0 length then return null.
	 * 
	 * @param rand         A random number generator.
	 * @param responseList A list of responses to choose from.
	 * @return A randomly selected response
	 */
	public static String selectResponse(Random rand, ArrayList<String> responseList) {// does as instructed
		if (responseList == null || responseList.size() == 0) {
			return null;
		}
		int num = rand.nextInt(responseList.size());
		String returnValue = responseList.get(num);
		return returnValue;
	}

	/**
	 * This method takes processed user input and forms a response. This looks
	 * through the response table in order checking to see if each keyword pattern
	 * matches the userWords. The first matching keyword pattern found determines
	 * the list of responses to choose from. A keyword pattern matches the
	 * userWords, if all the keywords are found, in order, but not necessarily
	 * contiguous. This keyword matching is done by findKeyWordsInPhrase method. See
	 * the findKeyWordsInPhrase algorithm in the Eliza.pdf.
	 * 
	 * If no keyword pattern matches then Config.NO_MATCH_RESPONSE is returned.
	 * Otherwise one of possible responses for the matched keywords is selected with
	 * selectResponse method. The response selected is checked for the replacement
	 * symbol <n> where n is 1 to the length of unmatchedWords array returned by
	 * findKeyWordsInPhrase. For each replacement symbol the corresponding unmatched
	 * words element (index 0 for <1>, 1 for <2> etc.) has its pronouns swapped with
	 * swapWords using Config.PRONOUN_MAP and then replaces the replacement symbol
	 * in the response.
	 * 
	 * @param userWords     using input after preparing.
	 * @param rand          A random number generator.
	 * @param responseTable A table containing a list of keywords and response
	 *                      pairs.
	 * @return The generated response
	 */
	public static String prepareResponse(String[] userWords, Random rand, ArrayList<ArrayList<String>> responseTable) {
		String result = "";
		ArrayList<String> responseList = new ArrayList<String>();
		int index = 0;
		for (int i = 0; i < userWords.length; i++) {
			result += userWords[i] + " ";
		}
		boolean found = false;
		if (result.trim().equals("")) {
			index = responseTable.size() - 2;
			found = true;
		} else {
			for (int i = 0; i < responseTable.size(); i += 2) {
				responseList = new ArrayList<String>();
				String[] list;
				if (!(responseTable.get(i).get(0).equals(""))) {
					list = responseTable.get(i).get(0).split(" ");
					for (int j = 0; j < list.length; j++) {
						responseList.add(list[j]);// adds the j'th element
					}
				} else {
					responseList.add("");// otherwise adds empty string
				}

				if (!(findKeyWordsInPhrase(responseList, userWords) == null
						|| findKeyWordsInPhrase(responseList, userWords)[0].equals(result.trim()))) {// to set found
																										// boolean
					found = true;
					index = i;
					break;
				}

			}
		}

		String response = "";
		if (!found) {// returns as instructed
			return Config.NO_MATCH_RESPONSE;
		}

		response = selectResponse(rand, responseTable.get(index + 1));

		for (int i = 0; i < response.length(); i++) {
			if (response.charAt(i) == '<') {
				response = response.substring(0, i)
						+ swapWords(findKeyWordsInPhrase(responseList, userWords)[response.charAt(i + 1) - '0' - 1],
								Config.PRONOUN_MAP)
						+ response.substring(i + 3);// adds to response as required
			}

		}

		// Iterate through the response table.
		// The response table has paired rows. The first row is a list of key
		// words, the next a list of corresponding responses. The 3rd row another
		// list of keywords and 4th row the corresponding responses.

		// checks to see if the current keywords match the user's words
		// using findKeyWordsInPhrase.

		// if no keyword pattern was matched, return Config.NO_MATCH_RESPONSE
		// else, select a response using the appropriate list of responses for the
		// keywords

		// Look for <1>, <2> etc in the chosen response. The number starts with 1 and
		// there won't be more than the number of elements in unmatchedWords returned by
		// findKeyWordsInPhrase. Note the number of elements in unmatchedWords will be
		// 1 more than the number of keywords.
		// For each <n> found, find the corresponding unmatchedWords phrase (n-1) and
		// swap
		// its pronoun words (swapWords using Config.PRONOUN_MAP). Then use the
		// result to replace the <n> in the chosen response.

		// in the selected echo, swap pronouns

		// inserts the new phrase with pronouns swapped, into the response

		return response;
	}

	/**
	 * Creates a file with the given name, and fills that file line-by-line with the
	 * tracked conversation. Every line ends with a newline. Throws an IOException
	 * if a writing error occurs.
	 * 
	 * @param dialog   the complete conversation
	 * @param fileName The file in which to write the conversation
	 * @throws IOException
	 */
	public static void saveDialog(ArrayList<String> dialog, String fileName) throws IOException {
		FileOutputStream fileByteStream = null;
		PrintWriter outFS = null;

		File file = new File(fileName);

		if (!file.exists()) {// checks if the file exists if dosen't deal with exceptions

			try {
				file.createNewFile();
			} catch (IOException e) {
				System.out.print("Error");
				e.printStackTrace();
			}
		}

		fileByteStream = new FileOutputStream(fileName);// opens the required files
		outFS = new PrintWriter(fileByteStream);

		for (int i = 0; i < dialog.size(); i++) {
			outFS.println(dialog.get(i));
		}

		outFS.flush();// flushes the file

		try {
			fileByteStream.close();// close the file and if can't deals with the exceptions
		} catch (IOException e) {
			System.out.println("fail to close the file");
		}
		System.out.print("file saved");

	}

}