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
        debug();
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
            this.load(states.get(current), view);
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

        // Execute current command
        // Increment current
        // Save result in states


        // TODO: Heuristic von current ueberdenken (Sprungbefehle)

        if (current >= states.get(current).getCounter().size() - 1) {
            // TODO: Anzeigen das (rechtes) Ende erreicht wurde
            return;
        }

        // Haben wir den naechsten State schon ?
        if(current+2 <= states.size()) {
            current++;
        } else {
            states.add(new Worker(states.get(current)));
            current++;
            states.get(current).next();
        }

        update();
    }

    private void back() {
        if (current == 0) {
            // TODO: Anzeigen das (linkes) Ende erreicht wurde
            return;
        }

        current--;
        update();
    }

    private void reset() {
        //TODO:
    }

    public void update() {

        if (states.get(current) == null) {
            return;
        }

        System.out.println();
        // Print currents von jedem state
        for(Worker peon : states) {
            System.out.println(peon.getCurrent());
        }
        System.out.println();

        // DEBUG: Korregiere alle isNext
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

    private void load(Worker peon, Primary view) {
        reset();
        states.get(current).feed(Parser.parseMultible(Parser.cut(Parser.load(view.invokeFileChooser()))));
        update();
    }

    private void debug() {
        // TODO: Entfernen
        Worker temp = new Worker();
        temp.feed(Parser.parseMultible(Parser.cut(Parser.load("/Users/akira/Projects/java/ProjectRa/src/tests/raw/TPicSim1.LST"))));
        states.add(temp); //TODO: Aedern (reset bei nicht 0 eintragen ??)
        current = 0;
        update();
    }

    public static void main(String args[]) {
        Main manager = new Main();
        manager.start();
    }

}

