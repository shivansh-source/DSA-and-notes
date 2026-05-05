# SQL Joins

> Joins let you combine rows from two or more tables based on a related column. Think of it like a lookup: "for each row in table A, find the matching row(s) in table B."

---

## Sample Tables

```sql
-- users
┌────┬─────────┬─────────────┐
│ id │ name    │ country     │
├────┼─────────┼─────────────┤
│  1 │ Alice   │ US          │
│  2 │ Bob     │ UK          │
│  3 │ Charlie │ IN          │
│  4 │ Diana   │ US          │
└────┴─────────┴─────────────┘

-- orders
┌────┬─────────┬────────┐
│ id │ user_id │ amount │
├────┼─────────┼────────┤
│  1 │       1 │  50.00 │
│  2 │       1 │  75.00 │
│  3 │       2 │  30.00 │
│  4 │       5 │  20.00 │  ← user_id 5 doesn't exist in users
└────┴─────────┴────────┘
```

---

## INNER JOIN

Returns only rows where there is a match in **both** tables.

```sql
SELECT users.name, orders.amount
FROM users
INNER JOIN orders ON users.id = orders.user_id;
```

Result:
```
name    | amount
--------+--------
Alice   |  50.00
Alice   |  75.00
Bob     |  30.00
```

Charlie and Diana (no orders) are excluded.
Order with user_id=5 (no matching user) is excluded.

---

## LEFT JOIN (LEFT OUTER JOIN)

Returns **all rows from the left table**, and matched rows from the right. Unmatched right rows get NULL.

```sql
SELECT users.name, orders.amount
FROM users
LEFT JOIN orders ON users.id = orders.user_id;
```

Result:
```
name    | amount
--------+--------
Alice   |  50.00
Alice   |  75.00
Bob     |  30.00
Charlie |   NULL   ← Charlie has no orders
Diana   |   NULL   ← Diana has no orders
```

**Use when:** You want all users, even if they have no orders.

---

## RIGHT JOIN (RIGHT OUTER JOIN)

Returns **all rows from the right table**, and matched rows from the left. Unmatched left rows get NULL.

```sql
SELECT users.name, orders.amount
FROM users
RIGHT JOIN orders ON users.id = orders.user_id;
```

Result:
```
name  | amount
------+--------
Alice |  50.00
Alice |  75.00
Bob   |  30.00
NULL  |  20.00  ← order with user_id=5 has no matching user
```

**Note:** RIGHT JOIN is rarely used. You can usually rewrite it as a LEFT JOIN by swapping the tables.

---

## FULL OUTER JOIN

Returns **all rows from both tables**. Unmatched rows get NULL on the side that has no match.

```sql
SELECT users.name, orders.amount
FROM users
FULL OUTER JOIN orders ON users.id = orders.user_id;
```

Result:
```
name    | amount
--------+--------
Alice   |  50.00
Alice   |  75.00
Bob     |  30.00
Charlie |   NULL
Diana   |   NULL
NULL    |  20.00
```

**Note:** Not supported in MySQL — use `UNION` of LEFT JOIN and RIGHT JOIN instead.

---

## CROSS JOIN

Returns the Cartesian product — every row from A combined with every row from B.

```sql
SELECT colors.name, sizes.name
FROM colors
CROSS JOIN sizes;
```

If `colors` has 3 rows and `sizes` has 4 rows, the result has 3 × 4 = 12 rows.

**Use when:** Generating combinations (e.g., all product variants).

---

## Self Join

A table joined with itself. Useful for hierarchical data.

```sql
-- employees table: each row has a manager_id that points to another employee
SELECT e.name AS employee, m.name AS manager
FROM employees e
LEFT JOIN employees m ON e.manager_id = m.id;
```

---

## Diagram: Visual Join Summary

```
         LEFT          INNER         RIGHT
        ┌─────┐       ┌─────┐       ┌─────┐
       ╔╪═════╪╗     ╔╪═════╪╗     ╔╪═════╪╗
       ║│  A  │║     ║│  A  │║     ║│  A  │║
       ║└─────┘║     ║└─────┘║     ║└─────┘║
       ╚═══════╝     ╚═══════╝     ╚═══════╝
       All of A     Intersection   All of B
       + match        only          + match

         FULL OUTER JOIN
        ┌─────┐
       ╔╪═════╪╗
       ║│  A  │║
       ╚╪═════╪╝
        └─────┘
       Everything from both tables
```

---

## Common Join Patterns

```sql
-- Find users who have placed at least one order
SELECT DISTINCT users.name
FROM users
INNER JOIN orders ON users.id = orders.user_id;

-- Find users who have NEVER placed an order
SELECT users.name
FROM users
LEFT JOIN orders ON users.id = orders.user_id
WHERE orders.id IS NULL;

-- Total spent per user, including users with no orders
SELECT users.name, COALESCE(SUM(orders.amount), 0) AS total_spent
FROM users
LEFT JOIN orders ON users.id = orders.user_id
GROUP BY users.id, users.name;
```

---

## Real-World Use Case

Joins are used in nearly every query that touches more than one table:
- **E-commerce:** Join users + orders + products to build order history pages
- **Analytics:** Join events + users + sessions to analyze behavior
- **Permissions:** Join users + roles + permissions to check access
