# FinTech Wallet - Clean Architecture Implementation

A production-grade FinTech wallet system built with **Clean Architecture (Hexagonal/Ports & Adapters)** using Spring Boot 3.2.1.

## 🏗️ Architecture Overview

This application strictly follows **Clean Architecture** principles to ensure:
- **Clear separation of concerns** - Each layer has a distinct responsibility
- **Testability** - Domain logic is independent of frameworks
- **Maintainability** - Changes in one layer don't ripple across others
- **Flexibility** - Easy to swap infrastructure components

```
┌─────────────────────────────────────────────────────────────┐
│                    Interface Layer                          │
│           (REST Controllers, DTOs, Exception Handlers)      │
└─────────────────────────────────────────────────────────────┘
                             ↓
┌─────────────────────────────────────────────────────────────┐
│                   Application Layer                         │
│         (Use Cases, Commands, Ports/Interfaces)             │
└─────────────────────────────────────────────────────────────┘
                             ↓
┌─────────────────────────────────────────────────────────────┐
│                     Domain Layer                            │
│     (Pure Java - Entities, Value Objects, Exceptions)       │
│              NO FRAMEWORK DEPENDENCIES                      │
└─────────────────────────────────────────────────────────────┘
                             ↑
┌─────────────────────────────────────────────────────────────┐
│                 Infrastructure Layer                        │
│    (JPA Entities, Repositories, Adapters, Configuration)    │
└─────────────────────────────────────────────────────────────┘
```

### Why Each Layer Exists

| Layer | Purpose | Key Rules |
|-------|---------|-----------|
| **Domain** | Contains core business logic and rules | ❌ No Spring, ❌ No JPA, ❌ No Lombok |
| **Application** | Orchestrates business workflows | Defines ports (interfaces), transaction boundaries |
| **Infrastructure** | Handles external concerns | Database, messaging, external APIs |
| **Interface** | Entry points for external systems | REST APIs, DTOs, validation |

## 🔑 Key Design Decisions

### 1. Ledger-Based Wallet (No Balance Column)

**Traditional Approach:**
```sql
CREATE TABLE wallets (
    id UUID,
    balance DECIMAL  -- ❌ Race conditions!
);
```

**Our Approach:**
```sql
CREATE TABLE ledger_entries (
    id UUID,
    wallet_id UUID,
    entry_type VARCHAR(10),  -- CREDIT or DEBIT
    amount DECIMAL
);
-- Balance = SUM(credits) - SUM(debits)
```

**Benefits:**
- ✅ **Complete audit trail** - Every transaction is traceable
- ✅ **No race conditions** - Balance is calculated, not stored
- ✅ **Financial accuracy** - Immutable ledger entries
- ✅ **Regulatory compliance** - Full transaction history

### 2. Money Value Object (No Floating Point)

**❌ NEVER DO THIS:**
```java
double balance = 100.50;  // Floating-point errors!
```

**✅ OUR APPROACH:**
```java
Money balance = Money.of(new BigDecimal("100.50"), Currency.of("USD"));
```

**Benefits:**
- ✅ **Exact decimal arithmetic** using `BigDecimal`
- ✅ **Explicit rounding** with `HALF_UP` mode
- ✅ **Currency safety** - Cannot mix USD and EUR
- ✅ **Immutable** - Thread-safe value object

### 3. Separation of Domain and JPA Entities

**Domain Model (Pure Java):**
```java
public final class Wallet {
    private final WalletId id;
    private final Currency currency;
    private final List<LedgerEntry> ledgerEntries;
    // NO @Entity, NO @Table, NO @Column
}
```

**JPA Entity (Infrastructure):**
```java
@Entity
@Table(name = "wallets")
public class WalletJpaEntity {
    @Id private UUID id;
    @Column private String currency;
    // Spring/JPA annotations allowed here
}
```

**Benefits:**
- ✅ Domain stays **framework-agnostic**
- ✅ Easy to **unit test** without Spring context
- ✅ Can **swap persistence** technologies easily

### 4. Constructor Injection Only

**❌ AVOID:**
```java
@Autowired
private WalletRepository repository;  // Field injection
```

**✅ USE:**
```java
private final WalletRepository repository;

public WalletService(WalletRepository repository) {
    this.repository = repository;  // Constructor injection
}
```

**Benefits:**
- ✅ **Explicit dependencies** - Clear what's required
- ✅ **Testable** - Easy to inject mocks
- ✅ **Immutable** - Dependencies are final

## 📦 Package Structure

