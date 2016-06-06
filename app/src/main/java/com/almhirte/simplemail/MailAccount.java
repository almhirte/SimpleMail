package com.almhirte.simplemail;

/**
 * Created by shenkel on 30.05.2016.
 */
public class MailAccount
{
    private String accountName = "";
    private String userName = "";
    private String password = "";

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
        return this.getAccountName();
    }
}
