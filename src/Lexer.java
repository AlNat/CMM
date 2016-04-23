import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by AlNat on 31.03.2016.
 */

//package Lexer;

    // ГДЕ ТО ЗДЕСЬ ПРОСХОдЯТ 2 ЛИШНИХ дейтсвия


public class Lexer {

    Tokenizer tokenizer;
    Map <String, Boolean> boolID; // Булевые переменные - имя и значение
    Map <String, Integer> integerID;// Целочисленные переменные - имя и значение
    Map <String, Double> doubleID; // Дробные переменные

    public Lexer (){

        tokenizer = new Tokenizer();

    }

    public void Parse (String filename) throws IOException {

        tokenizer.Parse(filename);

        String token = tokenizer.GetNextToken();

        if (       token.equals("int")  // Распарсили обьявление программы
                && tokenizer.GetNextToken().equals("main")
                && tokenizer.GetNextToken().equals("(void)")
                && tokenizer.GetNextToken().equals("{")
                ) {
            Body();

        } else {
            err (token);
        }


    }

    private void Body () { // Распарсиваем тело функции

        String token = tokenizer.GetNextToken();

        if (token.equals("/*")) { // Комментарии - просто пропускаем их мимо
            do {
                token = tokenizer.GetNextToken();
            } while (!token.equals("*/"));

            Body();

        } else {


            if (token.equalsIgnoreCase("if")) { // Конструкия if else
                IF();
            }

            if (token.equalsIgnoreCase("for")) { // Цикл for
                FOR();
            }

            if (token.equalsIgnoreCase("while")) { // Цикл while
                WHILE();
            }


            if (token.equalsIgnoreCase("out")) { // Вывод переменной
                OUT();
            }


            if (token.equalsIgnoreCase("bool")) { // Инициализация типа bool
                BOOL(); // Отправили туда имя переменной
            }

            if (token.equalsIgnoreCase("int")) { // Инициализация типа int
                INT();
            }

            if (token.equalsIgnoreCase("double")) { // Инициализация типа double
                DOUBLE();
            }

            if (token.equalsIgnoreCase("return")
                    && tokenizer.GetNextToken().equals("0;")
                    && tokenizer.GetNextToken().equals("}")
                    ) { // Если программа закончилась
                System.out.println("Correct!");
                return;
            }

            ARYTH(token); // Если все выше не подошло, то мы получили работу с переменной или ошибку
        }

    }

    private void IF() {
        String token = tokenizer.GetNextToken();

        if (token.equals("(")) {
            IFHEAD();
        } else {
            err(token);
        }

    }

    private void IFHEAD () {
        String token = tokenizer.GetNextToken();

        // add token to tree - перемеенная 1

        token = tokenizer.GetNextToken();

        if (token.equals("==")
                || token.equals(">=")
                || token.equals("<=")
                || token.equals("!=")
                || token.equals(">")
                || token.equals("<")
                ) {
            String token2 = tokenizer.GetNextToken();
            // add token-действие and token2 to tree - 2 переменная
            Body();
        } else {
            err(token);
        }

    }

    private void FOR() {
        String token = tokenizer.GetNextToken();
        if (token.equals("(")) {
            FORHEAD();
        } else {
            err(token);
        }
    }

    private void FORHEAD () {
        //TODO


    }

    private void WHILE() {
        String token = tokenizer.GetNextToken();
        if (token.equals("(")) {
            WHILEHEAD();
        } else {
            err(token);
        }
    }

    private void WHILEHEAD() {
        //TODO
    }

    private void OUT () {


        if (tokenizer.GetNextToken().equals("(")) {
            String token = tokenizer.GetNextToken();
            // ToDo вставить вывод этой перменной в список на выполнение
            if (tokenizer.GetNextToken().equals(");")) {
                Body();
            } else {
                System.out.println("Out cannot do it!");
                err(token);
            }
        }

    }

    /*

        // Я уже делаю семантический анализатор, не знаю зачем

    private void OUT() { // Вывод переменной


        String token = tokenizer.GetNextToken();
        // Если переменная существет, то выводим ее со значением
        if (boolID.containsKey(token) ) {
            System.out.println("The bool variable " + token + " = " + boolID.get(token));
        } else if (integerID.containsKey(token) ) {
            System.out.println("The int variable " + token + " = " + integerID.get(token));
        } else if (doubleID.containsKey(token)) {
            System.out.println("The double variable " + token + " = " + doubleID.get(token));
        } else { // Иначе ошибка
            System.out.println("We cannot out this variable!");
            err();
        }
    }
    */

