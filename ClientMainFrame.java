package Package7CLIENT;

import java.awt.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import javax.swing.*;

public class ClientMainFrame extends JFrame {
    private static final String FRAME_TITLE = "Клиент мгновенных сообщений";
    private static final int FRAME_MINIMUM_WIDTH = 500;
    private static final int FRAME_MINIMUM_HEIGHT = 500;
    private static final int TO_FIELD_DEFAULT_COLUMNS = 20;
    private static final int INCOMING_AREA_DEFAULT_ROWS = 10;
    private static final int OUTGOING_AREA_DEFAULT_ROWS = 5;
    private static final int SMALL_GAP = 5;
    private static final int MEDIUM_GAP = 10;
    private static Integer SERVER_PORT;
    private Integer USER_PORT;
    private final JTextField textFieldSearch;
    private final JTextArea textAreaIncoming;
    private final JTextArea textAreaOutgoing;
    private boolean error;
    private boolean closed = false;
    private boolean conversation = false;
    private String searchName;
    private String senderName;

    public ClientMainFrame() throws IOException
    {
        super(FRAME_TITLE);
        setMinimumSize(new Dimension(FRAME_MINIMUM_WIDTH, FRAME_MINIMUM_HEIGHT));
        final Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - getWidth()) / 2, (kit.getScreenSize().height - getHeight()) / 2);
        textAreaIncoming = new JTextArea(INCOMING_AREA_DEFAULT_ROWS, 0);
        textAreaIncoming.setEditable(false);
        final JScrollPane scrollPaneIncoming = new JScrollPane(textAreaIncoming);
        final JLabel labelTo = new JLabel("Поиск");
        textFieldSearch = new JTextField(TO_FIELD_DEFAULT_COLUMNS);
        textAreaOutgoing = new JTextArea(OUTGOING_AREA_DEFAULT_ROWS, 0);
        final JScrollPane scrollPaneOutgoing = new JScrollPane(textAreaOutgoing);
        final JPanel messagePanel = new JPanel();
        messagePanel.setBorder(BorderFactory.createTitledBorder("Сообщение"));
        final JButton sendButton = new JButton("Отправить");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });


        final GroupLayout layout2 = new GroupLayout(messagePanel);
        messagePanel.setLayout(layout2);
        layout2.setHorizontalGroup(layout2.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout2.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addGroup(layout2.createSequentialGroup()
                                .addGap(SMALL_GAP)
                                .addComponent(labelTo)
                                .addGap(SMALL_GAP)
                                .addComponent(textFieldSearch))
                        .addComponent(scrollPaneOutgoing)
                        .addComponent(sendButton))
                .addContainerGap());
        layout2.setVerticalGroup(layout2.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout2.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelTo)
                        .addComponent(textFieldSearch))
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

        this.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                closed = true;
                sendState();
            }
        });

        final Socket portsocket = new Socket("127.0.0.1", 4567);
        final DataInputStream inport = new DataInputStream(portsocket.getInputStream());
        SERVER_PORT = Integer.parseInt(inport.readUTF());
        portsocket.close();

        while(true)
        {
            final Socket socket = new Socket("127.0.0.1", SERVER_PORT);
            final DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            final DataInputStream in = new DataInputStream(socket.getInputStream());

            String m = JOptionPane.showInputDialog(ClientMainFrame.this , "Для получения доступа введите пароль", "Окно безопасности", JOptionPane.INFORMATION_MESSAGE);
            if (m == null)
            {
                m = "error/qwerty111";
                out.writeUTF(m);
                socket.close();
                System.exit(0);
            }
            else
            {
                out.writeUTF(m);
            }
            error = in.readBoolean();
            if (error == false)
            {
                USER_PORT = Integer.parseInt(in.readUTF());
                senderName = JOptionPane.showInputDialog(ClientMainFrame.this, "Введите логин", "Окно инициализации", JOptionPane.INFORMATION_MESSAGE);
                if (senderName == null)
                {
                    senderName = "error/qwerty111";
                    out.writeUTF(senderName);
                    socket.close();
                    System.exit(0);
                }
                else
                {
                    out.writeUTF(senderName);
                }
                socket.close();
                break;
            }
            else
            {
                socket.close();
            }
        }

        final ServerSocket userServer = new ServerSocket(USER_PORT);

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {

                    while (!Thread.interrupted())
                    {
                        final Socket rsocket = userServer.accept();
                        final DataInputStream in = new DataInputStream(rsocket.getInputStream());
                        final String senderNameF;
                        final String message;
                        final boolean cnv;
                        cnv = in.readBoolean();
                        senderNameF = in.readUTF();
                        message = in.readUTF();
                        rsocket.close();
                        if (cnv == false)
                        {
                            textAreaIncoming.append(senderNameF + " : " + message + "\n");
                        }
                        else
                        {
                            if (searchName.equals(""))
                            {
                                searchName = senderNameF;
                            }
                            conversation = cnv;
                        }
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {

                    while (!Thread.interrupted())
                    {
                        if (conversation == true)
                        {
                            PrivateMessage DialogFrame = new PrivateMessage(USER_PORT + 1000);
                            DialogFrame.setSenderName(senderName);
                            DialogFrame.setSearchName(searchName);
                            DialogFrame.setVisible(true);
                            conversation = false;
                            searchName = "";
                        }
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void sendMessage()
    {
        try
        {

            searchName = textFieldSearch.getText();
            final String message = textAreaOutgoing.getText();
            final Integer state;

            if (message.isEmpty())
            {
                JOptionPane.showMessageDialog(this, "Введите текст сообщения", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            final Socket socket = new Socket("127.0.0.1", SERVER_PORT);
            final DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            final DataInputStream in = new DataInputStream(socket.getInputStream());

            out.writeBoolean(closed);
            out.writeUTF(message);
            if (!searchName.isEmpty())
            {
                state = 1;
                textFieldSearch.setText("");
            } else {
                state = 0;
            }
            out.writeUTF(state.toString());
            if (state == 1)
            {
                out.writeUTF(searchName);
            }
            conversation = in.readBoolean();
            socket.close();
            textAreaOutgoing.setText("");
        } catch (UnknownHostException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(ClientMainFrame.this, "Не удалось отправить сообщение: узел-адресат не найден", "Ошибка", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(ClientMainFrame.this, "Не удалось отправить сообщение", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    void sendState()
    {
        try
        {

            final Socket socket = new Socket("127.0.0.1", SERVER_PORT);
            final DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeBoolean(closed);
            socket.close();

        } catch (UnknownHostException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(ClientMainFrame.this, "Не удалось отправить сообщение: узел-адресат не найден", "Ошибка", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(ClientMainFrame.this, "Не удалось отправить сообщение", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                final ClientMainFrame frame;
                try {
                    frame = new ClientMainFrame();
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setVisible(true);
                    frame.closed = false;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}