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

    public Timer(Worker peon, Timer timer) {
        this.peon = peon;
        this.wdtEnabled = timer.wdtEnabled;
        this.reset = timer.reset;
        this.wdtCounter = timer.wdtCounter;
        this.tmrCounter = timer.tmrCounter;
        this.TIMER_COUNTER_DEFAULT = timer.TIMER_COUNTER_DEFAULT;
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
        return (peon.getMemory().content()[1][1] & 8) > 0 ? 1 : 0;
    }

    private int getScale() {
        return peon.getMemory().content()[1][1] & 7;
    }

    private int getSource() {
        return peon.getMemory().content()[1][1] & 16;
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

            // Zyklen von tmr Register runterrechnen
            wdtCounter -= cycles;

            if (wdtCounter <= 0) {
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
            if (tmrCounter == TIMER_COUNTER_DEFAULT) {
                resetTMR0();
            }

            // Debug
            try {
                // Zyklen von tmr Register runterrechnen
                tmrCounter -= cycles;
            } catch (IndexOutOfBoundsException e) {

            }

            // Dec. TMR0 Register
            if (tmrCounter <= 0) {
                resetTMR0();
                peon.getMemory().content()[0][1]++;

                // TMR0 uebergeloffen ?
                if (peon.getMemory().content()[0][1] >= 256) {
                    peon.getMemory().content()[0][1] = 0;

                    // T0IF setzen
                    peon.getMemory().content()[0][12] = peon.getMemory().content()[0][12] | 4;

                    // Sind interrupts enabled ?
                    if (peon.getMemory().getGIE() == 1 && peon.getMemory().getT0IE() == 1) {
                        triggerTMRInterrupt();
                    }
                }
            }

        } else {
            // TODO: Port abfragen
        }
    }

    private void triggerTMRInterrupt() {
        // GIE ausschalten
        peon.getMemory().content()[0][11] = peon.getMemory().content()[0][11] & 127;

        // Setze T0IF
        peon.getMemory().content()[0][11] = peon.getMemory().content()[0][11] | 4;

        // Speichere PC in Stack
        peon.getStack().push(peon.getCurrent());

        // Springe zu Interrupt Adresse
        // ISR Adresse bei 3 + 1 (inc) = 4
        peon.setCurrent(3);
        peon.getMemory().content()[0][2] = 3;
    }

    public void resetTMR0() {
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
        // TODO: Normallerweise 18000
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

    public boolean isReset() {
        return reset;
    }

    private void reset() {
        reset = true;
        // TODO: Warnung anzeigen
    }
}
