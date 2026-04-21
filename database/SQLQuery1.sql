USE [master];
GO

-- ==========================================
-- 1. TẠO DATABASE (NẾU CHƯA TỒN TẠI)
-- ==========================================
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'ShopGiayDB')
BEGIN
    CREATE DATABASE [ShopGiayDB];
END
GO

USE [ShopGiayDB];
GO

-- ==========================================
-- 2. TẠO CÁC BẢNG & TÍCH HỢP SẴN DEFAULT VALUE
-- ==========================================

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Role')
CREATE TABLE [dbo].[Role](
	[id] [int] IDENTITY(1,1) PRIMARY KEY,
	[name] [nvarchar](50) NOT NULL,
	[permissions] [nvarchar](max) NULL
);
GO

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'User')
CREATE TABLE [dbo].[User](
	[id] [int] IDENTITY(1,1) PRIMARY KEY,
	[role_id] [int] NULL,
	[email] [varchar](100) NOT NULL,
	[password_hash] [varchar](max) NOT NULL,
	[phone] [varchar](20) NULL,
	[status] [nvarchar](20) DEFAULT ('Active'),
	[created_at] [datetime] DEFAULT (GETDATE())
);
GO

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Address_Book')
CREATE TABLE [dbo].[Address_Book](
	[id] [int] IDENTITY(1,1) PRIMARY KEY,
	[user_id] [int] NULL,
	[receiver_name] [nvarchar](100) NULL,
	[phone] [varchar](20) NULL,
	[full_address] [nvarchar](max) NULL,
	[is_default] [bit] DEFAULT ((0))
);
GO

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Brand')
CREATE TABLE [dbo].[Brand](
	[id] [int] IDENTITY(1,1) PRIMARY KEY,
	[name] [nvarchar](100) NOT NULL,
	[logo_url] [nvarchar](max) NULL
);
GO

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Category')
CREATE TABLE [dbo].[Category](
	[id] [int] IDENTITY(1,1) PRIMARY KEY,
	[parent_id] [int] NULL,
	[name] [nvarchar](100) NOT NULL,
	[slug] [varchar](150) NOT NULL
);
GO

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Customer_Profile')
CREATE TABLE [dbo].[Customer_Profile](
	[user_id] [int] PRIMARY KEY,
	[full_name] [nvarchar](100) NOT NULL,
	[dob] [date] NULL,
	[shoe_size_preference] [decimal](4, 1) NULL,
	[reward_points] [int] DEFAULT ((0))
);
GO

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Product')
CREATE TABLE [dbo].[Product](
	[id] [int] IDENTITY(1,1) PRIMARY KEY,
	[category_id] [int] NULL,
	[brand_id] [int] NULL,
	[name] [nvarchar](200) NOT NULL,
	[slug] [varchar](250) NOT NULL,
	[description] [nvarchar](max) NULL,
	[base_price] [decimal](18, 2) NOT NULL,
	[status] [nvarchar](50) DEFAULT ('Selling'),
	[gender] [nvarchar](20) DEFAULT ('Unisex')
);
GO

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Product_Image')
CREATE TABLE [dbo].[Product_Image](
	[id] [int] IDENTITY(1,1) PRIMARY KEY,
	[product_id] [int] NULL,
	[image_url] [nvarchar](max) NOT NULL,
	[is_primary] [bit] DEFAULT ((0)),
	[display_order] [int] DEFAULT ((0))
);
GO

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Product_Variant')
CREATE TABLE [dbo].[Product_Variant](
	[id] [int] IDENTITY(1,1) PRIMARY KEY,
	[product_id] [int] NULL,
	[sku_code] [varchar](100) NOT NULL,
	[size] [decimal](4, 1) NOT NULL,
	[color] [nvarchar](50) NULL,
	[stock_quantity] [int] DEFAULT ((0)) CHECK ([stock_quantity]>=(0)),
	[weight_grams] [int] DEFAULT ((0))
);
GO

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Inventory_Transaction')
CREATE TABLE [dbo].[Inventory_Transaction](
	[id] [int] IDENTITY(1,1) PRIMARY KEY,
	[variant_id] [int] NULL,
	[user_id] [int] NULL,
	[type] [nvarchar](50) NOT NULL,
	[quantity_change] [int] NOT NULL,
	[ref_id] [int] NULL,
	[note] [nvarchar](max) NULL,
	[created_at] [datetime] DEFAULT (GETDATE())
);
GO

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Promotion')
CREATE TABLE [dbo].[Promotion](
	[id] [int] IDENTITY(1,1) PRIMARY KEY,
	[code] [varchar](50) NOT NULL,
	[type] [nvarchar](50) NULL,
	[discount_value] [decimal](18, 2) NOT NULL,
	[min_order_value] [decimal](18, 2) DEFAULT ((0)),
	[max_discount_amount] [decimal](18, 2) NULL,
	[usage_limit] [int] NULL,
	[current_usage] [int] DEFAULT ((0)),
	[start_date] [datetime] NULL,
	[end_date] [datetime] NULL
);
GO

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Order')
CREATE TABLE [dbo].[Order](
	[id] [int] IDENTITY(1,1) PRIMARY KEY,
	[order_code] [varchar](50) NOT NULL,
	[customer_id] [int] NULL,
	[employee_id] [int] NULL,
	[promotion_id] [int] NULL,
	[type] [nvarchar](50) NULL,
	[status] [nvarchar](50) DEFAULT ('Pending'),
	[subtotal] [decimal](18, 2) NOT NULL,
	[shipping_fee] [decimal](18, 2) DEFAULT ((0)),
	[discount_amount] [decimal](18, 2) DEFAULT ((0)),
	[final_amount] [decimal](18, 2) NOT NULL,
	[customer_note] [nvarchar](max) NULL,
	[created_at] [datetime] DEFAULT (GETDATE())
);
GO

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Order_Detail')
CREATE TABLE [dbo].[Order_Detail](
	[id] [int] IDENTITY(1,1) PRIMARY KEY,
	[order_id] [int] NULL,
	[variant_id] [int] NULL,
	[quantity] [int] NOT NULL,
	[unit_price] [decimal](18, 2) NOT NULL,
	[subtotal] [decimal](18, 2) NOT NULL
);
GO

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Payment')
CREATE TABLE [dbo].[Payment](
	[id] [int] IDENTITY(1,1) PRIMARY KEY,
	[order_id] [int] NULL,
	[method] [nvarchar](50) NULL,
	[transaction_id] [varchar](100) NULL,
	[amount] [decimal](18, 2) NOT NULL,
	[status] [nvarchar](50) DEFAULT ('Pending'),
	[paid_at] [datetime] NULL
);
GO

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Shipping')
CREATE TABLE [dbo].[Shipping](
	[id] [int] IDENTITY(1,1) PRIMARY KEY,
	[order_id] [int] NULL,
	[address_id] [int] NULL,
	[carrier] [nvarchar](100) NULL,
	[tracking_code] [varchar](100) NULL,
	[weight_grams] [int] NULL,
	[status] [nvarchar](50) DEFAULT ('Picking')
);
GO

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Supplier')
CREATE TABLE [dbo].[Supplier](
	[id] [int] IDENTITY(1,1) PRIMARY KEY,
	[name] [nvarchar](200) NOT NULL,
	[contact_info] [nvarchar](max) NULL
);
GO

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Purchase_Order')
CREATE TABLE [dbo].[Purchase_Order](
	[id] [int] IDENTITY(1,1) PRIMARY KEY,
	[supplier_id] [int] NULL,
	[created_by] [int] NULL,
	[status] [nvarchar](50) DEFAULT ('Draft'),
	[total_cost] [decimal](18, 2) DEFAULT ((0)),
	[expected_delivery_date] [datetime] NULL,
	[created_at] [datetime] DEFAULT (GETDATE())
);
GO

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Purchase_Order_Item')
CREATE TABLE [dbo].[Purchase_Order_Item](
	[id] [int] IDENTITY(1,1) PRIMARY KEY,
	[po_id] [int] NULL,
	[variant_id] [int] NULL,
	[quantity] [int] NOT NULL CHECK ([quantity]>(0)),
	[unit_cost] [decimal](18, 2) NOT NULL
);
GO

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Return_Request')
CREATE TABLE [dbo].[Return_Request](
	[id] [int] IDENTITY(1,1) PRIMARY KEY,
	[order_id] [int] NULL,
	[customer_id] [int] NULL,
	[handled_by] [int] NULL,
	[reason] [nvarchar](max) NULL,
	[status] [nvarchar](50) DEFAULT ('Pending'),
	[refund_amount] [decimal](18, 2) DEFAULT ((0)),
	[created_at] [datetime] DEFAULT (GETDATE())
);
GO

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Review')
CREATE TABLE [dbo].[Review](
	[id] [int] IDENTITY(1,1) PRIMARY KEY,
	[product_id] [int] NULL,
	[customer_id] [int] NULL,
	[order_id] [int] NULL,
	[rating] [int] NULL CHECK ([rating]>=(1) AND [rating]<=(5)),
	[comment] [nvarchar](max) NULL,
	[is_edited] [bit] DEFAULT ((0)),
	[created_at] [datetime] DEFAULT (GETDATE())
);
GO

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Cart')
CREATE TABLE [dbo].[Cart](
	[id] [int] IDENTITY(1,1) PRIMARY KEY,
	[user_id] [int] NULL,
	[session_id] [varchar](100) NULL,
	[updated_at] [datetime] DEFAULT (GETDATE())
);
GO

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Cart_Item')
CREATE TABLE [dbo].[Cart_Item](
	[id] [int] IDENTITY(1,1) PRIMARY KEY,
	[cart_id] [int] NULL,
	[variant_id] [int] NULL,
	[quantity] [int] NOT NULL DEFAULT ((1))
);
GO

