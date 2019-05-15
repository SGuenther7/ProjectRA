package Model;

public class Command {
    private Instruction instruction;
    private int value[];

    private boolean next;
    private boolean selected;
    private boolean breakpoint = false;

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

    public void toggleBreakpoint() {
        breakpoint = !breakpoint;
    }

    public boolean isBreakpoint() {
        return breakpoint;
    }

    @Override
    public boolean equals(Object obj) {

        if (((Command) obj).instruction != this.instruction) {
            return false;
        }


        if (((Command) obj).value.length != this.value.length) {
            return false;
        }


        for (int i = 0; i < this.value.length; i++) {

            if (((Command) obj).value[i] != this.value[i]) {
                return false;
            }
        }

        return true;
    }
}
