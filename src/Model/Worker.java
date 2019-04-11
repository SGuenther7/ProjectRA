package Model;

import java.util.ArrayList;
import java.util.Observable;

import static Model.Instruction.*;

public class Worker extends Observable {

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

    public void feed(ArrayList<Command> fresh) {
        // Programmspeicher limit von 1024
        if (counter.size() > 1024) {
            this.counter.addAll(fresh);
        }
    }

    public void execute(int i) {
        switch (this.counter.get(i).getInstruction()) {
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
                this.stack.push(this.current);
                this.current = this.counter.get(i).getValue()[0];
                break;
            case GOTO:
                this.current = this.counter.get(i).getValue()[0];
                break;
        }
    }

    public void next() {
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

        if(base + add > 15) {
            this.memory[3] = this.memory[3] | 2;
            return true;
        }

        return false;
    }

    private boolean handleDigitCarryOnSub(int base, int sub) {
        base = base & 15;
        sub = sub & 15;

        if(base - sub < 0) {
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
}
