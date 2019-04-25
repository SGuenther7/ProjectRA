package tests;

import Model.Command;
import Model.Instruction;
import Model.Parser;
import Model.Worker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParserTests {

	@Test
	void convertTest() {
		// Convertiere Hex codes in Command mit korrektem Command und Wert(en)

		// Erstelle benoetigte variablen
		String raw = "0000 3011           00017           movlw 11h           ;in W steht nun 11h, Statusreg. unveraendert";

		// Convertiere von String zu Command
		Command result = Parser.parse(Parser.cut(raw));

		// Check ob resultat korrekt ist
		assertEquals(result, new Command(Instruction.MOVLW, new int[] { 0x11 }));

	}

	@Test
	void convertRandomTest() {
		// Convertiere Hex codes in Command mit korrektem Command und Wert(en)

		// Erstelle benoetigte variablen
		String raw = "0000 30";

		// Zufallszahl generieren
		int value = (int) (100 * Math.random());

		// Zahl in Text einfuegen
		if (value < 16) {
			raw += '0';
		}

		raw += Integer.toHexString(value);
		raw += "           00017           movlw XXh           ;in W steht nun 11h, Statusreg. unveraendert";

		// Convertiere von String zu Command
		Command result = Parser.parse(Parser.cut(raw));

		// Check ob resultat korrekt ist
		assertEquals(result, new Command(Instruction.MOVLW, new int[] { value }));
	}

	@Test
	void addWFTest() {
		String raw = "0003 070C           00037           addwf wert1,w       ;W = 25h, DC=0, C=0, Z=0";
		Command result = Parser.parse(Parser.cut(raw));
//		assertEquals(result, new Command(Instruction.ADDWF, new int[] { 12,0 }));
		assertEquals(result.getValue(),  new int[] { 12,0 });
		
	}

	@Test
	void andWFTest() {
		String raw = "0005 050C           00039           andwf wert1,w       ;W = 24h, wert1 = 36h, DC=0, C=0, Z=0";
		Command result = Parser.parse(Parser.cut(raw));
		assertEquals(result, new Command(Instruction.ANDWF, new int[] { 12,0 }));
	}

	@Test
	void clrFTest() {
		String raw = "0007 018C           00041           clrf wert1          ;W=24h, wert1=0, wert2=24h, DC=0, C=0, Z=1";
		Command result = Parser.parse(Parser.cut(raw));
		assertEquals(result, new Command(Instruction.CLRF, new int[] { 12 }));
	}

	@Test
	void clrWTest() {
		String raw = "0010 0100           00050           clrw                ;W=00h, wert1=D9h, wert2=52h, DC=0, C=0, Z=1";
		Command result = Parser.parse(Parser.cut(raw));
		assertEquals(result.getInstruction(), Instruction.CLRW);
	}

	@Test
	void comFTest() {
		String raw = "0008 090D           00042           comf wert2,w        ;W=DBh, wert1=0, wert2=24h, DC=0, C=0, Z=0";
		Command result = Parser.parse(Parser.cut(raw));
		assertEquals(result, new Command(Instruction.COMF, new int[] { 13,0 }));
	}

	@Test
	void decFTest() {
		String raw = "0012 038D           00046           decf wert2          ;in wert2 muss 04h stehen";
		Command result = Parser.parse(Parser.cut(raw));
		assertEquals(result, new Command(Instruction.DECF, new int[] { 13,1 }));
	}

	@Test
	void decFSZTest() {
		String raw = "0012 0B8C           00059           decfsz wert1        ;wert1=08h, wert1=07h, ... DC,C und Z bleiben unverändert";
		Command result = Parser.parse(Parser.cut(raw));
		assertEquals(result, new Command(Instruction.DECFSZ, new int[] { 12,0 }));
	}

	@Test
	void incFTest() {
		String raw = "0019 0A8D           00084           incf wert2          ;W=F0h, wert1=F0h, wert2=01h, DC=0, C=0, Z=0";
		Command result = Parser.parse(Parser.cut(raw));
		assertEquals(result, new Command(Instruction.INCF, new int[] { 13,1 }));
	}

	@Test
	void incFSZTest() {
		String raw = "001A 0F8C           00101           incfsz wert1        ;wert1=F1h, F2h, F3h .. FFh, 00h, wert2 und Flags bleiben unverändert";
		Command result = Parser.parse(Parser.cut(raw));
		assertEquals(result, new Command(Instruction.INCFSZ, new int[] { 12,1 }));
	}

	@Test
	void iOrWFTest() {
		String raw = "000C 048C           00046           iorwf wert1         ;W=FFh, wert1=FFh, wert2=25h, DC=0, C=0, Z=0";
		Command result = Parser.parse(Parser.cut(raw));
		assertEquals(result, new Command(Instruction.IORWF, new int[] { 12,1 }));
	}

	@Test
	void movFTest() {
		String raw = "0010 080F           00043           movf 0fh,w";
		Command result = Parser.parse(Parser.cut(raw));
		assertEquals(result, new Command(Instruction.MOVF, new int[] { 15,0 }));
	}

	@Test
	void movWFTest() {
		String raw = "0001 008F           00027           movwf 0fh";
		Command result = Parser.parse(Parser.cut(raw));
		assertEquals(result, new Command(Instruction.MOVWF, new int[] { 15,1 }));
	}

	@Test
	void nopTest() {
		String raw = "00F4 0000           00279           nop";
		Command result = Parser.parse(Parser.cut(raw));
		assertEquals(result.getInstruction(), Instruction.NOP);
	}

	@Test
	void RLFTest() {
		String raw = "000D 0D86           00040           rlf       rb                  ;ja, nochmal schieben";
		Command result = Parser.parse(Parser.cut(raw));
		assertEquals(result, new Command(Instruction.RLF, new int[] { 6,1 }));
	}

	@Test
	void RRFTest() {
		String raw = "0007 0C86           00032           rrf       rb";
		Command result = Parser.parse(Parser.cut(raw));
		assertEquals(result, new Command(Instruction.RRF, new int[] { 6,1 }));
	}

	@Test
	void subWFTest() {
		String raw = "0014 028D           00055           subwf wert2         ;W=79h, wert1=D9h, wert2=60h, DC=1, C=1, Z=0";
		Command result = Parser.parse(Parser.cut(raw));
		assertEquals(result, new Command(Instruction.SUBWF, new int[] { 13,1 }));
	}

	@Test
	void swapFTest() {
		String raw = "001E 0E80           00082           swapf indirect      ;F11=34h";
		Command result = Parser.parse(Parser.cut(raw));
		assertEquals(result, new Command(Instruction.SWAPF, new int[] { 0,1 }));
	}

	@Test
	void xOrWFTest() {
		String raw = "001F 0680           00083           xorwf indirect      ;F11=4Ch";
		Command result = Parser.parse(Parser.cut(raw));
		assertEquals(result, new Command(Instruction.XORWF, new int[] { 0,1 }));
	}

	@Test
	void BCFTest() {
		String raw = "0002 1283           00025           bcf       status,rp0          ;zurück auf Bank 0";
		Command result = Parser.parse(Parser.cut(raw));
		assertEquals(result, new Command(Instruction.BCF, new int[] { 3,5 }));
	}

	@Test
	void BSFTest() {
		String raw = "0000 1683           00023           bsf       status,rp0          ;auf Bank 1 umschalten";
		Command result = Parser.parse(Parser.cut(raw));
		assertEquals(result, new Command(Instruction.BSF, new int[] { 3,5 }));
	}

	@Test
	void BTFSCTest() {
		String raw = "000C 1803           00039           btfsc     status,0            ;ist Carry = 1";
		Command result = Parser.parse(Parser.cut(raw));
		assertEquals(result, new Command(Instruction.BTFSC, new int[] { 3,0 }));
	}

	@Test
	void BTFSSTest() {
		String raw = "0005 1C05           00029           btfss     ra,0                ;in welche Richtung?";
		Command result = Parser.parse(Parser.cut(raw));
		assertEquals(result, new Command(Instruction.BTFSS, new int[] { 5,0 }));
	}

	@Test
	void addLWTest() {
		String raw = "0005 3E25           00022           addlw 25h           ;W = 25h, C=0, DC=0, Z=0";
		Command result = Parser.parse(Parser.cut(raw));
		assertEquals(result, new Command(Instruction.ADDLW, new int[] { 0x25 }));
	}

	@Test
	void andLWTest() {
		String raw = "0001 3930           00018           andlw 30h           ;W = 10h, C=x, DC=x, Z=0";
		Command result = Parser.parse(Parser.cut(raw));
		assertEquals(result, new Command(Instruction.ANDLW, new int[] { 0x30 }));
	}

	@Test
	void callTest() {
		String raw = "0001 2006           00017           call up1            ;beim Call wird Rücksprungadresse auf Stack gelegt";
		Command result = Parser.parse(Parser.cut(raw));
		assertEquals(result, new Command(Instruction.CALL, new int[] { 6 }));
	}

	@Test
	void clrWDTTest() {
		String raw = "0007 0064           00050           clrwdt";
		Command result = Parser.parse(Parser.cut(raw));
		assertEquals(result.getInstruction(), Instruction.CLRWDT);
	}

	@Test
	void goToTest() {
		String raw = "0006 2806           00026           goto ende           ;Endlosschleife, verhindert Nirwana";
		Command result = Parser.parse(Parser.cut(raw));
		assertEquals(result, new Command(Instruction.GOTO, new int[] { 6 }));
	}

	@Test
	void iOrLWTest() {
		String raw = "0002 380D           00019           iorlw 0Dh           ;W = 1Dh, C=x, DC=x, Z=0";
		Command result = Parser.parse(Parser.cut(raw));
		assertEquals(result, new Command(Instruction.IORLW, new int[] { 0x0D }));
	}

	@Test
	void movLWTest() {
		String raw = "0014 3052           00047           movlw 'R'           ;schreibe ein R nach 22h";
		Command result = Parser.parse(Parser.cut(raw));
//		assertEquals(result, new Command(Instruction.MOVLW, new int[] { 0x52 }));
//		assertEquals(result.getInstruction(), Instruction.MOVLW);
		assertEquals(result.getValue(),new int[] { 82});
	}

	@Test
	void retFIeTest() {
		String raw = "001B 0009           00057           retfie              ;Ende der Inetrrupt-Service-Routine";
		Command result = Parser.parse(Parser.cut(raw));
		assertEquals(result.getInstruction(), Instruction.RETFIE);
	}

	@Test
	void retLWTest() {
		String raw = "0008 3477           00028  up2      retlw 77h";
		Command result = Parser.parse(Parser.cut(raw));
		assertEquals(result, new Command(Instruction.RETLW, new int[] { 0x77 }));
	}

	@Test
	void returnTest() {
		String raw = "0007 0008           00025           return";
		Command result = Parser.parse(Parser.cut(raw));
		assertEquals(result.getInstruction(), Instruction.RETURN);
	}

	@Test
	void sleepTest() {
		String raw = "0003 0063           00036           sleep                         ;warte bis Watchdogtimer anspricht";
		Command result = Parser.parse(Parser.cut(raw));
		assertEquals(result.getInstruction(), Instruction.SLEEP);
	}

	@Test
	void subLWTest() {
		String raw = "0003 3C3D           00020           sublw 3Dh           ;W = 20h, C=1, DC=1, Z=0";
		Command result = Parser.parse(Parser.cut(raw));
		assertEquals(result, new Command(Instruction.SUBLW, new int[] { 0x3D }));
	}

	@Test
	void xOrLWTest() {
		String raw = "0004 3A20           00021           xorlw 20h           ;W = 00h, C=1, DC=1, Z=1";
		Command result = Parser.parse(Parser.cut(raw));
		assertEquals(result, new Command(Instruction.XORLW, new int[] { 0x20 }));
	}
}
