package org.chatapp.client;

import org.chatapp.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class ClientUI {
    private final JFrame frame;
    private final JTextField textField;
    private final JTextArea messageArea;
    private final JPanel userListArea;
    private final ButtonGroup userListButtons;
    private static Map<Integer, User> users;

    private final Client client;
    private final String serverAddress;

    public ClientUI(String serverAddress, Client client) {
        this.client = client;
        this.serverAddress = serverAddress;
        frame = new JFrame("Chatter");
        textField = new JTextField(50);
        messageArea = new JTextArea(16, 50);
        userListArea = new JPanel();
        userListButtons = new ButtonGroup();

        setupUI();
    }

    private void setupUI() {

        textField.setEditable(false);
        messageArea.setEditable(false);
        textField.setBounds(150, 410, 330, 30);
        textField.setColumns(10);
        messageArea.setBounds(70, 120, 530, 250);
        userListArea.setBounds(620, 160, 80, 350);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        frame.setLayout(null);

        // User Details Button
        JButton button = new JButton("Get User Details");
        button.setBounds(620, 90, 200,40);
        userListArea.add(button);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (User user: users.values()) {
                    client.out.println("/details " + " > ID: " + user.getId() + " , Name: " + user.getName() + " , IP Address: " + user.getAddress());
                }
            }
        });

        JLabel title = new JLabel("Welcome to ChatBox");
        title.setFont(new Font("Arial", Font.PLAIN, 30));
        title.setBounds(250, 30, 400,50);
        frame.getContentPane().add(title);

        JLabel lblNewLabel = new JLabel("Send Message:");
        lblNewLabel.setBounds(620, 130, 200,50);
        lblNewLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        frame.getContentPane().add(lblNewLabel);

        JLabel lblNewLabel_1 = new JLabel("Message:");
        lblNewLabel_1.setBounds(70, 100, 200, 16);
        frame.getContentPane().add(lblNewLabel_1);

        JLabel lblNewLabel_3 = new JLabel("Press enter to send message !..");
        lblNewLabel_3.setBounds(150, 390, 200, 16);
        frame.getContentPane().add(lblNewLabel_3);

        JLabel lblNewLabel_2 = new JLabel("Type Message");
        lblNewLabel_2.setBounds(50, 410, 200, 16);
        frame.getContentPane().add(lblNewLabel_2);

        frame.setLayout(null);
        frame.getContentPane().add(textField);
        frame.getContentPane().add(messageArea);
        frame.getContentPane().add(userListArea);
        frame.getContentPane().add(button);
        AddUserToPanel(new User(0, "All", "0.0.0.0"));
        userListButtons.getElements().nextElement().setSelected(true);


        frame.setSize(850, 550);

        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JRadioButton selectedButton = null;
                for (Component child : userListArea.getComponents())
                {

                    if (child instanceof JRadioButton)
                    {

                        JRadioButton button = (JRadioButton)child;
                        if (button.isSelected())
                        {
                            selectedButton = button;
                            break;
                        }
                    }
                }
                Integer selectedUserId = (Integer)selectedButton.getClientProperty("user_id");
                if (selectedUserId == 0)
                {
                    client.out.println(textField.getText());
                }
                else
                {
                    //Send a private message using the selected user's id.
                    client.out.println("/msg " + selectedUserId + " " + textField.getText());
                    messageArea.append(textField.getText() + "\n");
                }
                textField.setText("");
            }
        });
    }

    public String getName() {
        return JOptionPane.showInputDialog(
                frame,
                "Choose a screen name:",
                "Screen name selection",
                JOptionPane.PLAIN_MESSAGE
        );
    }

    public void displayMessage(String message) {
        messageArea.append(message + "\n");
    }

    public void AddUserToPanel(User user) {
        //Create the user entry.
        JRadioButton userEntry = new JRadioButton();
        userEntry.putClientProperty("user_id", user.getId());
        userEntry.setText(user.getName());

        userListButtons.add(userEntry);

        GridBagConstraints userEntryConstraints = new GridBagConstraints();
        userEntryConstraints.gridx = 0;
        userEntryConstraints.gridy = GetUsersUICount();
        userEntryConstraints.fill = GridBagConstraints.HORIZONTAL;
        userEntryConstraints.weightx = 1.0;

        userListArea.add(userEntry, userEntryConstraints);

        UpdateUserPanel();
    }

    public void UpdateUserPanel() {
        SwingUtilities.invokeLater(() -> {
            Object _filler = userListArea.getClientProperty("filler");
            if (!(_filler instanceof JLabel)) {
                _filler = new JLabel();
                userListArea.putClientProperty("filler", _filler);
            }
            JLabel filler = (JLabel) _filler;

            int childCount = GetUsersUICount();

            userListArea.remove(filler);

            GridBagConstraints userListFillerConstraints = new GridBagConstraints();
            userListFillerConstraints.gridx = 0;
            userListFillerConstraints.gridy = childCount;
            userListFillerConstraints.weighty = 1.0;

            userListArea.add(filler, userListFillerConstraints);

            userListArea.revalidate();
        });
    }
    public int GetUsersUICount() {
        int count = 0;
        for (Component child : userListArea.getComponents())
            if (child instanceof JRadioButton)
                count++;
        return count;
    }
    public void setNameAcceptedDetails(String value) {
        frame.setTitle("Chatter - " + value);
        textField.setEditable(true);
        frame.setVisible(true);
    }
    public void RemoveUserFromPanel(Integer id) {
        SwingUtilities.invokeLater(() -> {
            for (Component child : userListArea.getComponents()) {
                if (child instanceof JRadioButton) {
                    JRadioButton button = (JRadioButton) child;
                    if (button.getClientProperty("user_id").equals(id)) {
                        userListArea.remove(button);
                        userListButtons.remove(button);
                        UpdateUserPanel();
                    } else if (button.getClientProperty("user_id") == (Integer) 0) {
                        button.setSelected(true);
                    }
                }
            }
        });
    }

    public void handleFinally() {
        frame.setVisible(false);
        frame.dispose();
    }
}
