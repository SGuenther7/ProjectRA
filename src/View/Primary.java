package View;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;

public class Primary {

    private JFrame main;
    private JPanel buttons;
    private JPanel operations;
    private JPanel registers;

    private JScrollPane instructionView;

    private JButton run;
    private JButton stop;
    private JButton step;
    private JButton reset;
    private JButton load;

    private JLabel indf_content;
    private JLabel tmr_content;
    private JLabel pcl_content;
    private JLabel status_content;
    private JLabel fsr_content;
    private JLabel porta_content;
    private JLabel portb_content;
    private JLabel eedatea_content;
    private JLabel eeadr_content;
    private JLabel pclath_content;
    private JLabel incton_content;
    private JLabel working_content;

    private JLabel indf_label;
    private JLabel tmr_label;
    private JLabel pcl_label;
    private JLabel status_label;
    private JLabel fsr_label;
    private JLabel porta_label;
    private JLabel portb_label;
    private JLabel eedatea_label;
    private JLabel eeadr_label;
    private JLabel pclath_label;
    private JLabel incton_label;
    private JLabel working_label;
    private JLabel cycles_name_label;
    private JLabel cycles_content_label;
    private JLabel speed_name_label;
    private JLabel speed_min_label;
    private JLabel speed_max_label;

    private JSlider speed;
    private JList instructions;

    public void initialize() {

        main = new JFrame();
        main.setLayout(null);

        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main.setSize(new Dimension(640, 480));

        initializePanels();
        initializeButtons();
        initializeRegisters();


        //Inhalt von Code Panel
        instructionView = new JScrollPane();
        instructionView.setBounds(10, 10, 300, 400);
        instructionView.setBackground(new Color(200, 200, 200));

        instructions = new JList();
        instructions.setBackground(new Color(200, 200, 200));

        instructions.setCellRenderer(new OperationList());
        instructionView.setViewportView(instructions);
        operations.add(instructionView);

        this.setBounds();
        //main.pack();
        main.setResizable(false);
        main.setVisible(false);
    }

    public void start() {

        main.setVisible(true);
    }

    private void initializeRegisters() {

        indf_content = new JLabel();
        tmr_content = new JLabel();
        pcl_content = new JLabel();
        status_content = new JLabel();
        fsr_content = new JLabel();
        porta_content = new JLabel();
        portb_content = new JLabel();
        eedatea_content = new JLabel();
        eeadr_content = new JLabel();
        pclath_content = new JLabel();
        incton_content = new JLabel();
        working_content = new JLabel();


        // TODO: Entfernen. GUI debug.
        indf_content.setText("0");
        tmr_content.setText("0");
        pcl_content.setText("0");
        status_content.setText("0");
        fsr_content.setText("0");
        porta_content.setText("0");
        portb_content.setText("0");
        eedatea_content.setText("0");
        eeadr_content.setText("0");
        pclath_content.setText("0");
        incton_content.setText("0");
        working_content.setText("0");

        indf_label = new JLabel("INDF :");
        tmr_label = new JLabel("TMR :");
        pcl_label = new JLabel("PCL :");
        status_label = new JLabel("STATUS :");
        fsr_label = new JLabel("FSR :");
        porta_label = new JLabel("PORTA :");
        portb_label = new JLabel("PORT B:");
        eedatea_label = new JLabel("EEDATEA :");
        eeadr_label = new JLabel("EEADR :");
        pclath_label = new JLabel("PCLATH :");
        incton_label = new JLabel("INCTON :");
        working_label = new JLabel("WORKING :");
        cycles_name_label = new JLabel("Cycles : ");
        cycles_content_label = new JLabel("0");

        //TODO:
        speed = new JSlider(0, 5);

        speed_name_label = new JLabel("Speed : ");
        speed_min_label = new JLabel("");
        speed_max_label = new JLabel("");

        speed_max_label.setForeground(Color.RED);

        registers.add(indf_label);
        registers.add(indf_content);

        registers.add(tmr_label);
        registers.add(tmr_content);

        registers.add(pcl_label);
        registers.add(pcl_content);

        registers.add(status_label);
        registers.add(status_content);

        registers.add(fsr_label);
        registers.add(fsr_content);

        registers.add(porta_label);
        registers.add(porta_content);

        registers.add(portb_label);
        registers.add(portb_content);

        registers.add(eedatea_label);
        registers.add(eedatea_content);

        registers.add(eeadr_label);
        registers.add(eeadr_content);

        registers.add(pclath_label);
        registers.add(pclath_content);

        registers.add(incton_label);
        registers.add(incton_content);

        registers.add(working_label);
        registers.add(working_content);

        registers.add(cycles_name_label);
        registers.add(cycles_content_label);

        registers.add(speed);
        registers.add(speed_name_label);
        registers.add(speed_min_label);
        registers.add(speed_max_label);
    }

