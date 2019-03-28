import java.util.ArrayList;

public class Main {

    public static void main(String args[]) {
        Parser parser = new Parser();
        ArrayList<String> commands = parser.load("/Users/akira/Downloads/LST/LST1.LST");
        for (String temp : commands
             ) {
            System.out.println(temp);

        }
    }
}
