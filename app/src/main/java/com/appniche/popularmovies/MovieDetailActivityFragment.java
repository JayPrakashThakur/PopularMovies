package com.appniche.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailActivityFragment extends Fragment {

    private Movie mMovie;

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        Intent intent = getActivity().getIntent();

        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)){

            mMovie = intent.getParcelableExtra(Intent.EXTRA_TEXT);

            TextView originalTitle = (TextView)rootView.findViewById(R.id.original_title);
            originalTitle.setText(mMovie.getmTitle());

            ImageView imageView = (ImageView)rootView.findViewById(R.id.movie_thumbnail);

            Picasso
                    .with(getActivity())
                    .load(mMovie.getmPosterUrl())
                    .into(imageView);

            TextView releaseDate = (TextView)rootView.findViewById(R.id.releaseDate_textView);
            releaseDate.setText(mMovie.getmReleaseDate());

            TextView userRating = (TextView)rootView.findViewById(R.id.userRating_textView);
            userRating.setText(mMovie.getmRating());

            TextView overview = (TextView)rootView.findViewById(R.id.overview_textView);
            overview.setText(mMovie.getmOverview());

        }

        return rootView;
    }
}