    private void setBounds() {
        indf_label.setBounds(10, 10, 130, 14);
        indf_content.setBounds(140, 10, 130, 14);

        tmr_label.setBounds(10, 29, 130, 14);
        tmr_content.setBounds(140, 29, 130, 14);

        pcl_label.setBounds(10, 48, 130, 14);
        pcl_content.setBounds(140, 48, 130, 14);

        status_label.setBounds(10, 67, 130, 14);
        status_content.setBounds(140, 67, 130, 14);

        fsr_label.setBounds(10, 86, 130, 14);
        fsr_content.setBounds(140, 86, 130, 14);

        porta_label.setBounds(10, 105, 130, 14);
        porta_content.setBounds(140, 105, 130, 14);

        portb_label.setBounds(10, 124, 130, 14);
        portb_content.setBounds(140, 124, 130, 14);

        eedatea_label.setBounds(10, 143, 130, 14);
        eedatea_content.setBounds(140, 143, 130, 14);

        eeadr_label.setBounds(10, 162, 130, 14);
        eeadr_content.setBounds(140, 162, 130, 14);

        pclath_label.setBounds(10, 181, 130, 14);
        pclath_content.setBounds(140, 181, 130, 14);


        incton_label.setBounds(10, 200, 130, 14);
        incton_content.setBounds(140, 200, 130, 14);

        working_label.setBounds(10, 219, 130, 14);
        working_content.setBounds(140, 219, 130, 14);

        cycles_name_label.setBounds(10, 238, 130, 14);
        cycles_content_label.setBounds(145, 238, 130, 14);

        speed.setBounds(140, 270, 130, 14);
        speed_name_label.setBounds(10, 270, 130, 14);
        speed_min_label.setBounds(130, 270, 30, 14);
        speed_max_label.setBounds(280, 270, 30, 14);

        buttons.setBounds(0, 0, 640, 40);
        operations.setBounds(0, 40, 320, 440);
        registers.setBounds(320, 40, 320, 440);

    }

    private void initializePanels() {

        buttons = new JPanel(null);
        operations = new JPanel(null);
        registers = new JPanel(null);

        buttons.setBackground(new Color(200, 200, 200));
        operations.setBackground(new Color(200, 200, 200));
        registers.setBackground(new Color(200, 200, 200));

        main.getContentPane().add(buttons);
        main.getContentPane().add(operations);
        main.getContentPane().add(registers);
    }

    private void initializeButtons() {
        run = new JButton("Run");
        stop = new JButton("Stop");
        step = new JButton("Step");
        reset = new JButton("Reset");
        load = new JButton("Load...");

        run.setBounds(10, 5, 90, 30);
        stop.setBounds(110, 5, 90, 30);
        step.setBounds(210, 5, 90, 30);
        reset.setBounds(310, 5, 90, 30);
        load.setBounds(410, 5, 90, 30);

        buttons.add(run);
        buttons.add(stop);
        buttons.add(step);
        buttons.add(reset);
        buttons.add(load);
    }

    public JList getList() {
        return instructions;
    }

    public JLabel[] getRegisters() {
        return new JLabel[]{
                indf_content,
                tmr_content,
                pcl_content,
                status_content,
                fsr_content,
                porta_content,
                portb_content,
                eedatea_content,
                eeadr_content,
                pclath_content,
                incton_content,
                working_content
        };
    }

    // Mache Buttons von aussen verfügbar um ActionListener hinzuzufuegen
    public JButton[] getButtons() {
        return new JButton[]{this.run, this.stop, this.step, this.reset, this.load};
    }

    public String invokeFileChooser() {
        JFileChooser files = new JFileChooser();

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                ".LST", "LST", "lst");
        files.setFileFilter(filter);

        int selectedFile = files.showOpenDialog(null);

        if (selectedFile == JFileChooser.CANCEL_OPTION) {
            return "";
        }

        if (selectedFile == JFileChooser.APPROVE_OPTION) {
            System.out.println("Selected file : " + files.getSelectedFile().getName());
        }

        return files.getSelectedFile().getAbsolutePath();
    }
}
