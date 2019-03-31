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
    
    public ArrayList<String> parse(ArrayList<String> lines) {

    	ArrayList<String> temp = new ArrayList<String>();
    	for (int i = 0; i<lines.size();i++) {
    		if (Character.isDigit(lines.get(i).charAt(0))) {
    			temp.add(lines.get(i).substring(5,9));
    		}
    	}

    	return temp;
    }
}
