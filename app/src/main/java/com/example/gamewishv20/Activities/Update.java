package com.example.gamewishv20.Activities;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gamewishv20.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Update extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Button chooseBtn, updateBtn;
    private EditText updateName, updateSummary;
    private Spinner updateSpinGenre;
    private ImageView updateImage;
    private ProgressBar updateProgBar;

    private Uri imageUri;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;

    private RatingBar updateRatingBar;

    private String mRef_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("games");
        mStorageRef = FirebaseStorage.getInstance().getReference().child("games");

        mRef_key = getIntent().getExtras().getString("gamev_id");
        Toast.makeText(Update.this, "Key " + mRef_key, Toast.LENGTH_SHORT).show();

        chooseBtn = (Button) findViewById(R.id.img_update_btn);
        updateBtn = (Button) findViewById(R.id.btn_update_db);
        updateName = (EditText) findViewById(R.id.update_game_name);
        updateSpinGenre = (Spinner) findViewById(R.id.update_spinner_genres);
        updateSummary = (EditText) findViewById(R.id.update_game_summary);
        updateImage = (ImageView) findViewById(R.id.update_image_view);
        updateProgBar = (ProgressBar) findViewById(R.id.update_progress_bar);
        updateRatingBar = (RatingBar) findViewById(R.id.update_bar_rating);

       mDatabaseRef.child(mRef_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String insert_name = (String) dataSnapshot.child("name").getValue();
                //String insert_genre = (String) dataSnapshot.child("genre").getValue();
                String insert_summary = (String) dataSnapshot.child("summary").getValue();
                String insert_image = (String) dataSnapshot.child("imageUrl").getValue();
                long insert_rating = (long) dataSnapshot.child("rating").getValue();

                updateName.setText(insert_name);
                // updateSpinGenre.setSelection(insert_genre);
                updateSummary.setText(insert_summary);
                updateRatingBar.setRating(insert_rating);
                Picasso.with(Update.this).load(insert_image)
                        .fit()
                        .centerCrop()
                        .into(updateImage);

                updateBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mUploadTask != null && mUploadTask.isInProgress()) {
                            Toast.makeText(Update.this, "Upload in Progress", Toast.LENGTH_SHORT).show();
                        } else {
                            updateGame();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateGame() {
        Toast.makeText(Update.this, "Updating", Toast.LENGTH_SHORT).show();
    }
}