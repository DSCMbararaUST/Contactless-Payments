package com.example.nfcdsc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;

/**
 *
 * @author Michael Ajuna and Baluku Edgar <michaelajnew@gmail.com, edgarbaluku@gmail.com>
 *
 */

public class MainActivity extends Activity {
    private static final String TAG = "Contactless Payment";
    private boolean mResumed = false;
    private boolean mWriteMode = false;
    NfcAdapter mNfcAdapter;
    EditText mMoney;

    String storedMoney, transferMoney;

    PendingIntent mNfcPendingIntent;
    IntentFilter[] mWriteTagFilters;
    IntentFilter[] mNdefExchangeFilters;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        findViewById(R.id.process_payment).setOnClickListener(mTransfer);
        mMoney = ((EditText) findViewById(R.id.note));

        storedMoney = mMoney.getText().toString();

        mMoney.addTextChangedListener(mTextWatcher);

        // Handle all of our received NFC intents in this activity.
        mNfcPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        // Intent filters for reading a note from a tag or exchanging over p2p.
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndefDetected.addDataType("text/plain");
        } catch (MalformedMimeTypeException e) { }

        mNdefExchangeFilters = new IntentFilter[] { ndefDetected };

        // Intent filters for writing to a tag
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        mWriteTagFilters = new IntentFilter[] { tagDetected };

        // Function to hanlde the bottom nav bar
        handleBottomNavBarActions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mResumed = true;
        //Data received from Android
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            NdefMessage[] messages = getNdefMessages(getIntent());
            byte[] payload = messages[0].getRecords()[0].getPayload();
            setNoteBody(new String(payload));
            setIntent(new Intent());
        }
        enableNdefExchangeMode();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mResumed = false;
        mNfcAdapter.disableForegroundNdefPush(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // NDEF exchange mode
        if (!mWriteMode && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            NdefMessage[] msgs = getNdefMessages(intent);
            promptForContent(msgs[0]);

            ToastMaker.toast(MainActivity.this,"P2P DATA EXCHANGE");

        }
        // Writing to the receiving device
        if (mWriteMode && NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            writeTag(getNoteAsNdef(), detectedTag);

            ToastMaker.toast(MainActivity.this,"WRITING TO TAG");
        }
    }

    private TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

        }

        @Override
        public void afterTextChanged(Editable arg0) {
            if (mResumed) {
                mNfcAdapter.enableForegroundNdefPush(MainActivity.this, getNoteAsNdef());
            }
        }
    };

    private View.OnClickListener mTransfer = arg0 -> {
        // Write to a tag for as long as the dialog is shown.
        disableNdefExchangeMode();
        enableTagWriteMode();
//
//        new AlertDialog.Builder(MainActivity.this)
//                .setTitle("Tap the device to pay")
//                .setOnCancelListener(dialog -> {
//
//                    disableTagWriteMode();
//                    enableNdefExchangeMode();
//
//                })
//                .create()
//                .show();

        String paid = mMoney.getText().toString();

        if(paid.isEmpty()){
            ToastMaker.toast(this, "Make a transaction first ! ");
        }else{
            Intent transferIntent = new Intent(this, PaymentHistory.class);
            transferIntent.putExtra("PAID", paid);

            startActivity(transferIntent);
        }
    };

    private void promptForContent(final NdefMessage msg) {
        new AlertDialog.Builder(this)
                .setTitle("Are you sure ?")
                .setPositiveButton("Yes", (arg0, arg1) -> {

                    String body = new String(msg.getRecords()[0].getPayload());

                    //Converting the received data to double for the calculations
                    //double amount_paid = Integer.parseInt(body);

//                    Intent intent = new Intent(this, PaymentHistory.class);
//                    intent.putExtra("AMOUNT", body);
//                    startActivity(intent);
                    setNoteBody(body);

                    /**
                     * Function to send the money to the payment activity
                     * @params money_paid
                     * the parameter above is the NDEFMessage received
                     */
                })
                .setNegativeButton("No", (arg0, arg1) -> {
                }).show();
    }

    //Function to set the received data to the edit text
    private void setNoteBody(String body) {
        Editable text = mMoney.getText();
        text.clear();
        text.append(body);
    }

    private NdefMessage getNoteAsNdef() {
        byte[] textBytes = mMoney.getText().toString().getBytes();
        NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "text/plain".getBytes(),
                new byte[] {}, textBytes);
        return new NdefMessage(new NdefRecord[] {
                textRecord
        });
    }

    NdefMessage[] getNdefMessages(Intent intent) {
        // Parse the intent
        NdefMessage[] msgs = null;
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                // Unknown tag type
                byte[] empty = new byte[] {};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[] {
                        record
                });
                msgs = new NdefMessage[] {
                        msg
                };
            }
        } else {
            Log.d(TAG, "Unknown intent.");
            finish();
        }
        return msgs;
    }

    private void enableNdefExchangeMode() {
        mNfcAdapter.enableForegroundNdefPush(MainActivity.this, getNoteAsNdef());
        mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mNdefExchangeFilters, null);
    }

    private void disableNdefExchangeMode() {
        mNfcAdapter.disableForegroundNdefPush(this);
        mNfcAdapter.disableForegroundDispatch(this);
    }

    private void enableTagWriteMode() {
        mWriteMode = true;
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        mWriteTagFilters = new IntentFilter[] {
                tagDetected
        };
        mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mWriteTagFilters, null);
    }

    private void disableTagWriteMode() {
        mWriteMode = false;
        mNfcAdapter.disableForegroundDispatch(this);
    }

    boolean writeTag(NdefMessage message, Tag tag) {
        int size = message.toByteArray().length;

        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();

                if (!ndef.isWritable()) {
                    ToastMaker.toast(MainActivity.this,"Tag is read-only.");
                    return false;
                }
                if (ndef.getMaxSize() < size) {
                    ToastMaker.toast(MainActivity.this,"Tag capacity is "
                            + ndef.getMaxSize() + " bytes, message is " + size
                            + " bytes.");
                    return false;
                }

                ndef.writeNdefMessage(message);
                ToastMaker.toast(MainActivity.this,"Wrote message to pre-formatted tag.");
                return true;
            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        ToastMaker.toast(MainActivity.this,"Formatted tag and wrote message");
                        return true;
                    } catch (IOException e) {
                        ToastMaker.toast(MainActivity.this,"Failed to format tag.");
                        return false;
                    }
                } else {
                    ToastMaker.toast(MainActivity.this,"Tag doesn't support NDEF.");
                    return false;
                }
            }
        } catch (Exception e) {
            ToastMaker.toast(MainActivity.this, "Failed to write tag");
        }

        return false;
    }

    // Function handling the bottom nav bar
    private void handleBottomNavBarActions(){

        //Handling the Bottom navigation view actions
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_nav_bar);

        // CHECKING THE CURRENT CLICKED BOTTOM NAV BAR MENU ITEM
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.tap2pay:
                    break;
                case R.id.topup_activity:
                    startActivity(new Intent(this, TopUpActivity.class));
                    break;
                case R.id.payment_history_activity:
                    startActivity(new Intent(this, PaymentHistory.class));
                    break;
                case R.id.profile:
                    startActivity(new Intent(this,SettingsActivity.class));
                    break;
            }
            return false;
        });
    }
}