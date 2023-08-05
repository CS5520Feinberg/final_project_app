package edu.northeastern.final_project.viewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.northeastern.final_project.R;

public class ContactsViewHolder extends RecyclerView.ViewHolder {

    public TextView contact_name;
    private Button action_on_contact;

    public TextView getContact_name() {
        return contact_name;
    }

    public Button getAction_on_contact() {
        return action_on_contact;
    }

    public ContactsViewHolder(@NonNull View itemView) {
        super(itemView);
        this.contact_name = itemView.findViewById(R.id.textview_contact);
       this.action_on_contact = itemView.findViewById(R.id.button_action_on_contact);
    }

}
