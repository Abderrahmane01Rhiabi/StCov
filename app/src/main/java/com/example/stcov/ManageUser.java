package com.example.stcov;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

public class ManageUser extends AppCompatActivity {

    TextView email,firstname,lastname;
    Button delete;
    Switch aSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);

        firstname = findViewById(R.id.firstname_user);
        lastname = findViewById(R.id.lastname_user);
        email = findViewById(R.id.email_user);
        delete = findViewById(R.id.delete);
        aSwitch = findViewById(R.id.switch_btn);




    }
}
