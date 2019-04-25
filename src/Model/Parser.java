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

		int temp = Integer.valueOf(line, 16).intValue();
		int value[] = null;

		int fMask = 0b1111111;
		int bMask = 0b1110000000;
		int kMask = 0b11111111;
		int destBit = 0b10000000;

		int addwf  =  0b11100000000;
		int andwf  =  0b10100000000;
		int clrf   =    0b110000000;
		int clrw   =    0b100000000;
		int comf   = 0b100100000000;
		int decf   =   0b1100000000;
		int decfsz = 0b101100000000;
		int incf   = 0b101000000000;
		int incfsz = 0b111100000000;
		int iorwf  =  0b10000000000;
		int movf   = 0b100000000000;
		int movwf  =     0b10000000;
		int nop = 0;
		int rlf    = 0b110100000000;
		int rrf    = 0b110000000000;
		int subwf  =   0b1000000000;
		int swapf  = 0b111000000000;
		int xorwf  =  0b11000000000;

		int bcf    = 0b1000000000000;
		int bsf    = 0b1010000000000;
		int btfsc  = 0b1100000000000;
		int btfss  = 0b1110000000000;

		int addlw  = 0b11111000000000;
		int andlw  = 0b11100100000000;
		int call   = 0b10000000000000;
		int clrwdt = 0b1100100;
		int goTo   = 0b10100000000000;
		int iorlw  = 0b11100000000000;
		int movlw  = 0b11000000000000;
		int retfie = 0b1001;
		int retlw  = 0b11010000000000;
		int reTurn = 0b1000;
		int sleep = 0b1100011;
		int sublw  = 0b11110000000000;
		int xorlw  = 0b11101000000000;		

		
		// komplette Masken
		if (temp == clrwdt) {
			instruction = Instruction.CLRWDT;
		} else

		if (temp == retfie) {
			instruction = Instruction.RETFIE;
		} else

		if (temp == reTurn) {
			instruction = Instruction.RETURN;
		} else

		if (temp == sleep) {
			instruction = Instruction.SLEEP;
		} else
			
		// mit f und d
		if ((temp & addwf) == addwf) {
			instruction = Instruction.ADDWF;
			value = new int[2];
			value[0] = (fMask & temp);
			value[1] = 0b0;
			if ((destBit & temp) == destBit) {
				value[1] = 1;
			}
		} else

		if ((temp & andwf) == andwf) {
			instruction = Instruction.ANDWF;
			value = new int[2];
			value[0] = (fMask & temp);
			value[1] = 0b0;
			if ((destBit & temp) == destBit) {
				value[1] = 1;
			} 
		} else

		if ((temp & clrf) == clrf) {
			instruction = Instruction.CLRF;
			value = new int[1];
			value[0] = (fMask & temp);
		} else

		if ((temp & clrw) == clrw) {
			instruction = Instruction.CLRW;
		} else

		if ((temp & comf) == comf) {
			instruction = Instruction.COMF;
			value = new int[2];
			value[0] = (fMask & temp);
			value[1] = 0b0;
			if ((destBit & temp) == destBit) {
				value[1] = 1;
			}
		} else

		if ((temp & decf) == decf) {
			instruction = Instruction.DECF;
			value = new int[2];
			value[0] = (fMask & temp);
			value[1] = 0b0;
			if ((destBit & temp) == destBit) {
				value[1] = 1;
			}
		} else

		if ((temp & decfsz) == decfsz) {
			instruction = Instruction.DECFSZ;
			value = new int[2];
			value[0] = (fMask & temp);
			value[1] = 0b0;
			if ((destBit & temp) == destBit) {
				value[1] = 1;
			}
		} else

		if ((temp & incf) == incf) {
			instruction = Instruction.INCF;
			value = new int[2];
			value[0] = (fMask & temp);
			value[1] = 0b0;
			if ((destBit & temp) == destBit) {
				value[1] = 1;
			}
		} else

		if ((temp & incfsz) == incfsz) {
			instruction = Instruction.INCFSZ;
			value = new int[2];
			value[0] = (fMask & temp);
			value[1] = 0b0;
			if ((destBit & temp) == destBit) {
				value[1] = 1;
			}
		} else

		if ((temp & iorwf) == iorwf) {
			instruction = Instruction.IORWF;
			value = new int[2];
			value[0] = (fMask & temp);
			value[1] = 0b0;
			if ((destBit & temp) == destBit) {
				value[1] = 1;
			}
		} else

		if ((temp & movf) == movf) {
			instruction = Instruction.MOVF;
			value = new int[2];
			value[0] = (fMask & temp);
			value[1] = 0b0;
			if ((destBit & temp) == destBit) {
				value[1] = 1;
			}
		} else

		if ((temp & movwf) == movwf) {
			instruction = Instruction.MOVWF;
			value = new int[1];
			value[0] = (fMask & temp);
		} else

		if ((temp & nop) == nop) {
			instruction = Instruction.NOP;
		} else

		if ((temp & rlf) == rlf) {
			instruction = Instruction.RLF;
			value = new int[2];
			value[0] = (fMask & temp);
			value[1] = 0b0;
			if ((destBit & temp) == destBit) {
				value[1] = 1;
			}
		} else

		if ((temp & rrf) == rrf) {
			instruction = Instruction.RRF;
			value = new int[2];
			value[0] = (fMask & temp);
			value[1] = 0b0;
			if ((destBit & temp) == destBit) {
				value[1] = 1;
			}
		} else

		if ((temp & subwf) == subwf) {
			instruction = Instruction.SUBWF;
			value = new int[2];
			value[0] = (fMask & temp);
			value[1] = 0b0;
			if ((destBit & temp) == destBit) {
				value[1] = 1;
			}
		} else

		if ((temp & swapf) == swapf) {
			instruction = Instruction.SWAPF;
			value = new int[2];
			value[0] = (fMask & temp);
			value[1] = 0b0;
			if ((destBit & temp) == destBit) {
				value[1] = 1;
			}
		} else

		if ((temp & xorwf) == xorwf) {
			instruction = Instruction.XORWF;
			value = new int[2];
			value[0] = (fMask & temp);
			value[1] = 0b0;
			if ((destBit & temp) == destBit) {
				value[1] = 1;
			}
		}

		// mit f und b
		else

		if ((temp & bcf) == bcf) {
			instruction = Instruction.BCF;
			value = new int[2];
			value[0] = (fMask & temp);
			value[1] = (bMask & temp) >>> 7;
		} else

		if ((temp & bsf) == bsf) {
			instruction = Instruction.BSF;
			value = new int[2];
			value[0] = (fMask & temp);
			value[1] = (bMask & temp) >>> 7;
		} else

		if ((temp & btfsc) == btfsc) {
			instruction = Instruction.BTFSC;
			value = new int[2];
			value[0] = (fMask & temp);
			value[1] = (bMask & temp) >>> 7;
		} else

		if ((temp & btfss) == btfss) {
			instruction = Instruction.BTFSS;
			value = new int[2];
			value[0] = (fMask & temp);
			value[1] = (bMask & temp) >>> 7;
		}

		// mit k
		else

		if ((temp & addlw) == addlw) {
			instruction = Instruction.ADDLW;
			value = new int[1];
			value[0] = (kMask & temp);
		} else

		if ((temp & andlw) == andlw) {
			instruction = Instruction.ANDLW;
			value = new int[1];
			value[0] = (kMask & temp);
		} else

		if ((temp & call) == call) {
			instruction = Instruction.CALL;
			value = new int[1];
			value[0] = (0b11111111111 & temp);
		} else

		if ((temp & goTo) == goTo) {
			instruction = Instruction.GOTO;
			value = new int[1];
			value[0] = (0b11111111111 & temp);
		} else

		if ((temp & iorlw) == iorlw) {
			instruction = Instruction.IORLW;
			value = new int[1];
			value[0] = (kMask & temp);
		} else

		if ((temp & movlw) == movlw) {
			instruction = Instruction.MOVLW;
			value = new int[1];
			value[0] = (kMask & temp);
		} else
			
		if ((temp & retlw) == retlw) {
			instruction = Instruction.RETLW;
			value = new int[1];
			value[0] = (kMask & temp);
		} else

		if ((temp & sublw) == sublw) {
			instruction = Instruction.SUBLW;
			value = new int[1];
			value[0] = (kMask & temp);
		} else

		if ((temp & xorlw) == xorlw) {
			instruction = Instruction.XORLW;
			value = new int[1];
			value[0] = (kMask & temp);
		}

		Command c = new Command(instruction, value);

		return c;
	}
}
