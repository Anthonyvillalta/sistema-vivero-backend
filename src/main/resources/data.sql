-- Datos de Semilla (Seed Data) para Vivero

-- Roles
INSERT INTO roles (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT INTO roles (id, name) VALUES (2, 'ROLE_VENDEDOR');
INSERT INTO roles (id, name) VALUES (3, 'ROLE_REPARTIDOR');

-- Usuarios (Password en texto claro para desarrollo oBCrypt: 'admin123', 'vendedor123', 'repartidor123')
-- $2a$10$eD2vI.H0m6fA.Ea00ZzGce3/K1WJ4L0k2xQ54s1Z9vG4v9S1Yj1E6 (admin123)
INSERT INTO users (id, username, password, full_name, email, phone, role_id, active, created_by)
VALUES 
(1, 'admin', '$2a$10$eD2vI.H0m6fA.Ea00ZzGce3/K1WJ4L0k2xQ54s1Z9vG4v9S1Yj1E6', 'Administrador General', 'admin@viverovillaverde.pe', '+51 987654321', 1, true, 'system'),
(2, 'vendedor', '$2a$10$eD2vI.H0m6fA.Ea00ZzGce3/K1WJ4L0k2xQ54s1Z9vG4v9S1Yj1E6', 'María Ventas', 'ventas@viverovillaverde.pe', '+51 987654322', 2, true, 'system'),
(3, 'repartidor', '$2a$10$eD2vI.H0m6fA.Ea00ZzGce3/K1WJ4L0k2xQ54s1Z9vG4v9S1Yj1E6', 'Carlos Delivery', 'delivery@viverovillaverde.pe', '+51 987654323', 3, true, 'system');

-- Categorías
INSERT INTO categories (id, name, description, icon_name, created_by) VALUES
(1, 'Grass Natural', 'Mantas de grass en rollos por metro cuadrado (m²)', 'Sprout', 'system'),
(2, 'Plantas Ornamentales', 'Plantas de interior y exterior por unidades', 'Flower2', 'system'),
(3, 'Árboles y Palmeras', 'Árboles para jardín y palmeras decorativas', 'Trees', 'system'),
(4, 'Accesorios e Insumos', 'Tierra preparada, macetas y abonos', 'Package', 'system');

-- Productos
INSERT INTO products (id, code, name, variety, brand, description, category_id, unit_type, price, cost_price, stock, reserved_stock, min_stock, image_url, created_by) VALUES
(1, 'PROD-001', 'Grass americano', 'Americano Premium', NULL, 'Grass natural de alta calidad, ideal para jardines, parques y áreas recreativas. Mantiene su verdor durante todo el año.', 1, 'M2', 12.00, 6.50, 1000.00, 150.00, 100.00, 'https://images.unsplash.com/photo-1558904541-efa843a96f01?auto=format&fit=crop&w=600&q=80', 'system'),
(2, 'PROD-002', 'Grass bermuda', 'Bermuda Fina', NULL, 'Resistente al alto tráfico y a climas cálidos. Ideal para canchas deportivas y espacios concurridos.', 1, 'M2', 15.00, 8.00, 620.00, 0.00, 80.00, 'https://images.unsplash.com/photo-1584467735871-8e85353a8413?auto=format&fit=crop&w=600&q=80', 'system'),
(3, 'PROD-003', 'Grass japonés', 'Japonés Zoysia', NULL, 'De hoja muy fina y suave. Excelente para jardines residenciales de alto acabado estético.', 1, 'M2', 18.00, 9.50, 40.00, 0.00, 50.00, 'https://images.unsplash.com/photo-1592417817098-8f3d6ef23a23?auto=format&fit=crop&w=600&q=80', 'system'),
(4, 'PROD-004', 'Palmera Areca', 'Areca Lutescens', NULL, 'Palmera de interior/exterior ideal para purificar el aire y dar un toque tropical moderno.', 2, 'UNIDAD', 80.00, 40.00, 35.00, 0.00, 10.00, 'https://images.unsplash.com/photo-1614594975525-e45190c55d0b?auto=format&fit=crop&w=600&q=80', 'system'),
(5, 'PROD-005', 'Ficus Lyrata', 'Higuera de Hoja Violín', NULL, 'Planta ornamental elegante con hojas grandes en forma de violín, muy popular en decoración.', 2, 'UNIDAD', 120.00, 65.00, 18.00, 0.00, 5.00, 'https://images.unsplash.com/photo-1545241047-6083a3684587?auto=format&fit=crop&w=600&q=80', 'system'),
(6, 'PROD-006', 'Cactus San Pedro', 'Echinopsis pachanoi', NULL, 'Cactus ornamental emblemático, de bajo mantenimiento y alta resistencia a la sequía.', 2, 'UNIDAD', 45.00, 20.00, 28.00, 0.00, 5.00, 'https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?auto=format&fit=crop&w=600&q=80', 'system'),
(7, 'PROD-007', 'Tierra Preparada Multiuso 50kg', 'Sustinto Orgánico', 'Fertiplant', 'Tierra enriquecida con humus y compost orgánico para todo tipo de plantas y grass.', 4, 'UNIDAD', 25.00, 12.00, 150.00, 0.00, 20.00, 'https://images.unsplash.com/photo-1416879595882-3373a0480b5b?auto=format&fit=crop&w=600&q=80', 'system');

-- Clientes
INSERT INTO customers (id, full_name, document_type, document_number, phone, whatsapp, email, address, is_frequent, total_purchases, created_by) VALUES
(1, 'Juan Pérez', 'DNI', '45892134', '+51 981234567', '+51981234567', 'juan.perez@email.com', 'Av. Los Jardines 123, San Isidro', true, 2450.00, 'system'),
(2, 'María López', 'DNI', '71239845', '+51 987654321', '+51987654321', 'maria.lopez@email.com', 'Calle Las Flores 456, Miraflores', true, 1800.00, 'system'),
(3, 'Carlos Ruiz', 'DNI', '10458923', '+51 974125896', '+51974125896', 'carlos.ruiz@email.com', 'Jr. El Bosque 789, Surco', false, 850.00, 'system'),
(4, 'Ana Torres', 'DNI', '42981567', '+51 963258741', '+51963258741', 'ana.torres@email.com', 'Av. Primavera 1020, San Borja', false, 320.00, 'system');

-- Proveedores
INSERT INTO suppliers (id, company_name, contact_name, document_number, phone, email, address, created_by) VALUES
(1, 'Agro Grass del Perú S.A.C.', 'Ing. Roberto Gómez', '20512345678', '+51 955112233', 'ventas@agrograssperu.com', 'Panamericana Sur Km 35, Lurín', 'system'),
(2, 'Vivero Central Cieneguilla', 'Sra. Carmen Morales', '20601234567', '+51 944332211', 'contacto@viverocieneguilla.com', 'Av. Toledo 520, Cieneguilla', 'system');

-- Compras
INSERT INTO purchases (id, purchase_number, supplier_id, supplier_name, purchase_date, total_amount, status, notes, created_by) VALUES
(1, 'COM-2026-001', 1, 'Agro Grass del Perú S.A.C.', CURRENT_TIMESTAMP, 3250.00, 'COMPLETADO', 'Compra de 500m² Grass Americano', 'admin');

INSERT INTO purchase_items (id, purchase_id, product_id, product_name, quantity, unit_cost, total_cost) VALUES
(1, 1, 1, 'Grass americano', 500.00, 6.50, 3250.00);

-- Ventas
INSERT INTO sales (id, receipt_number, customer_id, customer_name, customer_phone, sale_date, subtotal, delivery_fee, discount, total, payment_method, payment_status, seller_username, created_by) VALUES
(1, 'VNT-2026-0125', 1, 'Juan Pérez', '+51 981234567', CURRENT_TIMESTAMP, 600.00, 15.00, 0.00, 615.00, 'YAPE', 'PAGADO', 'admin', 'admin'),
(2, 'VNT-2026-0124', 2, 'María López', '+51 987654321', CURRENT_TIMESTAMP, 160.00, 15.00, 0.00, 175.00, 'PLIN', 'PAGADO', 'admin', 'admin'),
(3, 'VNT-2026-0123', 3, 'Carlos Ruiz', '+51 974125896', CURRENT_TIMESTAMP, 450.00, 20.00, 0.00, 470.00, 'TRANSFERENCIA', 'PAGADO', 'vendedor', 'vendedor');

INSERT INTO sale_items (id, sale_id, product_id, product_name, unit_type, quantity, unit_price, total_price) VALUES
(1, 1, 1, 'Grass americano', 'M2', 50.00, 12.00, 600.00),
(2, 2, 4, 'Palmera Areca', 'UNIDAD', 2.00, 80.00, 160.00),
(3, 3, 2, 'Grass bermuda', 'M2', 30.00, 15.00, 450.00);

-- Pedidos (Exactos como en el diseño)
INSERT INTO orders (id, order_number, sale_id, customer_id, customer_name, customer_phone, delivery_address, delivery_date, delivery_time_slot, status, assigned_driver_name, assigned_driver_phone, created_by) VALUES
(1, '#P-00125', 1, 1, 'Juan Pérez', '+51 981234567', 'Av. Los Jardines 123, San Isidro', CURRENT_TIMESTAMP, '10:00 AM', 'PENDIENTE', 'Carlos Delivery', '+51 987654323', 'admin'),
(2, '#P-00124', 2, 2, 'María López', '+51 987654321', 'Calle Las Flores 456, Miraflores', CURRENT_TIMESTAMP, '02:00 PM', 'PENDIENTE', 'Carlos Delivery', '+51 987654323', 'admin'),
(3, '#P-00123', 3, 3, 'Carlos Ruiz', '+51 974125896', 'Jr. El Bosque 789, Surco', CURRENT_TIMESTAMP, '09:00 AM', 'EN_DELIVERY', 'Carlos Delivery', '+51 987654323', 'vendedor');

-- Deliveries
INSERT INTO deliveries (id, order_id, driver_name, driver_phone, route_status, current_latitude, current_longitude, recipient_notes) VALUES
(1, 3, 'Carlos Delivery', '+51 987654323', 'EN_CAMINO', -12.0897, -77.0365, 'Cliente en espera en domicilio');

-- Gastos Operativos
INSERT INTO expenses (id, category, description, amount, expense_date, payment_method, registered_by) VALUES
(1, 'TRANSPORTE', 'Combustible para camión de repartos', 150.00, CURRENT_TIMESTAMP, 'EFECTIVO', 'admin'),
(2, 'INSUMOS', 'Bolsas compostables y sustrato para plantas', 280.00, CURRENT_TIMESTAMP, 'YAPE', 'admin'),
(3, 'PERSONAL', 'Pago diario de ayudante de jardinería', 100.00, CURRENT_TIMESTAMP, 'EFECTIVO', 'admin');

-- Ubigeo: Departamentos, Provincias y Distritos
INSERT INTO departments (id, code, name, active) VALUES
(1, '07', 'Callao', true),
(2, '15', 'Lima', true),
(3, '04', 'Arequipa', true),
(4, '13', 'La Libertad', true);

INSERT INTO provinces (id, code, name, department_id, active) VALUES
(1, '0701', 'Callao', 1, true),
(2, '1501', 'Lima', 2, true),
(3, '0401', 'Arequipa', 3, true),
(4, '1301', 'Trujillo', 4, true);

INSERT INTO districts (id, code, name, province_id, active) VALUES
(1, '070102', 'Bellavista', 1, true),
(2, '070101', 'Callao (Cercado)', 1, true),
(3, '070103', 'Carmen de La Legua-Reynoso', 1, true),
(4, '070104', 'La Perla', 1, true),
(5, '070105', 'La Punta', 1, true),
(6, '070107', 'Mi Perú', 1, true),
(7, '070106', 'Ventanilla', 1, true),
(8, '150101', 'Lima (Cercado)', 2, true),
(9, '150122', 'Miraflores', 2, true),
(10, '150131', 'San Isidro', 2, true),
(11, '150140', 'Santiago de Surco', 2, true),
(12, '150130', 'San Borja', 2, true),
(13, '150136', 'San Miguel', 2, true),
(14, '150117', 'Los Olivos', 2, true);

-- INSERT statements work on both MySQL and PostgreSQL
-- MySQL-specific ALTER statements are handled by DatabaseMigrationRunner
