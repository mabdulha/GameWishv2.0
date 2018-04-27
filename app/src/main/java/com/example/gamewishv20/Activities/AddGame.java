
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class AddGame extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Button chooseBtn, addBtn;
    private EditText editName, editSummary;
    private Spinner spinGenre;
    private ImageView imgUpload;
    private ProgressBar progBar;

    private Uri imageUri;

    private StorageReference mStrorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;

    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_game);

        chooseBtn = (Button) findViewById(R.id.img_upload_btn);
        addBtn = (Button) findViewById(R.id.btn_add_db);
        editName = (EditText) findViewById(R.id.edit_game_name);
        editSummary = (EditText) findViewById(R.id.edit_game_summary);
        spinGenre = (Spinner) findViewById(R.id.spinner_genres);
        imgUpload = (ImageView) findViewById(R.id.image_view);
        progBar = (ProgressBar) findViewById(R.id.progress_bar);
        ratingBar = (RatingBar) findViewById(R.id.bar_rating);


        mStrorageRef = FirebaseStorage.getInstance().getReference("games");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("games");

        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFilePicker();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(AddGame.this, "Upload in Progress", Toast.LENGTH_SHORT).show();
                }
                else {
                    uploadGame();
                }
            }
        });
    }

    private void openFilePicker(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();

            Picasso.with(this).load(imageUri).into(imgUpload);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadGame() {
        if(imageUri != null && editName.length() != 0 && editSummary.length() != 0) {
            StorageReference fileReference = mStrorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            mUploadTask = fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progBar.setProgress(0);
                                }
                            }, 500);

                            Toast.makeText(AddGame.this, "Upload Sucessfull", Toast.LENGTH_LONG).show();
                            Game game = new Game(editName.getText().toString().trim(),
                                    editSummary.getText().toString().trim(),
                                    spinGenre.getSelectedItem().toString(),
                                    ratingBar.getRating(),
                                    taskSnapshot.getDownloadUrl().toString());
                            String gameId = mDatabaseRef.push().getKey();
                            mDatabaseRef.child(gameId).setValue(game);

                            startActivity(new Intent(AddGame.this, MainActivity.class));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddGame.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(this, "Please enter Data into all Fields", Toast.LENGTH_SHORT).show();
        }
    }
}