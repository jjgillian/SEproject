package com.pupr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUp extends AppCompatActivity {
    //Define the EditText boxes and the Register button

    EditText uname;
    EditText fname;
    EditText lname;
    EditText password;
    EditText confPass;
    Button register;
    Button cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account);
        uname =  findViewById(R.id.register_uname);
        fname = findViewById(R.id.register_fname);
        lname = findViewById(R.id.register_lname);
        password = findViewById(R.id.register_pass);
        confPass = findViewById(R.id.register_conf_pass);
        register = findViewById(R.id.register_button);
        cancel = findViewById(R.id.createAccountCancel);

        //Hitting cancel finishes this activity and goes back to login page
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean uniqueName = true; //false means that the user does not yet exist
                boolean incomplete = false; //false means that not all fields have been filled out
                for (int i = 0; i < User.userList.size(); i++) {
                    User thisUser = User.userList.get(i);
                    String user = thisUser.getUsername().toLowerCase(); //pull the username

                    if (uname.getText().toString().toLowerCase().equals(user)) {
                        Toast.makeText(getApplicationContext(), "This username is already taken", Toast.LENGTH_LONG).show();

                        uniqueName = false;
                    }
                }
                //if any fields are blank, set incomplete to true
                if (uname.getText().toString().equals("") || fname.getText().toString().equals("") || lname.getText().toString().equals("") || password.getText().toString().equals("") || confPass.getText().toString().equals("")) {
                    incomplete = true;
                    Toast.makeText(getApplicationContext(), "You must fill out all fields to continue", Toast.LENGTH_LONG).show();
                }

                if (uniqueName && !incomplete) {
                    if (password.getText().toString().equals(confPass.getText().toString())) {

                        User newUser = new User(fname.getText().toString(), lname.getText().toString(), uname.getText().toString(), password.getText().toString());

                        newUser.setPic(ImageSaver.setDefaultPic(getApplicationContext()));

                        User.setActiveUser(newUser); //sets the new user to the active user
                    //Clear activity stack and start new activity
                        Intent editProfile = new Intent(getBaseContext(), EditProfile.class);
                        editProfile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(editProfile);
                        finish();
                    } else
                        Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
                }
            }

        });
    }
    @Override
    public void onBackPressed() {
        UserSaver.saveUsers();
        finish();
    }
}