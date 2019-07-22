package jiguang.chat.http;


import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AccountHTTP {

    @GET("login")
    String login(String mobike, String vcode);

    @POST("register")
    String register(String mobike, String vcode);
}
