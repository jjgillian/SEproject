package com.pupr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Collections;

public class VotingPage extends AppCompatActivity {

    ImageView votingImage;
    EditText votingName;
    EditText votingBio;
    Button one;
    Button two;
    Button three;
    Button four;
    Button five;
    Button home;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting_page);

        Toast.makeText(getApplicationContext(), "Tap to vote. 5 is the highest score.", Toast.LENGTH_LONG).show();

        //shuffle queue
        Collections.shuffle(User.activeUser.votingQueue); //shuffles the queue to randomize which dogs show up next

        //print queue to log
        for (int i = 0; i < User.activeUser.votingQueue.size(); i ++)
            Log.d("Voting Queue", "Dog in Queue at " + i + ": " + User.activeUser.votingQueue.get(i).getDogName());

        Log.d("Voting Queue", "Queue size: " + User.activeUser.votingQueue.size());


        votingImage = findViewById(R.id.votingImage);
        votingName = findViewById(R.id.votingName);
        votingBio = findViewById(R.id.votingBio);
        one = findViewById(R.id.one);
        two = findViewById(R.id.two);
        three = findViewById(R.id.three);
        four = findViewById(R.id.four);
        five = findViewById(R.id.five);
        home = findViewById(R.id.votingHome);

        serveDog(); //show first dog

        home.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish(); //finishes activity, goes back home
            }
        });

    //Voting button onClickListeners
        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {vote(1);} //calls vote method with a score of 1
        });

        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {vote(2);} //calls vote method with a score of 2
        });

        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {vote(3);} //calls vote method with a score of 3

        });

        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {vote(4);} //calls vote method with a score of 4
        });

        five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {vote(5);} //calls vote method with a score of 5
        });
    }

//General voting method called by each button click; takes an integer input that is specified inside each .setOnClickListener method for each button
    public void vote (int score) {

        User.currentDog.incrementRatings(); //add score, recalculate values
        User.activeUser.votedOn.add(User.currentDog); //add this dog to the votedOn list of the current user
        User.currentDog.addScore(score); //add one to the score
        User.printVotedOn(); //debugging method
        User.activeUser.votingQueue.remove(0); //remove dog from queue
        serveDog();
        UserSaver.saveUsers(); //Save users
        }

//Serve up next dog from queue
    public void serveDog() {
        String endOfQueue = "No more dogs left. Please try again later."; //message to be displayed when the queue is empty

        if (!User.activeUser.votingQueue.isEmpty()) {
            User.currentDog = User.activeUser.votingQueue.get(0); //Looks at the first element in the votingQueue for the activeUser
            votingImage.setImageDrawable(User.currentDog.getPicture()); //Loads picture
            votingName.setText(User.currentDog.getDogName()); //load the dog's name
            votingBio.setText(User.currentDog.getBio()); //load the bio
        }

        else {
        //Redirect back to main page
            Toast.makeText(getApplicationContext(), endOfQueue, Toast.LENGTH_LONG).show();
            UserSaver.saveUsers(); //Save users
            finish(); //ends this activity

        }
    }
    //Disable user from hitting back button
    @Override
    public void onBackPressed() {}
}


