-- 1. USERTYPE table (no dependencies)
CREATE TABLE USERTYPE (
  id   SERIAL PRIMARY KEY,
  type VARCHAR(100) NOT NULL
);

-- 2. USER table (depends on USERTYPE)
CREATE TABLE "USER" (
  id           SERIAL       PRIMARY KEY,
  name         VARCHAR(255) NOT NULL,
  user_type_Id INTEGER      NOT NULL,
  CONSTRAINT fk_user_usertype FOREIGN KEY (user_type_Id) REFERENCES USERTYPE(id)
);

-- 3. ROLE table (no dependencies)
CREATE TABLE ROLE (
  id   SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL UNIQUE
);

-- 4. USERROLE table (junction table for USER and ROLE)
CREATE TABLE USERROLE (
  user_id INTEGER NOT NULL,
  role_id INTEGER NOT NULL,
  PRIMARY KEY (user_id, role_id),
  CONSTRAINT fk_userrole_user FOREIGN KEY (user_id) REFERENCES "USER"(id) ON DELETE CASCADE,
  CONSTRAINT fk_userrole_role FOREIGN KEY (role_id) REFERENCES ROLE(id) ON DELETE CASCADE
);

-- 5. ADDRESS table (no dependencies)
CREATE TABLE ADDRESS (
  id      SERIAL       PRIMARY KEY,
  line1   VARCHAR(255) NOT NULL,
  line2   VARCHAR(255),
  city    VARCHAR(100) NOT NULL,
  state   VARCHAR(100),
  zip     VARCHAR(20),
  country VARCHAR(100) NOT NULL
);

-- 6. CUSTOMER table (depends on USER and ADDRESS)
CREATE TABLE CUSTOMER (
  customer_id SERIAL       PRIMARY KEY,
  user_id     INTEGER      NOT NULL UNIQUE,
  email       VARCHAR(255) NOT NULL UNIQUE,
  first_name  VARCHAR(100) NOT NULL,
  last_name   VARCHAR(100) NOT NULL,
  phone       VARCHAR(20),
  address_id  INTEGER,
  created_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
  updated_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_customer_user FOREIGN KEY (user_id) REFERENCES "USER"(id) ON DELETE CASCADE,
  CONSTRAINT fk_customer_address FOREIGN KEY (address_id) REFERENCES ADDRESS(id)
);

-- 7. NOTIFICATION table (depends on CUSTOMER)
CREATE TABLE NOTIFICATION (
  id                  SERIAL                  PRIMARY KEY,
  customerid          INTEGER                 NOT NULL,
  notifiactiontype    user_notification_type  NOT NULL,
  notificationstatus  user_notification_status NOT NULL,
  CONSTRAINT fk_notification_customer FOREIGN KEY (customerid) REFERENCES CUSTOMER(customer_id) ON DELETE CASCADE
);

-- 8. PAYMENT_METHOD_TYPE table (no dependencies)
CREATE TABLE PAYMENT_METHOD_TYPE (
  method_type_id SERIAL       PRIMARY KEY,
  type_name      VARCHAR(100) NOT NULL UNIQUE,
  description    TEXT,
  is_active      BOOLEAN      DEFAULT TRUE
);

-- 9. PAYMENT_METHOD table (depends on CUSTOMER and PAYMENT_METHOD_TYPE)
CREATE TABLE PAYMENT_METHOD (
  payment_method_id SERIAL    PRIMARY KEY,
  customer_id       INTEGER   NOT NULL,
  method_type_id    INTEGER   NOT NULL,
  is_default        BOOLEAN   DEFAULT FALSE,
  created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_payment_method_customer FOREIGN KEY (customer_id) REFERENCES CUSTOMER(customer_id) ON DELETE CASCADE,
  CONSTRAINT fk_payment_method_type FOREIGN KEY (method_type_id) REFERENCES PAYMENT_METHOD_TYPE(method_type_id)
);

