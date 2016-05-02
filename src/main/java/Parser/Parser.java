package Parser;

import Lexer.Tokenizer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Created by AlNat on 31.03.2016
 * @author Alex Natarov
 * Licensed by Apache License, Version 2.0
 */

/**
 * Syntax and Semantic analyzer. Also interpreter.
 *
 * Парсер - исполняет функции синтаксического и семантического анализатора, а так-же выполняет наш файл.
 *
 */

public class Parser {

    private Tokenizer tokenizer;

    public boolean isCorrect; // Флаг орректности программы с точки зрения парсера
    private int deep; // Глубина погружения в {}
    private Stack<Integer> loop; // Стек для петель во влоенных циклах
    private int start; // Место начало петли

    private Map <String, Integer> integerID;// Целочисленные переменные - имя и значение
    private Map <String, Double> doubleID; // Дробные переменные

    /**
     * Constructor, initialised the Map and other variables
     */
    public Parser () {
        /*
            Да, по хорошему надо бы сделать древовидные структуры, но лень писать еще и класс дерево.
            Так что будем тут и испольнять файл и проверять и тд
        */
        tokenizer = new Tokenizer();
        tokenizer.SetTokenPosition(0); // Установили токенайзер в начало файла
        integerID = new HashMap<>(); // Создали мапы переменных
        doubleID = new HashMap<>();
        deep = 0; // Уровень глубины
        isCorrect = false; // Флаг корретности

    }

