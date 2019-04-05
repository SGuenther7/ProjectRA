package Model;

public class Command {
    private Instruction instruction;
    private int value[];

    private boolean next;
    private boolean selected;

    public Command(Instruction instruction, int value[]) {
        this.instruction = instruction;
        this.value = value;
    }

    public Instruction getInstruction() {
        return instruction;
    }

    public int[] getValue() {
        return value;
    }

    public void setValue(int[] value) {
        this.value = value;
    }

    public boolean isNext() {
        return next;
    }

    public void setNext(boolean next) {
        this.next = next;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
