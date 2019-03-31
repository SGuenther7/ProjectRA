package Model;

import java.util.ArrayList;
import java.util.Stack;

import static Model.Instruction.*;

public class Worker {

    int working;
    int[] memory;
    ArrayList<String> counter;
    Stack stack; // TODO: Ringbuffer bei pop/push

    public Worker() {
        this.working = 0;
        this.memory = new int[94];
        this.counter = new ArrayList<>();
        this.stack = new Stack();
    }

    public void feed(ArrayList<String> counter) {
        this.counter.addAll(counter);
    }

    public void execute(int i) {
        switch (Instruction.ERROR) {
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
}
