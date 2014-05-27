package com.insidion.anl.opdracht.twee.ui;

import com.insidion.anl.opdracht.twee.Controller;
import com.insidion.anl.opdracht.twee.enums.StatType;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mitchell on 5/25/2014.
 */
public class ManagerForm {
    private JTextField tfDirtyRead;
    private JTextField tfUnrepeatableRead;
    private JTextField tfPhantomRead;

    private JTextField tfStatDeadlock;
    private JTextField tfStatDirtyRead;
    private JTextField tfStatUnrepeatableRead;
    private JTextField tfStatPhantomRead;

    private JButton btAddPhantomRead;
    private JButton btAddDirtRead;
    private JButton btAddUnrepeatableRead;

    private JButton btRemoveDirtRead;
    private JButton btRemoveUnrepeatableRead;
    private JButton btRemovePhantomRead;

    private Map<StatType, JTextField> statTfMap = new HashMap<StatType, JTextField>();
    private Map<StatType, Integer> statMap = new HashMap<StatType, Integer>();


    private JPanel formMain;
    private Controller controller;

    public ManagerForm(Controller controller) {
        this.controller = controller;
        fillMaps();
        setOnclicks();
    }

    public static void main(String[] args) throws ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        Controller controller = new Controller();
        ManagerForm form = new ManagerForm(controller);
        controller.setWindow(form);
        JFrame frame = new JFrame("ManagerForm");
        frame.setContentPane(form.formMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public void reportOccurrence(StatType type) {
        // Update map value
        statMap.put(type, statMap.get(type) + 1);

        // Update text field
        statTfMap.get(type).setText(statMap.get(type).toString());
    }

    private void fillMaps() {
        for (StatType type : StatType.values()) {
            statMap.put(type, 0);
        }

        // Map stattypes to textfields
        statTfMap.put(StatType.Deadlock, tfStatDeadlock);
        statTfMap.put(StatType.DirtyRead, tfStatDirtyRead);
        statTfMap.put(StatType.PhantomRead, tfStatPhantomRead);
        statTfMap.put(StatType.UnrepeatableRead, tfStatUnrepeatableRead);
    }


    private void setOnclicks() {
        btAddDirtRead.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.increaseNumberOfThreads(StatType.DirtyRead);
                tfDirtyRead.setText(Integer.toString(Integer.parseInt(tfDirtyRead.getText()) + 1));
            }
        });
        btRemoveDirtRead.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.decreaseNumberOfThreads(StatType.DirtyRead);
                tfDirtyRead.setText(Integer.toString(Integer.parseInt(tfDirtyRead.getText()) - 1));
            }
        });
        btAddPhantomRead.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.increaseNumberOfThreads(StatType.PhantomRead);
                tfPhantomRead.setText(Integer.toString(Integer.parseInt(tfPhantomRead.getText()) + 1));
            }
        });
        btRemovePhantomRead.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.decreaseNumberOfThreads(StatType.PhantomRead);
                tfPhantomRead.setText(Integer.toString(Integer.parseInt(tfPhantomRead.getText()) - 1));
            }
        });

        btAddUnrepeatableRead.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.increaseNumberOfThreads(StatType.UnrepeatableRead);
                tfUnrepeatableRead.setText(Integer.toString(Integer.parseInt(tfUnrepeatableRead.getText()) + 1));
            }
        });
        btRemoveUnrepeatableRead.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.decreaseNumberOfThreads(StatType.UnrepeatableRead);
                tfUnrepeatableRead.setText(Integer.toString(Integer.parseInt(tfUnrepeatableRead.getText()) - 1));
            }
        });


    }
}
