package Package7SERVER;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import javax.swing.*;

public class ServerApp
{
    private String password = "12345678";
    private static int TRANSPORT_PORT = 4567;
    private static int DIALOG_PORT = 4568;
    private static ArrayList<Boolean> Ports = new ArrayList(100);
    private static ArrayList<Boolean> ServerPorts = new ArrayList(100);
    private static LinkedHashMap<Integer, String> Connections = new LinkedHashMap<Integer, String>();
    private static ArrayList<Thread> Threads = new ArrayList(100);
    final ServerSocket transportSocket = new ServerSocket(TRANSPORT_PORT);
    final ServerSocket dialogSocket = new ServerSocket(DIALOG_PORT);


    ServerApp() throws IOException
    {
        for (int i = 0; i < 100; i++)
        {
            Ports.add(false);
        }
        for (int i = 0; i < 100; i++)
        {
            ServerPorts.add(false);
        }
        for (int i = 0; i < 100; i++)
        {
            Thread A = new Thread(threadFactory(i));
            Threads.add(A);
        }
        for (int i = 0; i < 100; i++)
        {
            Threads.get(i).start();
        }

        new Thread(new Runnable()
        {
            public void run()
            {
                try {
                    while (!Thread.interrupted()) {
                        final Socket outputSocket = transportSocket.accept();
                        final DataOutputStream outport = new DataOutputStream(outputSocket.getOutputStream());
                        for (int i = 0; i < 100; i++)
                        {
                            if (ServerPorts.get(i) == false)
                            {
                                Integer help = i + 50000;
                                outport.writeUTF(help.toString());
                                outputSocket.close();
                                ServerPorts.set(i, true);
                                break;
                            }
                        }
                    }
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();

        DialogThreadFactory().start();

    }


    private Thread threadFactory(int x) throws IOException {
        return new Thread(new Runnable()
        {
            private String enteredPassword;
            private String message;
            private String senderName;
            private String searchName;
            private int state;
            private boolean closed = true;
            private int USER_PORT;
            private int SERVER_PORT = x + 50000;
            private final ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            public void run()
            {
                try
                {
                    while (!Thread.interrupted())
                    {
                        if (closed == true)
                        {
                            while (true)
                            {
                                final Socket socket = serverSocket.accept();
                                final DataInputStream in = new DataInputStream(socket.getInputStream());
                                final DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                                enteredPassword = in.readUTF();
                                if (enteredPassword.equals("error/qwerty111")) {
                                    socket.close();
                                    continue;
                                }
                                if (enteredPassword.equals(password))
                                {
                                    out.writeBoolean(false);
                                    for (int i = 0; i < 100; i++)
                                    {
                                        if (Ports.get(i) == false)
                                        {
                                            Integer port = i + 1500;
                                            USER_PORT = port;
                                            out.writeUTF(port.toString());
                                            break;
                                        }
                                    }
                                    senderName = in.readUTF();
                                    if (senderName.equals("error/qwerty111"))
                                    {
                                        socket.close();
                                        continue;
                                    }
                                    if (!Connections.containsKey(USER_PORT))
                                    {
                                        Connections.put(USER_PORT, senderName);
                                    }
                                    Ports.set(USER_PORT - 1500, true);
                                    socket.close();
                                    break;
                                } else {
                                    out.writeBoolean(true);
                                    socket.close();
                                }
                            }
                        }

                        final Socket socket = serverSocket.accept();
                        final DataInputStream in = new DataInputStream(socket.getInputStream());
                        final DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                        closed = in.readBoolean();

                        if (closed == false)
                        {
                            message = in.readUTF();
                            state = Integer.parseInt(in.readUTF());

                            if (state == 1) {
                                searchName = in.readUTF();
                            } else {
                                searchName = "";
                            }

                            if (!searchName.equals(""))
                            {
                                if (Connections.containsValue(searchName))
                                {
                                    out.writeBoolean(true);
                                }
                                else
                                {
                                    out.writeBoolean(false);
                                }
                            }
                            else
                            {
                                out.writeBoolean(false);
                            }
                            socket.close();
                            for (int i = 0; i < 100; i++)
                            {
                                if (Ports.get(i) == true)
                                {
                                    final Socket tsocket = new Socket("127.0.0.1", i + 1500);
                                    final DataOutputStream tout = new DataOutputStream(tsocket.getOutputStream());
                                    if (Connections.get(i + 1500).equals(searchName))
                                    {
                                        tout.writeBoolean(true);
                                    }
                                    else
                                    {
                                        tout.writeBoolean(false);
                                    }
                                    tout.writeUTF(senderName);
                                    tout.writeUTF(message);
                                    tsocket.close();
                                }
                            }
                        }
                        else
                        {
                            if (Connections.containsKey(USER_PORT))
                            {
                                Connections.remove(USER_PORT);
                            }
                            Ports.set(USER_PORT - 1500, false);
                            ServerPorts.set(SERVER_PORT - 50000, false);
                        }
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    private Thread DialogThreadFactory()
    {
        return new Thread(new Runnable()
        {
            String privateMessage;
            String senderName;
            String searchName;
            Integer DIALOG_PORT;
            public void run()
            {
                try
                {
                    while (!Thread.interrupted())
                    {
                        final Socket socket = dialogSocket.accept();
                        final DataInputStream in = new DataInputStream(socket.getInputStream());
                        privateMessage = in.readUTF();
                        senderName = in.readUTF();
                        searchName = in.readUTF();
                        socket.close();
                        for (int i = 0; i < 100; i++)
                        {
                            if (searchName.equals(Connections.get(i + 1500)))
                            {
                                DIALOG_PORT = i + 1500;
                                break;
                            }
                        }
                        DIALOG_PORT = DIALOG_PORT + 1000;
                        final Socket DdialogSocket = new Socket("127.0.0.1", DIALOG_PORT);
                        final DataOutputStream out = new DataOutputStream(DdialogSocket.getOutputStream());
                        out.writeUTF(privateMessage);
                        out.writeUTF(senderName);
                        DdialogSocket.close();
                    }
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    final ServerApp server = new ServerApp();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
}
