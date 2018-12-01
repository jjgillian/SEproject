

package com.pupr;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


class UserSaver {

    private static final String separator = "@@";
    private final static String path = "pupr/users.csv";
    static void saveUsers() {
        File file = new File(Environment.getExternalStorageDirectory(), path);

        BufferedWriter bw;
        FileWriter fw;

        try {
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            String fname;
            String lname;
            String username;
            String password;
            String dogName;
            String bio;
            String totalScore;
            String numberOfRatings;
            String averageRating;
            StringBuilder votes = new StringBuilder();
            for (int i = 0; i < User.userList.size(); i++) {
                fname = User.userList.get(i).getFirstName();
                lname = User.userList.get(i).getLastName();
                username = User.userList.get(i).getUsername();
                password = User.userList.get(i).getPassword();
                dogName = User.userList.get(i).getDogName();
                bio = User.userList.get(i).getBio();
                totalScore = "" + User.userList.get(i).getScore();
                numberOfRatings = "" + User.userList.get(i).getRatings();
                averageRating = "" + User.userList.get(i).getAverage();

                for (int j = 0; j < User.userList.get(i).votedOn.size(); j++) {
                    votes.append(User.userList.get(i).votedOn.get(j).getUserId()).append(separator);
                }


             votes = new StringBuilder(votes.substring(0, votes.length() - 1)); //remove last separator
                bw.write(fname + separator + lname + separator + username + separator + password + separator + totalScore + separator + numberOfRatings + separator +
                        averageRating + separator + dogName + separator + bio + separator + votes);
                bw.newLine();
                votes = new StringBuilder();
            }
            bw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    static void loadUsers() {
        File file = new File(Environment.getExternalStorageDirectory(), path);
        FileReader fr;
        BufferedReader reader = null;
        String line = "";

        try {
            fr = new FileReader(file);
            reader = new BufferedReader(fr);
            while ((line = reader.readLine()) != null) {

                //Read the file, set the corresponding field to a User attribute
                String[] tokens = line.split(separator);  //split by ',' since this is a CSV file

                User newUser = new User(tokens[0], tokens[1], tokens[2], tokens[3]); //reads the data and saves the information as a defaultpicture user
                User.userList.add(newUser); //add user to userList
                newUser.setDefaultFalse();
                //Set more attributes
                newUser.setTotalScore(Double.parseDouble(tokens[4]));
                newUser.setRatings(Integer.parseInt(tokens[5]));
                newUser.setAverage(Double.parseDouble(tokens[6]));
                newUser.setDogName(tokens[7]); //set dog name
                newUser.setBio((tokens[8])); //set dog bio
                Log.d("UserSaver", "set 0 - 8 for " + newUser.getDogName());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //call method to load votes
        loadVotes();

        //call method to load pictures
        for (int i = 0; i < User.userList.size(); i++) {
            loadPictures(i);
            Log.d("UserSaver", "Called loadPictures for " + i);
        }
    }

    static void loadPictures(int id) {
        User curr = User.userList.get(id);
        Bitmap b = new ImageSaver().load("/pupr/img" + curr.getUserId() + ".png");
        Log.d("UserSaver", "Found image");
        Drawable d = new BitmapDrawable(Resources.getSystem(), b);
        curr.setPic(d);
    }

    private static void loadVotes() {
        File file = new File(Environment.getExternalStorageDirectory(), path);
        FileReader fr;
        BufferedReader reader = null;
        String line = "";

        try {
            fr = new FileReader(file);
            reader = new BufferedReader(fr);
            int id = 0; //corresponds to userid
            while ((line = reader.readLine()) != null) {

                //Read the file, set the corresponding field to a User attribute
                String[] tokens = line.split(separator);

                //debug
                for (int i = 0; i < tokens.length; i++) {
                    Log.d("UserSaver", "tokens[" + i + "] = " + tokens[i]);
                }
                    for (int i = 10; i < tokens.length; i++) {
                    if(i == tokens.length - 1)
                        tokens[i] = tokens[i].substring(0, tokens[i].length() - 1); //remove the last separator
                    Log.d("UserSaver", "Entered for loop, i = " + i + " and dog = " + tokens[7]);
                    // if (i > 9) { //votes start at index 9, but that is your vote for yourself
                    int vote = Integer.parseInt(tokens[i]); //returns the userId for the vote
                    Log.d("UserSaver", "vote = " + vote + " for i = " + i);
                    User voted = User.userList.get(vote); //finds the User for the userId
                    User.userList.get(id).votedOn.add(voted);
                    //}
                }
                id++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}