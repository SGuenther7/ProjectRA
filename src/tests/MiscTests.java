package tests;

import Model.Command;
import Model.Instruction;
import Model.Worker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MiscTests {

    @Test
    void StackOverflowTest() {
        Model.Stack temp = new Model.Stack();

        for (int i = 0; i < 9; i++) {
            temp.push(i);
        }

        assertEquals(true, temp.isOverflow());
    }

    @Test
    void carryAddSetTest() {
        // TODO: imp.
    }

    @Test
    void carrySubTest() {
        // TODO: imp.
    }

    @Test
    void carryResetTest() {
        // TODO: imp.
    }

    @Test
    void digitCarryAddSetTest() {
        // TODO: imp.
    }

    @Test
    void digitCarrySubTest() {
        // TODO: imp.
    }

    @Test
    void digitCarryResetTest() {
        // TODO: imp.
    }

    @Test
    void DestinationBitTest() {
        // TODO: imp.
    }



}


