# Monk Commerce – Coupons Management API

## GitHub Repository
[https://github.com/utkarshgupta321/Monk-Commerce-Coupons-API](https://github.com/utkarshgupta321/Monk-Commerce-Coupons-API)

---

## Overview
This project is a **RESTful Coupons Management API** for an e-commerce platform.  
It enables the creation, retrieval, updating, deletion, and application of discount coupons on shopping carts.  

The API currently supports:
- **Cart-wise coupons** – discount on entire cart  
- **Product-wise coupons** – discount on specific products  
- **BxGy coupons** – Buy X Get Y free, with repetition limits  
- Coupon **expiration date handling**  
- **Error handling & validation**  
- **Extensible architecture** → easily add new coupon types in the future  

Built with **Spring Boot 3.1**, **Spring Data JPA**, and **H2 in-memory database**.  

---

## Features Implemented
### 1. Cart-wise Coupons
- Apply discount on entire cart if threshold is met.  
- Example: *10% off if cart total > 100*.  

### 2. Product-wise Coupons
- Apply discount on selected products.  
- Example: *20% off on Product A*.  

### 3. BxGy Coupons
- “Buy X Get Y Free” with repetition limits.  
- Example: *Buy 3 of Product X, Get 1 of Product Y free (up to 2 times)*.  

### 4. Expiration Date
- Each coupon has an `expiryDate`.  
- Expired coupons cannot be applied.  

### 5. Error Handling
- Coupon not found → `404 Not Found`  
- Expired/inactive coupon → `400 Bad Request`  
- Invalid cart input → validation errors returned in structured JSON  
- Global Exception Handler in place  

### 6. Health Check
- `GET /api` → returns `"Welcome to the Monk Coupon API"`.  

---

## Unimplemented (but considered for future)
- **Stacking multiple coupons** (only best coupon is applied now).  
- **Category-wise discounts** (requires product categories).  
- **User-specific coupons** (requires authentication).  
- **Global coupon usage limits** (e.g., max 1000 uses).  
- **Flat discounts in BxGy** (currently only free items supported).  
- **Shipping/loyalty integration**.  

---

## Limitations
- Only one coupon applied at a time (best discount).  
- No authentication/authorization (open endpoints).  
- Products represented only by `productId` (no product catalog).  
- Uses **H2 in-memory DB** (resets on restart).  
- Carts are **not persisted** (request-scoped JSON only).  

---

## Assumptions
- Prices are in INR (₹).  
- Cart items follow `{ productId, quantity, price }`.  
- Cart total = Σ (price × quantity).  
- Coupon considered invalid if:
  - `active=false` OR  
  - `expiryDate < today`.  
- One coupon per cart.  

---

## Tech Stack
- **Java 21**  
- **Spring Boot 3.1.4**  
- **Spring Data JPA**  
- **H2 Database** (in-memory)   
- **Maven**  

---

## Running the Application
1. Clone repo:  
   ```bash
   git clone https://github.com/utkarshgupta321/Monk-Commerce-Coupons-API.git
   cd monk-coupons-api
   ```

2. Run with Maven:  
   ```bash
   mvn spring-boot:run
   ```

3. App runs at:  
   ```
   http://localhost:8080
   ```

4. Welcome Page:  
   ```
   http://localhost:8080/api
   ```

---

## API Endpoints

### Coupons CRUD
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/coupons` | Create new coupon |
| `GET`  | `/api/coupons` | Get all coupons |
| `GET`  | `/api/coupons/{id}` | Get coupon by ID |
| `PUT`  | `/api/coupons/{id}` | Update coupon |
| `DELETE` | `/api/coupons/{id}` | Delete coupon |

### Coupon Application
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/applicable-coupons` | Get all applicable coupons for a cart |
| `POST` | `/api/apply-coupon/{id}` | Apply specific coupon to a cart |
| `POST` | `/api/apply-best-coupon` | Apply the best coupon to a cart |

---

## Example Payloads

### 1. Create Cart-wise Coupon
```json
{
  "type": "CART_WISE",
  "detailsJson": "{\"threshold\":100, \"discount\":10}",
  "active": true,
  "expiryDate": "2099-12-31",
  "code": "CART10"
}
```

### 2. Applicable Coupons
Request:
```json
{
  "items": [
    { "productId": 1, "quantity": 6, "price": 50 },
    { "productId": 2, "quantity": 3, "price": 30 },
    { "productId": 3, "quantity": 2, "price": 25 }
  ]
}
```
Response:
```json
[
  { "couponId": 1, "type": "CART_WISE", "discount": 40.0 },
  { "couponId": 3, "type": "BXGY", "discount": 50.0 }
]
```

### 3. Apply Coupon
Request:
```json
{
  "items": [
    { "productId": 1, "quantity": 6, "price": 50 },
    { "productId": 2, "quantity": 3, "price": 30 },
    { "productId": 3, "quantity": 2, "price": 25 }
  ]
}
```
Response:
```json
{
  "items": [
    { "productId": 1, "quantity": 6, "price": 50 },
    { "productId": 2, "quantity": 3, "price": 30 },
    { "productId": 3, "quantity": 2, "price": 25 }
  ],
  "totalPrice": 400.0,
  "totalDiscount": 50.0,
  "finalPrice": 350.0
}
```

---

## Documentation
Swagger UI is available at:  
👉 [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## Testing with Postman
A ready-made Postman collection is included:  
`Monk-Coupons-API.postman_collection.json`  

Import into Postman → test all endpoints quickly.  

---

## Author
Developed by **Utkarsh Gupta**.  