-- ==========================================
-- 3. TẠO CÁC RÀNG BUỘC UNIQUE (NẾU CHƯA CÓ)
-- ==========================================
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'UQ_Category_Slug')
ALTER TABLE [dbo].[Category] ADD CONSTRAINT UQ_Category_Slug UNIQUE NONCLUSTERED ([slug]);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'UQ_Order_Code')
ALTER TABLE [dbo].[Order] ADD CONSTRAINT UQ_Order_Code UNIQUE NONCLUSTERED ([order_code]);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'UQ_Product_Slug')
ALTER TABLE [dbo].[Product] ADD CONSTRAINT UQ_Product_Slug UNIQUE NONCLUSTERED ([slug]);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'UQ_ProductVariant_SKU')
ALTER TABLE [dbo].[Product_Variant] ADD CONSTRAINT UQ_ProductVariant_SKU UNIQUE NONCLUSTERED ([sku_code]);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'UQ_Promotion_Code')
ALTER TABLE [dbo].[Promotion] ADD CONSTRAINT UQ_Promotion_Code UNIQUE NONCLUSTERED ([code]);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'UQ_User_Email')
ALTER TABLE [dbo].[User] ADD CONSTRAINT UQ_User_Email UNIQUE NONCLUSTERED ([email]);
GO