    /**
     * Function parsing the file and start work syntax and semantic analyzer
     * @param filename - File to parse
     * @throws IOException
     */
    public void Parse (String filename) throws IOException {

        tokenizer.Parse(filename); // Двупроходный интерпритатор получаеться у нас

        tokenizer.SetTokenPosition (6); // Установили поизицю токенов на 7 (с 0 считаем - 6).
        // Тк у нас программа корректная, а первые 6 токенов это int main ( void ) {

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
            deep++;
            IF();
        } else if (token.equalsIgnoreCase("for")) { // Цикл for
            deep++;
            FOR();
        } else if (token.equalsIgnoreCase("while")) { // Цикл while
            deep++;
            WHILE();
        } else if (token.equalsIgnoreCase("out")) { // Вывод переменной
            OUT();
        } else if (token.equalsIgnoreCase("int")) { // Инициализация int
            INT();
        } else if (token.equalsIgnoreCase("double")) { // Инициализация double
            DOUBLE();
        } else if (token.equalsIgnoreCase("return")) { // Если программа закончилась
            isCorrect = true;
        } else if (token.equals("}")) { // Если закрыли цикл или if
            deep--;
            tokenizer.SetTokenPosition(start); // Пошли в петлю
            Body();
        } else {
            ARITH(token); // Если все выше не подошло, то мы получили работу с переменной или ошибку
        }

    }

    /**
     * Function print the value in out function
     */
    private void OUT () {

        tokenizer.GetNextToken(); // (
        String printName = tokenizer.GetNextToken();

        while (!printName.equals(");")) { // Пока скобки не закрылись

            if (integerID.containsKey(printName)) { // Если это интовая переменная
                System.out.println("The int variable " + printName + " = " + integerID.get(printName) ); // То выводим значение
            } else if (doubleID.containsKey(printName)) { // Если это дабл
                System.out.println("The double variable " + printName + " = " + doubleID.get(printName) ); // То выводим ее значение
            } else if (printName.equals("endl")) {
                System.out.print ("\n");
            } else { // Если это что-то другое
                System.out.print(printName + " "); // То тоже выводим
            }

            printName = tokenizer.GetNextToken();
        }

        Body();

    }

    /**
     * Function skipping all int the commentary braskets
     */
    private void COMMENTARY () {

        String token = tokenizer.GetNextToken();

        while (!token.equals("*/")) { // Просто игнориуем комментарии
            token = tokenizer.GetNextToken();

            if (token.equals("ERROR")) {
                System.out.println("Comments must be closed!");
                err(token);
            }

        }

        Body();

    }

    /**
     * Function Looks at variable stack and use special function to it (intarith and doublearith) or print error
     * @param tokenName - variable to work
     */
    private void ARITH(String tokenName) {

        // Если переменная существет, то работаем с ней, в зависимости от ее типа
        if (integerID.containsKey(tokenName) ) {
            INTARITH(tokenName);
        } else if (doubleID.containsKey(tokenName)) {
            DOUBLEARITH(tokenName);
        } else { // Иначе ошибка
            System.out.println("Cannot find the variable!");
            err(tokenName);
        }

    }

    /**
     * Implement int arythmetic
     * @param tokenName - name of variable to work
     */
    private void INTARITH(String tokenName) { // tokenName гарантиравано существует как интовая переменная

        String token = tokenizer.GetNextToken();
        String tokenName2 = tokenizer.GetNextToken();

        if (doubleID.containsKey(tokenName2)) { // Если попытались изменить через double
            System.out.println("Incompatable types!");
            err(tokenName2);
            return;
        }

        int value;

        if (tokenName2.endsWith(";")) { // Если это конструкция вида name = something;

            String t = tokenName2.substring(0, tokenName2.length() - 1); // Удалили последний символ

            if (integerID.containsKey(t)) { // Если int = int;
                value = integerID.get(t);
                integerID.put(tokenName, value);
                Body();
            } else if (doubleID.containsKey(t)) { // Если int = double;
                System.out.println("Your cannot write int = double!");
                err(token);
            } else { // Если int = value;
                value = getDigit(tokenName2);
                integerID.put(tokenName, value);
                Body();
            }
        } else { // Если name = something ARUTH something;

            if (tokenName2.equals(tokenName)) {// Если это конструкция вида name = name + something;
                value = integerID.get(tokenName);
            } else { // Если name = something + something;
                value = getDigit(tokenName2);
            }

            token = tokenizer.GetNextToken();
            int tmp;

            switch (token) {// Проверяем что это одно из 4 арифметических операций
                case "+":  // Если +
                    token = tokenizer.GetNextToken(); // Положили значение
                    tmp = getDigit(token);
                    value += tmp;
                    integerID.put(tokenName, value);
                    Body();
                    break;
                case "-":  // Если -
                    token = tokenizer.GetNextToken(); // Положили значение
                    tmp = getDigit(token);
                    value -= tmp;
                    integerID.put(tokenName, value);
                    Body();
                    break;
                case "*":  // Если *
                    token = tokenizer.GetNextToken(); // Положили значение
                    tmp = getDigit(token);
                    value *= tmp;
                    integerID.put(tokenName, value);
                    Body();
                    break;
                case "/":  // Если /
                    token = tokenizer.GetNextToken(); // Положили значение
                    tmp = getDigit(token);
                    value /= tmp;
                    integerID.put(tokenName, value);
                    Body();
                    break;
                default:
                    System.out.println("Operator unexpected!");
                    err(token);
                    break;
            }
        }

    }

    /**
     * Function parsing string and return int if is it
     * @param token - string to parse
     * @return int from string if it correct
     */
    private int getDigit(String token) {
        String answer;
        if (token.endsWith(";")) { // Удалили ;
            answer = token.substring(0, token.length() - 1);
        } else {
            answer = token;
        }

        if (integerID.containsKey(answer)) { // Если вторая переменная - переменная
            return integerID.get(answer);
        } else if (answer.contains("\\D")) { // Если содерит не тольо числа
            System.out.println("Incorect name!");
            err(token);
            return 0;
        } else {
            return Integer.parseInt(answer);
        }
    }

    /**
     * Implement double arythmetic
     * @param tokenName - name of variable to work
     */
    private void DOUBLEARITH(String tokenName) {

        String token = tokenizer.GetNextToken();
        String variable = tokenizer.GetNextToken();

        if (integerID.containsKey(variable)) { // Если попытались изменить через double
            System.out.println("Incompatable types!");
            err(variable);
            return;
        }

        double value;

        if (variable.endsWith(";")) { // Если это конструкция вида name = something;

            String t = variable.substring(0, variable.length() - 1); // Удалили последний символ

            if (doubleID.containsKey(t)) { // Если double = double;
                value = doubleID.get(t);
                doubleID.put(tokenName, value);
                Body();
            } else if (integerID.containsKey(t)) { // Если int = double;
                System.out.println("Your cannot write double = int!");
                err(token);
            } else { // Если double = value;
                value = getDouble(variable);
                doubleID.put(tokenName, value);
                Body();
            }
        } else { // Если name = something ARUTH something;

            if (variable.equals(tokenName)) {// Если это конструкция вида name = name + something;
                value = doubleID.get(tokenName);
            } else { // Если name = something + something;
                value = getDouble(variable);
            }

            token = tokenizer.GetNextToken();
            double tmp;

            // Проверяем что это одно из 4 арифметических операций
            switch (token) {
                case "+":  // Если +
                    token = tokenizer.GetNextToken(); // Положили значение
                    tmp = getDouble(token);
                    value += tmp;
                    doubleID.put(tokenName, value);
                    Body();
                    break;
                case "-":  // Если -
                    token = tokenizer.GetNextToken(); // Положили значение
                    tmp = getDouble(token);
                    value -= tmp;
                    doubleID.put(tokenName, value);
                    Body();
                    break;
                case "*":  // Если *
                    token = tokenizer.GetNextToken(); // Положили значение
                    tmp = getDouble(token);
                    value *= tmp;
                    doubleID.put(tokenName, value);
                    Body();
                    break;
                case "/":  // Если /
                    token = tokenizer.GetNextToken(); // Положили значение
                    tmp = getDouble(token);
                    value /= tmp;
                    doubleID.put(tokenName, value);
                    Body();
                    break;
                default:
                    System.out.println("Operator unexpected!");
                    err(token);
                    break;
            }
        }

    }

    /**
     * Function parsing string and return double if is it
     * @param token  - string to parse
     * @return double from string if it correct
     */
    private double getDouble (String token) {
        String answer;
        if (token.endsWith(";")) { // Удалили ;
            answer = token.substring(0, token.length() - 1);
        } else {
            answer = token;
        }

        if (doubleID.containsKey(answer)) { // Если вторая переменная - содериться в double
            return doubleID.get(answer);
        } else if ( answer.matches("(\\d)+.(\\d)+") ) { // Если это нормальный double
            return Double.parseDouble(answer); // Распирсили его
        } else {
            System.out.println("Incorrect name!"); // Иначе ошибка
            err(token);
            return 0;
        }
    }

    /**
     * Function skipping if body if УСЛОВИЕ is incorrect
     */
    private void SKIPBODY () {

        String token = tokenizer.GetNextToken();
        int l = deep; // Глубина до которой пропускаем

        while (!token.equals("}") && deep == l) {
            token = tokenizer.GetNextToken();

            if (token.equals("ERROR")) {
                err(token);
            }

        }

        start = tokenizer.GetCurrentTokenNumber(); // Убрали петлю

        Body();
    }

    /**
     * Function checking the if УСЛОВИЕ
     */
    private void IF () {

        tokenizer.GetNextToken(); // (

        String variableName = tokenizer.GetNextToken();
        String operator; // Оператор, с которым работаем
        boolean flag; // Флаг правильности выраения

        double value; // Дабл тк int в дабл вместить можно
        double value2;

        if (integerID.containsKey(variableName)) { // Если интовая переменная
            value = integerID.get(variableName); // Получили первое значение
            operator = tokenizer.GetNextToken(); // Получили оператор
            variableName = tokenizer.GetNextToken();
            value2 = getDigit(variableName);  // Получили второе значение

        } else if (doubleID.containsKey(variableName)) { // Если дабл
            value = doubleID.get(variableName); // Получили первое значение
            operator = tokenizer.GetNextToken(); // Получили оператор
            variableName = tokenizer.GetNextToken();
            value2 = getDouble(variableName);  // Получили второе значение
        } else {
            System.out.println("The if statement must have view \" if ( variable operand something ) { \" !");
            err(variableName);
            return;
        }

        flag = CONDITION(value, value2, operator);

        if (!flag){ // Если выражение не верно, то пропускаем все тело
            SKIPBODY();
        } else { // Иначе выполняем
            tokenizer.GetNextToken(); // )
            tokenizer.GetNextToken(); // {
            Body();
        }

    }

    /**
     * Function implements while statement
     */
    private void WHILE () {

        start = tokenizer.GetCurrentTokenNumber() - 1; // Номер токена, к которому будем возвращаться

        tokenizer.GetNextToken(); // (
        String variableName = tokenizer.GetNextToken();
        String operator; // Оператор, с которым работаем

        double value; // Дабл тк int в дабл вместить можно
        double value2;

        if (integerID.containsKey(variableName)) { // Если интовая переменная
            value = integerID.get(variableName); // Получили первое значение
            operator = tokenizer.GetNextToken(); // Получили оператор
            variableName = tokenizer.GetNextToken();
            value2 = getDigit(variableName);  // Получили второе значение

        } else if (doubleID.containsKey(variableName)) { // Если дабл
            value = doubleID.get(variableName); // Получили первое значение
            operator = tokenizer.GetNextToken(); // Получили оператор
            variableName = tokenizer.GetNextToken();
            value2 = getDouble(variableName);  // Получили второе значение
        } else {
            System.out.println("The while statement must have view \" while ( variable operand something ) { \" !");
            err(variableName);
            return;
        }

        if ( !CONDITION(value, value2, operator) ) { // Если выражение не верно, то пропускаем все тело цикла
            SKIPBODY();
        } else { // Иначе выполняем
            tokenizer.GetNextToken(); // )
            tokenizer.GetNextToken(); // {
            Body();
        }

    }

    /**
     * Function implementing for condition
     */
    private void FOR () {

        // for ( a; a < 3; 1 ) {

        start = tokenizer.GetCurrentTokenNumber() - 1; // Номер токена, к которому будем возвращаться

        tokenizer.GetNextToken(); // (

        String token = tokenizer.GetNextToken(); // a;

        boolean doubleflag = false; // Флаг на то что переменная дабл
        double value; // Значение переменной на момент начал цикла

        token = token.substring(0, token.length() - 1); // Образали последнюю ;

        if (integerID.containsKey(token)) {
            value = getDigit(token);
        } else if (doubleID.containsKey(token)) {
            value = getDouble(token);
            doubleflag = true;
        } else {
            System.out.println("The for circle have syntax like \" for ( var; var operand value; value ) { \" ");
            err(token);
            return;
        }

        String name1 = tokenizer.GetNextToken(); // a

        if (!name1.equals(token)) {
            System.out.println("The for circle have syntax like \" for ( var; var operand value; value ) { \" ");
            err(token);
        }

        token = tokenizer.GetNextToken(); // <
        String name2 = tokenizer.GetNextToken();

        double value2; // 3
        if (doubleflag) { // Если работаем с даблом
            value2 = getDouble(name2);
        } else {
            value2 = getDigit(name2);
        }

        String n = tokenizer.GetNextToken();
        double vv; // 1
        if (doubleflag) {// Если работаем с даблом
            vv = getDouble(n);
        } else {
            vv = getDigit(n);
        }

        if ( !CONDITION(value, value2, token) ) { // Если выражение не верно, то пропускаем все тело цикла
            SKIPBODY();
        } else { // Иначе выполняем
            tokenizer.GetNextToken(); // )
            tokenizer.GetNextToken(); // {

            int v = (int)value + (int)vv;
            if (doubleflag){ // Если работаем с даблом то полоили его
                doubleID.put(name1, value + vv);
            } else { // Иначе кинули инт
                integerID.put(name1, v);
            }

            Body();
        }

    }

    /**
     * Function checking statement
     * @param value left value
     * @param value2 right value
     * @param operator operator between
     * @return true if value operator value2
     */
    private boolean CONDITION (double value, double value2, String operator) {
        boolean flag = false;
        switch (operator) { // Проверили выраение на истинность
            case "<":
                if (value < value2 ) {
                    flag = true;
                }
                break;
            case "<=":
                if (value <= value2 ) {
                    flag = true;
                }
                break;
            case ">":
                if (value > value2 ) {
                    flag = true;
                }
                break;
            case ">=":
                if (value >= value2 ) {
                    flag = true;
                }
                break;
            case "==":
                if (value == value2 ) {
                    flag = true;
                }
                break;
            case "!=":
                if (value != value2 ) {
                    flag = true;
                }
                break;
            default:
                System.out.println("The statement must have view \" ( variable operand something ) { \" !");
                err(operator);
                break;
        }

        return flag;
    }

    /**
     * This function adding the variable to variables stack if statement is correct
     */
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

            if ( isNameCorrect(tokenName) ) { // Если имя корректно

                tokenizer.GetNextToken(); // =
                token = tokenizer.GetNextToken(); // Получили значение

                int value = getDigit(token);
                integerID.put(tokenName, value); // И положили его
                Body();

            } else { // Если имя не корректно
                err(tokenName);
            }

        }

    }

    /**
     * This function adding the variable to variables stack if statement is correct
     */
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

            if ( isNameCorrect(tokenName) ) { // Если имя корректно

                tokenizer.GetNextToken(); // =

                token = tokenizer.GetNextToken(); // Получили значение

                double value = getDouble (token);
                doubleID.put(tokenName, value); // И положили его
                Body();

            } else { // Если имя не корректно
                err(tokenName);
            }

        }

    }

    /**
     * Function check correction of variable name
     * @param name - variable name
     * @return true if name is correct, false another
     */
    private boolean isNameCorrect(String name) {
        if (    name.equalsIgnoreCase("out") || // Если имя - зарезервиравано то ошибка
                name.equalsIgnoreCase("for") ||
                name.equalsIgnoreCase("if") ||
                name.equalsIgnoreCase("while") ||
                name.equalsIgnoreCase("main") ||
                name.equalsIgnoreCase("else") ||
                name.equalsIgnoreCase("endl") ||
                name.equalsIgnoreCase("return") ||
                name.equalsIgnoreCase("bool") ||
                name.equalsIgnoreCase("int") ||
                name.equalsIgnoreCase("double")
                ) {
            System.out.println("Variable cannot named with reserved name!");
            return false;
        } else if (name.matches("(\\W)*")) { // Проверка на нечитаемые символы - любой символ, кроме буквенного или цифрового символа или знака подчёркивания
            System.out.println("Variable cannot have special symbols in the name! Only English letters, numbers and _");
            return false;
        } else if ( Character.isDigit( name.charAt(0) ) ) { // Если первый символ - цифра то ошибка
            System.out.println("Variable name must starts with letter!");
            return false;
        } else {
            return true;
        }

    }

    /**
     * Function sygnalaze, where error exsist
     * @param in - token, where error happens
     */
    private void err (String in) {
        System.out.println("Error! Token => " + in + " Row = " + tokenizer.GetTokenRow());
        System.exit(-1);
    }

}