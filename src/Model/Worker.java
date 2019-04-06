package Model;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Stack;

import static Model.Instruction.*;

public class Worker extends Observable {

    int working;
    int[] memory;
    ArrayList<Command> counter;
    Stack stack; // TODO: Ringbuffer bei pop/push

    public Worker() {
        this.working = 0;
        this.memory = new int[94];
        this.counter = new ArrayList<>();
        this.stack = new Stack();
    }

    public void feed(ArrayList<Command> fresh) {
        this.counter.addAll(fresh);
    }

    public void execute(int i) {
        switch (this.counter.get(i).getInstruction()) {
            case ADDWF:
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
                break;
            case XORLW:
                break;
            case CALL:
                break;
            case GOTO:
                break;
        }
    }

    public void next() {


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
