package View;

import Model.Command;

import javax.swing.*;
import java.awt.*;

public class OperationList extends JCheckBox implements ListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        /*
        setComponentOrientation(list.getComponentOrientation());
        setFont(list.getFont());
        setBackground(list.getBackground());
        setForeground(list.getForeground());

        Command current = (Command) value;


        setSelected(current.getSelected());

        if(current.getRunning()) {
            this.setBackground(Color.BLUE);
        }
        else {
            this.setBackground(Color.WHITE);
        }


        setEnabled(list.isEnabled());

        if(value == null) {
            setText("");
        }
        else {
            setText(value.toString());
        }

        */
        return this;
    }
}
