package dev.kejonamc.gui;

import dev.kejonamc.options.FriendsList;
import dev.kejonamc.options.Purge;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class XFRGui extends JFrame {
    public final JTextArea textArea;
    public XFRGui() {

        this.setLayout(new GridLayout(2, 4));
        this.setPreferredSize(new Dimension(830,500));
        this.setResizable(false);
        this.setLocation(100, 100);
        this.setTitle("Xbox Friend Remover");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.pack();

        textArea = new JTextArea(24,70);
        textArea.setEditable(false);
        textArea.setBackground(Color.LIGHT_GRAY);

        JScrollPane scrollTextArea = new JScrollPane(textArea);
        scrollTextArea.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        // Buttons
        JButton purgeButton = new JButton("Purge");
        purgeButton.addActionListener(e -> new Purge().purgeFriends());

        JButton friendListButton = new JButton("Friends List");
        friendListButton.addActionListener(e -> {
            FriendsList friendsList = new FriendsList();
            friendsList.friendsUpdater();
        });

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));

        // Main Container.
        Container mainContainer = this.getContentPane();
        mainContainer.setLayout(new BorderLayout(8, 2));
        mainContainer.setBackground(Color.WHITE);
        this.getRootPane().setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.GRAY));

        // Bottom panel.
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(new LineBorder(Color.DARK_GRAY, 3));
        bottomPanel.setBackground(Color.LIGHT_GRAY);
        bottomPanel.setLayout(new FlowLayout(5));
        mainContainer.add(bottomPanel, BorderLayout.SOUTH);

        bottomPanel.add(friendListButton);
        bottomPanel.add(purgeButton);
        bottomPanel.add(exitButton);

        // Center panel.
        JPanel centerPanel = new JPanel();
        centerPanel.add(scrollTextArea);
        centerPanel.setBackground(Color.LIGHT_GRAY);
        centerPanel.setBorder(new LineBorder(Color.DARK_GRAY, 3));

        mainContainer.add(centerPanel);

        this.setVisible(true);
    }
}