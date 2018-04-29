package com.example.gamewishv20.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gamewishv20.Models.Game;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.example.gamewishv20.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    private DatabaseReference mDatabaseRef;

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

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent game_intent = new Intent(MainActivity.this, GameView.class);
                        game_intent.putExtra("gamev_id", ref_key);
                        startActivity(game_intent);
                    }
                });

                viewHolder.updateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent update_intent = new Intent(MainActivity.this, Update.class);
                        update_intent.putExtra("gamev_id", ref_key);
                        startActivity(update_intent);
                    }
                });

                viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Deletion Confirmation")
                                .setMessage("Are you sure you want to delete this game?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        mDatabaseRef.child(ref_key).removeValue();
                                        Toast.makeText(MainActivity.this, "Game Deleted", Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .show();
                    }
                });
            }
        };

        mRecyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    public static class GamesViewHolder extends RecyclerView.ViewHolder {

        View mView;

        ImageButton updateButton, deleteButton;

        DatabaseReference mDatabaseRef;

        public GamesViewHolder(View itemView) {
            super(itemView);
            mView= itemView;

            updateButton = (ImageButton) mView.findViewById(R.id.update_icon);
            deleteButton = (ImageButton) mView.findViewById(R.id.delete_icon);

            mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("games");

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