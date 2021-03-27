package com.example.nfcdsc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.nfcdsc.adapters.RecyclerViewAdapter;
import com.example.nfcdsc.db_objects.Data;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 *
 * @author Michael Ajuna and Baluku Edgar <michaelajnew@gmail.com, edgarbaluku@gmail.com>
 *
 */

public class PaymentHistory extends AppCompatActivity {

    TextView account_balance_txt;

    //Global Variables to store the data from the MainActivity and the top up activity.
    String account_balance, amount_paid, topped_amount, new_bal;
    // Variable storing the amount that has been sent written to a tag via NFC
    double current_amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);

        account_balance_txt = findViewById(R.id.acc_balance);

        //Intent to load data from the TopUp Activity and the payment activity(MainActivity).
        //Intent dataIntent = getIntent();

        //if (dataIntent!=null){
            //Account balance from the TopUp Activity
            //topped_amount = dataIntent.getStringExtra("MESSAGE");
            //amount_paid = dataIntent.getStringExtra("PAID");
//
//            Double new_balance = Double.parseDouble(topped_amount) - Double.parseDouble(amount_paid);
//
//            new_bal = String.valueOf(new_balance);

            account_balance_txt.setText(amount_paid);

//        }else{
//            //userDataCalc();
//        }

        //FUNCTIONS.
        populateRecyclerView();
        handleBottomNavBarActions();

        userDataCalc();
    }

    // METHOD/FUNCTION TO POPULATE THE PAYMENT HISTORY RECYCLER VIEW LIST
    private void populateRecyclerView() {

        RecyclerView recyclerView = findViewById(R.id.payment_history_list);

        Intent stringIntent = getIntent();

        if (stringIntent!=null){

            String data = stringIntent.getStringExtra("PAID");
            RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, data);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

        }else{
            ToastMaker.toast(PaymentHistory.this, " MAKE SOME TRANSACTIONS ");
        }
    }

    /**
     *
     * GFunction to read data from the firebase realtime database.
     */
    private void userDataCalc(){
        /**
         *
         * Getting the realtime database instance and a reference to the database
         */

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("user_data/new_user/-MWmLvWdn_Q_b505DTnA");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Data post = dataSnapshot.getValue(Data.class);

                assert post != null;
                account_balance = post.getBalance();
//
//                double current_balance = Double.parseDouble(account_balance);
//
//                current_amount = Double.parseDouble(amount_paid);
//
//                double updated_balance = current_balance+current_amount;
//
//                account_balance = String.valueOf(updated_balance);

                // Formatting the account balance for the view
                //String my_acc_balance = String.format("%,d", account_balance);

                account_balance_txt.setText(account_balance);
                //messageView.setText((int) current_amount);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
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