-- ==========================================
-- 4. TẠO CÁC KHÓA NGOẠI (FOREIGN KEYS)
-- ==========================================
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_User_Role') ALTER TABLE [dbo].[User] ADD CONSTRAINT FK_User_Role FOREIGN KEY([role_id]) REFERENCES [dbo].[Role] ([id]);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_Address_User') ALTER TABLE [dbo].[Address_Book] ADD CONSTRAINT FK_Address_User FOREIGN KEY([user_id]) REFERENCES [dbo].[User] ([id]);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_Customer_User') ALTER TABLE [dbo].[Customer_Profile] ADD CONSTRAINT FK_Customer_User FOREIGN KEY([user_id]) REFERENCES [dbo].[User] ([id]);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_Category_Parent') ALTER TABLE [dbo].[Category] ADD CONSTRAINT FK_Category_Parent FOREIGN KEY([parent_id]) REFERENCES [dbo].[Category] ([id]);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_Product_Brand') ALTER TABLE [dbo].[Product] ADD CONSTRAINT FK_Product_Brand FOREIGN KEY([brand_id]) REFERENCES [dbo].[Brand] ([id]);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_Product_Category') ALTER TABLE [dbo].[Product] ADD CONSTRAINT FK_Product_Category FOREIGN KEY([category_id]) REFERENCES [dbo].[Category] ([id]);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_Image_Product') ALTER TABLE [dbo].[Product_Image] ADD CONSTRAINT FK_Image_Product FOREIGN KEY([product_id]) REFERENCES [dbo].[Product] ([id]);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_Variant_Product') ALTER TABLE [dbo].[Product_Variant] ADD CONSTRAINT FK_Variant_Product FOREIGN KEY([product_id]) REFERENCES [dbo].[Product] ([id]);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_Order_Customer') ALTER TABLE [dbo].[Order] ADD CONSTRAINT FK_Order_Customer FOREIGN KEY([customer_id]) REFERENCES [dbo].[User] ([id]);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_Order_Employee') ALTER TABLE [dbo].[Order] ADD CONSTRAINT FK_Order_Employee FOREIGN KEY([employee_id]) REFERENCES [dbo].[User] ([id]);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_Order_Promotion') ALTER TABLE [dbo].[Order] ADD CONSTRAINT FK_Order_Promotion FOREIGN KEY([promotion_id]) REFERENCES [dbo].[Promotion] ([id]);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_OrderDetail_Order') ALTER TABLE [dbo].[Order_Detail] ADD CONSTRAINT FK_OrderDetail_Order FOREIGN KEY([order_id]) REFERENCES [dbo].[Order] ([id]);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_OrderDetail_Variant') ALTER TABLE [dbo].[Order_Detail] ADD CONSTRAINT FK_OrderDetail_Variant FOREIGN KEY([variant_id]) REFERENCES [dbo].[Product_Variant] ([id]);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_Payment_Order') ALTER TABLE [dbo].[Payment] ADD CONSTRAINT FK_Payment_Order FOREIGN KEY([order_id]) REFERENCES [dbo].[Order] ([id]);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_Shipping_Address') ALTER TABLE [dbo].[Shipping] ADD CONSTRAINT FK_Shipping_Address FOREIGN KEY([address_id]) REFERENCES [dbo].[Address_Book] ([id]);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_Shipping_Order') ALTER TABLE [dbo].[Shipping] ADD CONSTRAINT FK_Shipping_Order FOREIGN KEY([order_id]) REFERENCES [dbo].[Order] ([id]);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_PO_Supplier') ALTER TABLE [dbo].[Purchase_Order] ADD CONSTRAINT FK_PO_Supplier FOREIGN KEY([supplier_id]) REFERENCES [dbo].[Supplier] ([id]);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_PO_Creator') ALTER TABLE [dbo].[Purchase_Order] ADD CONSTRAINT FK_PO_Creator FOREIGN KEY([created_by]) REFERENCES [dbo].[User] ([id]);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_POItem_PO') ALTER TABLE [dbo].[Purchase_Order_Item] ADD CONSTRAINT FK_POItem_PO FOREIGN KEY([po_id]) REFERENCES [dbo].[Purchase_Order] ([id]);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_POItem_Variant') ALTER TABLE [dbo].[Purchase_Order_Item] ADD CONSTRAINT FK_POItem_Variant FOREIGN KEY([variant_id]) REFERENCES [dbo].[Product_Variant] ([id]);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_Return_Order') ALTER TABLE [dbo].[Return_Request] ADD CONSTRAINT FK_Return_Order FOREIGN KEY([order_id]) REFERENCES [dbo].[Order] ([id]);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_Return_Customer') ALTER TABLE [dbo].[Return_Request] ADD CONSTRAINT FK_Return_Customer FOREIGN KEY([customer_id]) REFERENCES [dbo].[User] ([id]);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_Review_Product') ALTER TABLE [dbo].[Review] ADD CONSTRAINT FK_Review_Product FOREIGN KEY([product_id]) REFERENCES [dbo].[Product] ([id]);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_Review_Customer') ALTER TABLE [dbo].[Review] ADD CONSTRAINT FK_Review_Customer FOREIGN KEY([customer_id]) REFERENCES [dbo].[User] ([id]);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_Cart_User') ALTER TABLE [dbo].[Cart] ADD CONSTRAINT FK_Cart_User FOREIGN KEY([user_id]) REFERENCES [dbo].[User] ([id]);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_CartItem_Cart') ALTER TABLE [dbo].[Cart_Item] ADD CONSTRAINT FK_CartItem_Cart FOREIGN KEY([cart_id]) REFERENCES [dbo].[Cart] ([id]);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_CartItem_Variant') ALTER TABLE [dbo].[Cart_Item] ADD CONSTRAINT FK_CartItem_Variant FOREIGN KEY([variant_id]) REFERENCES [dbo].[Product_Variant] ([id]);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_InvTrans_Variant') ALTER TABLE [dbo].[Inventory_Transaction] ADD CONSTRAINT FK_InvTrans_Variant FOREIGN KEY([variant_id]) REFERENCES [dbo].[Product_Variant] ([id]);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_InvTrans_User') ALTER TABLE [dbo].[Inventory_Transaction] ADD CONSTRAINT FK_InvTrans_User FOREIGN KEY([user_id]) REFERENCES [dbo].[User] ([id]);
GO


