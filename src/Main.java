import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        String filename;

        System.out.println("Please, input filename");
        //filename = scanner.nextLine();
        filename = "D:\\2.cmm"; // Временно
        Lexer lex = new Lexer();
        Parser par = new Parser();

        try {
           lex.Parse(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
