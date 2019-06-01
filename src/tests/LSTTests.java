package tests;

import Model.Command;
import Model.Instruction;
import Model.Parser;
import Model.Worker;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class LSTTests {
    // Komplette Tests von allen Komponenten
    // Durchläuft alle LST Dateien und ueberprueft
    // ob diese die korrekten Werte liefern.

    // Laden von Datei ausgeschlossen.
    // Fokus liegt auf W und Status Register,
    // wie auch Timer und Interrupt Verhalten.

    @Test
    void LST1Test() {
        ArrayList<String> lines = new ArrayList();

        lines.add("0000 3011           00017           movlw 11h           ");
        lines.add("0001 3930           00018           andlw 30h           ");
        lines.add("0002 380D           00019           iorlw 0Dh           ");
        lines.add("0003 3C3D           00020           sublw 3Dh           ");
        lines.add("0004 3A20           00021           xorlw 20h           ");
        lines.add("0005 3E25           00022           addlw 25h           ");
        lines.add("0006 2806           00026           goto ende           ");

        Worker peon = new Worker();
        peon.feed(Parser.parseMultible(Parser.cut(lines)));

        // MOVLW
        peon.next();
        assertEquals(0x11, peon.getWorking());

        // ANDLW
        peon.next();
        assertEquals(0x10, peon.getWorking());
        assertEquals(0, peon.getMemory().getZero());

        // IORLW
        peon.next();
        assertEquals(0x1D, peon.getWorking());
        assertEquals(0, peon.getMemory().getZero());

        // SUBLW
        peon.next();
        assertEquals(0x20, peon.getWorking());
        assertEquals(1, peon.getMemory().getCarry());
        assertEquals(1, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());

        // XORLW
        peon.next();
        assertEquals(0x00, peon.getWorking());
        assertEquals(1, peon.getMemory().getCarry());
        assertEquals(1, peon.getMemory().getDitgitCarry());
        assertEquals(1, peon.getMemory().getZero());

        // ADDLW
        peon.next();
        assertEquals(0x25, peon.getWorking());
        assertEquals(1, peon.getMemory().getCarry());
        assertEquals(1, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());
    }

    @Test
    void LST2Test() {
        ArrayList<String> lines = new ArrayList();

        lines.add("0000 3011           00016           movlw 11h           ;in W steht nun 11h, Statusreg. unverändert");
        lines.add("0001 2006           00017           call up1            ;beim Call wird Rücksprungadresse auf Stack gelegt");
        lines.add("0002 0000           00018           nop                 ;W = 36h, C=0, DC=0, Z=0");
        lines.add("0003 2008           00019           call up2            ;in W steht der Rückgabewert");
        lines.add("0004 0000           00020           nop                 ;W = 77h, DC=0, C=0, Z=0;");
        lines.add("0005 2800           00021           goto loop");
        lines.add("0006 3E25           00024  up1      addlw 25h           ;W = 36h, DC=0, C=0, Z=0");
        lines.add("0007 0008           00025           return");
        lines.add("0008 3477           00028  up2      retlw 77h");
        lines.add("0009 2809           00031           goto ende           ;Endlosschleife, verhindert Nirwana");

        Worker peon = new Worker();
        peon.feed(Parser.parseMultible(Parser.cut(lines)));

        // MOVLW (1)
        peon.next();
        assertEquals(0x11, peon.getWorking());

        // CALL (1)
        peon.next();
        assertEquals(2, peon.getStack().elementAt(0));

        // ADDLW (6)
        peon.next();
        assertEquals(0x36, peon.getWorking());
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());

        // RETURN (7)
        peon.next();
        assertEquals(2, peon.getCurrent());

        // NOP (2)
        peon.next();
        assertEquals(0x36, peon.getWorking());
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());

        // CALL (3)
        peon.next();
        assertEquals(4, peon.getStack().elementAt(0));

        // RETLW (8)
        peon.next();
        assertEquals(4, peon.getCurrent());

        // NOP (4)
        peon.next();
        assertEquals(0x77, peon.getWorking());
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());

        // GOTO (5)
        peon.next();
        assertEquals(0, peon.getCurrent());
    }


    @Test
    void LST3Test() {

    }


    @Test
    void LST4Test() {

    }


    @Test
    void LST5Test() {

    }


    @Test
    void LST6Test() {

    }


    @Test
    void LST7Test() {

    }


    @Test
    void LST8Test() {

    }


    @Test
    void LST9Test() {

    }


    @Test
    void LST10Test() {

    }


    @Test
    void LST11Test() {

    }


    @Test
    void LST12Test() {

    }


    @Test
    void LST13Test() {

    }


    @Test
    void LST14Test() {

    }
}
