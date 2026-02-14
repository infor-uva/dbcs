# Resume

The article explains how **Clean Architecture** and **Domain-Driven Design (DDD)** help build robust, scalable, and
maintainable software—and how combining them strengthens system design.

**Clean Architecture**, introduced by Robert C. Martin, structures systems so that core business logic is independent of
frameworks, databases, and user interfaces. It emphasizes independence, testability, flexibility, and maintainability
through a layered (concentric) architecture where the domain sits at the center.

**Domain-Driven Design (DDD)**, pioneered by Eric Evans, focuses on deeply understanding and modeling the business
domain. It introduces concepts such as Entities, Value Objects, Aggregates, Repositories, Services, and Bounded Contexts
to manage complexity and align software closely with business needs.

### Benefits

* **Clean Architecture** improves separation of concerns, testability, flexibility, and scalability.
* **DDD** enhances business alignment, complexity management, communication between stakeholders, and focused
  development.

### Combining Both Approaches

When integrated:

* DDD’s rich domain model fits naturally into Clean Architecture’s core layer.
* Bounded Contexts align well with layered modular structures.
* Repositories and services integrate cleanly into application layers.
* The result is highly testable, maintainable, and business-aligned software.

**Conclusion:**
Clean Architecture provides structural discipline, while DDD ensures accurate business modeling. Together, they enable
the development of technically sound systems that remain adaptable and closely aligned with evolving business
requirements.

# Mix

Here’s a **plain-text example** combining **Clean Architecture** and **Domain-Driven Design (DDD)** in a simple **Order
Management System**.

---

## 🧩 Example: Order Management System

Business requirement:
Customers can place orders. An order contains products. Orders must not be empty and must calculate a total price.

We’ll structure it using:

* **Clean Architecture layers**
* **DDD tactical patterns (Entities, Value Objects, Aggregates, Repositories)**

---

## 🏗 Architecture Overview (Clean Architecture Style)

```
+--------------------------------------------------+
|                 Presentation Layer               |
| (Controllers, API endpoints, UI)                |
+--------------------------------------------------+
|               Application Layer                  |
| (Use Cases, Application Services)               |
+--------------------------------------------------+
|                   Domain Layer                   |
| (Entities, Value Objects, Aggregates,           |
|  Domain Services, Repository Interfaces)        |
+--------------------------------------------------+
|               Infrastructure Layer               |
| (Database, ORM, External APIs,                  |
|  Repository Implementations)                    |
+--------------------------------------------------+
```

The **Domain Layer** is the core and has no dependencies on other layers.

---

## 📦 Domain Layer (DDD Core)

This is the heart of the system.

### 1️⃣ Entity: Order (Aggregate Root)

```
class Order {
    private OrderId id;
    private List<OrderItem> items;
    private OrderStatus status;

    public Order(OrderId id) {
        this.id = id;
        this.items = new ArrayList<>();
        this.status = OrderStatus.CREATED;
    }

    public void addItem(Product product, int quantity) {
        if (quantity <= 0)
            throw new IllegalArgumentException("Quantity must be positive");

        items.add(new OrderItem(product, quantity));
    }

    public Money calculateTotal() {
        return items.stream()
            .map(OrderItem::calculateSubtotal)
            .reduce(Money.zero(), Money::add);
    }

    public void confirm() {
        if (items.isEmpty())
            throw new IllegalStateException("Cannot confirm empty order");

        this.status = OrderStatus.CONFIRMED;
    }
}
```

👉 `Order` is the **Aggregate Root**
All modifications go through it.

---

### 2️⃣ Entity: OrderItem

```
class OrderItem {
    private Product product;
    private int quantity;

    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Money calculateSubtotal() {
        return product.getPrice().multiply(quantity);
    }
}
```

---

### 3️⃣ Entity: Product

```
class Product {
    private ProductId id;
    private String name;
    private Money price;

    // getters
}
```

---

### 4️⃣ Value Object: Money

```
class Money {
    private BigDecimal amount;

    public Money(BigDecimal amount) {
        this.amount = amount;
    }

    public Money add(Money other) {
        return new Money(this.amount.add(other.amount));
    }

    public Money multiply(int factor) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(factor)));
    }

    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }
}
```

Value Objects:

* No identity
* Immutable
* Compared by value

---

### 5️⃣ Repository Interface (DDD + Clean Architecture)

Defined in **Domain Layer**:

```
interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(OrderId id);
}
```

⚠️ Notice:
No database details here.

---

## 🧠 Application Layer (Use Cases)

