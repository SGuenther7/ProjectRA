package Model;

import java.util.ArrayList;

public class Worker {

    private int working;
    private Memory memory;
    private ArrayList<Command> counter; // TODO: In Main auslagern (um mehrfachspeicherung zu vermeiden)
    private Stack stack;

    private int current;

    public Worker() {
        this.working = 0;
        this.memory = new Memory();
        this.counter = new ArrayList<>();
        this.stack = new Stack();

        this.current = 0;
    }

    public Worker(int working) {
        this();
        this.working = working;
    }

    public Worker(int working, Memory memory) {
        this(working);
        this.memory = memory;
    }

    public Worker(int working, Memory memory, ArrayList<Command> counter, Stack stack, int current) {
        this.working = working;
        this.memory = memory;
        this.counter = counter;
        this.stack = stack;
        this.current = current;
    }

    public Worker(Worker clone) {
        this();

        this.working = clone.working;

        this.memory = new Memory(clone.getMemory());

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

        Command command = counter.get(i);

        switch (command.getInstruction()) {
            case ADDWF:
                // Var : f, d
                // Flag : C, CD, Z

                int result = working + memory.get(getBank(), command.getValue()[1]);

                handleCarryFlagOnAdd(working, memory.get(getBank(), command.getValue()[1]));
                handleDigitCarryOnAdd(working, memory.get(getBank(), command.getValue()[1]));
                handleZeroFlag(working);

                // Destination Bit gesetzt ?
                if (command.getValue()[0] == 1) {
                    memory.set(getBank(), command.getValue()[1], result);
                } else {
                    working = result;
                }
                break;
            case ANDWF:
                // Var : f, d
                // Flag : Z
                break;
            case DECF:
                // Var : f, d
                // Flag : Z
                break;
            case DECFSZ:
                // Var : f, d
                break;
            case INCF:
                // Var : f, d
                // Flag : Z
                break;
            case INCFSZ:
                // Var : f, d
                break;
            case IORWF:
                // Var : f, d
                break;
            case MOVF:
                // Var : f
                break;
            case RLF:
                // Var : f, d
                break;
            case RRF:
                // Var : f, d
                break;
            case SUBWF:
                // Var : f, d
                // Flag : C, CD, Z
                break;
            case SWAPF:
                // Var : f, d
                break;
            case XORWF:
                // Var : f, d
                // Flag : Z
                break;
            case CLRF:
                // Var : f
                // Flag : Z
                break;
            case MOVWF:
                // Var : f
                memory.set(getBank(), command.getValue()[0], working);
                break;
            case CLRW:
                // Flag : Z
                break;
            case NOP:
                break;
            case CLRWDT:
                // Flag : TO, TP
                break;
            case RETFIE:
                break;
            case RETURN:
                break;
            case SLEEP:
                // Flag : TO, TP
                break;
            case BCF:
                // Var : f, b
                break;
            case BSF:
                // Var : f, b
                break;
            case BTFSS:
                // Var : f, b
                break;
            case BTFSC:
                // Var : f, b
                break;
            case ADDLW:
                // Var : k
                //C, CD, Z
                break;
            case ANDLW:
                // Var : k
                // Flag : Z
                break;
            case IORLW:
                // Var : k
                // Flag : Z
                break;
            case MOVLW:
                // Var : k
                working = command.getValue()[0];
                break;
            case RETLW:
                // Var : k
                break;
            case SUBLW:
                // Var : k
                // Flag : C, CD, Z
                break;
            case XORLW:
                // Var : k
                // Flag : Z
                break;
            case CALL:
                // Var : k
                stack.push(current);
                current = counter.get(i).getValue()[0];
                break;
            case GOTO:
                // Var : k
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

    public void run() {

        for (int i = 0; i < counter.size() - 1; i++) {
            next();
        }
    }

    public boolean hasNext() {
        return (current < counter.size() - 1);
    }

    private boolean handleZeroFlag(int value) {
        if (value == 0) {
            memory.set(getBank(), 3, memory.get(getBank(), 3) | 3);
            return true;
        }
        return false;
    }

    private boolean handleCarryFlagOnAdd(int base, int add) {
        if (base + add > 25) {
            memory.set(getBank(), 3, memory.get(getBank(), 3) | 1);
            return true;
        }
        return false;
    }

    private boolean handleCarryFlagOnSub(int base, int sub) {
        if (base - sub < 0) {
            memory.set(getBank(), 3, memory.get(getBank(), 3) | 1);
            return true;
        }
        return false;
    }

    private boolean handleDigitCarryOnAdd(int base, int add) {
        base = base & 15;
        add = add & 15;

        if (base + add > 15) {
            memory.set(getBank(), 3, memory.get(getBank(), 3) | 2);
            return true;
        }

        return false;
    }

    private boolean handleDigitCarryOnSub(int base, int sub) {
        base = base & 15;
        sub = sub & 15;

        if (base - sub < 0) {
            memory.set(getBank(), 3, memory.get(getBank(), 3) | 2);
            return true;
        }

        return false;
    }

    public int getWorking() {
        return working;
    }

    public Memory getMemory() {
        return memory;
    }

    public int getBank() {
        return 0;
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

        if (other.getWorking() != this.working) {
            System.out.println("Falcher Inhalt in Working.");
            return false;
        }

        // Check Speicher
        if (other.getMemory().equals(memory) != true) {
            System.out.println("Memory Inhalt unterschiedlich.");
            return false;
        }

        return true;
    }

    public void print() {
        System.out.println("Working : " + working);
        System.out.print("Status :  ");
        memory.print();
        System.out.println("Current : " + current);
        System.out.print("Befehle : ");

        for (Command element : counter) {
            System.out.print(element.getInstruction() + " ");
        }
        System.out.println();
    }
}
