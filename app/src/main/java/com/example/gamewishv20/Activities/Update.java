package com.example.gamewishv20.Activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gamewishv20.Models.Game;
import com.example.gamewishv20.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

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
                String insert_genre = (String) dataSnapshot.child("genre").getValue();
                String insert_summary = (String) dataSnapshot.child("summary").getValue();
                String insert_image = (String) dataSnapshot.child("imageUrl").getValue();
                //long insert_rating = (long) dataSnapshot.child("rating").getValue();

                updateName.setText(insert_name);

                //This spinner code was found on StackOverflow:
                //https://stackoverflow.com/questions/11072576/set-selected-item-of-spinner-programmatically
                //It is the second one under the verified comment
                updateSpinGenre.setSelection(((ArrayAdapter)updateSpinGenre.getAdapter()).getPosition(insert_genre));
                updateSummary.setText(insert_summary);

                mDatabaseRef.child(mRef_key).child("rating").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot != null && dataSnapshot.getValue() != null) {
                            float rating = Float.parseFloat(dataSnapshot.getValue().toString());
                            updateRatingBar.setRating(rating);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                Picasso.with(Update.this).load(insert_image)
                        .fit()
                        .centerCrop()
                        .into(updateImage);

                chooseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openFilePicker();
                    }
                });

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

    private void openFilePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();

            Picasso.with(this).load(imageUri).into(updateImage);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void updateGame() {
        if (imageUri != null || updateName.length() != 0 || updateSummary.length() != 0) {
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                   + "." + getFileExtension(imageUri));

            mUploadTask = fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    updateProgBar.setProgress(0);
                                }
                            }, 500);

                            Toast.makeText(Update.this, "Update Sucessfull", Toast.LENGTH_LONG).show();
                            Game game = new Game(updateName.getText().toString().trim(),
                                    updateSummary.getText().toString().trim(),
                                    updateSpinGenre.getSelectedItem().toString(),
                                    updateRatingBar.getRating(),
                                    taskSnapshot.getDownloadUrl().toString());
                            mDatabaseRef.child(mRef_key).setValue(game);

                            startActivity(new Intent(Update.this, GameList.class));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Update.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            updateProgBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(this, "Please update the image", Toast.LENGTH_SHORT).show();
        }
    }
}