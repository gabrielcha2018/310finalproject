import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.text.DefaultCaret;


public class GUI {
  JFrame frame = new JFrame("VR support");
  JPanel panel = new JPanel();
  JLabel label = new JLabel("Enter Text");
  
  JTextArea textHistory = new JTextArea();
  JTextField userinput = new JTextField(20);

  JButton send = new JButton("Send");
  JButton reset = new JButton("Reset");
  
  Chatbot chatbot;

  public GUI(Chatbot chatbot) {
    this.chatbot = chatbot;

    JScrollPane scroll_bar = new JScrollPane(textHistory, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(800, 600);
    frame.getContentPane().add(BorderLayout.SOUTH, panel);
    frame.getContentPane().add(BorderLayout.CENTER, scroll_bar);
    frame.setVisible(true);

    panel.add(label); 
    panel.add(userinput);
    panel.add(send);
    panel.add(reset);

    // Text history area modifiers
    textHistory.setEditable(false); // text area is not editable
    textHistory.setBackground(new Color(174,207,214));
    textHistory.setLineWrap(true);
    textHistory.setWrapStyleWord(true);

    DefaultCaret caret = (DefaultCaret) textHistory.getCaret();
    caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

    // Call relevant functions when key/button is pressed
    send.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        onUserInput();
      }
    });
    userinput.addKeyListener(new KeyListener() {
      @Override
      public void keyTyped(KeyEvent e) {
      }

      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == '\n') {
          onUserInput();
        }
      }

      @Override
      public void keyReleased(KeyEvent e) {
      }
    });
    reset.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        userinput.setText("");
        chatbot.reset();
      }
    });
  }

  public void sendMessage(String message) {
    textHistory.append("AGENT: " + message + "\n");
    userinput.setEditable(true);
    userinput.setText("");
  }

  void onUserInput() {
    String temp = userinput.getText();
    if (!temp.isBlank()) {
      String lastInput = userinput.getText();
      textHistory.append("YOU: " + lastInput + "\n");
      userinput.setEditable(false);
      userinput.setText("Agent typing...");
      new Thread(() -> { chatbot.processInput(lastInput); }).start();
    }
  }

}