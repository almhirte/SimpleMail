package com.almhirte.simplemail;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;
import java.security.KeyStore;

import javax.mail.Folder;
import javax.mail.Session;
import javax.mail.Store;

public class SimpleMailMainActivity extends AppCompatActivity
{

    private static final String SIMPLE_MAIL = "SimpleMail";
    private ArrayList<MailAccount> accountList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_mail_main);

        initKeyStore();

        initGui();
    }

    private void initKeyStore()
    {
        try
        {
            /**
             * The Keystore itself is encrypted using the user’s own lockscreen pin/password,
             * hence, when the device screen is locked the Keystore is unavailable.
             * Keep this in mind if you have a background service
             * that could need to access your application secrets.
             */
            KeyStore.getInstance("AndroidKeyStore").load(null);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            Log.i(SIMPLE_MAIL, e.getMessage());
        }
    }

    private void initGui()
    {
        accountList = getMailAccounts();

        ListAdapter adapter = new ArrayAdapter<MailAccount>(getApplicationContext(), R.layout.activity_simple_mail_main, android.R.id.text1, accountList);
        final ListView lv = (ListView) findViewById(R.id.email_list);
        lv.setAdapter(adapter);
    }

    private void addRow(MailAccount acc)
    {
        acc.getAccountName();
    }

    private ArrayList<MailAccount> getMailAccounts()
    {
        MailAdapter mailAdapter = new MailAdapter();
        mailAdapter.execute();
        if (accountList == null)
        {
            //rather return an empty List, than returning null
            accountList = new ArrayList<MailAccount>();
        }
        return accountList;
    }

    private class MailAdapter extends AsyncTask<Void, Long, Integer> // params, progress units, result unit
    {
        private int connectToMailbox()
        {
            int numMails = 0;

            /**
             * TODO: remove in final version when Security is available
             */
            String accountName = "";
            String password = "";

            try
            {
                String FILENAME = "accounts.txt";
                InputStream in = SimpleMailMainActivity.this.getAssets().open(FILENAME);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                accountName = reader.readLine();
                password = reader.readLine();
                in.close();
            }
            catch (java.io.IOException e)
            {
                e.printStackTrace();
                Log.i(SIMPLE_MAIL,e.getMessage());
            }


            Properties props = new Properties();
            props.setProperty("mail.store.protocol", "imaps");
            props.setProperty("mail.imap.host", "imap.mail.yahoo.com");

            props.setProperty("mail.password", password);
            try
            {
                Session session = Session.getInstance(props, null);
                Store store = session.getStore();
                store.connect("imap.mail.yahoo.com", accountName, password);
                Folder inbox = store.getFolder("INBOX");
                inbox.open(Folder.READ_ONLY);

                numMails = inbox.getMessageCount();

            /*
            Message msg = inbox.getMessage(inbox.getMessageCount());
            Address[] in = msg.getFrom();
            for (Address address : in)
            {
                System.out.println("FROM:" + address.toString());
            }
            Multipart mp = (Multipart) msg.getContent();
            BodyPart bp = mp.getBodyPart(0);
            System.out.println("SENT DATE:" + msg.getSentDate());
            System.out.println("SUBJECT:" + msg.getSubject());
            System.out.println("CONTENT:" + bp.getContent());
            */
            } catch (Exception ex)
            {
                ex.printStackTrace();
                return 0;
            }
            return numMails;
        }

        @Override
        protected Integer doInBackground(Void... params)
        {
            int numMails = connectToMailbox();
            return numMails;
        }

        protected void onProgressUpdate(Long... progress)
        {

        }

        protected void onPostExecute(Integer result)
        {
            //update GUI
            SimpleMailMainActivity.this.accountList.add(new MailAccount("rudi.m3nt@yahoo.com : " + result));

            runOnUiThread(
                    new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            ListAdapter adapter = new ArrayAdapter<MailAccount>(getApplicationContext(), R.layout.activity_simple_mail_main, android.R.id.text1, accountList);
                            final ListView lv = (ListView) findViewById(R.id.email_list);
                            lv.setAdapter(adapter);
                        }
                    });
        }
    }

}
