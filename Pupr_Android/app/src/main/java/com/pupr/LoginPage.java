package com.pupr;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class LoginPage extends AppCompatActivity {
    //Declare UI elements
    Button begin;
    Button signIn;
    Button signUp;
    Button reset;
    Button resetConfirm;
    Button resetCancel;
    EditText userText;
    EditText passwordText;
    EditText welcomeText;
    EditText resetText;
    ImageView logo;
    ImageView resetLogo;

    //Used for granting permissions -- do not delete
    private int requestCode;
    private int grantResults[];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashPage();
    }

    private void splashPage() {
        setContentView(R.layout.splash_page);
        String imagePath = "drawable/pupr";
        int imageKey = getResources().getIdentifier(imagePath, "drawable", "com.pupr");
        Drawable logoDrawable = getResources().getDrawable(imageKey); //turn image into a drawable

        //Put logo on the Main Page
        logo = findViewById(R.id.splashLogo);
        logo.setImageDrawable(logoDrawable);

        //Set a welcome message
        welcomeText = findViewById(R.id.welcomeText);
        String welcome = "Welcome to Pupr! \nIf this is your first time, press \"Begin\" to start!\nPlease note: the app will take a few moments to initialize." +
                "\nIf you would like to reset the application, please click \"Reset\"\nEnjoy!";
        welcomeText.setText(welcome);

        //Begin button
        begin = findViewById(R.id.begin);
        begin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //Request permission to write to external storage
                ActivityCompat.requestPermissions(LoginPage.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
            }

        });

        //Reset button
        reset = findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPage();
            }
        });

    }

    private void resetPage() {
        setContentView(R.layout.reset_confirm);
        resetLogo = findViewById(R.id.resetLogo);

        String imagePath = "drawable/pupr";
        int imageKey = getResources().getIdentifier(imagePath, "drawable", "com.pupr");
        Drawable logoDrawable = getResources().getDrawable(imageKey); //turn image into a drawable
        resetLogo.setImageDrawable(logoDrawable);

    //Display a warning message
        resetText = findViewById(R.id.resetText);
        String reset = "Warning! You are about to reset the application data, including any votes you have cast and any profiles you have edited or created!\n" +
                "Note that the app will take a few moments to reinitialize. Please be patient.\n\n" +
                "Are you sure you wish to proceed?";
        resetText.setText(reset);

    //Go back!!!
        resetCancel = findViewById(R.id.resetCancel);
        resetCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                splashPage(); //go back to splash pae
            }
        });

    //Full steam ahead - let's reset!
        resetConfirm = findViewById(R.id.resetConfirm);
        resetConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User.resetApp();
                createDefaultUsers();
                UserSaver.saveUsers();
                splashPage();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],  int[] grantResults) {

                // If request is cancelled, the result arrays are empty.
                if (grantResults != null && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d("permission", "granted");
                    runLoginPage(); //regular functionality of login page
                }

                else {
                    //close app if permission denied
                    Toast.makeText(getApplicationContext(), "Write External permission is required to run this app.", Toast.LENGTH_LONG).show();
                }

        }



    private void runLoginPage() {
        //Assign buttons
        setContentView(R.layout.login_screen);
        signIn = findViewById(R.id.signin_button);
        signUp = findViewById(R.id.signup_button);
        userText = findViewById(R.id.login_uname);
        passwordText = findViewById(R.id.login_pass);

        File users = new File(Environment.getExternalStorageDirectory(), "/pupr/users.csv");
        if (!users.exists()) //app has not been run yet
            createDefaultUsers();
        else if (User.userList.size() == 0) //app has been launched previously
            UserSaver.loadUsers();

        UserSaver.saveUsers(); //Save users


        //Sign in
        signIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        //Sign up
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register = new Intent(getBaseContext(), SignUp.class);
                register.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(register);
            }
        });
    }


    protected void signIn() {

        boolean flag = false;
        User currUser = new User(); //Declare a User to be later stored as the current user
        for (int i = 0; i < User.userList.size(); i++) {
            User iUser = User.userList.get(i); //thisUser is used as a pointer to traverse the list
            String user = iUser.getUsername().toLowerCase(); //pull the username
            String pass = iUser.getPassword().toLowerCase(); //pull the password
            //Compare username and password of the given user to what you have typed in the boxes
            if (userText.getText().toString().toLowerCase().equals(user) && passwordText.getText().toString().toLowerCase().equals(pass)) {
                flag = true;
                currUser = iUser;
            }

        }

        if (flag) {
            User.setActiveUser(currUser); //sets active User
            Intent home = new Intent(getBaseContext(), HomePage.class);
            home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(home);
        } else
            Toast.makeText(getApplicationContext(), "Wrong Credentials", Toast.LENGTH_SHORT).show();
    }


    //Method to read CSV raw file and create default users from it
    protected void createDefaultUsers() {
        InputStream is = getResources().openRawResource(R.raw.users); //read the raw CSV file
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8"))); //reader for the CSV file
        String line = ""; //used to iterate the CSV file
        String imagePath; //will be used to locate the drawable file that I put into the project folder
        String savePath; //where the new image will be saved to
        View v = new View(getBaseContext()); //need this View for saving the Bitmap
        int i = 0; //will be used to increment by userId
        try {

        //Start reading the file
            while ((line = reader.readLine()) != null) { //read until the end
                //Add users
                String[] tokens = line.split("@@");  //split by ',' since this is a CSV file
                User newUser = new User(tokens[0], tokens[1], tokens[2], tokens[3]); //reads the data and saves the information as a defaultpicture user
                newUser.setDogName(tokens[4]); //set dog name
                newUser.setBio((tokens[5])); //set dog bio
                User.userList.add(i, newUser);
                Log.d("MyActivity", "Just created: " + newUser.getUserId() + ", " + newUser.getDogName()); //puts userId into the log so we can make sure this method is just called one time
                newUser.setDefaultFalse(); //flag the user as already having a picture uploaded

            //Add dog images
                imagePath = "drawable/img" + i; //retrieve path from apk resources
                savePath = "/storage/emulated/0/pupr/img" + i + ".png"; //define where to save the image on the device

            //Create path for image file
            /*  File savedImage = new File(Environment.getExternalStorageDirectory(), savePath);
                if(!savedImage.exists())
                    savedImage.mkdirs(); //create the file if not yet created
*/
                int imageKey = getResources().getIdentifier(imagePath, "drawable", "com.pupr"); //generate a key for each image corresponding to each user
                Drawable d = getResources().getDrawable(imageKey); //turn image into a drawable
                Bitmap b0 = ((BitmapDrawable) d).getBitmap(); //get Bitmap for drawable

                new ImageSaver(v.getContext()).setExternal(true).setFileName("img" + i + ".png").save(b0); //save Bitmap to device
                Uri u0 = ImageSaver.getImageUri(b0, i); //convert b0 into a uri

                try {
                  b0 = ImageSaver.getCorrectlyOrientedImage(getApplicationContext(), u0, savePath, "Default"); //properly orient and reformat b0
                   new ImageSaver(v.getContext()).setExternal(true).setFileName("img" + i + ".png").save(b0); //save new Bitmap to device
                    UserSaver.loadPictures(i); //load properly formatted image from the newly saved bitmap
               } catch (IOException e) {
                   e.printStackTrace();
                }
                i++; //increment to next user

            }

        } catch (IOException e) {
            Log.wtf("MyActivity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }
    }
    //close app if the user hits the back button
    @Override
    public void onBackPressed() {
        UserSaver.saveUsers();
        finish();
    }
}