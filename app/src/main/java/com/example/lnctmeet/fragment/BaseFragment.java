package com.example.lnctmeet.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lnctmeet.R;
import com.example.lnctmeet.model.Post;
import com.example.lnctmeet.utils.Constants;
import com.example.lnctmeet.view.MainActivity;
import com.example.lnctmeet.view.SavedActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import retrofit2.http.POST;

public abstract class BaseFragment extends Fragment {
    Query q1;
    FirestoreRecyclerAdapter<Post, PostViewHolder> postAdapter;
    FirebaseFirestore firestore;
    FirestoreRecyclerOptions<Post> postsList;
    private ProgressDialog progressDialog;
    private static final String TAG_NAME = MainActivity.class.getName();
    private StorageReference mStorage;
    DocumentReference docRef;
    String uid;
public BaseFragment(){

}
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_notice, container, false);
        RecyclerView recyclerView;
        MainActivity activity=(MainActivity) getActivity();
        uid=activity.sendData();
        firestore = FirebaseFirestore.getInstance();
        docRef = firestore.collection("Students").
                document(uid);
        progressDialog=new ProgressDialog(getContext());
        mStorage= FirebaseStorage.getInstance().getReference(uid);
        recyclerView = rootview.findViewById(R.id.recycler_view);
Log.d(getContext().toString(),"inOnCreateview");
      q1=getQuery();
        postsList = new FirestoreRecyclerOptions.Builder<Post>().setQuery(q1, Post.class).build();
        postAdapter = new FirestoreRecyclerAdapter<Post, PostViewHolder>(postsList) {
            @Override
            protected void onBindViewHolder(@NonNull final PostViewHolder postViewHolder, int i, @NonNull final Post post) {
                Log.d(getContext().toString(),"onBindViewHolder");
                if (post.getIMAGE_URI() != null) {
                    Glide.with(getActivity()).load(post.getIMAGE_URI()).into(postViewHolder.img_doc);
                    postViewHolder.img_doc.setVisibility(View.VISIBLE);
                }
                docRef.collection("saved").document(post.getPOST_ID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot=task.getResult();
                        if(documentSnapshot.exists())
                            postViewHolder.save.setImageResource(R.drawable.ic_save_black);
                        else
                            postViewHolder.save.setImageResource(R.drawable.ic_save);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
                postViewHolder.tag.setText(post.getTAG());
                postViewHolder.desc.setText(post.getDESCRIPTION());
                postViewHolder.author.setText(post.getAUTHOR());
                if(post.getWEB_URL()!=null){
                    postViewHolder.link.setText(post.getWEB_URL());
                    postViewHolder.link.setVisibility(View.VISIBLE);
                }
                postViewHolder.created.setText(post.getDATE_CREATION().toString().substring(0, 11));
                final String id = postsList.getSnapshots().getSnapshot(i).getId();
                postViewHolder.save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        docRef.collection("saved").document(post.getPOST_ID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot documentSnapshot=task.getResult();
                                if(documentSnapshot.exists()) {
                                Toast.makeText(getActivity(),"Don't worry its already saved!!",Toast.LENGTH_SHORT).show();
                                  //  if(((SavedActivity)getActivity()).deleteFromSaved(post.getPOST_ID()))
                                   // postViewHolder.save.setImageResource(R.drawable.ic_save);
                                }
                                else {
                                    savePost(post);

                                }
                                }

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                        //if(postViewHolder.save.getDrawable()==getResources().getDrawable(R.drawable.ic_save))*/
                        //savePost(post);

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
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(postAdapter);
        return rootview;
    }
boolean savePost(final Post post){
    final boolean[] saved = {false};
    final Map<String,Object> post_tosave=new HashMap<>();
    post_tosave.put(Constants.TAG,post.getTAG());
    post_tosave.put(Constants.DESCRIPTION,post.getDESCRIPTION());
    post_tosave.put(Constants.DATE_CREATION,post.getDATE_CREATION());
    post_tosave.put(Constants.AUTHOR,post.getAUTHOR());
    post_tosave.put(Constants.VISIBLE_TO,post.getVISIBLE_TO());
    post_tosave.put(Constants.POST_ID, post.getPOST_ID());
    post_tosave.put(Constants.WEB_URL,post.getWEB_URL());
    final DocumentReference reference=firestore.collection("posts").document();
    if(post.getIMAGE_URI()!=null){
        progressDialog.setMessage("Uploading Image....");
        progressDialog.show();
        //remember that this image might got deleted from tracher side
        //handle that case ,either dont delete the original image when teacher deleted the post
         final StorageReference filepath=mStorage.child("saved_images").child(post.getSAVED_IMAGE());
        post_tosave.put(Constants.UPLOADING_IMAGEURI,post.getUPLOADING_IMAGEURI());
        post_tosave.put(Constants.IMAGE_URI,post.getIMAGE_URI());
        post_tosave.put(Constants.SAVED_IMAGE,post.getSAVED_IMAGE());
        docRef.collection("saved").document(post.getPOST_ID()).set(post_tosave).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), "Saved successfully", Toast.LENGTH_SHORT).show();
                progressDialog.cancel();
                saved[0] =true;

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error in processing the request", Toast.LENGTH_SHORT).show();
                progressDialog.cancel();
                Log.d(getActivity().toString(), e.getMessage());
            }
        });
        /*UploadTask uploadTask=filepath.putFile(Uri.parse(post.getUPLOADING_IMAGEURI()));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Uri downloaduri=taskSnapshot.getMetadata().getReference().getDownloadUrl();
                Task<Uri> downloadUrl = filepath.getDownloadUrl();
                progressDialog.cancel();
                downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        progressDialog.setMessage("Uploading the data....");
                        progressDialog.show();
                        post_tosave.put(Constants.UPLOADING_IMAGEURI,post.getUPLOADING_IMAGEURI());
                        post_tosave.put(Constants.IMAGE_URI,uri.toString());
                        post_tosave.put(Constants.SAVED_IMAGE,Uri.parse(post.getUPLOADING_IMAGEURI()).getLastPathSegment());
                        docRef.collection("saved").document(post.getPOST_ID()).set(post_tosave).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), "Saved successfully", Toast.LENGTH_SHORT).show();
                                progressDialog.cancel();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Error in processing the request", Toast.LENGTH_SHORT).show();
                                progressDialog.cancel();
                                Log.d(getActivity().toString(), e.getMessage());
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(getActivity().toString(), "Failed to download the image");
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.cancel();
                Log.d(getActivity().toString(), "Failed to upload the image,Please try later");
            }
        });*/
    }
    else {
        progressDialog.setMessage("Uploading the data....");
        progressDialog.show();
        post_tosave.put(Constants.IMAGE_URI, null);
        post_tosave.put(Constants.SAVED_IMAGE,null);
        post_tosave.put(Constants.UPLOADING_IMAGEURI,null);
       docRef.collection("saved").document(post.getPOST_ID()).set(post_tosave).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), "Posted successfully", Toast.LENGTH_SHORT).show();
                saved[0]=true;
                progressDialog.cancel();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error in processing the request", Toast.LENGTH_SHORT).show();
                progressDialog.cancel();
                Log.d(getTag(), e.getMessage());
            }
        });
    }
return saved[0];
}
    class PostViewHolder extends RecyclerView.ViewHolder {
        TextView tag, desc, link,created, author;
        View view;
        ImageView img_doc;
        ImageButton save, share;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tag = itemView.findViewById(R.id.txt_tag);
            desc = itemView.findViewById(R.id.txt_desc);
            created = itemView.findViewById(R.id.txt_date);
            author = itemView.findViewById(R.id.txt_author);
            link = itemView.findViewById(R.id.web_url);
            view = itemView;
            img_doc = itemView.findViewById(R.id.imageview);
            save=itemView.findViewById(R.id.btn_save);
            //share = itemView.findViewById(R.id.btn_share);
        }
    }
abstract public Query getQuery();
    @Override
    public void onStart() {
        super.onStart();
        Log.d(getContext().toString(),"startlisteninf");
        postAdapter.startListening();
    }
}
