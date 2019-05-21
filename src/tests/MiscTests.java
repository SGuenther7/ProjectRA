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
        wild.set(0, 5, 3);
        wild.set(1, 7, 9);
        Memory base = new Memory(new Worker());

        assertFalse(wild.equals(base));
    }

    @Test
    void memoryEqualsCloneTrueTest() {
        Memory base = new Memory(new Worker());
        base.set(0, 5, 3);
        base.set(1, 7, 9);

        assertTrue(base.equals(new Memory(base)));
    }

    @Test
    void memoryEqualsTrueTest() {

        Memory base = new Memory(new Worker());
        base.set(0, 15, 3);
        base.set(1, 17, 9);

        Memory other = new Memory(new Worker());
        other.set(0, 15, 3);
        other.set(1, 17, 9);

        assertTrue(base.equals(other));
    }

    @Test
    void memoryCloneTest() {
        Memory wild = new Memory(new Worker());
        Memory clone = new Memory(wild);

        assertEquals(wild, clone);
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
    void multipleCall() {
        Worker peon = new Worker(2);

        Command initial= new Command(Instruction.CALL, new int[]{1});
		Command nested = new Command(Instruction.CALL, new int[]{4});
		Command nop = new Command(Instruction.NOP, new int[]{});
		Command ret = new Command(Instruction.RETURN, new int[]{});
        Command clr = new Command(Instruction.CLRW, new int[]{});
        Command mov = new Command(Instruction.MOVLW, new int[]{3});

		peon.feed(initial);
        peon.feed(clr);
		peon.feed(nested);
        peon.feed(ret);
        peon.feed(nop);
        peon.feed(mov);
        peon.feed(ret);
        peon.feed(nop);

        // Spring zu nested : 2
        peon.next();
        // Spring zu mov : 5
        peon.next();
        assertEquals(2,peon.getWorking());
        // Setze W zu 3 : 6
        peon.next();
        assertEquals(3,peon.getWorking());
        // Return zu nested+1 : 3
        peon.next();
        // Return zu initial+1 : 1
        peon.next();
        // Clear W : 2
        peon.next();
        assertEquals(0,peon.getWorking());
    }

    @Test
    void stackOverflow() {
        Worker peon = new Worker(2);

        Command nop = new Command(Instruction.NOP, new int[]{});
        Command call = new Command(Instruction.CALL, new int[]{0});

        peon.feed(nop);
        peon.feed(call);

        peon.next();
        peon.next();
        peon.next();
        peon.next();
        peon.next();
        peon.next();
        peon.next();
        peon.next();
        peon.next();

        assertEquals(true,peon.getStack().isOverflow());
    }


    @Test
    void PCmanipulationVerhaltenTest() {
        Worker peon = new Worker(2);

        Command prepare = new Command(Instruction.ADDWF, new int[]{2, 1});
        Command clear = new Command(Instruction.CLRW, new int[]{});
        Command nop = new Command(Instruction.NOP, new int[]{});

        // Lade working (2) in PCL (0x2h)
        peon.feed(prepare);
        // Ueberspringe clearw
        peon.feed(clear);
        // Anstatt ausgefuehrt
        peon.feed(nop);
        peon.feed(nop);

        peon.execute(0);
        peon.execute(peon.getCurrent());

        assertEquals(2, peon.getWorking());
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

        assertEquals(0x8, peon.getMemory().get(peon.getBank(), 13));
    }

    @Test
    void carryFlagSubTest() {
        Worker peon = new Worker();
        assertEquals(true, peon.handleCarryFlagOnSub(15,15));
        assertEquals(false, peon.handleCarryFlagOnSub(1,2));
    }

    @Test
    void carryFlagAddTest() {
        Worker peon = new Worker();
        assertEquals(true, peon.handleCarryFlagOnAdd(255,1));
        assertEquals(false, peon.handleCarryFlagOnAdd(254,1));
    }


    @Test
    void digitCarryFlagAddTest() {
        Worker peon = new Worker();
        assertEquals(true, peon.handleDigitCarryOnAdd(127,1));
        assertEquals(false, peon.handleDigitCarryOnAdd(126,1));
    }

    @Test
    void digitCarryFlagSubTest() {
        Worker peon = new Worker();
        assertEquals(true, peon.handleDigitCarryOnSub(16,1));
        assertEquals(false, peon.handleDigitCarryOnSub(32,16));
    }

    @Test
    void zeroFlagTest() {
        Worker peon = new Worker();
        assertEquals(true, peon.handleZeroFlag(0));
        assertEquals(false, peon.handleZeroFlag(254));
    }
}