-- ==========================================
-- 5. CHÈN DỮ LIỆU (IDEMPOTENT - KHÔNG TRÙNG LẶP)
-- ==========================================

-- BẢNG ROLE
SET IDENTITY_INSERT [dbo].[Role] ON;
IF NOT EXISTS (SELECT 1 FROM [dbo].[Role] WHERE id=1) INSERT [dbo].[Role] ([id], [name], [permissions]) VALUES (1, N'Admin', N'["ALL"]');
IF NOT EXISTS (SELECT 1 FROM [dbo].[Role] WHERE id=2) INSERT [dbo].[Role] ([id], [name], [permissions]) VALUES (2, N'Manager', N'["PRODUCT","INVENTORY","EMPLOYEE"]');
IF NOT EXISTS (SELECT 1 FROM [dbo].[Role] WHERE id=3) INSERT [dbo].[Role] ([id], [name], [permissions]) VALUES (3, N'Staff', N'["PRODUCT","INVENTORY"]');
IF NOT EXISTS (SELECT 1 FROM [dbo].[Role] WHERE id=4) INSERT [dbo].[Role] ([id], [name], [permissions]) VALUES (4, N'Customer', N'["ORDER","REVIEW"]');
SET IDENTITY_INSERT [dbo].[Role] OFF;
GO

-- BẢNG BRAND
SET IDENTITY_INSERT [dbo].[Brand] ON;
IF NOT EXISTS (SELECT 1 FROM [dbo].[Brand] WHERE id=1) INSERT [dbo].[Brand] ([id], [name], [logo_url]) VALUES (1, N'Adidas', NULL);
IF NOT EXISTS (SELECT 1 FROM [dbo].[Brand] WHERE id=2) INSERT [dbo].[Brand] ([id], [name], [logo_url]) VALUES (2, N'Asics', NULL);
IF NOT EXISTS (SELECT 1 FROM [dbo].[Brand] WHERE id=3) INSERT [dbo].[Brand] ([id], [name], [logo_url]) VALUES (3, N'Birkenstock', NULL);
IF NOT EXISTS (SELECT 1 FROM [dbo].[Brand] WHERE id=4) INSERT [dbo].[Brand] ([id], [name], [logo_url]) VALUES (4, N'Biti''s', NULL);
IF NOT EXISTS (SELECT 1 FROM [dbo].[Brand] WHERE id=5) INSERT [dbo].[Brand] ([id], [name], [logo_url]) VALUES (5, N'Converse', NULL);
IF NOT EXISTS (SELECT 1 FROM [dbo].[Brand] WHERE id=6) INSERT [dbo].[Brand] ([id], [name], [logo_url]) VALUES (6, N'Crocs', NULL);
IF NOT EXISTS (SELECT 1 FROM [dbo].[Brand] WHERE id=7) INSERT [dbo].[Brand] ([id], [name], [logo_url]) VALUES (7, N'New Balance', NULL);
IF NOT EXISTS (SELECT 1 FROM [dbo].[Brand] WHERE id=8) INSERT [dbo].[Brand] ([id], [name], [logo_url]) VALUES (8, N'Nike', NULL);
IF NOT EXISTS (SELECT 1 FROM [dbo].[Brand] WHERE id=9) INSERT [dbo].[Brand] ([id], [name], [logo_url]) VALUES (9, N'Puma', NULL);
IF NOT EXISTS (SELECT 1 FROM [dbo].[Brand] WHERE id=10) INSERT [dbo].[Brand] ([id], [name], [logo_url]) VALUES (10, N'Vans', NULL);
IF NOT EXISTS (SELECT 1 FROM [dbo].[Brand] WHERE id=11) INSERT [dbo].[Brand] ([id], [name], [logo_url]) VALUES (11, N'Balenciaga', NULL);
SET IDENTITY_INSERT [dbo].[Brand] OFF;
GO

