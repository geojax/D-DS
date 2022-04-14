import com.sun.source.util.Trees;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Array;
import java.util.*;

public class Cheatle {

	ArrayList<String> allValidGuesses = new ArrayList<>();
	HashSet<String> allValidSolutions = new HashSet<>();
	HashSet<String> remainingValidGuesses = new HashSet<>();
	TreeSet<String> remainingValidSolutions = new TreeSet<>();
	TreeSet<Character> alphabet = new TreeSet<>();

	TreeSet<Character> correctPlaceLetters;
	TreeSet<Character> wrongPlaceLetters;
	TreeSet<Character> eliminatedLetters;
	TreeSet<Character> unguessedCharacters;
	int wordLength;
	int numberOfGuesses;// = 0;
	//Reads the dictionaryFile and puts all the allowed guesses into a data structure,
	//and also reads the solutionFile and puts all the possible solutions into a data structure,
	//also adding all the possible solutions to the allowed guesses.
	//Throws a BadDictionaryException if not every word in the dictionary & solutions are of the same length 
	public Cheatle(String dictionaryFile, String solutionFile) throws BadDictionaryException, FileNotFoundException {
		// initialize valid guesses
		{
			Scanner scanner = new Scanner(new File(dictionaryFile));
			if (scanner.hasNext()) {
				String newstr = scanner.next();
				allValidGuesses.add(newstr);
				wordLength = newstr.length();
			}
			while (scanner.hasNext()) {
				String newstr = scanner.next();
				if (newstr.length() != wordLength)
					throw new BadDictionaryException();
				for (char c : newstr.toCharArray()) {
					alphabet.add(c);
				}
				allValidGuesses.add(newstr);
			}
		}
		// initialize valid solutions and add to guesses as well.
		{
			Scanner scanner = new Scanner(new File(solutionFile));
			if (scanner.hasNext()) {
				String newstr = scanner.next();
				allValidSolutions.add(newstr);
				allValidGuesses.add(newstr);
				wordLength = newstr.length();
			}
			while (scanner.hasNext()) {
				String newstr = scanner.next();
				if (newstr.length() != wordLength)
					throw new BadDictionaryException();
				for (char c : newstr.toCharArray()) {
					alphabet.add(c);
				}
				allValidSolutions.add(newstr);
				allValidGuesses.add(newstr);
			}
		}
	}


	//Returns the length of the words in the list of words
	public int getWordLength() {
		return wordLength;
	}

	//Returns the complete alphabet of chars that are used in any word in the solution list,
	//in order as a String
	public String getAlphabet() {
		StringBuilder sb = new StringBuilder();
		for (Character c : alphabet) {
			sb.append(c);
		}
		return sb.toString();
	}


	//Begins a game of Cheatle, initializing any private instance variables necessary for
	// a single game.
	public void beginGame() {
		numberOfGuesses = 0;
		correctPlaceLetters = new TreeSet<>();
		wrongPlaceLetters = new TreeSet<>();
		eliminatedLetters = new TreeSet<>();
		unguessedCharacters = new TreeSet<>();
		for (String s : allValidGuesses) {
			remainingValidGuesses.add(s);
		}
		for (String s: allValidSolutions) {
			remainingValidSolutions.add(s);
			remainingValidGuesses.add(s);
		}
		for (Character c : alphabet) {
			unguessedCharacters.add(c);
		}
	}

	//Checks to see if the guess is in the dictionary of words.
	//Does NOT check to see whether the guess is one of the REMAINING words.
	public boolean isAllowable(String guess) {
		return allValidGuesses.contains(guess);
	}

	//Given a guess, returns a String of '*', '?', and '!'
	//that gives feedback about the corresponding letters in that guess:
	// * means that letter is not in the word
	// ? means that letter is in the word, but not in that place
	// ! means that letter is in that exact place in the word
	// Because this is CHEATLE, not Wordle, you are to return the feedback
	// that leaves the LARGEST possible number of words remaining!!!
	//makeGuess should also UPDATE the list of remaining words
	// and update the list of letters where we KNOW where they are,
	// the list of letters that are definitely in the word but we don't know where they are,
	// the list of letters that are not in the word,
	// and the list of letters that are still possibilities
	public String makeGuess(String guess) {
		StringBuilder sboutput = new StringBuilder();
		for (int i = 0 ; i < wordLength; i++)
			sboutput.append("!");
		String output = sboutput.toString();
		// Add each possible output to a hashmap.
		// for each possible output, add a number of valid solutions for that output.
		// choose output with greatest number of valid words.
		ArrayList<String> longestList = new ArrayList<>();
		{
			for (char c : guess.toCharArray()) {
				unguessedCharacters.remove(c);
			}

			HashMap<String, ArrayList<String>> outputPossibilities = new HashMap<>();
			// Generate possible outputs and numbers of solutions.
			for (String solution : remainingValidSolutions) {
				String possibleOutput = getOutput(guess, solution);
				if (possibleOutput.indexOf('?') != -1 || possibleOutput.indexOf('*') != -1) { // these were 0 instead of -1...
					if (outputPossibilities.containsKey(possibleOutput))
						outputPossibilities.get(possibleOutput).add(solution);
					else {
						ArrayList<String> a = new ArrayList<>();
						a.add(solution); // i was adding guess here....
						outputPossibilities.put(possibleOutput, a);
					}
					if (outputPossibilities.get(possibleOutput).size() > longestList.size()) {
						longestList = outputPossibilities.get(possibleOutput);
						output = possibleOutput;
					}
				}
			}
		}
		TreeSet<String> newSolutionTree = new TreeSet<>();
		for (String s : longestList) {
			newSolutionTree.add(s);
		}
		remainingValidSolutions = newSolutionTree;

		for (int i = 0; i < wordLength; i++) {
			if (output.charAt(i) == '!') {
				correctPlaceLetters.add(guess.charAt(i));
				wrongPlaceLetters.remove(guess.charAt(i));
			}
			else if (output.charAt(i) == '?') {
				wrongPlaceLetters.add(guess.charAt(i));
			}
			else {
				eliminatedLetters.add(guess.charAt(i));
			}
		}
		++numberOfGuesses;
		return  output;
	}

