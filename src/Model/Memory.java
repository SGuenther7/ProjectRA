package Model;

public class Memory {

    private int[][] memory;
    private Worker peon;

    public Memory(Worker peon) {
        memory = new int[2][48];
        this.peon = peon;
    }

    public Memory(Memory clone) {
        this(clone.getWorker());

        for (int y = 0; y < clone.content().length; y++) {
            for (int x = 0; x < clone.content()[0].length; x++) {
                memory[y][x] = clone.content()[y][x];
            }
        }
    }

    // Internes get() um korrekte Bank fuer Index zu finden.
    // Da manche Register in allen Baenken vorhanden sind.
    private int resolveBank(int bank, int index) {

        int target = bank;

        // Auf welcher Bank befinden wir uns ?
        switch (bank) {

            // Bank Eins ist das Haupt Register
            // kann also nur auf sich verweisen
            case 0:
                break;

            // Bank Zwei ?
            case 1:
                // Linke Duplikate Register von
                // Bank Zwei zu Bank eins
                switch (index) {
                    case 1:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                        target = 1;
                        break;
                    default:
                        target = 0;
                }
        }

        return target;
    }


    /* Check ob indirekt Adr. und liefere korrekten Index
     * @param Index
     * @return Ziel Index in Bank
     */
    private int resolveAddressing(int index) {

        if (index == 0) {
            return memory[0][4];
        }

        return index;
    }

    public Worker getWorker() {
        return this.peon;
    }

    public int get(int bank, int index) {
        return memory[resolveBank(bank, index)][resolveAddressing(index)];
    }

    public void set(int bank, int index, int value) {
        // Extra Funktionen bei Speicherzugriff
        switch (index) {
            case 0:
                if(memory[0][4] != 0) {
                    memory[0][memory[0][4]] = value;
                }
                return;
            case 1:
                // Zugriff auf TMR0
                // TODO: Benoetigt zwei extra Zyklen
                // TODO: Setzt Vorteiler zurueck falls
                //       TMR0 ihn hat.
                break;
            case 2:
                // PCL oder PCLATH wurde beschrieben
                // Aenderung von Wert
                memory[0][2] = value;
                peon.applyPCL();
                return;
            case 5:
            case 6:
                // TRIS Zugriff
                if(bank == 1) {
                    // Update Port Register mit Port Zwischenspeicher (an TRIS bits)
                    memory[1][index] = value;
                    memory[0][index] = memory[0][index] | (memory[1][index] & resolvePort(index).get());
                    return;
                }

                // Port Register Zugriff
                if(bank == 0) {
                    // Speichere Bits die auch in TRIS gesetzt sind zu Port Register
                    // Speicher Rest in Zwischenspeicher
                    memory[0][index] = memory[1][index] & value;
                    resolvePort(index).set(memory[1][index] ^ value);

                    //1010 tris
                    //1111 value
                    //0101 cache
                    return;
                }
                break;
        }

        // Aenderung von Wert
       memory[resolveBank(bank, index)][resolveAddressing(index)] = value;
    }

    private Port resolvePort(int index) {
        switch (index) {
            case 5:
                return peon.getPortA();
            case 6:
                return peon.getPortB();
        }
            return null;
    }

    private boolean accessesTris(int bank, int index) {
        return (bank == 0 && (index == 5 ||index == 6));
    }

    public int[][] content() {
        return memory;
    }

    public int getRP1() {
        return (memory[0][3] & 64) > 0 ? 1 : 0;
    }
    public int getRP0() {
        return (memory[0][3] & 32) > 0 ? 1: 0;
    }
    public int getZero() {
        return (memory[0][3] & 4) > 0 ? 1: 0;
    }
    public int getDitgitCarry() {
        return (memory[0][3] & 2) > 0 ? 1: 0;
    }
    public int getCarry() {
        return (memory[0][3] & 1) > 0 ? 1: 0;
    }
    public int getT0CS() {
        return (memory[1][3] & 64) > 0 ? 1: 0;
    }
    public int getPSA() {
        return (memory[1][1] & 8) > 0 ? 1: 0;
    }
    public int getPS0() {
        return (memory[1][1] & 1) > 0 ? 1: 0;
    }
    public int getPS1() {
        return (memory[1][1] & 2) > 0 ? 1: 0;
    }
    public int getPS2() {
        return (memory[1][1] & 4) > 0 ? 1: 0;
    }

    public int getGIE() {
        return (memory[0][12] & 128) > 0 ? 1: 0;
    }

    public int getT0IE() {
        return (memory[0][12] & 32) > 0 ? 1: 0;
    }

    public int getT0IF() {
        return (memory[0][12] & 32) > 0 ? 1: 0;
    }

    public void print() {
        for (int y = 0; y < memory.length; y++) {
            for (int x = 0; x < memory[0].length; x++) {
                System.out.println(memory[y][x]);
            }
        }
    }

    @Override
    public boolean equals(Object obj) {

        for (int y = 0; y < memory.length; y++) {
            for (int x = 0; x < memory[0].length; x++) {
                if (memory[y][x] != ((Memory) obj).content()[y][x]) {
                    return false;
                }
            }
        }

        return true;
    }
}
