package com.example.nfcdsc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.TextView;

import com.example.nfcdsc.adapters.RecyclerViewAdapter;
import com.example.nfcdsc.db_objects.Data;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 *
 * @author Michael Ajuna and Baluku Edgar <michaelajnew@gmail.com, edgarbaluku@gmail.com>
 *
 */

public class SettingsActivity extends AppCompatActivity {

    //UI Global variables
    private EditText names, phoneNo;
    private TextView acc_balance;
    //Global properties
    private String username, balance, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Binding the UI variables to their xml ids
        names = findViewById(R.id.username_field);
        acc_balance = findViewById(R.id.balance_field);
        phoneNo = findViewById(R.id.phone_number_field);

//        Intent stringIntent = getIntent();

//        username = stringIntent.getStringExtra("USERNAME");
//        balance = stringIntent.getStringExtra("BALANCE");
//        phone = stringIntent.getStringExtra("CONTACT");
//
//        names.setText(username);
//        acc_balance.setText(balance);
//        phoneNo.setText(phone);

        //Functions
        handleBottomNavBarActions();
        retrieveUserData();
    }

    /**
     *
     * Function to read data from the firebase realtime database.
     */
    private void retrieveUserData(){
        /**
         *
         * Getting the realtime database instance and a reference to the database
         */

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("user_data/-MWiSGkq-y3J6TDvpl2s");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Data post = dataSnapshot.getValue(Data.class);

                assert post != null;
                username = post.getFirstname();
                balance = post.getBalance();
                phone = post.getPhone();

                names.setText(username);
                acc_balance.setText(balance);
                phoneNo.setText(phone);

                System.out.println(post);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
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