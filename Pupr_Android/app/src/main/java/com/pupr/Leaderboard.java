package com.pupr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Collections;

public class Leaderboard extends AppCompatActivity {
    Button home;
    ImageView first, second, third, fourth, fifth;
    Button firstButton, secondButton, thirdButton, fourthButton, fifthButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        updateLeaderboard();
        Toast.makeText(getApplicationContext(), "Tap a button to view more information.", Toast.LENGTH_LONG).show();


        //Set pictures
        first = findViewById(R.id.firstPlacePic);
        second = findViewById(R.id.secondPlacePic);
        third = findViewById(R.id.thirdPlacePic);
        fourth = findViewById(R.id.fourthPlacePic);
        fifth = findViewById(R.id.fifthPlacePic);


    //Set Drawables for pictures
        ImageView[] images = new ImageView[] {first, second, third, fourth, fifth}; //store ImageViews in an array for easy access
        for (int i = 0; i < images.length; i++) {
            images[i].setImageDrawable(User.leaderboard.get(i).getPicture()); //update each image
        }
    //Set text for EditTexts
        firstButton = findViewById(R.id.leaderboardNumberOne);
        secondButton = findViewById(R.id.leaderboardNumberTwo);
        thirdButton = findViewById(R.id.leaderboardNumberThree);
        fourthButton = findViewById(R.id.leaderboardNumberFour);
        fifthButton =  findViewById(R.id.leaderboardNumberFive);

        Button[] buttons = new Button[] {firstButton, secondButton, thirdButton, fourthButton, fifthButton};
        for (int i = 0; i < buttons.length; i++) {
            String buttonText = "#" + (i+1) + " " + User.leaderboard.get(i).getDogName();
            buttons[i].setText(buttonText);
        }
    //set click listeners for pictures
        firstButton.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v){
               viewProfile(1);
           }
        });
        secondButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                viewProfile(2);
            }
        });
        thirdButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                viewProfile(3);
            }
        });
        fourthButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                viewProfile(4);
            }
        });
        fifthButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                viewProfile(5);
            }
        });

    //Home button
        home = findViewById(R.id.leaderboardHome);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {finish();}
        });
    }

    private void viewProfile(int i) {
        User.currentView = User.leaderboard.get(i-1); //set the profile to be viewed
        Intent viewProfile = new Intent(getBaseContext(), ViewProfile.class);
        startActivity(viewProfile);
    }

    //String method that takes an integer argument, used to print out leaderboard information
    public static String printLeaderboardText(User u) {
            int ranking = User.leaderboard.indexOf(u);
            String average = String.format(java.util.Locale.US, "%.1f", u.getAverage()); //round average score to 1 decimal
            return(
                    "Ranking # " + (ranking+1) + "\nName: " + u.getDogName() + "\nOwner: " + u.getFirstName() + " " + u.getLastName() + "\n" +
                    "Average Score: " + average + "\n" +
                    "Total Score: " + (int) u.getScore() + "\n" +
                    "Total Votes: " + u.getRatings());
    }

//Bubble sort method to sort the leaderboard from biggest to smallest
    private void updateLeaderboard() {
        for (int i = 0; i < User.userList.size(); i++) {
            for (int j = User.userList.size() - 1; j > i; j--) {
                User current = User.leaderboard.get(j);
                User previous = User.leaderboard.get(j - 1);

            //define some local variables for commonly used comparisons
                double currAvg = current.getAverage();
                double prevAvg = previous.getAverage();
                double currTot = current.getScore();
                double prevTot = previous.getScore();
                int currVotes = current.getRatings();
                int prevVotes = previous.getRatings();

                if (currAvg > prevAvg)
                    Collections.swap(User.leaderboard, j, j - 1);
                else if (currAvg == prevAvg) {
                    if (currTot > prevTot)
                        Collections.swap(User.leaderboard, j, j - 1);
                    else if (currTot == prevTot) {
                        if (currVotes > prevVotes)
                            Collections.swap(User.leaderboard, j, j  -1);
                    }
                }
                Log.d("Leaderboard", previous.getDogName() + " now at " + User.leaderboard.indexOf(previous));
                Log.d("Leaderboard", current.getDogName() + " now at " + User.leaderboard.indexOf(current));

            }
        }
        //Print whole leaderboard to check results
        Log.d("Leaderboard", "After swap");
        for (int i = 0; i < User.leaderboard.size(); i++)
            Log.d("Leaderboard", printLeaderboardText(User.leaderboard.get(i)));
    }
    //Disable user from hitting back button
    @Override
    public void onBackPressed() {}
}