-- حذف قاعدة البيانات إذا كانت موجودة
DROP DATABASE IF EXISTS DBproject;

-- إنشاء قاعدة البيانات
CREATE DATABASE DBproject;
USE DBproject;

-- إنشاء جدول الموظفين
CREATE TABLE employee (
    snn INT PRIMARY KEY,
    Ename VARCHAR(50) NOT NULL,
    date_of_birth DATE NOT NULL,
    date_of_join DATE NOT NULL,
    rate INT NOT NULL,
    address VARCHAR(100),
    email VARCHAR(100),
    phone_number VARCHAR(15),
    type VARCHAR(20) NOT NULL
);

-- إنشاء جدول عناوين الموظفين
CREATE TABLE employee_addresses (
    address_id INT AUTO_INCREMENT PRIMARY KEY,
    employee_ssn INT NOT NULL,
    address VARCHAR(255) NOT NULL,
    address_type VARCHAR(50),
    FOREIGN KEY (employee_ssn) REFERENCES employee(snn) ON DELETE CASCADE
);

-- إنشاء جدول عناوين البريد الإلكتروني للموظفين
CREATE TABLE email_addresses (
    email_id INT AUTO_INCREMENT PRIMARY KEY,
    employee_ssn INT NOT NULL,
    email VARCHAR(255) NOT NULL,
    email_type VARCHAR(50),
    FOREIGN KEY (employee_ssn) REFERENCES employee(snn) ON DELETE CASCADE
);

-- إنشاء جدول أرقام هواتف الموظفين
CREATE TABLE phone_numbers (
    phone_id INT AUTO_INCREMENT PRIMARY KEY,
    employee_ssn INT NOT NULL,
    phone_number VARCHAR(15) NOT NULL,
    phone_type VARCHAR(20),
    FOREIGN KEY (employee_ssn) REFERENCES employee(snn) ON DELETE CASCADE
);

-- إنشاء جدول المستخدمين
CREATE TABLE user (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    employee_id INT NOT NULL,
    FOREIGN KEY (employee_id) REFERENCES employee(snn) ON DELETE CASCADE
);

-- إنشاء جدول المرضى
CREATE TABLE Patients (
    cid INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,  -- Changed 'name' to 'full_name'
    gender VARCHAR(10),
    email VARCHAR(100),
    phone_number VARCHAR(15),
    address VARCHAR(200),
    date_of_birth DATE
);

-- إنشاء جدول الفواتير
CREATE TABLE Invoice (
    invoice_id INT AUTO_INCREMENT PRIMARY KEY,
    amount DECIMAL(10, 2) NOT NULL,
    date DATE NOT NULL,
    due_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    cid INT NOT NULL,
    FOREIGN KEY (cid) REFERENCES Patients(cid) ON DELETE CASCADE
);

-- إنشاء جدول المخزون
CREATE TABLE stock (
    stockId INT AUTO_INCREMENT PRIMARY KEY,
    shelfLocation VARCHAR(255) NOT NULL,
    lastUpdate DATE NOT NULL,
    employeeSsn INT NOT NULL,
    FOREIGN KEY (employeeSsn) REFERENCES employee(snn) ON DELETE CASCADE
);

-- إنشاء جدول المنتجات
CREATE TABLE product (
    productId INT AUTO_INCREMENT PRIMARY KEY,
    productName VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    reorderLevel INT NOT NULL,
    quantity INT NOT NULL,
    unitPrice DECIMAL(10, 2) NOT NULL,
    description TEXT,
    stockId INT NOT NULL,
    FOREIGN KEY (stockId) REFERENCES stock(stockId) ON DELETE CASCADE
);

-- إنشاء جدول الدفع
CREATE TABLE paying (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    amount_paid DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    invoice_id INT NOT NULL,
    payment_date DATE NOT NULL,
    employee_ssn INT,
    FOREIGN KEY (invoice_id) REFERENCES Invoice(invoice_id) ON DELETE CASCADE,
    FOREIGN KEY (employee_ssn) REFERENCES employee(snn) ON DELETE SET NULL
);

-- إنشاء جدول الدفع النقدي
CREATE TABLE cash (
    p_id INT PRIMARY KEY,
    currency VARCHAR(50) NOT NULL,
    FOREIGN KEY (p_id) REFERENCES paying(payment_id) ON DELETE CASCADE
);

