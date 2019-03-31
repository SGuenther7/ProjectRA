package Model;

import Model.Instruction;

public class Operation {
    Instruction instruction;
    int value[];

    public Operation(Instruction instruction, int value[]) {
        this.instruction = instruction;
        this.value = value;
    }

}
