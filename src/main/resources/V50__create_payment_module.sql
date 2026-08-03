CREATE TABLE subscription_plans (
 id BIGINT NOT NULL AUTO_INCREMENT,
 code VARCHAR(64) NOT NULL, name VARCHAR(120) NOT NULL, description VARCHAR(1000) NOT NULL,
 billing_cycle VARCHAR(32) NOT NULL, price DECIMAL(19,2) NOT NULL, currency VARCHAR(8) NOT NULL,
 duration_days INT NOT NULL, trial_days INT NOT NULL DEFAULT 0, active BIT NOT NULL DEFAULT 1,
 display_order INT NOT NULL DEFAULT 0, features_json JSON NULL, version BIGINT NULL,
 created_at DATETIME(6) NOT NULL, updated_at DATETIME(6) NOT NULL,
 PRIMARY KEY(id), CONSTRAINT uk_subscription_plan_code UNIQUE(code)
) ENGINE=InnoDB;

CREATE TABLE payments (
 id BIGINT NOT NULL AUTO_INCREMENT, payment_reference VARCHAR(64) NOT NULL,
 student_id BIGINT NOT NULL, subscription_plan_id BIGINT NULL, purpose VARCHAR(32) NOT NULL,
 gateway_provider VARCHAR(32) NOT NULL, status VARCHAR(32) NOT NULL, method VARCHAR(32) NULL,
 amount DECIMAL(19,2) NOT NULL, currency VARCHAR(8) NOT NULL, idempotency_key VARCHAR(128) NOT NULL,
 gateway_order_id VARCHAR(128) NULL, gateway_payment_id VARCHAR(128) NULL, gateway_signature VARCHAR(256) NULL,
 gateway_status VARCHAR(64) NULL, failure_code VARCHAR(128) NULL, failure_message VARCHAR(1000) NULL,
 expires_at DATETIME(6) NULL, authorized_at DATETIME(6) NULL, captured_at DATETIME(6) NULL,
 refunded_amount DECIMAL(19,2) NOT NULL DEFAULT 0, version BIGINT NULL,
 created_at DATETIME(6) NOT NULL, updated_at DATETIME(6) NOT NULL,
 PRIMARY KEY(id), CONSTRAINT uk_payment_reference UNIQUE(payment_reference),
 CONSTRAINT uk_payment_idempotency UNIQUE(student_id,idempotency_key),
 CONSTRAINT uk_payment_gateway_order UNIQUE(gateway_provider,gateway_order_id),
 CONSTRAINT fk_payment_student FOREIGN KEY(student_id) REFERENCES students(id),
 CONSTRAINT fk_payment_plan FOREIGN KEY(subscription_plan_id) REFERENCES subscription_plans(id),
 INDEX idx_payment_student_created(student_id,created_at),
 INDEX idx_payment_status_updated(status,updated_at),
 INDEX idx_payment_gateway_payment(gateway_provider,gateway_payment_id)
) ENGINE=InnoDB;

CREATE TABLE student_subscriptions (
 id BIGINT NOT NULL AUTO_INCREMENT, student_id BIGINT NOT NULL, plan_id BIGINT NOT NULL, payment_id BIGINT NULL,
 status VARCHAR(40) NOT NULL, start_at DATETIME(6) NULL, end_at DATETIME(6) NULL,
 cancel_at_period_end BIT NOT NULL DEFAULT 0, cancelled_at DATETIME(6) NULL,
 cancellation_reason VARCHAR(1000) NULL, activated_by VARCHAR(64) NULL, version BIGINT NULL,
 created_at DATETIME(6) NOT NULL, updated_at DATETIME(6) NOT NULL,
 PRIMARY KEY(id), CONSTRAINT uk_subscription_payment UNIQUE(payment_id),
 CONSTRAINT fk_subscription_student FOREIGN KEY(student_id) REFERENCES students(id),
 CONSTRAINT fk_subscription_plan FOREIGN KEY(plan_id) REFERENCES subscription_plans(id),
 CONSTRAINT fk_subscription_payment FOREIGN KEY(payment_id) REFERENCES payments(id),
 INDEX idx_subscription_student_status(student_id,status), INDEX idx_subscription_expiry(status,end_at)
) ENGINE=InnoDB;

