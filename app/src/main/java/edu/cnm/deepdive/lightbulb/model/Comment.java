package edu.cnm.deepdive.lightbulb.model;

import com.google.gson.annotations.Expose;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

public class Comment {

  @Expose
  private UUID id;

  @Expose
  private Date created;

  @Expose
  private Date updated;

  @Expose
  private String name;

  @Expose
  private String text;

  @Expose
  private User user;

  @Expose
  private Comment reference;

  @Expose
  private Keyword[] keywords;

  @Expose
  private URL href;

  private int depth;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public Date getUpdated() {
    return updated;
  }

  public void setUpdated(Date updated) {
    this.updated = updated;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Comment getReference() {
    return reference;
  }

  public void setReference(Comment reference) {
    this.reference = reference;
  }

  public Keyword[] getKeywords() {
    return keywords;
  }

  public void setKeywords(Keyword[] keywords) {
    this.keywords = keywords;
  }

  public URL getHref() {
    return href;
  }

  public void setHref(URL href) {
    this.href = href;
  }

  public int getDepth() {
    return depth;
  }

  public void setDepth(int depth) {
    this.depth = depth;
  }

  public boolean isResponseTo(Comment comment) {
    Comment reference = getReference();
    return reference != null && (reference == comment || reference.isResponseTo(comment));
  }
}
