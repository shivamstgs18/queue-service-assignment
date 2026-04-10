package com.example;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

public class InMemoryPriorityQueueService implements QueueService {

  private final Map<String, PriorityQueue<Message>> queues;

  InMemoryPriorityQueueService() {
    this.queues = new ConcurrentHashMap<>();
  }

  // Comparator: Priority DESC, FIFO ASC
  private PriorityQueue<Message> createQueue() {
    return new PriorityQueue<>((a, b) -> {
      if (b.getPriority() != a.getPriority()) {
        return b.getPriority() - a.getPriority(); // Higher priority first
      }
      return Long.compare(a.getCreatedAt(), b.getCreatedAt()); // FIFO
    });
  }

  // Default push (priority = 0)
  @Override
  public void push(String queueUrl, String msgBody) {
    push(queueUrl, msgBody, 0);
  }

  // Overloaded push with priority
  public void push(String queueUrl, String msgBody, int priority) {
    PriorityQueue<Message> queue = queues.get(queueUrl);

    if (queue == null) {
      queue = createQueue();
      queues.put(queueUrl, queue);
    }

    queue.add(new Message(msgBody, priority));
  }

  @Override
  public Message pull(String queueUrl) {
    PriorityQueue<Message> queue = queues.get(queueUrl);

    if (queue == null || queue.isEmpty()) {
      return null;
    }

    Message msg = queue.poll();

    // Return new message with receiptId (like existing implementation)
    return new Message(msg.getBody(), UUID.randomUUID().toString());
  }

  @Override
  public void delete(String queueUrl, String receiptId) {
    // Not required for priority queue basic implementation
  }
}