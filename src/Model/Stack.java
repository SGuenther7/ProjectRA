package Model;

public class Stack extends java.util.Stack {

    private boolean overflow = false;

    public Stack() {
        super();
    }

    public Stack(Stack stack) {
        this();

        for(Object obj : stack) {
            super.push(obj);
        }
    }

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