Contains orchestration logic.

### Use Case: PlaceOrder

```
class PlaceOrderUseCase {

    private final OrderRepository orderRepository;

    public PlaceOrderUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void execute(PlaceOrderRequest request) {

        Order order = new Order(new OrderId(request.orderId()));

        for (OrderItemRequest item : request.items()) {
            Product product = /* fetch product from domain service */;
            order.addItem(product, item.quantity());
        }

        order.confirm();

        orderRepository.save(order);
    }
}
```

Responsibilities:

* Orchestrates flow
* Calls domain logic
* Persists through repository
* No framework code

---

## 🌍 Infrastructure Layer

Implements repository using a database.

```
class JpaOrderRepository implements OrderRepository {

    private final SpringDataOrderRepository jpaRepository;

    @Override
    public void save(Order order) {
        jpaRepository.save(OrderMapper.toEntity(order));
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return jpaRepository.findById(id.value())
            .map(OrderMapper::toDomain);
    }
}
```

Infrastructure depends on:

* Database
* ORM
* Frameworks

But **Domain does NOT depend on Infrastructure**.

---

## 🎯 Presentation Layer

Example REST Controller:

```
@RestController
class OrderController {

    private final PlaceOrderUseCase placeOrderUseCase;

    @PostMapping("/orders")
    public ResponseEntity<Void> placeOrder(@RequestBody PlaceOrderRequest request) {
        placeOrderUseCase.execute(request);
        return ResponseEntity.ok().build();
    }
}
```

This layer depends on:

* Framework (e.g., Spring)
* Application layer

---

## 🔄 Dependency Rule (Very Important)

Dependencies always point **inward**:

```
Presentation → Application → Domain
Infrastructure → Domain
```

The **Domain layer depends on nothing**.

---

## 🧩 How DDD and Clean Architecture Work Together

### DDD provides:

* Rich domain model
* Aggregates
* Value objects
* Bounded contexts

### Clean Architecture provides:

* Strict layering
* Dependency inversion
* Isolation of business logic

---

## 🚀 Final Result

You get:

✔ Business rules isolated in Domain
✔ High testability (domain can be tested without DB or frameworks)
✔ Replaceable infrastructure
✔ Clear separation of responsibilities
✔ Strong alignment with business language

---

If you want, I can also show:

* A microservices version with Bounded Contexts
* A folder structure example
* The same example in TypeScript or .NET
* A diagram version (ASCII with more details)

# -------------

Here’s a **plain-text example** combining **Clean Architecture** and **Domain-Driven Design (DDD)** in a simple **Order
Management System**.

---

# 🧩 Example: Order Management System

Business requirement:
Customers can place orders. An order contains products. Orders must not be empty and must calculate a total price.

We’ll structure it using:

* **Clean Architecture layers**
* **DDD tactical patterns (Entities, Value Objects, Aggregates, Repositories)**

---

# 🏗 Architecture Overview (Clean Architecture Style)

```
+--------------------------------------------------+
|                 Presentation Layer               |
| (Controllers, API endpoints, UI)                 |
+--------------------------------------------------+
|               Application Layer                  |
| (Use Cases, Application Services)                |
+--------------------------------------------------+
|                   Domain Layer                   |
| (Entities, Value Objects, Aggregates,            |
|  Domain Services, Repository Interfaces)         |
+--------------------------------------------------+
|               Infrastructure Layer               |
| (Database, ORM, External APIs,                   |
|  Repository Implementations)                     |
+--------------------------------------------------+
```

The **Domain Layer** is the core and has no dependencies on other layers.

---

# 📦 Domain Layer (DDD Core)

This is the heart of the system.

## 1️⃣ Entity: Order (Aggregate Root)

```
class Order {
    private OrderId id;
    private List<OrderItem> items;
    private OrderStatus status;

    public Order(OrderId id) {
        this.id = id;
        this.items = new ArrayList<>();
        this.status = OrderStatus.CREATED;
    }

    public void addItem(Product product, int quantity) {
        if (quantity <= 0)
            throw new IllegalArgumentException("Quantity must be positive");

        items.add(new OrderItem(product, quantity));
    }

    public Money calculateTotal() {
        return items.stream()
            .map(OrderItem::calculateSubtotal)
            .reduce(Money.zero(), Money::add);
    }

    public void confirm() {
        if (items.isEmpty())
            throw new IllegalStateException("Cannot confirm empty order");

        this.status = OrderStatus.CONFIRMED;
    }
}
```

👉 `Order` is the **Aggregate Root**
All modifications go through it.

---

