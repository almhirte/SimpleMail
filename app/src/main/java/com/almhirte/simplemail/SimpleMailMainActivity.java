package com.almhirte.simplemail;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;
import java.security.KeyStore;

import javax.mail.Folder;
import javax.mail.Session;
import javax.mail.Store;

public class SimpleMailMainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener
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
        accountList = loadMailAccounts();

        ListAdapter adapter = new ArrayAdapter<MailAccount>(getApplicationContext(), R.layout.activity_simple_mail_main, android.R.id.text1, accountList);
        final ListView lv = (ListView) findViewById(R.id.email_list);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
    }

    private ArrayList<MailAccount> loadMailAccounts()
    {
        /**
         * TODO: remove in final version when Security is available
         */
        if (accountList == null)
        {
            //rather return an empty List, than returning null
            accountList = new ArrayList<MailAccount>();
        }

        try
        {
            String FILENAME = "accounts.txt";
            InputStream in = SimpleMailMainActivity.this.getAssets().open(FILENAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String userName = reader.readLine();
            String password = reader.readLine();
            String serverIncoming = reader.readLine();
            in.close();
            MailAccount account = null;
            account = new MailAccount(userName);
            account.setUserName(userName);
            account.setPassword(password);
            account.setServerIncoming(serverIncoming);
            accountList.add(account);

            MailAdapter mailAdapter = new MailAdapter();
            mailAdapter.execute(account);
        }
        catch (java.io.IOException e)
        {
            e.printStackTrace();
            Log.i(SIMPLE_MAIL,e.getMessage());
        }
        return accountList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Toast.makeText(getApplicationContext(),"Klickiklacki in Liste", Toast.LENGTH_LONG).show();
    }

    private class MailAdapter extends AsyncTask<MailAccount, Long, Integer> // params, progress units, result unit
    {
        private int connectToMailbox(MailAccount account)
        {
            int numMails = 0;

            Properties props = new Properties();
            props.setProperty("mail.store.protocol", "imaps");
            props.setProperty("mail.imap.host", account.getServerIncoming());
            props.setProperty("mail.imap.user", account.getUserName());
            props.setProperty("mail.password", account.getPassword());
            try
            {
                Session session = Session.getInstance(props, null);
                Store store = session.getStore();
                store.connect(props.getProperty("mail.imap.host"), props.getProperty("mail.imap.user"), props.getProperty("mail.password"));//TODO: ConnectionListener should be used to verify successful connect
                if(store.isConnected())
                {
                    Folder inbox = store.getFolder("INBOX");
                    if(inbox != null)
                    {
                        inbox.open(Folder.READ_ONLY);
                        if(inbox.isOpen())
                        {
                            numMails = inbox.getMessageCount();
                        }
                    }
                }

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

            account.setNumberOfMails(numMails);

            return numMails;
        }

        @Override
        protected Integer doInBackground(MailAccount... params)
        {
            int numMails = connectToMailbox(params[0]);
            return numMails;
        }

        protected void onProgressUpdate(Long... progress)
        {

        }

        protected void onPostExecute(Integer result)
        {
            //update GUI
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
