package Parser;

import Lexer.Tokenizer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by AlNat on 31.03.2016.
 * @author Alex Natarov
 * Licensed by Apache License, Version 2.0
 */

/**
 * Syntax and Semantic analyzer
 *
 * Парсер - исполняет функции синтаксического и семантического анализатора, а так-же выполняет наш файл.
 *
 * /// Пока вложенные if и циклы не реализованны
 */

public class Parser {

    private Tokenizer tokenizer;

    public boolean isCorrect; // Флаг орректности программы с точки зрения парсера
    private int deep; // Глубина погруения в {}

    private Map <String, Integer> integerID;// Целочисленные переменные - имя и значение
    private Map <String, Double> doubleID; // Дробные переменные

    /**
     * Constuctor, initialised the Map and other variables
     */
    public Parser () {
        /*
            Да, по хорошему надо бы сделать древовидные структуры, но лень писать еще и класс дерево.
            Так что будем тут и испольнять файл и проверять и тд
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
     * @return error is is
     */
    private int COMMENTARY () {

        String token = tokenizer.GetNextToken();

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

    /**
     * Implement int arythmetic
     * @param tokenName - name of variable to work
     * @return error if exsist
     */
    private int INTARYTH(String tokenName) { // tokenName гарантиравано существует как интовая переменная

        String token = tokenizer.GetNextToken();
        String tokenName2 = tokenizer.GetNextToken();

        if (doubleID.containsKey(tokenName2)) { // Если попытались изменить через double
            System.out.println("Incompatable types!");
            err(tokenName2);
            return -1;
        }

        if (token.equals("=") ) {  // Если есть =

            int value;

            if (tokenName2.endsWith(";")) { // Если это конструкция вида name = something;

                String t = tokenName2.substring(0, tokenName2.length() - 1); // Удалили последний символ

                if (integerID.containsKey(t)) { // Если int = int;
                    value = integerID.get(t);
                    integerID.put(tokenName, value);
                    Body();
                    return 1;
                } else if (doubleID.containsKey(t)) { // Если int = double;
                    System.out.println("Your cannot write int = double!");
                    err(token);
                    return -1;
                } else { // Если int = value;
                    value = getDigit(tokenName2);
                    integerID.put(tokenName, value);
                    Body();
                    return 1;
                }
            } else { // Если name = something ARUTH something;

                if (tokenName2.equals(tokenName)) {// Если это конструкция вида name = name + something;
                    value = integerID.get(tokenName);
                } else { // Если name = something + something;
                    value = getDigit(tokenName2);
                }

                token = tokenizer.GetNextToken();

                // Проверяем что это одно из 4 арифметических операций
                switch (token) {
                    case "+":  // Если +
                        token = tokenizer.GetNextToken(); // Положили значение
                        if (token.endsWith(";")) { // Если выраение заончилось ; то именили знчаение переменной
                            int tmp = getDigit(token);
                            value += tmp;
                            integerID.put(tokenName, value);
                            Body();
                        } else {
                            err(tokenName);
                        }
                        break;
                    case "-":  // Если -
                        token = tokenizer.GetNextToken(); // Положили значение
                        if (token.endsWith(";")) { // Если выраение заончилось ; то именили знчаение переменной
                            int tmp = getDigit(token);
                            value -= tmp;
                            integerID.put(tokenName, value);
                            Body();
                        } else {
                            err(tokenName);
                        }
                        break;
                    case "*":  // Если *
                        token = tokenizer.GetNextToken(); // Положили значение
                        if (token.endsWith(";")) { // Если выраение заончилось ; то именили знчаение переменной
                            int tmp = getDigit(token);
                            value *= tmp;
                            integerID.put(tokenName, value);
                            Body();
                        } else {
                            err(tokenName);
                        }
                        break;
                    case "/":  // Если /
                        token = tokenizer.GetNextToken(); // Положили значение
                        if (token.endsWith(";")) { // Если выраение заончилось ; то именили знчаение переменной
                            int tmp = getDigit(token);
                            value /= tmp;
                            integerID.put(tokenName, value);
                            Body();
                        } else {
                            err(tokenName);
                        }
                        break;
                    default:
                        System.out.println("Operator unexpected!");
                        err(token);
                        break;
                }
            }

        } else { // Если нет равно
            System.out.println("There are only two kinds of aruthmetic! \n Name = something; \n Name = something +(-,*,\\) something !");
            err(token);
        }

        return 0;

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
     * @return error if exsist
     */
    private int DOUBLEARYTH(String tokenName) {

        String token = tokenizer.GetNextToken();
        String tokenName2 = tokenizer.GetNextToken();

        if (integerID.containsKey(tokenName2)) { // Если попытались изменить через double
            System.out.println("Incompatable types!");
            err(tokenName2);
            return -1;
        } else if (token.equals("=") ) {  // Если есть =

            double value;

            if (tokenName2.endsWith(";")) { // Если это конструкция вида name = something;

                String t = tokenName2.substring(0, tokenName2.length() - 1); // Удалили последний символ

                if (doubleID.containsKey(t)) { // Если double = double;
                    value = doubleID.get(t);
                    doubleID.put(tokenName, value);
                    Body();
                    return 1;
                } else if (integerID.containsKey(t)) { // Если int = double;
                    System.out.println("Your cannot write double = int!");
                    err(token);
                    return -1;
                } else { // Если double = value;
                    value = getDouble(tokenName2);
                    doubleID.put(tokenName, value);
                    Body();
                    return 1;
                }
            } else { // Если name = something ARUTH something;

                if (tokenName2.equals(tokenName)) {// Если это конструкция вида name = name + something;
                    value = doubleID.get(tokenName);
                } else { // Если name = something + something;
                    value = getDouble(tokenName2);
                }

                token = tokenizer.GetNextToken();

                // Проверяем что это одно из 4 арифметических операций
                switch (token) {
                    case "+":  // Если +
                        token = tokenizer.GetNextToken(); // Положили значение
                        if (token.endsWith(";")) { // Если выраение заончилось ; то именили знчаение переменной
                            double tmp = getDouble(token);
                            value += tmp;
                            doubleID.put(tokenName, value);
                            Body();
                            return 0;
                        } else {
                            err(tokenName);
                        }
                        break;
                    case "-":  // Если -
                        token = tokenizer.GetNextToken(); // Положили значение
                        if (token.endsWith(";")) { // Если выраение заончилось ; то именили знчаение переменной
                            double tmp = getDouble(token);
                            value -= tmp;
                            doubleID.put(tokenName, value);
                            Body();
                            return 0;
                        } else {
                            err(tokenName);
                        }
                        break;
                    case "*":  // Если *
                        token = tokenizer.GetNextToken(); // Положили значение
                        if (token.endsWith(";")) { // Если выраение заончилось ; то именили знчаение переменной
                            double tmp = getDouble(token);
                            value *= tmp;
                            doubleID.put(tokenName, value);
                            Body();
                            return 0;
                        } else {
                            err(tokenName);
                        }
                        break;
                    case "/":  // Если /
                        token = tokenizer.GetNextToken(); // Положили значение
                        if (token.endsWith(";")) { // Если выраение заончилось ; то именили знчаение переменной
                            double tmp = getDouble(token);
                            value /= tmp;
                            doubleID.put(tokenName, value);
                            Body();
                            return 0;
                        } else {
                            err(tokenName);
                        }
                        break;
                    default:
                        System.out.println("Operator unexpected!");
                        err(token);
                        break;
                }
            }

        } else { // Если нет равно
            System.out.println("There are only two kinds of aruthmetic! \n Name = something; \n Name = something +(-,*,\\) something !");
            err(token);
            return -1;
        }

        return 2;

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
     * Function checking the if УСЛОВИЕ
     */
    private void IF () {

        tokenizer.GetNextToken(); // (

        String variableName = tokenizer.GetNextToken();
        String operator; // Оператор, с которым работаем
        boolean flag = false; // Флаг правильности выраения

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
     * Function skipping if body if УСЛОВИЕ is incorrect
     */
    private void SKIPBODY () {

        String token = tokenizer.GetNextToken();
        int l = deep; // На будущее - глубина до которой пропускаем

        while (!token.equals("}") && deep == l) {
            token = tokenizer.GetNextToken();

            if (token.equals("ERROR")) {
                err(token);
            }

        }

        Body();
    }

    /**
     *
     */
    private void FOR () {
        //TODO Пока пропусто опускаем все что внутри
        String token = tokenizer.GetNextToken();
        int l = deep; // На будущее - глубина до которой пропускаем

        while (!token.equals("}") && deep == l) {
            token = tokenizer.GetNextToken();

            if (token.equals("ERROR")) {
                err(token);
            }

        }

        Body();
    }

    /**
     * Function implements while statement
     */
    private void WHILE () {

        int start = tokenizer.GetCurrentTokenNumber(); // Номер токена, к которому будем возвращаться

        tokenizer.GetNextToken(); // (
        String variableName = tokenizer.GetNextToken();
        String operator; // Оператор, с которым работаем
        boolean flag = false; // Флаг правильности выраения

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

        if ( CONDITION(value, value2, operator) ) { // Если выражение верно то пропускаем все тело цикла
            SKIPBODY();
        } else { // Иначе выполняем
            String a = tokenizer.GetNextToken(); // )
            tokenizer.GetNextToken(); // {
            Body();
            // TODO Доделать
        }

    }

    /**
     * TODO Доделать описание фукнции
     * @param value
     * @param value2
     * @param operator
     * @return
     */
    boolean CONDITION (double value, double value2, String operator) {
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

    }

    /**
     * Function sygnalaze, where error exsist
     * @param in - token, where error happens
     */
    private void err (String in) {
        System.out.println("Error! Token => " + in + " Prev => " + tokenizer.GetPrevToken() + " Row = " + tokenizer.GetTokenRow());
        System.exit(-1);
    }

}
