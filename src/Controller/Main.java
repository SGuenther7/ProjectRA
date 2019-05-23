package Controller;

import Model.Instruction;
import Model.Parser;
import Model.Worker;
import View.Primary;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Main {
    private ArrayList<Worker> states;
    private Primary view;
    private int current = 0;
    private Thread runner;

    private void debug() {
        this.load("/Users/akira/Projects/java/ProjectRa/src/tests/raw/TPicSim1.LST");
        this.update();
    }

    public void start() {
        runner = new Thread() {
            public void run() {
                Main.this.run();
            }
        };

        view = new Primary();
        view.initialize();
        this.initialiseActionListeners(view);
        reset();

        // Mache GUI sichtbar
        view.start();
    }

    private void initialiseActionListeners(Primary view) {
        JButton buttons[] = view.getButtons();
        buttons[0].addActionListener(e -> {
            //this.run();
            this.runner.start();
            this.update();
        });
        buttons[1].addActionListener(e -> {
            this.stop();
            this.update();
        });
        buttons[2].addActionListener(e -> {
            this.forward();
            this.update();
        });
        buttons[3].addActionListener(e -> {
            this.back();
            this.update();
        });
        buttons[4].addActionListener(e -> {
            this.reset();
            this.update();
        });
        buttons[5].addActionListener(e -> {
            this.load(getURL());
            this.update();
        });

        // JList
        view.getJList().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                JList list = (JList) e.getSource();
                int index = list.locationToIndex(e.getPoint());

                try {
                    states.get(current).getCounter().get(index).toggleBreakpoint();
                } catch (IndexOutOfBoundsException b) {
                    System.out.println("lol");
                }

                super.mouseReleased(e);
            }
        });


        // Ports
        // TRIS A
        buttons = view.getPortATRIS();

        for (int i = 0; i < buttons.length; i++) {
            final int temp = i;
            buttons[i].addActionListener(e -> {

                if (states.size() == 0) {
                    // TODO: Fehlermeldung/Bitanweisung vorspeichern
                    return;
                }

                int bit = calculateBit(temp);
                // Aender Bit in TRIS
                invertPortBit(0, true, bit);

                // Update Port Bit
                if (checkBit(getCurrentState().getPortA().get(), bit) != checkPortBit(0, false, bit)) {
                    invertPortBit(0, false, bit);
                }

                updateButtons();
            });
        }

        // TRIS B
        buttons = view.getPortBTRIS();

        for (int i = 0; i < buttons.length; i++) {
            final int temp = i;
            buttons[i].addActionListener(e -> {
                if (states.size() == 0) {
                    // TODO: Fehlermeldung/Bitanweisung vorspeichern
                    return;
                }

                int bit = calculateBit(temp);
                // Aender Bit in TRIS
                invertPortBit(1, true, bit);

                // Update Port Bit
                // Nehme Bit von internal Register und schiebe in Port
                boolean internal = checkBit(getCurrentState().getPortB().get(), bit);
                boolean port = checkPortBit(1, false, bit);

                System.out.println("PORT : " + port);
                System.out.println("INTE : " + internal);

                if (port != internal) {
                    System.out.println("Ausgefuehrt!");
                    System.out.println();
                    invertPortBit(1, false, bit);
                }

                updateButtons();
            });
        }

        // Port A
        buttons = view.getPortAPins();

        for (int i = 0; i < buttons.length; i++) {
            final int temp = i;
            buttons[i].addActionListener(e -> {
                if (states.size() == 0) {
                    // TODO: Fehlermeldung/Bitanweisung vorspeichern
                    return;
                }

                if (checkPortBit(0, true, calculateBit(temp))) {
                    invertPortBit(0, false, calculateBit(temp));
                } else {
                    // Speicher in Zwischenregister
                    getCurrentState().getPortA().and(calculateBit(temp));
                }

                updateButtons();
            });
        }

        // Port B
        buttons = view.getPortBPins();

        for (int i = 0; i < buttons.length; i++) {
            final int temp = i;
            buttons[i].addActionListener(e -> {
                if (states.size() == 0) {
                    // TODO: Fehlermeldung/Bitanweisung vorspeichern
                    return;
                }

                if (checkPortBit(1, true, calculateBit(temp))) {
                    invertPortBit(1, false, calculateBit(temp));
                } else {
                    // Speicher in Zwischenregister
                    getCurrentState().getPortB().and(calculateBit(temp));
                }
            });
        }
        /*
         */
    }

    /**
     * Helfer Funktion um aus einem Index (0-7) ein Bit des Registers zu machen
     */
    private int calculateBit(int index) {
        return (int) Math.pow(2, (8 - index));
    }

    /**
     * Helfer Funktion um genau ein Bit in den Port Registern zu invertieren
     */
    private void invertPortBit(int port, boolean tris, int bit) {
        int register = 5 + port;
        int bank = (tris == true) ? 1 : 0;
        int value = getCurrentState().getMemory().content()[bank][register];
        int result = value;

        if ((value & bit) > 0) {
            result -= bit;
        } else {
            result += bit;
        }

        // TODO: Fehler bei Bit uebersetzung. Letztes Bit nicht
        //       Adressierbar wenn auf 0xFF limitiert
        getCurrentState().getMemory().content()[bank][register] = result & 0x1FF;
    }

    /**
     * Helfer Funktion fuer das setzen der Ports
     */
    private boolean checkPortBit(int port, boolean tris, int bit) {
        int register = 5 + port;
        int bank = (tris == true) ? 1 : 0;
        return checkBit(getCurrentState().getMemory().content()[bank][register], bit);
    }

    private boolean checkBit(int target, int bit) {
        return ((target & bit) > 0);
    }


    private void run() {
        // TODO: Execute bis Ende, dann update()
        // TODO: Benoetigt Auto-Breakpoint bei GOTO/CALL!

        // Ist eine .LST Datei geladen ?
        if (states.size() == 0) {
            return;
        }

        // Waerend nicht am Ende
        while (getCurrentState().getCurrent() < getCurrentState().getCounter().size()) {

            // Ist breakpoint oder GOTO ?
            if (getCurrentState().getCounter().get(getCurrentState().getCurrent()).isBreakpoint() ||
                    getCurrentState().getCounter().get(getCurrentState().getCurrent()).getInstruction() == Instruction.GOTO) {
                break;
            }

            // Naechsten Befehl ausfuehren
            states.add(new Worker(states.get(current)));
            current++;
            states.get(current).next();

            // DEBUG
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

            }
        }

        update();
    }

    private void stop() {
        // TODO: Thread mit run() stoppen
        runner.interrupt();
    }

    private void forward() {
        if (states.size() == 0) {
            return;
        }

        // Gibt es etwas zum ausfuehren ?
        if (states.get(current).hasNext()) {
            // Haben wir den naechsten State schon ?
            if (current + 1 <= states.size() - 1) {
                current++;
            } else {
                // Naechsten Befehl ausfuehren
                states.add(new Worker(states.get(current)));
                current++;
                states.get(current).next();
            }
        }
    }

    private void back() {
        if (current == 0 || states.size() == 0) {
            return;
        }

        current--;
    }

    private void updateLabels() {
        JLabel[] labels = view.getLabels();

        labels[0].setText("" + getCurrentState().getWorking());
        labels[2].setText("" + getCurrentState().getMemory().content()[0][2]);
        labels[4].setText("" + getCurrentState().getMemory().content()[0][10]);
        labels[6].setText("" + getCurrentState().getCurrent());
        labels[8].setText("" + getCurrentState().getMemory().content()[0][0]);
        labels[10].setText("" + getCurrentState().getMemory().getRP1());
        labels[12].setText("" + getCurrentState().getMemory().getRP0());
        labels[14].setText("" + getCurrentState().getMemory().getZero());
        labels[16].setText("" + getCurrentState().getMemory().getDitgitCarry());
        labels[18].setText("" + getCurrentState().getMemory().getCarry());
        labels[20].setText("" + getCurrentState().getMemory().getT0CS());
        labels[22].setText("" + getCurrentState().getMemory().getPSA());
        labels[24].setText("" + getCurrentState().getMemory().getPS0());
        labels[26].setText("" + getCurrentState().getMemory().getPS1());
        labels[28].setText("" + getCurrentState().getMemory().getPS2());
    }

    private void updateButtons() {
        setButtons(view.getPortATRIS(), 0, true);
        setButtons(view.getPortBTRIS(), 1, true);
        setButtons(view.getPortAPins(), 0, false);
        setButtons(view.getPortBPins(), 1, false);
    }

    private void setButtons(JButton[] buttons, int port, boolean tris) {
        for (int i = 0; i < buttons.length; i++) {
            if (checkPortBit(port, tris, calculateBit(i))) {
                buttons[i].setText("1");
            } else {
                buttons[i].setText("0");
            }
        }
    }

    /**
     * Aktualisiert Befehls Liste, Highlighting der Liste, Button Verfuegbarkeit und Register Inhalte.
     */
    public void update() {

        if (states.size() > 0) {
            updateLabels();
        }

        //  Haben wir etwas das geladen werden kann ?
        if (states.size() == 0) {

            // Buttons inaktiv machen
            view.getButtons()[2].setEnabled(false);
            view.getButtons()[3].setEnabled(false);

            // Setze JList
            view.getList().setModel(new OperationModel());
            view.getList().updateUI();

            return;
        }

        // Update Button
        view.getButtons()[2].setEnabled(true);
        view.getButtons()[3].setEnabled(true);

        if (current == 0) {
            view.getButtons()[3].setEnabled(false);
        }

        if (!states.get(current).hasNext() && !isJump(getCurrentState().getCounter().get(getCurrentState().getCurrent()).getInstruction())) {
            view.getButtons()[2].setEnabled(false);
        }

        // Highlighting von Ausgeawaehlten Befehl
        for (int i = 0; i < states.get(current).getCounter().size(); i++) {
            states.get(current).getCounter().get(i).setNext((i == states.get(current).getCurrent()));
        }

        // Setze Register
        updateLabels();
        updateButtons();

        // Setze JList
        view.getList().setModel(new OperationModel(states.get(current).getCounter()));
        view.getList().updateUI();

        // Stack overflow ?
        if (getCurrentState().getStack().isOverflow()) {
            view.warnOverflow();
            getCurrentState().getStack().setOverflow(false);
        }
    }

    private boolean isJump(Instruction inst) {
        if (inst == Instruction.GOTO || inst == Instruction.CALL || inst == Instruction.RETURN || inst == Instruction.RETFIE || inst == Instruction.RETLW) {
            return true;
        }
        return false;
    }

    /**
     * Setze internen Speicher zurueck.
     */
    private void reset() {
        states = new ArrayList<Worker>();
        update();
    }

    private Worker getCurrentState() {
        return states.get(current);
    }

    private void load(String url) {
        if (url != "") {
            Worker temp = new Worker();
            temp.feed(Parser.parseMultible(Parser.cut(Parser.load(url))));

            reset();
            states.add(temp);
        }
    }

    private String getURL() {
        return view.invokeFileChooser();
    }

    public static void main(String args[]) {
        Main manager = new Main();
        manager.start();
        manager.debug();
    }
}

