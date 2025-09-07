-- =====================================================
-- IPAS - Insurance Policy Management System Database
-- Sistema de Gestion de Polizas de Seguros
-- =====================================================

CREATE DATABASE IF NOT EXISTS ipas_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE ipas_db;

-- =====================================================
-- TABLA: users
-- Gestion de usuarios del sistema (administradores y asesores)
-- =====================================================

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    role ENUM('ADMINISTRADOR', 'ASESOR') NOT NULL DEFAULT 'ASESOR',
    status ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
    two_factor_enabled BOOLEAN DEFAULT FALSE,
    two_factor_secret VARCHAR(255),
    reset_password_token VARCHAR(255),
    reset_password_expires DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login DATETIME,
    
    -- Índices para optimizar consultas
    INDEX idx_user_email (email),
    INDEX idx_user_status (status),
    INDEX idx_user_role (role),
    INDEX idx_user_reset_token (reset_password_token),
    INDEX idx_user_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLA: clients
-- Gestion de clientes del sistema
-- =====================================================

CREATE TABLE IF NOT EXISTS clients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    document_number VARCHAR(50) NOT NULL UNIQUE,
    document_type ENUM('DNI', 'PASSPORT', 'CARNET_EXTRANJERIA') NOT NULL DEFAULT 'DNI',
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE,
    phone_number VARCHAR(20),
    birth_date DATE,
    address TEXT,
    occupation VARCHAR(100),
    status ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    user_id BIGINT NOT NULL,
    
    -- Claves foráneas
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    -- Índices para optimizar consultas
    INDEX idx_client_document (document_number),
    INDEX idx_client_email (email),
    INDEX idx_client_user (user_id),
    INDEX idx_client_status (status),
    INDEX idx_client_name (first_name, last_name),
    INDEX idx_client_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLA: policies
-- Gestion de pólizas de seguros
-- =====================================================

