-- Create ENUM types first

CREATE TYPE user_notification_type AS ENUM (
  'email',
  'sms',
  'push',
  'in_app'
);

CREATE TYPE user_notification_status AS ENUM (
  'pending',
  'sent',
  'delivered',
  'failed',
  'read'
);

CREATE TYPE payment_status_type AS ENUM (
  'pending',
  'authorized',
  'captured',
  'completed',
  'failed',
  'cancelled',
  'refunded'
);

CREATE TYPE payment_detail_status_type AS ENUM (
  'pending',
  'success',
  'failed',
  'cancelled',
  'held',
  'reversed'
);
