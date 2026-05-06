# SQL Basics

> SQL (Structured Query Language) is how you talk to a relational database. Think of a database as a collection of spreadsheets (tables), and SQL is the language you use to read, write, update, and delete rows in those spreadsheets.

---

## Core Concepts

| Term | Meaning |
|------|---------|
| **Database** | A container for related tables |
| **Table** | A structured set of rows and columns (like a spreadsheet) |
| **Row** | One record in a table |
| **Column** | An attribute/field (has a data type) |
| **Primary Key** | Uniquely identifies each row in a table |
| **Foreign Key** | References a primary key in another table (links tables) |

---

## The Four Core Operations (CRUD)

```sql
-- CREATE (Insert)
INSERT INTO users (name, email, age)
VALUES ('Alice', 'alice@example.com', 30);

-- READ (Select)
SELECT name, email FROM users WHERE age > 25;

-- UPDATE
UPDATE users SET age = 31 WHERE name = 'Alice';

-- DELETE
DELETE FROM users WHERE name = 'Alice';
```

---

## SELECT in Depth

```sql
-- All columns
SELECT * FROM users;

-- Specific columns with an alias
SELECT name AS full_name, email FROM users;

-- Distinct values (no duplicates)
SELECT DISTINCT country FROM users;

-- Limiting results
SELECT * FROM users LIMIT 10;

-- Counting rows
SELECT COUNT(*) FROM users;
SELECT COUNT(DISTINCT country) FROM users;
```

---

## WHERE Clause (Filtering)

```sql
-- Comparison operators
SELECT * FROM users WHERE age > 18;
SELECT * FROM users WHERE age BETWEEN 18 AND 30;
SELECT * FROM users WHERE name = 'Alice';
SELECT * FROM users WHERE name != 'Alice';

-- Pattern matching
SELECT * FROM users WHERE email LIKE '%@gmail.com'; -- ends with
SELECT * FROM users WHERE name LIKE 'A%';            -- starts with A
SELECT * FROM users WHERE name LIKE '_l%';           -- second char is 'l'

-- IN list
SELECT * FROM users WHERE country IN ('US', 'UK', 'IN');

-- NULL checks (use IS, not =)
SELECT * FROM users WHERE phone IS NULL;
SELECT * FROM users WHERE phone IS NOT NULL;

-- Combining conditions
SELECT * FROM users WHERE age > 18 AND country = 'US';
SELECT * FROM users WHERE age < 18 OR country = 'UK';
SELECT * FROM users WHERE NOT country = 'US';
```

---

## ORDER BY and LIMIT

```sql
-- Sort ascending (default)
SELECT * FROM users ORDER BY age;

-- Sort descending
SELECT * FROM users ORDER BY age DESC;

-- Sort by multiple columns
SELECT * FROM users ORDER BY country ASC, age DESC;

-- Get the top 5 oldest users
SELECT * FROM users ORDER BY age DESC LIMIT 5;
```

---

## Aggregate Functions

```sql
SELECT COUNT(*)     FROM orders;         -- number of rows
SELECT SUM(amount)  FROM orders;         -- total
SELECT AVG(amount)  FROM orders;         -- average
SELECT MIN(amount)  FROM orders;         -- smallest value
SELECT MAX(amount)  FROM orders;         -- largest value
```

---

## GROUP BY

Group rows with the same value in a column, then apply aggregate functions.

```sql
-- How many users per country?
SELECT country, COUNT(*) AS total
FROM users
GROUP BY country;

-- Average order amount per customer
SELECT customer_id, AVG(amount) AS avg_order
FROM orders
GROUP BY customer_id;
```

### HAVING (Filter After Grouping)

`WHERE` filters before grouping. `HAVING` filters after.

```sql
-- Countries with more than 100 users
SELECT country, COUNT(*) AS total
FROM users
GROUP BY country
HAVING total > 100;
```

---

## Creating Tables

```sql
CREATE TABLE users (
    id       INT          PRIMARY KEY AUTO_INCREMENT,
    name     VARCHAR(100) NOT NULL,
    email    VARCHAR(150) NOT NULL UNIQUE,
    age      INT,
    country  VARCHAR(50)  DEFAULT 'US',
    created_at TIMESTAMP  DEFAULT CURRENT_TIMESTAMP
);
```

---

## Diagram: Table Relationships

```
users                   orders
┌────────────────┐      ┌──────────────────────┐
│ id  (PK)       │──┐   │ id          (PK)     │
│ name           │  └──▶│ user_id     (FK)     │
│ email          │      │ amount               │
│ country        │      │ created_at           │
└────────────────┘      └──────────────────────┘
```

`orders.user_id` is a foreign key that references `users.id`.

---

## Real-World Use Case

SQL is used everywhere:
- **E-commerce** — Track users, orders, products, inventory
- **Analytics** — Aggregate sales data, user behavior, retention metrics
- **APIs** — Most backend services read/write from a SQL database on every request
- **Reporting** — Business intelligence dashboards run complex SQL queries
