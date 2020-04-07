package edu.cnm.deepdive.lightbulb.controller;

import android.app.Dialog;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import edu.cnm.deepdive.lightbulb.R;
import edu.cnm.deepdive.lightbulb.model.Comment;
import edu.cnm.deepdive.lightbulb.model.Keyword;
import edu.cnm.deepdive.lightbulb.viewmodel.MainViewModel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class NewCommentFragment extends DialogFragment {

  private static final String REFERENCE_ID_KEY = "reference_id";

  private UUID referenceId;
  private View root;
  private MainViewModel viewModel;
  private TextView subject;
  private TextView text;
  private ListView keywordList;
  private Comment refComment; //this will be null if we dont have a referenceid.
  private TextView refUser;
  private TextView refName;
  private List<Keyword> keywords;
  private Pattern replyCheck;
  // TODO Declare additional contextual fields as necessary.

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
    keywordList = root.findViewById(R.id.keyword_list);
//    keywordList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
//    keywordList.setItemsCanFocus(false);
    refUser = root.findViewById(R.id.ref_user);
    refName = root.findViewById(R.id.ref_name);
    // TODO Get references to additional contextual fields.
    return new Builder(getContext())
        .setTitle((referenceId != null) ? "New Response" : "New Conversation")
        .setIcon(R.drawable.ic_text_dialog)
        .setView(root)
        .setNegativeButton(android.R.string.cancel, (dlg, which) -> {
        })
        .setPositiveButton(android.R.string.ok, (dlg, which) -> {
          Comment comment = new Comment();
          comment.setName(subject.getText().toString().trim());
          comment.setText(text.getText().toString().trim());
          comment.setReference(refComment);
          List<Keyword> keywords = new ArrayList<>();
          SparseBooleanArray checks = keywordList.getCheckedItemPositions();
          for (int i = 0; i < this.keywords.size(); ++i) {
            if (checks.get(i)) {
              keywords.add(this.keywords.get(i));
            }
          }
          comment.setKeywords(keywords.toArray(new Keyword[0]));
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
    viewModel.getKeywords().observe(getViewLifecycleOwner(), (keywords) -> {
      this.keywords = keywords;
      ArrayAdapter<Keyword> adapter =
          new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_multiple_choice, keywords);
      keywordList.setAdapter(adapter);
    });
    viewModel.refreshKeywords();
    if (referenceId != null) {
      viewModel.setCommentId(referenceId);
      viewModel.getComment().observe(getViewLifecycleOwner(), (ref) -> {
        if (ref != null) {
          replyCheck = Pattern.compile(getString(R.string.reply_check));
          if (replyCheck.matcher(ref.getName()).matches()) {
            subject.setText(ref.getName());
          } else {
            subject.setText(getString(R.string.default_reply_subject, ref.getName()));
          }
          refComment = ref;
          refUser.setText(getString(R.string.ref_user_format, ref.getUser().getName()));
          refName.setText(getString(R.string.ref_name_format, ref.getName()));
          // TODO Set the content of additional contextual view objects, as neccesory.
          refUser.setVisibility(View.VISIBLE);
          refName.setVisibility(View.VISIBLE);
          // TODO Make additional contextual view visible, as neccesory.
        }
      });
    }
  }
}