CREATE TABLE IF NOT EXISTS policies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    policy_number VARCHAR(100) NOT NULL UNIQUE,
    policy_type VARCHAR(50) NOT NULL,
    coverage TEXT NOT NULL,
    premium_amount DECIMAL(12,2) NOT NULL,
    coverage_amount DECIMAL(15,2) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE', 'EXPIRED', 'CANCELLED', 'SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
    deductible DECIMAL(12,2),
    beneficiaries TEXT,
    terms_conditions TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    client_id BIGINT NOT NULL,
    
    -- Claves foráneas
    FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE,
    
    -- Índices para optimizar consultas
    INDEX idx_policy_number (policy_number),
    INDEX idx_policy_status (status),
    INDEX idx_policy_client (client_id),
    INDEX idx_policy_dates (start_date, end_date),
    INDEX idx_policy_type (policy_type),
    INDEX idx_policy_created (created_at),
    INDEX idx_policy_expiration (end_date, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLA: audit_logs (Opcional - para auditoría)
-- Registro de actividades del sistema
-- =====================================================

CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT,
    old_values JSON,
    new_values JSON,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    -- Claves foráneas
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    
    -- Índices para optimizar consultas
    INDEX idx_audit_user (user_id),
    INDEX idx_audit_action (action),
    INDEX idx_audit_entity (entity_type, entity_id),
    INDEX idx_audit_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- DATOS INICIALES
-- =====================================================

-- Insertar usuario administrador por defecto
INSERT IGNORE INTO users (
    email, 
    password, 
    first_name, 
    last_name, 
    role, 
    status,
    created_at
) VALUES (
    'admin@ipas.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- password: admin123
    'Administrador',
    'Sistema',
    'ADMINISTRADOR',
    'ACTIVE',
    NOW()
);

-- Insertar usuario asesor por defecto
INSERT IGNORE INTO users (
    email, 
    password, 
    first_name, 
    last_name, 
    role, 
    status,
    created_at
) VALUES (
    'asesor@ipas.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- password: asesor123
    'Carlos',
    'Mendoza',
    'ASESOR',
    'ACTIVE',
    NOW()
);

-- Insertar algunos clientes de ejemplo
INSERT IGNORE INTO clients (
    document_number,
    document_type,
    first_name,
    last_name,
    email,
    phone_number,
    birth_date,
    address,
    occupation,
    status,
    user_id,
    created_at
) VALUES 
(
    '12345678',
    'DNI',
    'Juan',
    'Pérez',
    'juan.perez@email.com',
    '987654321',
    '1985-03-15',
    'Av. Principal 123, Lima',
    'Ingeniero',
    'ACTIVE',
    2, -- ID del asesor
    NOW()
),
(
    '87654321',
    'DNI',
    'María',
    'García',
    'maria.garcia@email.com',
    '987654322',
    '1990-07-22',
    'Jr. Los Olivos 456, Lima',
    'Doctora',
    'ACTIVE',
    2, -- ID del asesor
    NOW()
),
(
    '11223344',
    'DNI',
    'Pedro',
    'López',
    'pedro.lopez@email.com',
    '987654323',
    '1988-12-10',
    'Calle Las Flores 789, Lima',
    'Abogado',
    'ACTIVE',
    2, -- ID del asesor
    NOW()
);

-- Insertar algunas pólizas de ejemplo
INSERT IGNORE INTO policies (
    policy_number,
    policy_type,
    coverage,
    premium_amount,
    coverage_amount,
    start_date,
    end_date,
    status,
    deductible,
    beneficiaries,
    terms_conditions,
    client_id,
    created_at
) VALUES 
(
    'POL-2024-001',
    'VIDA',
    'Seguro de vida con cobertura completa por muerte natural o accidental',
    150.00,
    50000.00,
    '2024-01-01',
    '2024-12-31',
    'ACTIVE',
    500.00,
    'Cónyuge: María Pérez, Hijos: Juan Jr., Ana Pérez',
    'Póliza sujeta a términos y condiciones generales de la compañía',
    1,
    NOW()
),
(
    'POL-2024-002',
    'AUTOMOVIL',
    'Seguro vehicular todo riesgo con cobertura contra terceros',
    200.00,
    25000.00,
    '2024-02-01',
    '2025-01-31',
    'ACTIVE',
    1000.00,
    'Propietario del vehículo',
    'Cobertura válida en territorio nacional',
    2,
    NOW()
),
(
    'POL-2024-003',
    'HOGAR',
    'Seguro de hogar contra incendios, robos y desastres naturales',
    120.00,
    80000.00,
    '2024-03-01',
    '2025-02-28',
    'ACTIVE',
    800.00,
    'Familia López',
    'Cobertura incluye contenido y estructura',
    3,
    NOW()
);

-- =====================================================
-- VISTAS PARA REPORTES (Opcional)
-- =====================================================

-- Vista para estadísticas de pólizas por asesor
CREATE OR REPLACE VIEW policy_stats_by_user AS
SELECT 
    u.id as user_id,
    u.first_name,
    u.last_name,
    u.email,
    COUNT(DISTINCT c.id) as total_clients,
    COUNT(p.id) as total_policies,
    COUNT(CASE WHEN p.status = 'ACTIVE' THEN 1 END) as active_policies,
    SUM(CASE WHEN p.status = 'ACTIVE' THEN p.premium_amount ELSE 0 END) as total_premiums,
    SUM(CASE WHEN p.status = 'ACTIVE' THEN p.coverage_amount ELSE 0 END) as total_coverage
FROM users u
LEFT JOIN clients c ON u.id = c.user_id
LEFT JOIN policies p ON c.id = p.client_id
WHERE u.role = 'ASESOR'
GROUP BY u.id, u.first_name, u.last_name, u.email;

-- Vista para pólizas próximas a vencer
CREATE OR REPLACE VIEW expiring_policies AS
SELECT 
    p.id,
    p.policy_number,
    p.policy_type,
    p.end_date,
    DATEDIFF(p.end_date, CURDATE()) as days_to_expire,
    c.first_name as client_first_name,
    c.last_name as client_last_name,
    c.email as client_email,
    u.first_name as asesor_first_name,
    u.last_name as asesor_last_name,
    u.email as asesor_email
FROM policies p
JOIN clients c ON p.client_id = c.id
JOIN users u ON c.user_id = u.id
WHERE p.status = 'ACTIVE' 
AND p.end_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 30 DAY)
ORDER BY p.end_date ASC;

-- =====================================================
-- PROCEDIMIENTOS ALMACENADOS (Opcional)
-- =====================================================

DELIMITER //

