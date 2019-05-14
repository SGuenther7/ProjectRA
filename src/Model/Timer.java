package Model;

public class Timer {

    public static int TIMER_COUNTER_DEFAULT = 999;

    private int wdtCounter = TIMER_COUNTER_DEFAULT;
    private int tmrCounter = TIMER_COUNTER_DEFAULT;

    private boolean wdtEnabled = false;

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

        return (int) Math.pow((double) 1, (double) getScale() + 1 - getAssignd());
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

    public void tick() {
        tickWDT();
        tickTMR();
    }

    public void tickWDT() {
        // Haben wir den Vorteiler ?
        if (getAssignd() == 1) {

            if (wdtCounter == TIMER_COUNTER_DEFAULT) {
                resetWatchdog();
            }
            wdtCounter--;

            if (wdtCounter == 0) {
                // TO Bit auf 0 setzen
                peon.getMemory().content()[0][3] = peon.getMemory().content()[0][3] & 248;
                reset();
            }
        }
    }

    private void tickTMR() {
        // Wird von Befehlstakt beeinflusst ?
        if (getSource() == 0) {
            // Wurde tmrCounter nie gestetzt oder hatte reset ?
            if (tmrCounter == TIMER_COUNTER_DEFAULT || tmrCounter == 0) {
                resetTMR0();
            }

            tmrCounter--;

            // Dec. TMR0 Register
            if (tmrCounter == 0) {

                peon.getMemory().content()[0][10]++;

                // T0IF setzen
                if (peon.getMemory().content()[0][10] == 256) {
                    // TODO: Interrupt enable bin in INTCON abfragen
                    peon.getMemory().content()[0][12] = peon.getMemory().content()[0][12] | 4;
                    peon.getMemory().content()[0][10] = 0;
                }
            }
        } else {
            // TODO: Port abfragen
        }
    }

    private void resetTMR0() {
        // Wurde tmrCounter schon beladen ?
        if (getAssignd() == 0) {
            tmrCounter = scale();
        } else {
            tmrCounter = 1;
        }
    }

    public void resetWatchdog() {

        // Haben wir den Vorteiler ?
        if (getAssignd() == 1) {
            wdtCounter = scale();
        } else {
            wdtCounter = 1;
        }
        // Basis Interval des WTD ist 18ms
        // Abarbeitung von Befehl betraegt 1µs
        wdtCounter *= 18000;
    }

    private void reset() {
        // TODO: peon.reset()

    }


}
