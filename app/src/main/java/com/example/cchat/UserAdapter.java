package com.example.cchat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private ArrayList<User> users;
    private OnUserClickListener listener;
    private String lastMessage;

    public interface OnUserClickListener{
        void onUserClick(int position);
    }

    public void setOnUserClickListener(OnUserClickListener listener){
        this.listener=listener;
    }

    public UserAdapter(ArrayList<User> users){
        this.users=users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        UserViewHolder viewHolder=new UserViewHolder(view, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User currentUser=users.get(position);
        Glide.with(holder.avatarImageView.getContext())
                .load(currentUser.getImageUri())
                .into(holder.avatarImageView);
        holder.userNameTextView.setText(currentUser.getName());
        //lastMessage(currentUser.getId(), holder.lastMessageTextView);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class UserViewHolder extends  RecyclerView.ViewHolder{

        public CircleImageView avatarImageView;
        public TextView userNameTextView, lastMessageTextView;

        public UserViewHolder(@NonNull View itemView,final OnUserClickListener listener) {
            super(itemView);
            avatarImageView=itemView.findViewById(R.id.avatarImageView);
            userNameTextView=itemView.findViewById(R.id.userNameTextView);
            lastMessageTextView=itemView.findViewById(R.id.lastMessageTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null){
                        int position=getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            listener.onUserClick(position);
                        }
                    }
                }
            });
        }
    }
  /*  private void lastMessage(String userId, TextView lastMessageTextView){
        lastMessage="default";

        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("messages");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    CMessage message=snapshot.getValue(CMessage.class);
                    if(message.getRecipient().equals(firebaseUser.getUid())
                            && message.getSender().equals(userId) ||
                            message.getSender().equals(firebaseUser.getUid())
                            && message.getRecipient().equals(userId)){
                        lastMessage=message.getText();
                    }
                }
                switch (lastMessage){
                    case "default":
                        lastMessageTextView.setText("Start Messaging! ");
                        break;
                    default:
                        lastMessageTextView.setText(lastMessage);
                        break;
                }
                lastMessage="default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/
}
