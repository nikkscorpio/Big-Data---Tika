/* package whatever; // don't place package name! */
import java.util.*;
import java.lang.*;
import java.io.*;
/* Name of the class has to be "Main" only if the class is public. */
class MasterMind {
    public static void main(String[] args) {
        Map newMap = checkGuess("1234", "1122");
        System.out.println(newMap);
    }
    public static Map checkGuess(String answer, String guess) {
        // answer : 1 2 3 4
        // guess : 1 1 2 2
        // note: [0] [1] [2] [3]
        // note: answer: 1 -> 2 -> 3 -> 4
        // note: guess : 1 -> 1 -> 2 -> 2
        int correctPositions = 0;
        int correctDigits = 0;
        HashMap hm = new HashMap(); // note: hm { }
        Map resultMap = new HashMap();
        List correctPositionList = new ArrayList();
        char[] answerArr = answer.toCharArray();
        char[] guessArr = guess.toCharArray();
        int i = 0;
        while (i < answerArr.length) { // answer: 3 [2], guess: 2 [2]
            if (!hm.containsKey(answerArr[i])) {
                hm.put(answerArr[i], 1); // note: hm { 1 -> 1, 2 -> 1 }
            }
            if (answerArr[i] == guessArr[i]) {
                correctPositions++; // note: correctPositions = 1
                correctPositionList.add(answerArr[i]);
            }
            i++;
        }
        System.out.println(correctPositionList);
        // note: hm { 1 -> 1, 2 -> 1, 3 -> 1, 4 -> 1 }
        int j = 0;
        while (j < guessArr.length) {
            //check if the guess[j] has already been counted in the CorrectPositionList. If so, ignore it
            // check if the guess[j] has already occured prior in the guess list. If so, it is duplicate and is ignored.
            if (!(correctPositionList.contains(guessArr[j])) && hm.containsKey(guessArr[j]) && (int) hm.get(guessArr[j]) == 1) {
                hm.put(guessArr[j], 2);
                correctDigits++;
            }
            j++;
        }
        resultMap.put("correctPositions", correctPositions);
        resultMap.put("correctDigits", correctDigits);
        return resultMap;
        // Correct Positions
        // Correct Digits
    }
}