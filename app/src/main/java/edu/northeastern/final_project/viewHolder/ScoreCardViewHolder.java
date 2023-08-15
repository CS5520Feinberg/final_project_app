package edu.northeastern.final_project.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import edu.northeastern.final_project.R;

public class ScoreCardViewHolder extends RecyclerView.ViewHolder {
    public ImageView userImage;
    public TextView name;
    public TextView stepCount;
    public CardView cardView;
    public ScoreCardViewHolder(@NonNull View itemView) {
        super(itemView);
        cardView = itemView.findViewById(R.id.score_card);
        userImage = itemView.findViewById(R.id.imageView_user);
        name = itemView.findViewById(R.id.imageView_user_name);
        stepCount = itemView.findViewById(R.id.score_text_view);
    }
}
