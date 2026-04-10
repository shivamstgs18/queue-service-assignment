package com.example;

import java.io.*;
import java.net.*;
import java.util.UUID;

public class RedisQueueService implements QueueService {

  private static final String BASE_URL = "https://dominant-mudfish-77131.upstash.io";
  private static final String TOKEN = "gQAAAAAAAS1LAAIncDI5Y2Q0NDQzOWQxZGY0MjFmOTA1YThkZmFjN2I1YjgxOHAyNzcxMzE";

  private String sendRequest(String endpoint) {
    try {
      URL url = new URL(BASE_URL + endpoint);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();

      conn.setRequestMethod("POST");
      conn.setRequestProperty("Authorization", "Bearer " + TOKEN);
      conn.setDoOutput(true);

      BufferedReader in = new BufferedReader(
          new InputStreamReader(conn.getInputStream())
      );

      String response = in.readLine();
      in.close();

      return response;

    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  // Extract value from {"result":"hello"}
  private String extractResult(String json) {
    if (json == null) return null;
    return json.replaceAll(".*\"result\":\"?(.*?)\"?}.*", "$1");
  }

  @Override
  public void push(String queueUrl, String msgBody) {
    sendRequest("/lpush/" + queueUrl + "/" + msgBody);
  }

  @Override
  public Message pull(String queueUrl) {
    String response = sendRequest("/rpop/" + queueUrl);

    String value = extractResult(response);

    if (value == null || value.equals("null")) return null;

    return new Message(value, UUID.randomUUID().toString());
  }

  @Override
  public void delete(String queueUrl, String receiptId) {
    // Not needed for this assignment
  }
}