	//Returns a String of all letters that have received a ! feedback
	// IN ORDER
	public String correctPlaceLetters() {
		StringBuilder sb = new StringBuilder();
		for (Character c : correctPlaceLetters) {
			sb.append(c);
		}
		return sb.toString();
	}

	//Returns a String of all letters that have received a ? feedback
	// IN ORDER
	public String wrongPlaceLetters() {
		StringBuilder sb = new StringBuilder();
		for (Character c : wrongPlaceLetters) {
			sb.append(c);
		}
		return sb.toString();
	}

	//Returns a String of all letters that have received a * feedback
	// IN ORDER
	public String eliminatedLetters() {
		StringBuilder sb = new StringBuilder();
		for (Character c : eliminatedLetters) {
			sb.append(c);
		}
		return sb.toString();
	}

	//Returns a String of all unguessed letters
	public String unguessedLetters() {
		StringBuilder sb = new StringBuilder();
		for (Character c : unguessedCharacters) {
			sb.append(c);
		}
		return sb.toString();
	}

	//Returns true if the feedback string is the winning one,
	//i.e. if it is all !s
	public boolean isWinningFeedback(String feedback) {
		StringBuilder winningOutput = new StringBuilder();
		for (int i = 0; i < wordLength; i++)
			winningOutput.append("!");
		return feedback.equals(winningOutput.toString());
	}

	//Returns a String of all the remaining possible words, with one word per line,
	// IN ORDER
	public String getWordsRemaining() {
		StringBuilder sb = new StringBuilder();
		for (String s : remainingValidSolutions) {
			sb.append(s + " ");
		}
		return sb.toString();
	}
	
	//Returns the number of possible words remaining
	public int getNumRemaining() {
		return remainingValidSolutions.size();
	}

	//Returns the number of guesses made in this game
	public int numberOfGuessesMade() {
		return numberOfGuesses;
	}

	//Ends the current game and starts a new game.
	public void restart() {
		beginGame();
	}

	public String getOutput(String guess, String solution) {
		char[] arrayGuess = guess.toCharArray();
//		char[] arraySolution = solution.toCharArray();
		ArrayList<Character> arraySolution = new ArrayList<>();
		for (char c : solution.toCharArray()) {
			arraySolution.add(c);
		}

		char[] arrayPossibleOutput = new char[wordLength];
		for (int i = 0; i < arrayPossibleOutput.length; i++)
			arrayPossibleOutput[i] = '*';

		// Put exclamation marks wherever guess and solution have the same character.
		// Set arrayGuess[i] to " " if output[i] has been set to '!'.
		for (int i = 0; i < guess.length(); i++) {
			// letters in correct spots
			if (arrayGuess[i] == solution.charAt(i)) {
				arrayPossibleOutput[i] = '!';
				arrayGuess[i] = ' ';
				arraySolution.set(i, ' ');
			}
		}
		// wrong place or incorrect letters
		for (int i = 0; i < guess.length(); i++){
			if (arrayGuess[i] != ' ' && arraySolution.contains(arrayGuess[i])) {
				arrayPossibleOutput[i] = '?';
				arraySolution.remove((Character)arrayGuess[i]);
				arrayGuess[i] = ' ';
			}
		}
		StringBuilder possibleOutput = new StringBuilder();
		for (int i = 0; i < wordLength; i++) {
			possibleOutput.append(arrayPossibleOutput[i]);
		}
		return possibleOutput.toString();

		// I believe the bug was that i was counting spaces in the '?' part of getoutput.
		// another bug was that i was checking indexOf = 0 instead of -1 in makeguess
		// that's java's fault since they can't take a character in frikin string.contains()
		//
		// uh oh another bug...
	}
}