-- إنشاء جدول الدفع عبر البطاقات
CREATE TABLE cards (
    p_id INT PRIMARY KEY,
    currency VARCHAR(50) NOT NULL,
    FOREIGN KEY (p_id) REFERENCES paying(payment_id) ON DELETE CASCADE
);

-- إنشاء جدول الموردين
CREATE TABLE supplier (
    s_id INT AUTO_INCREMENT PRIMARY KEY,
    s_name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone_number VARCHAR(15),
    employee_ssn INT NOT NULL,
    FOREIGN KEY (employee_ssn) REFERENCES employee(snn) ON DELETE CASCADE
);

CREATE TABLE supplier_phone (
    s_id INT NOT NULL,
    phone_number VARCHAR(15) NOT NULL,
    phone_type VARCHAR(20),
    PRIMARY KEY (s_id, phone_number),
    FOREIGN KEY (s_id) REFERENCES supplier(s_id) ON DELETE CASCADE
);

CREATE TABLE supplier_email (
    s_id INT AUTO_INCREMENT PRIMARY KEY,
    supplier_id INT NOT NULL,
    email VARCHAR(255) NOT NULL,
    FOREIGN KEY (supplier_id) REFERENCES supplier(s_id) ON DELETE CASCADE
);

-- إنشاء جدول الأطباء
CREATE TABLE doctors (
    doctor_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    speciality VARCHAR(100),
    phonenumber VARCHAR(15),
    email VARCHAR(100),
    address VARCHAR(200),
    date DATE,
    employee_ssn INT,
    FOREIGN KEY (employee_ssn) REFERENCES employee(snn) ON DELETE CASCADE
);
CREATE TABLE appointments (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    time TIME NOT NULL,
    purpose VARCHAR(255),
    cid INT NOT NULL,
    doctor_id INT NOT NULL,
    FOREIGN KEY (cid) REFERENCES patients(cid) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE CASCADE
);



SELECT se.email, s.s_name
FROM supplier_email se
JOIN supplier s ON se.supplier_id = s.s_id;

-- إدخال بيانات الموظفين
INSERT INTO employee (snn, Ename, date_of_birth, date_of_join, rate, address, email, phone_number, type) VALUES
(1, 'John Doe', '1985-05-15', '2010-08-01', 5000, '123 Main St, City', 'johndoe@example.com', '1234567890', 'Manager'),
(2, 'Jane Smith', '1990-03-22', '2015-02-20', 4500, '456 Elm St, City', 'janesmith@example.com', '0987654321', 'accountant'),
(3, 'Emily Clark', '1995-07-10', '2021-06-15', 4000, '789 Oak St, City', 'emilyclark@example.com', '1122334455', 'worker'),
(4, 'Robert Brown', '1982-01-30', '2012-03-15', 4800, '101 Pine St, City', 'robertbrown@example.com', '1239876543', 'assistant manager'),
(5, 'Laura Green', '1992-11-11', '2018-04-20', 4700, '202 Maple St, City', 'lauragreen@example.com', '9876543210', 'HR'),
(6, 'Michael White', '1987-06-12', '2010-07-01', 5200, '303 Birch St, City', 'michaelwhite@example.com', '6543219870', 'developer'),
(7, 'Sophia Black', '1993-09-15', '2017-08-01', 4600, '404 Cedar St, City', 'sophiablack@example.com', '1122336677', 'sales'),
(8, 'Daniel Lewis', '1980-02-20', '2009-11-10', 5300, '505 Walnut St, City', 'daniellewis@example.com', '7654321098', 'manager'),
(9, 'Olivia Harris', '1991-04-25', '2014-06-05', 4900, '606 Chestnut St, City', 'oliviaharris@example.com', '3216549870', 'IT support');

-- إدخال بيانات المستخدمين
INSERT INTO user (username, password, employee_id) VALUES
('admin', 'password123', 1),
('optician1', 'securepass', 2),
('hrmanager', 'hrpass123', 5),
('salesadmin', 'salespass456', 7),
('developer01', 'devpassword', 6),
('assistant_manager01', 'managerpass789', 4),
('Adam','6427',5);

