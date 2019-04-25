package Model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Parser {

	public static ArrayList<String> load(String path) {

		BufferedReader re;
		ArrayList<String> sentences = new ArrayList<String>();

		try {
			re = new BufferedReader(new FileReader(path));

			String next;

			while ((next = re.readLine()) != null) {
				sentences.add(next);
			}
		} catch (FileNotFoundException e) {
			return new ArrayList<>();
		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}

		return sentences;
	}

	public static ArrayList<String> cut(ArrayList<String> lines) throws NullPointerException {

		ArrayList<String> temp = new ArrayList<String>();
		for (int i = 0; i < lines.size(); i++) {
			if (Character.isDigit(lines.get(i).charAt(0))) {
				temp.add(lines.get(i).substring(5, 9));
			}
		}

		return temp;
	}

	public static String cut(String line) throws NullPointerException {

		ArrayList<String> wrapper = new ArrayList<>();
		wrapper.add(line);

		wrapper = Parser.cut(wrapper);

		if (wrapper.size() == 0) {
			return null;
		}

		return wrapper.get(0);

	}

	public static ArrayList<Command> parseMultible(ArrayList<String> lines) throws NullPointerException {

		return new ArrayList<Command>();
	}

	public static Command parse(String line) throws NullPointerException {

		Instruction instruction = null;

		int code = Integer.valueOf(line, 16).intValue();
		int tempX1 = (code & 0b1100000000) >>> 8;
		int tempX2 = (code & 0b11110000) >>> 4;
		int value[] = null;

		int fMask = 0b1111111;
		int bMask = 0b1110000000;
		int kMask = 0b11111111;
		int destBit = 0b10000000;

		int addwf = 0b00011100000000;
		int andwf = 0b00010100000000;
		int clrf = 0b00000110000000;
		int clrw = 0b00000100000000;
		int comf = 0b00100100000000;
		int decf = 0b00001100000000;
		int decfsz = 0b00101100000000;
		int incf = 0b00101000000000;
		int incfsz = 0b00111100000000;
		int iorwf = 0b00010000000000;
		int movf = 0b00100000000000;
		int movwf = 0b00000010000000;
		int rlf = 0b00110100000000;
		int rrf = 0b00110000000000;
		int subwf = 0b00001000000000;
		int swapf = 0b00111000000000;
		int xorwf = 0b00011000000000;

		int bcf = 0b01000000000000;
		int bsf = 0b01010000000000;
		int btfsc = 0b01100000000000;
		int btfss = 0b01110000000000;

		int addlw = 0b11111000000000;
		int andlw = 0b11100100000000;
		int call = 0b10000000000000;
		int clrwdt = 0b00000001100100;
		int goTo = 0b10100000000000;
		int iorlw = 0b11100000000000;
		int movlw = 0b11000000000000;
		int retfie = 0b00000000001001;
		int retlw = 0b11010000000000;
		int reTurn = 0b00000000001000;
		int sleep = 0b00000001100011;
		int sublw = 0b11110000000000;
		int xorlw = 0b11101000000000;

		switch (tempX1) {

		case 0:
			if (tempX2 > 0) {
				switch (tempX2) {
				case 0b1:
					if ((code >>> 7) == 3) {
						instruction = Instruction.CLRF;
						value = new int[] { (fMask & code) };
					} else
						instruction = Instruction.CLRW;
					break;
				case 0b0010:
					instruction = Instruction.SUBWF;
					break;
				case 0b0011:
					instruction = Instruction.DECF;
					break;
				case 0b0100:
					instruction = Instruction.IORWF;
					break;
				case 0b0101:
					instruction = Instruction.ANDWF;
					break;
				case 0b0110:
					instruction = Instruction.XORWF;
					break;
				case 0b0111:
					instruction = Instruction.ADDWF;
					break;
				case 0b1000:
					instruction = Instruction.MOVF;
					break;
				case 0b1001:
					instruction = Instruction.COMF;
					break;
				case 0b1010:
					instruction = Instruction.INCF;
					break;
				case 0b1011:
					instruction = Instruction.DECFSZ;
					break;
				case 0b1100:
					instruction = Instruction.RRF;
					break;
				case 0b1101:
					instruction = Instruction.RLF;
					break;
				case 0b1110:
					instruction = Instruction.SWAPF;
					break;
				case 0b1111:
					instruction = Instruction.INCFSZ;
					break;
				}
				value = new int[] { (fMask & code), ((destBit & code) >>> 7) };

			} else {
				switch (code) {
				case 0x0008:
					instruction = Instruction.RETURN;
					break;
				case 0x0009:
					instruction = Instruction.RETFIE;
					break;
				case 0x0063:
					instruction = Instruction.SLEEP;
					break;
				case 0x0064:
					instruction = Instruction.CLRWDT;
					break;
				default:
					if ((code >>> 7) == 1) {
						instruction = Instruction.MOVWF;
						value = new int[] { (fMask & code) };
					} else
						instruction = Instruction.NOP;
					break;
				}
			}
			break;
		case 1:
			switch (tempX2>>>2) {
			case 0b0100:
				instruction = Instruction.BCF;
				break;
			case 0b0101:
				instruction = Instruction.BSF;
				break;
			case 0b0110:
				instruction = Instruction.BTFSC;
				break;
			case 0b0111:
				instruction = Instruction.BTFSS;
				break;
			}
			value = new int[] { (fMask & code), ((bMask & code) >>> 7) };
			break;
		case 2:
			if ((code >>> 11) == 0x100) {
				instruction = Instruction.CALL;
			} else {
				instruction = Instruction.GOTO;
			}
			value = new int[] { (code & 0b11111111111) };
			break;
		case 3:
			switch (tempX2) {

			case 0b1000:
				instruction = Instruction.IORLW;
				break;
			case 0b1010:
				instruction = Instruction.XORLW;
				break;
			case 0b1001:
				instruction = Instruction.ANDLW;
				break;
			default:
				int tempCode = tempX2 >>> 1;
				if (tempCode == 0b111) {
					instruction = Instruction.ADDLW;
				} else if (tempCode == 0b110) {
					instruction = Instruction.SUBLW;
				} else {
					tempCode = tempCode >>> 1;
					if (tempCode == 1) {
						instruction = Instruction.RETLW;
					} else if (tempCode == 0)
						instruction = Instruction.MOVLW;
				}
			} 
			value = new int[] { (code & kMask) };
			break;
		}

		Command c = new Command(instruction, value);

		return c;
	}
}
