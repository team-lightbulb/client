package edu.cnm.deepdive.lightbulb.service;

import android.annotation.SuppressLint;
import edu.cnm.deepdive.lightbulb.model.Comment;
import edu.cnm.deepdive.lightbulb.model.Content;
import edu.cnm.deepdive.lightbulb.model.Keyword;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CommentRepository {

  private static final int NETWORK_POOL_SIZE = 10;
  private static final String OAUTH_HEADER_FORMAT = "Bearer %s";
  private static final String ISO_DATE_FORMAT = "yyyy-MM-dd";

  private final LightBulbService proxy;
  private final Executor networkPool;
  private final DateFormat formatter;

  @SuppressLint("SimpleDateFormat")
  private CommentRepository() {
    proxy = LightBulbService.getInstance();
    networkPool = Executors.newFixedThreadPool(NETWORK_POOL_SIZE);
    formatter = new SimpleDateFormat(ISO_DATE_FORMAT);
  }

  public CommentRepository(LightBulbService proxy, Executor networkPool,
      DateFormat formatter) {
    this.proxy = proxy;
    this.networkPool = networkPool;
    this.formatter = formatter;
  }

  public static CommentRepository getInstance() {
    return InstanceHolder.INSTANCE;
  }

  public Single<List<Comment>> getAllComments(String token) {
    return proxy.getAllComments(String.format(OAUTH_HEADER_FORMAT, token))
        .subscribeOn(Schedulers.from(networkPool));
  }

  public Single<List<Comment>> searchComments(String token, String filter) {
    return proxy.getCommentsFiltered(String.format(OAUTH_HEADER_FORMAT, token), filter)
        .subscribeOn(Schedulers.from(networkPool));
  }

  public Single<List<Keyword>> getAllKeywords(
      String token, boolean includeNull, boolean includeEmpty) {
    return proxy.getAllKeywords(String.format(OAUTH_HEADER_FORMAT, token), includeNull, includeEmpty)
        .subscribeOn(Schedulers.from(networkPool));
  }

  public Completable save(String token, Comment comment) {
    if (comment.getId() == null) {
      return Completable.fromSingle(
          proxy.postComment(String.format(OAUTH_HEADER_FORMAT, token), comment)
              .subscribeOn(Schedulers.from(networkPool))
      );
    } else {
      return Completable.fromSingle(
          proxy.putComment(String.format(OAUTH_HEADER_FORMAT, token), comment, comment.getId())
              .subscribeOn(Schedulers.from(networkPool))
      );
    }
  }

  public Completable remove(String token, Comment comment) {
    if (comment.getId() != null) {
      return proxy.deleteComment(String.format(OAUTH_HEADER_FORMAT, token), comment.getId())
          .subscribeOn(Schedulers.from(networkPool));
    } else {
      return Completable.complete();
    }
  }

  public Single<Comment> get(String token, UUID id) {
    return proxy.getComment(String.format(OAUTH_HEADER_FORMAT, token), id)
        .subscribeOn(Schedulers.from(networkPool));
  }

  private static class InstanceHolder {

    private static final CommentRepository INSTANCE = new CommentRepository();

  }

}
