package mvc;

public class Model
{
    private String clientIP = "127.0.0.1";

    private String username = "";

    private String serverIP = "";

    private boolean connection;

    private int port;

    public void setIP(String ip)
    {
        this.clientIP = ip;
    }

    public String getIP()
    {
        return clientIP;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getUsername()
    {
        return username;
    }

    public void setServerIP(String serverIP)
    {
        this.serverIP = serverIP;
    }

    public String getServerIP()
    {
        return serverIP;
    }

    public void setConnected(boolean connection)
    {
        this.connection = connection;
    }

    public boolean isConnected()
    {
        return connection;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public int getPort()
    {
        return port;
    }

}
