package Model;

import java.util.Arrays;

public class Memory {

    private int[][] memory;

    public Memory() {
        memory = new int[2][48];
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
                        target = 0;
                        break;
                    default :
                        target = 0;

                }
        }

        return target;
    }


    // Check ob indirekt Adr. und liefere korrekten Index
    private int resolveAddressing(int index) {

        if (index == 0) {
            return memory[0][3];
        }

        return index;
    }

    public int get(int bank, int index) {
        return memory[resolveBank(bank,index)][resolveAddressing(index)];
    }

    public void set(int bank, int index, int value) {
        // TODO: Check ob korrekte Indirekte Adrr.
        memory[resolveBank(bank,index)][resolveAddressing(index)] = value;
    }


    public int[][] content() {
        return memory;
    }

    @Override
    public boolean equals(Object obj) {
        return Arrays.equals(memory, ((Memory) obj).content());
    }
}
