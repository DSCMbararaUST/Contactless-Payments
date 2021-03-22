package com.example.nfcdsc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.nfcdsc.adapters.RecyclerViewAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

/**
 *
 * @author Michael Ajuna and Baluku Edgar <michaelajnew@gmail.com, edgarbaluku@gmail.com>
 *
 */

public class SettingsActivity extends AppCompatActivity {

    //UI Global variables
    private EditText names, acc_balance, phoneNo;
    //Global properties
    private String username, balance, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Binding the UI variables to their xml ids
        names = findViewById(R.id.names_field);
        acc_balance = findViewById(R.id.balance_field);
        phoneNo = findViewById(R.id.phone_field);

        Intent stringIntent = getIntent();

        username = stringIntent.getStringExtra("USERNAME");
        balance = stringIntent.getStringExtra("BALANCE");
        phone = stringIntent.getStringExtra("CONTACT");

        names.setText(username);
        acc_balance.setText(balance);
        phoneNo.setText(phone);

        //Functions
        handleBottomNavBarActions();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        
        //function to terminate the activity
        finish();
    }

    public void handleBottomNavBarActions(){
        //Handling the Bottom navigation view actions
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_nav_bar);

        // CHECKING THE CURRENT CLICKED BOTTOM NAV BAR MENU ITEM
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
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
                    startActivity(new Intent(this, PaymentHistory.class));
                    break;
                case R.id.profile:
                    break;
            }
            return false;
        });
    }
}