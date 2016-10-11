package fragment;

import android.os.Bundle;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;

import utils.Config;


public class YoutubeFragment extends YouTubePlayerSupportFragment {

    private String currentVideoID = "video_id";
    private YouTubePlayer activePlayer;

    public static YoutubeFragment newInstance(String url) {

        YoutubeFragment playerYouTubeFrag = new YoutubeFragment();

        Bundle bundle = new Bundle();
        bundle.putString("url", url);

        playerYouTubeFrag.setArguments(bundle);

        playerYouTubeFrag.init(); //This line right here

        return playerYouTubeFrag;
    }

    private void init() {

        initialize(Config.YOUTUBE_ANDROID_KEY, new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) { }

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                activePlayer = player;
                activePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                if (!wasRestored) {
                    activePlayer.loadVideo(getArguments().getString("url"), 0);

                }
            }
        });
    }



//    @Override
//    public void onYouTubeVideoPaused() {
//        activePlayer.pause();
//    }
}
