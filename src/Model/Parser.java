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

	    ArrayList<Command> result = new ArrayList<>();

		for (String line : lines) {

			result.add(parse(line));

		}

		return result;
	}

	public static int getCycles(Instruction inst) {
		switch (inst) {
			case CALL:
			case GOTO:
			case RETFIE:
			case RETLW:
			case RETURN:
				return 2;
		}

	    return 1;
	}

	public static Command parse(String line) throws NullPointerException {

		Instruction instruction = null;

		int code = Integer.valueOf(line, 16).intValue();
		int value[] = null;

		int tempX1 = (code & 0b11000000000000) >>> 12;
		int tempX2 = (code & 0b00111100000000) >>> 8;

		int fMask = 0b1111111;
		int bMask = 0b1110000000;
		int kMask = 0b11111111;
		int destBit = 0b10000000;

		switch (tempX1) {
		case 0:
			if (tempX2 > 0) {
				switch (tempX2) {
				case 0b0001:
					if ((code >>> 7) == 3) {
						instruction = Instruction.CLRF;
						value = new int[] { (fMask & code) };
					} else {
						instruction = Instruction.CLRW;
					}
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
				if (value == null) {
					value = new int[] { (fMask & code), ((destBit & code) >>> 7) };
				}
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
			switch (tempX2 >>> 2) {
			case 0b00:
				instruction = Instruction.BCF;
				break;
			case 0b001:
				instruction = Instruction.BSF;
				break;
			case 0b010:
				instruction = Instruction.BTFSC;
				break;
			case 0b011:
				instruction = Instruction.BTFSS;
				break;
			}
			value = new int[] { (fMask & code), ((bMask & code) >>> 7) };
			break;
		case 2:
			if ((code >>> 11) == 0b100) {
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

		return new Command(instruction, value,getCycles(instruction));
	}
}
