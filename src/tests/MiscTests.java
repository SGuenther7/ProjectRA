package tests;

import Model.Command;
import Model.Instruction;
import Model.Memory;
import Model.Worker;
import com.sun.org.apache.bcel.internal.generic.NOP;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MiscTests {

    @Test
    void bankZugriffTest() {
        Worker peon = new Worker(32);

        Command switchBank = new Command(Instruction.MOVWF, new int[]{3});
        Command loadW = new Command(Instruction.MOVLW, new int[]{1});
        Command movTRIS = new Command(Instruction.MOVWF, new int[]{5});

        peon.feed(switchBank);
        peon.feed(loadW);
        peon.feed(movTRIS);

        peon.execute(0);
        peon.execute(1);
        peon.execute(2);

        assertEquals(1,peon.getMemory().content()[1][5]);
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

        Command initial = new Command(Instruction.CALL, new int[]{2});
        Command nested = new Command(Instruction.CALL, new int[]{4});
        Command nop = new Command(Instruction.NOP, new int[]{});
        Command ret = new Command(Instruction.RETURN, new int[]{});
        Command clr = new Command(Instruction.CLRW, new int[]{});
        Command mov = new Command(Instruction.MOVLW, new int[]{3});

        peon.feed(initial);
        peon.feed(clr);
        peon.feed(nested);
        peon.feed(ret);
        peon.feed(mov);
        peon.feed(ret);
        peon.feed(nop);

        // Spring zu nested : 2
        peon.next();
        // Spring zu mov : 5
        peon.next();
        assertEquals(2, peon.getWorking());
        // Setze W zu 3 : 6
        peon.next();
        assertEquals(3, peon.getWorking());
        // Return zu nested 2
        peon.next();
        // Return zu initial 0
        peon.next();
        // Clear W : 2
        peon.next();
        assertEquals(0, peon.getWorking());
    }

    @Test
    void stackOverflow() {
        Worker peon = new Worker(2);

        Command nop = new Command(Instruction.NOP, new int[]{});
        Command call = new Command(Instruction.CALL, new int[]{0});

        peon.feed(nop);
        peon.feed(call);

        for (int i = 0; i < 26; i++) {
            peon.next();
        }

        assertEquals(true, peon.getStack().isOverflow());
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
        assertEquals(true, peon.handleCarryFlagOnSub(15, 15));
        assertEquals(false, peon.handleCarryFlagOnSub(1, 2));
    }

    @Test
    void carryFlagAddTest() {
        Worker peon = new Worker();
        assertEquals(true, peon.handleCarryFlagOnAdd(255, 1));
        assertEquals(false, peon.handleCarryFlagOnAdd(254, 1));
    }


    @Test
    void digitCarryFlagAddTest() {
        Worker peon = new Worker();
        assertEquals(true, peon.handleDigitCarryOnAdd(127, 1));
        assertEquals(false, peon.handleDigitCarryOnAdd(126, 1));
    }

    @Test
    void digitCarryFlagSubTest() {
        Worker peon = new Worker();
        assertEquals(true, peon.handleDigitCarryOnSub(16, 1));
        assertEquals(false, peon.handleDigitCarryOnSub(32, 16));
    }

    @Test
    void zeroFlagTest() {
        Worker peon = new Worker();
        assertEquals(true, peon.handleZeroFlag(0));
        assertEquals(false, peon.handleZeroFlag(254));
    }

    @Test
    void destinationBitTest() {
        Worker peon = new Worker(2);
        peon.getMemory().content()[0][13] = 3;
        Command add = new Command(Instruction.ADDWF, new int[]{13, 1});

        peon.feed(add);
        peon.execute(0);

        assertEquals(5, peon.getMemory().content()[0][13]);
    }

    @Test
    void destinationBitIndAdressTest() {
        Worker peon = new Worker(13);
        peon.getMemory().content()[0][13] = 3;

        Command mov = new Command(Instruction.MOVWF, new int[]{4});
        Command setw = new Command(Instruction.MOVLW, new int[]{2});
        Command add = new Command(Instruction.ADDWF, new int[]{0, 1});

        peon.feed(mov);
        peon.feed(setw);
        peon.feed(add);

        peon.execute(0);
        assertEquals(13, peon.getMemory().content()[0][4]);
        peon.execute(1);
        peon.execute(2);
        assertEquals(5, peon.getMemory().content()[0][13]);
    }

    @Test
    void portCachingIn() {
        // Schreibe an PIN von PortA
        // Check ob PortA Register geaendert wurde (false)
        // Schreibe in TrisA
        // Check Register nochmal (true)
        Worker peon = new Worker(1);

        Command movPIN = new Command(Instruction.MOVWF, new int[]{5});
        Command loadSwitch = new Command(Instruction.MOVLW, new int[]{32});
        Command switchBank = new Command(Instruction.MOVWF, new int[]{3});
        Command loadW = new Command(Instruction.MOVLW, new int[]{1});
        Command movTRIS = new Command(Instruction.MOVWF, new int[]{5});

        peon.feed(movPIN);
        peon.feed(loadSwitch);
        peon.feed(switchBank);
        peon.feed(loadW);
        peon.feed(movTRIS);

        peon.execute(0);

        // Nicht auf Port geschrieben
        assertEquals(peon.getMemory().content()[0][5], 0);
        // In zwischen Register geladen
        assertEquals(1, peon.getPortA().get());

        // Switche Bank
        peon.execute(1);
        peon.execute(2);
        peon.execute(3);

        // Setze TRIS bit
        peon.execute(4);

        // Jetzt von Zwischenspei. in Port geladen
        assertEquals(1,peon.getMemory().content()[0][5]);
    }


    @Test
    void checkCycles() {
        Worker peon = new Worker();
        Command nop = new Command(Instruction.NOP, new int[]{});
        peon.feed(nop);
        peon.feed(nop);
        peon.next();
        peon.next();
        assertEquals(2, peon.getCycles());
    }

    @Test
    void checkTMRAccessCycles() {
        Worker peon = new Worker(1);
        Command set = new Command(Instruction.MOVWF, new int[]{1});

        peon.feed(set);
        assertEquals(0,peon.getCycles());
        peon.next();
        assertEquals(2,peon.getCycles());
    }

    @Test
    void watchdogTest() {
        Worker peon = new Worker();

        // Starte WDT
        peon.getTimer().wdtEnabled = true;
        peon.getMemory().content()[1][1] = 0b0;

        Command nop = new Command(Instruction.NOP, new int[]{});
        Command jump = new Command(Instruction.GOTO, new int[]{0});

        peon.feed(nop);
        peon.feed(jump);

        for (int i = 0; i < 18001; i++) {
            peon.next();
        }

        assertEquals(true, peon.getTimer().reset);
    }

    @Test
    void watchdogScalerAllReset() {
        Worker peon = new Worker();

        Command nop = new Command(Instruction.NOP, new int[]{});
        Command jump = new Command(Instruction.GOTO, new int[]{0},2);

        peon.feed(nop);
        peon.feed(jump);

        // Starte WDT
        peon.getTimer().wdtEnabled = true;

        // TMR auf Port, Prescaler auf 128 und WDT
        peon.getMemory().content()[1][1] = 0b0101111;

        for (int i = 1; i < 2304000; i++) {
            peon.next();
        }
        assertEquals(false, peon.getTimer().reset);

        peon.next();
        assertEquals(true, peon.getTimer().reset);
    }

    @Test
    void watchdogScalerHalfReset() {
        Worker peon = new Worker();

        Command nop = new Command(Instruction.NOP, new int[]{});
        Command jump = new Command(Instruction.GOTO, new int[]{0});

        peon.feed(nop);
        peon.feed(jump);

        // Starte WDT
        peon.getTimer().wdtEnabled = true;

        // TMR auf Port, Prescaler auf 8 und WDT
        peon.getMemory().content()[1][1] = 0b101011;

        for (int i = 1; i < 144000; i++) {
            peon.next();
        }
        assertEquals(false, peon.getTimer().reset);

        peon.next();
        assertEquals(true, peon.getTimer().reset);
    }


    @Test
    void timerScalerFullTest() {
        Worker peon = new Worker();

        // GOTO hier als 1-Zyclus befehl
        Command jump = new Command(Instruction.GOTO, new int[]{0});
        Command nop = new Command(Instruction.NOP, new int[]{});

        peon.feed(nop);
        peon.feed(jump);

        // Prescaler auf 128 und TMR
        peon.getMemory().content()[1][1] = 0b00111;

        for (int i = 1; i < 65536; i++) {
            peon.next();
        }
        assertEquals(0,peon.getMemory().content()[0][12] & 4);
        peon.next();
        assertEquals(4,peon.getMemory().content()[0][12] & 4);
    }


    @Test
    void timerScalerHalfTest() {
        Worker peon = new Worker();

        // GOTO hier als 1-Zyclus befehl
        Command jump = new Command(Instruction.GOTO, new int[]{0});
        Command nop = new Command(Instruction.NOP, new int[]{});

        peon.feed(nop);
        peon.feed(jump);

        // Prescaler auf 8 und TMR
        peon.getMemory().content()[1][1] = 0b00011;

        // 256 * 16
        for (int i = 1; i < 4096; i++) {
            peon.next();
        }
        assertEquals(0,peon.getMemory().content()[0][12] & 4);
        peon.next();
        assertEquals(4,peon.getMemory().content()[0][12] & 4);
    }

    @Test
    void timerJustScalerTest() {
        Worker peon = new Worker();

        // GOTO hier als 1-Zyclus befehl
        Command jump = new Command(Instruction.GOTO, new int[]{0});
        Command nop = new Command(Instruction.NOP, new int[]{});

        peon.feed(nop);
        peon.feed(jump);

        // Prescaler auf 0 und TMR
        peon.getMemory().content()[1][1] = 0;

        // 256 *  2
        for (int i = 1; i < 512; i++) {
            peon.next();
        }
        assertEquals(0,peon.getMemory().content()[0][12] & 4);
        peon.next();
        assertEquals(4,peon.getMemory().content()[0][12] & 4);
    }

    @Test
    void timerPresetTest() {
        Worker peon = new Worker();

        // GOTO hier als 1-Zyclus befehl
        Command jump = new Command(Instruction.GOTO, new int[]{0});
        Command nop = new Command(Instruction.NOP, new int[]{});

        peon.feed(nop);
        peon.feed(jump);

        // Prescaler auf 0 und WDT
        peon.getMemory().content()[1][1] = 0b1000;

        // Timer Register vor setzten
        peon.getMemory().content()[0][1] = 128;

        // (256 - 128)  *  1
        for (int i = 1; i < 128; i++) {
            peon.next();
        }
        assertEquals(0,peon.getMemory().content()[0][12] & 4);
        peon.next();
        assertEquals(4,peon.getMemory().content()[0][12] & 4);
    }

    @Test
    void timerPresetWithScalerTest() {
        Worker peon = new Worker();

        // GOTO hier als 1-Zyclus befehl
        Command jump = new Command(Instruction.GOTO, new int[]{0});
        Command nop = new Command(Instruction.NOP, new int[]{});

        peon.feed(nop);
        peon.feed(jump);

        // Prescaler auf 256 und TMR
        peon.getMemory().content()[1][1] = 0b0111;

        // Timer Register vor setzten
        peon.getMemory().content()[0][1] = 128;

        // (256 - 128)  *  256
        for (int i = 1; i < 32768; i++) {
            peon.next();
        }
        assertEquals(0,peon.getMemory().content()[0][12] & 4);
        peon.next();
        assertEquals(4,peon.getMemory().content()[0][12] & 4);
    }

    @Test
    void timerTest() {
        Worker peon = new Worker();

        // TMR auf Clock, Prescaler auf WDT
        peon.getMemory().content()[1][1] = 0b1000;

        Command nop = new Command(Instruction.NOP, new int[]{});
        Command jump = new Command(Instruction.GOTO, new int[]{0});

        peon.feed(nop);
        peon.feed(jump);

        // Kein Vorteiler, also 255 + 1 fuer Ueberfluss
        for (int i = 1; i < 256; i++) {
            peon.next();
        }
        assertEquals(0, peon.getMemory().content()[0][12] & 4);
        peon.next();
        assertEquals(4,peon.getMemory().content()[0][12] & 4);
    }

    @Test
    void timerCloseToOverflowTest() {
        // Scaler gesetzt, kurz vor ueberfluss
        // aber ueber standard wert
        Worker peon = new Worker();

        // TMR auf Clock, Prescaler auf WDT
        peon.getMemory().content()[1][1] = 0b1000;

        Command nop = new Command(Instruction.NOP, new int[]{});
        Command jump = new Command(Instruction.GOTO, new int[]{0});

        peon.feed(nop);
        peon.feed(jump);

        // Kein Vorteiler, also 255 + 1 fuer Ueberfluss
        for (int i = 1; i < 131; i++) {
            peon.next();
        }
        assertEquals(0, peon.getMemory().content()[0][12] & 4);
    }

    @Test
    void timerScalerResetTest(){
        Worker peon = new Worker();

        // TMR auf Clock, Prescaler auf WDT
        peon.getMemory().content()[1][1] = 0b1000;

        Command nop = new Command(Instruction.NOP, new int[]{});
        Command jump = new Command(Instruction.GOTO, new int[]{0});
        Command reset = new Command(Instruction.CLRF, new int[]{1});

        peon.feed(nop);
        peon.feed(jump);

        // Kein Vorteiler, also 255 + 1 fuer Ueberfluss
        for (int i = 1; i < 254; i++) {
            peon.next();
        }

        // PC zuruecksetzen
        peon.feed(reset);
        peon.getMemory().content()[0][2] = 2;
        peon.setCurrent(2);
        peon.next();

        peon.feed(jump);
        peon.next();

        // Kein Vorteiler, also 255 + 1 fuer Ueberfluss
        for (int i = 1; i < 16; i++) {
            peon.next();
        }

        assertEquals(0, peon.getMemory().content()[0][12] & 4);
    }

    @Test
    void interruptJumpTest() {
        Worker peon = new Worker();

        // Setze Interrupt Bits
        peon.getMemory().content()[0][12] = 160;

        // Setze Vorteiler
        peon.getMemory().content()[1][1] = 0b0;

        Command nop = new Command(Instruction.NOP, new int[]{});
        Command jump = new Command(Instruction.GOTO, new int[]{0});

        peon.feed(nop);
        peon.feed(jump);
        peon.feed(nop);
        peon.feed(nop);
        peon.feed(nop);

        for (int i = 0; i < 512; i++) {
            peon.next();
        }

        assertEquals(4, peon.getCurrent());
    }
}


