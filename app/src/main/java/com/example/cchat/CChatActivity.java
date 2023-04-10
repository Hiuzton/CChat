package com.example.cchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class CChatActivity extends AppCompatActivity {


    private ListView messageListView;
    private CChatAdapter adapter;
    private ProgressBar progressBar;
    private ImageButton sendImageButton;
    private ImageButton sendMessageButton;
    private EditText messageEditText;

    private String userName;
    private String recipientUserId;
    private String recipientUserName;
    private static final int RC_IMAGE_PICKER=124;

    private FirebaseAuth auth;
    private FirebaseDatabase database ;
    private DatabaseReference databaseReferenceMessages;
    private ChildEventListener messagesChildEventListener;
    private DatabaseReference usersDatabaseReference;
    private ChildEventListener usersChildEventListener;
    private FirebaseStorage storage;
    private StorageReference chatImageStorageReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cchat);

        Intent intent=getIntent();
        if(intent!=null){
            recipientUserId=intent.getStringExtra("recipientUserId");
            userName=intent.getStringExtra("userName");
            recipientUserName=intent.getStringExtra("recipientUserName");
        }


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setTitle(recipientUserName);
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();
        databaseReferenceMessages=database.getReference().child("messages");

        usersDatabaseReference = database.getReference().child("users");
        chatImageStorageReference=storage.getReference().child("chat_images");

        messageListView=findViewById(R.id.messageListView);
        progressBar=findViewById(R.id.progressBar);
        sendImageButton=findViewById(R.id.sendPhotoButton);
        sendMessageButton=findViewById(R.id.sendMessageButton);
        messageEditText=findViewById(R.id.messageEditText);

        List<CMessage> cMessageList=new ArrayList<>();
        adapter=new CChatAdapter(this, R.layout.message_item,
                cMessageList);
        messageListView.setAdapter(adapter);

        progressBar.setVisibility(ProgressBar.INVISIBLE);

        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(charSequence.toString().trim().length()>0){
                    sendMessageButton.setEnabled(true);
                }else{
                    sendMessageButton.setEnabled(false);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        messageEditText.setFilters(new InputFilter[]
                {
                 new InputFilter.LengthFilter(500)
                });



        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CMessage message=new CMessage();
                message.setText(messageEditText.getText().toString());
                message.setName(userName);
                message.setSender(auth.getCurrentUser().getUid());
                message.setRecipient(recipientUserId);
                message.setImageUrl(null);
                databaseReferenceMessages.push().setValue(message);

                messageEditText.setText("");

            }
        });

        sendImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(Intent.ACTION_GET_CONTENT);
                intent1.setType("image/*");
                intent1.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent1, "Choose an image"),
                        RC_IMAGE_PICKER);
            }
        });

        usersChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user = dataSnapshot.getValue(User.class);
                if (user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    userName = user.getName();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        usersDatabaseReference.addChildEventListener(usersChildEventListener);

        messagesChildEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                CMessage message=snapshot.getValue(CMessage.class);

                if(message.getSender().equals(auth.getCurrentUser().getUid())
                && message.getRecipient().equals(recipientUserId) ){
                    message.setMineMessage(true);
                    adapter.add(message);
                }else if(message.getRecipient().equals(auth.getCurrentUser().getUid())
                        && message.getSender().equals(recipientUserId)) {
                    message.setMineMessage(false);
                    adapter.add(message);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReferenceMessages.addChildEventListener(messagesChildEventListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(CChatActivity.this, SignInActivity.class));
                return true;
            case R.id.acount:
                Intent intent = new Intent(CChatActivity.this,
                        AcountActivity.class);
                intent.putExtra("userName", userName);
                startActivity(intent);
                return true;
            case android.R.id.home:
                startActivity(new Intent(CChatActivity.this, UserListActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_IMAGE_PICKER && resultCode==RESULT_OK){
            Uri selectedImageUri=data.getData();
            final StorageReference imageReference=chatImageStorageReference
                    .child(selectedImageUri.getLastPathSegment());

            UploadTask uploadTask=imageReference.putFile(selectedImageUri);
            uploadTask = imageReference.putFile(selectedImageUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return imageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        CMessage cMessage=new CMessage();
                        cMessage.setImageUrl(downloadUri.toString());
                        cMessage.setName(userName);
                        cMessage.setSender(auth.getCurrentUser().getUid());
                        cMessage.setRecipient(recipientUserId);
                        databaseReferenceMessages.push().setValue(cMessage);
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
        }
    }
}