package edu.northeastern.final_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.final_project.R;
import edu.northeastern.final_project.backgroundThreadClass.AddFollowingDataToFirebase;
import edu.northeastern.final_project.backgroundThreadClass.DownloadImageThread;
import edu.northeastern.final_project.entity.Contact;
import edu.northeastern.final_project.viewHolder.ContactsViewHolder;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsViewHolder> {
    List<Contact> contacts;
    Context context;
    String action;

    public List<Contact> getContacts() {
        return contacts;
    }

    public ContactsAdapter(List<Contact> contacts, Context context, String action) {
        this.contacts = contacts;
        this.context = context;
        this.action = action;
    }

    @NonNull
    @Override
    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_view_holder,parent,false);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);

        return new ContactsViewHolder(view,this);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsViewHolder holder, int position) {
        holder.contact_name.setText(contacts.get(position).getName());
        holder.action_on_contact.setText(action);
        if(action.equals("Follow")){
            if(contacts.get(position).getImage_uri()!=null){
                new DownloadImageThread(contacts.get(position).getImage_uri(),holder.imageView).execute();
            }else{
                holder.imageView.setImageResource(R.drawable.default_face_image_contacts);
                //set default image avatar
            }
        }else if(action.equals("followers")){
            if(contacts.get(position).getImage_uri()!=null){
                new DownloadImageThread(contacts.get(position).getImage_uri(),holder.imageView).execute();
            }else{
                holder.imageView.setImageResource(R.drawable.default_face_image_contacts);
                //set default image avatar
            }
            holder.action_on_contact.setVisibility(View.INVISIBLE);
        }else{
            holder.imageView.setImageResource(R.drawable.blank_face_invite_contacts);
            //invite set default
        }


    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public void deletePosition(int position){
        Contact contact = contacts.get(position);

        if(action.equals("Follow")){
            String following_contact_number = contact.getPhone_number();
            contacts.remove(position);
            notifyDataSetChanged();
            Toast.makeText(context,"Started Following",Toast.LENGTH_SHORT).show();
            new AddFollowingDataToFirebase(following_contact_number).execute();
        }

    }

}
