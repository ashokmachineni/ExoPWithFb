package com.ashok.exopwithfb;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private DatabaseReference mDatabase;
    public FirebaseRecyclerAdapter<Movie, MovieViewHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Movie");
        mRecyclerView = (RecyclerView) findViewById(R.id.recview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Movie, MovieViewHolder>(
                Movie.class,
                R.layout.video_list,
                MovieViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(final MovieViewHolder viewHolder, final Movie model, int position) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setImage(getApplicationContext(), model.getImage());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /** PLAYER */
                        UriSample uriSample = new UriSample("Test", null, null, null, false, model.getLink(), null);
                        startActivity(uriSample.buildIntent(getApplicationContext()));
                    }
                });
            }
        };
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Movie, MovieViewHolder>(
//                Movie.class,
//                R.layout.video_list,
//                MovieViewHolder.class,
//                mDatabase
//        ) {
//            @Override
//            protected void populateViewHolder(final MovieViewHolder viewHolder, final Movie model, int position) {
//                viewHolder.setTitle(model.getTitle());
//                viewHolder.setImage(getApplicationContext(), model.getImage());
//
//                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        /** PLAYER */
//                        UriSample uriSample = new UriSample("Test", null, null, null, false, model.getLink(), null);
//                        startActivity(uriSample.buildIntent(getApplicationContext()));
//                    }
//                });
//            }
//        };
//        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTitle(String title) {
            TextView movie_title = (TextView) mView.findViewById(R.id.movie_title);
            movie_title.setText(title);
        }

        public void setImage(Context context, String image) {
            ImageView movie_image = (ImageView) mView.findViewById(R.id.movie_image);
            Picasso.with(context).load(image).into(movie_image);
        }
    }

    /**
     * EXOPLAYER
     */
    private abstract static class Sample {

        public final String name;
        public final boolean preferExtensionDecoders;
        public final UUID drmSchemeUuid;
        public final String drmLicenseUrl;
        public final String[] drmKeyRequestProperties;

        public Sample(String name, UUID drmSchemeUuid, String drmLicenseUrl,
                      String[] drmKeyRequestProperties, boolean preferExtensionDecoders) {
            this.name = name;
            this.drmSchemeUuid = drmSchemeUuid;
            this.drmLicenseUrl = drmLicenseUrl;
            this.drmKeyRequestProperties = drmKeyRequestProperties;
            this.preferExtensionDecoders = preferExtensionDecoders;

        }

        public Intent buildIntent(Context context) {
            Intent intent = new Intent(context, PlayerActivity.class);
            intent.putExtra(PlayerActivity.PREFER_EXTENSION_DECODERS, preferExtensionDecoders);
            if (drmSchemeUuid != null) {
                intent.putExtra(PlayerActivity.DRM_SCHEME_UUID_EXTRA, drmSchemeUuid.toString());
                intent.putExtra(PlayerActivity.DRM_LICENSE_URL, drmLicenseUrl);
                intent.putExtra(PlayerActivity.DRM_KEY_REQUEST_PROPERTIES, drmKeyRequestProperties);
            }
            return intent;
        }
    }

    private static final class UriSample extends Sample {

        public final String uri;
        public final String extension;

        public UriSample(String name, UUID drmSchemeUuid, String drmLicenseUrl,
                         String[] drmKeyRequestProperties, boolean preferExtensionDecoders, String uri,
                         String extension) {
            super(name, drmSchemeUuid, drmLicenseUrl, drmKeyRequestProperties, preferExtensionDecoders);
            this.uri = uri;
            this.extension = extension;
        }

        @Override
        public Intent buildIntent(Context context) {
            return super.buildIntent(context)
                    .setData(Uri.parse(uri))
                    .putExtra(PlayerActivity.EXTENSION_EXTRA, extension)
                    .setAction(PlayerActivity.ACTION_VIEW);
        }
    }
}
