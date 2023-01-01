package com.example.musicplayernew;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class MusicAdpter extends RecyclerView.Adapter<MusicAdpter.MyViewHolding> {
    private final List<Music> musicList;
    private final OnMusicListener musicListener;
    private int currentMusicPosition=-1;
    public MusicAdpter(List<Music> musicList,OnMusicListener musicListener){
        this.musicListener=musicListener;
        this.musicList=musicList;
    }
    @NonNull
    @Override
    public MyViewHolding onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_music,parent,false);
        return new MyViewHolding(view);
    }



    @Override
    public void onBindViewHolder(@NonNull MyViewHolding holder, int position) {
            holder.bindMusic(musicList.get(position));
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public class MyViewHolding extends RecyclerView.ViewHolder {
        private final TextView tvMusicName;
        private final SimpleDraweeView imgCover;
        private final LottieAnimationView animationView;
        public MyViewHolding(@NonNull View itemView) {
            super(itemView);
            tvMusicName=itemView.findViewById(R.id.tvMusicNameList);
            imgCover=itemView.findViewById(R.id.coverList);
            animationView=itemView.findViewById(R.id.animationViewMain);

        }
        public void bindMusic(Music music){
            imgCover.setActualImageResource(music.getCoverResId());
            tvMusicName.setText(music.getName());

            itemView.setOnClickListener(view -> musicListener.onClick(music,getAdapterPosition()));
            if (currentMusicPosition==getAdapterPosition()){animationView.setVisibility(View.VISIBLE);}else{animationView.setVisibility(View.GONE);}
        }

    }
    public interface OnMusicListener{
        void onClick(Music music,int musicPos);
    }
    public void getMusicPosition(Music music){
        int index=musicList.indexOf(music);
        if (index!=-1){
            if (index!=currentMusicPosition){
                notifyItemChanged(currentMusicPosition);
                currentMusicPosition=index;
                notifyItemChanged(currentMusicPosition);
            }
        }
    }


}
