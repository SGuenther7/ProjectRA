package Controller;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class SelectionListener implements ListSelectionListener {

    @Override
    public void valueChanged(ListSelectionEvent e) {
        //TODO: Aendere current in Worker zu selectionIndex
    }

    public void changeSelection(ListSelectionEvent e) {

        ListSelectionModel target = ((JList) e.getSource()).getSelectionModel();
        if(((JList) e.getSource()).getSelectedIndex() == -1) {
            target.addSelectionInterval(e.getFirstIndex(),e.getFirstIndex());
        }
        else {
            target.clearSelection();
        }
    }
}
