# FinTech Wallet API Documentation

## Overview

This document describes the REST API endpoints for the FinTech Wallet application, including the newly implemented medium-priority features:

- 💱 Currency Exchange
- 📊 Analytics & Reporting
- 🔔 Notifications (Event-Driven)
- ⏸️ Scheduled Payments

---

## Base URL

```
http://localhost:8088/api/v1
```

---

## 💱 Currency Exchange API

### Get Exchange Rates

Retrieve current exchange rates from a base currency.

**Endpoint:** `GET /exchange/rates`

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| baseCurrency | String | No | Base currency code (default: USD) |

**Response:**
```json
{
  "baseCurrency": "USD",
  "rates": {
    "EUR": 0.85,
    "GBP": 0.73,
    "JPY": 110.25
  },
  "timestamp": "2024-01-29T12:00:00Z"
}
```

### Cross-Currency Transfer

Transfer money between wallets with different currencies.

**Endpoint:** `POST /exchange/transfer`

**Request Body:**
```json
{
  "sourceWalletId": "uuid",
  "destinationWalletId": "uuid",
  "amount": 100.00,
  "sourceCurrency": "USD",
  "targetCurrency": "EUR",
  "description": "Cross-currency transfer"
}
```

**Response:**
```json
{
  "transactionId": "uuid",
  "sourceAmount": 100.00,
  "sourceCurrency": "USD",
  "convertedAmount": 85.00,
  "targetCurrency": "EUR",
  "exchangeRate": 0.85,
  "timestamp": "2024-01-29T12:00:00Z"
}
```

---

## 📊 Analytics & Reporting API

### Get Account Statement

Retrieve account statement for a wallet within a date range.

**Endpoint:** `GET /reports/wallets/{walletId}/statement`

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| walletId | UUID | The wallet ID |

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| startDate | LocalDate | Yes | Start date (YYYY-MM-DD) |
| endDate | LocalDate | Yes | End date (YYYY-MM-DD) |

**Response:**
```json
{
  "walletId": "uuid",
  "startDate": "2024-01-01",
  "endDate": "2024-01-31",
  "openingBalance": 1000.00,
  "closingBalance": 1500.00,
  "currency": "USD",
  "transactions": [
    {
      "date": "2024-01-15",
      "type": "CREDIT",
      "amount": 500.00,
      "description": "Deposit",
      "balance": 1500.00
    }
  ]
}
```

### Get Monthly Summary

Retrieve monthly spending summary for a wallet.

**Endpoint:** `GET /reports/wallets/{walletId}/monthly-summary`

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| walletId | UUID | The wallet ID |

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| year | Integer | Yes | Year (e.g., 2024) |
| month | Integer | Yes | Month (1-12) |

**Response:**
```json
{
  "walletId": "uuid",
  "year": 2024,
  "month": 1,
  "totalIncome": 2000.00,
  "totalExpenses": 500.00,
  "netChange": 1500.00,
  "currency": "USD",
  "transactionCount": 15
}
```

### Export Statement

Export account statement in PDF or CSV format.

**Endpoint:** `GET /reports/wallets/{walletId}/export`

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| walletId | UUID | The wallet ID |

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| startDate | LocalDate | Yes | Start date (YYYY-MM-DD) |
| endDate | LocalDate | Yes | End date (YYYY-MM-DD) |
| format | String | Yes | Export format: `pdf` or `csv` |

**Response:** Binary file download with appropriate Content-Type header.

---

## ⏸️ Scheduled Payments API

### Create Scheduled Payment

Create a one-time or recurring scheduled payment.

**Endpoint:** `POST /scheduled-payments`

**Request Body:**
```json
{
  "sourceWalletId": "uuid",
  "destinationWalletId": "uuid",
  "amount": 100.00,
  "currency": "USD",
  "description": "Monthly rent",
  "recurrencePattern": "MONTHLY",
  "startDate": "2024-02-01",
  "endDate": "2024-12-31",
  "maxExecutions": 12
}
```

**Recurrence Pattern Values:**
- `ONCE` - One-time payment
- `DAILY` - Every day
- `WEEKLY` - Every week
- `BIWEEKLY` - Every two weeks
- `MONTHLY` - Every month
- `QUARTERLY` - Every three months
- `YEARLY` - Every year

