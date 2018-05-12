package com.addrone.controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.addrone.R;
import com.addrone.service.AdDroneService;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Kamil on 8/23/2016.
 */
public class ControlPadFragment extends Fragment implements StreamConnection.OnNewFrameListener {

    ImageView imageView;
    StreamConnection streamConnection;

    Bitmap bitmap;
    Lock bitmapLock = new ReentrantLock();

    Runnable setImageBitmapRunnable = new Runnable() {
        @Override
        public void run() {
            imageView.setImageBitmap(bitmap);
        }
    };

    Timer timer;
    TimerTask timerUpdateTask = new TimerTask() {
        @Override
        public void run() {
            bitmapLock.lock();
            ControlPadFragment.this.getActivity().runOnUiThread(setImageBitmapRunnable);
            bitmapLock.unlock();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_camera, container, false);

        streamConnection = new StreamConnection(AdDroneService.currentIp, this);
        imageView = root.findViewById(R.id.image_view);

        timer = new Timer();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        streamConnection.start();
        timer = new Timer();
        timer.scheduleAtFixedRate(timerUpdateTask, 0, 40);
    }

    @Override
    public void onPause() {
        super.onPause();
        streamConnection.disconnect();
        timer.cancel();
    }

    @Override
    public void setNewFrame(final byte[] array, final int length) {
        bitmapLock.lock();
        bitmap = BitmapFactory.decodeByteArray(array, 0, length);
        bitmapLock.unlock();
    }

    public ImageView getImageView() {
        return imageView;
    }
}