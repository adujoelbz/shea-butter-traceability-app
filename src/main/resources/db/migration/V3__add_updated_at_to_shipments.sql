-- Add updated_at column (if not exists) using a more compatible approach
ALTER TABLE shipments ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Drop existing constraint if it exists, then add new one (safe approach)
ALTER TABLE shipments DROP CONSTRAINT IF EXISTS uk_shipments_shipment_number;
ALTER TABLE shipments ADD CONSTRAINT uk_shipments_shipment_number UNIQUE (shipment_number);