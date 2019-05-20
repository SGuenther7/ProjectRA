package Controller;

import Model.Parser;
import Model.Worker;
import View.Primary;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Main {
    private ArrayList<Worker> states;
    private Primary view;
    private int current = 0;

    private void debug() {
        this.load("/Users/akira/Projects/java/ProjectRa/src/tests/raw/TPicSim1.LST");
        /*

        Worker peon = new Worker();

        for(int i = 0 ; i < peon.getMemory().content()[0].length ; i++){
            //peon.getMemory().content()[0][i] = (int) ((Math.random() * 1000) + 1) & 0xFF;
            //peon.getMemory().content()[1][i] = (int) ((Math.random() * 1000) + 1) & 0xFF;
            peon.getMemory().content()[0][i] = 255;
            peon.getMemory().content()[1][i] = 255;
        }

        peon.getMemory().content()[0][10] = 1;
        peon.getMemory().content()[0][10] = 1;
        this.states.add(peon);
        this.current = 0;
         */
        this.update();
    }

    public void start() {
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
            this.run();
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

                invertPortBit(0, true, calculatePortBit(temp));
                updateButtons();
            });
        }

        // TRIS B
        buttons = view.getPortAPins();

        for (int i = 0; i < buttons.length; i++) {
            final int temp = i;
            buttons[i].addActionListener(e -> {
                if (states.size() == 0) {
                    // TODO: Fehlermeldung/Bitanweisung vorspeichern
                    return;
                }

                invertPortBit(0, false, calculatePortBit(temp));
                updateButtons();
            });
        }

        // Port A
        buttons = view.getPortBTRIS();

        for (int i = 0; i < buttons.length; i++) {
            final int temp = i;
            buttons[i].addActionListener(e -> {
                if (states.size() == 0) {
                    // TODO: Fehlermeldung/Bitanweisung vorspeichern
                    return;
                }

                invertPortBit(1, true, calculatePortBit(temp));
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

                invertPortBit(1, false, calculatePortBit(temp));
                updateButtons();
            });
        }

    }

    /**
     * Helfer Funktion um aus einem Index (0-7) ein Bit des Registers zu machen
     */
    private int calculatePortBit(int index) {
        return (int) Math.pow(2, (8 - index));
    }

    /**
     * Helfer Funktion um genau ein Bit in den Port Registern zu invertieren
     */
    private void invertPortBit(int port, boolean tris, int bit) {


        int register = 5 + port;
        int bank = 0;

        if (tris) {
            bank = 1;
        }

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

    private void run() {
        // TODO: Execute bis Ende, dann update()
        // TODO: in Thread auslagern
        // TODO: Benoetigt Auto-Breakpoint bei GOTO/CALL!
    }

    private void stop() {
        // TODO: Thread mit run() stoppen
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

        JButton buttons[] = view.getPortATRIS();
        for (int i = 0; i < buttons.length; i++) {
            if (checkPortBit(1, 5, (int) Math.pow(2, 8 - i))) {
                buttons[i].setText("1");
            } else {
                buttons[i].setText("0");
            }
        }

        buttons = view.getPortAPins();
        for (int i = 0; i < buttons.length; i++) {
            if (checkPortBit(0, 5, (int) Math.pow(2, 8 - i))) {
                buttons[i].setText("1");
            } else {
                buttons[i].setText("0");
            }
        }

        buttons = view.getPortBTRIS();
        for (int i = 0; i < buttons.length; i++) {
            if (checkPortBit(1, 6, (int) Math.pow(2, 8 - i))) {
                buttons[i].setText("1");
            } else {
                buttons[i].setText("0");
            }
        }

        buttons = view.getPortBPins();
        for (int i = 0; i < buttons.length; i++) {
            if (checkPortBit(0, 6, (int) Math.pow(2, 8 - i))) {
                buttons[i].setText("1");
            } else {
                buttons[i].setText("0");
            }
        }
    }

    /**
     * Helfer Funktion fuer das setzen der Ports
     */
    private boolean checkPortBit(int bank, int register, int bit) {
        if ((getCurrentState().getMemory().content()[bank][register] & bit) > 0) {
            return true;
        }
        return false;
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

        if (!states.get(current).hasNext()) {
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
        //manager.debug();
    }
}

