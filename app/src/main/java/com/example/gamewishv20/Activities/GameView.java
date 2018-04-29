package com.example.gamewishv20.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.gamewishv20.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class GameView extends AppCompatActivity {

    private String mRef_key = null;

    private DatabaseReference mDatabaseRef;

    private ImageView game_image;
    private TextView game_genre, game_summary;
    private RatingBar mRatingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_view);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("games");

         mRef_key = getIntent().getExtras().getString("gamev_id");

         game_image = (ImageView) findViewById(R.id.games_image);
         game_genre = (TextView) findViewById(R.id.insert_genre);

         game_summary = (TextView) findViewById(R.id.insert_summary);
         game_summary.setMovementMethod(new ScrollingMovementMethod());

         mRatingBar = (RatingBar) findViewById(R.id.star_rating);

         mDatabaseRef.child(mRef_key).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                String insert_genre = (String) dataSnapshot.child("genre").getValue();
                String insert_summary = (String) dataSnapshot.child("summary").getValue();
                String insert_image = (String) dataSnapshot.child("imageUrl").getValue();

                mRatingBar = (RatingBar) findViewById(R.id.star_rating);

                game_genre.setText(insert_genre);
                game_summary.setText(insert_summary);

                //This code was found on StackOverFlow and can be found at:
                // https://stackoverflow.com/questions/46645568/how-to-display-rating-in-ratingbar-from-firebase
                mDatabaseRef.child(mRef_key).child("rating").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot != null && dataSnapshot.getValue() != null) {
                            float rating = Float.parseFloat(dataSnapshot.getValue().toString());
                            mRatingBar.setRating(rating);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                Picasso.with(GameView.this).load(insert_image)
                        .fit()
                        .centerCrop()
                        .into(game_image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