-- BẢNG CATEGORY
SET IDENTITY_INSERT [dbo].[Category] ON;
IF NOT EXISTS (SELECT 1 FROM [dbo].[Category] WHERE id=6) INSERT [dbo].[Category] ([id], [parent_id], [name], [slug]) VALUES (6, NULL, N'Sneaker', N'sneaker');
IF NOT EXISTS (SELECT 1 FROM [dbo].[Category] WHERE id=7) INSERT [dbo].[Category] ([id], [parent_id], [name], [slug]) VALUES (7, NULL, N'Running', N'running');
IF NOT EXISTS (SELECT 1 FROM [dbo].[Category] WHERE id=8) INSERT [dbo].[Category] ([id], [parent_id], [name], [slug]) VALUES (8, NULL, N'Sandal & Clog', N'sandal-clog');
IF NOT EXISTS (SELECT 1 FROM [dbo].[Category] WHERE id=9) INSERT [dbo].[Category] ([id], [parent_id], [name], [slug]) VALUES (9, NULL, N'Skateboarding', N'skateboarding');
IF NOT EXISTS (SELECT 1 FROM [dbo].[Category] WHERE id=10) INSERT [dbo].[Category] ([id], [parent_id], [name], [slug]) VALUES (10, NULL, N'Slide', N'slide');
SET IDENTITY_INSERT [dbo].[Category] OFF;
GO

-- BẢNG PROMOTION
SET IDENTITY_INSERT [dbo].[Promotion] ON;
IF NOT EXISTS (SELECT 1 FROM [dbo].[Promotion] WHERE id=1) INSERT [dbo].[Promotion] ([id], [code], [type], [discount_value], [min_order_value], [usage_limit], [current_usage], [start_date], [end_date]) VALUES (1, N'TEST1775085343923', N'PERCENT', 15.00, 0.00, 100, 1, '2026-04-02', '2026-05-02');
SET IDENTITY_INSERT [dbo].[Promotion] OFF;
GO

-- BẢNG PRODUCT
SET IDENTITY_INSERT [dbo].[Product] ON;
IF NOT EXISTS (SELECT 1 FROM [dbo].[Product] WHERE id=1) INSERT [dbo].[Product] ([id], [brand_id], [name], [slug], [description], [base_price], [gender]) VALUES (1, 8, N'Nike Air Force 1 ''07', N'nike-air-force-1-07', N'Biểu tượng văn hóa đại chúng.', 2939000.00, N'Nam');
IF NOT EXISTS (SELECT 1 FROM [dbo].[Product] WHERE id=2) INSERT [dbo].[Product] ([id], [brand_id], [name], [slug], [description], [base_price], [gender]) VALUES (2, 1, N'Adidas Ultraboost Light', N'adidas-ultraboost-light', N'Đệm Boost êm ái nhất.', 5200000.00, N'Nữ');
IF NOT EXISTS (SELECT 1 FROM [dbo].[Product] WHERE id=3) INSERT [dbo].[Product] ([id], [brand_id], [name], [slug], [description], [base_price], [gender]) VALUES (3, 6, N'Crocs Classic Clog', N'crocs-classic-clog', N'Thoải mái và bền bỉ.', 1290000.00, N'Unisex');
IF NOT EXISTS (SELECT 1 FROM [dbo].[Product] WHERE id=4) INSERT [dbo].[Product] ([id], [brand_id], [name], [slug], [description], [base_price], [gender]) VALUES (4, 10, N'Vans Old Skool Classic', N'vans-old-skool', N'Giày trượt ván kinh điển.', 1950000.00, N'Unisex');
IF NOT EXISTS (SELECT 1 FROM [dbo].[Product] WHERE id=5) INSERT [dbo].[Product] ([id], [brand_id], [name], [slug], [description], [base_price], [gender]) VALUES (5, 5, N'Converse Chuck 70 High Top', N'converse-chuck-70', N'Phong cách Vintage cổ điển.', 2200000.00, N'Unisex');
IF NOT EXISTS (SELECT 1 FROM [dbo].[Product] WHERE id=18) INSERT [dbo].[Product] ([id], [brand_id], [name], [slug], [base_price], [gender]) VALUES (18, 11, N'Balenciaga Triple S', N'balenciaga-triple-s', 25000000.00, N'Nữ');
IF NOT EXISTS (SELECT 1 FROM [dbo].[Product] WHERE id=19) INSERT [dbo].[Product] ([id], [brand_id], [name], [slug], [base_price], [gender]) VALUES (19, 2, N'Asics Gel-Kayano 29', N'asics-gel-kayano-29', 3500000.00, N'Nam');
IF NOT EXISTS (SELECT 1 FROM [dbo].[Product] WHERE id=20) INSERT [dbo].[Product] ([id], [brand_id], [name], [slug], [base_price], [gender]) VALUES (20, 3, N'Birkenstock Arizona', N'birkenstock-arizona', 2800000.00, N'Nữ');
IF NOT EXISTS (SELECT 1 FROM [dbo].[Product] WHERE id=21) INSERT [dbo].[Product] ([id], [brand_id], [name], [slug], [base_price], [gender]) VALUES (21, 4, N'Bitis Hunter X', N'bitis-hunter-x', 1200000.00, N'Nam');
IF NOT EXISTS (SELECT 1 FROM [dbo].[Product] WHERE id=22) INSERT [dbo].[Product] ([id], [brand_id], [name], [slug], [base_price], [gender]) VALUES (22, 9, N'Puma Suede Classic', N'puma-suede-classic', 1800000.00, N'Nam');
IF NOT EXISTS (SELECT 1 FROM [dbo].[Product] WHERE id=23) INSERT [dbo].[Product] ([id], [brand_id], [name], [slug], [base_price], [gender]) VALUES (23, 7, N'New Balance 550', N'new-balance-550', 2500000.00, N'Nam');
SET IDENTITY_INSERT [dbo].[Product] OFF;
GO

