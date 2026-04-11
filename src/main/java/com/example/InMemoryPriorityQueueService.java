package com.example;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class InMemoryPriorityQueueService implements QueueService {

  private final Map<String, PriorityQueue<Message>> visibleQueues;
  private final Map<String, Map<String, Message>> inFlightMessages;

  private final long visibilityTimeout = 30; // seconds

  InMemoryPriorityQueueService() {
    this.visibleQueues = new ConcurrentHashMap<>();
    this.inFlightMessages = new ConcurrentHashMap<>();
  }

  // Comparator: Priority DESC, FIFO ASC
  private PriorityQueue<Message> createQueue() {
    return new PriorityQueue<>((a, b) -> {
      if (b.getPriority() != a.getPriority()) {
        return b.getPriority() - a.getPriority();
      }
      return Long.compare(a.getCreatedAt(), b.getCreatedAt());
    });
  }

  @Override
  public void push(String queueUrl, String msgBody) {
    push(queueUrl, msgBody, 0);
  }

  public void push(String queueUrl, String msgBody, int priority) {
    visibleQueues.putIfAbsent(queueUrl, createQueue());

    PriorityQueue<Message> queue = visibleQueues.get(queueUrl);
    queue.add(new Message(msgBody, priority));
  }

  @Override
  public Message pull(String queueUrl) {
    PriorityQueue<Message> queue = visibleQueues.get(queueUrl);
    if (queue == null) return null;

    long now = System.currentTimeMillis();

    // Step 1: Reinsert expired messages from in-flight
    inFlightMessages.putIfAbsent(queueUrl, new ConcurrentHashMap<>());
    Map<String, Message> inFlight = inFlightMessages.get(queueUrl);

    Iterator<Map.Entry<String, Message>> it = inFlight.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<String, Message> entry = it.next();
      Message msg = entry.getValue();

      if (msg.isVisibleAt(now)) {
        queue.add(msg); // back to visible queue
        it.remove();
      }
    }

    // Step 2: Fetch next visible message
    Message msg = queue.poll();
    if (msg == null) return null;

    // Step 3: Mark as in-flight
    String receiptId = UUID.randomUUID().toString();
    msg.setReceiptId(receiptId);
    msg.incrementAttempts();
    msg.setVisibleFrom(now + TimeUnit.SECONDS.toMillis(visibilityTimeout));

    inFlight.put(receiptId, msg);

    // Step 4: Return safe copy
    return new Message(msg.getBody(), receiptId);
  }

  @Override
  public void delete(String queueUrl, String receiptId) {
    Map<String, Message> inFlight = inFlightMessages.get(queueUrl);
    if (inFlight == null) return;

    inFlight.remove(receiptId);
  }
}