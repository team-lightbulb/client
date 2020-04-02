package edu.cnm.deepdive.lightbulb.service;

import android.annotation.SuppressLint;
import edu.cnm.deepdive.lightbulb.model.Comment;
import edu.cnm.deepdive.lightbulb.model.Keyword;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CommentRepository {

  private static final int NETWORK_POOL_SIZE = 10;
  private static final String OAUTH_HEADER_FORMAT = "Bearer %s";
  private static final String ISO_DATE_FORMAT = "yyyy-MM-dd";
  private static final Comparator<Comment> THREADED_COMPARATOR = (c1, c2) -> {
    int comparison = 0;
    if (c1.isResponseTo(c2)) {
      comparison = 1;
    } else if (c2.isResponseTo(c1)) {
      comparison = -1;
    } else {
      Comment[] ancestors = getAncestorSiblings(c1, c2);
      comparison = -ancestors[0].getCreated().compareTo(ancestors[1].getCreated());
    }
    return comparison;
  };

  private final LightBulbService proxy;
  private final Executor networkPool;
  private final DateFormat formatter;

  @SuppressLint("SimpleDateFormat")
  private CommentRepository() {
    proxy = LightBulbService.getInstance();
    networkPool = Executors.newFixedThreadPool(NETWORK_POOL_SIZE);
    formatter = new SimpleDateFormat(ISO_DATE_FORMAT);
  }

  public static CommentRepository getInstance() {
    return InstanceHolder.INSTANCE;
  }

  public Single<List<Comment>> getAllComments(String token) {
    return proxy.getAllComments(String.format(OAUTH_HEADER_FORMAT, token))
        .subscribeOn(Schedulers.from(networkPool))
        .map(this::linkReferences)
        .map((comments) -> {
          Collections.sort(comments, THREADED_COMPARATOR);
          return comments;
        });
  }

  public Single<List<Comment>> searchComments(String token, String filter) {
    return proxy.getCommentsFiltered(String.format(OAUTH_HEADER_FORMAT, token), filter)
        .subscribeOn(Schedulers.from(networkPool))
        .map(this::linkReferences)
        .map((comments) -> {
          Collections.sort(comments, (c1, c2) -> {
            int result = THREADED_COMPARATOR.compare(c1, c2);
            if (result == 0) {
              result = c2.getCreated().compareTo(c1.getCreated());
            }
            return result;
          });
          return comments;
        });
  }

  public Single<List<Keyword>> getAllKeywords(
      String token, boolean includeNull, boolean includeEmpty) {
    return proxy
        .getAllKeywords(String.format(OAUTH_HEADER_FORMAT, token), includeNull, includeEmpty)
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

  private List<Comment> linkReferences(List<Comment> comments) {
    Map<UUID, Comment> map = new HashMap<>();
    for (Comment comment : comments) {
      map.put(comment.getId(), comment);
    }
    for (Comment comment : comments) {
      if (comment.getReference() != null) {
        Comment reference = map.get(comment.getReference().getId());
        if (reference != null) {
          comment.setReference(reference);
          comment.setDepth(-1);
        } else {
          comment.setDepth(0);
        }
      } else {
        comment.setDepth(0);
      }
    }
    for (Comment comment : comments) {
      if (comment.getDepth() < 0) {
        getDepth(comment);
      }
    }
    return comments;
  }

  private int getDepth(Comment comment) {
    if (comment.getDepth() < 0) {
      comment.setDepth(getDepth(comment.getReference()) + 1);
    }
    return comment.getDepth();
//    return (comment.getDepth() >= 0) ? comment.getDepth() : getDepth(comment.getReference()) + 1;
  }

  private static Comment[] getAncestorSiblings(Comment c1, Comment c2) {
    while (c1.getDepth() > c2.getDepth()) {
      c1 = c1.getReference();
    }
    while (c2.getDepth() > c1.getDepth()) {
      c2 = c2.getReference();
    }
    while (c1.getReference() != c2.getReference()) {
      c1 = c1.getReference();
      c2 = c2.getReference();
    }
    return new Comment[] {c1, c2};
  }

  private static class InstanceHolder {

    private static final CommentRepository INSTANCE = new CommentRepository();

  }

}
