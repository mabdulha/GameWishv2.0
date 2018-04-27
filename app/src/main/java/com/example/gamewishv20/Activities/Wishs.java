package com.example.gamewishv20.Activities;

import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gamewishv20.Models.Game;
import com.example.gamewishv20.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Wishs extends AppCompatActivity {

   private String mRef_key = null;

   private DatabaseReference mDatabaseRef;

   private ImageView wish_image;
   private TextView wish_name;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_wish_view);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("games");

        mRef_key = getIntent().getExtras().getString("wishv_id");

        wish_image = (ImageView) findViewById(R.id.wish_image);
        wish_name = (TextView) findViewById(R.id.wish_name);

        mDatabaseRef.child(mRef_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String insert_wName = (String) dataSnapshot.child("name").getValue();
                String insert_wImageUrl = (String) dataSnapshot.child("imageUrl").getValue();

                Picasso.with(Wishs.this).load(insert_wImageUrl)
                        .fit()
                        .centerCrop()
                        .into(wish_image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}