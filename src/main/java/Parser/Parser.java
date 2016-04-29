package Parser;

import Lexer.Tokenizer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by AlNat on 31.03.2016.
 */

/**
 * Sintaxis and Semanthic analyzer
 */

/**
 * Парсер - исполняет функции синтаксического и семантического анализатора, а так-же выполняет наш файл.
 *
 */
public class Parser {

    Tokenizer tokenizer;

    public boolean isCorrect; // Флаг орректности программы с точки зрения парсера
    private int deep; // Глубина погруения в {}

    Map <String, Integer> integerID;// Целочисленные переменные - имя и значение
    Map <String, Double> doubleID; // Дробные переменные

    /**
     * Constuctor, initialised the Map and other variables
     */
    public Parser () {

        /*
            Да, по хорошему надо бы сделать древовидные структуры, но лень писать еще и класс дерево.
        */
        tokenizer = new Tokenizer();
        tokenizer.SetTokenPosition(0);
        integerID = new HashMap<>();
        doubleID = new HashMap<>();
        deep = 0;
        isCorrect = false;

    }

    /**
     * Function parsing the file and start work syntax and semantic analyzer
     * @param filename - File to parse
     * @throws IOException
     */
    public void Parse (String filename) throws IOException {

        tokenizer.Parse(filename); // Двупроходный интерпритатор получаеться у нас

        System.out.println("Parser starts work");

        tokenizer.SetTokenPosition (6); // Установили поизицю токенов на 7 (с 0 считаем - 6). Тк у нас программа корректная
        // а первые 6 токенов это int main ( void ) {

        Body();

    }

    /**
     * Function printing all variables with them
     */
    public void PrintAllVariables() {
        System.out.println("\nInt:");
        for (Map.Entry e : integerID.entrySet()) {
            System.out.println(e.getKey() + " = " + e.getValue());
        }

        System.out.println("Double:");
        for (Map.Entry e : doubleID.entrySet()) {
            System.out.println(e.getKey() + " = " + e.getValue());
        }

    }

