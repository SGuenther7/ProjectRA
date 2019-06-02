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
        // TODO: Check
        //assertEquals(1, peon.getMemory().getCarry());
        //assertEquals(1, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());

        // XORLW
        peon.next();
        assertEquals(0x0, peon.getWorking());
        assertEquals(1, peon.getMemory().getZero());

        // ADDLW
        peon.next();
        assertEquals(0x25, peon.getWorking());
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
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
        ArrayList<String> lines = new ArrayList();

        lines.add("0000 3011           00034           movlw 11h           ;in W steht nun 11h, DC=?, C=?, Z=?");
        lines.add("0001 008C           00035           movwf wert1         ;diesen Wert abspeichern, DC=?, C=?, Z=?");
        lines.add("0002 3014           00036           movlw 14h           ;W = 14h, DC=?, C=?, Z=?");
        lines.add("0003 070C           00037           addwf wert1,w       ;W = 25h, DC=0, C=0, Z=0");
        lines.add("0004 078C           00038           addwf wert1         ;W = 25h, wert1 = 36h, DC=0, C=0, Z=0");
        lines.add("0005 050C           00039           andwf wert1,w       ;W = 24h, wert1 = 36h, DC=0, C=0, Z=0");
        lines.add("0006 008D           00040           movwf wert2         ;W=24h, wert1=36, wert2=24h");
        lines.add("0007 018C           00041           clrf wert1          ;W=24h, wert1=0, wert2=24h, DC=0, C=0, Z=1");
        lines.add("0008 090D           00042           comf wert2,w        ;W=DBh, wert1=0, wert2=24h, DC=0, C=0, Z=0");
        lines.add("0009 030C           00043           decf wert1,w        ;W=FFh, wert1=0, wert2=24h, DC=0, C=0, Z=0");
        lines.add("000A 0A8D           00044           incf wert2          ;W=FFh, wert1=0, wert2=25h, DC=0, C=0, Z=0");
        lines.add("000B 088C           00045           movf wert1          ;W=FFh, wert1=0, wert2=25h, DC=0, C=0, Z=1");
        lines.add("000C 048C           00046           iorwf wert1         ;W=FFh, wert1=FFh, wert2=25h, DC=0, C=0, Z=0");
        lines.add("000D 020D           00047           subwf wert2,w       ;W=26h, wert1=FFh, wert2=25h, DC=0, C=0, Z=0");
        lines.add("000E 0E8D           00048           swapf wert2         ;W=26h, wert1=FFh, wert2=52h, DC=0, C=0, Z=0");
        lines.add("000F 068C           00049           xorwf wert1         ;W=26h, wert1=D9h, wert2=52h, DC=0, C=0, Z=0");
        lines.add("0010 0100           00050           clrw                ;W=00h, wert1=D9h, wert2=52h, DC=0, C=0, Z=1");
        lines.add("0011 020C           00052           subwf wert1,w       ;W=D9h, wert1=D9h, wert2=52h, DC=0, C=0, Z=0");
        lines.add("0012 020D           00053           subwf wert2,w       ;W=79h, wert1=D9h, wert2=52h, DC=0, C=0, Z=0");
        lines.add("0013 028D           00054           subwf wert2         ;W=79h, wert1=D9h, wert2=D9h, DC=0, C=0, Z=0");
        lines.add("0014 028D           00055           subwf wert2         ;W=79h, wert1=D9h, wert2=60h, DC=1, C=1, Z=0");
        lines.add("0015 2815           00059           goto ende           ;Endlosschleife, verhindert Nirwana");

        Worker peon = new Worker();
        peon.feed(Parser.parseMultible(Parser.cut(lines)));

        // MOVLW (0)
        peon.next();
        assertEquals(0x11, peon.getWorking());

        // MOVWF (1)
        peon.next();
        assertEquals(0x11, peon.getMemory().content()[0][0xC]);

        // MOVLW (2)
        peon.next();
        assertEquals(0x14, peon.getWorking());

        // ADDWF (3)
        peon.next();
        assertEquals(0x25, peon.getWorking());
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());

        // ADDWF (4)
        peon.next();
        assertEquals(0x36, peon.getMemory().content()[0][0xC]);
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());

        // ANDWF (5)
        peon.next();
        assertEquals(0x24, peon.getWorking());
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());

        // MOVWF (6)
        peon.next();
        assertEquals(0x24, peon.getWorking());
        assertEquals(0x24, peon.getMemory().content()[0][0xD]);

        // CLRF (7)
        peon.next();
        assertEquals(0, peon.getMemory().content()[0][0xC]);
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(1, peon.getMemory().getZero());

        // COMF (8)
        peon.next();
        assertEquals(0xDB, peon.getWorking());
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());

        // DECF (9)
        peon.next();
        assertEquals(0xFF, peon.getWorking());
        assertEquals(0x0, peon.getMemory().content()[0][0xC]);
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());

        // INCF (10)
        peon.next();
        assertEquals(0xFF, peon.getWorking());
        assertEquals(0x25, peon.getMemory().content()[0][0xD]);
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());

        // MOVF (11)
        peon.next();
        assertEquals(0xFF, peon.getWorking());
        assertEquals(0x0, peon.getMemory().content()[0][0xC]);
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(1, peon.getMemory().getZero());

        // IORWF (12)
        peon.next();
        assertEquals(0xFF, peon.getWorking());
        assertEquals(0xFF, peon.getMemory().content()[0][0xC]);
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());

        // SUBWF (13)
        peon.next();
        assertEquals(0x26, peon.getWorking());
        assertEquals(0xFF, peon.getMemory().content()[0][0xC]);
        assertEquals(0x25, peon.getMemory().content()[0][0xD]);
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());

        // SWAPF (14)
        peon.next();
        assertEquals(0x52, peon.getMemory().content()[0][0xD]);

        // XORWF (15)
        peon.next();
        assertEquals(0x26, peon.getWorking());
        assertEquals(0xD9, peon.getMemory().content()[0][0xC]);
        assertEquals(0x52, peon.getMemory().content()[0][0xD]);
        assertEquals(0, peon.getMemory().getZero());

        // CLRW (16)
        peon.next();
        assertEquals(0x00, peon.getWorking());
        assertEquals(0xD9, peon.getMemory().content()[0][0xC]);
        assertEquals(0x52, peon.getMemory().content()[0][0xD]);
        assertEquals(1, peon.getMemory().getZero());

        // SUBWF (17)
        peon.next();
        assertEquals(0xD9, peon.getWorking());
        assertEquals(0xD9, peon.getMemory().content()[0][0xC]);
        assertEquals(0x52, peon.getMemory().content()[0][0xD]);
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());

        // SUBWF (18)
        peon.next();
        assertEquals(0x79, peon.getWorking());
        assertEquals(0xD9, peon.getMemory().content()[0][0xC]);
        assertEquals(0x52, peon.getMemory().content()[0][0xD]);
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());

        // SUBWF (19)
        peon.next();
        assertEquals(0x79, peon.getWorking());
        assertEquals(0xD9, peon.getMemory().content()[0][0xC]);
        assertEquals(0xD9, peon.getMemory().content()[0][0xD]);
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());

        // SUBWF (20)
        peon.next();
        assertEquals(0x79, peon.getWorking());
        assertEquals(0xD9, peon.getMemory().content()[0][0xC]);
        assertEquals(0x60, peon.getMemory().content()[0][0xD]);
        assertEquals(1, peon.getMemory().getCarry());
        assertEquals(1, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());

        // GOTO (21)
        peon.next();
        assertEquals(0x15, peon.getCurrent());
    }


    @Test
    void LST4Test() {
        ArrayList<String> lines = new ArrayList();
        lines.add("0000 3011           00023           movlw 11h           ;in W steht nun 11h, DC=?, C=?, Z=?");
        lines.add("0001 008C           00024           movwf wert1         ;diesen Wert abspeichern, DC=?, C=?, Z=?");
        lines.add("0002 3E11           00025           addlw 11h           ;löscht u.a. das Carry-Flag,  DC=0, C=0, Z=0");
        lines.add("0003 0D8C           00026           rlf wert1           ;W=22h, wert1=22h, wert2=?? , DC=0, C=0, Z=0");
        lines.add("0004 0D8C           00027           rlf wert1           ;W=22h, wert1=44h, wert2=?? , DC=0, C=0, Z=0");
        lines.add("0005 0D8C           00028           rlf wert1           ;W=22h, wert1=88h, wert2=?? , DC=0, C=0, Z=0");
        lines.add("0006 0D0C           00029           rlf wert1,w         ;W=10h, wert1=88h, wert2=?? , DC=0, C=1, Z=0");
        lines.add("0007 0D8C           00030           rlf wert1           ;W=10h, wert1=11h, wert2=?? , DC=0, C=1, Z=0");
        lines.add("0008 0D0C           00031           rlf wert1,w         ;W=23h, wert1=11h, wert2=?? , DC=0, C=0, Z=0");
        lines.add("0009 0C8C           00032           rrf wert1           ;W=23h, wert1=08h, wert2=?? , DC=0, C=1, Z=0");
        lines.add("000A 008D           00033           movwf wert2         ;W=23h, wert1=08h, wert2=23h, DC=0, C=1, Z=0");
        lines.add("000B 0C8D           00034           rrf wert2           ;W=23h, wert1=08h, wert2=91h, DC=0, C=1, Z=0");
        lines.add("000C 0C0D           00035           rrf wert2,w         ;W=C8h, wert1=08h, wert2=91h, DC=0, C=1, Z=0");
        lines.add("000D 3009           00037           movlw 9             ;W=09h, wert1=08h, wert2=91h, DC=0, C=1, Z=0");
        lines.add("000E 008C           00038           movwf wert1         ;W=09h, wert1=09h, wert2=91h, DC=0, C=1, Z=0");
        lines.add("000F 0100           00039           clrw                ;W=00h, wert1=09h, wert2=91h, DC=0, C=1, Z=1");
        lines.add("0010 3E01           00041           addlw 1             ;W=01h, DC=0, C=0, Z=0");
        lines.add("0011 078D           00050           addwf wert2         ;wert2=92h, DC=0, C=0, Z=0");
        lines.add("0012 0B8C           00059           decfsz wert1        ;wert1=08h, wert1=07h, ... DC,C und Z bleiben unverändert");
        lines.add("0013 2810           00060           goto loop1");
        lines.add("0014 30F0           00062           movlw 0f0h          ;Wert wird bis 00h (über FFh) hochgezählt");
        lines.add("0015 008C           00063           movwf wert1");
        lines.add("0016 018D           00064           clrf wert2");
        lines.add("0017 0100           00065           clrw");
        lines.add("0018 070C           00067           addwf wert1,w       ;W=F0h, wert1=F0h, wert2=00h, DC=0, C=0, Z=0");
        lines.add("0019 0A8D           00084           incf wert2          ;W=F0h, wert1=F0h, wert2=01h, DC=0, C=0, Z=0");
        lines.add("001A 0F8C           00101           incfsz wert1        ;wert1=F1h, F2h, F3h .. FFh, 00h, wert2 und Flags bleiben unverändert");
        lines.add("001B 2818           00102           goto loop2");
        lines.add("001C 281C           00105           goto ende           ;Endlosschleife, verhindert Nirwana");

        Worker peon = new Worker();
        peon.feed(Parser.parseMultible(Parser.cut(lines)));

        // MOVLW (0)
        peon.next();
        assertEquals(0x11, peon.getWorking());

        // MOVWF (1)
        peon.next();
        assertEquals(0x11, peon.getMemory().content()[0][0xC]);

        // ADDLW (2)
        peon.next();
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());

        // RLF (3)
        peon.next();
        assertEquals(0x22, peon.getWorking());
        assertEquals(0x22, peon.getMemory().content()[0][0xC]);
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());

        // RLF (4)
        peon.next();
        assertEquals(0x22, peon.getWorking());
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());

        // RLF (5)
        peon.next();
        assertEquals(0x22, peon.getWorking());
        assertEquals(0x88, peon.getMemory().content()[0][0xC]);
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());

        // RLF (6)
        peon.next();
        assertEquals(0x10, peon.getWorking());
        assertEquals(0x88, peon.getMemory().content()[0][0xC]);
        assertEquals(1, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());

        // RLF (7)
        peon.next();
        assertEquals(0x10, peon.getWorking());
        assertEquals(0x11, peon.getMemory().content()[0][0xC]);
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(1, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getZero());

        // RLF (8)
        peon.next();
        assertEquals(0x23, peon.getWorking());
        assertEquals(0x11, peon.getMemory().content()[0][0xC]);
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getZero());

        // RRF (9)
        peon.next();
        assertEquals(0x23, peon.getWorking());
        assertEquals(0x08, peon.getMemory().content()[0][0xC]);
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(1, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getZero());

        // MOVWF (10)
        peon.next();
        assertEquals(0x23, peon.getWorking());
        assertEquals(0x08, peon.getMemory().content()[0][0xC]);
        assertEquals(0x23, peon.getMemory().content()[0][0xD]);
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(1, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getZero());

        // RRF (11)
        peon.next();
        assertEquals(0x23, peon.getWorking());
        assertEquals(0x08, peon.getMemory().content()[0][0xC]);
        assertEquals(0x91, peon.getMemory().content()[0][0xD]);
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(1, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getZero());

        // RRF (12)
        peon.next();
        assertEquals(0xC8, peon.getWorking());
        assertEquals(0x08, peon.getMemory().content()[0][0xC]);
        assertEquals(0x91, peon.getMemory().content()[0][0xD]);
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(1, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getZero());

        // MOVLW (13)
        peon.next();
        assertEquals(0x09, peon.getWorking());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(1, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getZero());

        // MOVWF (14)
        peon.next();
        assertEquals(0x09, peon.getWorking());
        assertEquals(0x09, peon.getMemory().content()[0][0xC]);
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(1, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getZero());

        // CLRW (15)
        peon.next();
        assertEquals(0x0, peon.getWorking());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(1, peon.getMemory().getCarry());
        assertEquals(1, peon.getMemory().getZero());

        // ADDLW (16)
        peon.next();
        assertEquals(0x1, peon.getWorking());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getZero());

        // ADDWF (17)
        peon.next();
        assertEquals(0x92, peon.getMemory().content()[0][0xD]);
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getZero());

        // DECFSZ (18)
        peon.next();
        assertEquals(0x08, peon.getMemory().content()[0][0xC]);
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getZero());

        // Loop bis Wert1 auf 0
        for (int i = 0; i < 33; i++) {
            peon.next();
        }

        // DECFSZ (18)
        assertEquals(0x0, peon.getMemory().content()[0][0xC]);

        // MOVLW (19)
        peon.next();
        assertEquals(0xF0, peon.getWorking());

        // MOVWF (20)
        peon.next();
        assertEquals(0xF0, peon.getMemory().content()[0][0xC]);

        // CLRF (21)
        peon.next();
        assertEquals(0x0, peon.getMemory().content()[0][0xD]);

        // CLRW (22)
        peon.next();
        assertEquals(0x0, peon.getWorking());

        // ADDWF (23)
        peon.next();
        assertEquals(0xF0, peon.getWorking());
        assertEquals(0xF0, peon.getMemory().content()[0][0xC]);
        assertEquals(0x0, peon.getMemory().content()[0][0xD]);
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getZero());

        // INCF (24)
        peon.next();
        assertEquals(0xF0, peon.getWorking());
        assertEquals(0xF0, peon.getMemory().content()[0][0xC]);
        assertEquals(0x01, peon.getMemory().content()[0][0xD]);
        assertEquals(0, peon.getMemory().getZero());

        // INCFSZ (25)
        peon.next();
        assertEquals(0xF0, peon.getWorking());
        assertEquals(0xF1, peon.getMemory().content()[0][0xC]);

        for (int i = 0; i < 59; i++) {
            peon.next();
        }

        // INCFSZ (25)
        peon.next();
        assertEquals(0x00, peon.getMemory().content()[0][0xC]);
    }

    @Test
    void LST5Test() {
        ArrayList<String> lines = new ArrayList<>();

        lines.add("0000 3011           00027           movlw 11h           ;in W steht nun 11h, DC=?, C=?, Z=?");
        lines.add("0001 008C           00028           movwf wert1         ;diesen Wert abspeichern, DC=?, C=?, Z=?");
        lines.add("0002 018D           00029           clrf wert2          ;W=11h, wert1=11h, wert2=00h, DC=?, C=?, Z=1");
        lines.add("0003 178C           00030           bsf wert1,7         ;W=11h, wert1=91h, wert2=00h, DC=?, C=?, Z=1");
        lines.add("0004 158C           00031           bsf wert1,3         ;W=11h, wert1=99h, wert2=00h, DC=?, C=?, Z=1");
        lines.add("0005 120C           00032           bcf wert1,4         ;W=11h, wert1=89h, wert2=00h, DC=?, C=?, Z=1");
        lines.add("0006 100C           00033           bcf wert1,0         ;W=11h, wert1=88h, wert2=00h, DC=?, C=?, Z=1");
        lines.add("0007 180C           00035           btfsc wert1,0");
        lines.add("0008 0A8D           00036           incf wert2");
        lines.add("0009 0A8D           00037           incf wert2");
        lines.add("000A 198C           00038           btfsc wert1,3");
        lines.add("000B 0A8D           00039           incf wert2");
        lines.add("000C 0A8D           00040           incf wert2");
        lines.add("000D 1D0C           00041           btfss wert1,2");
        lines.add("000E 0A8D           00042           incf wert2");
        lines.add("000F 0A8D           00043           incf wert2");
        lines.add("0010 1F8C           00044           btfss wert1,7");
        lines.add("0011 0A8D           00045           incf wert2");
        lines.add("0012 038D           00046           decf wert2          ;in wert2 muss 04h stehen");
        lines.add("0013 2813           00049           goto ende           ;Endlosschleife, verhindert Nirwana");

        Worker peon = new Worker();
        peon.feed(Parser.parseMultible(Parser.cut(lines)));

        peon.next();
        peon.next();
        assertEquals(0x11, peon.getMemory().content()[0][0xC]);
        peon.next();
        assertEquals(0x00, peon.getMemory().content()[0][0xD]);
        assertEquals(1, peon.getMemory().getZero());

        // BSF 3
        peon.next();
        assertEquals(0x91, peon.getMemory().content()[0][0xC]);

        // BSF 4
        peon.next();
        assertEquals(0x99, peon.getMemory().content()[0][0xC]);

        // BCF 5
        peon.next();
        assertEquals(0x89, peon.getMemory().content()[0][0xC]);

        // BCF 6
        peon.next();
        assertEquals(0x88, peon.getMemory().content()[0][0xC]);
        // TODO: LST5 fertig machen

    }

    @Test
    void LST6Test() {
        ArrayList<String> lines = new ArrayList<>();
        lines.add("0000 3020           00047           movlw 20h           ;in W steht nun 20h, DC=?, C=?, Z=?");
        lines.add("0001 008C           00048           movwf wert1         ;diesen Wert abspeichern, DC=?, C=?, Z=?");
        lines.add("0002 3010           00049           movlw 10h           ;W = 10h, DC=?, C=?, Z=?");
        lines.add("0003 0084           00050           movwf fsr           ;W=10h, FSR=10h, wert1=20h, wert2=?? , DC=?, C=?, Z=?");
        lines.add("0004 008D           00051           movwf wert2         ;W=10h, FSR=10h, wert1=20h, wert2=10h, DC=?, C=?, Z=?");
        lines.add("0005 080C           00052           movf wert1,w        ;W=20h");
        lines.add("0006 0080           00055           movwf indirect      ;W=20h, FSR=10h, F10=20h");
        lines.add("0007 3E01           00056           addlw 1             ;W=20h, 21h, 22h, etc");
        lines.add("0008 0A84           00057           incf fsr            ;FSR=11h, 12h, etc");
        lines.add("0009 0B8D           00058           decfsz wert2");
        lines.add("000A 2806           00059           goto loop1");
        lines.add("000B 301F           00061           movlw 1fh           ;FSR-Zeiger wieder auf Anfang stellen");
        lines.add("000C 0084           00062           movwf fsr");
        lines.add("000D 30F0           00063           movlw 0f0h");
        lines.add("000E 008D           00064           movwf wert2");
        lines.add("000F 0100           00065           clrw");
        lines.add("0010 0700           00067           addwf indirect,w");
        lines.add("0011 0384           00068           decf fsr");
        lines.add("0012 0F8D           00069           incfsz wert2");
        lines.add("0013 2810           00070           goto loop2");
        lines.add("0014 008D           00072           movwf wert2");
        lines.add("0015 0A84           00073           incf fsr");
        lines.add("0016 0C80           00074           rrf indirect        ;F10=10h");
        lines.add("0017 0A80           00075           incf indirect       ;F10=11h");
        lines.add("0018 0C80           00076           rrf indirect        ;F10=08h, C=1");
        lines.add("0019 1780           00077           bsf indirect,7      ;F10=88h");
        lines.add("001A 1003           00078           bcf status,0        ;C=0");
        lines.add("001B 0D80           00079           rlf indirect        ;F10=10h, C=1");
        lines.add("001C 0A84           00080           incf fsr            ;fsr=11h");
        lines.add("001D 0D80           00081           rlf indirect        ;F11=43h, C=0");
        lines.add("001E 0E80           00082           swapf indirect      ;F11=34h");
        lines.add("001F 0680           00083           xorwf indirect      ;F11=4Ch");
        lines.add("0020 1A80           00084           btfsc indirect,5");
        lines.add("0021 2800           00085           goto loop");
        lines.add("0022 1D00           00086           btfss indirect,2");
        lines.add("0023 2800           00087           goto loop");
        lines.add("0024 1980           00088           btfsc indirect,3");
        lines.add("0025 2827           00089           goto ende");
        lines.add("0026 2800           00090           goto loop");
        lines.add("0027 2827           00093           goto ende           ;Endlosschleife, verhindert Nirwana");

        Worker peon = new Worker();
        peon.feed(Parser.parseMultible(Parser.cut(lines)));

        // movlw 0
        peon.next();
        assertEquals(0x20, peon.getWorking());

        // movwf 1
        peon.next();
        assertEquals(0x20, peon.getMemory().content()[0][0xC]);

        // movlw 2
        peon.next();
        assertEquals(0x10, peon.getWorking());

        // movwf 3
        peon.next();
        assertEquals(0x10, peon.getWorking());
        assertEquals(0x10, peon.getMemory().content()[0][0x4]);
        assertEquals(0x20, peon.getMemory().content()[0][0xC]);

        // movwf 4
        peon.next();
        assertEquals(0x10, peon.getWorking());
        assertEquals(0x10, peon.getMemory().content()[0][0x4]);
        assertEquals(0x20, peon.getMemory().content()[0][0xC]);
        assertEquals(0x10, peon.getMemory().content()[0][0xD]);

        // movwf 5
        peon.next();
        assertEquals(0x20, peon.getWorking());

        // movwf 6
        peon.next();
        assertEquals(0x20, peon.getWorking());
        assertEquals(0x10, peon.getMemory().content()[0][0x4]);
        assertEquals(0x20, peon.getMemory().content()[0][0x10]);

        // addlw 7
        peon.next();
        // Loop Kondition

        //return;

        // incf 8
        peon.next();

        // decfsz 9
        peon.next();

        // goto A
        peon.next();

        // movlw B
        peon.next();
        assertEquals(0x1F, peon.getWorking());

        // movwf C
        peon.next();
        assertEquals(0x1F, peon.getMemory().content()[0][0x4]);

        // movlw D
        peon.next();
        assertEquals(0xF0, peon.getWorking());

        // movwf E
        peon.next();

        // movlw 0
        peon.next();
        assertEquals(0x22, peon.getWorking());
        assertEquals(0x22, peon.getMemory().content()[0][0xC]);
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());


        // movlw 0
        peon.next();
        assertEquals(0x22, peon.getWorking());
        assertEquals(0x22, peon.getMemory().content()[0][0xC]);
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());


        // movlw 0
        peon.next();
        assertEquals(0x22, peon.getWorking());
        assertEquals(0x22, peon.getMemory().content()[0][0xC]);
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());


        // movlw 0
        peon.next();
        assertEquals(0x22, peon.getWorking());
        assertEquals(0x22, peon.getMemory().content()[0][0xC]);
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());


        // movlw 0
        peon.next();
        assertEquals(0x22, peon.getWorking());
        assertEquals(0x22, peon.getMemory().content()[0][0xC]);
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());


        // movlw 0
        peon.next();
        assertEquals(0x22, peon.getWorking());
        assertEquals(0x22, peon.getMemory().content()[0][0xC]);
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());


        // movlw 0
        peon.next();
        assertEquals(0x22, peon.getWorking());
        assertEquals(0x22, peon.getMemory().content()[0][0xC]);
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());


        // movlw 0
        peon.next();
        assertEquals(0x22, peon.getWorking());
        assertEquals(0x22, peon.getMemory().content()[0][0xC]);
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());


        // movlw 0
        peon.next();
        assertEquals(0x22, peon.getWorking());
        assertEquals(0x22, peon.getMemory().content()[0][0xC]);
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());


        // movlw 0
        peon.next();
        assertEquals(0x22, peon.getWorking());
        assertEquals(0x22, peon.getMemory().content()[0][0xC]);
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());


        // movlw 0
        peon.next();
        assertEquals(0x22, peon.getWorking());
        assertEquals(0x22, peon.getMemory().content()[0][0xC]);
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());


        // movlw 0
        peon.next();
        assertEquals(0x22, peon.getWorking());
        assertEquals(0x22, peon.getMemory().content()[0][0xC]);
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());


        // movlw 0
        peon.next();
        assertEquals(0x22, peon.getWorking());
        assertEquals(0x22, peon.getMemory().content()[0][0xC]);
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());


        // movlw 0
        peon.next();
        assertEquals(0x22, peon.getWorking());
        assertEquals(0x22, peon.getMemory().content()[0][0xC]);
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());


        // movlw 0
        peon.next();
        assertEquals(0x22, peon.getWorking());
        assertEquals(0x22, peon.getMemory().content()[0][0xC]);
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());


        // movlw 0
        peon.next();
        assertEquals(0x22, peon.getWorking());
        assertEquals(0x22, peon.getMemory().content()[0][0xC]);
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());


        // movlw 0
        peon.next();
        assertEquals(0x22, peon.getWorking());
        assertEquals(0x22, peon.getMemory().content()[0][0xC]);
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());


        // movlw 0
        peon.next();
        assertEquals(0x22, peon.getWorking());
        assertEquals(0x22, peon.getMemory().content()[0][0xC]);
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());
        /*
         */
    }

    @Test
    void LST7Test() {
        ArrayList<String> lines = new ArrayList();
        lines.add("0000 3001           00025           movlw 00000001B     ;Option-Register entsp. initialisieren");
        lines.add("0001 1683           00026           bsf status,5        ;Bank umschalten");
        lines.add("0002 0081           00027           movwf 1             ;Option-Register");
        lines.add("0003 1283           00028           bcf status,5");
        lines.add("0004 3001           00029           movlw 1             ;Timer 1 auf 1");
        lines.add("0005 0081           00030           movwf 1");
        lines.add("0006 0190           00031           clrf 10h            ;zähler");
        lines.add("0007 0000           00033           nop");
        lines.add("0008 0000           00034           nop");
        lines.add("0009 0000           00035           nop");
        lines.add("000A 0A90           00036           incf 10h");
        lines.add("000B 0801           00037           movf 1,w            ;lese Timerwert aus");
        lines.add("000C 1D03           00038           btfss status,2      ;wenn Timer = 0, dann fertig");
        lines.add("000D 2807           00039           goto loop1");
        lines.add("000E 3003           00044           movlw 00000011B     ;Option-Register entsp. initialisieren");
        lines.add("000F 1683           00045           bsf status,5        ;Bank umschalten");
        lines.add("0010 0081           00046           movwf 1             ;Option-Register");
        lines.add("0011 1283           00047           bcf status,5");
        lines.add("0012 3001           00048           movlw 1             ;Timer 1 auf 1");
        lines.add("0013 0081           00049           movwf 1");
        lines.add("0014 0190           00050           clrf 10h            ;zähler");
        lines.add("0015 0A90           00052           incf 10h");
        lines.add("0016 0801           00053           movf 1,w            ;lese Timerwert aus");
        lines.add("0017 1D03           00054           btfss status,2      ;wenn Timer = 0, dann fertig");
        lines.add("0018 2815           00055           goto loop2");
        lines.add("0019 3038           00060           movlw 00111000B     ;Option-Register initialisieren");
        lines.add("001A 1683           00061           bsf status,5");
        lines.add("001B 0081           00062           movwf 1             ;Wert ins Option-Register");
        lines.add("001C 1283           00063           bcf status,5");
        lines.add("001D 0181           00064           clrf 1              ;Timer löschen");
        lines.add("001E 1E01           00066           btfss 1,4           ;bis im Timer0 der Wert 16 erreicht wird");
        lines.add("001F 281E           00067           goto loop3");
        lines.add("0020 3031           00070           movlw 00110001B     ;Option-Register initialisieren");
        lines.add("0021 1683           00071           bsf status,5");
        lines.add("0022 0081           00072           movwf 1             ;Wert ins Option-Register");
        lines.add("0023 1283           00073           bcf status,5");
        lines.add("0024 0181           00074           clrf 1              ;Timer löschen");
        lines.add("0025 1D81           00076           btfss 1,3           ;bis im Timer0 der Wert 8 erreicht wird");
        lines.add("0026 2825           00077           goto loop4");
        lines.add("0027 2827           00082           goto ende           ;Endlosschleife, verhindert Nirwana");

        Worker peon = new Worker();
        peon.feed(Parser.parseMultible(Parser.cut(lines)));

        // MOVLW 0
        peon.next();
        assertEquals(0x1, peon.getWorking());

        // BSF 1
        peon.next();
        assertEquals(32, peon.getMemory().content()[0][3] & 32);

        // MOVWF 2
        peon.next();
        assertEquals(1, peon.getMemory().content()[1][1] & 1);

        // BCF 3
        peon.next();
        assertEquals(0, peon.getMemory().content()[0][3] & 32);

        // MOVLW 4
        peon.next();
        assertEquals(1, peon.getWorking());

        // MOVWF 5
        peon.next();
        assertEquals(1, peon.getMemory().content()[1][1] & 1);

        // CLRF 6
        peon.next();
        assertEquals(0, peon.getMemory().content()[1][10]);

        peon.next();
        peon.next();
        peon.next();

        // INCF A
        peon.next();
        assertEquals(1, peon.getMemory().content()[1][10]);
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