-- إدخال بيانات الأطباء
INSERT INTO doctors (name, speciality, phonenumber, email, address, date, employee_ssn) VALUES
('Dr. John Carter', 'Cardiology', '9876543210', 'johncarter@example.com', '123 Heart St, City', '2015-01-01', 1),
('Dr. Emily Scott', 'Pediatrics', '8765432109', 'emilyscott@example.com', '456 Child St, City', '2018-03-15', 2),
('Dr. William Brown', 'Orthopedics', '7654321098', 'williambrown@example.com', '789 Bone St, City', '2020-05-10', 3),
('Dr. Sophia White', 'Dermatology', '6543210987', 'sophiawhite@example.com', '101 Skin Ave, City', '2017-08-22', 4),
('Dr. Daniel Adams', 'General Medicine', '5432109876', 'danieladams@example.com', '202 Health Rd, City', '2019-02-12', 5),
('Dr. Olivia Johnson', 'Neurology', '4321098765', 'oliviajohnson@example.com', '303 Brain Blvd, City', '2016-11-07', 6);


-- إدخال بيانات المرضى
INSERT INTO Patients (full_name, gender, email, phone_number, address, date_of_birth) VALUES  -- Changed 'name' to 'full_name'
('Alice Brown', 'Female', 'alicebrown@example.com', '1239876543', '789 Oak St, City', '1995-06-10'),
('Bob Smith', 'Male', 'bobsmith@example.com', '4563217890', '321 Pine St, City', '1988-04-18'),
('Charlie Wilson', 'Male', 'charliewilson@example.com', '5556667777', '12 Maple St, City', '2000-01-20'),
('Diana Johnson', 'Female', 'dianajohnson@example.com', '1112223333', '34 Cedar St, City', '1992-05-10'),
('Eva Williams', 'Female', 'evawilliams@example.com', '6667778888', '56 Birch St, City', '1985-11-30'),
('George Scott', 'Male', 'georgescott@example.com', '2223334444', '78 Oak St, City', '1978-09-17');

INSERT INTO appointments (date, time, purpose, cid, doctor_id) VALUES
('2025-01-10', '10:30:00', 'General Checkup', 1, 1),
('2025-01-12', '14:00:00', 'Dental Consultation', 2, 2),
('2025-01-15', '09:00:00', 'Eye Examination', 3, 3),
('2025-01-18', '11:00:00', 'Follow-up', 4, 1),
('2025-01-20', '16:00:00', 'Physiotherapy', 5, 4);



-- إدخال بيانات الفواتير
INSERT INTO Invoice (amount, date, due_date, status, cid) VALUES
(150.50, '2024-01-10', '2024-01-25', 'Paid', 1),
(200.75, '2024-01-12', '2024-01-26', 'Unpaid', 2),
(99.99, '2024-01-14', '2024-01-28', 'Paid', 3),
(300.00, '2024-01-15', '2024-01-30', 'Unpaid', 4),
(120.00, '2024-01-16', '2024-01-31', 'Paid', 5),
(500.00, '2024-01-17', '2024-02-01', 'Unpaid', 6);

-- إدخال بيانات المخزون
INSERT INTO stock (shelfLocation, lastUpdate, employeeSsn) VALUES
('Shelf A1', '2024-01-05', 1),
('Shelf B2', '2024-01-06', 2),
('Shelf C3', '2024-01-07', 3);

-- إدخال بيانات المنتجات
INSERT INTO product (productName, category, reorderLevel, quantity, unitPrice, description, stockId) VALUES
('Aspirin', 'Medicine', 20, 100, 5.50, 'Pain relief medication', 1),
('Bandages', 'Medical Supplies', 50, 200, 2.00, 'For wound dressing', 2),
('Cough Syrup', 'Medicine', 30, 150, 8.00, 'For cough relief', 3);

-- إدخال بيانات الدفع
INSERT INTO paying (type, amount_paid, currency, invoice_id, payment_date, employee_ssn) VALUES
('Cash', 150.50, 'USD', 1, '2024-01-10', 1),
('Card', 200.75, 'USD', 2, '2024-01-12', 2),
('Cash', 99.99, 'USD', 3, '2024-01-14', 3);