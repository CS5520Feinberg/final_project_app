package edu.northeastern.final_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.final_project.R;
import edu.northeastern.final_project.backgroundThreadClass.DownloadImageThread;
import edu.northeastern.final_project.entity.ScoreCard;
import edu.northeastern.final_project.viewHolder.ScoreCardViewHolder;

public class ScoreCardAdapter extends RecyclerView.Adapter<ScoreCardViewHolder> {

    List<ScoreCard> scoreCards;

    public void setScoreCards(List<ScoreCard> scoreCards) {
        this.scoreCards = scoreCards;
    }

    Context context;

    public ScoreCardAdapter(List<ScoreCard> scoreCards, Context context) {
        this.scoreCards = scoreCards;
        this.context = context;
    }

    @NonNull
    @Override
    public ScoreCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_view_larderboard,parent,false);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        return new ScoreCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreCardViewHolder holder, int position) {
        if(position == 0){
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.gold));
        }
        if(position == 1){
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.silver));
        }
        if(position == 2){
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.bronze));
        }

        holder.name.setText(scoreCards.get(position).getContact().getName());
        holder.stepCount.setText(""+scoreCards.get(position).getStepCount());
        if (scoreCards.get(position).getContact().getImage_uri() != null) {
            new DownloadImageThread(scoreCards.get(position).getContact().getImage_uri() , holder.userImage).execute();
        } else {
            holder.userImage.setImageResource(R.drawable.default_face_image_contacts);
            //set default image avatar
        }

    }
    @Override
    public int getItemCount() {
        return scoreCards.size();
    }
}
