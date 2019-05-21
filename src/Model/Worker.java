package Model;

import java.util.ArrayList;

public class Worker {

    public static int PC_MAX_VALUE = 1024;

    private int working;
    private Memory memory;
    private ArrayList<Command> counter;
    private Stack stack;

    private Port portA;
    private Port portB;

    private Timer timer;

    // TODO: Current setzt sich aus PCL und PCLATH zusammen
    private int current;

    public Worker() {
        this.working = 0;
        this.memory = new Memory(this);
        this.counter = new ArrayList<>();
        this.stack = new Stack();
        this.timer = new Timer(this);

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
        this.current = clone.getCurrent();
    }

    public boolean feed(ArrayList<Command> container) {
        for (Command fresh : container) {
            if (!feed(fresh)) {
                return false;
            }
        }

        counter.get(getCurrent()).setNext(true);

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
        int result;

        switch (command.getInstruction()) {
            case ADDWF:
                // Var : f, d
                // Flag : C, CD, Z

                result = working + memory.get(getBank(), command.getValue()[0]);

                handleCarryFlagOnAdd(working, memory.get(getBank(), command.getValue()[0]));
                handleDigitCarryOnAdd(working, memory.get(getBank(), command.getValue()[0]));
                handleZeroFlag(working);

                // Destination Bit gesetzt ?
                if (command.getValue()[1] == 1) {
                    memory.set(getBank(), command.getValue()[0], result);
                } else {
                    working = result;
                }
                break;
            case ANDWF:
                // Var : f, d
                // Flag : Z

                result = working & memory.get(getBank(), command.getValue()[0]);
                handleZeroFlag(working);

                if (command.getValue()[1] == 1) {
                    memory.set(getBank(), command.getValue()[0], result);
                } else {
                    working = result;
                }
                break;
            case DECF:
                // Var : f, d
                // Flag : Z

                result = memory.get(getBank(), command.getValue()[0]) - 1;
                handleZeroFlag(working);

                if (command.getValue()[1] == 1) {
                    memory.set(getBank(), command.getValue()[0], result);
                } else {
                    working = result;
                }
                break;
            case DECFSZ:
                // Var : f, d
                // TODO: imp. + test
                break;
            case INCF:
                // Var : f, d
                // Flag : Z

                result = memory.get(getBank(), command.getValue()[0]) + 1;
                handleZeroFlag(working);

                if (command.getValue()[1] == 1) {
                    memory.set(getBank(), command.getValue()[0], result);
                } else {
                    working = result;
                }
                break;
            case INCFSZ:
                // Var : f, d
                // TODO: imp. + test
                break;
            case IORWF:
                // Var : f, d

                result = working | memory.get(getBank(), command.getValue()[0]);

                if (command.getValue()[1] == 1) {
                    memory.set(getBank(), command.getValue()[0], result);
                } else {
                    working = result;
                }
                break;
            case MOVF:
                // Var : f, d
                result = memory.get(getBank(), command.getValue()[0]);

                if (command.getValue()[1] == 1) {
                    memory.set(getBank(), command.getValue()[0], result);
                } else {
                    working = result;
                }
                break;
            case RLF:
                // Var : f, d
                // TODO: imp. + test
                break;
            case RRF:
                // Var : f, d
                // TODO: imp. + test
                break;
            case SUBWF:
                // Var : f, d
                // Flag : C, CD, Z
                result = memory.get(getBank(), command.getValue()[0]) - working;

                handleCarryFlagOnSub(working, command.getValue()[0]);
                handleDigitCarryOnSub(working, command.getValue()[0]);
                handleZeroFlag(working);

                // Destination Bit gesetzt ?
                if (command.getValue()[1] == 1) {
                    memory.set(getBank(), command.getValue()[0], result);
                } else {
                    working = result;
                }
                break;
            case SWAPF:
                // Var : f, d
                // TODO: imp. + test
                break;
            case XORWF:
                // Var : f, d
                // Flag : Z

                result = working ^ memory.get(getBank(), command.getValue()[0]);

                handleZeroFlag(working);

                if (command.getValue()[1] == 1) {
                    memory.set(getBank(), command.getValue()[0], result);
                } else {
                    working = result;
                }
                break;
            case CLRF:
                // Var : f
                // Flag : Z
                memory.set(getBank(), command.getValue()[0], 0);

                handleZeroFlag(working);
                break;
            case MOVWF:
                // Var : f
                memory.set(getBank(), command.getValue()[0], working);
                break;
            case CLRW:
                // Flag : Z
                handleZeroFlag(working); // TODO: korrekt behav. ?
                working = 0;
                break;
            case NOP:
                break;
            case CLRWDT:
                // Flag : TO, TP
                // TODO: imp. + test
                break;
            case RETFIE:
                // TODO: imp. + test
                break;
            case RETURN:
                // TODO: imp. + test
                if (stack.size() > 0) {
                    memory.content()[0][2] = (int) stack.pop();
                }
                break;
            case SLEEP:
                // Flag : TO, TP
                // TODO: imp. + test
                break;
            case BCF:
                // Var : f, b
                // TODO: imp. + test
                break;
            case BSF:
                // Var : f, b
                // TODO: imp. + test
                break;
            case BTFSS:
                // Var : f, b
                // TODO: imp. + test
                break;
            case BTFSC:
                // Var : f, b
                // TODO: imp. + test

                break;
            case ADDLW:
                // Var : k
                // C, CD, Z
                result = working + command.getValue()[0];

                handleCarryFlagOnAdd(working, memory.get(getBank(), command.getValue()[0]));
                handleDigitCarryOnAdd(working, memory.get(getBank(), command.getValue()[0]));
                handleZeroFlag(working);

                working = result;
                break;
            case ANDLW:
                // Var : k
                // Flag : Z
                result = working & command.getValue()[0];
                handleZeroFlag(working);
                working = result;
                break;
            case IORLW:
                // Var : k
                // Flag : Z
                result = working | command.getValue()[0];
                handleZeroFlag(working);
                working = result;
                break;
            case MOVLW:
                // Var : k
                working = command.getValue()[0];
                break;
            case RETLW:
                // Var : k
                // TODO: imp. + test
                break;
            case SUBLW:
                // Var : k
                // Flag : C, CD, Z
                result = command.getValue()[0] - working;

                handleCarryFlagOnSub(working, command.getValue()[0]);
                handleDigitCarryOnSub(working, command.getValue()[0]);
                handleZeroFlag(working);

                working = result;
                break;
            case XORLW:
                // Var : k
                // Flag : Z
                working = working ^ command.getValue()[0];
                handleZeroFlag(working);
                break;
            case CALL:
                // Var : k
                stack.push(getCurrent());
            case GOTO:
                // Var : k
                memory.content()[0][2] = counter.get(i).getValue()[0];
                break;
        }

        // Timer updaten
        timer.tick();
        updateCurrent();
    }

