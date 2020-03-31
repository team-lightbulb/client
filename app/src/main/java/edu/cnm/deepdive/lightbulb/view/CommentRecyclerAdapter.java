package edu.cnm.deepdive.lightbulb.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.cnm.deepdive.lightbulb.R;
import edu.cnm.deepdive.lightbulb.model.Comment;
import edu.cnm.deepdive.lightbulb.model.Keyword;
import edu.cnm.deepdive.lightbulb.view.CommentRecyclerAdapter.Holder;
import java.text.DateFormat;
import java.util.LinkedList;
import java.util.List;

public class CommentRecyclerAdapter extends RecyclerView.Adapter<Holder> {

  private final Context context;
  private final List<Comment> comments;
  private final OnQuoteClickListener listener;
  private final DateFormat dateFormat;
  private final DateFormat timeFormat;
  private final String dateTimeFormat;

  public CommentRecyclerAdapter(Context context,
      List<Comment> comments,
      OnQuoteClickListener listener) {
    this.context = context;
    this.comments = comments;
    this.listener = listener;
    dateFormat = android.text.format.DateFormat.getMediumDateFormat(context);
    timeFormat = android.text.format.DateFormat.getTimeFormat(context);
    dateTimeFormat = context.getString(R.string.date_time_format);
  }


  @NonNull
  @Override
  public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View root = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
    return new Holder(root);
  }

  @Override
  public void onBindViewHolder(@NonNull Holder holder, int position) {
    holder.bind(position, comments.get(position));
  }

  @Override
  public int getItemCount() {
    return comments.size();
  }

  @FunctionalInterface
  public interface OnQuoteClickListener {

    void onCommentClick(int position, Comment comment);

  }

  class Holder extends RecyclerView.ViewHolder {

    private static final String KEYWORD_DELIMITER = ", ";

    private final View clickView;
    private final TextView date;
    private final TextView name;
    private final TextView author;
    private final TextView keywords;

    private Holder(View root) {
      super(root);
      clickView = root.findViewById(R.id.click_view);
      date = root.findViewById(R.id.date);
      name = root.findViewById(R.id.name);
      author = root.findViewById(R.id.author);
      keywords = root.findViewById(R.id.keywords);
    }

    private void bind(int position, Comment comment) {
      date.setText(String.format(dateTimeFormat,
          dateFormat.format(comment.getCreated()), timeFormat.format(comment.getCreated())));
      name.setText(comment.getName());
      author.setText(comment.getUser().getName());
      StringBuilder builder = new StringBuilder();
      for (Keyword keyword : comment.getKeywords()) {
        builder.append(keyword.getName()).append(KEYWORD_DELIMITER);
      }
      keywords.setText(builder.substring(0, builder.length() - KEYWORD_DELIMITER.length()));
      clickView.setOnClickListener((v) -> listener.onCommentClick(getAdapterPosition(), comment));
      itemView.setTag(comment);
    }

  }

}
