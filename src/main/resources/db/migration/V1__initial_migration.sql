-- =====================================================
-- Shea Butter Traceability System - Ultra Simple Schema
-- PostgreSQL (VARCHAR only, no ENUMs, no functions)
-- =====================================================

-- =====================================================
-- Core Tables
-- =====================================================

-- 1. USERS TABLE
CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       username VARCHAR(50) UNIQUE NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       first_name VARCHAR(100) NOT NULL,
                       last_name VARCHAR(100) NOT NULL,
                       phone_number VARCHAR(20),
                       role VARCHAR(50) DEFAULT 'VIEWER',
                       is_active BOOLEAN DEFAULT true,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. COOPERATIVES TABLE
CREATE TABLE cooperatives (
                              id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              name VARCHAR(255) NOT NULL,
                              registration_number VARCHAR(100) UNIQUE,
                              region VARCHAR(100) NOT NULL,
                              district VARCHAR(100) NOT NULL,
                              phone_number VARCHAR(20),
                              email VARCHAR(255),
                              contact_person VARCHAR(255),
                              is_active BOOLEAN DEFAULT true,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. COLLECTORS TABLE
CREATE TABLE collectors (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            first_name VARCHAR(100) NOT NULL,
                            last_name VARCHAR(100) NOT NULL,
                            national_id VARCHAR(50) UNIQUE,
                            date_of_birth DATE,
                            phone_number VARCHAR(20) NOT NULL,
                            village VARCHAR(255) NOT NULL,
                            district VARCHAR(100) NOT NULL,
                            region VARCHAR(100) NOT NULL,
                            gps_coordinates VARCHAR(100),
                            years_of_experience INTEGER,
                            bank_name VARCHAR(100),
                            bank_account_number VARCHAR(50),
                            mobile_money_number VARCHAR(20),
                            emergency_contact_name VARCHAR(255),
                            emergency_contact_phone VARCHAR(20),
                            status VARCHAR(50) DEFAULT 'ACTIVE',
                            photo_path VARCHAR(500),
                            notes TEXT,
                            cooperative_id UUID REFERENCES cooperatives(id),
                            registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            last_collection_date TIMESTAMP
);

-- 4. BATCHES TABLE (Main table)
CREATE TABLE batches (
                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         batch_number VARCHAR(50) UNIQUE NOT NULL,
                         collector_id UUID NOT NULL REFERENCES collectors(id),
                         cooperative_id UUID REFERENCES cooperatives(id),

    -- Collection Details
                         collection_date TIMESTAMP NOT NULL,
                         collection_zone VARCHAR(255) NOT NULL,
                         gps_coordinates VARCHAR(100),
                         quantity_kg DECIMAL(10,2) NOT NULL,

    -- Quality Details
                         quality_grade VARCHAR(50) DEFAULT 'GRADE_B',
                         moisture_content DECIMAL(5,2),
                         impurity_percentage DECIMAL(5,2),
                         quality_notes TEXT,

    -- Processing Details
                         processing_date TIMESTAMP,
                         processed_quantity_kg DECIMAL(10,2),

    -- Pricing
                         base_price_per_kg DECIMAL(10,2),
                         quality_premium DECIMAL(10,2) DEFAULT 0,
                         total_price DECIMAL(12,2),
                         currency VARCHAR(3) DEFAULT 'GHS',

    -- Payment
                         is_paid BOOLEAN DEFAULT FALSE,
                         payment_date TIMESTAMP,

    -- QR Code
                         qr_code_hash VARCHAR(255) UNIQUE,
                         qr_code_path VARCHAR(500),

    -- Status
                         status VARCHAR(50) DEFAULT 'COLLECTED',
                         notes TEXT,

                         created_by UUID REFERENCES users(id),
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Simple constraints
                         CONSTRAINT positive_quantity CHECK (quantity_kg > 0)
);

-- 5. BATCH PHOTOS TABLE
CREATE TABLE batch_photos (
                              id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              batch_id UUID NOT NULL REFERENCES batches(id) ON DELETE CASCADE,
                              file_name VARCHAR(255) NOT NULL,
                              file_path VARCHAR(500) NOT NULL,
                              thumbnail_path VARCHAR(500),
                              is_primary BOOLEAN DEFAULT FALSE,
                              uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              uploaded_by UUID REFERENCES users(id)
);

-- 6. TRACEABILITY EVENTS TABLE
CREATE TABLE traceability_events (
                                     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                     batch_id UUID NOT NULL REFERENCES batches(id) ON DELETE CASCADE,
                                     event_type VARCHAR(50) NOT NULL,
                                     event_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     location VARCHAR(255),
                                     description TEXT,
                                     performed_by UUID REFERENCES users(id),
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 7. QUALITY CHECKS TABLE
CREATE TABLE quality_checks (
                                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                batch_id UUID NOT NULL REFERENCES batches(id) ON DELETE CASCADE,
                                inspector_id UUID REFERENCES users(id),
                                moisture_content DECIMAL(5,2),
                                impurity_percentage DECIMAL(5,2),
                                quality_grade VARCHAR(50) NOT NULL,
                                is_passed BOOLEAN NOT NULL,
                                notes TEXT,
                                check_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 8. PAYMENTS TABLE
CREATE TABLE payments (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          batch_id UUID REFERENCES batches(id),
                          collector_id UUID NOT NULL REFERENCES collectors(id),
                          amount DECIMAL(12,2) NOT NULL,
                          currency VARCHAR(3) DEFAULT 'GHS',
                          payment_method VARCHAR(50) DEFAULT 'CASH',
                          payment_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          transaction_reference VARCHAR(100) UNIQUE,
                          mobile_money_number VARCHAR(20),
                          notes TEXT,
                          processed_by UUID REFERENCES users(id),
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          CONSTRAINT positive_amount CHECK (amount > 0)
);

-- 9. SHIPMENTS TABLE (Optional - for export tracking)
CREATE TABLE shipments (
                           id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           shipment_number VARCHAR(50) UNIQUE NOT NULL,
                           buyer_name VARCHAR(255) NOT NULL,
                           destination_country VARCHAR(100) NOT NULL,
                           shipment_date DATE NOT NULL,
                           container_number VARCHAR(50),
                           bill_of_lading VARCHAR(100),
                           status VARCHAR(50) DEFAULT 'PENDING',
                           notes TEXT,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 10. BATCH_SHIPMENTS (Junction table)
CREATE TABLE batch_shipments (
                                 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                 batch_id UUID NOT NULL REFERENCES batches(id) ON DELETE CASCADE,
                                 shipment_id UUID NOT NULL REFERENCES shipments(id) ON DELETE CASCADE,
                                 quantity_kg DECIMAL(10,2) NOT NULL,
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 UNIQUE(batch_id, shipment_id)
);

-- =====================================================
-- Basic Indexes for Performance
-- =====================================================

-- Batches indexes
CREATE INDEX idx_batches_batch_number ON batches(batch_number);
CREATE INDEX idx_batches_collector_id ON batches(collector_id);
CREATE INDEX idx_batches_status ON batches(status);
CREATE INDEX idx_batches_collection_date ON batches(collection_date DESC);
CREATE INDEX idx_batches_qr_code ON batches(qr_code_hash);

-- Collectors indexes
CREATE INDEX idx_collectors_name ON collectors(last_name, first_name);
CREATE INDEX idx_collectors_phone ON collectors(phone_number);
CREATE INDEX idx_collectors_village ON collectors(village);
CREATE INDEX idx_collectors_district ON collectors(district);
CREATE INDEX idx_collectors_region ON collectors(region);
CREATE INDEX idx_collectors_status ON collectors(status);
CREATE INDEX idx_collectors_cooperative ON collectors(cooperative_id);

-- Events indexes
CREATE INDEX idx_events_batch_id ON traceability_events(batch_id);
CREATE INDEX idx_events_type ON traceability_events(event_type);
CREATE INDEX idx_events_date ON traceability_events(event_date DESC);

-- Photos indexes
CREATE INDEX idx_photos_batch_id ON batch_photos(batch_id);
CREATE INDEX idx_photos_primary ON batch_photos(batch_id) WHERE is_primary = TRUE;

-- Payments indexes
CREATE INDEX idx_payments_collector_id ON payments(collector_id);
CREATE INDEX idx_payments_batch_id ON payments(batch_id);
CREATE INDEX idx_payments_date ON payments(payment_date DESC);

-- Shipments indexes
CREATE INDEX idx_shipments_number ON shipments(shipment_number);
CREATE INDEX idx_shipments_date ON shipments(shipment_date DESC);
CREATE INDEX idx_shipments_status ON shipments(status);