package com.pupr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class ViewProfile extends AppCompatActivity {
    ImageView image;
    EditText text;
    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        image = findViewById(R.id.viewProfileImage);
        text = findViewById(R.id.viewProfileText);
        back = findViewById(R.id.viewProfileBack);

        image.setImageDrawable(User.currentView.getPicture());
        text.setText(Leaderboard.printLeaderboardText(User.currentView));

        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){finish();}
        });

    }
    //Disable user from hitting back button
    @Override
    public void onBackPressed() {}
}