## 2️⃣ Entity: OrderItem

```
class OrderItem {
    private Product product;
    private int quantity;

    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Money calculateSubtotal() {
        return product.getPrice().multiply(quantity);
    }
}
```

---

## 3️⃣ Entity: Product

```
class Product {
    private ProductId id;
    private String name;
    private Money price;

    // getters
}
```

---

## 4️⃣ Value Object: Money

```
class Money {
    private BigDecimal amount;

    public Money(BigDecimal amount) {
        this.amount = amount;
    }

    public Money add(Money other) {
        return new Money(this.amount.add(other.amount));
    }

    public Money multiply(int factor) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(factor)));
    }

    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }
}
```

Value Objects:

* No identity
* Immutable
* Compared by value

---

## 5️⃣ Repository Interface (DDD + Clean Architecture)

Defined in **Domain Layer**:

```
interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(OrderId id);
}
```

⚠️ Notice:
No database details here.

---

# 🧠 Application Layer (Use Cases)

Contains orchestration logic.

## Use Case: PlaceOrder

```
class PlaceOrderUseCase {

    private final OrderRepository orderRepository;

    public PlaceOrderUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void execute(PlaceOrderRequest request) {

        Order order = new Order(new OrderId(request.orderId()));

        for (OrderItemRequest item : request.items()) {
            Product product = /* fetch product from domain service */;
            order.addItem(product, item.quantity());
        }

        order.confirm();

        orderRepository.save(order);
    }
}
```

Responsibilities:

* Orchestrates flow
* Calls domain logic
* Persists through repository
* No framework code

---

# 🌍 Infrastructure Layer

Implements repository using a database.

```
class JpaOrderRepository implements OrderRepository {

    private final SpringDataOrderRepository jpaRepository;

    @Override
    public void save(Order order) {
        jpaRepository.save(OrderMapper.toEntity(order));
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return jpaRepository.findById(id.value())
            .map(OrderMapper::toDomain);
    }
}
```

Infrastructure depends on:

* Database
* ORM
* Frameworks

But **Domain does NOT depend on Infrastructure**.

---

# 🎯 Presentation Layer

Example REST Controller:

```
@RestController
class OrderController {

    private final PlaceOrderUseCase placeOrderUseCase;

    @PostMapping("/orders")
    public ResponseEntity<Void> placeOrder(@RequestBody PlaceOrderRequest request) {
        placeOrderUseCase.execute(request);
        return ResponseEntity.ok().build();
    }
}
```

This layer depends on:

* Framework (e.g., Spring)
* Application layer

---

# 🔄 Dependency Rule (Very Important)

Dependencies always point **inward**:

```
Presentation → Application → Domain
Infrastructure → Domain
```

The **Domain layer depends on nothing**.

---

# 🧩 How DDD and Clean Architecture Work Together

### DDD provides:

* Rich domain model
* Aggregates
* Value objects
* Bounded contexts

### Clean Architecture provides:

* Strict layering
* Dependency inversion
* Isolation of business logic

---

# 🚀 Final Result

You get:

✔ Business rules isolated in Domain
✔ High testability (domain can be tested without DB or frameworks)
✔ Replaceable infrastructure
✔ Clear separation of responsibilities
✔ Strong alignment with business language

---

If you want, I can also show:

* A microservices version with Bounded Contexts
* A folder structure example
* The same example in TypeScript or .NET
* A diagram version (ASCII with more details)

# -----------

Excellent 👍 — now we’ll make it cleaner and more “UML-like”.

We will:

* Separate **each layer**
* Use **stereotypes** (like `<<Entity>>`, `<<ValueObject>>`, etc.)
* Keep everything in **plain text diagram style**
* Avoid code

---

# 📦 1️⃣ DOMAIN LAYER (Core – No Dependencies)

