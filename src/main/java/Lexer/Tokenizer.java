package Lexer;

import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AlNat on 20.04.2016.
 */

public class Tokenizer {

    private ArrayList<String> tokens; // Массив числовых представлений токенов
    private List<String> file; // Наш файл с программа для распарсивания
    private int currentToken; // Указатель на текущий токен

    public Tokenizer () {
        tokens = new ArrayList<String>();
        currentToken = 0;
    }

    /**
     * Print all tokens
     */
    public void PrintAllTokens () {
        int t = tokens.size();
        for (int a = 0; a < t; a++) {
            System.out.println( "Token #" + a + " => " + tokens.get(a) );
        }
    }

    /**
     *
     * @return next token
     */
    public String GetNextToken () {

        currentToken++;
        if (currentToken > tokens.size() ) { // Если больше токенов нет
            //System.out.println("OUT OF BOUND! #" + currentToken); // DEBUG
            return "ERROR";
        }

        // System.out.println("Cur => " + tokens.get(currentToken - 1));

        return tokens.get(currentToken - 1);

    }

    /**
     *
     * @return token, before next
     */
    public String GetPrevToken () {
        return tokens.get(currentToken - 2);
    }

    /**
     *
     * @param index
     * @return String token with that index
     */
    public String GetToken (int index) {
        return tokens.get(index);
    }

    /**
     * Set token position with index
     * @param index
     */
    public void SetTokenPosition (int index) {
        currentToken = index;
    }

    /**
     * Add new token to array
     * @param token
     */

    private void addToken (String token) {
        tokens.add(token);
        currentToken++;
    }

    /**
     * Parsing the file and add all tokens inside in array
     * @param filename
     * @throws IOException
     */

    public void Parse (String filename) throws IOException {

        file = Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8); // Прочитали весь файл
        String[] tokensInFile; // Массив строковых токенов
        for (String st: file) {
            tokensInFile = st.split(" "); // Расрпарсили строки, по правилу через regex
            // Теперь в tokensInFile лежут куча текстовых токенов для конкретной строки

            for (String st2: tokensInFile) { // Для всех распарсенных строк
                String t = st2.replaceAll("[\\n\\t\\r\\s ]+", "");// Заменили все пробелы на ничто
                if (t.equals("")) { // Чтобы пстые строчки не добавлялись
                    continue;
                }
                addToken( t ); // И добавили его
            }
        }

        currentToken = 0;
    }


}
