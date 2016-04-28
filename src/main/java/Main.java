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

        filename = "D:\\test 4.cmm"; // DEBUG

        Lexer lex = new Lexer();
        Parser par = new Parser();

        try { // Пробуем фацл лексическим анализатором
           lex.Parse(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (lex.isCorrect) { // Если у лексера нет ошибок то пробуем его парсером
            try {
                par.Parse(filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (par.isCorrect) {
            System.out.println("File is correct!");
        } else {
            System.out.println("\nFile incorrect. Please, fix the bugs and come back again!");
        }


    }



}
