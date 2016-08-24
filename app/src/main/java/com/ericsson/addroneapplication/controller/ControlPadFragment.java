package com.ericsson.addroneapplication.controller;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import com.ericsson.addroneapplication.R;

/**
 * Created by Kamil on 8/23/2016.
 */
public class ControlPadFragment extends Fragment {

    VideoView videoView;
    DisplayMetrics displayMetrics;
    MediaController mediaController;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_camera, container, false);

        videoView = (VideoView) root.findViewById(R.id.video_view);
        mediaController = new MediaController(getActivity());
        displayMetrics = new DisplayMetrics();

        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        videoView.setMinimumWidth(displayMetrics.widthPixels);
        videoView.setMinimumHeight(displayMetrics.heightPixels);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(Uri.parse("rtsp://mpv.cdn3.bigCDN.com:554/bigCDN/definst/mp4:bigbuckbunnyiphone_400.mp4"));
        videoView.start();

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        videoView.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        videoView.resume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        videoView.stopPlayback();
    }
}