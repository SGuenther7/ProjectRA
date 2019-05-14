package Controller;

import Model.Parser;
import Model.Port;
import Model.Worker;
import View.Primary;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Main {
    private ArrayList<Worker> states; // TODO: Groesse einschraenken (performance).
    private Primary view;
    private int current = 0;

    public void start() {
        reset();

        view = new Primary();
        view.initialize();
        this.initialiseActionListeners(view);
        update();

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

    /**
     * Aktualisiert Befehls Liste, Highlighting der Liste, Button Verfuegbarkeit und Register Inhalte.
     */
    public void update() {
        //  Haben wir etwas das geladen werden kann ?
        if (states.size() == 0) {

            // Buttons inaktiv machen
            view.getButtons()[2].setEnabled(false);
            view.getButtons()[3].setEnabled(false);

            JLabel registers[] = view.getRegisters();

            registers[0].setText("0");
            registers[1].setText("0");
            registers[2].setText("0");
            registers[3].setText("0");
            registers[4].setText("0");
            registers[5].setText("0");
            registers[6].setText("0");
            registers[7].setText("0");
            registers[7].setText("0");
            registers[8].setText("0");
            registers[11].setText("0");

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
        JLabel registers[] = view.getRegisters();

        // TODO: Richtige Register aussuchen
        for(int i = 0 ; i < 12 ; i++) {

            registers[i].setText(states.get(current).getMemory().get(states.get(current).getBank(),i) + "");
        }

        registers[11].setText(states.get(current).getWorking() + "");

        // Setze JList
        view.getList().setModel(new OperationModel(states.get(current).getCounter()));
        view.getList().updateUI();
    }

    /**
     * Setze internen Speicher zurueck.
     */
    private void reset() {
        states = new ArrayList<Worker>();
        current = 0;
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
        manager.load("/Users/akira/Projects/java/ProjectRa/src/tests/raw/TPicSim1.LST");
    }
}

