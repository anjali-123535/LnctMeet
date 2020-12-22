package com.example.lnctmeet.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lnctmeet.R;
import com.example.lnctmeet.fragment.BaseFragment;
import com.example.lnctmeet.model.Post;
import com.example.lnctmeet.preferences.UserSessionManager;
import com.example.lnctmeet.utils.Constants;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SavedActivity extends AppCompatActivity {
    private static final String TAG = SavedActivity.class.getName();
    FirebaseFirestore firestore;
    Query q1;
    FirestoreRecyclerAdapter<Post,PostViewHolder> postAdapter;
    FirestoreRecyclerOptions<Post> postsList;
    DocumentReference stu_Ref;
    RecyclerView recyclerView;
    String uid;
    boolean deleted;
    UserSessionManager userSessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);
        setUIViews();
        uid=userSessionManager.getUserDetails().get(UserSessionManager.KEY_LOGIN);

       // mStorage= FirebaseStorage.getInstance().getReference(uid);
        //Log.d(getContext().toString(),"inOnCreateview");
        stu_Ref=firestore.collection("Students").document(uid);
                q1=stu_Ref.collection("saved");

        postsList = new FirestoreRecyclerOptions.Builder<Post>().setQuery(q1, Post.class).build();
        postAdapter=new FirestoreRecyclerAdapter<Post, PostViewHolder>(postsList) {
            @Override
            protected void onBindViewHolder(@NonNull PostViewHolder postViewHolder, int i, @NonNull final Post post) {
                Log.d(TAG,"onBindViewHolder");
                if (post.getIMAGE_URI() != null) {
                    Glide.with(getApplicationContext()).load(post.getIMAGE_URI()).into(postViewHolder.img_doc);
                    postViewHolder.img_doc.setVisibility(View.VISIBLE);
                }
                if(post.getWEB_URL()!=null){
                    postViewHolder.link.setText(post.getWEB_URL());
                    postViewHolder.link.setVisibility(View.VISIBLE);
                }
                postViewHolder.tag.setText(post.getTAG());
                postViewHolder.desc.setText(post.getDESCRIPTION());
                postViewHolder.author.setText(post.getAUTHOR());
                postViewHolder.created.setText(post.getDATE_CREATION().toString().substring(0, 11));
                postViewHolder.save.setImageResource(R.drawable.ic_save_black);
                postViewHolder.save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG,"postid   "+post.getPOST_ID());
                        showDeleteConfirmationDialog(post.getPOST_ID());
                    }
                });
            }

            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
                return new PostViewHolder(view);
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(postAdapter);
    }
    void showDeleteConfirmationDialog(final String id)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Remove from saved?");
        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteFromSaved(id);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(dialogInterface!=null)
                    dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }
   public boolean deleteFromSaved(String id){
       // final boolean deleted=false;
         deleted=false;
        final DocumentReference doc_REf=stu_Ref.collection("saved").document(id);
        doc_REf.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task){
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        if(document.getString(Constants.SAVED_IMAGE)!=null)
                        {
                            StorageReference storageReference= FirebaseStorage.getInstance()
                                    .getReference(uid).child("saved_images").child(document.getString(Constants.SAVED_IMAGE));
                            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(SavedActivity.this,"Image deleted successfully",Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(),"Image deleted successfully",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        doc_REf.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                deleted=true;
                                Toast.makeText(getApplicationContext(), "Successfully deleted", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "DELETION FAILED", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else
                        Log.d(TAG,"No such document");
                }
                else
                    Log.d(TAG,"Task failed");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
return deleted;
    }
    void setUIViews(){
        firestore=FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerview);
        userSessionManager=new UserSessionManager(this);
    }
    class PostViewHolder extends RecyclerView.ViewHolder {
        TextView tag, desc, created,link, author;
        View view;
        ImageView img_doc;
        ImageButton save, share;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tag = itemView.findViewById(R.id.txt_tag);
            desc = itemView.findViewById(R.id.txt_desc);
            created = itemView.findViewById(R.id.txt_date);
            author = itemView.findViewById(R.id.txt_author);
            view = itemView;
            link = itemView.findViewById(R.id.web_url);
            img_doc = itemView.findViewById(R.id.imageview);
            save=itemView.findViewById(R.id.btn_save);
           // share = itemView.findViewById(R.id.btn_share);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        postAdapter.startListening();
    }
}