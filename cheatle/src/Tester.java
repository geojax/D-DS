import java.io.FileNotFoundException;

public class Tester {
    public static void main(String[] args) throws FileNotFoundException, BadDictionaryException {
        Cheatle cheatle = new Cheatle("WordleDictionary.txt", "WordleSolutionList.txt");
        //System.out.println(cheatle.getOutput("there", "renew"));
        if (cheatle.getOutput("heart", "wreck").equals("*?*?*")) {
            System.out.println("good");
        }
        System.out.println(cheatle.getOutput("heart", "beefy"));
    }
}
