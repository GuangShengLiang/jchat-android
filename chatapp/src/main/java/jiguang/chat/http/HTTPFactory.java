package jiguang.chat.http;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HTTPFactory {

    private static final String base_url="http://localhost:8080/api";
    private static final AccountHTTP ahttp;

    static {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ahttp = retrofit.create(AccountHTTP.class);
    }

    public AccountHTTP getAccountHTTP(){
        return ahttp;
    }
}
