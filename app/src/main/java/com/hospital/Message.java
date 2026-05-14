package com.hospital;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {
  private static final long serialVersionUID = 1L;
  private LocalDateTime timestamp;
  private String patientName;
  private String content;

  public Message(String patientName, String content) {
    this.timestamp = LocalDateTime.now();
    this.patientName = patientName;
    this.content = content;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public String getPatientName() {
    return patientName;
  }

  public String getContent() {
    return content;
  }
}
