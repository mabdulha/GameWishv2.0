package com.example.gamewishv20.Activities;

import android.content.Context;
import android.content.Intent;
import android.media.Rating;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gamewishv20.Models.Game;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.example.gamewishv20.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    private DatabaseReference mDatabaseRef;
    private boolean mProcessWish = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("games");

        mRecyclerView = (RecyclerView) findViewById(R.id.RecycleView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Game, GamesViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Game, GamesViewHolder>(
                Game.class,
                R.layout.image_card_view,
                GamesViewHolder.class,
                mDatabaseRef) {

            @Override
            protected void populateViewHolder(GamesViewHolder viewHolder, Game model, final int position)  {

                final String ref_key = getRef(position).getKey();

                viewHolder.setName(model.getName());
                viewHolder.setImage(getApplicationContext(), model.getImageUrl());

                viewHolder.setWishButton(ref_key);

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent game_intent = new Intent(MainActivity.this, GameView.class);
                        game_intent.putExtra("gamev_id", ref_key);
                        startActivity(game_intent);
                    }
                });

                viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        Intent update_intent = new Intent(MainActivity.this, Update.class);
                        update_intent.putExtra("gamev_id", ref_key);
                        startActivity(update_intent);
                       // Toast.makeText(MainActivity.this, "Key" + ref_key, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });

                viewHolder.wishButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mProcessWish = true;


                            mDatabaseRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (mProcessWish) {

                                    if (dataSnapshot.child(ref_key).hasChild("user")) {

                                        mDatabaseRef.child(ref_key).child("user").removeValue();
                                        mProcessWish = false;
                                    } else {
                                        mDatabaseRef.child(ref_key).child("user").setValue("wished");
                                        mProcessWish = false;

                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                    }
                });
            }
        };

        mRecyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    public static class GamesViewHolder extends RecyclerView.ViewHolder {

        View mView;

        ImageButton wishButton;

        DatabaseReference mDatabaseRef;

        public GamesViewHolder(View itemView) {
            super(itemView);
            mView= itemView;

            wishButton = (ImageButton) mView.findViewById(R.id.wish_icon);

            mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("games");

        }



        public void setWishButton(final String ref_key) {
            mDatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(ref_key).hasChild("user")) {

                        wishButton.setImageResource(R.drawable.ic_favorite_black_24dp);
                    }
                    else {
                        wishButton.setImageResource(R.drawable.ic_empty_fav);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void setName(String name) {
            TextView insert_title = (TextView) mView.findViewById(R.id.textViewName);
            insert_title.setText(name);
        }

        public void setImage(Context mContext, String imageUrl) {

            ImageView insert_image = (ImageView) mView.findViewById(R.id.imageViewList);
            Picasso.with(mContext).load(imageUrl)
                    .fit()
                    .centerCrop()
                    .into(insert_image);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuSearch:
                Intent intent = new Intent(MainActivity.this, Search.class);
                startActivity(intent);
                break;

            case R.id.action_add:
                Intent intent1 = new Intent(MainActivity.this, AddGame.class);
                startActivity(intent1);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
