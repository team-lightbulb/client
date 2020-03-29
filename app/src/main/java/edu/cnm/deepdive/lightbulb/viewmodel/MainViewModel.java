package edu.cnm.deepdive.lightbulb.viewmodel;

import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;
import edu.cnm.deepdive.lightbulb.model.Comment;
import edu.cnm.deepdive.lightbulb.model.Content;
import edu.cnm.deepdive.lightbulb.model.Keyword;
import edu.cnm.deepdive.lightbulb.service.CommentRepository;
import edu.cnm.deepdive.lightbulb.service.GoogleSignInService;
import io.reactivex.disposables.CompositeDisposable;
import java.util.List;
import java.util.UUID;

public class MainViewModel {

  private MutableLiveData<Comment> random;
  private MutableLiveData<Comment> daily;
  private MutableLiveData<List<Comment>> comments;
  private MutableLiveData<List<Keyword>> keywords;
  private MutableLiveData<List<Content>> contents;
  private MutableLiveData<Comment> comment;
  private final MutableLiveData<Throwable> throwable;
  private final CommentRepository repository;
  private CompositeDisposable pending;

  public MainViewModel() {
    repository = CommentRepository.getInstance();
    pending = new CompositeDisposable();
    random = new MutableLiveData<>();
    daily = new MutableLiveData<>();
    comments = new MutableLiveData<>();
    keywords = new MutableLiveData<>();
    comment = new MutableLiveData<>();
    contents = new MutableLiveData<>();
    throwable = new MutableLiveData<>();
    refreshDaily();
    refreshQuotes();
    refreshSources();
    refreshContents();
  }

  public LiveData<Comment> getRandom() {
    return random;
  }

  public LiveData<Comment> getDaily() {
    return daily;
  }

  public LiveData<List<Comment>> getComments() {
    return comments;
  }

  public LiveData<List<Keyword>> getKeywords() {
    return keywords;
  }

  public LiveData<Comment> getComment() {
    return comment;
  }

  public LiveData<List<Content>> getContents() {
    return contents;
  }

  public LiveData<Throwable> getThrowable() {
    return throwable;
  }

  public void refreshRandom() {
    throwable.postValue(null);
    GoogleSignInService.getInstance().refresh()
        .addOnSuccessListener((account) -> {
          pending.add(
              repository.getRandom(account.getIdToken())
                  .subscribe(
                      random::postValue,
                      throwable::postValue
                  )
          );
        })
        .addOnFailureListener(throwable::postValue);
  }

  public void refreshDaily() {
    throwable.postValue(null);
    GoogleSignInService.getInstance().refresh()
        .addOnSuccessListener((account) -> {
          pending.add(
              repository.getCommentOfDay(account.getIdToken())
                  .subscribe(
                      daily::postValue,
                      throwable::postValue
                  )
          );
        })
        .addOnFailureListener(throwable::postValue);
  }

  public void refreshQuotes() {
    throwable.postValue(null);
    GoogleSignInService.getInstance().refresh()
        .addOnSuccessListener((account) -> {
          pending.add(
              repository.getAllQuotes(account.getIdToken())
                  .subscribe(
                      comments::postValue,
                      throwable::postValue
                  )
          );
        })
        .addOnFailureListener(throwable::postValue);
  }

  public void refreshSources() {
    throwable.postValue(null);
    GoogleSignInService.getInstance().refresh()
        .addOnSuccessListener((account) -> {
          pending.add(
              repository.getAllKeywords(account.getIdToken(), false, true)
                  .subscribe(
                      keywords::postValue,
                      throwable::postValue
                  )
          );
        })
        .addOnFailureListener(throwable::postValue);
  }

  public void refreshContents() {
    throwable.postValue(null);
    GoogleSignInService.getInstance().refresh()
        .addOnSuccessListener((account) -> {
          pending.add(
              repository.getAllContent(account.getIdToken())
                  .subscribe(
                      contents::postValue,
                      throwable::postValue
                  )
          );
        })
        .addOnFailureListener(throwable::postValue);
  }

  public void save(Comment comment) {
    throwable.setValue(null);
    GoogleSignInService.getInstance().refresh()
        .addOnSuccessListener((account) -> {
          pending.add(
              repository.save(account.getIdToken(), comment)
                  .subscribe(
                      () -> {
                        this.comment.postValue(null);
                        refreshDaily();
                        refreshContents();
                        refreshComments();
                        refreshKeywords();
                      },
                      throwable::postValue
                  )
          );
        })
        .addOnFailureListener(throwable::postValue);
  }

  public void remove(Comment comment) {
    throwable.setValue(null);
    GoogleSignInService.getInstance().refresh()
        .addOnSuccessListener((account) -> {
          pending.add(
              repository.remove(account.getIdToken(), comment)
                  .subscribe(
                      () -> {
                        this.comment.postValue(null);
                        refreshDaily();
                        refreshContents();
                        refreshComments();
                      },
                      throwable::postValue
                  )
          );
        })
        .addOnFailureListener(throwable::postValue);
  }

  public void setCommentId(UUID id) {
    throwable.setValue(null);
    GoogleSignInService.getInstance().refresh()
        .addOnSuccessListener(
            (account) -> pending.add(
                repository.get(account.getIdToken(), id)
                    .subscribe(
                        comment::postValue,
                        throwable::postValue
                    )
            )
        )
        .addOnFailureListener(throwable::postValue);
  }

  @OnLifecycleEvent(Event.ON_STOP)
  private void clearPending() {
    pending.clear();
  }

}