```
================================================================================
                               DOMAIN LAYER
================================================================================


                        <<AggregateRoot>> <<Entity>>
+--------------------------------------------------+
|                      Order                       |
+--------------------------------------------------+
| - id : OrderId                                  |
| - items : List<OrderItem>                       |
| - status : OrderStatus                          |
+--------------------------------------------------+
| + addItem(product, quantity)                    |
| + calculateTotal() : Money                      |
| + confirm()                                     |
+--------------------------------------------------+
                |
                | 1..*
                v


                            <<Entity>>
+--------------------------------------------------+
|                    OrderItem                    |
+--------------------------------------------------+
| - product : Product                             |
| - quantity : int                                |
+--------------------------------------------------+
| + calculateSubtotal() : Money                   |
+--------------------------------------------------+



                            <<Entity>>
+--------------------------------------------------+
|                     Product                      |
+--------------------------------------------------+
| - id : ProductId                                |
| - name : String                                 |
| - price : Money                                 |
+--------------------------------------------------+



-----------------------------
        VALUE OBJECTS
-----------------------------

                        <<ValueObject>>
+---------------------------+
|          Money            |
+---------------------------+
| - amount : Decimal        |
+---------------------------+
| + add()                   |
| + multiply()              |
+---------------------------+

                        <<ValueObject>>
+---------------------------+
|         OrderId           |
+---------------------------+
| - value : UUID            |
+---------------------------+

                        <<ValueObject>>
+---------------------------+
|        ProductId          |
+---------------------------+
| - value : UUID            |
+---------------------------+



-----------------------------
     DOMAIN INTERFACES
-----------------------------

                        <<Repository>>
+-------------------------------+
|        OrderRepository        |
+-------------------------------+
| + save(order)                 |
| + findById(id) : Order        |
+-------------------------------+

                        <<Repository>>
+-------------------------------+
|       ProductRepository       |
+-------------------------------+
| + findById(id) : Product      |
+-------------------------------+
```

✅ This layer contains:

* Business rules
* No framework references
* No database knowledge
* Pure domain model

---

# 🧠 2️⃣ APPLICATION LAYER (Use Cases)

```
================================================================================
                            APPLICATION LAYER
================================================================================


                        <<UseCase>>
+-------------------------------------------+
|           PlaceOrderUseCase               |
+-------------------------------------------+
| - orderRepository : OrderRepository      |
| - productRepository : ProductRepository  |
+-------------------------------------------+
| + execute(request)                        |
+-------------------------------------------+


                        <<DTO>>
+-------------------------------------------+
|           PlaceOrderRequest               |
+-------------------------------------------+
| - orderId                                 |
| - items                                   |
+-------------------------------------------+


                        <<DTO>>
+-------------------------------------------+
|           PlaceOrderItemRequest           |
+-------------------------------------------+
| - productId                               |
| - quantity                                |
+-------------------------------------------+
```

✅ Responsibilities:

* Orchestrates domain objects
* Uses repository interfaces
* Contains application flow logic
* No infrastructure details

---

# 🌍 3️⃣ INFRASTRUCTURE LAYER

```
================================================================================
                           INFRASTRUCTURE LAYER
================================================================================


                    <<RepositoryImplementation>>
+----------------------------------------------+
|            JpaOrderRepository                |
+----------------------------------------------+
| implements OrderRepository                   |
+----------------------------------------------+


                    <<RepositoryImplementation>>
+----------------------------------------------+
|           JpaProductRepository               |
+----------------------------------------------+
| implements ProductRepository                 |
+----------------------------------------------+


                    <<Mapper>>
+----------------------------------------------+
|                OrderMapper                   |
+----------------------------------------------+
| + toDomain()                                 |
| + toPersistence()                            |
+----------------------------------------------+
```

✅ Responsibilities:

* Database access
* ORM usage
* External integrations
* Implements domain interfaces

---

# 🌐 4️⃣ PRESENTATION LAYER

```
================================================================================
                           PRESENTATION LAYER
================================================================================


                        <<Controller>>
+-------------------------------------+
|          OrderController            |
+-------------------------------------+
| - placeOrderUseCase                 |
+-------------------------------------+
| + POST /orders                      |
+-------------------------------------+
```

✅ Responsibilities:

* HTTP handling
* Validation
* Calls use cases
* Returns responses

---

# 🔁 DEPENDENCY RULE (CLEAN ARCHITECTURE)

```
Presentation  --->  Application  --->  Domain
Infrastructure --->  Domain

Domain depends on NOTHING.
```

---

# 🎯 What This Demonstrates

Clean Architecture provides:

* Strict layering
* Dependency inversion
* Isolated core

DDD provides:

* Rich domain model
* Aggregates
* Entities
* Value Objects
* Repository abstractions

---

If you'd like next, I can:

* Add <<DomainService>> and <<DomainEvent>>
* Show the same example split into Bounded Contexts
* Add Payments and Shipping to make it more realistic
* Show a microservice boundary version

# ---------

## Comparative

Great question 👌
Below you’ll see **two UML-style diagrams** that make the structural difference clear:

* 🔵 **Clean Architecture (layer-driven)**
* 🟢 **Domain-Driven Design (domain-driven / model-centered)**

Both use the same example (Order system) so you can compare them directly.

---

