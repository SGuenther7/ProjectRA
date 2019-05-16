package View;

import Model.Command;

import javax.swing.*;
import java.awt.*;

public class OperationList extends JCheckBox implements ListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        Command current = (Command) value;
        System.out.println();

        if(current.isNext()) {
            this.setBackground(Color.BLUE);
        }
        else {
            this.setBackground(Color.WHITE);
        }
        setSelected(current.isBreakpoint());

        setEnabled(list.isEnabled());
        setText(current.getInstruction().toString());

        return this;
    }
}