-- BẢNG PRODUCT_VARIANT (Sử dụng kỹ thuật SELECT chèn gộp để code ngắn mà vẫn Idempotent)
SET IDENTITY_INSERT [dbo].[Product_Variant] ON;

INSERT INTO [dbo].[Product_Variant] ([id], [product_id], [sku_code], [size], [color], [stock_quantity])
SELECT id, product_id, sku_code, size, color, stock_quantity FROM (
    VALUES 
    (1, 1, N'NK-AF1-38-W', 38.0, N'Trắng (White)', 15),
    (2, 1, N'NK-AF1-39-W', 39.0, N'Trắng (White)', 13),
    (3, 1, N'NK-AF1-40-W', 40.0, N'Trắng (White)', 13),
    (4, 1, N'NK-AF1-41-W', 41.0, N'Trắng (White)', 12),
    (5, 1, N'NK-AF1-42-W', 42.0, N'Trắng (White)', 16),
    (6, 1, N'NK-AF1-43-W', 43.0, N'Trắng (White)', 17),
    (7, 1, N'NK-AF1-44-W', 44.0, N'Trắng (White)', 13),
    (8, 2, N'AD-UB-40-B', 40.0, N'Đen (Core Black)', 11),
    (9, 2, N'AD-UB-41-B', 41.0, N'Đen (Core Black)', 19),
    (10, 2, N'AD-UB-42-B', 42.0, N'Đen (Core Black)', 11),
    (11, 2, N'AD-UB-43-B', 43.0, N'Đen (Core Black)', 16),
    (12, 2, N'AD-UB-44-B', 44.0, N'Đen (Core Black)', 16),
    (13, 2, N'AD-UB-45-B', 45.0, N'Đen (Core Black)', 20),
    (14, 3, N'CR-CL-36-NV', 36.0, N'Xanh Navy (Navy)', 20),
    (15, 3, N'CR-CL-37-NV', 37.0, N'Xanh Navy (Navy)', 13),
    (16, 3, N'CR-CL-38-NV', 38.0, N'Xanh Navy (Navy)', 10),
    (21, 4, N'VN-OS-36-BW', 36.0, N'Đen Trắng (Black White)', 16),
    (22, 4, N'VN-OS-37-BW', 37.0, N'Đen Trắng (Black White)', 10),
    (27, 5, N'CV-C70-37-PAR', 37.0, N'Màu Kem (Parchment)', 16),
    (28, 5, N'CV-C70-38-PAR', 38.0, N'Màu Kem (Parchment)', 17),
    (33, 1, N'NK-AF1-38-B', 38.0, N'Đen (Black)', 16),
    (34, 1, N'NK-AF1-39-B', 39.0, N'Đen (Black)', 10),
    (40, 2, N'AD-UB-40-W', 40.0, N'Trắng (Cloud White)', 15),
    (41, 2, N'AD-UB-41-W', 41.0, N'Trắng (Cloud White)', 15),
    (52, 5, N'CV-C70-37-BLK', 37.0, N'Đen (Black)', 11),
    (53, 5, N'CV-C70-38-BLK', 38.0, N'Đen (Black)', 19),
    (58, 3, N'CR-CL-36-WHT', 36.0, N'Trắng (White)', 16),
    (59, 3, N'CR-CL-37-WHT', 37.0, N'Trắng (White)', 13),
    (65, 3, N'CR-CL-36-BLK', 36.0, N'Đen (Black)', 8),
    (66, 3, N'CR-CL-37-BLK', 37.0, N'Đen (Black)', 18),
    (72, 3, N'CR-CL-36-PNK', 36.0, N'Hồng (Pink)', 16),
    (79, 1, N'NK-AF1-38-BLU', 38.0, N'Xanh Dương (Blue)', 5),
    (86, 1, N'NK-AF1-38-RED', 38.0, N'Đỏ (Red)', 13),
    (93, 2, N'AD-UB-40-GRY', 40.0, N'Xám (Grey)', 22),
    (99, 4, N'VN-OS-36-RED', 36.0, N'Đỏ (Red)', 19),
    (105, 18, N'BL-TS-39-WHT', 39.0, N'Trắng (White)', 3),
    (109, 19, N'AS-GK29-40-BLU', 40.0, N'Xanh Biển (Blue)', 14),
    (113, 20, N'BK-AZ-37-BRN', 37.0, N'Nâu (Mocha)', 16),
    (117, 21, N'BT-HX-39-BLK', 39.0, N'Đen Cam (Black Orange)', 25),
    (122, 22, N'PM-SC-38-BLK', 38.0, N'Đen Trắng (Black White)', 14),
    (126, 23, N'NB-550-38-WGR', 38.0, N'Trắng Xanh Lá (White Green)', 15),
    (131, 18, N'BL-TS-39-BLK', 39.0, N'Đen (Black)', 30),
    (143, 19, N'AS-GK29-40-BKW', 40.0, N'Đen Trắng (Black White)', 25),
    (155, 20, N'BK-AZ-37-BLK', 37.0, N'Đen (Black)', 15),
    (167, 21, N'BT-HX-39-WHT', 39.0, N'Trắng Xám (White Grey)', 32),
    (182, 22, N'PM-SC-38-RED', 38.0, N'Đỏ (Red)', 24),
    (194, 23, N'NB-550-38-WBK', 38.0, N'Trắng Đen (White Black)', 30)
) AS tmp(id, product_id, sku_code, size, color, stock_quantity)
WHERE NOT EXISTS (SELECT 1 FROM [dbo].[Product_Variant] pv WHERE pv.id = tmp.id);

