package Controller;

import Model.Parser;
import Model.Worker;
import View.Primary;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class Main implements Observer {

    public static void main(String args[]) {


        //TODO: Interface zwischen Primary und Model ausarbeiten

        /*

        Parser parser = new Parser();
        ArrayList<String> commands = parser.cut(parser.load("./src/tests/raw/TPicSim1.LST"));

        for (int i = 0; i < commands.size(); i++) {
            System.out.println(commands.get(i));
        }
        */
        Main manager = new Main();
        manager.start();
    }


    private Worker peon;
    private Primary view;

    public void start(){

        peon = new Worker();
        view = new Primary();
        view.initialize();
        this.initialiseActionListeners(peon, view);

        // Mache GUI sichtbar
        view.start();
    }

    public void initialiseActionListeners(Worker peon, Primary view) {
        JButton buttons[] = view.getButtons();
        buttons[0].addActionListener( e -> {this.run(peon);});
        buttons[1].addActionListener( e -> {this.stop(peon);});
        buttons[2].addActionListener( e -> {this.step(peon);});
        buttons[3].addActionListener( e -> {this.reset(peon);});
        buttons[4].addActionListener( e -> {this.load(peon,view);});
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
        //System.out.println(view.invokeFileChooser());
        peon.feed(Parser.load(view.invokeFileChooser()));

        view.getList().setModel(new OperationModel(peon.getCounter()));
        //TODO: view.getList().addListSelectionListener(new SelectionListener());
    }

    public void update() {
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

    @Override
    public void update(Observable o, Object arg) {
        //TODO: Pruefen ob Nachricht wirklich von Worker kommt.
        //TODO: Unit Test bauen
        this.update();
    }
}

