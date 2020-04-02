package edu.cnm.deepdive.lightbulb.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import edu.cnm.deepdive.lightbulb.R;
import edu.cnm.deepdive.lightbulb.model.Comment;
import edu.cnm.deepdive.lightbulb.view.CommentRecyclerAdapter;
import edu.cnm.deepdive.lightbulb.viewmodel.MainViewModel;

public class RecentFragment extends Fragment {


  private MainViewModel viewModel;
  private RecyclerView commentList;

  public View onCreateView(@NonNull LayoutInflater inflater,
      ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_recent, container, false);
    setupUI(root);
    setupViewModel();
    return root;
  }

  private void setupUI(View root) {
    commentList = root.findViewById(R.id.comment_list);
    root.findViewById(R.id.new_conversation).setOnClickListener((v) -> {
      NewCommentFragment fragment = NewCommentFragment.createInstance(null);
      fragment.show(getChildFragmentManager(), fragment.getClass().getName());
    });
  }

  private void setupViewModel() {
    viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
    viewModel.getRecentComments().observe(getViewLifecycleOwner(), (comments) -> {
      CommentRecyclerAdapter adapter = new CommentRecyclerAdapter(getContext(), comments, (pos, comment) -> {

      });
      commentList.setAdapter(adapter);
    });
  }
}