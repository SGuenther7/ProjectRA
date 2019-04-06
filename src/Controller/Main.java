package Controller;

import Model.Parser;
import Model.Worker;
import View.Primary;

import javax.swing.*;
import java.util.Observable;
import java.util.Observer;

public class Main implements Observer {
    private Worker peon;
    private Primary view;


    public void start() {

        peon = new Worker();
        view = new Primary();
        view.initialize();
        this.initialiseActionListeners(peon, view);
        peon.addObserver(this);

        // Mache GUI sichtbar
        view.start();
    }

    public void initialiseActionListeners(Worker peon, Primary view) {
        JButton buttons[] = view.getButtons();
        buttons[0].addActionListener(e -> {
            this.run(peon);
        });
        buttons[1].addActionListener(e -> {
            this.stop(peon);
        });
        buttons[2].addActionListener(e -> {
            this.step(peon);
        });
        buttons[3].addActionListener(e -> {
            this.reset(peon);
        });
        buttons[4].addActionListener(e -> {
            this.load(peon, view);
        });
    }

    public void run(Worker peon) {
    }

    public void stop(Worker peon) {
    }

    public void step(Worker peon) {
    }

    public void reset(Worker peon) {
    }

    public void load(Worker peon, Primary view) {
        peon.feed(Parser.parse(Parser.cut(Parser.load(view.invokeFileChooser()))));

        view.getList().setModel(new OperationModel(peon.getCounter()));
        //TODO: view.getList().addListSelectionListener(new SelectionListener());
    }

    @Override
    public void update(Observable o, Object arg) {
        // TODO: Nachricht von Worker ? Dependency unschoen!

        JLabel registers[] = this.view.getRegisters();

        registers[0].setText(this.peon.getMemory()[0] + "");
        registers[1].setText(this.peon.getMemory()[1] + "");
        registers[2].setText(this.peon.getMemory()[2] + "");
        registers[3].setText(this.peon.getMemory()[3] + "");
        registers[4].setText(this.peon.getMemory()[4] + "");
        registers[5].setText(this.peon.getMemory()[5] + "");
        registers[6].setText(this.peon.getMemory()[6] + "");
        registers[7].setText(this.peon.getMemory()[8] + "");
        registers[7].setText(this.peon.getMemory()[9] + "");
        registers[8].setText(this.peon.getMemory()[10] + "");
        registers[11].setText(this.peon.getWorking() + "");
    }

    public static void main(String args[]) {
        Main manager = new Main();
        manager.start();
    }

}

