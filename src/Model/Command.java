package Model;

import Model.Instruction;

public class Command {
    Instruction instruction;
    int value[];

    public Command(Instruction instruction, int value[]) {
        this.instruction = instruction;
        this.value = value;
    }

}
