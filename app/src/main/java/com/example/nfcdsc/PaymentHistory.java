package com.example.nfcdsc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.nfcdsc.adapters.RecyclerViewAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 *
 * @author Michael Ajuna and Baluku Edgar <michaelajnew@gmail.com, edgarbaluku@gmail.com>
 *
 */

public class PaymentHistory extends AppCompatActivity {

    TextView account_balance_txt;

    //Global Variables to store the data from the MainActivity and the top up activity.
    String account_balance;
    double current_amount = 60000d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);

        Intent intent = getIntent();
        //Account balance from the TopUp Activity
        account_balance = intent.getStringExtra("MESSAGE");

        // Amount from the transaction.
        //current_amount = Integer.parseInt(intent.getStringExtra("AMOUNT CHARGED"));

        account_balance_txt = findViewById(R.id.acc_balance);

        double current_balance = Double.parseDouble(account_balance);

        double updated_balance = current_balance-current_amount;

        account_balance = String.valueOf(updated_balance);

        // Formatting the account balance for the view
        //String my_acc_balance = String.format("%,d", account_balance);

        account_balance_txt.setText(account_balance);
        //messageView.setText((int) current_amount);

        //FUNCTIONS.
        populateRecyclerView();
        handleBottomNavBarActions();

    }

    // METHOD/FUNCTION TO POPULATE THE PAYMENT HISTORY RECYCLER VIEW LIST
    private void populateRecyclerView() {

        RecyclerView recyclerView = findViewById(R.id.payment_history_list);

        Intent stringIntent = getIntent();

        String data = stringIntent.getStringExtra("AMOUNT CHARGED");

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, data);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    // Function handling the bottom nav bar
    private void handleBottomNavBarActions(){

        //Handling the Bottom navigation view actions
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_nav_bar);

        // CHECKING THE CURRENT CLICKED BOTTOM NAV BAR MENU ITEM
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
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