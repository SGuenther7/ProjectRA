package Model;

import java.util.ArrayList;

public class Worker {

    public static int PC_MAX_VALUE = 1024;

    private int working;
    private Memory memory;
    private ArrayList<Command> counter;
    private Stack stack;

    private Port portA;
    private Port portB;

    private int cycles;

    private Timer timer;

    private int current;

    public Worker() {
        reset();
        this.counter = new ArrayList<>();
    }

    public Worker(int working) {
        this();
        this.working = working;
    }

    public Worker(int working, Memory memory) {
        this(working);
        this.memory = memory;
    }

    public Worker(int working, Memory memory, ArrayList<Command> counter, Stack stack, int current) {
        this(working,memory);
        this.counter = counter;
        this.stack = stack;
        this.current = current;
    }

    public Worker(int working, Memory memory, ArrayList<Command> counter, Stack stack, int current, int cycles) {
        this(working,memory,counter,stack,current);
        this.cycles = cycles;
    }

    public Worker(Worker clone) {
        this();

        this.working = clone.working;

        this.memory = new Memory(this,clone.getMemory());

        this.counter = new ArrayList<>();
        this.counter.addAll(clone.counter);
        this.timer = new Timer(this, clone.getTimer());
        this.stack = new Stack(clone.stack);
        this.current = clone.getCurrent();
        this.cycles = clone.getCycles();
    }

    public boolean feed(ArrayList<Command> container) {
        for (Command fresh : container) {
            if (!feed(fresh)) {
                return false;
            }
        }

        counter.get(getCurrent()).setNext(true);
        return true;
    }

    public boolean feed(Command fresh) {
        // Programmspeicher limit von 1024
        if (counter.size() < 1024) {
            counter.add(fresh);
            return true;
        }
        return false;
    }