-- 10. PAYMENT_METHOD_CONFIGURATION table (depends on PAYMENT_METHOD)
CREATE TABLE PAYMENT_METHOD_CONFIGURATION (
  payment_method_configuration_id SERIAL PRIMARY KEY,
  payment_method_id               INTEGER NOT NULL UNIQUE,
  configurations                  JSONB   NOT NULL,
  CONSTRAINT fk_payment_config_method FOREIGN KEY (payment_method_id) REFERENCES PAYMENT_METHOD(payment_method_id) ON DELETE CASCADE
);

-- 11. PAYMENT table (depends on PAYMENT_METHOD)
CREATE TABLE PAYMENT (
  payment_id         SERIAL               PRIMARY KEY,
  payment_method_id  INTEGER              NOT NULL,
  status             payment_status_type  NOT NULL DEFAULT 'pending',
  amount             DECIMAL(10, 2)      NOT NULL,
  currency           VARCHAR(3)          NOT NULL DEFAULT 'USD',
  payment_gateway_id VARCHAR(255), -- ex Paypal orderId
  payment_date       TIMESTAMP,
  authorized_at      TIMESTAMP,
  captured_at        TIMESTAMP,
  created_at         TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
  updated_at         TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_payment_method FOREIGN KEY (payment_method_id) REFERENCES PAYMENT_METHOD(payment_method_id)
);

-- 12. PAYMENT_DETAIL table (depends on PAYMENT)
CREATE TABLE PAYMENT_DETAIL (
  payment_transaction_id SERIAL                  PRIMARY KEY,
  payment_id             INTEGER                 NOT NULL,
  status                 payment_detail_status_type NOT NULL,
  transaction_type       VARCHAR(50)             NOT NULL, -- authorize, capture, void, refund
  amount                 DECIMAL(10, 2)          NOT NULL,
  currency               VARCHAR(3)              NOT NULL,
  gateway_transaction_id VARCHAR(255),
  gateway_response       JSONB,
  transaction_date       TIMESTAMP               NOT NULL,
  created_at             TIMESTAMP               DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_payment_detail_payment FOREIGN KEY (payment_id) REFERENCES PAYMENT(payment_id) ON DELETE CASCADE
);

-- 13. PAYMENT_ORDER table (depends on PAYMENT)
CREATE TABLE PAYMENT_ORDER (
  payment_order_id     SERIAL  PRIMARY KEY,
  payment_id           INTEGER NOT NULL UNIQUE,
  payment_order_details JSONB  NOT NULL,
  CONSTRAINT fk_payment_order_payment FOREIGN KEY (payment_id) REFERENCES PAYMENT(payment_id) ON DELETE CASCADE
);

-- 14. FLIGHTBOOKING table (depends on PAYMENT and CUSTOMER)
CREATE TABLE FLIGHTBOOKING (
  id             SERIAL       PRIMARY KEY,
  payment_id     INTEGER      NOT NULL UNIQUE,
  customerid     INTEGER      NOT NULL,
  bookingreference VARCHAR(100) NOT NULL UNIQUE,
  amadeuspnr     VARCHAR(100),
  status         VARCHAR(50)  NOT NULL,
  bookingdate    TIMESTAMP    NOT NULL,
  totalamount    DECIMAL(10, 2) NOT NULL,
  currency       VARCHAR(3)   NOT NULL,
  responsedata   JSONB,
  origin         VARCHAR(10)  NOT NULL,
  destination    VARCHAR(10)  NOT NULL,
  departuretime  TIMESTAMP    NOT NULL,
  arrivaltime    TIMESTAMP    NOT NULL,
  flightnumber   VARCHAR(20)  NOT NULL,
  carriercode    VARCHAR(10)  NOT NULL,
  cabinclass     VARCHAR(50),
  bookingclass   VARCHAR(10),
  ticketNumber   VARCHAR(100),
  CONSTRAINT fk_flightbooking_payment FOREIGN KEY (payment_id) REFERENCES PAYMENT(payment_id),
  CONSTRAINT fk_flightbooking_customer FOREIGN KEY (customerid) REFERENCES CUSTOMER(customer_id)
);