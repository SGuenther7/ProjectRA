package Model;

public class Stack extends java.util.Stack {

    @Override
    public Object push(Object item) throws IndexOutOfBoundsException{

        if(this.size() > 8) {
            throw new IndexOutOfBoundsException("Ueberlauf bei Stack");
        }

        return super.push(item);
    }
}
