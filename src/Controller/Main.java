package Controller;

import Model.Instruction;
import Model.Parser;
import Model.Worker;
import View.Primary;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    private ArrayList<Worker> states;
    private Primary view;
    private int current = 0;
    private Thread runner = null;
    private boolean wdtEnabled = false;

    private void debug() {
        this.load("/Users/akira/Projects/java/ProjectRa/src/tests/raw/TPicSim6.LST");
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
            startThread();
        });
        buttons[1].addActionListener(e -> {
            this.interrupt();
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

        // WDT Button
        buttons[6].addActionListener(e -> {
            wdtEnabled = !wdtEnabled;
            view.toggleWDTButtonImage();
        });

        // JList
        view.getJList().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                JList list = (JList) e.getSource();
                int index = list.locationToIndex(e.getPoint());
                try {
                    states.get(current).getCounter().get(index).toggleBreakpoint();
                    view.getList().repaint();
                } catch (IndexOutOfBoundsException b) {
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
                boolean internal = checkBit(getCurrentState().getPortB().get(), bit);
                boolean port = checkPortBit(0, false, bit);

                if (port != internal) {
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
                boolean internal = checkBit(getCurrentState().getPortB().get(), bit);
                boolean port = checkPortBit(1, false, bit);

                if (port != internal) {
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
                    getCurrentState().getPortA().add(calculateBit(temp));
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
                    getCurrentState().getPortB().add(calculateBit(temp));
                }
            });
        }
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

    private void startThread() {
        if (runner != null) {
            return;
        }

        runner = new Thread() {
            public void run() {
                Main.this.run();
            }
        };

        runner.start();
    }

    private void run() {
        // Ist eine .LST Datei geladen ?
        if (states.size() == 0) {
            return;
        }

        // Waerend nicht am Ende
        while (!runner.interrupted() && getCurrentState().hasNext()) {
            if (getCurrentState().isBreakpoint()) {
                resetThread();
                return;
            }

            forward();
            update();

            try {
                runner.sleep(1000); // TODO: Zeit einstellbar ?
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                runner = null;
            }
        }
    }

    private void interrupt() {
        if (runner != null) {
            runner.interrupt();
        }
    }

    private void resetThread() {
        runner.interrupt();
        runner = null;
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

            // WDT uebernehmen
            getCurrentState().getTimer().wdtEnabled = wdtEnabled;
        }
    }

    private void back() {
        if (current == 0 || states.size() == 0) {
            return;
        }

        current--;
    }

    private void updateLabels(Worker current) {
        JLabel[] labels = view.getLabels();

        labels[0].setText("" + current.getWorking());
        labels[2].setText("" + current.getMemory().content()[0][2]);
        labels[4].setText("" + current.getMemory().content()[0][10]);
        labels[6].setText("" + current.getCurrent());
        labels[8].setText("" + current.getMemory().content()[0][0]);
        labels[10].setText("" + current.getMemory().getRP1());
        labels[12].setText("" + current.getMemory().getRP0());
        labels[14].setText("" + current.getMemory().getZero());
        labels[16].setText("" + current.getMemory().getDitgitCarry());
        labels[18].setText("" + current.getMemory().getCarry());
        labels[20].setText("" + current.getMemory().getT0CS());
        labels[22].setText("" + current.getMemory().getPSA());
        labels[24].setText("" + current.getMemory().getPS0());
        labels[26].setText("" + current.getMemory().getPS1());
        labels[28].setText("" + current.getMemory().getPS2());

        updateStatus(current);
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

    private void updateStatus(Worker current) {
        String cycles = "Cycles : " + current.getCycles();
        String status = getStackContent(current);
        view.getStatus().setText(cycles + "   " + status);
    }

    private String getStackContent(Worker current) {
        String status = "";
        for (int i = 0; i < current.getStack().size(); i++) {
            status += "" + current.getStack().elementAt(i).toString();

            if (i < current.getStack().size() - 1) {
                status += " > ";
            }
        }

        return status;
    }

    private void updateMemTable(Worker current) {
        JTable bank0 = view.getBank0Table();
        JTable bank1 = view.getBank1Table();

        int content[][] = new int[2][47];

        if (states.size() > 0) {
            content = current.getMemory().content();
        } else {
            for (int i = 0; i < 2; i++) {
                for (int o = 0; o < 47; o++) {
                    content[i][o] = 0;
                }
            }
        }

        for (int i = 0; i < 47; i++) {
            for (int o = 0; o < 8; o++) {
                // TODO: Mit calcBit tauschen
                int bank0Bit = getSingleBit(content[0][i], o);
                int bank1Bit = 0;

                switch (i) {
                    case 1:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                        bank1Bit = getSingleBit(content[1][i], o);
                    default:
                        bank1Bit = getSingleBit(content[0][i], o);
                }

                bank0.setValueAt(bank0Bit, i, 7 - o);
                bank1.setValueAt(bank1Bit, i, 7 - o);
            }
        }
    }

    private int getSingleBit(int whole, int bit) {
        return (whole & (int) Math.pow(2, bit)) > 0 ? 1 : 0;
    }

    /**
     * Aktualisiert GUI Elemente mit jetzigem Worker Object (Befehls Liste, Highlighting der Liste, Button Verfuegbarkeit und Register Inhalte ...)
     */
    public void update() {
        //  Haben wir etwas das geladen werden kann ?
        if (states.size() == 0) {
            Worker temp = new Worker();

            // Buttons inaktiv machen
            view.getButtons()[2].setEnabled(false);
            view.getButtons()[3].setEnabled(false);

            // Setze JList
            view.getList().setModel(new OperationModel());
            view.getList().updateUI();

            updateLabels(temp);
            updateMemTable(temp);

            return;
        }

        updateLabels(getCurrentState());
        updateMemTable(getCurrentState());

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
        updateLabels(getCurrentState());
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

    /**
     * Ist Instruction ein Jump Befehl ?
     */
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
        current = 0;
        wdtEnabled = false;
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

