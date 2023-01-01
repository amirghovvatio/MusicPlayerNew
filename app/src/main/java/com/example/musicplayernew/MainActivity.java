package com.example.musicplayernew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import com.example.musicplayernew.databinding.ActivityMainBinding;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.material.slider.Slider;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity  implements MusicAdpter.OnMusicListener {
    private ActivityMainBinding binder;
    private List<Music> musicList=Music.getList();
    private Timer timer;
    private MediaPlayer mediaPlayer;

    @Override
    public void onClick(Music music,int musicPos) {
        timer.purge();
        timer.cancel();
        mediaPlayer.release();
        cursor=musicPos;
        onMusicChange(musicList.get(cursor));
    }

    private enum MUSIC_STATE{PLAY,STOP};
    private MUSIC_STATE music_state;
    private int cursor=0;
    private boolean isDragging;
    private MusicAdpter musicAdpter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this);
        binder=ActivityMainBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binder.getRoot());
        musicAdpter=new MusicAdpter(musicList,this);

        binder.rvMain.setAdapter(musicAdpter);
        binder.rvMain.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));


        binder.sliderMain.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                     binder.tvStartDuration.setText(Music.convertMillisToString((long) slider.getValue()));
            }
        });
        binder.sliderMain.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
                isDragging=true;
                mediaPlayer.seekTo((int) slider.getValue());

            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                isDragging=false;


            }
        });
        binder.skipIcMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goPrev();
            }
        });
        binder.skipForwardIcMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goNext();
            }
        });

        onMusicChange(musicList.get(cursor));



    }
    public void onMusicChange(Music music){
        musicAdpter.getMusicPosition(music);
        mediaPlayer= MediaPlayer.create(this, music.getMusicFileResId());

        timer=new Timer();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                binder.sliderMain.setValueTo(mediaPlayer.getDuration());
//
//                mediaPlayer.start();
                  music_state=MUSIC_STATE.STOP;

                binder.playIcMain.setImageResource(R.drawable.play_ic);
                binder.sliderMain.setValue(0);

                binder.playIcMain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (music_state){
                            case PLAY:
                                mediaPlayer.pause();
                                binder.playIcMain.setImageResource(R.drawable.play_ic);
                                  music_state=MUSIC_STATE.STOP;
                                break;
                            case STOP:
                                mediaPlayer.start();
                                binder.playIcMain.setImageResource(R.drawable.ic_baseline_pause_24);

                                music_state=MUSIC_STATE.PLAY;

                                break;
                        }
                    }
                });
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!isDragging) {
                                    binder.sliderMain.setValue(mediaPlayer.getCurrentPosition());
                                    binder.tvStartDuration.setText(Music.convertMillisToString(mediaPlayer.getCurrentPosition()));
                                }
                            }
                        });
                    }
                },1000,1000);

            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                binder.sliderMain.setValue(0);

                timer.cancel();
                timer.purge();
                goNext();

            }
        });


        binder.tvMusicNameMain.setText(music.getName());
        binder.tvEndDurationMain.setText(Music.convertMillisToString(mediaPlayer.getDuration()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        timer.purge();
        timer.cancel();
    }
    public void goNext(){
        timer.purge();
        timer.cancel();
        mediaPlayer.release();
        if (cursor<musicList.size()-1){
            cursor++;
        }else{cursor=0;}
        onMusicChange(musicList.get(cursor));
    }
    public void goPrev(){
        timer.cancel();
        timer.purge();
        mediaPlayer.release();
        if (cursor==0){
            cursor=musicList.size()-1;
        }else{cursor--;}
        onMusicChange(musicList.get(cursor));
    }
}