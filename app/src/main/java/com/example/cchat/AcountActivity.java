package com.example.cchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AcountActivity extends AppCompatActivity {

    private TextView acountUserName;
    private TextView acountSignOut;
    private ImageButton acountAddImageButton;
    private CircleImageView acountImageView;

    private static final int RC_IMAGE_PICKER=123;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference userDatabaseReference;
    private ChildEventListener userChilEventListener;
    private StorageReference accountImageStorageReference;
    private FirebaseStorage storage;

    private String userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acount);

        setTitle("Acount");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        acountUserName=findViewById(R.id.acountUserName);
        acountSignOut=findViewById(R.id.acountSignOut);
        acountAddImageButton=findViewById(R.id.acountAddImageButton);
        acountImageView=findViewById(R.id.acountImageView);

        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();
        accountImageStorageReference=storage.getReference().
                child("account_images").child(auth.getUid());
        userDatabaseReference= FirebaseDatabase.getInstance().
                  getReference().child("users");


        Log.d("TAG", "onCreate: "+database.getReference().child("users").child(auth.getUid()));

        acountSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(AcountActivity.this, SignInActivity.class));
            }
        });
        acountAddImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent();
                intent1.setAction(Intent.ACTION_GET_CONTENT);
                intent1.setType("image/*");
                intent1.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent1, RC_IMAGE_PICKER);
            }
        });


        database.getReference().child("users").child(auth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user=snapshot.getValue(User.class);

                            userName = user.getName();
                            acountUserName.setText(userName);

                            Picasso.get().load(user.getImageUri())
                                    .placeholder(R.drawable.baseline_person_24)
                                    .into(acountImageView);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(data.getData()!=null){
            Uri fileUri=data.getData();
            acountImageView.setImageURI(fileUri);

               final StorageReference reference=storage.getReference().child("account_images")
                       .child( auth.getUid());
               reference.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                   @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            database.getReference().child("users").child(auth.getUid())
                                    .child("imageUri").setValue(uri.toString());
                        }
                    });
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                startActivity(new Intent(AcountActivity.this, UserListActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}