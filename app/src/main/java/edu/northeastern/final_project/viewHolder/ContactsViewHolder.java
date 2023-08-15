package edu.northeastern.final_project.viewHolder;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.northeastern.final_project.R;
import edu.northeastern.final_project.adapter.ContactsAdapter;

public class ContactsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView contact_name;
    public Button action_on_contact;
    public ImageView imageView;
    private ContactsAdapter adapter;

    Context context;



    public ContactsViewHolder(@NonNull View itemView, RecyclerView.Adapter<ContactsViewHolder> adapter,Context context) {
        super(itemView);
        this.contact_name = itemView.findViewById(R.id.textview_contact);
        this.action_on_contact = itemView.findViewById(R.id.button_action_on_contact);
        this.imageView = itemView.findViewById(R.id.image_view_contact);
        action_on_contact.setOnClickListener(this);
        this.adapter = (ContactsAdapter) adapter;
        this.context = context;
    }


    @Override
    public void onClick(View v) {
        int position = getAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {
            if (action_on_contact.getText().equals("Follow")) {
                // add the number to follower and following list

                action_on_contact.setText("FOLLOWED");
                adapter.deletePosition(position);
            }
        }
        if (position != RecyclerView.NO_POSITION) {
            if (action_on_contact.getText().equals("Invite")) {
                Toast.makeText(context,"This feature is in future scope",Toast.LENGTH_LONG).show();
            }
        }
    }
}
