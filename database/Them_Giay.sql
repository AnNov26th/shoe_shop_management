USE [ShopGiayDB];
GO

-- =========================================================================
-- 1. THÊM 39 THƯƠNG HIỆU MỚI (Từ ID 12 đến 50)
-- =========================================================================
SET IDENTITY_INSERT [dbo].[Brand] ON;
INSERT INTO [dbo].[Brand] ([id], [name], [logo_url])
SELECT id, name, logo_url FROM (
    VALUES 
    (12, N'Reebok', NULL), (13, N'Under Armour', NULL), (14, N'Saucony', NULL), 
    (15, N'Fila', NULL), (16, N'Skechers', NULL), (17, N'Brooks', NULL), 
    (18, N'Hoka', NULL), (19, N'Salomon', NULL), (20, N'Mizuno', NULL), 
    (21, N'Timberland', NULL), (22, N'Dr. Martens', NULL), (23, N'Clarks', NULL), 
    (24, N'Merrell', NULL), (25, N'Columbia', NULL), (26, N'Onitsuka Tiger', NULL), 
    (27, N'Keds', NULL), (28, N'Superga', NULL), (29, N'DC Shoes', NULL), 
    (30, N'Ecco', NULL), (31, N'Geox', NULL), (32, N'Cole Haan', NULL), 
    (33, N'Aldo', NULL), (34, N'Camper', NULL), (35, N'Lacoste', NULL), 
    (36, N'Hugo Boss', NULL), (37, N'Tommy Hilfiger', NULL), (38, N'Calvin Klein', NULL), 
    (39, N'Guess', NULL), (40, N'Coach', NULL), (41, N'Michael Kors', NULL), 
    (42, N'MLB', NULL), (43, N'Ralph Lauren', NULL), (44, N'Gucci', NULL), 
    (45, N'Prada', NULL), (46, N'Louis Vuitton', NULL), (47, N'Dior', NULL), 
    (48, N'Chanel', NULL), (49, N'Hermes', NULL), (50, N'Fendi', NULL)
) AS tmp(id, name, logo_url)
WHERE NOT EXISTS (SELECT 1 FROM [dbo].[Brand] b WHERE b.id = tmp.id);
SET IDENTITY_INSERT [dbo].[Brand] OFF;
GO