-- Procedimiento para actualizar estado de pólizas vencidas
CREATE PROCEDURE UpdateExpiredPolicies()
BEGIN
    UPDATE policies 
    SET status = 'EXPIRED', updated_at = NOW()
    WHERE status = 'ACTIVE' 
    AND end_date < CURDATE();
    
    SELECT ROW_COUNT() as policies_updated;
END //

-- Procedimiento para obtener estadísticas del dashboard
CREATE PROCEDURE GetDashboardStats(IN user_id BIGINT, IN user_role VARCHAR(20))
BEGIN
    IF user_role = 'ADMINISTRADOR' THEN
        SELECT 
            (SELECT COUNT(*) FROM users WHERE status = 'ACTIVE') as total_users,
            (SELECT COUNT(*) FROM clients WHERE status = 'ACTIVE') as total_clients,
            (SELECT COUNT(*) FROM policies WHERE status = 'ACTIVE') as total_policies,
            (SELECT SUM(premium_amount) FROM policies WHERE status = 'ACTIVE') as total_premiums,
            (SELECT COUNT(*) FROM policies WHERE status = 'ACTIVE' AND end_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 30 DAY)) as expiring_soon;
    ELSE
        SELECT 
            (SELECT COUNT(*) FROM clients WHERE user_id = user_id AND status = 'ACTIVE') as total_clients,
            (SELECT COUNT(*) FROM policies p JOIN clients c ON p.client_id = c.id WHERE c.user_id = user_id AND p.status = 'ACTIVE') as total_policies,
            (SELECT SUM(p.premium_amount) FROM policies p JOIN clients c ON p.client_id = c.id WHERE c.user_id = user_id AND p.status = 'ACTIVE') as total_premiums,
            (SELECT COUNT(*) FROM policies p JOIN clients c ON p.client_id = c.id WHERE c.user_id = user_id AND p.status = 'ACTIVE' AND p.end_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 30 DAY)) as expiring_soon;
    END IF;
END //

DELIMITER ;

-- =====================================================
-- TRIGGERS PARA AUDITORÍA
-- =====================================================

DELIMITER //

-- Trigger para auditar cambios en usuarios
CREATE TRIGGER audit_users_update
AFTER UPDATE ON users
FOR EACH ROW
BEGIN
    INSERT INTO audit_logs (
        user_id, 
        action, 
        entity_type, 
        entity_id, 
        old_values, 
        new_values,
        created_at
    ) VALUES (
        NEW.id,
        'UPDATE',
        'USER',
        NEW.id,
        JSON_OBJECT(
            'email', OLD.email,
            'first_name', OLD.first_name,
            'last_name', OLD.last_name,
            'role', OLD.role,
            'status', OLD.status
        ),
        JSON_OBJECT(
            'email', NEW.email,
            'first_name', NEW.first_name,
            'last_name', NEW.last_name,
            'role', NEW.role,
            'status', NEW.status
        ),
        NOW()
    );
END //

-- Trigger para auditar cambios en clientes
CREATE TRIGGER audit_clients_update
AFTER UPDATE ON clients
FOR EACH ROW
BEGIN
    INSERT INTO audit_logs (
        user_id, 
        action, 
        entity_type, 
        entity_id, 
        old_values, 
        new_values,
        created_at
    ) VALUES (
        NEW.user_id,
        'UPDATE',
        'CLIENT',
        NEW.id,
        JSON_OBJECT(
            'document_number', OLD.document_number,
            'first_name', OLD.first_name,
            'last_name', OLD.last_name,
            'email', OLD.email,
            'status', OLD.status
        ),
        JSON_OBJECT(
            'document_number', NEW.document_number,
            'first_name', NEW.first_name,
            'last_name', NEW.last_name,
            'email', NEW.email,
            'status', NEW.status
        ),
        NOW()
    );
END //

-- Trigger para auditar cambios en pólizas
CREATE TRIGGER audit_policies_update
AFTER UPDATE ON policies
FOR EACH ROW
BEGIN
    INSERT INTO audit_logs (
        user_id, 
        action, 
        entity_type, 
        entity_id, 
        old_values, 
        new_values,
        created_at
    ) VALUES (
        (SELECT user_id FROM clients WHERE id = NEW.client_id),
        'UPDATE',
        'POLICY',
        NEW.id,
        JSON_OBJECT(
            'policy_number', OLD.policy_number,
            'policy_type', OLD.policy_type,
            'premium_amount', OLD.premium_amount,
            'coverage_amount', OLD.coverage_amount,
            'status', OLD.status
        ),
        JSON_OBJECT(
            'policy_number', NEW.policy_number,
            'policy_type', NEW.policy_type,
            'premium_amount', NEW.premium_amount,
            'coverage_amount', NEW.coverage_amount,
            'status', NEW.status
        ),
        NOW()
    );
