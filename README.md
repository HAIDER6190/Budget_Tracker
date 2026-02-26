# 💰 BudgetTracker API

A secure REST API built with **Spring Boot** for personal budget and expense management. Track your income, expenses, accounts, and budgets — all organized by categories with JWT-based authentication.

---

## 🎯 Problem It Solves 

Managing personal finances is hard without a system. Most people lose track of where their money goes. BudgetTracker solves this by providing:

- **One place** to record all income and expenses
- **Category-based tracking** — know exactly how much you spend on Food, Transport, Entertainment, etc.
- **Account separation** — track Checking, Savings, Cash accounts independently
- **Budget limits** — set monthly spending limits per category
- **Running balance** — see your financial health at a glance
- **Multi-user** — each user's data is isolated and secured with JWT authentication

---

## ✨ Features

| Feature | Description |
|---|---|
| 🔐 **JWT Authentication** | Secure registration & login with Bearer token |
| 👤 **User Management** | Register, view/update profile, delete account |
| 🏦 **Accounts** | Create multiple accounts (Checking, Savings, Cash) |
| 📂 **Categories** | Custom INCOME/EXPENSE categories per user |
| 💸 **Transactions** | Record transactions linked to accounts & categories |
| 📊 **Balance Tracking** | Auto-calculated balance per account (income - expenses) |
| 📅 **Budgets** | Set monthly spending limits per category |
| ✅ **Input Validation** | All inputs validated with clear error messages |
| 🌐 **CORS Support** | Ready for frontend integration |
| 🐳 **Docker** | PostgreSQL & pgAdmin via Docker Compose |

---

## 🛠️ Tech Stack

- **Backend**: Java 21, Spring Boot 3.5.5
- **Security**: Spring Security + JWT (jjwt)
- **Database**: PostgreSQL 15
- **ORM**: Spring Data JPA / Hibernate
- **Build**: Maven
- **Containerization**: Docker Compose
- **Other**: Lombok, Bean Validation, spring-dotenv

---

## 🏗️ Architecture

```
src/main/java/com/BudgetTracker/
├── Controller/          # REST API endpoints
│   ├── AuthController        # Login
│   ├── UserController        # Registration & profile
│   ├── AccountController     # Account CRUD
│   ├── CategoryController    # Category CRUD
│   ├── TransactionController # Transaction CRUD
│   └── BudgetController      # Budget CRUD
├── Service/             # Business logic
├── Repository/          # Data access (JPA)
├── Entity/              # Database models
├── Security/            # JWT filter, config
├── Exception/           # Custom exceptions & global handler
└── dto/                 # Request/Response DTOs with validation
```

---

## 🚀 Getting Started

### Prerequisites

- **Java 21** or higher
- **Docker** & **Docker Compose** (for PostgreSQL)
- **Maven** (or use the included `mvnw` wrapper)

### 1. Clone the repository

```bash
git clone https://github.com/HAIDER6190/Budget_Tracker.git
cd Budget_Tracker
```

### 2. Create your `.env` file

Create a `.env` file in the project root with your configuration:

```env
# Database
DB_NAME=BudgetTrackerdb
DB_USERNAME=admin
DB_PASSWORD=boss

# JWT (change this to your own secret!)
JWT_SECRET=YourSuperSecretKeyForJWT-MustBeAtLeast256BitsLong-ChangeThisInProduction!
JWT_EXPIRATION=3600000

# CORS (your frontend URL)
CORS_ALLOWED_ORIGINS=http://localhost:3000
```

### 3. Start the database

```bash
docker compose up -d
```

This starts:
- **PostgreSQL** on port `5434`
- **pgAdmin** on port `5051` (login: `admin@admin.com` / `root123`)

### 4. Run the application

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

The API will be available at **`http://localhost:9090`**

---

## 📡 API Endpoints

> All endpoints except Register & Login require a JWT token in the `Authorization` header:
> `Authorization: Bearer <your-token>`

### Auth & User

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/api/users/register` | ❌ | Register a new user |
| `POST` | `/api/auth/login` | ❌ | Login and get JWT token |
| `GET` | `/api/users/me` | ✅ | Get your profile |
| `PUT` | `/api/users/me` | ✅ | Update your profile |
| `DELETE` | `/api/users/me` | ✅ | Delete your account |

### Categories

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/categories` | Create a category (INCOME or EXPENSE) |
| `GET` | `/api/categories` | List your categories |
| `PUT` | `/api/categories/{id}` | Update a category |
| `DELETE` | `/api/categories/{id}` | Delete a category |

### Accounts

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/accounts` | Create an account |
| `GET` | `/api/accounts` | List your accounts |
| `PUT` | `/api/accounts/{id}` | Update an account |
| `DELETE` | `/api/accounts/{id}` | Delete an account |
| `GET` | `/api/accounts/{id}/balance` | Get income, expense & balance |

### Transactions

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/transactions` | Create a transaction |
| `PUT` | `/api/transactions/{id}` | Update a transaction |
| `DELETE` | `/api/transactions/{id}` | Delete a transaction |
| `GET` | `/api/transactions/account/{id}` | Transactions by account |
| `GET` | `/api/transactions/user` | All transactions with running balance |

### Budgets

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/budgets` | Set a monthly budget for a category |
| `GET` | `/api/budgets` | List your budgets |
| `PUT` | `/api/budgets/{id}` | Update a budget |
| `DELETE` | `/api/budgets/{id}` | Delete a budget |

---

## 📝 Example Usage

### 1. Register
```json
POST /api/users/register
{
  "username": "john",
  "email": "john@example.com",
  "password": "123456"
}
```

### 2. Login
```json
POST /api/auth/login
{
  "username": "john",
  "password": "123456"
}
// Response: { "username": "john", "token": "eyJhbG..." }
```

### 3. Create a Category
```json
POST /api/categories
Authorization: Bearer <token>
{
  "name": "Food",
  "type": "EXPENSE"
}
```

### 4. Create a Transaction
```json
POST /api/transactions
Authorization: Bearer <token>
{
  "amount": 50.00,
  "description": "Groceries",
  "transactionDate": "2026-02-26",
  "categoryId": 1,
  "accountId": 1
}
```

---

## 🔮 Future Work

- [ ] **Dashboard Analytics** — Monthly/weekly spending charts and summaries
- [ ] **Recurring Transactions** — Auto-create monthly bills (rent, subscriptions)
- [ ] **Budget Alerts** — Notifications when spending exceeds budget limits
- [ ] **Export to CSV/PDF** — Download transaction reports
- [ ] **Role-Based Access** — Admin role for multi-user management
- [ ] **Frontend (React/Angular)** — Build a web UI for the API
- [ ] **Mobile App (Flutter)** — Cross-platform mobile client
- [ ] **Multi-Currency Support** — Track expenses in different currencies
- [ ] **Tags & Notes** — Add tags to transactions for better filtering
- [ ] **Swagger/OpenAPI Docs** — Auto-generated interactive API docs
- [ ] **Unit & Integration Tests** — Full test coverage

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

---

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request
