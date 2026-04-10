
package com.example;

import org.junit.Test;
import static org.junit.Assert.*;

public class RedisQueueTest {

  @Test
  public void testRedisQueue() {
    RedisQueueService q = new RedisQueueService();

    q.push("q1", "hello");

    Message msg = q.pull("q1");

    assertNotNull(msg);
    assertEquals("hello", msg.getBody());
  }

  @Test
  public void testMultipleRedisOperations() {
    RedisQueueService q = new RedisQueueService();

    q.push("q2", "a");
    q.push("q2", "b");

    assertEquals("a", q.pull("q2").getBody());
    assertEquals("b", q.pull("q2").getBody());
  }
  
}