CREATE TABLE payment_transactions (
 id BIGINT NOT NULL AUTO_INCREMENT, payment_id BIGINT NOT NULL, type VARCHAR(40) NOT NULL,
 gateway_transaction_id VARCHAR(128) NULL, amount DECIMAL(19,2) NOT NULL, currency VARCHAR(8) NOT NULL,
 status VARCHAR(64) NOT NULL, method VARCHAR(32) NULL, processed_at DATETIME(6) NOT NULL,
 gateway_payload LONGTEXT NULL, created_at DATETIME(6) NOT NULL, updated_at DATETIME(6) NOT NULL,
 PRIMARY KEY(id), CONSTRAINT fk_transaction_payment FOREIGN KEY(payment_id) REFERENCES payments(id),
 INDEX idx_transaction_payment(payment_id,created_at), INDEX idx_transaction_gateway(gateway_transaction_id)
) ENGINE=InnoDB;

CREATE TABLE payment_refunds (
 id BIGINT NOT NULL AUTO_INCREMENT, refund_reference VARCHAR(64) NOT NULL, payment_id BIGINT NOT NULL,
 amount DECIMAL(19,2) NOT NULL, status VARCHAR(32) NOT NULL, reason VARCHAR(1000) NOT NULL,
 gateway_refund_id VARCHAR(128) NULL, gateway_status VARCHAR(64) NULL, requested_by BIGINT NOT NULL,
 reviewed_by BIGINT NULL, review_note VARCHAR(1000) NULL, processed_at DATETIME(6) NULL,
 failure_message VARCHAR(1000) NULL, version BIGINT NULL, created_at DATETIME(6) NOT NULL, updated_at DATETIME(6) NOT NULL,
 PRIMARY KEY(id), CONSTRAINT uk_refund_reference UNIQUE(refund_reference),
 CONSTRAINT fk_refund_payment FOREIGN KEY(payment_id) REFERENCES payments(id),
 INDEX idx_refund_payment(payment_id), INDEX idx_refund_status(status), INDEX idx_refund_gateway(gateway_refund_id)
) ENGINE=InnoDB;

CREATE TABLE payment_webhook_events (
 id BIGINT NOT NULL AUTO_INCREMENT, gateway_provider VARCHAR(32) NOT NULL, gateway_event_id VARCHAR(255) NOT NULL,
 event_type VARCHAR(100) NOT NULL, processing_status VARCHAR(32) NOT NULL, signature VARCHAR(256) NOT NULL,
 raw_payload LONGTEXT NOT NULL, attempt_count INT NOT NULL DEFAULT 0, next_retry_at DATETIME(6) NULL,
 processed_at DATETIME(6) NULL, last_error VARCHAR(2000) NULL, version BIGINT NULL,
 created_at DATETIME(6) NOT NULL, updated_at DATETIME(6) NOT NULL,
 PRIMARY KEY(id), CONSTRAINT uk_webhook_provider_event UNIQUE(gateway_provider,gateway_event_id),
 INDEX idx_webhook_retry(processing_status,next_retry_at)
) ENGINE=InnoDB;

CREATE TABLE payment_status_history (
 id BIGINT NOT NULL AUTO_INCREMENT, payment_id BIGINT NOT NULL, from_status VARCHAR(32) NULL,
 to_status VARCHAR(32) NOT NULL, source VARCHAR(64) NOT NULL, reason VARCHAR(1000) NULL,
 actor_user_id BIGINT NULL, created_at DATETIME(6) NOT NULL, updated_at DATETIME(6) NOT NULL,
 PRIMARY KEY(id), CONSTRAINT fk_history_payment FOREIGN KEY(payment_id) REFERENCES payments(id),
 INDEX idx_payment_history(payment_id,created_at)
) ENGINE=InnoDB;
