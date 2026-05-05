# Go Concurrency

> Go's big selling point is making concurrency easy. Instead of threads (heavy), Go uses **goroutines** (lightweight). Instead of shared memory with locks, Go encourages **channels** to communicate between goroutines.
>
> Go's motto: **"Don't communicate by sharing memory; share memory by communicating."**

---

## Goroutines

A goroutine is a function running concurrently with other goroutines. Start one with the `go` keyword.

```go
func say(s string) {
    for i := 0; i < 3; i++ {
        fmt.Println(s)
        time.Sleep(100 * time.Millisecond)
    }
}

func main() {
    go say("world") // runs concurrently
    say("hello")    // runs in main goroutine
}
```

**Key facts:**
- Goroutines are multiplexed onto OS threads by the Go runtime (M:N threading).
- Starting a goroutine takes ~2KB of stack (OS threads are 1-8MB).
- You can run millions of goroutines on a single machine.

---

## Channels

Channels let goroutines communicate by passing values.

```go
// Create a channel for int values
ch := make(chan int)

// Send a value (blocks until someone receives)
go func() {
    ch <- 42
}()

// Receive a value (blocks until someone sends)
val := <-ch
fmt.Println(val) // 42
```

### Buffered Channels

A buffered channel holds values without a receiver being ready, up to its capacity.

```go
ch := make(chan int, 3) // capacity of 3

ch <- 1
ch <- 2
ch <- 3
// ch <- 4 would block — channel is full

fmt.Println(<-ch) // 1
fmt.Println(<-ch) // 2
```

---

## Diagram: Goroutine Communication via Channel

```
Goroutine A          Channel           Goroutine B
───────────      ──────────────      ───────────────
  compute()  →→→  [value] ─────→  →→→  process(value)
```

The channel acts as a safe pipe between goroutines.

---

## WaitGroups

Use `sync.WaitGroup` to wait for a group of goroutines to finish.

```go
import "sync"

func main() {
    var wg sync.WaitGroup

    for i := 0; i < 5; i++ {
        wg.Add(1) // increment counter before launching goroutine
        go func(id int) {
            defer wg.Done() // decrement when goroutine finishes
            fmt.Printf("worker %d done\n", id)
        }(i)
    }

    wg.Wait() // block until all goroutines call Done()
    fmt.Println("all workers finished")
}
```

---

## Select Statement

`select` lets a goroutine wait on multiple channel operations.

```go
ch1 := make(chan string)
ch2 := make(chan string)

go func() { ch1 <- "one" }()
go func() { ch2 <- "two" }()

// Wait for whichever channel is ready first
select {
case msg1 := <-ch1:
    fmt.Println("Received from ch1:", msg1)
case msg2 := <-ch2:
    fmt.Println("Received from ch2:", msg2)
}
```

With a timeout:

```go
select {
case result := <-ch:
    fmt.Println("got:", result)
case <-time.After(1 * time.Second):
    fmt.Println("timed out")
}
```

---

## Mutex (Shared Memory When Needed)

When you must share state between goroutines, use `sync.Mutex` to protect it.

```go
import "sync"

type SafeCounter struct {
    mu sync.Mutex
    v  map[string]int
}

func (c *SafeCounter) Inc(key string) {
    c.mu.Lock()
    c.v[key]++
    c.mu.Unlock()
}

func (c *SafeCounter) Value(key string) int {
    c.mu.Lock()
    defer c.mu.Unlock()
    return c.v[key]
}
```

---

## Common Concurrency Patterns

### Worker Pool

```go
func worker(id int, jobs <-chan int, results chan<- int) {
    for j := range jobs {
        fmt.Printf("worker %d processing job %d\n", id, j)
        results <- j * 2 // send result
    }
}

func main() {
    jobs    := make(chan int, 100)
    results := make(chan int, 100)

    // Launch 3 workers
    for w := 1; w <= 3; w++ {
        go worker(w, jobs, results)
    }

    // Send 5 jobs
    for j := 1; j <= 5; j++ {
        jobs <- j
    }
    close(jobs)

    // Collect results
    for a := 1; a <= 5; a++ {
        fmt.Println(<-results)
    }
}
```

### Pipeline

```go
// Stage 1: generate numbers
func generate(nums ...int) <-chan int {
    out := make(chan int)
    go func() {
        for _, n := range nums {
            out <- n
        }
        close(out)
    }()
    return out
}

// Stage 2: square numbers
func square(in <-chan int) <-chan int {
    out := make(chan int)
    go func() {
        for n := range in {
            out <- n * n
        }
        close(out)
    }()
    return out
}

func main() {
    c := generate(2, 3, 4)
    out := square(c)
    fmt.Println(<-out, <-out, <-out) // 4, 9, 16
}
```

---

## Real-World Use Case

- **HTTP servers** — Each request is handled in its own goroutine automatically.
- **Parallel data processing** — Fan out work to a pool of workers, fan in results.
- **Background jobs** — Run periodic tasks or async processing alongside your main app.
- **Microservices** — Handle thousands of concurrent connections cheaply.
