package Package7CLIENT;

import java.awt.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import javax.swing.*;

public class PrivateMessage extends JFrame
{
    private static final String FRAME_TITLE = "Диалог";
    private static final int FRAME_MINIMUM_WIDTH = 400;
    private static final int FRAME_MINIMUM_HEIGHT = 400;
    private static final int INCOMING_AREA_DEFAULT_ROWS = 10;
    private static final int OUTGOING_AREA_DEFAULT_ROWS = 5;
    private static final int MEDIUM_GAP = 10;
    private Integer DIALOG_PORT = 4568;
    private Integer RECIEVE_PORT;
    private final JTextArea textAreaIncoming;
    private final JTextArea textAreaOutgoing;
    private String senderName;
    private String searchName;


    public PrivateMessage(int port) throws IOException
    {
        super(FRAME_TITLE);
        setMinimumSize(new Dimension(FRAME_MINIMUM_WIDTH, FRAME_MINIMUM_HEIGHT));
        final Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - getWidth()) / 2, (kit.getScreenSize().height - getHeight()) / 2);
        textAreaIncoming = new JTextArea(INCOMING_AREA_DEFAULT_ROWS, 0);
        textAreaIncoming.setEditable(false);
        final JScrollPane scrollPaneIncoming = new JScrollPane(textAreaIncoming);
        textAreaOutgoing = new JTextArea(OUTGOING_AREA_DEFAULT_ROWS, 0);
        final JScrollPane scrollPaneOutgoing = new JScrollPane(textAreaOutgoing);
        final JPanel messagePanel = new JPanel();
        messagePanel.setBorder(BorderFactory.createTitledBorder("Сообщение"));
        final JButton sendButton = new JButton("Отправить");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try
                {
                    sendMessageDialog();
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                }
            }
        });

        RECIEVE_PORT = port;
        final ServerSocket recieveSocket = new ServerSocket(RECIEVE_PORT);
        final Socket[] rsocket = new Socket[1];

        final GroupLayout layout2 = new GroupLayout(messagePanel);
        messagePanel.setLayout(layout2);
        layout2.setHorizontalGroup(layout2.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout2.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(scrollPaneOutgoing)
                        .addComponent(sendButton))
                .addContainerGap());
        layout2.setVerticalGroup(layout2.createSequentialGroup()
                .addContainerGap()
                .addGap(MEDIUM_GAP)
                .addComponent(scrollPaneOutgoing)
                .addGap(MEDIUM_GAP)
                .addComponent(sendButton)
                .addContainerGap());

        final GroupLayout layout1 = new GroupLayout(getContentPane());
        setLayout(layout1);
        layout1.setHorizontalGroup(layout1.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout1.createParallelGroup()
                        .addComponent(scrollPaneIncoming)
                        .addComponent(messagePanel))
                .addContainerGap());
        layout1.setVerticalGroup(layout1.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPaneIncoming)
                .addGap(MEDIUM_GAP)
                .addComponent(messagePanel)
                .addContainerGap());

        new Thread(new Runnable()
        {

            String recievedMessage;
            String senderNameF;
            public void run()
            {
                try
                {
                    while (!Thread.interrupted())
                    {
                        rsocket[0] = recieveSocket.accept();
                        final DataInputStream in = new DataInputStream(rsocket[0].getInputStream());
                        recievedMessage = in.readUTF();
                        senderNameF = in.readUTF();
                        rsocket[0].close();
                        textAreaIncoming.append(senderNameF + " : " + recievedMessage + "\n");
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();

        this.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                try {
                    rsocket[0].close();
                    recieveSocket.close();
                } catch (IOException ex)
                {
                    ex.printStackTrace();
                }
            }
        });

    }

    void sendMessageDialog() throws IOException
    {
        final String message = textAreaOutgoing.getText();
        if (message.isEmpty())
        {
            JOptionPane.showMessageDialog(this, "Введите текст сообщения", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        final Socket dialogSocket = new Socket("127.0.0.1", DIALOG_PORT);
        final DataOutputStream outdialog = new DataOutputStream(dialogSocket.getOutputStream());
        outdialog.writeUTF(message);
        outdialog.writeUTF(senderName);
        outdialog.writeUTF(searchName);
        dialogSocket.close();
        textAreaIncoming.append(senderName + " : " + message + "\n");
        textAreaOutgoing.setText("");
    }

    void setSenderName(String rememberName)
    {
        this.senderName = rememberName;
    }

    void setSearchName(String sName)
    {
        this.searchName = sName;
    }

}
