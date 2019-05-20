package View;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Primary {

    private JFrame main;
    private JPanel buttons;
    private JPanel operations;
    private JPanel registers;

    private JPanel ports;
    private JPanel portA;
    private JPanel portB;

    private JPanel systemPanel;
    private JPanel statusPanel;
    private JPanel optionPanel;

    private JScrollPane instructionView;

    private JButton run;
    private JButton stop;
    private JButton forward;
    private JButton back;
    private JButton reset;
    private JButton load;
    private JButton watchdog;

    private JLabel WorkingContent;
    private JLabel WorkingLabel;
    private JLabel PCLContent;
    private JLabel PCLLabel;
    private JLabel PCLATHContent;
    private JLabel PCLATHLabel;
    private JLabel PCContent;
    private JLabel PCLabel;
    private JLabel INDFContent;
    private JLabel INDFLabel;
    private JLabel RP1Content;
    private JLabel RP1Label;
    private JLabel RP0Content;
    private JLabel RP0Label;
    private JLabel ZContent;
    private JLabel ZLabel;
    private JLabel DCContent;
    private JLabel DCLabel;
    private JLabel CContent;
    private JLabel CLabel;
    private JLabel TOCSContent;
    private JLabel TOCSLabel;
    private JLabel PSAContent;
    private JLabel PSALabel;
    private JLabel PS0Content;
    private JLabel PS0Label;
    private JLabel PS1Content;
    private JLabel PS1Label;
    private JLabel PS2Content;
    private JLabel PS2Label;

    // Port A
    private JLabel portALabel;
    private JLabel portATrisLabel;
    private JButton portATrisBit0;
    private JButton portATrisBit1;
    private JButton portATrisBit2;
    private JButton portATrisBit3;
    private JButton portATrisBit4;
    private JButton portATrisBit5;
    private JButton portATrisBit6;
    private JButton portATrisBit7;

    private JLabel portAPinLabel;
    private JButton portAPinBit0;
    private JButton portAPinBit1;
    private JButton portAPinBit2;
    private JButton portAPinBit3;
    private JButton portAPinBit4;
    private JButton portAPinBit5;
    private JButton portAPinBit6;
    private JButton portAPinBit7;

    // Port B
    private JLabel portBLabel;
    private JLabel portBTrisLabel;
    private JButton portBTrisBit0;
    private JButton portBTrisBit1;
    private JButton portBTrisBit2;
    private JButton portBTrisBit3;
    private JButton portBTrisBit4;
    private JButton portBTrisBit5;
    private JButton portBTrisBit6;
    private JButton portBTrisBit7;

    private JLabel portBPinLabel;
    private JButton portBPinBit0;
    private JButton portBPinBit1;
    private JButton portBPinBit2;
    private JButton portBPinBit3;
    private JButton portBPinBit4;
    private JButton portBPinBit5;
    private JButton portBPinBit6;
    private JButton portBPinBit7;

    // TODO: TRIS und PORT (A und B) in externen Feld (mit Klickbarkeit)

    private JLabel status;
    private JList instructions;

    public void initialize() {

        main = new JFrame();
        main.setLayout(null);
        main.setLocationRelativeTo(null);

        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main.setSize(new Dimension(640, 480));

        initializePanels();

        initializeButtons();
        initializeRegisters();

        portALabel = new JLabel("Port A");
        portBLabel = new JLabel("Port B");

        initializePorts();

        //Inhalt von Code Panel
        instructionView = new JScrollPane();
        instructionView.setBounds(10, 10, 300, 378);
        instructionView.setBackground(new Color(200, 200, 200));

        status = new JLabel();
        status.setBounds(14, 392, 300, 16);

        status.setBackground(Color.BLUE);
        status.setForeground(Color.GRAY);
        operations.add(status);

        instructions = new JList();
        instructions.setBackground(new Color(200, 200, 200));

        instructions.setCellRenderer(new OperationList());
        instructionView.setViewportView(instructions);
        operations.add(instructionView);

        setBounds();

        // TODO: Richtig in Code platzieren
        registers.add(portALabel);
        registers.add(portBLabel);
        portALabel.setBounds(0,100,100,15);
        portBLabel.setBounds(155,100,100,15);

        main.setResizable(false);
        main.setVisible(false);
    }

    public void start() {
        main.setVisible(true);
    }

    private void initializeRegisters() {

        initializeSystemLabels();
        initializeStatusLabels();
        initializeOptionLabels();
    }

    private void initializeSystemLabels() {
        WorkingLabel = new JLabel("W");
        PCLLabel = new JLabel("PCL");
        PCLATHLabel = new JLabel("PCLATH");
        PCLabel = new JLabel("PC");
        INDFLabel = new JLabel("INDF");

        WorkingContent = new JLabel("0");
        PCLContent = new JLabel("0");
        PCLATHContent = new JLabel("0");
        PCContent = new JLabel("0");
        INDFContent = new JLabel("0");

        systemPanel.add(WorkingContent);
        systemPanel.add(WorkingLabel);
        systemPanel.add(PCLContent);
        systemPanel.add(PCLLabel);
        systemPanel.add(PCLATHContent);
        systemPanel.add(PCLATHLabel);
        systemPanel.add(PCContent);
        systemPanel.add(PCLabel);
        systemPanel.add(INDFContent);
        systemPanel.add(INDFLabel);
    }

    private void initializeStatusLabels() {
        RP1Content = new JLabel("0");
        RP1Label = new JLabel("RP1");
        RP0Content = new JLabel("0");
        RP0Label = new JLabel("RP0");
        ZContent = new JLabel("0");
        ZLabel = new JLabel("Z");
        DCContent = new JLabel("0");
        DCLabel = new JLabel("DC");
        CContent = new JLabel("0");
        CLabel = new JLabel("C");

        statusPanel.add(RP1Content);
        statusPanel.add(RP1Label);
        statusPanel.add(RP0Content);
        statusPanel.add(RP0Label);
        statusPanel.add(ZContent);
        statusPanel.add(ZLabel);
        statusPanel.add(DCContent);
        statusPanel.add(DCLabel);
        statusPanel.add(CContent);
        statusPanel.add(CLabel);
    }

    private void initializeOptionLabels() {
        TOCSContent = new JLabel("0");
        TOCSLabel = new JLabel("TOCS");
        PSAContent = new JLabel("0");
        PSALabel = new JLabel("PSA");
        PS0Content = new JLabel("0");
        PS0Label = new JLabel("PS0");
        PS1Content = new JLabel("0");
        PS1Label = new JLabel("PS1");
        PS2Content = new JLabel("0");
        PS2Label = new JLabel("PS2");

        optionPanel.add(TOCSContent);
        optionPanel.add(TOCSLabel);
        optionPanel.add(PSAContent);
        optionPanel.add(PSALabel);
        optionPanel.add(PS0Content);
        optionPanel.add(PS0Label);
        optionPanel.add(PS1Content);
        optionPanel.add(PS1Label);
        optionPanel.add(PS2Content);
        optionPanel.add(PS2Label);
    }

    private void initializePorts() {
        initializePortA();
        initializePortB();
    }

    private void initializePortA() {
        portATrisBit0 = new JButton("0");
        portATrisBit1 = new JButton("0");
        portATrisBit2 = new JButton("0");
        portATrisBit3 = new JButton("0");
        portATrisBit4 = new JButton("0");
        portATrisBit5 = new JButton("0");
        portATrisBit6 = new JButton("0");
        portATrisBit7 = new JButton("0");
        portAPinBit0 = new JButton("0");
        portAPinBit1 = new JButton("0");
        portAPinBit2 = new JButton("0");
        portAPinBit3 = new JButton("0");
        portAPinBit4 = new JButton("0");
        portAPinBit5 = new JButton("0");
        portAPinBit6 = new JButton("0");
        portAPinBit7 = new JButton("0");


        portATrisLabel = new JLabel("Tris");
        portA.add(portATrisLabel);

        for (JButton trisBit : getPortATRIS()) {
            portA.add(trisBit);
        }

        portAPinLabel = new JLabel("Pin");
        portA.add(portAPinLabel);

        for (JButton pins : getPortAPins()) {
            portA.add(pins);
        }
    }

    private void initializePortB() {
        portBTrisBit0 = new JButton("0");
        portBTrisBit1 = new JButton("0");
        portBTrisBit2 = new JButton("0");
        portBTrisBit3 = new JButton("0");
        portBTrisBit4 = new JButton("0");
        portBTrisBit5 = new JButton("0");
        portBTrisBit6 = new JButton("0");
        portBTrisBit7 = new JButton("0");
        portBPinBit0 = new JButton("0");
        portBPinBit1 = new JButton("0");
        portBPinBit2 = new JButton("0");
        portBPinBit3 = new JButton("0");
        portBPinBit4 = new JButton("0");
        portBPinBit5 = new JButton("0");
        portBPinBit6 = new JButton("0");
        portBPinBit7 = new JButton("0");


        portBTrisLabel = new JLabel("Tris");
        portB.add(portBTrisLabel);

        for (JButton trisBit : getPortBTRIS()) {
            portB.add(trisBit);
        }

        portBPinLabel = new JLabel("Pin");
        portB.add(portBPinLabel);

        for (JButton pins : getPortBPins()) {
            portB.add(pins);
        }
    }

    private void setBounds() {
        setBoundsRegister();
        setBoundsPanels();
        setBoundsPorts();
    }

    private void setBoundsPorts() {
        setBoundsPortA();
        setBoundsPortB();
    }

    private void setBoundsPortA() {
        portATrisLabel.setBounds(0, 0, 25, 15);

        portATrisBit0.setBounds(30, 0, 15, 15);
        portATrisBit1.setBounds(45, 0, 15, 15);
        portATrisBit2.setBounds(60, 0, 15, 15);
        portATrisBit3.setBounds(75, 0, 15, 15);
        portATrisBit4.setBounds(90, 0, 15, 15);
        portATrisBit5.setBounds(105, 0, 15, 15);
        portATrisBit6.setBounds(120, 0, 15, 15);
        portATrisBit7.setBounds(135, 0, 15, 15);

        portAPinLabel.setBounds(0, 20, 25, 15);

        portAPinBit0.setBounds(30, 20, 15, 15);
        portAPinBit1.setBounds(45, 20, 15, 15);
        portAPinBit2.setBounds(60, 20, 15, 15);
        portAPinBit3.setBounds(75, 20, 15, 15);
        portAPinBit4.setBounds(90, 20, 15, 15);
        portAPinBit5.setBounds(105, 20, 15, 15);
        portAPinBit6.setBounds(120, 20, 15, 15);
        portAPinBit7.setBounds(135, 20, 15, 15);
    }

    private void setBoundsPortB() {
        portBTrisLabel.setBounds(0, 0, 25, 15);

        portBTrisBit0.setBounds(30, 0, 15, 15);
        portBTrisBit1.setBounds(45, 0, 15, 15);
        portBTrisBit2.setBounds(60, 0, 15, 15);
        portBTrisBit3.setBounds(75, 0, 15, 15);
        portBTrisBit4.setBounds(90, 0, 15, 15);
        portBTrisBit5.setBounds(105, 0, 15, 15);
        portBTrisBit6.setBounds(120, 0, 15, 15);
        portBTrisBit7.setBounds(135, 0, 15, 15);

        portBPinLabel.setBounds(0, 20, 25, 15);

        portBPinBit0.setBounds(30, 20, 15, 15);
        portBPinBit1.setBounds(45, 20, 15, 15);
        portBPinBit2.setBounds(60, 20, 15, 15);
        portBPinBit3.setBounds(75, 20, 15, 15);
        portBPinBit4.setBounds(90, 20, 15, 15);
        portBPinBit5.setBounds(105, 20, 15, 15);
        portBPinBit6.setBounds(120, 20, 15, 15);
        portBPinBit7.setBounds(135, 20, 15, 15);
    }

    private void setBoundsPanels() {
        buttons.setBounds(0, 0, 640, 40);
        operations.setBounds(0, 40, 320, 440);
        registers.setBounds(320, 40, 320, 440);
        ports.setBounds(0, 120, 305, 40);
        portA.setBounds(0, 0, 150, 40);
        portB.setBounds(155, 0, 150, 40);
        systemPanel.setBounds(0, 0, 100, 100);
        statusPanel.setBounds(105, 0, 190, 35);
        optionPanel.setBounds(105, 40, 190, 35);
    }

    private void setBoundsRegister() {
        setBoundsSystemPanel();
        setBoundsStatusPanel();
        setBoundsOptionPanel();
    }

    private void setBoundsSystemPanel() {
        WorkingLabel.setBounds(0, 0, 15, 15);
        WorkingContent.setBounds(75, 0, 60, 15);
        PCLLabel.setBounds(0, 20, 45, 15);
        PCLContent.setBounds(75, 20, 60, 15);
        PCLATHLabel.setBounds(0, 40, 60, 15);
        PCLATHContent.setBounds(75, 40, 60, 15);
        PCLabel.setBounds(0, 60, 30, 15);
        PCContent.setBounds(75, 60, 90, 15);
        INDFLabel.setBounds(0, 80, 60, 15);
        INDFContent.setBounds(75, 80, 60, 15);
    }

    private void setBoundsStatusPanel() {
        RP1Label.setBounds(0, 0, 40, 15);
        RP1Content.setBounds(0, 20, 15, 15);
        RP0Label.setBounds(40, 0, 40, 15);
        RP0Content.setBounds(40, 20, 15, 15);
        ZLabel.setBounds(80, 0, 40, 15);
        ZContent.setBounds(80, 20, 15, 15);
        DCLabel.setBounds(120, 0, 40, 15);
        DCContent.setBounds(120, 20, 15, 15);
        CLabel.setBounds(160, 0, 40, 15);
        CContent.setBounds(160, 20, 15, 15);
    }

    private void setBoundsOptionPanel() {
        TOCSLabel.setBounds(0, 0, 40, 15);
        TOCSContent.setBounds(0, 20, 15, 15);
        PSALabel.setBounds(40, 0, 40, 15);
        PSAContent.setBounds(40, 20, 15, 15);
        PS0Label.setBounds(80, 0, 40, 15);
        PS0Content.setBounds(80, 20, 15, 15);
        PS1Label.setBounds(120, 0, 40, 15);
        PS1Content.setBounds(120, 20, 15, 15);
        PS2Label.setBounds(160, 0, 40, 15);
        PS2Content.setBounds(160, 20, 15, 15);
    }

    private void initializePanels() {

        buttons = new JPanel(null);
        operations = new JPanel(null);
        registers = new JPanel(null);
        ports = new JPanel(null);

        portA = new JPanel(null);
        portB = new JPanel(null);

        systemPanel = new JPanel(null);
        statusPanel = new JPanel(null);
        optionPanel = new JPanel(null);

        buttons.setBackground(new Color(200, 200, 200));
        operations.setBackground(new Color(200, 200, 200));
        registers.setBackground(new Color(200, 200, 200));

        ports.setBackground(new Color(200, 200, 200));
        portA.setBackground(new Color(200, 200, 200));
        portB.setBackground(new Color(200, 200, 200));

        statusPanel.setBackground(new Color(200, 200, 200));
        optionPanel.setBackground(new Color(200, 200, 200));
        systemPanel.setBackground(new Color(200, 200, 200));

        registers.add(systemPanel);
        registers.add(statusPanel);
        registers.add(optionPanel);


        ports.add(portA);
        ports.add(portB);
        registers.add(ports);

        main.getContentPane().add(buttons);
        main.getContentPane().add(operations);
        main.getContentPane().add(registers);
    }

    private void initializeButtons() {
        run = new JButton("Run");
        stop = new JButton("Stop");
        forward = new JButton("Forward");
        back = new JButton("Backwards");
        reset = new JButton("Reset");
        load = new JButton("Load...");

        try {
            BufferedImage idleWTD = ImageIO.read(new File("./src/img/idle.png"));
            BufferedImage enabledWTD = ImageIO.read(new File("./src/img/enabled.png"));
            watchdog = new JButton(new ImageIcon(idleWTD.getScaledInstance(24,24,Image.SCALE_SMOOTH)));
            watchdog.setBorder(BorderFactory.createEmptyBorder());
            watchdog.setContentAreaFilled(false);

            watchdog.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // TODO: WTD Button verhalten impl.
                    watchdog.setIcon(new ImageIcon(enabledWTD.getScaledInstance(24,24,Image.SCALE_SMOOTH)));
                    super.mouseClicked(e);
                }
            });
        } catch (IOException e) {
            System.out.println(e.getMessage());
            watchdog = new JButton("WDT");
        }

        run.setBounds(10, 5, 90, 30);
        stop.setBounds(90, 5, 90, 30);
        forward.setBounds(185, 5, 90, 30);
        back.setBounds(265, 5, 90, 30);
        reset.setBounds(370, 5, 90, 30);
        load.setBounds(450, 5, 90, 30);
        watchdog.setBounds(530, 5, 90, 30);

        forward.setEnabled(false);
        back.setEnabled(false);

        buttons.add(run);
        buttons.add(stop);
        buttons.add(forward);
        buttons.add(back);
        buttons.add(reset);
        buttons.add(load);
        buttons.add(watchdog);
    }

    public JList getList() {
        return instructions;
    }

    public JLabel[] getLabels() {
        return new JLabel[]{
                WorkingContent,
                WorkingLabel,
                PCLContent,
                PCLLabel,
                PCLATHContent,
                PCLATHLabel,
                PCContent,
                PCLabel,
                INDFContent,
                INDFLabel,
                RP1Content,
                RP1Label,
                RP0Content,
                RP0Label,
                ZContent,
                ZLabel,
                DCContent,
                DCLabel,
                CContent,
                CLabel,
                TOCSContent,
                TOCSLabel,
                PSAContent,
                PSALabel,
                PS0Content,
                PS0Label,
                PS1Content,
                PS1Label,
                PS2Content,
                PS2Label
        };
    }

    public void warnOverflow() {

        JOptionPane.showMessageDialog(main,"A Stack overflow has taken place.");

    }

    // Mache Buttons von aussen verfügbar um ActionListener hinzuzufuegen
    public JButton[] getButtons() {
        return new JButton[]{run, stop, forward, back, reset, load};
    }

    public JButton[] getPortATRIS() {
        return new JButton[]{portATrisBit0, portATrisBit1, portATrisBit2, portATrisBit3, portATrisBit4, portATrisBit5, portATrisBit6, portATrisBit7};
    }

    public JButton[] getPortAPins() {
        return new JButton[]{portAPinBit0, portAPinBit1, portAPinBit2, portAPinBit3, portAPinBit4, portAPinBit5, portAPinBit6, portAPinBit7};
    }

    public JButton[] getPortBTRIS() {
        return new JButton[]{portBTrisBit0, portBTrisBit1, portBTrisBit2, portBTrisBit3, portBTrisBit4, portBTrisBit5, portBTrisBit6, portBTrisBit7};
    }

    public JButton[] getPortBPins() {
        return new JButton[]{portBPinBit0, portBPinBit1, portBPinBit2, portBPinBit3, portBPinBit4, portBPinBit5, portBPinBit6, portBPinBit7};
    }

    public JList getJList() {
        return instructions;
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

    public JLabel getStatus() {
        return status;
    }
}
