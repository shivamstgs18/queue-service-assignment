# Message Queue Service

This project is an implementation of a message queue system with multiple backends and core queueing features. It is designed to simulate production-grade queue behavior for local development, testing, and learning purposes.

---

## Enhancements (Added by Shivam)

- Implemented In-Memory Priority Queue  
  - Supports priority-based message delivery  
  - Maintains FIFO ordering within the same priority using timestamps  

- Implemented Redis-based Queue using Upstash  
  - Uses Upstash REST API (no Redis client required)  
  - Demonstrates distributed queue capability  

- Added unit tests for both implementations  
  - Covers ordering, FIFO behavior, and basic operations  

---

## Implementations

The project supports the following queue types:

1. In-Memory Queue  
   - Thread-safe  
   - Suitable for same-JVM producers and consumers  

2. File-Based Queue  
   - Thread-safe and inter-process safe  
   - Uses file system for coordination across JVMs  

3. SQS Queue  
   - Adapter for AWS SQS  

4. In-Memory Priority Queue (Added)  
   - Priority-based delivery  
   - FIFO maintained within same priority  
   - Uses custom comparator over PriorityQueue  

5. Redis Queue (Upstash)  
   - Distributed queue using REST API  
   - No infrastructure setup required  
   - Demonstrates scalable queue design  

---

## Core Features

Multiplicity  
- Supports multiple producers and consumers  

Delivery  
- At-least-once delivery guarantee  
- Messages may be re-delivered in failure scenarios  

Order  
- FIFO ordering (best-effort)  

Reliability  
- Visibility timeout mechanism  
  - Messages are temporarily hidden after being pulled  
  - If not deleted, they become visible again  

---

## Code Structure

All code resides under:

com.example

Key files:

- QueueService.java – Core interface  
- InMemoryQueueService.java – FIFO queue  
- FileQueueService.java – File-based queue  
- SqsQueueService.java – AWS SQS adapter  
- InMemoryPriorityQueueService.java – Priority queue (added)  
- RedisQueueService.java – Upstash Redis queue (added)  
- Message.java – Message model  

---

## Testing

Unit tests are included for:

- In-memory queue  
- File queue  
- Priority queue (ordering and FIFO)  
- Redis queue (push and pull)  

Run tests using:

mvn test

---

## Building

mvn clean install

---

## Design Decisions

- Priority Queue implemented using PriorityQueue with:  
  - Primary key: priority (descending)  
  - Secondary key: timestamp (FIFO)  

- Redis Queue implemented using Upstash HTTP API:  
  - Simplifies integration  
  - Avoids dependency on Redis client libraries  

---

## Future Improvements

- Add priority support in Redis (using sorted sets)  
- Implement visibility timeout in Redis queue  
- Add dead-letter queue (DLQ)  
- Support retry mechanisms  
- Improve exactly-once delivery semantics  

---

## Author

Shivam Sharma
