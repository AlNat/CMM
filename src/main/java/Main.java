import Lexer.Lexer;
import Parser.Parser;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        /**
         * C--
         *
         * Язык интерпретируемый.
         *
         * Расширение файла .cmm
         *
         * Граматика в Grammar
         *
         * Тесты в файлах "test n.cmm"
         *
         * Лексический и частично синтаксический анализатор - в Lexer
         *
         * Все остальное - в Parses
         *
         */

        Scanner scanner = new Scanner(System.in);
        String filename; // Имя файла для разбора

        if (args.length == 0) { // Если мы не получили имя файла в виде ключа
            System.out.println("Please, input filename"); // То вводим вручную
            filename = scanner.nextLine();
        } else {
            filename = args[0]; // Иначае взяли имя файла
        }

        // DEBUG
        filename = "C:\\Users\\AlNat\\Source\\Studi\\CM\\src\\tests\\Parser\\test 5.cmm";

        Lexer lex = new Lexer();
        Parser par = new Parser();

        try { // Пробуем файл лексическим анализатором
            lex.Parse(filename);

            if (lex.isCorrect) { // Если у лексера нет ошибок

                System.out.println("File is lexical correct");

                par.Parse(filename); // То пробуем его парсером

                if (par.isCorrect) { // Если парсер все сделал
                    System.out.println("File is correct and complete!");
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }



}
