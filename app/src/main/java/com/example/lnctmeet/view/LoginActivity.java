package com.example.lnctmeet.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lnctmeet.R;
import com.example.lnctmeet.api.ApiClient;
import com.example.lnctmeet.api.RetrofitService;
import com.example.lnctmeet.model.LoginUser;
import com.example.lnctmeet.model.Student;
import com.example.lnctmeet.preferences.UserSessionManager;
import com.example.lnctmeet.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG =LoginActivity.class.getName() ;
    RetrofitService service;
    TextView  txt_email, txt_pass;
    Button btn_login;
    ProgressDialog progressDialog;
    UserSessionManager userSessionManager;
    FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setUpUIViews();
        service= ApiClient.getClient().create(RetrofitService.class);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(txt_email.getText()) || TextUtils.isEmpty(txt_pass.getText()))
                    Toast.makeText(LoginActivity.this,"Both fields are necessary",Toast.LENGTH_SHORT).show();
                else
                {
                    final String id=txt_email.getText().toString().trim();
                    final String pass=txt_pass.getText().toString().trim();
                    Call<Student> call=service.getUser(id,pass);
                    call.enqueue(new Callback<Student>() {
                        @Override
                        public void onResponse(Call<Student> call, Response<Student> response) {
                            if (response.code() == 200) {
                                final LoginUser loginUser=new LoginUser(id,pass);
                                Student student = response.body();
                                Log.d(TAG, student.getName() + student.getBranch() + student.getSemseter());
                                userSessionManager.createUserLoginSession(loginUser.getUsername(),
                                        student.getName(),
                                        student.getSemseter(),
                                        student.getBranch(),
                                        student.getCollege(),
                                        student.getGender());

                                final DocumentReference docRef = firestore.collection("Students").
                                        document(loginUser.getUsername());


                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot documentSnapshot=task.getResult();
                                            if(!documentSnapshot.exists()){
                                                Map<String,Object> user=new HashMap<>();
                                                user.put(UserSessionManager.KEY_LOGIN,loginUser.getUsername());
                                               docRef.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                   @Override
                                                   public void onComplete(@NonNull Task<Void> task) {
                                                  Toast.makeText(getApplicationContext(),loginUser.getUsername()+" added to firestore",Toast.LENGTH_SHORT).show();
                                                       // Starting MainActivity
                                                       Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                                       i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                       // Add new Flag to start new Activity
                                                       i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                       startActivity(i);
                                                       finish();
                                                   }
                                               }).addOnFailureListener(new OnFailureListener() {
                                                   @Override
                                                   public void onFailure(@NonNull Exception e) {
                                                       Toast.makeText(getApplicationContext(),loginUser.getUsername()+"Failed to add "+loginUser.getUsername() + "to firestore",Toast.LENGTH_SHORT).show();
                                                   }
                                               });
                                            }
                                            else {
                                                Toast.makeText(getApplicationContext(),loginUser.getUsername()+" added to firestore",Toast.LENGTH_SHORT).show();
                                                // Starting MainActivity
                                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                // Add new Flag to start new Activity
                                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(i);
                                                finish();
                                            }
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                            }
                        }
                        @Override
                        public void onFailure(Call<Student> call, Throwable t) {
                        Log.d(TAG,t.getMessage()+" in failure");
                            Toast.makeText(LoginActivity.this,"Incorrect ID/PASSWORD",Toast.LENGTH_SHORT).show();
                        call.cancel();
                        }
                    });
                }
            }
        });
    }
    void setUpUIViews()
    {
        txt_email=findViewById(R.id.edit_id);
        txt_pass=findViewById(R.id.edit_pass);
        btn_login=findViewById(R.id.btn_login);
        progressDialog=new ProgressDialog(this);
        firestore=FirebaseFirestore.getInstance();
        //ClientUsers=firestore.collection("Students");
        userSessionManager=new UserSessionManager(this);
    }
}