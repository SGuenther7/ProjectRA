package Model;

import java.util.ArrayList;

public class Worker {

    private int working;
    private int[] memory;
    private ArrayList<Command> counter; // TODO: In Main auslagern (um mehrfachspeicherung zu vermeiden)
    private Stack stack;

    private int current;

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

        Command command = counter.get(i);

        switch (command.getInstruction()) {
            case ADDWF:
                //C, CD, Z

                int result = working + memory[command.getValue()[1]];

                handleCarryFlagOnAdd(working, memory[command.getValue()[1]]);
                handleDigitCarryOnAdd(working, memory[command.getValue()[1]]);
                handleZeroFlag(working);

                // Destination Bit gesetzt ?
                if(command.getValue()[0] == 1) {
                    memory[command.getValue()[1]] = result;
                } else {
                    working = result;
                }
                break;
            case ANDWF:
                // Z
                break;
            case DECF:
                // Z
                break;
            case DECFSZ:
                break;
            case INCF:
                // Z
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
                // Z
                break;
            case CLRF:
                // Z
                break;
            case MOVWF:
                memory[command.getValue()[0]] = working;
                break;
            case CLRW:
                // Z
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
                // Z
                break;
            case IORLW:
                // Z
                break;
            case MOVLW:
                break;
            case RETLW:
                break;
            case SUBLW:
                //C, CD, Z

                break;
            case XORLW:
                // Z
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

        if (other.getWorking() != this.working) {
            return false;
        }

        if (other.getMemory().length != this.memory.length) {
            return false;
        }

        // Check Speicher
        for(int i = 0 ; i < other.getMemory().length ; i++) {

            if(other.getMemory()[i] != this.memory[i]) {
                return false;
            }
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
