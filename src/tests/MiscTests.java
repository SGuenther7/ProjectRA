package tests;

import Model.Command;
import Model.Instruction;
import Model.Memory;
import Model.Worker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MiscTests {

    @Test
    void bankZugriffTest() {
        // TODO memory testen
    }


    @Test
    void memoryEqualsFalseTest() {
        Memory wild = new Memory(new Worker());
        wild.set(0,5,3);
        wild.set(1,7,9);
        Memory base = new Memory(new Worker());

        assertFalse(wild.equals(base));
    }

    @Test
    void memoryEqualsCloneTrueTest() {
        Memory base = new Memory(new Worker());
        base.set(0,5,3);
        base.set(1,7,9);

        assertTrue(base.equals(new Memory(base)));
    }

    @Test
    void memoryEqualsTrueTest() {

        Memory base = new Memory(new Worker());
        base.set(0,15,3);
        base.set(1,17,9);

        Memory other = new Memory(new Worker());
        other.set(0,15,3);
        other.set(1,17,9);

        assertTrue(base.equals(other));
    }

    @Test
    void memoryCloneTest() {
        Memory wild = new Memory(new Worker());
        Memory clone = new Memory(wild);

        assertEquals(wild,clone);
    }

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

    @Test
    void indirekteAdressierungTest() {

        Worker peon = new Worker();
        // Lade 13 in W
        Command load = new Command(Instruction.MOVLW, new int[]{0xD});
        // Schiebe W in 4
        Command push = new Command(Instruction.MOVWF, new int[]{0x4});
        // Lade 8 in W
        Command read = new Command(Instruction.MOVLW, new int[]{0x8});
        // Schreibe W indirekt
        Command write = new Command(Instruction.MOVWF, new int[]{0x0});

        // Register 12 sollte jetzt Inhalt 8 haben

        peon.feed(load);
        peon.feed(push);
        peon.feed(read);
        peon.feed(write);

        peon.execute(0);
        peon.execute(1);
        peon.execute(2);
        peon.execute(3);

        assertEquals(0x8, peon.getMemory().get(peon.getBank(),13));
    }
}


