# Database Indexing

> An index is a separate data structure that the database maintains to make lookups faster. Think of it like the index at the back of a textbook — instead of reading every page to find a topic, you jump straight to the right page.

---

## The Problem Without Indexes

Suppose you have a `users` table with 1 million rows:

```sql
SELECT * FROM users WHERE email = 'alice@example.com';
```

Without an index, the database performs a **full table scan** — it reads every single row to find matches. That's O(n) for 1 million rows.

With an index on `email`, the database uses a **B-tree** lookup — O(log n). For 1 million rows, that's about 20 comparisons instead of 1,000,000.

---

## Creating Indexes

```sql
-- Single column index
CREATE INDEX idx_users_email ON users(email);

-- Unique index (also enforces uniqueness constraint)
CREATE UNIQUE INDEX idx_users_email ON users(email);

-- Composite index (multiple columns)
CREATE INDEX idx_orders_user_status ON orders(user_id, status);

-- Index on a specific column automatically created for PRIMARY KEY and UNIQUE
CREATE TABLE users (
    id    INT PRIMARY KEY,  -- index created automatically
    email VARCHAR(150) UNIQUE  -- index created automatically
);

-- Drop an index
DROP INDEX idx_users_email ON users;
```

---

## How B-Tree Indexes Work

Most database indexes use a **B-tree** (balanced tree) structure.

```
                    [50]
                   /    \
              [25]        [75]
             /    \       /   \
          [10]  [30]  [60]   [90]
```

- Each node contains sorted keys.
- The tree stays balanced — all leaf nodes are at the same depth.
- Finding a value takes O(log n) steps.
- Works great for: equality (`=`), range (`>`, `<`, `BETWEEN`), sorting (`ORDER BY`).

---

## When Indexes Help

```sql
-- These queries BENEFIT from an index on (email):
SELECT * FROM users WHERE email = 'alice@example.com';
SELECT * FROM users WHERE email > 'n';         -- range
SELECT * FROM users ORDER BY email;            -- sorting

-- These queries BENEFIT from a composite index on (user_id, status):
SELECT * FROM orders WHERE user_id = 1 AND status = 'shipped';
SELECT * FROM orders WHERE user_id = 1;  -- leftmost prefix rule
```

---

## When Indexes DON'T Help (or Hurt)

```sql
-- Function on the indexed column — can't use index
SELECT * FROM users WHERE LOWER(email) = 'alice@example.com';
-- Fix: create a functional index or store email lowercased

-- Leading wildcard — can't use B-tree index
SELECT * FROM users WHERE email LIKE '%@gmail.com';
-- Fix: use full-text search or reverse the string

-- Very low cardinality column (e.g., boolean) — often not worth indexing
CREATE INDEX idx_users_active ON users(is_active);
-- If 90% of rows have is_active=1, the index barely helps
```

---

## Index Overhead

Indexes are not free:
- **Storage:** Each index takes disk space (can be as large as the table itself).
- **Write speed:** Every INSERT, UPDATE, DELETE must also update all relevant indexes.
- **Maintenance:** Fragmented indexes need periodic rebuilding.

**Rule of thumb:** Index columns you frequently filter, join, or sort on. Don't index everything.

---

## EXPLAIN — Check If Your Index Is Used

Most databases support `EXPLAIN` to show the query execution plan.

```sql
EXPLAIN SELECT * FROM users WHERE email = 'alice@example.com';
```

Look for:
- `type: ref` or `type: eq_ref` → index being used ✅
- `type: ALL` → full table scan (no index) ⚠️
- `key: idx_users_email` → shows which index was chosen

---

## Composite Index Tips

```sql
-- Index on (country, city, age)
CREATE INDEX idx_users_location ON users(country, city, age);

-- Can use this index:
WHERE country = 'US'
WHERE country = 'US' AND city = 'NYC'
WHERE country = 'US' AND city = 'NYC' AND age > 25

-- Cannot use this index (skips leftmost column):
WHERE city = 'NYC'
WHERE age > 25
```

**Leftmost prefix rule:** A composite index on `(A, B, C)` can be used for queries on `A`, `A+B`, or `A+B+C` — but not `B` or `C` alone.

---

## Other Index Types

| Type | Description | Use Case |
|------|-------------|----------|
| B-Tree | Default. Sorted tree structure | Most queries |
| Hash | Exact equality only, faster for `=` | Exact match lookups |
| Full-Text | Tokenized text search | `LIKE '%word%'` / search features |
| Spatial (GiST) | Geometric/geographic data | Location queries |
| Partial | Index only a subset of rows | `WHERE status = 'active'` |

---

## Real-World Use Case

- **User lookups:** Index on `email` makes login queries instant.
- **Order history:** Composite index on `(user_id, created_at DESC)` speeds up "show me this user's recent orders."
- **Search filters:** Index on `(category, price, in_stock)` for product filter pages.
- **Analytics:** Covering index (index includes all needed columns) avoids reading the main table entirely.
