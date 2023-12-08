/*
 * AutoOL
 * Copyright (C) 2022  bigfoot547 <olauncher@figboot.dev>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://github.com/olauncher/autool .
 */

package dev.figboot.autool.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SwingProgressUpdater extends JFrame implements ProgressUpdater {
    private JLabel statusLabel;
    private JProgressBar progressBar;

    private boolean cancelOp;

    public SwingProgressUpdater() {
        initInterface();
        cancelOp = false;
    }

    private void initInterface() {
        JPanel mainPanel = new JPanel();
        setContentPane(mainPanel);

        setTitle("Minecraft Redistributable");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setAlwaysOnTop(true);

        Box mainBox = Box.createVerticalBox();
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.add(mainBox);

        statusLabel = new JLabel("Please wait...");
        Box lblBox = Box.createHorizontalBox();
        lblBox.add(statusLabel);
        lblBox.add(Box.createHorizontalGlue());
        mainBox.add(lblBox);

        progressBar = new JProgressBar(0, 1);
        progressBar.setPreferredSize(new Dimension(300, progressBar.getMinimumSize().height));
        mainBox.add(progressBar);
        mainBox.add(Box.createVerticalStrut(5));

        Box boxCancelButton = Box.createHorizontalBox();
        JButton cancelButton = new JButton("Cancel");
        boxCancelButton.add(Box.createHorizontalGlue());
        boxCancelButton.add(cancelButton);
        mainBox.add(boxCancelButton);

        cancelButton.addActionListener((evt) -> {
            cancelOp = true;
            cancelButton.setEnabled(false);
        });

        pack();
        setLocationByPlatform(true);
        setLocationRelativeTo(null);
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cancelOp = true;
            }
        });
    }

    @Override
    public void changeStatus(String status) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(status);
            progressBar.setValue(0);
        });
    }

    @Override
    public void setMaxProgress(int max) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setMaximum(max);
            updateProgressBarText();
        });
    }

    @Override
    public void setProgress(int progress) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setValue(progress);
            updateProgressBarText();
        });
    }

    private void updateProgressBarText() {
        progressBar.setStringPainted(true);
        progressBar.setString(String.format("%d%%", progressBar.getValue() * 100 / progressBar.getMaximum()));
    }

    @Override
    public boolean cancelOperation() {
        return cancelOp;
    }

    @Override
    public void shutdown() {
        dispose();
    }

    @Override
    public void error(Object message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
