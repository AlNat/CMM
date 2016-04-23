import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AlNat on 20.04.2016.
 */

//package Lexer;

public class Tokenizer {

    private ArrayList<String> tokens; // Массив числовых представлений токенов
    private List<String> file; // Наш файл с программа для распарсивания
    private int currentToken; // Указатель на текущий токен

    public Tokenizer () {

        tokens = new ArrayList<>();
        currentToken = 0;
    }

    public String GetNextToken () {

        currentToken++;
        if (currentToken  > tokens.size() ) { // Если больше токенов нет
            System.out.println("OUT OF BOUND! #" + currentToken);
            return "ERROR";
        }

        return tokens.get(currentToken - 1);

    }

    public String GetPrevToken () {

        return tokens.get(currentToken - 2);

    }

    public void SetTokenPosition (int index) {
        currentToken = index;
    }

    private void addToken (String token) {
        tokens.add(token);
        currentToken++;
    }

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
                //System.out.print("Token #" + currentToken + " => ");
                //System.out.println (t);
            }
        }

        currentToken = 0;
    }


}
