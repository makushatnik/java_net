package main;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Ageev Evgeny on 25.03.2016.
 */
public class FilesTableModel extends AbstractTableModel implements Observer {
    private static final String[] columnNames = {"File","Size","Progress","Status"};

    private static final Class[] columnClasses = {String.class, String.class,
            JProgressBar.class, String.class,
    };

    private ArrayList<Download> filesList = new ArrayList<>();

    public void addDownload(Download download) {
        download.addObserver(this);
        filesList.add(download);

        fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
    }

    public Download getDownload(int row) {
        return (Download) filesList.get(row);
    }

    public void clearDownload(int row) {
        filesList.remove(row);
        this.fireTableRowsDeleted(row, row);
    }

    public void clearAll() {
        int size = filesList.size();
        filesList.removeAll(filesList);
        this.fireTableRowsDeleted(0, size);
    }

    @Override
    public int getRowCount() {
        return filesList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Class getColumnClass(int col) {
        return columnClasses[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        Download download = (Download) filesList.get(row);
        switch(col) {
            case 0:
                //return download.getUrl();
                return download.getFname();
            case 1:
                long size = download.getSize();
                return (size == -1) ? "" : Long.toString(size);
            case 2:
                return new Float(download.getProgress());
            case 3:
                return Download.STATUSES[download.getStatus()];
        }
        return "";
    }

    @Override
    public void update(Observable o, Object arg) {
        int idx = filesList.indexOf(o);

        fireTableRowsUpdated(idx, idx);
    }
}
