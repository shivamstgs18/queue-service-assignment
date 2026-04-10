package com.example;

import org.junit.Test;
import static org.junit.Assert.*;

public class InMemoryPriorityQueueTest {

  @Test
    public void testEmptyQueue() {
    InMemoryPriorityQueueService q = new InMemoryPriorityQueueService();

    assertNull(q.pull("q1"));
  }

  @Test
  public void testPriorityOrdering() {
    InMemoryPriorityQueueService q = new InMemoryPriorityQueueService();

    q.push("q1", "low", 1);
    q.push("q1", "high", 5);
    q.push("q1", "medium", 3);

    assertEquals("high", q.pull("q1").getBody());
    assertEquals("medium", q.pull("q1").getBody());
    assertEquals("low", q.pull("q1").getBody());
  }

  @Test
  public void testFIFOWithinSamePriority() {
    InMemoryPriorityQueueService q = new InMemoryPriorityQueueService();

    q.push("q1", "first", 2);
    q.push("q1", "second", 2);

    assertEquals("first", q.pull("q1").getBody());
    assertEquals("second", q.pull("q1").getBody());
  }
}