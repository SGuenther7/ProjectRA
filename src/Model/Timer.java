package Model;

public class Timer {

    public static int TIMER_COUNTER_DEFAULT = 4608001;

    public boolean reset = false;

    private int wdtCounter = TIMER_COUNTER_DEFAULT;
    private int tmrCounter = TIMER_COUNTER_DEFAULT;

    public boolean wdtEnabled = false;

    private final Worker peon;

    public Timer(Worker peon) {
        this.peon = peon;
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
        return (peon.getMemory().content()[1][2] & 8) > 0 ? 1 : 0;
    }

    private int getScale() {
        return peon.getMemory().content()[1][2] & 7;
    }

    private int getSource() {
        return peon.getMemory().content()[1][2] & 16;
    }

    public void tick() {
        tickWDT(fetchCycles());
        tickTMR(fetchCycles());
    }

    public void tick(int cycles) {
        tickWDT(cycles);
        tickTMR(cycles);
    }

    public void tickWDT(int cycles) {
        if (wdtEnabled) {
            if (wdtCounter == TIMER_COUNTER_DEFAULT) {
                resetWatchdog();
            }

            // Debug
            try
            {
                // Zyklen von tmr Register runterrechnen
                tmrCounter -= cycles;
            } catch (IndexOutOfBoundsException e) {

            }
            if (wdtCounter == 0) {
                // TO Bit auf 0 setzen (alles ausser bit 4)
                peon.getMemory().content()[0][3] = peon.getMemory().content()[0][3] & 248;
                reset();
            }
        }
    }

    private void tickTMR(int cycles) {
        // Wird von Befehlstakt beeinflusst ?
        if (getSource() == 0) {
            // Wurde tmrCounter nie gestetzt oder hatte reset ?
            if (tmrCounter == TIMER_COUNTER_DEFAULT || tmrCounter == 0) {
                resetTMR0();
            }

            // Debug
            try
            {
                // Zyklen von tmr Register runterrechnen
                tmrCounter -= cycles;
            } catch (IndexOutOfBoundsException e) {

            }

            // Dec. TMR0 Register
            if (tmrCounter <= 0) {
                peon.getMemory().content()[0][1]++;

                // TMR0 uebergeloffen ?
                if (peon.getMemory().content()[0][1] >= 256) {
                    peon.getMemory().content()[0][1] = 0;

                    // T0IF setzen
                    peon.getMemory().content()[0][12] = peon.getMemory().content()[0][12] | 4;

                    // Sind interrupts enabled ?
                    if(peon.getMemory().getGIE() == 1 && peon.getMemory().getT0IE() == 1) {
                        triggerTMRInterrupt();
                    }
                }
            }
        } else {
            // TODO: Port abfragen
        }
    }

    private int fetchCycles() {
        return peon.getCounter().get(peon.getCurrent()).getCycles();
    }

    private void triggerTMRInterrupt() {
        // TODO: Setze korrekte Bits (auch bei RETFIE)
        // Setze T0IF
        peon.getMemory().content()[0][12] = peon.getMemory().content()[0][12] | 4;

        // Speichere PC in Stack
        peon.getStack().push(peon.getCurrent());

        // Springe zu Interrupt Adresse
        peon.setCurrent(4);
        peon.getMemory().content()[0][2] = 4;
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
        // Basis Interval 18ms
        // Abarbeitung von Befehl betraegt 1µs
        wdtCounter *= 18000;
    }

    public boolean toggleWDT() {
        return this.wdtEnabled = !wdtEnabled;
    }

    public int getWdtCounter() {
        return wdtCounter;
    }

    public int getTmrCounter() {
        return tmrCounter;
    }

    private void reset() {
        reset = true;
        // TODO: Warnung anzeigen
    }
}
