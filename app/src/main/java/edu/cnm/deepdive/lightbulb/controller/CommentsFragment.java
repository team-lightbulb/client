package edu.cnm.deepdive.lightbulb.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputLayout;
import edu.cnm.deepdive.lightbulb.R;
import edu.cnm.deepdive.lightbulb.model.Comment;
import edu.cnm.deepdive.lightbulb.model.Keyword;
import edu.cnm.deepdive.lightbulb.view.CommentRecyclerAdapter;
import edu.cnm.deepdive.lightbulb.viewmodel.MainViewModel;
import java.util.List;
import java.util.UUID;

public class CommentsFragment extends Fragment {

  private static final String VARIANT_KEY = "variant";

  private Variant variant;
  private MainViewModel viewModel;
  private RecyclerView commentList;
  private TextInputLayout filterLayout;
  private AutoCompleteTextView filter;
  private ImageView submitSearch;

  public View onCreateView(@NonNull LayoutInflater inflater,
      ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_comments, container, false);
    setupUI(root);
    setupViewModel();
    return root;
  }

  private void setupUI(View root) {
    commentList = root.findViewById(R.id.comment_list);
    filterLayout = root.findViewById(R.id.filter_layout);
    filter = root.findViewById(R.id.filter);
    root.findViewById(R.id.new_conversation).setOnClickListener((v) -> createComment(null));
    submitSearch = root.findViewById(R.id.submit_search);
    variant = (Variant) getArguments().getSerializable(VARIANT_KEY);
    if (variant == null) {
      variant = Variant.RECENT_COMMENTS;
    }
    if (variant != Variant.SEARCH_COMMENTS) {
      filterLayout.setVisibility(View.GONE);
      submitSearch.setVisibility(View.GONE);
    } else {
      submitSearch.setOnClickListener((v) -> {
        String f = filter.getText().toString().trim();
        if (f.length() >= 3) {
          viewModel.setSearchFilter(filter.getText().toString().trim());
        }
      });
    }
  }

  private void setupViewModel() {
    Observer<List<Comment>> observer = (comments) -> {
      CommentRecyclerAdapter adapter = new CommentRecyclerAdapter(getContext(), comments,
          (pos, comment) -> {}, (pos, comment) -> createComment(comment.getId()));
      commentList.setAdapter(adapter);
    };
    viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
    switch (variant) {
      case RECENT_COMMENTS:
        viewModel.getRecentComments().observe(getViewLifecycleOwner(), observer);
        viewModel.refreshComments(); //FIXME Add query for last n days.(Should come from shared preferences)
        break;
      case MY_COMMENTS:
        viewModel.getMyComments().observe(getViewLifecycleOwner(), observer);
        viewModel.refreshComments(); //FIXME Add query for current users comments
        break;
      case SEARCH_COMMENTS:
        viewModel.getKeywords().observe(getViewLifecycleOwner(), (keywords) -> {
          ArrayAdapter<Keyword> adapter =
              new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, keywords);
          filter.setAdapter(adapter);
        });
        viewModel.getSearchComments().observe(getViewLifecycleOwner(), observer);
        viewModel.refreshKeywords();
        viewModel.setSearchFilter(null);
        break;
    }
  }

  private void createComment(UUID id) {
    NewCommentFragment fragment = NewCommentFragment.createInstance(id);
    fragment.show(getChildFragmentManager(), fragment.getClass().getName());
  }

  public enum Variant {
    RECENT_COMMENTS, MY_COMMENTS, SEARCH_COMMENTS
  }
}