    public void execute(int i) {
        Command command = counter.get(i);
        int result;

        switch (command.getInstruction()) {
            case ADDWF:
                // Var : f, d
                // Flag : C, CD, Z

                result = working + memory.get(getBank(), command.getValue()[0]);
                result = result & 255;

                handleCarryFlagOnAdd(working, memory.get(getBank(), command.getValue()[0]));
                handleDigitCarryOnAdd(working, memory.get(getBank(), command.getValue()[0]));
                handleZeroFlag(result);

                // Destination Bit gesetzt ?
                if (command.getValue()[1] == 1) {
                    memory.set(getBank(), command.getValue()[0], result);
                } else {
                    working = result;
                }
                break;
            case ANDWF:
                // Var : f, d
                // Flag : Z

                result = working & memory.get(getBank(), command.getValue()[0]);
                result = result & 255;
                handleZeroFlag(working);

                if (command.getValue()[1] == 1) {
                    memory.set(getBank(), command.getValue()[0], result);
                } else {
                    working = result;
                }
                break;
            case COMF:
            	// Var : f, d
            	result = ~(memory.get(getBank(),command.getValue()[0]));
                result = result & 255;
            	handleZeroFlag(result);
	
            	 if (command.getValue()[1] == 1) {
                     memory.set(getBank(), command.getValue()[0], result);
                 } else {
                     working = result;
                 }
                break;
            case DECF:
                // Var : f, d
                // Flag : Z
                int beforeDEC = memory.get(getBank(), command.getValue()[0]);
                if(beforeDEC == 0) {
                    result = 255;
                } else {
                  result = beforeDEC - 1;
                }

                handleZeroFlag(result);

                if (command.getValue()[1] == 1) {
                    memory.set(getBank(), command.getValue()[0], result);
                } else {
                    working = result;
                }
                break;
            case DECFSZ:
                // Var : f, d
            	result = memory.get(getBank(), command.getValue()[0]);
                if(result == 0) {
                    result = 255;
                } else {
                  result--;
                }
                
                if (command.getValue()[1] == 1) {
                    memory.set(getBank(), command.getValue()[0], result);
                } else {
                    working = result;
                } 
                if(result == 0) {
                	counter.set(i + 1, new Command( Instruction.NOP, new int[] {}));
                }
                break;
            case INCF:
                // Var : f, d
                // Flag : Z
            	result = memory.get(getBank(), command.getValue()[0]);
            	if(result == 255) {
            		result = 0;
            	} else {
            		result++;
            	}
            	
                handleZeroFlag(working);
                
                if (command.getValue()[1] == 1) {
                    memory.set(getBank(), command.getValue()[0], result);
                } else {
                    working = result;
                }
                break;
            case INCFSZ:
                // Var : f, d
            	 result = memory.get(getBank(), command.getValue()[0]);
            	 
            	 if(result == 255) {
             		result = 0;
             		} else {
             		result++;
            	 }
            	
            	 if (command.getValue()[1] == 1) {
                     memory.set(getBank(), command.getValue()[0], result);
                 } else {
                     working = result;
                 }
                 
            	 if (result == 0) {
                	 counter.set(i + 1, new Command(Instruction.NOP, new int[] {}));
                 }
                break;
            case IORWF:
                // Var : f, d
                result = working | memory.get(getBank(), command.getValue()[0]);
                result = result & 255;
                handleZeroFlag(result);

                if (command.getValue()[1] == 1) {
                    memory.set(getBank(), command.getValue()[0], result);
                } else {
                    working = result;
                }
                break;
            case MOVF:
                // Var : f, d
                result = memory.get(getBank(), command.getValue()[0]);
                result = result & 255;

                handleZeroFlag(result);

                if (command.getValue()[1] == 1) {
                    memory.set(getBank(), command.getValue()[0], result);
                } else {
                    working = result;
                }
                break;
            case RLF:
                // Var : f, d

                // Carry speichern
            	int temp = (memory.content()[getBank()][3] & 0b1);

            	// Rotate
            	result = memory.get(getBank(), command.getValue()[0]) << 1;

            	// Altes carry hinzufuegen
            	result = (result & 511) + temp;

            	// Bit setzen
            	if(result  >= 256) {
                    memory.set(getBank(), 3, (memory.get(getBank(),3) | 0b1));
                } else {
                    memory.set(getBank(), 3, (memory.get(getBank(),3) & 254));
                }

            	// Auf 8 bit verkleinern
                result = (result & 255);

            	if (command.getValue()[1] == 1) {
                    memory.set(getBank(), command.getValue()[0], result);
                } else {
                    working = result;
                }
                break;
            case RRF:
                // Var : f, d
                // Carry speichern
            	int tempCarryBit = (memory.content()[getBank()][3] & 0b1) << 7;
            	// Carry setzen
            	memory.set(getBank(),3,memory.get(getBank(),command.getValue()[0]) & 0b1);
            	// Rotate
            	result = memory.get(getBank(), command.getValue()[0]) >> 1;
            	result += tempCarryBit;
            	// Auf 8 bit verkleinern
                result = (result & 255);

            	if (command.getValue()[1] == 1) {
                    memory.set(getBank(), command.getValue()[0], result);
                } else {
                    working = result;
                }
            	break;
            case SUBWF:
                // Var : f, d
                // Flag : C, CD, Z
                result = memory.get(getBank(), command.getValue()[0]) - working;
                result = result & 255;

                handleCarryFlagOnSub(memory.get(getBank(), command.getValue()[0]), working);
                handleDigitCarryOnSub(memory.get(getBank(), command.getValue()[0]),working);
                handleZeroFlag(result);

                // Destination Bit gesetzt ?
                if (command.getValue()[1] == 1) {
                    memory.set(getBank(), command.getValue()[0], result);
                } else {
                    working = result;
                }
                break;
            case SWAPF:
                // Var : f, d
            	result = memory.get(getBank(),command.getValue()[0]);
            	int resultFirst = (0b11110000 & result) >>> 4;
            	int resultSecond = (0b1111 & result) << 4;
            	result = resultFirst + resultSecond;
            	
            	 if (command.getValue()[1] == 1) {
                     memory.set(getBank(), command.getValue()[0], result);
                 } else {
                     working = result;
                 }
                break;
            case XORWF:
                // Var : f, d
                // Flag : Z
                result = working ^ memory.get(getBank(), command.getValue()[0]);

                handleZeroFlag(working);

                if (command.getValue()[1] == 1) {
                    memory.set(getBank(), command.getValue()[0], result);
                } else {
                    working = result;
                }
                break;
            case CLRF:
                // Var : f
                // Flag : Z
                memory.set(getBank(), command.getValue()[0], 0);

                handleZeroFlag(0);
                break;
            case MOVWF:
                // Var : f
                memory.set(getBank(), command.getValue()[0], working);
                break;
            case CLRW:
                // Flag : Z
                working = 0;
                handleZeroFlag(working);
                break;
            case NOP:
                break;
            case CLRWDT:
                // TO und PD setzen
                getMemory().content()[0][3] = getMemory().content()[0][3] | 24;
                // Prescaler
                //getMemory().content()[1][1] = getMemory().content()[0][3] & 248;

                getTimer().resetWatchdog();
                break;
            case RETFIE:
                // Timer und cycles laufen weiter,
                // PC wird nicht inkrementiert.

                if (stack.size() > 0) {
                    memory.content()[0][2] = (int) stack.pop();
                    current = memory.content()[0][2];
                }

                // GIE auf 1
                getMemory().content()[0][11] = getMemory().content()[0][11] | 128;

                updateCycles(i);
                updateTimer(i);
                return;
            case RETURN:
                if (stack.size() > 0) {
                    memory.content()[0][2] = (int) stack.pop();
                    current = memory.content()[0][2];
                }

                // Timer und cycles laufen weiter,
                // PC wird nicht inkrementiert.
                updateCycles(i);
                updateTimer(i);
                return;
            case SLEEP:
                // Setze TO
                getMemory().content()[0][3] = getMemory().content()[0][3] | 16;
                // Loesche PD
                getMemory().content()[0][3] = getMemory().content()[0][3] & 247;

                if(!getTimer().reset) {
                    updateCycles(i);
                    updateTimer(i);
                } else {
                    // Wake up
                    getTimer().reset = false;
                    getTimer().resetWatchdog();

                    updateCycles(i);
                    updateTimer(i);
                    updateCurrent();
                }
                return;
            case BCF:
                // Var : f, b
            	result = memory.get(getBank(), command.getValue()[0]);
            	
            	int bit = (int) Math.pow(2, command.getValue()[1]);
            	
            	temp = 0b11111111 - bit;
            	
            	memory.set(getBank(),command.getValue()[0], result & temp);
                break;
            case BSF:
                // Var : f, b
            	result = memory.get(getBank(), command.getValue()[0]);
            	
            	temp = (int) Math.pow(2, command.getValue()[1]);
            	
            	memory.set(getBank(),command.getValue()[0], result | temp);
            	break;
            case BTFSS:
                // Var : f, b
            	result = memory.get(getBank(), command.getValue()[0]) & (int) Math.pow(2, command.getValue()[1]);
            	
            	if(result != 0) {
            		counter.set(i + 1, new Command(Instruction.NOP, new int[] {}));
            	}
                break;
            case BTFSC:
                // Var : f, b
                // TODO: imp. + test
             	result = memory.get(getBank(), command.getValue()[0]) & (int) Math.pow(2, command.getValue()[1]);

            	if(result == 0) {
            		counter.set(i + 1, new Command(Instruction.NOP, new int[] {}));
            	}
                break;
            case ADDLW:
                // Var : k
                // C, CD, Z
                result = working + command.getValue()[0];

                handleCarryFlagOnAdd(working, command.getValue()[0]);
                handleDigitCarryOnAdd(working, command.getValue()[0]);
                handleZeroFlag(result);

                working = result;
                break;
            case ANDLW:
                // Var : k
                // Flag : Z
                result = working & command.getValue()[0];
                handleZeroFlag(working);
                working = result;
                break;
            case IORLW:
                // Var : k
                // Flag : Z
                result = working | command.getValue()[0];
                handleZeroFlag(working);
                working = result;
                break;
            case MOVLW:
                // Var : k
                working = command.getValue()[0];
                break;
            case RETLW:
                // Var : k
                working = command.getValue()[0];

                if (stack.size() > 0) {
                    memory.content()[0][2] = (int) stack.pop();
                    current = memory.content()[0][2];
                }

                updateCycles(i);
                updateTimer(i);
                return;
            case SUBLW:
                // Var : k
                // Flag : C, CD, Z
                result = command.getValue()[0] - working;

                handleCarryFlagOnSub(command.getValue()[0], working);
                handleDigitCarryOnSub(command.getValue()[0], working);
                handleZeroFlag(result);

                working = result;
                break;
            case XORLW:
                // Var : k
                // Flag : Z
                working = working ^ command.getValue()[0];
                handleZeroFlag(working);
                break;
            case CALL:
                // Var : k
                stack.push(getCurrent()+1);
            case GOTO:
                // Var : k
                memory.set(0,2, counter.get(i).getValue()[0]);
                current = memory.content()[0][2];

                // Timer und cycles laufen weiter,
                // PC wird nicht inkrementiert.
                updateCycles(i);
                updateTimer(i);
                return;
        }

        updateCycles(i);
        checkWDT();
        updateTimer(i);
        updateCurrent();
    }

