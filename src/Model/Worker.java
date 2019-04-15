package Model;

import java.util.ArrayList;

public class Worker {

    int working;
    int[] memory;
    ArrayList<Command> counter;
    Stack stack;

    int current;

    public Worker() {
        this.working = 0;
        this.memory = new int[94];
        this.counter = new ArrayList<>();
        this.stack = new Stack();

        this.current = 0;
    }

    public Worker(int working) {
        this();
        this.working = working;
    }

    public Worker(int working, int memory[]) {
        this(working);
        this.memory = memory;
    }

    public Worker(int working, int memory[], ArrayList<Command> counter, Stack stack, int current) {
        this.working = working;
        this.memory = memory;
        this.counter = counter;
        this.stack = stack;
        this.current = current;
    }

    public Worker (Worker clone) {
        this();

        this.working = clone.working;

        for(int i = 0 ; i < clone.memory.length; i++) {
            this.memory[i] = clone.memory[i];
        }

        this.counter = new ArrayList<>();
        this.counter.addAll(clone.counter);

        this.stack = new Stack(clone.stack);
        this.current = clone.current;
    }

    public boolean feed(ArrayList<Command> container) {
        for (Command fresh : container) {
            if (!feed(fresh)) {
                return false;
            }
        }

        counter.get(current).setNext(true);

        return true;
    }

    public boolean feed(Command fresh) {
        // Programmspeicher limit von 1024
        if (counter.size() < 1024) {
            counter.add(fresh);
            return true;
        }
        return false;
    }

    public void execute(int i) {
        switch (counter.get(i).getInstruction()) {
            case ADDWF:
                //C, CD, Z

                break;
            case ANDWF:
                break;
            case DECF:
                break;
            case DECFSZ:
                break;
            case INCF:
                break;
            case INCFSZ:
                break;
            case IORWF:
                break;
            case MOVF:
                break;
            case RLF:
                break;
            case RRF:
                break;
            case SUBWF:
                //C, CD, Z

                break;
            case SWAPF:
                break;
            case XORWF:
                break;
            case CLRF:
                break;
            case MOVWF:
                break;
            case CLRW:
                break;
            case NOP:
                break;
            case CLRWDT:
                break;
            case RETFIE:
                break;
            case RETURN:
                break;
            case SLEEP:
                break;
            case BCF:
                break;
            case BSF:
                break;
            case BTFSS:
                break;
            case BTFSC:
                break;
            case ADDLW:
                //C, CD, Z

                break;
            case ANDLW:
                break;
            case IORLW:
                break;
            case MOVLW:
                break;
            case RETLW:
                break;
            case SUBLW:
                //C, CD, Z

                break;
            case XORLW:
                break;
            case CALL:
                stack.push(current);
                current = counter.get(i).getValue()[0];
                break;
            case GOTO:
                current = counter.get(i).getValue()[0];
                break;
        }
    }

    public void next() {
        execute(current);
        counter.get(current).setNext(false);
        current++;
        counter.get(current).setNext(true);
    }

    public boolean hasNext() {
        return (current < counter.size() - 1);
    }

    private boolean handleZeroFlag(int value) {
        if (value == 0) {
            this.memory[3] = this.memory[3] | 3;
            return true;
        }
        return false;
    }

    private boolean handleCarryFlagOnAdd(int base, int add) {
        if (base + add > 25) {
            this.memory[3] = this.memory[3] | 1;
            return true;
        }
        return false;
    }

    private boolean handleCarryFlagOnSub(int base, int sub) {
        if (base - sub < 0) {
            this.memory[3] = this.memory[3] | 1;
            return true;
        }
        return false;
    }

    private boolean handleDigitCarryOnAdd(int base, int add) {
        base = base & 15;
        add = add & 15;

        if (base + add > 15) {
            this.memory[3] = this.memory[3] | 2;
            return true;
        }

        return false;
    }

    private boolean handleDigitCarryOnSub(int base, int sub) {
        base = base & 15;
        sub = sub & 15;

        if (base - sub < 0) {
            this.memory[3] = this.memory[3] | 2;
            return true;
        }

        return false;
    }

    public int getWorking() {
        return working;
    }

    public int[] getMemory() {
        return memory;
    }

    public ArrayList<Command> getCounter() {
        return counter;
    }

    public Stack getStack() {
        return stack;
    }

    public int getCurrent() {
        return current;
    }

    @Override
    public boolean equals(Object obj) {
        Worker other = (Worker) obj;

        if (other.working != this.working) {
            return false;
        }

        if (other.memory != this.memory) {
            return false;
        }

        return true;
    }


    public void print() {
        System.out.println("Working : " + working);
        System.out.println("Status :  " + memory[3]);
        System.out.println("Current : " + current);
        System.out.print("Befehle : ");

        for(Command element : counter) {
            System.out.print(element.getInstruction() + " ");
        }
        System.out.println();
    }
}
