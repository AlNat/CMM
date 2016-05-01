package Lexer;

import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AlNat on 20.04.2016.
 * @author Alex Natarov
 * Licensed by Apache License, Version 2.0
 */


/**
 * Токенйзер выполняет самую простую часть работы - он принимает путь файлу, читает его построчно и аписывает все в
 * встречеемые слова, разделенные проблема в массив токенов, убирая табуляции, переводы строк и тд.
 */
public class Tokenizer {

    private ArrayList<String> tokens; // Массив числовых представлений токенов
    private ArrayList<Integer> tokensPage; // Массив номеров строк для всех токенов, там где они находяться
    private List<String> file; // Наш файл с программа для распарсивания
    private int currentToken; // Указатель на текущий токен

    public Tokenizer () {
        tokens = new ArrayList<>();
        tokensPage = new ArrayList<>();
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
     * @return number of row< where token placed
     */
    public int GetTokenRow() { // Вернули номер строчки, где этот токен
        return tokensPage.get(currentToken - 2) + 1 ;
    }

    /**
     * @return token, before next
     */
    public String GetPrevToken () {
        return tokens.get(currentToken - 2);
    }

    /**
     * @return currentToken
     */
    public int GetCurrentTokenNumber () {
        return currentToken;
    }

    /**
     *  Get token by id
     * @param index - token position
     * @return String token with that index
     */
    public String GetToken (int index) {
        return tokens.get(index);
    }

    /**
     * Set token position with index
     * @param index - position to set
     */
    public void SetTokenPosition (int index) {
        if (index != 0) { // Проверка на ошибочность токена
            currentToken = index;
        }
    }

    /**
     * Add new token to array
     * @param token - string to add to tokens, page - page numbet of this token
     */
    private void addToken (String token, int page) {
        tokens.add(token);
        tokensPage.add(page);
        currentToken++;
    }


    /**
     * Parsing the file and add all tokens inside in array
     * @param filename - parsing file path
     * @throws IOException
     */
    public void Parse (String filename) throws IOException {

        file = Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8); // Прочитали весь файл
        String[] tokensInFile; // Массив строковых токенов
        for (String st: file) {
            tokensInFile = st.split(" "); // Расрпарсили строки, по правилу через regex
            // Теперь в tokensInFile лежут куча текстовых токенов для конкретной строки

            for (String st2: tokensInFile) { // Для всех распарсенных строк
                String t = st2.replaceAll("[\\n\\t\\r\\s ]+", "");// Заменили все пробелы и переводы строк на ничто
                if (t.equals("")) { // Чтобы пстые строчки не добавлялись
                    continue;
                }

                addToken( t, file.indexOf(st) ); // И добавили его

            }


        }

        currentToken = 0;
    }


}