    public void checkWDT() {
        if(timer.isReset()) {
            reset();
        }
    }

    private void reset() {
        this.working = 0;
        this.memory = new Memory(this);
        this.stack = new Stack();
        this.timer = new Timer(this);

        portA = new Port();
        portB = new Port();

        current = 0;
        cycles = 0;

        // Option Register auf 1
        memory.content()[1][1] = 255;
    }

    public void updateCycles(int index) {
        this.cycles += counter.get(index).getCycles();
    }

    public void updateTimer(int index) {
        timer.tick(counter.get(index).getCycles());
    }

    public void next() {
        counter.get(getCurrent()).setNext(false);
        execute(getCurrent());
        if (counter.size() > getCurrent()) {
            counter.get(getCurrent()).setNext(true);
        }
    }

    public boolean hasNext() {
        return (getCurrent() < counter.size() - 1 || isJump(getCounter().get(getCurrent()).getInstruction()));
    }

    public boolean isJump() {
        return isJump(getCounter().get(getCurrent()).getInstruction());
    }

    public boolean isBreakpoint(Command command) {
        return command.isBreakpoint();
    }

    public boolean isBreakpoint() {
        return isBreakpoint(getCounter().get(getCurrent()));
    }

    public boolean isJump(Instruction instruction) {
        switch (instruction) {
            case GOTO:
            case CALL:
            case RETURN:
            case RETFIE:
            case RETLW:
                return true;
        }
        return false;
    }