**Response:**
```json
{
  "id": "uuid",
  "sourceWalletId": "uuid",
  "destinationWalletId": "uuid",
  "amount": 100.00,
  "currency": "USD",
  "description": "Monthly rent",
  "recurrencePattern": "MONTHLY",
  "startDate": "2024-02-01",
  "endDate": "2024-12-31",
  "nextExecutionDate": "2024-02-01",
  "executionCount": 0,
  "maxExecutions": 12,
  "status": "ACTIVE",
  "createdAt": "2024-01-29T12:00:00Z"
}
```

### Get Scheduled Payment

Retrieve a scheduled payment by ID.

**Endpoint:** `GET /scheduled-payments/{id}`

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| id | UUID | The scheduled payment ID |

**Response:** Same as create response.

### Get Scheduled Payments by Wallet

Retrieve all scheduled payments for a wallet.

**Endpoint:** `GET /scheduled-payments/wallet/{walletId}`

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| walletId | UUID | The source wallet ID |

**Response:** Array of scheduled payment objects.

### Cancel Scheduled Payment

Cancel an active scheduled payment.

**Endpoint:** `POST /scheduled-payments/{id}/cancel`

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| id | UUID | The scheduled payment ID |

**Response:** Updated scheduled payment object with status `CANCELLED`.

### Pause Scheduled Payment

Pause an active scheduled payment.

**Endpoint:** `POST /scheduled-payments/{id}/pause`

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| id | UUID | The scheduled payment ID |

**Response:** Updated scheduled payment object with status `PAUSED`.

### Resume Scheduled Payment

Resume a paused scheduled payment.

**Endpoint:** `POST /scheduled-payments/{id}/resume`

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| id | UUID | The scheduled payment ID |

**Response:** Updated scheduled payment object with status `ACTIVE`.

---

## 🔔 Notifications System

The notification system is event-driven and operates internally. The following domain events trigger notifications:

### Domain Events

| Event | Description | Email Notification | Webhook |
|-------|-------------|-------------------|---------|
| `WalletCreatedEvent` | Wallet was created | ✅ | ✅ |
| `MoneyDepositedEvent` | Money was deposited | ✅ | ✅ |
| `MoneyWithdrawnEvent` | Money was withdrawn | ✅ | ✅ |
| `MoneyTransferredEvent` | Money was transferred | ✅ | ✅ |

### Webhook Integration

To receive webhook notifications, configure your webhook endpoint in the application configuration:

```yaml
app:
  webhook:
    endpoints:
      - url: https://your-service.com/webhook
        events:
          - WALLET_CREATED
          - MONEY_DEPOSITED
          - MONEY_WITHDRAWN
          - MONEY_TRANSFERRED
```

### Webhook Payload Example

```json
{
  "eventType": "MONEY_DEPOSITED",
  "timestamp": "2024-01-29T12:00:00Z",
  "payload": {
    "walletId": "uuid",
    "amount": 100.00,
    "currency": "USD",
    "newBalance": 1100.00
  }
}
```

---

## Error Responses

All endpoints return standard error responses:

```json
{
  "timestamp": "2024-01-29T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Detailed error message",
  "path": "/api/v1/endpoint"
}
```

### Common HTTP Status Codes

| Status | Description |
|--------|-------------|
| 200 | Success |
| 201 | Created |
| 400 | Bad Request - Invalid input |
| 404 | Not Found - Resource doesn't exist |
| 409 | Conflict - Business rule violation |
| 500 | Internal Server Error |

---

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `EXCHANGE_RATES_APP_ID` | Open Exchange Rates API key | - |
| `MAIL_HOST` | SMTP server host | smtp.gmail.com |
| `MAIL_PORT` | SMTP server port | 587 |
| `MAIL_USERNAME` | SMTP username | - |
| `MAIL_PASSWORD` | SMTP password | - |

### Scheduled Jobs

| Job | Description | Default Schedule |
|-----|-------------|------------------|
| Payment Execution | Executes due payments | Daily at 6 AM |
| Payment Reminders | Sends reminders for upcoming payments | Daily at 9 AM |