    public void next() {
        counter.get(getCurrent()).setNext(false);
        execute(getCurrent());
        if (counter.size() > getCurrent()) {
            counter.get(getCurrent()).setNext(true);
        }
    }

    public void run() {

        // TODO: Break points (?)
        while (hasNext() && getCurrent() != counter.size() - 1) {
            next();
        }
    }

    public boolean hasNext() {
        return (getCurrent() < counter.size() - 1);
    }

    private boolean handleZeroFlag(int value) {
        if (value == 0) {
            memory.set(getBank(), 3, memory.get(getBank(), 3) | 3);
            return true;
        }
        return false;
    }

    private boolean handleCarryFlagOnAdd(int base, int add) {
        if (base + add > 25) { // TODO handleCarryFlag testen
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

        if (base + add > 15) { // TODO handleDitiCarryFlag testen
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

    public void updateCurrent() {
        memory.content()[0][2]++;

        if (memory.content()[0][2] >= PC_MAX_VALUE) {
            memory.set(getBank(), 2, memory.get(getBank(), 3) | 1);
            memory.content()[0][2] = 0;
        }
        applyPCL();
    }

    private void applyPCL() {
        int temp = memory.content()[0][10];
        temp = temp << 8;
        temp += memory.content()[0][2];
        current = temp;
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

    public Port getPortA() {
        return portA;
    }

    public Port getPortB() {
        return portB;
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
