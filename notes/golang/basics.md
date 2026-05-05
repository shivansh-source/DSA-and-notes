# Go Basics

> Think of Go like a simpler C that has garbage collection and is built for concurrency. It's fast to compile, easy to read, and has a small standard library that covers most needs.

---

## Variables

In Go, you can declare variables in a few ways:

```go
// Long form — explicit type
var name string = "Alice"
var age  int    = 30

// Short form — type is inferred (only inside functions)
name := "Alice"
age  := 30

// Multiple variables at once
var x, y int = 1, 2
```

**Zero values** — Go initializes every variable to a zero value if you don't assign one:
- `int` → `0`
- `string` → `""`
- `bool` → `false`
- pointer/slice/map → `nil`

---

## Basic Types

```go
var i   int     = 42
var f   float64 = 3.14
var b   bool    = true
var s   string  = "hello"
var r   rune    = 'A'  // rune is an alias for int32 (Unicode code point)
var bt  byte    = 255  // byte is an alias for uint8
```

---

## Functions

```go
// Basic function
func add(x int, y int) int {
    return x + y
}

// Shorthand when parameters share a type
func add(x, y int) int {
    return x + y
}

// Multiple return values — very common in Go
func divide(a, b float64) (float64, error) {
    if b == 0 {
        return 0, fmt.Errorf("cannot divide by zero")
    }
    return a / b, nil
}

// Named return values
func minMax(nums []int) (min, max int) {
    min, max = nums[0], nums[0]
    for _, n := range nums {
        if n < min { min = n }
        if n > max { max = n }
    }
    return // "naked return" uses named return variables
}
```

---

## Control Flow

```go
// if / else — no parentheses required
if x > 10 {
    fmt.Println("big")
} else if x > 5 {
    fmt.Println("medium")
} else {
    fmt.Println("small")
}

// if with init statement — scope is limited to the block
if err := doSomething(); err != nil {
    fmt.Println("error:", err)
}

// for — Go's only loop keyword
for i := 0; i < 5; i++ {
    fmt.Println(i)
}

// while-style loop
for x < 100 {
    x *= 2
}

// infinite loop
for {
    // runs forever until break or return
}

// range — iterate over slices, maps, strings, channels
nums := []int{1, 2, 3}
for index, value := range nums {
    fmt.Println(index, value)
}

// switch — no fallthrough by default
switch day {
case "Mon", "Tue", "Wed", "Thu", "Fri":
    fmt.Println("weekday")
case "Sat", "Sun":
    fmt.Println("weekend")
default:
    fmt.Println("unknown")
}
```

---

## Arrays and Slices

```go
// Array — fixed size, rarely used directly
var arr [5]int = [5]int{1, 2, 3, 4, 5}

// Slice — dynamic size, used everywhere
s := []int{1, 2, 3}
s = append(s, 4, 5)  // append elements

// Slice of a slice
sub := s[1:3] // elements at index 1 and 2

// Make a slice with length and capacity
s2 := make([]int, 3, 10) // len=3, cap=10
```

**Mental model:** A slice is a view into an underlying array. It has three parts: a pointer to the array, a length, and a capacity.

```
slice header:
  ptr → [1][2][3][4][5]
  len = 3
  cap = 5
```

---

## Maps

```go
// Create a map
scores := map[string]int{
    "Alice": 95,
    "Bob":   87,
}

// Access
fmt.Println(scores["Alice"]) // 95

// Check if key exists — always do this!
val, ok := scores["Charlie"]
if !ok {
    fmt.Println("Charlie not found")
}

// Add / update
scores["Charlie"] = 72

// Delete
delete(scores, "Bob")

// Iterate
for name, score := range scores {
    fmt.Printf("%s: %d\n", name, score)
}
```

---

## Structs

```go
type Person struct {
    Name string
    Age  int
}

// Create an instance
p := Person{Name: "Alice", Age: 30}
fmt.Println(p.Name) // Alice

// Methods on structs (use pointer receiver to mutate)
func (p *Person) Birthday() {
    p.Age++
}

p.Birthday()
fmt.Println(p.Age) // 31
```

---

## Error Handling

Go has no exceptions. Errors are regular values returned from functions.

```go
result, err := strconv.Atoi("abc")
if err != nil {
    fmt.Println("conversion failed:", err)
    return
}
fmt.Println(result)
```

**Pattern:** Always check `err != nil` immediately after a call that can fail.

---

## Real-World Use Case

Go is used for:
- **Web servers** — net/http is built in; frameworks like Gin are popular
- **CLI tools** — fast startup, single binary, easy cross-compilation
- **Microservices** — lightweight, low memory, great concurrency
- **DevOps tools** — Docker, Kubernetes, and Terraform are all written in Go
