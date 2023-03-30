package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static int iPoints;
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static void main(String[] args) {


        String FILE_PATH = "src/main/java/org/example/Hangman.txt";
        iPoints = 10;
        boolean bIsStillPlaying = false;
        String sWord;

        try(FileReader fileReader = new FileReader(FILE_PATH)) {
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String sText = readAllLines(bufferedReader);
            String [] WordsOfText = SplitAndCleanWords(sText);


            //making sure the word to guess is longer than 3 letters
            boolean bIsWordChosen = false;
            do {
                sWord = RandomlyChoseWord(WordsOfText).toLowerCase();
                if(sWord.length()>3){
                    bIsWordChosen=true;
                }
            } while (!bIsWordChosen);

            //building an array to show which letters where guessed correctly
            String [] sWordToCheck = new String[sWord.length()];
            for(int i=0; i <sWord.length(); i++ ){
                sWordToCheck[i]="_";
            }

            //calculating the amount of points for the beginning of the game
            iPoints = setTheNumberOfPointsToStart(sWord);


            //giving the instructions for the game
            System.out.println();
            getInstructions(sWord);
            System.out.println("Number of points differ with different lengths of the word You are guessing");
            System.out.printf("%nLET'S START THE GAME!%n%n");


            bIsStillPlaying = play(sWord, sWordToCheck);
            int iNumberOfRounds=2;
            do{
                System.out.println();
                System.out.println("Do you want to play another round(Y/N)");
                String sAnotherRound = scanner.nextLine().toLowerCase();
                switch(sAnotherRound){
                    case "y":
                        System.out.println(ANSI_BLUE + "Round no" + iNumberOfRounds + ANSI_RESET);
                        bIsStillPlaying = play(sWord, sWordToCheck);
                        break;
                    case "n":
                        if(!checkGuessing(sWordToCheck)){
                            System.out.println("You have not guessed the word");
                            System.out.println("The word to guess was: "+sWord);
                            bIsStillPlaying = true;
                            break;
                        }
                    default:
                        System.out.println("Invalid answer!");
                }
                iNumberOfRounds++;
            }while(!bIsStillPlaying);

        } catch (IOException e) {
            throw new RuntimeException(e);
       }

    }

    private static boolean play(String sWord, String[] sWordToCheck){
        boolean isGameFinished = false;

        System.out.printf("The word to guess has %d letters: %n", sWord.length());
        showWord(sWordToCheck);
        isGameFinished = guess(sWord, sWordToCheck);
        if(iPoints<1){
            System.out.println("!!GAME OVER!!");
            System.out.println("!!YOUR ARE OUT OF POINTS!!");
            System.out.printf("The correct word was: %s%n", sWord);
            isGameFinished=true;
        }
        return isGameFinished;
    }
    private static int setTheNumberOfPointsToStart(String sWord){
        int iNumberOfPointsToStart=5;

        if(sWord.length()> 10){
            iNumberOfPointsToStart = (int)sWord.length()/2;
        }
        return iNumberOfPointsToStart;
    }
    private static boolean guess(String sWord, String[] sWordToCheck){
        boolean IsWordGuessed = false;
        boolean isChosenVariableCorrect = false;
        String sGuessLetterOrWord="";

            System.out.println("Do You want to guess a letter(1) or word(2)?");
            sGuessLetterOrWord = scanner.nextLine();

            switch(sGuessLetterOrWord) {
                case "1": {
                    guessLetter(sWord, sWordToCheck);
                    if (checkGuessing(sWordToCheck)) {
                        System.out.println(ANSI_GREEN+"Congratulation! You guessed the word!");
                        System.out.printf("The word to guess was: %s%n", sWord);
                        System.out.printf("You have %d points left", iPoints);
                        System.out.println(ANSI_RESET);
//                        showWord(sWordToCheck);
                        IsWordGuessed = true;
                    }
                    isChosenVariableCorrect = true;
                    break;
                }

                case "2": {
                    IsWordGuessed = checkWord(sWord);
                    isChosenVariableCorrect = true;
                    break;
                }
                default:
                    System.err.println("Wrong input, please chose number '1' (for letter) or '2' (for word)");
            }
        return IsWordGuessed;
    }
    private static boolean checkWord(String sWord){
        boolean isWordCorrect= false;
        System.out.println("Guess a word:");
        String sGuessedWord = scanner.nextLine().toLowerCase();

        if (sGuessedWord.equals(sWord)) {
            System.out.println("Congratulations You guessed the word!");
            isWordCorrect = true;
        } else {
            iPoints -= 2;
            System.out.printf("You are deducted 2 points for a wrong answer, You have %d points left%n", iPoints);
        }


//        System.out.println("Guess a word:");
//        String sGuessedWord = scanner.nextLine().toLowerCase();
        return isWordCorrect;
    }
    private static void guessLetter(String sWord, String[] sWordToCheck) {
        System.out.println("Guess a letter");
        Character cGuess = scanner.nextLine().charAt(0);
        cGuess = Character.toLowerCase(cGuess);


        if(sWord.contains(cGuess.toString())){
            int iNumberOfLettersGuessed=0;

            int iIndexOfChar = sWord.indexOf(cGuess);
            while(iIndexOfChar >= 0) {
                Character c = sWord.charAt(iIndexOfChar);
                sWordToCheck[iIndexOfChar] = c.toString();
                iIndexOfChar = sWord.indexOf(cGuess, iIndexOfChar + 1);
            }
            //checking how many letters where guessed correctly
            for(String s:sWordToCheck) {
                if (!s.equals("_")) {
                    iNumberOfLettersGuessed++;
                }
            }
            //checking if the whole word was guessed
            if(iNumberOfLettersGuessed==sWordToCheck.length){
                checkGuessing(sWordToCheck);
            }else{
                System.out.println("Congratulation! You chosen letter is part of the word!");
                System.out.printf("You have %d points left%n", iPoints);
            }
        } else {
            iPoints--;
            System.out.printf("You are deducted 1 point for a wrong answer, You have %d points left%n", iPoints);
        }
    }
    private static void showWord(String[] sWordToCheck) {
        for(String s: sWordToCheck){
            System.out.print(s);
        }
        System.out.println();
    }
    private static String RandomlyChoseWord(String[] WordsOfText) {
        Random random = new Random();
        int iNumberOfWords = WordsOfText.length;
        int iRandomlyChosenWord = (int)(Math.random()*iNumberOfWords);
        String sWord = WordsOfText[iRandomlyChosenWord];
        return sWord;
    }
    private static String[] SplitAndCleanWords(String sText) {
        String[] sCharactersToDelete = {".", ",", ":", "'", "!", "(", ")"};
        String[] sCharactersToReplace = {"-"};

            for (String cd: sCharactersToDelete){
                sText = sText.replace(cd, "");
            }

            for (String cr:sCharactersToReplace){
                sText = sText.replace(cr, "");
            }

            String[] SplitText = sText.split(" ");

            String[] ListOfWords = Arrays.stream(SplitText)
                    .filter(s->s.length()>3)
                    .toArray(size->new String[size]);

        return ListOfWords;

    }
    private static String readAllLines(BufferedReader bufferedReader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String sLine;
        while((sLine= bufferedReader.readLine()) != null){
            stringBuilder.append(sLine);
            stringBuilder.append(" ");
//                stringBuilder.append(System.lineSeparator());
        }

        return stringBuilder.toString();
    }
    private static boolean checkGuessing(String[] sWordToCheck){
        boolean bFullyGuessed = false;
        int i=0;
        for(String s:sWordToCheck){
            if(s!="_"){
                i++;
            }
            if(i==sWordToCheck.length){
                bFullyGuessed=true;
            }
        }

        return bFullyGuessed;
    }
    private static void getInstructions(String sWord){
        System.out.printf("INSTRUCTIONS:%n");
        System.out.printf("You get %d points %n", setTheNumberOfPointsToStart(sWord));
        System.out.println("For incorrectly choosing a word You are deducted 2 points, for incorrectly choosing a letter You are deducted 1 point");
        System.out.println("You have to guess the word before You are out of points");
    }

}