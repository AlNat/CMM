package Lexer;

import java.io.IOException;

/**
 * Created by AlNat on 19.04.2016.
 * @author Alex Natarov
 * Licensed by Apache License, Version 2.0
 */

/**
 *  Lexical analyzer
 *
 * Lexer - испольняет функции лексического анализатора и, частично, синтаксического. Функции:
 * - Проверяет корректность ввода зарезервированных слов
 * - Проверяет порядок (Например, что полсе for стоит ( и тд )
 * - Выводит ошибки, если есть проблемы при выводе цепочек.
 */

public class Lexer {

    private Tokenizer tokenizer;
    public boolean isCorrect; // Флаг, обзначающий корректность программы с точки зрения лексера

    /**
     * Constructor, where variables isntalizing
     */
    public Lexer () {
        tokenizer = new Tokenizer();
        isCorrect = false;
    }

    /**
     * Function parsing the file and start work lexical analyzer
     * @param filename - File to parse
     * @throws IOException
     */
    public void Parse (String filename) throws IOException {

        isCorrect = false;
        tokenizer.Parse(filename);

        String token = tokenizer.GetNextToken();

        if (       token.equals("int")  // Распарсили обьявление программы
                && tokenizer.GetNextToken().equals("main")
                && tokenizer.GetNextToken().equals("(")
                && tokenizer.GetNextToken().equals("void")
                && tokenizer.GetNextToken().equals(")")
                && tokenizer.GetNextToken().equals("{")
                ) {
            Body();

        } else {
            System.out.println("Program must start with int main ( void ) { !");
            err (token);
        }


    }

    /**
     * Function parsing body of statement and call another methods
     */
    private int Body () { // Распарсиваем тело функции

        String token = tokenizer.GetNextToken();

        if (token.equals("/*")) { // Комментарии - просто пропускаем их мимо
            COMMENTARY();
        } else if (token.equalsIgnoreCase("if")) { // Конструкия if else
            IF();
        } else if (token.equalsIgnoreCase("for")) { // Цикл for
            FOR();
        } else if (token.equalsIgnoreCase("while")) { // Цикл while
            WHILE();
        } else if (token.equalsIgnoreCase("out")) { // Вывод переменной
            OUT();
        } else if (token.equalsIgnoreCase("int") || token.equalsIgnoreCase("double")) { // Инициализация
            INIT();
        } else if (token.equalsIgnoreCase("return")
                && tokenizer.GetNextToken().equals("0;")
                && tokenizer.GetNextToken().equals("}")
                ) { // Если программа закончилась
            isCorrect = true;
            return 0;
        } else if (token.equals("}")) { // Если закрыли цикл или if
            Body();
        } else {
            ARYTH(token); // Если все выше не подошло, то мы получили работу с переменной или ошибку
        }

        return -1;
    }

    /**
     * Function checking commentary lexical
     * @return error if exsists
     */
    private int COMMENTARY () {

        String token;
        do {
            token = tokenizer.GetNextToken();

            if (token.equals("ERROR")) {
                System.out.println("Comments must be closed!");
                err(token);
                return -1;
            }

        } while (!token.equals("*/"));

        Body();

        return 0;
    }

    /**
     * Function checking if circle lexical
     */
    private void IF() {

        String token = tokenizer.GetNextToken();

        if (token.equals("(")) {

            token = tokenizer.GetNextToken();

            token = tokenizer.GetNextToken();

            if (token.equals("==")
                    || token.equals(">=")
                    || token.equals("<=")
                    || token.equals("!=")
                    || token.equals(">")
                    || token.equals("<")
                    ) {
                String token2 = tokenizer.GetNextToken();
                if (tokenizer.GetNextToken().equals(")") && tokenizer.GetNextToken().equals("{")) {
                    Body();
                } else {
                    System.out.println("if must be closed!");
                    err(token);
                }
            } else {
                System.out.println("If must have the condition!");
                err(token);
            }

        } else {
            System.out.println("If must have () block!");
            err(token);
        }

    }


