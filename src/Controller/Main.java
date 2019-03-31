package Controller;

import Model.Parser;

import java.util.ArrayList;

public class Main {

    public static void main(String args[]) {

        Parser parser = new Parser();
        ArrayList<String> commands = parser.cut(parser.load("./tests/raw/TPicSim1.LST"));

        for (int i = 0; i < commands.size(); i++) {
            System.out.println(commands.get(i));
        }
    }
}