```
com.fintech.wallet
├── domain
│   ├── model                    # Entities & Aggregates
│   │   ├── Wallet.java          # Aggregate Root
│   │   ├── LedgerEntry.java     # Entity
│   │   └── Transaction.java     # Entity
│   ├── valueobject              # Immutable Value Objects
│   │   ├── Money.java           # BigDecimal wrapper
│   │   ├── Currency.java        # Validated currency
│   │   └── *Id.java             # Type-safe IDs
│   └── exception                # Domain Exceptions
│       ├── InsufficientBalanceException.java
│       ├── InvalidCurrencyException.java
│       └── WalletNotFoundException.java
├── application
│   ├── usecase                  # Use Case Handlers
│   │   ├── CreateWalletUseCaseHandler.java
│   │   └── TransferMoneyUseCaseHandler.java
│   ├── port
│   │   ├── in                   # Input Ports (Interfaces)
│   │   │   ├── CreateWalletUseCase.java
│   │   │   └── TransferMoneyUseCase.java
│   │   └── out                  # Output Ports (Interfaces)
│   │       ├── LoadWalletPort.java
│   │       ├── SaveWalletPort.java
│   │       └── SaveTransactionPort.java
│   └── command                  # Immutable Commands
│       ├── CreateWalletCommand.java
│       └── TransferMoneyCommand.java
├── infrastructure
│   ├── persistence
│   │   ├── entity               # JPA Entities
│   │   │   ├── WalletJpaEntity.java
│   │   │   ├── LedgerEntryJpaEntity.java
│   │   │   └── TransactionJpaEntity.java
│   │   ├── repository           # Spring Data Repositories
│   │   │   └── WalletJpaRepository.java
│   │   └── mapper               # Domain ↔ JPA Mappers
│   │       ├── WalletMapper.java
│   │       └── TransactionMapper.java
│   ├── adapter                  # Port Implementations
│   │   ├── WalletPersistenceAdapter.java
│   │   └── TransactionPersistenceAdapter.java
│   └── config                   # Spring Configuration
│       └── BeanConfiguration.java
└── interfaces
    └── rest
        ├── controller           # REST Controllers
        │   ├── WalletController.java
        │   └── TransferController.java
        ├── dto                  # Request/Response DTOs
        │   ├── CreateWalletRequest.java
        │   ├── WalletResponse.java
        │   ├── TransferMoneyRequest.java
        │   └── TransferResponse.java
        └── advice               # Exception Handling
            └── GlobalExceptionHandler.java
```

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL 14+ (or use Docker)

### Database Setup

**Option 1: Docker**
```bash
docker run --name wallet-db -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres:14
```

**Option 2: Local PostgreSQL**
```bash
psql -U postgres
CREATE DATABASE wallet_db;
\i src/main/resources/schema.sql
```

### Running the Application

```bash
mvn clean install
mvn spring-boot:run
```

Application starts on `http://localhost:8080`

## 📡 API Endpoints

### Create Wallet

```bash
POST /api/wallets
Content-Type: application/json

{
  "currency": "USD"
}
```

**Response:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "currency": "USD",
  "balance": 0.00,
  "createdAt": "2026-01-20T22:00:00Z"
}
```

### Get Wallet

```bash
GET /api/wallets/{walletId}
```

### Transfer Money

```bash
POST /api/transfers
Content-Type: application/json

{
  "sourceWalletId": "550e8400-e29b-41d4-a716-446655440000",
  "destinationWalletId": "660f9511-f3ac-52e5-b827-557766551111",
  "amount": 100.50,
  "currency": "USD",
  "description": "Payment for services"
}
```

**Response:**
```json
{
  "transactionId": "770fa622-g4bd-63f6-c938-668877662222",
  "sourceWalletId": "550e8400-e29b-41d4-a716-446655440000",
  "destinationWalletId": "660f9511-f3ac-52e5-b827-557766551111",
  "amount": 100.50,
  "currency": "USD",
  "status": "COMPLETED",
  "timestamp": "2026-01-20T22:05:30Z"
}
```

## 🧪 Testing

### Run All Tests

```bash
mvn test
```

### Run Domain Tests Only

```bash
mvn test -Dtest="com.fintech.wallet.domain.**"
```

### Domain Tests Highlights

- **No Spring Context** - Pure Java unit tests
- **Fast Execution** - Milliseconds, not seconds
- **High Coverage** - Focuses on business logic

Example test:
```java
@Test
@DisplayName("Should throw exception when debiting more than balance")
void shouldThrowExceptionWhenDebitingMoreThanBalance() {
    Wallet wallet = Wallet.create(Currency.of("USD"));
    wallet.credit(Money.of("100.00", "USD"), transactionId, "Deposit");

    assertThatThrownBy(() -> 
        wallet.debit(Money.of("150.00", "USD"), transactionId, "Withdrawal"))
        .isInstanceOf(InsufficientBalanceException.class);
}
```

## 📊 Database Schema

### Tables

| Table | Purpose |
|-------|---------|
| `wallets` | Stores wallet metadata (currency, creation time) |
| `ledger_entries` | Immutable transaction log (no updates/deletes) |
| `transactions` | Transfer records between wallets |

### Key Constraints

- `ledger_entries.amount` - Always positive (sign determined by type)
- `transactions.source_wallet_id` ≠ `destination_wallet_id`
- All amounts use `NUMERIC(19, 2)` for precision

## 🔐 Security & Best Practices

✅ **Input Validation** - Jakarta Validation on DTOs  
✅ **Defensive Programming** - Null checks, validations  
✅ **Clean Error Responses** - No internal exceptions leaked  
✅ **Transaction Boundaries** - At use-case level  
✅ **Immutability** - Value objects and domain entities  
✅ **Type Safety** - Strong typing with value objects  

## 🎯 Flow Example: Money Transfer

```
1. REST Controller receives TransferMoneyRequest
   ↓
2. Validates request (Jakarta Validation)
   ↓
3. Creates TransferMoneyCommand
   ↓
4. Calls TransferMoneyUseCase
   ↓
5. Use CaseHandler:
   - Loads source & destination wallets (via LoadWalletPort)
   - Validates currencies match
   - Creates Transaction
   - Debits source wallet (validates balance)
   - Credits destination wallet
   - Saves wallets (via SaveWalletPort)
   - Saves transaction (via SaveTransactionPort)
   ↓
6. @Transactional ensures atomicity
   ↓
7. Returns TransactionId
   ↓
8. Controller maps to TransferResponse
```

## 📚 Further Reading

- [Clean Architecture (Robert C. Martin)](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/)
- [Domain-Driven Design](https://martinfowler.com/bliki/DomainDrivenDesign.html)

## 📝 License

This is a demonstration project for educational purposes.

---

**Built with ❤️ following senior-level best practices for FinTech applications.**
