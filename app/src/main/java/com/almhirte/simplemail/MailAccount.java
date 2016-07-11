package com.almhirte.simplemail;

/**
 * Created by shenkel on 30.05.2016.
 */
public class MailAccount
{
    private String accountName = "";
    private String userName = "";
    private String password = "";
    private String serverIncoming = "";
    private String serverOutgoing = "";
    private int numberOfMails = 0;

    public MailAccount(String accountName)
    {
        this.setAccountName(accountName);
    }

    public String getAccountName()
    {
        return accountName;
    }

    public void setAccountName(String accountName)
    {
        this.accountName = accountName;
    }

    @Override
    public String toString()
    {
        String displayString = "";

        displayString = this.getAccountName() + ": " + this.getNumberOfMails();
        return displayString;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getServerIncoming()
    {
        return serverIncoming;
    }

    public void setServerIncoming(String serverIncoming)
    {
        this.serverIncoming = serverIncoming;
    }

    public String getServerOutgoing()
    {
        return serverOutgoing;
    }

    public void setServerOutgoing(String serverOutgoing)
    {
        this.serverOutgoing = serverOutgoing;
    }

    public int getNumberOfMails()
    {
        return numberOfMails;
    }

    public void setNumberOfMails(int numberOfMails)
    {
        this.numberOfMails = numberOfMails;
    }
}