END //

DELIMITER ;

-- =====================================================
-- CONFIGURACIONES ADICIONALES
-- =====================================================

-- Configurar zona horaria
SET time_zone = '-05:00'; -- Hora de Perú

-- Configurar modo SQL para mayor compatibilidad
SET sql_mode = 'STRICT_TRANS_TABLES,NO_ZERO_DATE,NO_ZERO_IN_DATE,ERROR_FOR_DIVISION_BY_ZERO';

-- =====================================================
-- ÍNDICES ADICIONALES PARA OPTIMIZACIÓN
-- =====================================================

-- Índices compuestos para consultas complejas
CREATE INDEX idx_client_user_status ON clients(user_id, status);
CREATE INDEX idx_policy_client_status ON policies(client_id, status);
CREATE INDEX idx_policy_type_status ON policies(policy_type, status);
CREATE INDEX idx_policy_dates_status ON policies(start_date, end_date, status);

-- Índices de texto completo para búsquedas
ALTER TABLE clients ADD FULLTEXT(first_name, last_name, email);
ALTER TABLE policies ADD FULLTEXT(policy_number, policy_type, coverage);

-- =====================================================
-- VERIFICACIÓN DE LA ESTRUCTURA
-- =====================================================

-- Mostrar información de las tablas creadas
SHOW TABLES;

-- Mostrar estructura de cada tabla
DESCRIBE users;
DESCRIBE clients;
DESCRIBE policies;
DESCRIBE audit_logs;

-- Verificar datos iniciales
SELECT 'Usuarios creados:' as info, COUNT(*) as count FROM users;
SELECT 'Clientes de ejemplo:' as info, COUNT(*) as count FROM clients;
SELECT 'Pólizas de ejemplo:' as info, COUNT(*) as count FROM policies;

-- =====================================================
-- CONSULTAS DE EJEMPLO PARA VERIFICAR FUNCIONAMIENTO
-- =====================================================

-- Consultar usuarios con sus estadísticas
SELECT 
    u.email,
    u.first_name,
    u.last_name,
    u.role,
    COUNT(DISTINCT c.id) as total_clients,
    COUNT(p.id) as total_policies
FROM users u
LEFT JOIN clients c ON u.id = c.user_id
LEFT JOIN policies p ON c.id = p.client_id
GROUP BY u.id, u.email, u.first_name, u.last_name, u.role;

-- Consultar pólizas con información del cliente y asesor
SELECT 
    p.policy_number,
    p.policy_type,
    p.premium_amount,
    p.status,
    CONCAT(c.first_name, ' ', c.last_name) as client_name,
    c.document_number,
    CONCAT(u.first_name, ' ', u.last_name) as asesor_name
FROM policies p
JOIN clients c ON p.client_id = c.id
JOIN users u ON c.user_id = u.id
ORDER BY p.created_at DESC;

-- =====================================================
-- COMENTARIOS FINALES
-- =====================================================

/*
NOTAS IMPORTANTES:

1. SEGURIDAD:
   - Las contraseñas están hasheadas con BCrypt
   - Se incluyen triggers de auditoría para rastrear cambios
   - Índices optimizados para consultas rápidas

2. ESCALABILIDAD:
   - Uso de InnoDB para soporte de transacciones
   - Índices compuestos para consultas complejas
   - Particionamiento futuro posible por fechas

3. USUARIOS POR DEFECTO:
   - admin@ipas.com / admin123 (Administrador)
   - asesor@ipas.com / asesor123 (Asesor)

4. MANTENIMIENTO:
   - Ejecutar UpdateExpiredPolicies() periódicamente
   - Monitorear tabla audit_logs para limpieza
   - Considerar archivado de datos antiguos

5. BACKUP:
   - Configurar backups automáticos diarios
   - Mantener logs de transacciones para recuperación
*/