-- =========================================================================
-- 2. THÊM 39 MẪU GIÀY ĐẠI DIỆN CHO TỪNG THƯƠNG HIỆU (Từ ID 24 đến 62)
-- =========================================================================
SET IDENTITY_INSERT [dbo].[Product] ON;
INSERT INTO [dbo].[Product] ([id], [brand_id], [name], [slug], [base_price], [gender])
SELECT id, brand_id, name, slug, base_price, gender FROM (
    VALUES 
    (24, 12, N'Reebok Classic Leather', 'reebok-classic', 1800000, N'Unisex'),
    (25, 13, N'Under Armour Curry 9', 'ua-curry-9', 3200000, N'Nam'),
    (26, 14, N'Saucony Jazz Original', 'saucony-jazz', 1500000, N'Unisex'),
    (27, 15, N'Fila Disruptor II', 'fila-disruptor', 1700000, N'Nữ'),
    (28, 16, N'Skechers D''Lites', 'skechers-dlites', 1400000, N'Nữ'),
    (29, 17, N'Brooks Ghost 14', 'brooks-ghost-14', 2800000, N'Nam'),
    (30, 18, N'Hoka Clifton 8', 'hoka-clifton-8', 3500000, N'Unisex'),
    (31, 19, N'Salomon Speedcross 5', 'salomon-speedcross-5', 3800000, N'Nam'),
    (32, 20, N'Mizuno Wave Rider 25', 'mizuno-wave-rider-25', 2900000, N'Nam'),
    (33, 21, N'Timberland 6-Inch Boot', 'timberland-6inch', 4500000, N'Nam'),
    (34, 22, N'Dr. Martens 1460', 'drmartens-1460', 4200000, N'Unisex'),
    (35, 23, N'Clarks Desert Boot', 'clarks-desert', 2500000, N'Nam'),
    (36, 24, N'Merrell Moab 2', 'merrell-moab', 2300000, N'Nam'),
    (37, 25, N'Columbia Newton Ridge', 'columbia-newton', 2100000, N'Unisex'),
    (38, 26, N'Onitsuka Tiger Mexico 66', 'onitsuka-mexico66', 2200000, N'Unisex'),
    (39, 27, N'Keds Champion', 'keds-champion', 1100000, N'Nữ'),
    (40, 28, N'Superga 2750 Cotu', 'superga-2750', 1300000, N'Unisex'),
    (41, 29, N'DC Shoes Court Graffik', 'dc-court', 1600000, N'Nam'),
    (42, 30, N'Ecco Soft 7', 'ecco-soft-7', 3500000, N'Nam'),
    (43, 31, N'Geox Nebula', 'geox-nebula', 3200000, N'Nam'),
    (44, 32, N'Cole Haan Zerogrand', 'colehaan-zerogrand', 4000000, N'Nam'),
    (45, 33, N'Aldo Kaelane', 'aldo-kaelane', 1800000, N'Nữ'),
    (46, 34, N'Camper Pelotas', 'camper-pelotas', 3800000, N'Unisex'),
    (47, 35, N'Lacoste Carnaby Evo', 'lacoste-carnaby', 2500000, N'Nam'),
    (48, 36, N'Hugo Boss Saturn', 'hugo-boss-saturn', 4800000, N'Nam'),
    (49, 37, N'Tommy Hilfiger Corporate', 'tommy-corporate', 2400000, N'Nam'),
    (50, 38, N'Calvin Klein Maya', 'ck-maya', 2700000, N'Nữ'),
    (51, 39, N'Guess Vice', 'guess-vice', 2100000, N'Nữ'),
    (52, 40, N'Coach Citysole', 'coach-citysole', 3900000, N'Unisex'),
    (53, 41, N'Michael Kors Keaton', 'mk-keaton', 3500000, N'Nữ'),
    (54, 42, N'MLB Big Ball Chunky', 'mlb-bigball', 2500000, N'Unisex'),
    (55, 43, N'Ralph Lauren Thorton', 'ralph-thorton', 1900000, N'Nam'),
    (56, 44, N'Gucci Ace Sneaker', 'gucci-ace', 18000000, N'Unisex'),
    (57, 45, N'Prada Cloudbust', 'prada-cloudbust', 22000000, N'Unisex'),
    (58, 46, N'Louis Vuitton Archlight', 'lv-archlight', 28000000, N'Nữ'),
    (59, 47, N'Dior B22 Sneaker', 'dior-b22', 26000000, N'Nam'),
    (60, 48, N'Chanel Trainer', 'chanel-trainer', 24000000, N'Nữ'),
    (61, 49, N'Hermes Bounce', 'hermes-bounce', 21000000, N'Unisex'),
    (62, 50, N'Fendi Match', 'fendi-match', 19000000, N'Unisex')
) AS tmp(id, brand_id, name, slug, base_price, gender)
WHERE NOT EXISTS (SELECT 1 FROM [dbo].[Product] p WHERE p.id = tmp.id);
SET IDENTITY_INSERT [dbo].[Product] OFF;
GO

-- =========================================================================
-- 3. TẠO 624 PHÂN LOẠI (VARIANTS) BẰNG CROSS JOIN
-- =========================================================================
-- 3.1 Khởi tạo bảng ảo chứa 4 Size
DECLARE @Sizes TABLE (size DECIMAL(4,1));
INSERT INTO @Sizes VALUES (39.0), (40.0), (41.0), (42.0);

-- 3.2 Khởi tạo bảng ảo chứa 4 Màu sắc chuẩn form ảnh của bro
DECLARE @Colors TABLE (color NVARCHAR(50), color_code VARCHAR(10));
INSERT INTO @Colors VALUES 
(N'Đen (Black)', 'BLK'), 
(N'Trắng (White)', 'WHT'), 
(N'Trắng Xanh Lá (White Green)', 'WGR'), 
(N'Xám Trắng (Grey)', 'GRY');

-- 3.3 Đẩy vào DB
INSERT INTO [dbo].[Product_Variant] (product_id, sku_code, size, color, stock_quantity, weight_grams)
SELECT 
    p.id AS product_id,
    -- Mã SKU: 4-CHỮ-CÁI-ĐẦU + SIZE + MÃ-MÀU
    UPPER(SUBSTRING(REPLACE(p.name, ' ', ''), 1, 4)) + '-' + CAST(CAST(s.size AS INT) AS VARCHAR) + '-' + c.color_code AS sku_code,
    s.size,
    c.color,
    -- Random tồn kho từ 10 đến 50 đôi
    ABS(CHECKSUM(NEWID()) % 41) + 10 AS stock_quantity, 
    0 AS weight_grams
FROM [dbo].[Product] p
CROSS JOIN @Sizes s
CROSS JOIN @Colors c
WHERE p.id >= 24 
  AND NOT EXISTS (
      SELECT 1 FROM [dbo].[Product_Variant] pv 
      WHERE pv.product_id = p.id AND pv.size = s.size AND pv.color = c.color
  );
GO