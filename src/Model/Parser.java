package Model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Parser {

    public static ArrayList<String> load(String path) {

        BufferedReader re;
        ArrayList<String> sentences = new ArrayList<String>();

        try {
            re = new BufferedReader(new FileReader(path));

            String next;

            while ((next = re.readLine()) != null) {
                sentences.add(next);
            }
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        return sentences;
    }

    public static ArrayList<String> cut(ArrayList<String> lines) throws NullPointerException {

        ArrayList<String> temp = new ArrayList<String>();
        for (int i = 0; i < lines.size(); i++) {
            if (Character.isDigit(lines.get(i).charAt(0))) {
                temp.add(lines.get(i).substring(5, 9));
            }
        }

        return temp;
    }

    public static String cut(String line) throws NullPointerException {

        ArrayList<String> wrapper = new ArrayList<>();
        wrapper.add(line);

        wrapper = Parser.cut(wrapper);

        if(wrapper.size() == 0) {
            return null;
        }

        return wrapper.get(0);

    }

    public static ArrayList<Command> parse(ArrayList<String> lines) throws NullPointerException {

        return new ArrayList<Command>();
    }

    public static Command parse(String line) throws NullPointerException {

        ArrayList<String> wrapper = new ArrayList<>();
        wrapper.add(line);

        ArrayList<Command> result = Parser.parse(wrapper);

        if(result.size() == 0) {
            return null;
        }

        return result.get(0);
    }

}
