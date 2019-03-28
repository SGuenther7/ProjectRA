import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Parser {

    public ArrayList<String> load(String path){

        BufferedReader re;
        ArrayList<String> sentences = new ArrayList<String>();

        try {
            re = new BufferedReader(new FileReader(path));

            String next;

            while ((next = re.readLine()) != null ) {
                    sentences.add(next);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sentences;
    }
}