SET IDENTITY_INSERT [dbo].[Product_Variant] OFF;
GO

-- BẢNG USER (Tài khoản mẫu)
SET IDENTITY_INSERT [dbo].[User] ON;
IF NOT EXISTS (SELECT 1 FROM [dbo].[User] WHERE id=34) INSERT [dbo].[User] ([id], [role_id], [email], [password_hash], [phone]) VALUES (34, 4, N'102230221@sv1.dut.udn.vn', N'102230221', N'0789695329');
IF NOT EXISTS (SELECT 1 FROM [dbo].[User] WHERE id=40) INSERT [dbo].[User] ([id], [role_id], [email], [password_hash], [phone]) VALUES (40, 1, N'102240124@sv1.dut.udn.vn', N'102240124', N'0359287193');
IF NOT EXISTS (SELECT 1 FROM [dbo].[User] WHERE id=42) INSERT [dbo].[User] ([id], [role_id], [email], [password_hash], [phone]) VALUES (42, 2, N'102240133@sv1.dut.udn.vn', N'102240133', N'0971563287');
IF NOT EXISTS (SELECT 1 FROM [dbo].[User] WHERE id=44) INSERT [dbo].[User] ([id], [role_id], [email], [password_hash], [phone]) VALUES (44, 3, N'102240145@sv1.dut.udn.vn', N'102240145', N'0383667173');
SET IDENTITY_INSERT [dbo].[User] OFF;
GO

-- BẢNG CUSTOMER PROFILE
IF NOT EXISTS (SELECT 1 FROM [dbo].[Customer_Profile] WHERE user_id=34) INSERT [dbo].[Customer_Profile] ([user_id], [full_name], [reward_points]) VALUES (34, N'Phan Nguyễn Yến Trinh', 0);
USE [ShopGiayDB];
GO

-- ==========================================
-- 1. BƠM FULL DATA: USER (33 Tài khoản)
-- ==========================================
SET IDENTITY_INSERT [dbo].[User] ON;
INSERT INTO [dbo].[User] ([id], [role_id], [email], [password_hash], [phone])
SELECT id, role_id, email, password_hash, phone FROM (
    VALUES
    (34, 4, '102230221@sv1.dut.udn.vn', '102230221', '0789695329'),
    (35, 4, '102230406@sv1.dut.udn.vn', '102230406', '0886044354'),
    (36, 4, '102230407@sv1.dut.udn.vn', '102230407', '0886237164'),
    (37, 4, '102240015@sv1.dut.udn.vn', '102240015', '0328912922'),
    (38, 4, '102240072@sv1.dut.udn.vn', '102240072', '0385394717'),
    (39, 4, '102240073@sv1.dut.udn.vn', '102240073', '0368789576'),
    (40, 1, '102240124@sv1.dut.udn.vn', '102240124', '0359287193'),
    (41, 4, '102240130@sv1.dut.udn.vn', '102240130', '0905383132'),
    (42, 2, '102240133@sv1.dut.udn.vn', '102240133', '0971563287'),
    (43, 3, '102240135@sv1.dut.udn.vn', '102240135', '0766606051'),
    (44, 3, '102240145@sv1.dut.udn.vn', '102240145', '0383667173'),
    (45, 3, '102240152@sv1.dut.udn.vn', '102240152', '0835723919'),
    (46, 4, '102240180@sv1.dut.udn.vn', '102240180', '0773680845'),
    (47, 4, '102240181@sv1.dut.udn.vn', '102240181', '0827036227'),
    (48, 4, '102240182@sv1.dut.udn.vn', '102240182', '0368275453'),
    (49, 4, '102240187@sv1.dut.udn.vn', '102240187', '0911261259'),
    (50, 4, '102240188@sv1.dut.udn.vn', '102240188', '0935683216'),
    (51, 4, '102240189@sv1.dut.udn.vn', '102240189', '0777468294'),
    (52, 4, '102240215@sv1.dut.udn.vn', '102240215', '0787709173'),
    (53, 4, '102240217@sv1.dut.udn.vn', '102240217', '0867592216'),
    (54, 4, '102240222@sv1.dut.udn.vn', '102240222', '0935532814'),
    (55, 4, '102240223@sv1.dut.udn.vn', '102240223', '0396828545'),
    (56, 4, '102240228@sv1.dut.udn.vn', '102240228', '0827595686'),
    (57, 4, '102240229@sv1.dut.udn.vn', '102240229', '0817876421'),
    (58, 4, '102240231@sv1.dut.udn.vn', '102240231', '0819502371'),
    (59, 4, '102240422@sv1.dut.udn.vn', '102240422', '0779446158'),
    (60, 4, '102240423@sv1.dut.udn.vn', '102240423', '0327217308'),
    (61, 4, '102240424@sv1.dut.udn.vn', '102240424', '0928367641'),
    (62, 4, '102240425@sv1.dut.udn.vn', '102240425', '0583046186'),
    (63, 4, '102240426@sv1.dut.udn.vn', '102240426', '0564593551'),
    (64, 4, '102240258@sv1.dut.udn.vn', '102240258', '0388495742'),
    (65, 4, '102240264@sv1.dut.udn.vn', '102240264', '0787626137'),
    (66, 4, '102240268@sv1.dut.udn.vn', '102240268', '0763615519')
) AS tmp(id, role_id, email, password_hash, phone)
WHERE NOT EXISTS (SELECT 1 FROM [dbo].[User] u WHERE u.id = tmp.id);
SET IDENTITY_INSERT [dbo].[User] OFF;
GO

