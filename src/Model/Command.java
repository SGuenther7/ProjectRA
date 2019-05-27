package Model;

public class Command {
    private Instruction instruction;
    private int value[];
    private int cycles = 1;

    private boolean next;
    private boolean selected;
    private boolean breakpoint = false;

    public Command(Instruction instruction, int value[]) {
        this.instruction = instruction;
        this.value = value;
    }

    public Command(Instruction instruction, int value[], int cycles) {
        this(instruction,value);
        this.cycles = cycles;
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

    public void print() {
        System.out.print(instruction + " ");

        if (value == null) {
            return;
        }

        System.out.print(": ");

        for (int i = 0; i < value.length; i++) {
            System.out.print(getValue()[i] + " ");
        }

        System.out.println();
    }

    @Override
    public boolean equals(Object obj) {

        if (((Command) obj).instruction != this.instruction) {
            return false;
        }

        if (((Command) obj).value == null && this.value == null) {
            return true;
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

    public int getCycles() {
        return cycles;
    }

    public void setCycles(int cycles) {
        this.cycles = cycles;
    }
}
