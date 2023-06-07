# Design of the budget

## Errors

```json
{
  "error": {
    "id": "123",
    "name": "error_name",
    "detail": "Error detail"
  }
}
```

## List of budgets

```json
HTTP/1.1 200 OK
Content-Type: application/json; charset=utf-8

{"data": {
    "budgets": [
      {
        "id":"6ee704d9-ee24-4c36-b1a6-cb8ccf6a216c",
        "name":"My Budget",
        "last_modified_on":"2017-12-01T12:40:37.867Z",
        "first_month":"2017-11-01",
        "last_month":"2017-11-01"
      }
    ]
  }
}
```

### Transactions

- By YNAB

```json
{
  "data": {
    "transaction": {
      "id": "16da87a0-66c7-442f-8216-a3daf9cb82a8",
      "date": "2017-12-01",
      "payee_id": null,  // This transaction does not have a payee
      ...
    }
  }
}
```

- By the app
  - Category
  - Amount
  - Date
