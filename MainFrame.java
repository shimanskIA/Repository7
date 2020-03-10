package Package7;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import javax.swing.*;

@SuppressWarnings("serial")
public class MainFrame extends JFrame
{
    private static final String FRAME_TITLE = "Клиент мгновенных сообщений";
    private static final int FRAME_MINIMUM_WIDTH = 500;
    private static final int FRAME_MINIMUM_HEIGHT = 500;
    private static final int FROM_FIELD_DEFAULT_COLUMNS = 10;
    private static final int TO_FIELD_DEFAULT_COLUMNS = 20;
    private static final int INCOMING_AREA_DEFAULT_ROWS = 10;
    private static final int OUTGOING_AREA_DEFAULT_ROWS = 5;
    private static final int SMALL_GAP = 5;
    private static final int MEDIUM_GAP = 10;
    private static final int LARGE_GAP = 15;
    private static final int SERVER_PORT = 4567;
    private final JTextField textFieldFrom;
    private final JTextField textFieldSearch;
    private final JTextArea textAreaIncoming;
    private final JTextArea textAreaOutgoing;
    private static String rememberName = "";

    public MainFrame()
    {
        super(FRAME_TITLE);
        setMinimumSize(new Dimension(FRAME_MINIMUM_WIDTH, FRAME_MINIMUM_HEIGHT));
        final Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - getWidth()) / 2, (kit.getScreenSize().height - getHeight()) / 2);
        textAreaIncoming = new JTextArea(INCOMING_AREA_DEFAULT_ROWS, 0);
        textAreaIncoming.setEditable(false);
        final JScrollPane scrollPaneIncoming = new JScrollPane(textAreaIncoming);
        final JLabel labelFrom = new JLabel("Подпись");
        final JLabel labelTo = new JLabel("Поиск");
        textFieldFrom = new JTextField(FROM_FIELD_DEFAULT_COLUMNS);
        textFieldSearch = new JTextField(TO_FIELD_DEFAULT_COLUMNS);
        textAreaOutgoing = new JTextArea(OUTGOING_AREA_DEFAULT_ROWS, 0);
        final JScrollPane scrollPaneOutgoing = new JScrollPane(textAreaOutgoing);
        final JPanel messagePanel = new JPanel();
        messagePanel.setBorder(BorderFactory.createTitledBorder("Сообщение"));
        final JButton sendButton = new JButton("Отправить");
        sendButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                sendMessage();
            }
        });



        final GroupLayout layout2 = new GroupLayout(messagePanel);
        messagePanel.setLayout(layout2);
        layout2.setHorizontalGroup(layout2.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout2.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addGroup(layout2.createSequentialGroup()
                                .addComponent(labelFrom)
                                .addGap(SMALL_GAP)
                                .addComponent(textFieldFrom)
                                .addGap(LARGE_GAP)
                                .addComponent(labelTo)
                                .addGap(SMALL_GAP)
                                .addComponent(textFieldSearch))
                        .addComponent(scrollPaneOutgoing)
                        .addComponent(sendButton))
                .addContainerGap());
        layout2.setVerticalGroup(layout2.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout2.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelFrom)
                        .addComponent(textFieldFrom)
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

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    final ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
                    final String password = "12345678";
                    ArrayList<String> Names = new ArrayList();
                    while(true)
                    {
                        String m = JOptionPane.showInputDialog(MainFrame.this , "Для получения доступа введите пароль", "Окно безопасности", JOptionPane.INFORMATION_MESSAGE);
                        if (m == null)
                        {
                            System.exit(0);
                        }
                        if (m.equals(password))
                        {
                            break;
                        }

                    }
                    while (!Thread.interrupted())
                    {
                        final Socket socket = serverSocket.accept();
                        final DataInputStream in = new DataInputStream(socket.getInputStream());
                        final String senderName = in.readUTF();
                        if (!Names.contains(senderName) && !Names.contains(senderName + "qwerty111"))
                        {
                            Names.add(senderName);
                        }
                        final String message = in.readUTF();
                        final String searchName;
                        int state = Integer.parseInt(in.readUTF());
                        if (state == 1)
                        {
                            searchName = in.readUTF();
                        }
                        else
                        {
                            searchName = "";
                        }

                        class Frame extends JFrame
                        {
                            final JTextArea textAreaIncoming2;
                            final JTextArea textAreaOutgoing2;

                            private Frame()
                            {
                                super("Приватный чат с " + searchName);
                                setMinimumSize(new Dimension(FRAME_MINIMUM_WIDTH, FRAME_MINIMUM_HEIGHT));
                                final Toolkit kit2 = Toolkit.getDefaultToolkit();
                                setLocation((kit2.getScreenSize().width - getWidth()) / 2, (kit2.getScreenSize().height - getHeight()) / 2);
                                textAreaIncoming2 = new JTextArea(INCOMING_AREA_DEFAULT_ROWS, 0);
                                textAreaIncoming2.setEditable(false);
                                final JScrollPane scrollPaneIncoming2 = new JScrollPane(textAreaIncoming2);
                                textAreaOutgoing2 = new JTextArea(OUTGOING_AREA_DEFAULT_ROWS, 0);
                                final JScrollPane scrollPaneOutgoing2 = new JScrollPane(textAreaOutgoing2);
                                final JPanel messagePanel2 = new JPanel();
                                messagePanel2.setBorder(BorderFactory.createTitledBorder("Сообщение"));
                                final JButton sendButton2 = new JButton("Отправить 2");
                                sendButton2.addActionListener(new ActionListener()
                                {
                                    @Override
                                    public void actionPerformed(ActionEvent e2)
                                    {
                                        sendMessage2();
                                    }
                                });

                                final GroupLayout layout22 = new GroupLayout(messagePanel2);
                                messagePanel2.setLayout(layout22);
                                layout22.setHorizontalGroup(layout22.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(layout22.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                .addComponent(scrollPaneOutgoing2)
                                                .addComponent(sendButton2))
                                        .addContainerGap());
                                layout22.setVerticalGroup(layout22.createSequentialGroup()
                                        .addContainerGap()
                                        .addGap(MEDIUM_GAP)
                                        .addComponent(scrollPaneOutgoing2)
                                        .addGap(MEDIUM_GAP)
                                        .addComponent(sendButton2)
                                        .addContainerGap());

                                final GroupLayout layout12 = new GroupLayout(getContentPane());
                                setLayout(layout12);
                                layout12.setHorizontalGroup(layout12.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(layout12.createParallelGroup()
                                                .addComponent(scrollPaneIncoming2)
                                                .addComponent(messagePanel2))
                                        .addContainerGap());
                                layout12.setVerticalGroup(layout12.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(scrollPaneIncoming2)
                                        .addGap(MEDIUM_GAP)
                                        .addComponent(messagePanel2)
                                        .addContainerGap());
                            }

                            private void sendMessage2()
                            {
                                final String message2 = textAreaOutgoing2.getText();
                                textAreaIncoming2.append(senderName + " : " + message2 + "\n");
                                textAreaOutgoing2.setText("");
                            }
                        }
                        Frame frame2 = new Frame();

                        if (!searchName.equals(""))
                        {
                            for (int i = 0; i < Names.size(); i++)
                            {
                                if (searchName.equals(Names.get(i)))
                                {
                                    Names.add(searchName + "qwerty111");
                                    Names.remove(i);
                                    frame2.setVisible(true);
                                    frame2.textAreaIncoming2.append(senderName + " : " + message + "\n");
                                    break;
                                }
                            }
                        }
                        /*final String address = ((InetSocketAddress) socket
                                .getRemoteSocketAddress())
                                .getAddress()
                                .getHostAddress();*/
                        socket.close();
                        if (state != 1)
                        {
                            textAreaIncoming.append(senderName + " : " + message + "\n");
                        }
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(MainFrame.this, "Ошибка в работе сервера", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        }).start();
    }

    private void sendMessage()
    {
        try
        {

            final String searchName = textFieldSearch.getText();
            final String message = textAreaOutgoing.getText();
            final String senderName;
            final Integer state;
            if (MainFrame.rememberName.isEmpty())
            {
                senderName = textFieldFrom.getText();
                rememberName = senderName;
            }
            else
            {
                senderName = MainFrame.rememberName;
            }
            if (senderName.isEmpty())
            {
                JOptionPane.showMessageDialog(this, "Введите имя отправителя", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (message.isEmpty())
            {
                JOptionPane.showMessageDialog(this, "Введите текст сообщения", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            final Socket socket = new Socket("127.0.0.1", SERVER_PORT);
            final DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(senderName);
            out.writeUTF(message);
            if (!searchName.isEmpty())
            {
                state = 1;
                textFieldSearch.setText("");
            }
            else
            {
                state = 0;
            }
            out.writeUTF(state.toString());
           if (state == 1)
           {
               out.writeUTF(searchName);
           }
            socket.close();
            textAreaOutgoing.setText("");
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(MainFrame.this, "Не удалось отправить сообщение: узел-адресат не найден", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(MainFrame.this, "Не удалось отправить сообщение", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                final MainFrame frame = new MainFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }
}

