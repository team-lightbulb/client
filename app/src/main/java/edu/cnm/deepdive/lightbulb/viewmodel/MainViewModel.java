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

  private MutableLiveData<List<Comment>> myComments;
  private MutableLiveData<List<Comment>> recentComments;
  private MutableLiveData<List<Comment>> searchComments;
  private MutableLiveData<List<Keyword>> keywords;
  private MutableLiveData<Comment> comment;
  private final MutableLiveData<Throwable> throwable;
  private final CommentRepository repository;
  private CompositeDisposable pending;

  public MainViewModel() {
    repository = CommentRepository.getInstance();
    pending = new CompositeDisposable();
    myComments = new MutableLiveData<>();
    recentComments = new MutableLiveData<>();
    searchComments = new MutableLiveData<>();
    keywords = new MutableLiveData<>();
    comment = new MutableLiveData<>();
    throwable = new MutableLiveData<>();
  }


  public LiveData<List<Comment>> getMyComments() {
    return myComments;
  }

  public LiveData<List<Comment>> getRecentComments() {
    return recentComments;
  }

  public LiveData<List<Comment>> getSearchComments() {
    return searchComments;
  }

  public LiveData<List<Keyword>> getKeywords() {
    return keywords;
  }

  public LiveData<Comment> getComment() {
    return comment;
  }

  public LiveData<Throwable> getThrowable() {
    return throwable;
  }

  public void setSearchFilter(String filter) {
    throwable.setValue(null);
    GoogleSignInService.getInstance().refresh()
        .addOnSuccessListener((account) -> {
          pending.add(
              repository.searchComments(account.getIdToken(), filter)
                  .subscribe(
                      searchComments::postValue,
                      throwable::postValue
                  )
          );
        })
        .addOnFailureListener(
            throwable::postValue);
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
