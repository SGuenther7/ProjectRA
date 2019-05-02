package Model;

import java.util.Arrays;

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
        memory[resolveBank(bank, index)][resolveAddressing(index)] = value;

        // PCL oder PCLATH wurde beschrieben
        switch (index) {
            // Zugriff auf TMR0
            case 1:
                // TODO: Benoetigt zwei extra Zyklen
                // TODO: Setzt Vorteiler zurueck falls
                //       TMR0 ihn hat.
                break;
            case 2:
            case 10:
                peon.updateCurrent();
                break;
        }
    }


    public int[][] content() {
        return memory;
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
