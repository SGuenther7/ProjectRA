package tests;

import Model.Command;
import Model.Instruction;
import Model.Worker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandTests {
	@Test
	void ADDWFTest() {
		// W = 0Ah, C=x, DC=x, Z=0
		Worker expected = new Worker(10);
		expected.getMemory().set(expected.getBank(), 13, 5);

		// Lade 5 in W und in 13
		// Erwarte 10 in W
		Worker peon = new Worker(5);
		peon.getMemory().set(peon.getBank(), 13, 5);

		peon.feed(new Command(Instruction.ADDWF, new int[] { 13, 0 }));
		peon.execute(0);

		assertEquals(expected.getWorking(), peon.getWorking());
	}

	@Test
	void ANDWFTest() {
		Worker expected = new Worker(1);
		expected.getMemory().set(expected.getBank(), 13, 3);

		Worker peon = new Worker(5);
		peon.getMemory().set(peon.getBank(), 13, 3);

		peon.feed(new Command(Instruction.ANDWF, new int[] { 13, 0 }));
		peon.execute(0);

		assertEquals(expected.getWorking(), peon.getWorking());
	}

	@Test
	void CLRFTest() {
		Worker expected = new Worker();
		expected.getMemory().set(expected.getBank(), 13, 0);

		Worker peon = new Worker();
		peon.getMemory().set(peon.getBank(), 13, 13);

		peon.feed(new Command(Instruction.CLRF, new int[] { 12 }));
		peon.execute(0);

		assertEquals(expected.getWorking(), peon.getWorking());
	}

	@Test
	void CLRWTest() {
		Worker expected = new Worker(0);
		expected.getMemory().set(expected.getBank(), 13, 13);

		Worker peon = new Worker(5);
		peon.getMemory().set(peon.getBank(), 13, 13);

		peon.feed(new Command(Instruction.CLRW, new int[] { }));
		peon.execute(0);

		assertEquals(expected.getWorking(), peon.getWorking());
	}

	@Test
	void COMFTest() {
		Worker expected = new Worker(114);
		expected.getMemory().set(expected.getBank(), 13, 5);

		Worker peon = new Worker(13);
		peon.getMemory().set(peon.getBank(), 13, 5);

		peon.feed(new Command(Instruction.COMF, new int[] { 13,0 }));
		peon.execute(0);

		assertEquals(expected.getWorking(), peon.getWorking());
	}

	@Test
	void DECFTest() {
		Worker expected = new Worker();
		expected.getMemory().set(expected.getBank(), 5, 4);

		Worker peon = new Worker();
		peon.getMemory().set(peon.getBank(), 5, 5);

		peon.feed(new Command(Instruction.DECF, new int[] { 5, 1 }));
		peon.execute(0);

		assertEquals(expected.getMemory().content()[0][5], peon.getMemory().content()[0][5]);
	}

	@Test
	void DECFSZTest() {
		Worker expected = new Worker();
		expected.getMemory().content()[0][12] = 11;

		Worker peon = new Worker();
		peon.getMemory().content()[0][12] = 12;

		peon.feed(new Command(Instruction.DECFSZ, new int[] { 12,1 }));
//		peon.feed(new Command(Instruction.DECF, new int[] { 12,1 }));
		peon.execute(0);

		assertEquals(expected.getMemory().content()[0][1], peon.getMemory().content()[0][1]);
	}

	@Test
	void INCFTest() {
		Worker expected = new Worker(14);
		expected.getMemory().set(expected.getBank(), 13, 13);

		Worker peon = new Worker(13);
		peon.getMemory().set(peon.getBank(), 13, 13);

		peon.feed(new Command(Instruction.INCF, new int[] { 13, 0 }));
		peon.execute(0);

		assertEquals(expected.getWorking(), peon.getWorking());
	}

	@Test
	void INCFSZTest() {
		Worker expected = new Worker(14);
		expected.getMemory().content()[0][13] = 13;

		Worker peon = new Worker(13);
		peon.getMemory().content()[0][13] = 13;

		peon.feed(new Command(Instruction.INCFSZ, new int[] { 13, 0 }));
//		peon.feed(new Command(Instruction.INCF, new int[] { 12, 0 }));
		peon.execute(0);

		assertEquals(expected.getWorking(), peon.getWorking());
	}

	@Test
	void IORWFTest() {

		Worker expected = new Worker(5);
		expected.getMemory().set(expected.getBank(), 13, 13);

		Worker peon = new Worker(5);
		peon.getMemory().set(peon.getBank(), 13, 0);

		peon.feed(new Command(Instruction.IORWF, new int[] { 12, 1 }));
		peon.execute(0);

		assertEquals(expected.getWorking(), peon.getWorking());
	}

	@Test
	void MOVFTest() {
		Worker expected = new Worker(15);
		expected.getMemory().set(expected.getBank(), 15, 15);

		Worker peon = new Worker(5);
		peon.getMemory().set(peon.getBank(), 15, 15);

		peon.feed(new Command(Instruction.MOVF, new int[] { 15, 0 }));
		peon.execute(0);

		assertEquals(expected.getWorking(), peon.getWorking());
	}

    @Test
    void MOVWFTest() {
        // W = 10h, C=x, DC=x, Z=0
        Worker expected = new Worker(17);
        expected.getMemory().set(expected.getBank(),13,17);

        Worker peon = new Worker(17);
        peon.getMemory().set(peon.getBank(), 13, 15);
    
        peon.feed(new Command(Instruction.MOVWF, new int[]{13}));
        peon.execute(0);

        assertEquals(expected.getMemory().content()[0][13], peon.getMemory().content()[0][13]);
    }

	@Test
	void NOPTest() {
		Worker expected = new Worker(15);
		expected.getMemory().set(expected.getBank(), 13, 15);

		Worker peon = new Worker(15);
		peon.getMemory().set(peon.getBank(), 13, 15);

		peon.feed(new Command(Instruction.NOP, new int[] {}));
		peon.execute(0);

		assertEquals(expected.getWorking(), peon.getWorking());
	}

	@Test
	void RLFTest() {
		Worker expected = new Worker();
		expected.getMemory().set(expected.getBank(), 13, 26);

		Worker peon = new Worker();
		peon.getMemory().set(peon.getBank(), 13, 13);

		peon.feed(new Command(Instruction.RLF, new int[] {13, 1}));
		peon.execute(0);

		assertEquals(expected.getMemory().content()[0][13], peon.getMemory().content()[0][13]);
		assertEquals(0, peon.getMemory().content()[0][3]);
	}

	@Test
	void RRFTest() {
		Worker expected = new Worker();
		expected.getMemory().set(expected.getBank(), 13, 6);

		Worker peon = new Worker();
		peon.getMemory().set(peon.getBank(), 13, 13);

		peon.feed(new Command(Instruction.RRF, new int[] {13, 1}));
		peon.execute(0);

		assertEquals(expected.getMemory().content()[0][13], peon.getMemory().content()[0][13]);
		assertEquals(1, peon.getMemory().content()[0][3]);
	}

	@Test
	void SUBWFTest() {
		Worker expected = new Worker(10);
		expected.getMemory().set(expected.getBank(), 13, 3);

		Worker peon = new Worker(10);
		peon.getMemory().set(peon.getBank(), 13, 13);

		peon.feed(new Command(Instruction.SUBWF, new int[] {13, 1}));
		peon.execute(0);

		assertEquals(expected.getMemory().content()[0][13], peon.getMemory().content()[0][13]);
	}

	@Test
	void SWAPFTest() {
		Worker expected = new Worker(10);
		expected.getMemory().set(expected.getBank(), 0x25, 0x52);

		Worker peon = new Worker(10);
		peon.getMemory().set(peon.getBank(), 0x25, 0);

		peon.feed(new Command(Instruction.SWAPF, new int[] {0x25, 1}));
		peon.execute(0);

		assertEquals(expected.getMemory().content()[0][0x25], peon.getMemory().content()[0][0x25]);
	}

	@Test
	void XORWFTest() {
		Worker expected = new Worker(5);
		expected.getMemory().set(expected.getBank(), 13, 13);

		Worker peon = new Worker(5);
		peon.getMemory().set(peon.getBank(), 13, 13);

		peon.feed(new Command(Instruction.XORWF, new int[] { 0,1 }));
		peon.execute(0);

		assertEquals(expected.getWorking(), peon.getWorking());
	}

	@Test
	void BCFTest() {
		Worker expected = new Worker(5);
		expected.getMemory().set(expected.getBank(), 3, 0);

		Worker peon = new Worker(5);
		peon.getMemory().set(peon.getBank(), 3, 5);

		peon.feed(new Command(Instruction.BCF, new int[] { 3,5 }));
		peon.execute(0);

		assertEquals(expected.getMemory().content()[0][3], peon.getMemory().content()[0][3]);
	}

	@Test
	void BSFTest() {
		Worker expected = new Worker(5);
		expected.getMemory().set(expected.getBank(), 3, 1);

		Worker peon = new Worker(5);
		peon.getMemory().set(peon.getBank(), 3, 5);

		peon.feed(new Command(Instruction.BSF, new int[] { 3, 5}));
		peon.execute(0);

		assertEquals(expected.getMemory().content()[0][3], peon.getMemory().content()[0][3]);
	}

	@Test
	void ADDLWTest() {
		Worker expected = new Worker(42);
		Worker peon = new Worker(5);

		peon.feed(new Command(Instruction.ADDLW, new int[] { 0x25 }));
		peon.execute(0);

		assertEquals(expected.getWorking(), peon.getWorking());
	}

	@Test
	void ANDLWTest() {
		Worker expected = new Worker(0);
		Worker peon = new Worker(5);
		
		peon.feed(new Command(Instruction.ANDLW, new int[] { 0x30 }));
		peon.execute(0);

		assertEquals(expected.getWorking(), peon.getWorking());
	}

	@Test
	void CALLTest() {
		Worker peon = new Worker(2);

        Command call = new Command(Instruction.CALL, new int[]{2});
		Command clr = new Command(Instruction.CLRW, new int[]{});
		Command nop = new Command(Instruction.NOP, new int[]{});
		Command ret = new Command(Instruction.RETURN, new int[]{});

		peon.feed(call);
		peon.feed(clr);
		peon.feed(nop);
		peon.feed(ret);
		peon.feed(nop);	// Das setNext() in next() kein OOB wirft

		// Springe von 0 auf 2
		peon.next();
		// CLR wurde uebersprungen, lande auf NOP
		peon.next();
		assertEquals(2,peon.getWorking());

		// RETURN zu 0+1 = CLRW
		peon.next();
		// Setze W zurueck
		peon.next();
		assertEquals(0,peon.getWorking());
	}

	@Test
	void RETURNTest() {
		Worker peon = new Worker();
		peon.getStack().push(4);

		Command ret = new Command(Instruction.RETURN, new int[]{});
		peon.feed(ret);
		peon.next();

		assertEquals(4,peon.getCurrent());
	}


	@Test
	void CRLWDTTest() {
	}

	@Test
	void GOTOTest() {
	}

	@Test
	void IORLWTest() {
		Worker expected = new Worker(0xD);
		expected.getMemory().set(expected.getBank(), 15, 15);
	
		Worker peon = new Worker(5);
		peon.getMemory().set(peon.getBank(), 15, 15);

		peon.feed(new Command(Instruction.IORLW, new int[] { 0x0D }));
		peon.execute(0);

		assertEquals(expected.getWorking(), peon.getWorking());
	}

    @Test
    void MOVLWTest() {
        // W = 10h, C=x, DC=x, Z=0
        Worker expected = new Worker(17);
        Worker peon = new Worker(0);

        peon.feed(new Command(Instruction.MOVLW, new int[]{0x11}));
        peon.execute(0);

        assertEquals(expected.getWorking(), peon.getWorking());
    }

	@Test
	void RETFIETest() {
	}

	@Test
	void RETLWTest() {
	}

	@Test
	void SLEEPTest() {
	}

	@Test
	void SUBLWTest() {
		Worker expected = new Worker(0x38);
		Worker peon = new Worker(5);

		peon.feed(new Command(Instruction.SUBLW, new int[] { 0x3D }));
		peon.execute(0);

		assertEquals(expected.getWorking(), peon.getWorking());
	}

	@Test
	void XORLWTest() {
		Worker expected = new Worker(37);
		expected.getMemory().set(expected.getBank(), 13, 13);

		Worker peon = new Worker(5);
		peon.getMemory().set(peon.getBank(), 13, 13);

		peon.feed(new Command(Instruction.XORLW, new int[]{ 0x20 }));
		peon.execute(0);

		assertEquals(expected.getWorking(), peon.getWorking());
	}
}
