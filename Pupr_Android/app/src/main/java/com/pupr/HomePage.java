package com.pupr;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class HomePage extends AppCompatActivity {
    Button goToVoting;
    Button editProfile;
    Button viewLeaderboard;
    Button logout;
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
        logo = findViewById(R.id.logo);

        //Save users
        try {
            UserSaver.saveUsers();
        } catch (Exception e) {
            e.printStackTrace();
        }

    //Parameters for the default picture
        String imagePath = "drawable/defaultpicture"; //path for default picture, the P part of the pupr logo
        int imageKey = getResources().getIdentifier(imagePath, "drawable", "com.pupr"); //imageKey for the default pic
        final Drawable defaultPicture = getResources().getDrawable(imageKey); //turn image into a drawable

    //Put logo on the Main Page
        imagePath = "drawable/pupr";
        imageKey = getResources().getIdentifier(imagePath, "drawable", "com.pupr");
        Drawable d = getResources().getDrawable(imageKey); //turn image into a drawable
        logo.setImageDrawable(d);

        //Generate voting queue if it's empty
        if (User.activeUser.votingQueue.isEmpty())
            User.activeUser.makeQueue();

    //Assign buttons
        goToVoting = findViewById(R.id.goToVoting);
        editProfile = findViewById(R.id.editProfile);
        viewLeaderboard = findViewById(R.id.goToLeaderboard);
        logout = findViewById(R.id.logOut);


    //Go to voting
        goToVoting.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            String endOfQueue = "No more dogs left. Please try again later."; //message to be displayed when the queue is empty
            String mustUpload = "You must upload a picture of your dog to be able to vote."; //message to be displayed if user has not uploaded dog picture
            if (User.activeUser.getPicture() == defaultPicture)
                Toast.makeText(getApplicationContext(), mustUpload, Toast.LENGTH_LONG).show();

            else if(User.activeUser.votingQueue.isEmpty())
                    Toast.makeText(getApplicationContext(), endOfQueue, Toast.LENGTH_LONG).show(); //display a message if queue is empty

            //Otherwise, enter voting screen
                else{
                Intent vote = new Intent(getBaseContext(), VotingPage.class);
                    startActivity(vote);
                }
            }
        });

    //Edit profile
        editProfile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Go to edit profile
                Intent editProfile = new Intent(getBaseContext(), EditProfile.class);
                startActivity(editProfile);
            }
        });

    //View leaderboard
        viewLeaderboard.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent leaderboard = new Intent(getBaseContext(), Leaderboard.class);
                startActivity(leaderboard);
            }
        });

        //Log out
        logout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                User.activeUser.votingQueue.clear(); //clear the active user's voting queue
                User temp = new User();
                User.setActiveUser(temp);
                Intent login = new Intent(getBaseContext(), LoginPage.class);
                login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Toast.makeText(getApplicationContext(), "You have successfully logged out.", Toast.LENGTH_SHORT ).show();
                startActivity(login);
                finish();

            }
        });

    }
    //Disable user from hitting back button
    @Override
    public void onBackPressed() {}
}
