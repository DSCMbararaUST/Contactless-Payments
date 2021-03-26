package com.example.nfcdsc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 *
 * @author Michael Ajuna and Baluku Edgar <michaelajnew@gmail.com, edgarbaluku@gmail.com>
 *
 */

public class TopUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);

        Button topUp = findViewById(R.id.write_tag);

        topUp.setOnClickListener(v -> {
            //function to load Mobile money into the application
            loadMoney();

            // Function to send money from the top up activity to Payment History activity
            // to compute the current account balance.
            transferTopUpData();
        });

        //Function to handle the bottom nav bar actions
        handleBottomNavBarActions();
    }

    private void transferTopUpData(){
        EditText amount = findViewById(R.id.note);
        String msg = amount.getText().toString();

        if (!msg.isEmpty()) {
            Intent intent = new Intent(this, PaymentHistory.class);
            intent.putExtra("MESSAGE", msg);
            startActivity(intent);
            amount.setText("");
        } else {
            ToastMaker.toast(TopUpActivity.this," ENTER ANY AMOUNT ");
            amount.requestFocus();
        }
    }

    //Prompting the dialog for entering USSD to load money to the application
    private void loadMoney(){
        //Encoding the # for use programmatically
        String encodedHash = Uri.encode("#");
        String ussd = "*" + "165" + encodedHash;

        //Checking for user consent for the app to use the PHONE CALL permission.
        if (ContextCompat.checkSelfPermission(TopUpActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(TopUpActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE}, 1);

        } else {
            Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ussd));
            startActivity(dialIntent);
        }
    }

    // Function handling the bottom nav bar
    private void handleBottomNavBarActions(){

        //Handling the Bottom navigation view actions
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_nav_bar);

        // CHECKING THE CURRENT CLICKED BOTTOM NAV BAR MENU ITEM
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.tap2pay:
                    startActivity(new Intent(this, MainActivity.class));
                    break;
                case R.id.topup_activity:
                    startActivity(new Intent(this, TopUpActivity.class));
                    break;
                case R.id.payment_history_activity:
                    break;
                case R.id.profile:
                    startActivity(new Intent(this,SettingsActivity.class));
                    break;
            }
            return false;
        });
    }
}