    /**
     * Function parsing body of statement and call another methods
     */
    private void Body () {

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
        } else if (token.equalsIgnoreCase("int")) { // Инициализация int
            INT();
        } else if (token.equalsIgnoreCase("double")) { // Инициализация double
            DOUBLE();
        } else if (token.equalsIgnoreCase("return")) { // Если программа закончилась
            System.out.println("Program finished!");
            isCorrect = true;
        } else if (token.equals("}")) { // Если закрыли цикл или if
            deep--;
            Body();
        } else {
            ARYTH(token); // Если все выше не подошло, то мы получили работу с переменной или ошибку
        }

    }

    /**
     * Function print the value in out function
     */
    private void OUT () {

        tokenizer.GetNextToken(); // Скобка
        String printName = tokenizer.GetNextToken();

        if (tokenizer.GetNextToken().equals(");")) {

            if (integerID.containsKey(printName)) { // Если это интовая переменная
                System.out.println("The int variable " + printName + " = " + integerID.get(printName)); // То выводим значение
            } else if (doubleID.containsKey(printName)) { // Если это дабл
                System.out.println("The double variable " + printName + " = " + doubleID.get(printName)); // То выводим ее значение
            } else { // Если это что-то другое
                System.out.println(printName); // То тоже выводим
            }

            Body();
        } else {
            System.out.println("Out function mut ends with ');' !");
            err(printName);
        }

    }

    /**
     * Function skipping all int the commentary braskets
     * @return
     */
    private int COMMENTARY () {

        String token;
        token = tokenizer.GetNextToken();

        while (!token.equals("*/")) {
            token = tokenizer.GetNextToken();

            if (token.equals("ERROR")) {
                System.out.println("Comments must be closed!");
                err(token);
                return -1;
            }

        }

        Body();

        return 0;
    }

    /**
     * Function Looks at variable stack and use specialfunction to it (intaryth and doubleayth) or print error
     * @param tokenName - variable to work
     */
    private void ARYTH (String tokenName) {
        // Если переменная существет, то работаем с ней, в зависимости от ее типа
        if (integerID.containsKey(tokenName) ) {
            INTARYTH(tokenName);
        } else if (doubleID.containsKey(tokenName)) {
            DOUBLEARYTH(tokenName);
        } else { // Иначе ошибка
            System.out.println("Cannot find the variable!");
            err(tokenName);
        }

    }

    private void INTARYTH(String tokenName) { // tokenName гарантиравано существует как интовая переменная

        String token = tokenizer.GetNextToken();
        String tokenName2 = tokenizer.GetNextToken();

        if (token.equals("=") && tokenName2.equals(tokenName)) { // Если это онструкция вида name = name

            // Проверяем что это одно из 4 арифметических операций
            token = tokenizer.GetNextToken();
            if (token.equals("+") ) { // Если плюс

                token = tokenizer.GetNextToken();
                //if () TODO Сделать арифметичсеие операции для всех 4 типов действий и сдлеать на сравнение типов

            }

        // } else if (tokenName2) {
            // TODO Сделать проверки на оба типа переменных и на тупо числа приравнивание

        } else {
            err(tokenName);
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
            err(tokenName);
        }

    }

    //todo сделать 3 фунции ниже
    private void IF () {
        Body();
    }

    private void FOR () {
        Body();
    }

    private void WHILE () {
        Body();
    }


    private void INT () { // Инициализация булевой переменной. tokenName - имя переменной

        String token; // Просто токен
        String tokenName = tokenizer.GetNextToken(); // Имя переменной

        if (integerID.containsKey(tokenName) ) { // Если int переменная уже существует
            System.out.println("The int variable with this name already exist!");
            err(tokenName);
        } else if (doubleID.containsKey(tokenName)) {
            System.out.println("The double variable with this name already exist!");
            err(tokenName);
        } else {

            if ( isNameCoorect(tokenName) ) { // Если имя корректно

                if (tokenizer.GetNextToken().equals("=")) { // Если есть равно
                    token = tokenizer.GetNextToken(); // Получили значение

                    if (token.endsWith(";")) { // Если обьявление корреткно

                        token = token.substring(0, token.length() - 1); // Удалили последний символ

                        if (token.matches("\\d+")) { // Если значение - число
                            int value = Integer.parseInt(token);
                            integerID.put(tokenName, value); // И положили его
                            Body();
                        } else { // Если не равняеться числу
                            System.out.println("The variable must = digit !");
                            err(token);
                        }

                    } else { // Если не закрыто ;
                        System.out.println("The definition must be closed with ';' !");
                        err(token);
                    }

                } else { // Если нету равно
                    System.out.println("The variable must be initialized with =");
                    err(tokenName);
                }

            } else { // Если имя не корректно
                err(tokenName);
            }

        }

    }

    private void DOUBLE () { // Инициализация булевой переменной. tokenName - имя переменной

        String token; // Просто токен
        String tokenName = tokenizer.GetNextToken(); // Имя переменной

        if (integerID.containsKey(tokenName) ) { // Если int переменная уже существует
            System.out.println("The int variable with this name already exist!");
            err(tokenName);
        } else if (doubleID.containsKey(tokenName)) {
            System.out.println("The double variable with this name already exist!");
            err(tokenName);
        } else {

            if ( isNameCoorect(tokenName) ) { // Если имя корректно

                if (tokenizer.GetNextToken().equals("=")) { // Если есть равно
                    token = tokenizer.GetNextToken(); // Получили значение

                    if (token.endsWith(";")) { // Если обьявление корреткно

                        token = token.substring(0, token.length() - 1); // Удалили последний символ

                        if (token.matches("(\\d+).(\\d+)")) { // Если значение - число
                            double value = Double.parseDouble(token);
                            doubleID.put(tokenName, value); // И положили его
                            Body();
                        } else { // Если не равняеться числу
                            System.out.println("The variable must = digit.digit!");
                            err(token);
                        }

                    } else { // Если не закрыто ;
                        System.out.println("The definition must be closed with ';' !");
                        err(token);
                    }

                } else { // Если нету равно
                    System.out.println("The variable must be initialized with =");
                    err(tokenName);
                }

            } else { // Если имя не корректно
                err(tokenName);
            }

        }

    }

    /**
     * Function check corection of variable name
     * @param name - variable name
     * @return true if name is correct, false another
     */
    private boolean isNameCoorect (String name) {
        
        char first = name.charAt(0);

        if (    name.equalsIgnoreCase("out") || // Если имя - зарезервиравано то ошибка
                name.equalsIgnoreCase("for") ||
                name.equalsIgnoreCase("if") ||
                name.equalsIgnoreCase("while") ||
                name.equalsIgnoreCase("main") ||
                name.equalsIgnoreCase("else") ||
                name.equalsIgnoreCase("return") ||
                name.equalsIgnoreCase("int") ||
                name.equalsIgnoreCase("double")
                ) {
            System.out.println("Variable cannot named with reserved name!");
            return false;
        } else if (name.matches("(\\W)*")) { // Проверка на нечитаемые символы - любой символ, кроме буквенного или цифрового символа или знака подчёркивания
            System.out.println("Variable cannot have special symbols in the name! Only English letters, numbers and _");
            return false;
        } else if ( Character.isDigit( name.charAt(0) ) ) { // Если первый символ - цифра то ошиба
            System.out.println("Variable name must starts with letter!");
            return false;
        } else {
            return true;
        }

        //

    }

    private boolean IFSTATMENT (String name1, String ex, String name2) {



        return false;
    }

    private void err (String in) {
        System.out.println("Error! Token => " + in + " Prev => " + tokenizer.GetPrevToken() + " Row = " + tokenizer.GetTokenRow());
    }

}
