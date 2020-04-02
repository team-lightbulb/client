package edu.cnm.deepdive.lightbulb.controller;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import edu.cnm.deepdive.lightbulb.R;
import edu.cnm.deepdive.lightbulb.model.Comment;
import edu.cnm.deepdive.lightbulb.viewmodel.MainViewModel;
import java.util.UUID;

public class NewCommentFragment extends DialogFragment {

  private static final String REFERENCE_ID_KEY = "reference_id";

  private UUID referenceId;
  private View root;
  private MainViewModel viewModel;
  private TextView subject;
  private TextView text;

  public static NewCommentFragment createInstance(UUID referenceId) {
    NewCommentFragment fragment = new NewCommentFragment();
    Bundle args = new Bundle();
    args.putSerializable(REFERENCE_ID_KEY, referenceId);
    fragment.setArguments(args);
    return fragment;
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    referenceId = (UUID) getArguments().getSerializable(REFERENCE_ID_KEY);
    root = LayoutInflater.from(getContext()).inflate(R.layout.fragment_new_comment, null);
    subject = root.findViewById(R.id.subject);
    text = root.findViewById(R.id.text);
    return new AlertDialog.Builder(getContext())
        .setTitle("New Conversation")
        .setIcon(android.R.drawable.ic_dialog_email)
        .setView(root)
        .setNegativeButton(android.R.string.cancel, (dlg, which) -> {})
        .setPositiveButton(android.R.string.ok, (dlg, which) -> {
          Comment comment = new Comment();
          comment.setName(subject.getText().toString().trim());
          comment.setText(text.getText().toString().trim());
          // TODO getKeyWord and other info.
          viewModel.save(comment);
        })
        .create();
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return root;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
  }
}
