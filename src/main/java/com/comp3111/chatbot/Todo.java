package com.comp3111.chatbot;

import java.sql.Timestamp;

class Todo {
  private Integer key;
  private Timestamp deadline;
  private String content;
  private String userId;

  /**
   * Contstructor with full properties
   * @param Timestamp deadline
   * @param String content
   * @param String userId
   * @param Integer key
   */
  public Todo (Timestamp deadline, String content, String userId, Integer key) {
    this.deadline = deadline;
    this.content = content;
    this.userId = userId;
    this.key = key;
  }

  /**
   * Set deadline
   * @param Timestamp deadline
   */
  public void setDeadline (Timestamp deadline) {
    this.deadline = deadline;
  }

  /**
   * Set content
   * @param String content
   */
  public void setContent (String content) {
    this.content = content;
  }

  /**
   * Get deadline
   * @return {Timestamp} deadline
   */
  public Timestamp getDeadline () {
    return this.deadline;
  }

  /**
   * Get content
   * @return {String} content
   */
  public String getContent () {
    return this.content;
  }

  /**
   * Get key
   * @return {Integer} key
   */
  public Integer getKey () {
    return this.key;
  }

  /**
   * Get userid
   * @return {String} userId
   */
  public String getUserId () {
    return this.userId;
  }
}
