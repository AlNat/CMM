import Lexer.Lexer;
import Parser.Parser;

import java.io.IOException;
import java.util.Scanner;

// TODO Сделать вложенные if и циклы - через стек и тд
// TODO Сделать конструкцию if ( ) { } else { } - через пропуск до else и тд

/**
 * Created by @author AlNat on 20.04.2016.
 * Licensed by Apache License, Version 2.0
 */

/**
 *  C--
 *  Язык интерпретируемый.
 *  Расширение файла .cmm
 *
 *  Граматика в Grammar
 *  Тесты в файлах "test n.cmm"
 *
 *  Лексический и частично синтаксический анализатор - в Lexer
 *  Все остальное - в Parses
 */
public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        String filename; // Имя файла для разбора

        if (args.length == 0) { // Если мы не получили имя файла в виде ключа
            System.out.println("Please, input filename"); // То вводим вручную
            filename = scanner.nextLine();
        } else {
            filename = args[0]; // Иначе взяли имя файла
        }

        //filename = "C:\\Users\\AlNat\\Source\\Studi\\CM\\src\\tests\\prog.cmm";  // DEBUG

        Lexer lex = new Lexer();
        Parser par = new Parser();

        try { // Пробуем файл лексическим анализатором
            lex.Parse(filename);

            if (lex.isCorrect) { // Если у лексера нет ошибок

                System.gc(); // Запустили сборщик мусора - просто так

                System.out.println("File is lexical correct!\n");

                par.Parse(filename); // То пробуем его парсером

                if (par.isCorrect) { // Если парсер все сделал
                    //par.PrintAllVariables();
                    System.out.println("\nFile is correct and complete!");
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
