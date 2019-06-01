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
        assertEquals(0x26, peon.getWorking());
        assertEquals(0xFF, peon.getMemory().content()[0][0xC]);
        assertEquals(0x52, peon.getMemory().content()[0][0xD]);
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());


        // XORWF (15)
        peon.next();
        assertEquals(0x26, peon.getWorking());
        assertEquals(0xD9, peon.getMemory().content()[0][0xC]);
        assertEquals(0x52, peon.getMemory().content()[0][0xD]);
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getZero());


        // CLRW (16)
        peon.next();
        assertEquals(0x00, peon.getWorking());
        assertEquals(0xD9, peon.getMemory().content()[0][0xC]);
        assertEquals(0x52, peon.getMemory().content()[0][0xD]);
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getDitgitCarry());
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
        assertEquals(15, peon.getCurrent());
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
        assertEquals(0x01, peon.getWorking());
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
        for (int i = 0; i < 89; i++) {
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
        assertEquals(0x0, peon.getMemory().content()[0][0xC]);
        assertEquals(0x0, peon.getMemory().content()[0][0xD]);
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(0, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getZero());

        // INCF (24)
        peon.next();
        assertEquals(0xF0, peon.getWorking());
        assertEquals(0x0, peon.getMemory().content()[0][0xC]);
        assertEquals(0x01, peon.getMemory().content()[0][0xD]);
        assertEquals(0, peon.getMemory().getDitgitCarry());
        assertEquals(1, peon.getMemory().getCarry());
        assertEquals(0, peon.getMemory().getZero());

        // INCFSZ (25)
        peon.next();
        assertEquals(0x0, peon.getWorking());
        assertEquals(0xF1, peon.getMemory().content()[0][0xC]);

        for (int i = 0; i < 35; i++) {
            peon.next();
        }

        // INCFSZ (25)
        peon.next();
        assertEquals(0x00, peon.getMemory().content()[0][0xC]);
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