-- ==========================================
-- 2. BƠM FULL DATA: CUSTOMER PROFILE (33 Hồ sơ)
-- ==========================================
INSERT INTO [dbo].[Customer_Profile] ([user_id], [full_name], [reward_points])
SELECT user_id, full_name, reward_points FROM (
    VALUES
    (34, N'Phan Nguyễn Yến Trinh', 0),
    (35, N'Soundavong Cho', 0),
    (36, N'Homnabounlath Hutsany', 0),
    (37, N'Lê Ngọc Tuấn Anh', 0),
    (38, N'Võ Ngọc Cư', 0),
    (39, N'Nguyễn Lê Đình Diệu', 0),
    (40, N'Trần Hoài An', 0),
    (41, N'Phan Thanh Duy', 0),
    (42, N'Nguyễn Bá Giàu', 0),
    (43, N'Nguyễn Hoàng Hiếu', 0),
    (44, N'Trần Kim Lanh', 0),
    (45, N'Huỳnh Hiếu Nghĩa', 0),
    (46, N'Trần Quang Bách', 0),
    (47, N'Nguyễn Minh Chiến', 0),
    (48, N'Nguyễn Văn Cường', 0),
    (49, N'Võ Văn Đạt', 0),
    (50, N'Huỳnh Đức Hà', 0),
    (51, N'Nguyễn Văn Hoàng Hiệp', 0),
    (52, N'Ngô Lê Anh Quân', 0),
    (53, N'Hoàng Đức Quyền', 0),
    (54, N'Đinh Văn Tiên', 0),
    (55, N'Trần Nhật Tiến', 0),
    (56, N'Nguyễn Đình Trường', 0),
    (57, N'Phạm Quốc Tuấn', 0),
    (58, N'Ngô Văn Việt', 0),
    (59, N'Trần Phạm Gia Bảo', 0),
    (60, N'Chansina Khammalay', 0),
    (61, N'Nenhouangmala Xaychaluen', 0),
    (62, N'Phongsy Phoumanat', 0),
    (63, N'Nguyễn Hữu Trọng', 0),
    (64, N'Nguyễn Đoàn Chí Kiệt', 0),
    (65, N'Trần Văn Minh', 0),
    (66, N'Lê Văn Nin', 0)
) AS tmp(user_id, full_name, reward_points)
WHERE NOT EXISTS (SELECT 1 FROM [dbo].[Customer_Profile] cp WHERE cp.user_id = tmp.user_id);
GO

-- ==========================================
-- 3. FIX LỖI DATA (QUAN TRỌNG)
-- Trỏ lại các ID sản phẩm bị NULL trong bảng Product_Variant
-- ==========================================
UPDATE [dbo].[Product_Variant] 
SET product_id = 18 WHERE product_id IS NULL AND sku_code LIKE 'BL-TS%';

UPDATE [dbo].[Product_Variant] 
SET product_id = 19 WHERE product_id IS NULL AND sku_code LIKE 'AS-GK29%';

UPDATE [dbo].[Product_Variant] 
SET product_id = 20 WHERE product_id IS NULL AND sku_code LIKE 'BK-AZ%';

UPDATE [dbo].[Product_Variant] 
SET product_id = 21 WHERE product_id IS NULL AND sku_code LIKE 'BT-HX%';

UPDATE [dbo].[Product_Variant] 
SET product_id = 22 WHERE product_id IS NULL AND sku_code LIKE 'PM-SC%';

UPDATE [dbo].[Product_Variant] 
SET product_id = 23 WHERE product_id IS NULL AND sku_code LIKE 'NB-550%';
GO