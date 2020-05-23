package com.example.android.musicplayer;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;


public class MainActivity extends AppCompatActivity {

    private AudioManager audioManager;
    private MediaPlayer mMediaPlayer;
    AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange (int focusStatus) {
            //                mMediaPlayer.seekTo(0);
            if (focusStatus == AUDIOFOCUS_LOSS_TRANSIENT) mMediaPlayer.pause();
            else // Resume playback
                if (focusStatus == AudioManager.AUDIOFOCUS_GAIN) mMediaPlayer.start();
                else if (focusStatus == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK)
                    mMediaPlayer.setVolume(30, 30);
                else // Stop playback
                    if (focusStatus == AudioManager.AUDIOFOCUS_LOSS) releaseMediaPlayer();
        }
    };


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        releaseMediaPlayer();

        Button playButton = findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                // Request audio focus for playback
                int result = audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

                // Audio focus gained - Start playback
                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
                    mMediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.ending_avatar);
                else if (result == AudioManager.AUDIOFOCUS_REQUEST_DELAYED)
                    Toast.makeText(MainActivity.this, "Queued", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(MainActivity.this, "Request denied!", Toast.LENGTH_LONG).show();

                mMediaPlayer.start();
                Toast.makeText(MainActivity.this, "Playing", Toast.LENGTH_SHORT).show();

                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion (MediaPlayer mediaPlayer) {
                        releaseMediaPlayer();
                        Toast.makeText(MainActivity.this, "Nickelodeon, 2012", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        Button pauseButton = findViewById(R.id.pause_button);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                mMediaPlayer.pause();
                Toast.makeText(MainActivity.this, "Paused", Toast.LENGTH_SHORT).show();
            }
        });


        Button stopButton = findViewById(R.id.stop_button);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                mMediaPlayer.reset();
                Toast.makeText(MainActivity.this, "Stopped", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Clean up the media player by releasing its resources.
     */
    private void releaseMediaPlayer () {
        // If the media player is not null, then it may be currently playing a sound
        if (mMediaPlayer != null) {
            //Regardless of the current state of the media player, release its
            // because we no longer need it.
            mMediaPlayer.release();

            // Set the media player back to null. For our, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            mMediaPlayer = null;

            // Release audio focus
            audioManager.abandonAudioFocus(onAudioFocusChangeListener);
        }
    }
}

//    @Override
//    protected void onResume () {
//        super.onResume();
//        mMediaPlayer = MediaPlayer.create(this, R.raw.ending_avatar);
//    }
//    @Override
//    protected void onStop () {
//        super.onStop();
//        // When the activity is stopped, release the media player resources because I won't be playing any more sounds.
//        releaseMediaPlayer();
//    }