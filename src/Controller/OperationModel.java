package Controller;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.ArrayList;
import java.util.Collection;

public class OperationModel extends ArrayList implements ListModel {

    public OperationModel() {
        super();
    }

    public OperationModel(Collection c) {
        super(c);
    }

    @Override
    public int getSize() {
        return size();
    }

    @Override
    public Object getElementAt(int index) {
        return get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {

    }

    @Override
    public void removeListDataListener(ListDataListener l) {

    }
}