    public boolean handleZeroFlag(int value) {
        if (value == 0) {
            memory.set(getBank(), 3, memory.get(getBank(), 3) | 4);
            return true;
        } else {
            memory.set(getBank(), 3, memory.get(getBank(), 3) & 251);
            return false;
        }
    }

    public boolean handleCarryFlagOnAdd(int base, int add) {
        if (base + add > 255) {
            memory.set(getBank(), 3, memory.get(getBank(), 3) | 1);
            return true;
        } else {
            memory.set(getBank(), 3, memory.get(getBank(), 3) & 254);
            return false;
        }
    }

    public boolean handleCarryFlagOnSub(int base, int sub) {
        int s = (~sub + 1) & 255;

        if((base + s) >= 256){
            memory.set(getBank(), 3, memory.get(getBank(), 3) | 1);
            return true;
        }
        else {
            memory.set(getBank(), 3, memory.get(getBank(), 3) & 254);
            return false;
        }
    }

    public boolean handleDigitCarryOnAdd(int base, int add) {
        base = base & 15;
        add = add & 15;

        if (base + add > 15) {
            memory.set(getBank(), 3, memory.get(getBank(), 3) | 2);
            return true;
        } else {
            memory.set(getBank(), 3, memory.get(getBank(), 3) & 253);
            return false;
        }
    }

    public boolean handleDigitCarryOnSub(int base, int sub) {
        base = base & 15;
        sub = (~sub + 1) & 15;

        if ((base + sub) >= 16) {
            memory.set(getBank(), 3, memory.get(getBank(), 3) | 2);
            return true;
        } else {
            memory.set(getBank(), 3, memory.get(getBank(), 3) & 253);
            return false;
        }
    }

    public int getWorking() {
        return working;
    }

    public Memory getMemory() {
        return memory;
    }

    public int getBank() {
        int bank = memory.getRP0();

        if(memory.getRP1() == 1) {
            bank += 2;
        }
        return bank;
    }

    public ArrayList<Command> getCounter() {
        return counter;
    }

    public Stack getStack() {
        return stack;
    }

    public void updateCurrent() {
        memory.content()[0][2]++;

        if (memory.content()[0][2] >= PC_MAX_VALUE) {
            memory.set(getBank(), 2, memory.get(getBank(), 3) | 1);
            memory.content()[0][2] = 0;
        }
        current = memory.content()[0][2];
        //applyPCL();
    }

    public void applyPCL() {
        int temp = memory.content()[0][10];
        temp = temp << 8;
        temp += memory.content()[0][2];
        current = temp;
    }

    public int getCurrent() {
        return current;
    }

    public Command getCurrentCommand() {
        return counter.get(current);
    }

    @Override
    public boolean equals(Object obj) {
        Worker other = (Worker) obj;

        if (other.getWorking() != this.working) {
            System.out.println("Falcher Inhalt in Working.");
            return false;
        }

        // Check Speicher
        if (other.getMemory().equals(memory) != true) {
            System.out.println("Memory Inhalt unterschiedlich.");
            return false;
        }

        return true;
    }

    public Timer getTimer() {
        return timer;
    }

    public Port getPortA() {
        return portA;
    }

    public Port getPortB() {
        return portB;
    }

    public void print() {
        System.out.println("Working : " + working);
        System.out.print("Status :  ");
        memory.print();
        System.out.println("Current : " + current);
        System.out.print("Befehle : ");

        for (Command element : counter) {
            System.out.print(element.getInstruction() + " ");
        }
        System.out.println();
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getCycles() {
        return cycles;
    }

    public void setCycles(int cycles) {
        this.cycles = cycles;
    }
}
