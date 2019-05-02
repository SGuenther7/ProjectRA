package Model;

public class Timer {

    private int wdtCounter;
    private final Worker peon;

    public Timer(Worker peon) {
        this.peon = peon;
        wdtCounter = 0;
    }

    private int scale() {
        // TMR0 faengt bei 2^1, WDT bei 2^0 an.
        // TMR0 im Prescaler ist 0.
        // 0 + 1 - 0  = 1
        // TMR0 bei 0 ist 2^1 = 2
        // 0 + 1 - 1 = 0
        // WDT bei 0 ist 2^0 = 1

        return (int) Math.pow((double) 2, (double) getScale() + 1 - getAssignd());
    }

    private int getAssignd() {
        return peon.getMemory().content()[1][12] & 4;
    }

    private int getScale() {
        return peon.getMemory().content()[1][12] & 7;
    }

    private int getSource() {
        return peon.getMemory().content()[1][12] & 16;
    }

    public void tick(int cycle) {

        wdtCounter += cycle;

        // Update WDT
        if (getAssignd() == 1) {

            if (wdtCounter >= 18000 * scale()) {
                reset();
            }

        } else {
            if (wdtCounter >= 18000) {
                reset();
            }
        }

        // Update Timer0
        if (getSource() == 0) {
            // TODO: Prescaler impl. nachsuchen

            // Update nur wenn wir
            if (getAssignd() == 0) {
                peon.getMemory().content()[0][1] += scale();
            } else {
                peon.getMemory().content()[0][1] += 2;
            }

            // Ueberfluss ?
            if (peon.getMemory().content()[0][1] >= scale()) {
                // T0IF bit setzen
                peon.getMemory().content()[0][8] = peon.getMemory().content()[0][8] & 4;

            }

        }

    }

    private void reset() {
        // TODO: peon.reset()

    }


}
