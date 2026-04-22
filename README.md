Functional Discount Pipeline (Scala)

A high-performance, rule-driven discount calculation engine built in pure functional Scala, designed to process large-scale retail transaction data and apply complex business rules in a scalable and testable way.


System Overview

The system reads raw CSV transactions, evaluates multiple discount rules, computes the final price, and persists results into a relational database while maintaining full observability through structured logging.

This project implements a functional pipeline architecture where:

Business logic is isolated in a pure functional core.
Batch processing enables large-scale data ingestion
Parallel execution improves throughput on large datasets



 Batch-Oriented Streaming Execution

Instead of loading all data into memory:

CSV is processed in chunks
Each batch is processed independently
Parallel execution is applied per chunk

Project Structure
engine/
│
├── Main.scala            → Pipeline orchestration (I/O layer)
├── Rules.scala           → Pure discount rule engine
├── DB.scala              → Database persistence layer
├── DB_connection.scala   → JDBC connection handler
├── AppLogger.scala       → Logging configuration
├── utils.scala           → Parsing + helper utilities
└── models                → Order and procceed order models

 ### Execution Pipeline High-Level Flow
Load CSV file
Split into batches
Parse raw lines into Orders
Apply rule engine
Compute final price
Persist results to DB
Log execution events

The system evaluates six independent discount rules, each producing a candidate discount.

Final discount logic:

- Collect all applicable discounts
- Remove zeros
- Sort descending
- Take top 2 values
- Return average

 Business Rules
1. Expiry-Based Discount

Applies linear decay based on remaining days until expiration:

1 → 29%
2 → 28%
...
29 → 1%
2. Product Category Discount
Cheese → 10%
Wine → 5%


3. Special Date Rule

Orders placed on:

March 23rd → 50% discount

4. Quantity Discount

6–9	5%
10–14	7%
15+	10%

5. App Channel Discount

Encourages App usage via step-based rewards:

discount = (ceil(quantity / 5)) * 5%
6. Visa Payment Discount
Visa payment → 5%
🔁 Rule Aggregation Strategy

When multiple rules apply:

finalDiscount =
  average(top2(sorted(applicableDiscounts)))
Edge Cases:
No matching rule → 0%
One rule → direct value
Multiple rules → top-2 average
⚡ Performance Strategy

To handle large-scale datasets (millions of records):

🔹 Parallel Processing
Batch-level parallel execution
ForkJoinPool utilization
🔹 Streaming Input
File processed lazily
No full dataset loading
🔹 Batch DB Writes
Prepared statements
Batched insert execution
🧵 Concurrency Model
Each batch processed independently
Parallel map over batch rows
Thread pool-based execution
🗄 Database Layer
Storage Table
processed_orders

Stores:

transaction metadata
computed discount
final price
timestamp
🪵 Logging System

Two-level logging strategy:

File	Purpose
pipeline.log	General execution flow
errors.log	Failures & exceptions

Format:

TIMESTAMP LEVEL MESSAGE
🧩 Utility Functions
Safe Execution Wrapper

Handles exception-free functional flow:

safe[A](block: => A): Option[A]
CSV Parsing

Converts raw CSV lines into structured Order objects.

🔄 Error Handling Strategy

The system avoids runtime crashes using:

Option for safe computation
Defensive parsing
Controlled exception boundaries
Logging-based failure tracking
📈 Scalability Characteristics

The architecture supports:

Horizontal scaling via batch splitting
CPU utilization via parallel collections
Constant memory usage per batch
Stateless rule evaluation
🧪 Testing Strategy
Unit testing focused on rule correctness
Deterministic outputs (pure functions)
Edge-case validation (boundary conditions)
No dependency on I/O for core logic
🧱 Key Architectural Decisions
✔ Pure Functional Core

All business rules are deterministic functions.

✔ Immutable Data Flow

No mutation across pipeline stages.

✔ Separation of Concerns
Rules → logic
Main → orchestration
DB → persistence
Logger → observability
🧠 Key Insight

Instead of treating discounts as procedural logic, the system models them as:

A combinatorial function over independent transformations

This enables:

Easy extensibility
High testability
Predictable composition

🏁 Summary

This project demonstrates a production-grade functional pipeline architecture for retail systems, combining:

Functional programming discipline
High-performance batch processing
Parallel computation
Clean separation of concerns
Scalable database integration