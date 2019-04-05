package Controller;

import Model.Parser;
import Model.Worker;
import View.Primary;

import javax.swing.*;
import java.util.ArrayList;

public class Main {

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

        Worker peon = new Worker();
        Primary view = new Primary();
        view.initialize();
        manager.initialiseActionListeners(peon, view);

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
}

