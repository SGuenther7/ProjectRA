package Model;

public class Stack extends java.util.Stack {

    private boolean overflow = false;

    @Override
    public Object push(Object item) {

        if (this.size() >= 8) {
            this.overflow = true;
            return null;
        }

        this.overflow = false;
        return super.push(item);
    }

    public boolean isOverflow() {
        return this.overflow;
    }
}
