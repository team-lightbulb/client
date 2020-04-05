package edu.cnm.deepdive.lightbulb.view;

import android.content.Context;
import android.media.Image;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.cnm.deepdive.lightbulb.R;
import edu.cnm.deepdive.lightbulb.model.Comment;
import edu.cnm.deepdive.lightbulb.model.Keyword;
import edu.cnm.deepdive.lightbulb.view.CommentRecyclerAdapter.Holder;
import java.text.DateFormat;
import java.util.List;

public class CommentRecyclerAdapter extends RecyclerView.Adapter<Holder> {

  private final Context context;
  private final List<Comment> comments;
  private final OnCommentClickListener clickListener;
  private final OnReplyClickListener replyListener;
  private final DateFormat dateFormat;
  private final DateFormat timeFormat;
  private final String dateTimeFormat;

  public CommentRecyclerAdapter(Context context,
      List<Comment> comments,
      OnCommentClickListener clickListener,
      OnReplyClickListener replyListener) {
    this.context = context;
    this.comments = comments;
    this.replyListener = replyListener;
    this.clickListener = clickListener;
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
  public interface OnCommentClickListener {

    void onCommentClick(int position, Comment comment);

  }

  @FunctionalInterface
  public interface OnReplyClickListener {
    void onReplyClick(int position, Comment comment);


  }

  class Holder extends RecyclerView.ViewHolder {

    private static final String KEYWORD_DELIMITER = ", ";

    private final View clickView;
    private final ImageView hierarchy;
    private final TextView date;
    private final TextView name;
    private final TextView text;
    private final ImageView more;
    private final ImageView less;
    private final TextView author;
    private final TextView keywords;

    private Holder(View root) {
      super(root);
      clickView = root.findViewById(R.id.click_view);
      hierarchy = root.findViewById(R.id.hierarchy);
      date = root.findViewById(R.id.date);
      name = root.findViewById(R.id.name);
      text = root.findViewById(R.id.text);
      more = root.findViewById(R.id.more);
      less = root.findViewById(R.id.less);
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
      keywords.setText(
          builder.substring(0, Math.max(0, builder.length() - KEYWORD_DELIMITER.length())));
      int startPadding = clickView.getPaddingStart();
      int endPadding = clickView.getPaddingEnd();
      int topPadding = clickView.getPaddingTop();
      int bottomPadding = clickView.getPaddingBottom();
      clickView.setPaddingRelative(
          startPadding * (comment.getDepth() + 1), topPadding, endPadding, bottomPadding);
      clickView.setOnClickListener((v) -> clickListener.onCommentClick(getAdapterPosition(), comment));
      itemView.setTag(comment);
    }

  }

}
