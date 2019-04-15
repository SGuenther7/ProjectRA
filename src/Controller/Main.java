package Controller;

import Model.Parser;
import Model.Worker;
import View.Primary;

import javax.swing.*;
import java.util.ArrayList;

public class Main {

    private ArrayList<Worker> states;
    private Primary view;
    private int current = 0;

    public void start() {

        states = new ArrayList<>(); // TODO: Max. groesse fuer Performance ?
        view = new Primary();
        view.initialize();
        this.initialiseActionListeners(view);

        // Mache GUI sichtbar
        view.start();
    }

    private void initialiseActionListeners(Primary view) {
        JButton buttons[] = view.getButtons();
        buttons[0].addActionListener(e -> {
            this.run();
        });
        buttons[1].addActionListener(e -> {
            this.stop();
        });
        buttons[2].addActionListener(e -> {
            this.forward();
        });
        buttons[3].addActionListener(e -> {
            this.back();
        });
        buttons[4].addActionListener(e -> {
            this.reset();
        });
        buttons[5].addActionListener(e -> {
            this.load();
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

        // TODO: Heuristic von current ueberdenken (Sprungbefehle)

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
            update(states, view, current);
        }
    }

    private void back() {
        if (current == 0 || states.get(current) == null) {
            return;
        }

        current--;
        update(states, view, current);
    }

    public void update(ArrayList<Worker> states, Primary view, int current) {

        if (states == null || states.size() == 0) {

            view.getButtons()[2].setEnabled(false);
            view.getButtons()[3].setEnabled(false);

            resetRegisters(view);

            // Setze JList
            view.getList().setModel(new OperationModel());
            view.getList().updateUI();

            return;
        }

        view.getButtons()[2].setEnabled(true);
        view.getButtons()[3].setEnabled(true);

        // Update Button
        if (current == 0) {
            view.getButtons()[3].setEnabled(false);
        }

        if (!states.get(current).hasNext()) {
            view.getButtons()[2].setEnabled(false);
        }

        // TODO: Brute-force von Markierung isNext()
        for (int i = 0; i < states.get(current).getCounter().size(); i++) {
            states.get(current).getCounter().get(i).setNext((i == states.get(current).getCurrent()));
        }

        // Setze Register
        JLabel registers[] = view.getRegisters();

        registers[0].setText(states.get(current).getMemory()[0] + "");
        registers[1].setText(states.get(current).getMemory()[1] + "");
        registers[2].setText(states.get(current).getMemory()[2] + "");
        registers[3].setText(states.get(current).getMemory()[3] + "");
        registers[4].setText(states.get(current).getMemory()[4] + "");
        registers[5].setText(states.get(current).getMemory()[5] + "");
        registers[6].setText(states.get(current).getMemory()[6] + "");
        registers[7].setText(states.get(current).getMemory()[8] + "");
        registers[7].setText(states.get(current).getMemory()[9] + "");
        registers[8].setText(states.get(current).getMemory()[10] + "");
        registers[11].setText(states.get(current).getWorking() + "");

        // Setze JList
        view.getList().setModel(new OperationModel(states.get(current).getCounter()));
        view.getList().updateUI();
    }

    private void resetRegisters(Primary view) {
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
    }

    private void reset() {
        states = new ArrayList<Worker>();
        current = 0;
        update(states, view, current);
    }

    private void load() {
        String url =  view.invokeFileChooser();

        if(url == "") return;
        Worker temp = new Worker();
        temp.feed(Parser.parseMultible(Parser.cut(Parser.load(url))));

        reset();
        states.add(temp);
        update(states, view, current);
    }

    public static void main(String args[]) {
        Main manager = new Main();
        manager.start();
    }
}

