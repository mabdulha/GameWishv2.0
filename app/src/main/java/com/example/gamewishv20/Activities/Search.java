package com.example.gamewishv20.Activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gamewishv20.Models.Game;
import com.example.gamewishv20.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class Search extends AppCompatActivity {

    private EditText mSearchText;
    private Button mSearchBtn;

    private RecyclerView mResultList;

    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mSearchBtn = (Button) findViewById(R.id.search_btn);
        mSearchText = (EditText) findViewById(R.id.search_edit);

        mResultList = (RecyclerView) findViewById(R.id.result_list);
        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this));

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("games");

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String searchText = mSearchText.getText().toString();
                firebaseUserSearch(searchText);
            }
        });
    }

    private void firebaseUserSearch (String searchText) {

        Query firebaseSearchQuery = mDatabaseRef.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerAdapter<Game, GamesViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Game, GamesViewHolder>(
                Game.class,
                R.layout.list_layout,
                GamesViewHolder.class,
                firebaseSearchQuery

        ) {
            @Override
            protected void populateViewHolder(GamesViewHolder viewHolder, Game model, int position) {

                viewHolder.setDetails(getApplicationContext(), model.getName(), model.getImageUrl());
            }
        };

        mResultList.setAdapter(firebaseRecyclerAdapter);
    }



        public static class GamesViewHolder extends RecyclerView.ViewHolder {

            View mView;

            public GamesViewHolder(View itemView) {
                super(itemView);

                mView = itemView;

            }

            public void setDetails(Context c, String gameName, String gameImage) {
                TextView game_name = (TextView) mView.findViewById(R.id.return_text);
                ImageView game_image = (ImageView) mView.findViewById(R.id.return_image_view);

                game_name.setText(gameName);
                Picasso.with(c).load(gameImage)
                        .fit()
                        .centerCrop()
                        .into(game_image);
            }

        }
}