## 🔵 1️⃣ Clean Architecture – UML Class Diagram View

Focus: **Layer separation & dependency direction**

```
================================================================================
                         CLEAN ARCHITECTURE (Layered)
================================================================================


        ┌─────────────────────────────────────────────┐
        │              <<Controller>>                 │
        │              OrderController                │
        └─────────────────────────────────────────────┘
                              │
                              ▼
        ┌─────────────────────────────────────────────┐
        │               <<UseCase>>                   │
        │           PlaceOrderUseCase                 │
        │---------------------------------------------│
        │ - orderRepository : OrderRepository        │
        └─────────────────────────────────────────────┘
                              │
                              ▼
        ┌─────────────────────────────────────────────┐
        │              <<Entity>>                     │
        │                  Order                      │
        └─────────────────────────────────────────────┘
                              ▲
                              │
        ┌─────────────────────────────────────────────┐
        │           <<Repository Interface>>         │
        │             OrderRepository                │
        └─────────────────────────────────────────────┘
                              ▲
                              │ implements
                              │
        ┌─────────────────────────────────────────────┐
        │     <<Repository Implementation>>          │
        │           JpaOrderRepository               │
        └─────────────────────────────────────────────┘
```

### 🔎 What this shows

* Strong **layer separation**
* Dependencies point **inward**
* Repository is defined in Domain
* Infrastructure implements it
* Focus is structural isolation

Clean Architecture is about:

```
Dependency Rule > Business isolation > Framework independence
```

It does NOT define:

* Aggregates
* Value objects
* Bounded contexts
* Ubiquitous language

It only defines structure and dependency flow.

---

## 🟢 2️⃣ Domain-Driven Design – UML Class Diagram View

Focus: **Domain modeling & relationships**

```
================================================================================
                        DOMAIN-DRIVEN DESIGN (Model-Centered)
================================================================================


                    <<AggregateRoot>> <<Entity>>
        ┌─────────────────────────────────────────────┐
        │                    Order                   │
        │---------------------------------------------│
        │ - id : OrderId                             │
        │ - status : OrderStatus                     │
        └─────────────────────────────────────────────┘
                      │ 1
                      │
                      │ 1..*
                      ▼
                <<Entity>>
        ┌─────────────────────────┐
        │        OrderItem        │
        └─────────────────────────┘
                      │
                      │ 1
                      ▼
                <<Entity>>
        ┌─────────────────────────┐
        │        Product          │
        └─────────────────────────┘


                <<ValueObject>>
        ┌─────────────────────────┐
        │         Money           │
        └─────────────────────────┘


                <<Repository>>
        ┌─────────────────────────┐
        │     OrderRepository     │
        └─────────────────────────┘
```

### 🔎 What this shows

* Aggregate boundaries
* Entity relationships
* Value objects
* Business invariants enforced in Aggregate Root
* Domain terminology

DDD is about:

```
Business modeling > Language alignment > Complexity management
```

It does NOT define:

* Layers
* Dependency rules
* Controllers
* Infrastructure isolation

---

## 🆚 Structural Difference (Side-by-Side Conceptual View)

| Clean Architecture                | Domain-Driven Design                 |
|-----------------------------------|--------------------------------------|
| Layer-focused                     | Model-focused                        |
| Organizes by technical boundaries | Organizes by domain boundaries       |
| Emphasizes dependency inversion   | Emphasizes aggregate consistency     |
| Defines where code lives          | Defines how business concepts relate |
| Protects business logic           | Enriches business logic              |

---

## 🧠 Another Way to Visualize It

### Clean Architecture is vertical separation

```
[ UI ]
  ↓
[ Application ]
  ↓
[ Domain ]
  ↑
[ Infrastructure ]
```

### DDD is horizontal domain modeling

```
Aggregate: Order
 ├── OrderItem
 ├── Money (ValueObject)
 └── Repository
```

---

## 🎯 The Core Difference

Clean Architecture answers:

> "How should dependencies flow?"

DDD answers:

> "How should the business domain be modeled?"

---

## 💡 Why They Work Together

When combined:

* DDD fills the **Domain layer**
* Clean Architecture protects it

So the final system becomes:

```
Clean Architecture (structure)
    contains
DDD (rich domain model)
```

---

If you'd like, I can next show:

* 🧩 Same comparison using relational (database-style) diagrams
* 🏢 A Bounded Context diagram vs layered architecture
* 🔄 What happens in a CRUD-only system vs a rich DDD system
* 📦 Microservices comparison (Clean Arch vs DDD contexts)