    /**
     * Function checking for circle lexical
     */
    private void FOR () {

        // ( a; a < 4; 1 ) {
        String token = tokenizer.GetNextToken();
        if (token.equals("(")) {

            token = tokenizer.GetNextToken(); // a

            if (token.endsWith(";")) {

                tokenizer.GetNextToken(); // a
                tokenizer.GetNextToken(); // <
                token = tokenizer.GetNextToken(); // 4;

                if (token.endsWith(";")) {

                    token = tokenizer.GetNextToken(); // 1

                    if (tokenizer.GetNextToken().equals(")") && tokenizer.GetNextToken().equals("{")) {
                        Body();
                    } else {
                        System.out.println("For must be closed!");
                        err(token);
                    }

                } else {
                    System.out.println("For must have ends condition");
                    err(token);
                }

            } else {
                System.out.println("For must have variable to circle");
                err(token);
            }

        } else {
            System.out.println("For must have () block!");
            err(token);
        }

    }


    /**
     * Function checking while lexical
     */
    private void WHILE() {

        // ( a <= 4 )

        String token = tokenizer.GetNextToken();
        if (token.equals("(")) {

            tokenizer.GetNextToken(); // a

            token = tokenizer.GetNextToken(); // <=

            if (       token.equals("==")
                    || token.equals(">=")
                    || token.equals("<=")
                    || token.equals("!=")
                    || token.equals(">")
                    || token.equals("<")
                    ) {
                tokenizer.GetNextToken(); // 4
                if (tokenizer.GetNextToken().equals(")") && tokenizer.GetNextToken().equals("{")) {
                    Body();
                } else {
                    System.out.println("While must be closed!");
                    err(token);
                }
            } else {
                System.out.println("While must have condition!");
                err(token);
            }

        } else {
            System.out.println("While must have () block!");
            err(token);
        }

    }

    /**
     * Function checking out function lexical
     */
    private void OUT () {

        if (tokenizer.GetNextToken().equals("(")) {
            String token = tokenizer.GetNextToken();
            if (tokenizer.GetNextToken().equals(");")) {
                Body();
            } else {
                System.out.println("Your must closed the out function by ');' !");
                err(token);
            }
        }

    }

    /**
     * Function checking variable init lexical
     */
    private void INIT() {

        String token; // Просто токен
        String tokenName = tokenizer.GetNextToken(); // Имя переменной

        if (tokenizer.GetNextToken().equals("=")) { // Если есть равно
            token = tokenizer.GetNextToken(); // Получили значение
            if (token.endsWith(";")) {
                Body();
            } else {
                System.out.println("Your must closed the expression by ';' !");
                err(token);
            }
        } else { // Если нету равно
            System.out.println("The variable must be initialized");
            err(tokenName);
        }

    }

    /**
     * Function checking arythmetic lexical
     * @param tokenName name of variable to do
     * @return error
     */
    private int ARYTH (String tokenName) {

        String token = tokenizer.GetNextToken();
        if (token.equals("=")) {
            String tokenNameTwo = tokenizer.GetNextToken();

            if (tokenNameTwo.endsWith(";")) { // Если приравняли  числу или переменной
                Body();
                return 0;
            }

            token = tokenizer.GetNextToken();

            if (    token.equals("+") ||
                    token.equals("-") ||
                    token.equals("*") ||
                    token.equals("/")
                    ) {

                token = tokenizer.GetNextToken(); // Здесь имя 3 переменной

                if (token.endsWith(";")) {
                    Body();
                    return 0;
                } else {
                    System.out.println("Variable must be closed!");
                    err(token);
                    return -1;
                }

            } else {
                System.out.println("Arythmetic error!");
                err(token);
                return -1;
            }

        } else {
            err(tokenName);
            System.out.println("Your cannot just paste something!");
            return -1;
        }

    }

    /**
     * Sygnalize of error, break the flag and print token with a mistake and token before that
     * @param in - token, where error was taking
     */
    private void err (String in) {
        System.out.println("Lexical error! Token => " + in + " Prev => " + tokenizer.GetPrevToken() + " Row = " + tokenizer.GetTokenRow() );
        isCorrect = false;
        System.exit(-1);
    }


}