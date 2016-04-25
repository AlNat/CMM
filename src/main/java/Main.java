import Lexer.Lexer;
import Parser.Parser;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        String filename; // Имя файла для разбора

        if (args.length == 0) { // Если мы не получили имя файла в виде ключа
            System.out.println("Please, input filename"); // То вводим вручную
            filename = scanner.nextLine();
        } else {
            filename = args[0]; // Иначае взяли имя файла
        }

        filename = "D:\\test 4.cmm"; // DEBUG TO DELETE

        Lexer lex = new Lexer();
        Parser par = new Parser();

        try {
           lex.Parse(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (lex.isCorrect) {
            try {
                par.Parse(filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }



}
