package tests;

import Model.Command;
import Model.Instruction;
import Model.Worker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandTest {
    @Test
    void ADDWFTest() {
        // W = 0Ah, C=x, DC=x, Z=0
        Worker expected = new Worker(10);
        expected.getMemory()[13] = 5;

        // Lade 5 in W und in 13
        // Erwarte 10 in W
        Worker peon = new Worker(5);
        peon.getMemory()[13] = 5;

        peon.feed(new Command(Instruction.ADDWF,new int[]{0,13}));
        peon.execute(0);

        assertEquals(expected,peon);
    }

    @Test
    void ANDWFTest() {
    }

    @Test
    void CLRFTest() {
    }

    @Test
    void CLRWTest() {
    }

    @Test
    void COMFTest() {
    }

    @Test
    void DECFTest() {
    }

    @Test
    void DECFSZTest() {
    }

    @Test
    void INCFTest() {
    }

    @Test
    void INCFSZTest() {
    }

    @Test
    void IORWFTest() {
    }

    @Test
    void MOVFTest() {
    }

    @Test
    void MOVWFTest() {
        // W = 10h, C=x, DC=x, Z=0
        Worker expected = new Worker(17);
        expected.getMemory()[13] = 17;

        Worker peon = new Worker(17);
        peon.feed(new Command(Instruction.MOVWF,new int[]{13}));
        peon.execute(0);

        assertEquals(expected,peon);
    }

    @Test
    void NOPTest() {
    }

    @Test
    void RLFTest() {
    }

    @Test
    void RRFTest() {
    }

    @Test
    void SUBWFTest() {
    }

    @Test
    void SWAPFTest() {
    }

    @Test
    void XORWFTest() {
    }

    @Test
    void BCFTest() {
    }

    @Test
    void BSFTest() {
    }

    @Test
    void ADDLWTest() {
    }

    @Test
    void ANDLWTest() {
    }

    @Test
    void CALLTest() {
    }

    @Test
    void CRLWDTTest() {
    }

    @Test
    void GOTOTest() {
    }

    @Test
    void IORLWTest() {
    }

    @Test
    void MOVLWTest() {
        // W = 10h, C=x, DC=x, Z=0
        Worker expected = new Worker(17,new int[94]);
        Worker peon = new Worker();
        Command target = new Command(Instruction.MOVLW,new int[]{0x11});

        peon.feed(target);
        peon.execute(0);

        assertEquals(expected,peon);
    }

    @Test
    void RETFIETest() {
    }

    @Test
    void RETLWTest() {
    }

    @Test
    void RETURNTest() {
    }

    @Test
    void SLEEPTest() {
    }

    @Test
    void SUBLWTest() {
    }

    @Test
    void XORLWTest() {
    }
}
