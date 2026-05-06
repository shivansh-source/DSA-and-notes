# Go Interfaces

> An interface in Go is just a list of method signatures. If a type has all those methods, it automatically satisfies the interface — no explicit declaration needed. This is called **duck typing**: "if it walks like a duck and quacks like a duck, it is a duck."

---

## What is an Interface?

An interface defines behaviour, not data.

```go
// Define an interface
type Animal interface {
    Sound() string
    Name()  string
}

// Dog implements Animal — implicitly, just by having the methods
type Dog struct{}

func (d Dog) Sound() string { return "Woof" }
func (d Dog) Name()  string { return "Dog" }

// Cat also implements Animal
type Cat struct{}

func (c Cat) Sound() string { return "Meow" }
func (c Cat) Name()  string { return "Cat" }
```

---

## Using Interfaces (Polymorphism)

```go
func describe(a Animal) {
    fmt.Printf("%s says %s\n", a.Name(), a.Sound())
}

func main() {
    animals := []Animal{Dog{}, Cat{}}
    for _, a := range animals {
        describe(a) // works for any Animal
    }
}
// Output:
// Dog says Woof
// Cat says Meow
```

**Key insight:** `describe` doesn't know or care about the concrete type. It only cares that the value has `Sound()` and `Name()` methods.

---

## The Empty Interface

`interface{}` (or `any` in Go 1.18+) has no methods — every type satisfies it.

```go
func printAnything(v interface{}) {
    fmt.Println(v)
}

printAnything(42)
printAnything("hello")
printAnything([]int{1, 2, 3})
```

Use it sparingly — you lose type safety. Prefer specific interfaces when you can.

---

## Type Assertion

Extract the underlying concrete value from an interface:

```go
var a Animal = Dog{}

// Unsafe — panics if wrong type
d := a.(Dog)

// Safe — use the "comma ok" pattern
d, ok := a.(Dog)
if ok {
    fmt.Println("it's a Dog:", d)
}
```

---

## Type Switch

Check multiple types cleanly:

```go
func whatIs(v interface{}) {
    switch t := v.(type) {
    case int:
        fmt.Printf("int: %d\n", t)
    case string:
        fmt.Printf("string: %q\n", t)
    case bool:
        fmt.Printf("bool: %v\n", t)
    default:
        fmt.Printf("unknown type: %T\n", t)
    }
}
```

---

## Common Standard Library Interfaces

| Interface | Methods | Used for |
|-----------|---------|----------|
| `fmt.Stringer` | `String() string` | Custom string representation |
| `io.Reader` | `Read(p []byte) (n int, err error)` | Reading from any source |
| `io.Writer` | `Write(p []byte) (n int, err error)` | Writing to any destination |
| `error` | `Error() string` | Error values |
| `sort.Interface` | `Len`, `Less`, `Swap` | Sorting custom types |

### Example: Implementing fmt.Stringer

```go
type Point struct {
    X, Y int
}

func (p Point) String() string {
    return fmt.Sprintf("(%d, %d)", p.X, p.Y)
}

p := Point{3, 4}
fmt.Println(p) // Prints: (3, 4) — calls p.String() automatically
```

---

## Interface Composition

Interfaces can be composed from other interfaces:

```go
type Reader interface {
    Read(p []byte) (n int, err error)
}

type Writer interface {
    Write(p []byte) (n int, err error)
}

// ReadWriter is the combination of both
type ReadWriter interface {
    Reader
    Writer
}
```

---

## Diagram: Interface Satisfaction

```
         Interface
         ┌─────────────────┐
         │  Animal          │
         │  ┌────────────┐  │
         │  │ Sound()    │  │
         │  │ Name()     │  │
         │  └────────────┘  │
         └─────────────────┘
                ▲   ▲
                │   │
         ┌──────┘   └──────┐
         │                 │
    ┌────┴───┐        ┌────┴───┐
    │  Dog   │        │  Cat   │
    │ Sound()│        │ Sound()│
    │ Name() │        │ Name() │
    └────────┘        └────────┘
```

Both `Dog` and `Cat` satisfy `Animal` without any explicit declaration.

---

## Real-World Use Case

- **`io.Reader` and `io.Writer`** — All of Go's I/O (files, network, buffers, gzip) uses these two interfaces. Write code that accepts `io.Reader` and it works with files, HTTP bodies, in-memory buffers, and more.
- **`http.Handler`** — The entire Go web framework ecosystem is built on one interface: `ServeHTTP(ResponseWriter, *Request)`.
- **Testing** — Interfaces make it easy to swap real dependencies with mocks/fakes in tests.
