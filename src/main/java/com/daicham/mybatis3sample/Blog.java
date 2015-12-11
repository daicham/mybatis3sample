package com.daicham.mybatis3sample;

import java.util.*;

public class Blog {
    private Long id;
    private String title;
    private String content;
    private Date date;
    
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
      return this.title;
    }
    
    public void setTitle(String title) {
      this.title = title;
    }
    
    public String getContent() {
      return this.content;
    }
    
    public void setContent(String content) {
      this.content = content;
    }
    
    public Date getDate() {
      return this.date;
    }
    
    public void setDate(Date date) {
      this.date = date;
    }

    @Override
    public String toString() {
        return String.format("[id]:%s, [date]:%s, [title]:%s, [conetnt]:%s", this.id, this.date, this.title, this.content);
    }
}