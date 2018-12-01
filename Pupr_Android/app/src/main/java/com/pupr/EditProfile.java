package com.pupr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;


public class EditProfile extends AppCompatActivity {
    private static final int RESULT_LOAD_IMAGE = 1; //Allows for image to be returned

    ImageView imageToUpload;
    EditText nameToUpload;
    EditText bioToUpload;
    Button submitProfile;
    Button cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        imageToUpload = findViewById(R.id.imageToUpload);
        nameToUpload = findViewById(R.id.new_dog_name);
        bioToUpload = findViewById(R.id.new_dog_bio);
        submitProfile = findViewById(R.id.submitDog);
        cancel = findViewById(R.id.cancelProfileChanges);

        Toast.makeText(getApplicationContext(), "Tap the picture to edit.", Toast.LENGTH_LONG).show();

        //Path information for a default picture
        Drawable defaultPicture = ImageSaver.setDefaultPic(getApplicationContext()); //turn image into a drawable
        final Bitmap defaultBit = ((BitmapDrawable)defaultPicture).getBitmap(); //default image as a bitmap


    //Load current information and picture for the user
        nameToUpload.setText(User.activeUser.getDogName());
        bioToUpload.setText(User.activeUser.getBio());
        Drawable userPic = User.activeUser.getPicture();
        imageToUpload.setImageDrawable(userPic);

        //ClickListener for Cancel button
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap currentBit = ((BitmapDrawable)imageToUpload.getDrawable()).getBitmap(); //current picture as a Bitmap
                if (currentBit.equals(defaultBit) || User.activeUser.getDefault()){
                    Toast.makeText(EditProfile.this, "You must upload a picture of your dog before proceeding", Toast.LENGTH_LONG).show();
                    Log.d("Image", "equals default, could not cancel");
                }
                else {
                    UserSaver.saveUsers(); //Save users
                    finish();
                }
            }
        });
        //ClickListener to let you upload a picture
        imageToUpload.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                      uploadImage();
                                             }
                                         });

        submitProfile.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    Bitmap currentBit = ((BitmapDrawable)imageToUpload.getDrawable()).getBitmap(); //current picture as a Bitmap
                                    Bitmap userBit = ((BitmapDrawable)User.activeUser.getPicture()).getBitmap();

                                    if(currentBit.equals(defaultBit)){ //default picture was not changed
                                        Toast.makeText(EditProfile.this, "You must a picture and submit your profile to continue", Toast.LENGTH_LONG).show();
                                        Log.d("Image", "equals default, could not complete profile submission");
                                    }
                                //Must have a dog name and bio to proceed
                                    else if(nameToUpload.getText().toString().trim().length() == 0 || bioToUpload.getText().toString().trim().length() == 0) {
                                        Toast.makeText(EditProfile.this, "You must fill in all fields before proceeding", Toast.LENGTH_LONG).show();
                                    }

                                    else { //user not using default picture
                                        User.activeUser.setDefaultFalse(); //update flag
                                        savePicture(userBit, currentBit); //save pic to phone if a new picture was uploaded
                                        Drawable newPic = imageToUpload.getDrawable(); //set pic on ImageView

                                        User.activeUser.setPic(newPic); //Set image as an attribute for the user
                                        User.activeUser.setBio(bioToUpload.getText().toString().replaceAll("\\n", "")); //set bio
                                        User.activeUser.setDogName(nameToUpload.getText().toString()); //set name

                                //If user has uploaded an image for the first time, add to userList
                                    boolean alreadyAdded = false;
                                    for (int i = 0; i < User.userList.size(); i++) {
                                        if (User.userList.get(i).equals(User.activeUser))
                                            alreadyAdded = true; //hit found
                                    }

                                    Log.d("Userlist", "Before adding");
                                    User.printUserList(); //print out the userList before adding

                                    if (!alreadyAdded) //no hit, add user
                                        User.userList.add(User.activeUser); //add this user to a list of all of the users

                                    Log.d("Userlist", "After adding");
                                    User.printUserList();
                                            UserSaver.saveUsers(); //Save users


                                        //Load the homepage
                                        Intent home= new Intent(getBaseContext(), HomePage.class);
                                        home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(home);
                                    }

                                }
        });
    }

    public static void savePicture(Bitmap oldPic, Bitmap newPic) {
        if (!newPic.equals(oldPic)) {
            new ImageSaver().setExternal(true).setFileName("img" + User.activeUser.getUserId() + ".png").save(newPic); //saves the image in /pupr on the internal storage of the android device
            Log.d("Image", "saved");
        }
        else
            Log.d("Image", "same image, not saved");
    }

    //Two methods that are used for uploading images
    protected void uploadImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); //Let user select an image from gallery
        startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
    }

    @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) { //check everything is good
                Uri selectedImage = data.getData(); //select image
                try {
                    final String path = "/pupr/img" + User.activeUser.getUserId() + ".png";
                    Bitmap newBit = ImageSaver.getCorrectlyOrientedImage(getApplicationContext(), selectedImage, path, "Uploaded");
                    Drawable d = new BitmapDrawable(getResources(), newBit);
                    imageToUpload.setImageDrawable(d); //set image and display it
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

//Disable user from hitting back button
    @Override
    public void onBackPressed() {}
}
