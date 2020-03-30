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
import java.util.List;

public class KeywordRecyclerAdapter extends RecyclerView.Adapter<Holder> {
  private final Context context;
  private final List<Keyword> keywords;
  private final KeywordRecyclerAdapter.OnQuoteClickListener listener;

  public KeywordRecyclerAdapter(Context context,
      List<Keyword> keywords,
      KeywordRecyclerAdapter.OnQuoteClickListener listener) {
    this.context = context;
    this.keywords = keywords;
    this.listener = listener;
  }


  @NonNull
  @Override
  public KeywordRecyclerAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View root = LayoutInflater.from(context).inflate(R.layout.item_keyword, parent, false);
    return new KeywordRecyclerAdapter(root);
  }


  public void onBindViewHolder(@NonNull KeywordRecyclerAdapter.Holder holder, int position) {
    holder.bind(position, keywords.get(position));
  }

  @Override
  public int getItemCount() {
    return keywords.size();
  }

  @FunctionalInterface
  public interface OnQuoteClickListener {

    void onCommentClick(int position, Comment comment);

  }

  class Holder extends RecyclerView.ViewHolder {

    private final TextView commentText;
    private final TextView commentKeyword;

    private Holder(View root) {
      super(root);
      commentText = root.findViewById(R.id.comment_text);
      commentKeyword = root.findViewById(R.id.comment_keyword);
    }

    private void bind(int position, Comment comment) {
      commentText.setText(context.getString(R.string.quote_format, comment.getText()));
      Keyword keyword = comment.getKeyword();
      String name = (keyword != null) ? keyword.getName() : null;
      String attribution = (name != null)
          ? context.getString(R.string.attribution_format, name)
          : context.getString(R.string.unattributed_source);
      commentKeyword.setText(attribution);
      itemView.setOnClickListener((v) -> listener.onCommentClick(getAdapterPosition(), comment));
      itemView.setTag(comment);
    }

  }

}
