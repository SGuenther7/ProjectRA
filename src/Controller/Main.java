package Controller;

import Model.Parser;

import java.util.ArrayList;

public class Main {

    public static void main(String args[]) {
        Parser parser = new Parser();

        ArrayList<String> commands = parser.load("./tests/raw/TPicSim1.LST");

        ArrayList<String> commandos = parser.parse(commands);
        for (int i = 0; i<commandos.size(); i++) {
        	System.out.println(commandos.get(i));
        }
    }
}
