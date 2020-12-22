package com.example.lnctmeet.api;

import com.example.lnctmeet.model.Student;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitService {
    @GET("/login?token")
    Call<Student> getUser(@Query("username") String username, @Query("password") String password);
}
