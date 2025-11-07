-- Add indexes for better performance
CREATE INDEX idx_customer_email ON CUSTOMER(email);
CREATE INDEX idx_customer_user_id ON CUSTOMER(user_id);
CREATE INDEX idx_payment_status ON PAYMENT(status);
CREATE INDEX idx_payment_date ON PAYMENT(payment_date);
CREATE INDEX idx_flightbooking_reference ON FLIGHTBOOKING(bookingreference);
CREATE INDEX idx_flightbooking_customer ON FLIGHTBOOKING(customerid);
CREATE INDEX idx_flightbooking_departure ON FLIGHTBOOKING(departuretime);
CREATE INDEX idx_notification_customer ON NOTIFICATION(customerid);
CREATE INDEX idx_payment_detail_payment ON PAYMENT_DETAIL(payment_id);