    /*
    private void BOOL () { // Инициализация булевой переменной. tokenName - имя переменной

        String token; // Просто токен
        String tokenName = tokenizer.GetNextToken(); // Имя переменной
        /* И опять семантический анализатор попер
        if (boolID.containsKey(tokenName)
                || integerID.containsKey(tokenName)
                || doubleID.containsKey(tokenName)
                ) { // Если переменная уже существует
            System.out.println("The variable already exist!");
            err();
        } else {

            if (tokenizer.GetNextToken().equals("=")) { // Если есть равно
                token = tokenizer.GetNextToken(); // Получили значение
                int value = Integer.parseInt(token);
                if ( value != 1  ) { // И проверили, что оно булево - в C-- булевые пишуться 1 и 0
                    if ( value != 0) {
                        System.out.println("The bool variable cannot be theese");
                        err();
                    } else { // Положили false если 0 и пошли дальше в тело фунцкии
                        //boolID.put(tokenName, false);
                        Body();
                    }
                } else { // И true если 1 и пошли дальше в тело функции
                    //boolID.put(tokenName, true);
                    Body();
                }
            } else { // Если нету равно
                System.out.println("The variable must be initialized");
                err();
            }

        }

    }
     */

    private void BOOL () {
        String token; // Просто токен
        String tokenName = tokenizer.GetNextToken(); // Имя переменной

        if (tokenizer.GetNextToken().equals("=")) { // Если есть равно
            token = tokenizer.GetNextToken(); // Получили значение

            if (token.endsWith(";")) {
                // TODO: 23.04.2016 add token and tokenName to tree
                Body();
            } else {
                err(token);
            }
        } else { // Если нету равно
            System.out.println("The variable must be initialized");
            err(tokenName);
        }

    }

    private void INT() {
        String token; // Просто токен
        String tokenName = tokenizer.GetNextToken(); // Имя переменной

        if (tokenizer.GetNextToken().equals("=")) { // Если есть равно
            token = tokenizer.GetNextToken(); // Получили значение
            if (token.endsWith(";")) {
                // TODO: 23.04.2016 add token and tokenName to tree
                Body();
            } else {
                err(token);
            }
        } else { // Если нету равно
            System.out.println("The variable must be initialized");
            err(tokenName);
        }
    }

    private void DOUBLE() {
        String token; // Просто токен
        String tokenName = tokenizer.GetNextToken(); // Имя переменной

        if (tokenizer.GetNextToken().equals("=")) { // Если есть равно
            token = tokenizer.GetNextToken(); // Получили значение
            if (token.endsWith(";")) {
                // TODO: 23.04.2016 add token and tokenName to tree
                Body();
            } else {
                err(token);
            }
        } else { // Если нету равно
            System.out.println("The variable must be initialized");
            err(tokenName);
        }
    }

    /*
    private void ARYTH (String tokenName) {

        // Это я уже сюда семантически анализатор впихнул снова, зачем то


        // Если переменная существет, то работаем с ней, в зависимости от ее типа
        if (boolID.containsKey(tokenName) ) {
            BOOLARYTH(tokenName);
        } else if (integerID.containsKey(tokenName) ) {
            INTARYTH(tokenName);
        } else if (doubleID.containsKey(tokenName)) {
            DOUBLEARYTH(tokenName);
        } else { // Иначе ошибка
            System.out.println("Incorrect symbol!");
            err();
        }

    }
    */

    private void ARYTH (String tokenName) {

        // add token name to tree
        String token = tokenizer.GetNextToken();
        if (token.equals("=")) {
            token = tokenizer.GetNextToken();
            if (token.endsWith(";")) {
                //add token to tree before del ;
                Body();
            } else {
                System.out.println("Aryth error!");
                err(token);
            }

        } else {
            err(tokenName);
        }

    }


    /* Один сплошной синтаксический аналиазатор!!!
    private void BOOLARYTH (String tokenName) {

        String token = tokenizer.GetNextToken();

        if (token.equals("=") && tokenizer.GetNextToken().equals(tokenName)) { // Если это онструкция вида name = name

            // Проверяем что это одно из 4 арифметических операций
            token = tokenizer.GetNextToken();
            if (token.equals("+") ) { // Если плюс

                token = tokenizer.GetNextToken();
                //if () TODO Сделать арифметичсеие операции для всех 4 типов и сдлеать на сравнение типов

            }

        } else {
            System.out.println("The arithmetic must be name = name +(or -,/,*) name(or 123)");
            err();
        }
    }

    private void INTARYTH(String tokenName) {

        String token = tokenizer.GetNextToken();

        if (token.equals("=") && tokenizer.GetNextToken().equals(tokenName)) { // Если это онструкция вида name = name

            // Проверяем что это одно из 4 арифметических операций
            token = tokenizer.GetNextToken();
            if (token.equals("+") ) { // Если плюс

                token = tokenizer.GetNextToken();
                //if () TODO Сделать арифметичсеие операции для всех 4 типов и сдлеать на сравнение типов

            }

        } else {
            err();
        }

    }

    private void DOUBLEARYTH(String tokenName) {

        String token = tokenizer.GetNextToken();

        if (token.equals("=") && tokenizer.GetNextToken().equals(tokenName)) { // Если это онструкция вида name = name

            // Проверяем что это одно из 4 арифметических операций
            token = tokenizer.GetNextToken();
            if (token.equals("+") ) { // Если плюс

                token = tokenizer.GetNextToken();
                //if () TODO Сделать арифметичсеие операции для всех 4 типов и сдлеать на сравнение типов

            }

        } else {
            err();
        }

    }
    */

    private void err (String in) {

        System.out.println("Error! Token = " + in + " Prev = " + tokenizer.GetPrevToken() );
    }

}
