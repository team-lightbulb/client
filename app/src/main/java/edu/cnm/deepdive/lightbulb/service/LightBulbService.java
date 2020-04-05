package edu.cnm.deepdive.lightbulb.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.cnm.deepdive.lightbulb.BuildConfig;
import edu.cnm.deepdive.lightbulb.model.Comment;
import edu.cnm.deepdive.lightbulb.model.Keyword;
import io.reactivex.Completable;
import io.reactivex.Single;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface LightBulbService {


  @GET("comments")
  Single<List<Comment>> getAllComments(@Header("Authorization") String oauthHeader);

  @GET("comments/{id}")
  Single<Comment> getComment(@Header("Authorization") String oauthHeader, @Path("id") UUID id);

  @GET("keywords")
  Single<List<Keyword>> getAllKeywords(
      @Header("Authorization") String oauthHeader);

  @GET("comments/search")
  Single<List<Comment>> getCommentsFiltered(
      @Header("Authorization") String oauthHeader,
      @Query("q") String filter);

  @POST("comments")
  Single<Comment> postComment(@Header("Authorization") String oauthHeader, @Body Comment comment);

  @PUT("comments/{id}")
  Single<Comment> putComment(
      @Header("Authorization") String oauthHeader, @Body Comment comment, @Path("id") UUID id);

  @DELETE("comments/{id}")
  Completable deleteComment(
      @Header("Authorization") String oauthHeader, @Path("id") UUID id);

  static LightBulbService getInstance() {
    return InstanceHolder.INSTANCE;
  }

  class InstanceHolder {

    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private static final LightBulbService INSTANCE;

    static {
      Gson gson  = new GsonBuilder()
          .setDateFormat(TIMESTAMP_FORMAT)
          .excludeFieldsWithoutExposeAnnotation()
          .create();
      HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
      interceptor.setLevel(Level.BODY);
      OkHttpClient client = new OkHttpClient.Builder()
          .readTimeout(60, TimeUnit.SECONDS)
          .addInterceptor(interceptor)
          .build();
      Retrofit retrofit = new Retrofit.Builder()
          .addConverterFactory(GsonConverterFactory.create(gson))
          .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
          .client(client)
          .baseUrl(BuildConfig.BASE_URL)
          .build();
      INSTANCE = retrofit.create(LightBulbService.class);
    }

  }

}
