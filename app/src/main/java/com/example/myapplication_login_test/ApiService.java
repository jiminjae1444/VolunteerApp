package com.example.myapplication_login_test;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    @POST("login") // Spring Boot 애플리케이션의 로그인 엔드포인트 URL로 변경해야 합니다.
    Call<LoginResponse> login(@Body LoginRequest loginResponse);

    @Headers("Content-Type: application/json")
    @POST("signup")
    Call<Void> signup(@Body SignupRequest signupRequest);

    @Headers("Content-Type: application/json")
    @GET("/api/user/by-username/{username}")
    Call<User> getUserByUsername(@Path("username") String username);

    @Headers("Content-Type: application/json")
    @PUT("/api/user/{username}")
    Call<User> updateUser(@Path("username") String username, @Body User updateuser);

    @Headers("Content-Type: application/json")
    @GET("/api/user-info/{username}") // 다른 엔드포인트 예시
    Call<UserInfo> getUserInfoByUsername(@Path("username") String username);

    @POST("/api/volunteer/create")
    Call<Void> createVolunteer(@Body VolunteerForminfo volunteerForminfo);

    @GET("/api/volunteer/volunteerForms")
    Call<List<VolunteerForm>> getVolunteerForm();

    @GET("/api/volunteer/list/getVolunteerList") // 실제 API 엔드포인트와 메서드를 맞게 수정해야 함
    Call<List<VolunteerList>> getVolunteerList();


}
