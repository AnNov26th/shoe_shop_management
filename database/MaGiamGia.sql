USE [ShopGiayDB];
GO

-- Bật cho phép chèn ID thủ công vào bảng Promotion
SET IDENTITY_INSERT [dbo].[Promotion] ON;

INSERT INTO [dbo].[Promotion] ([id], [code], [type], [discount_value], [min_order_value], [max_discount_amount], [usage_limit], [current_usage], [start_date], [end_date])
SELECT id, code, type, discount_value, min_order_value, max_discount_amount, usage_limit, current_usage, CAST(start_date AS datetime), CAST(end_date AS datetime) FROM (
    VALUES 
    -- ============================================================
    -- LOẠI 1: GIẢM THEO PHẦN TRĂM (PERCENT) - Cần có Max Discount
    -- ============================================================
    (2, 'WELCOME10', 'PERCENT', 10.00, 0.00, 50000.00, 1000, 0, '2026-04-01', '2026-12-31'),
    (3, 'SNEAKER20', 'PERCENT', 20.00, 1000000.00, 200000.00, 500, 0, '2026-04-01', '2026-06-30'),
    (4, 'VIP30', 'PERCENT', 30.00, 2500000.00, 500000.00, 100, 0, '2026-04-01', '2026-12-31'),
    (5, 'FLASH50', 'PERCENT', 50.00, 1500000.00, 400000.00, 50, 0, '2026-04-24', '2026-04-30'),
    (6, 'STUDENT15', 'PERCENT', 15.00, 0.00, 100000.00, 2000, 0, '2026-04-01', '2026-09-01'),
    (7, 'PBL3VIP', 'PERCENT', 25.00, 500000.00, 300000.00, 99, 0, '2026-04-01', '2026-08-01'),
    (8, 'BIRTHDAY', 'PERCENT', 15.00, 0.00, 200000.00, 500, 0, '2026-01-01', '2026-12-31'),
    (9, 'BIGSALE', 'PERCENT', 40.00, 3000000.00, 1000000.00, 20, 0, '2026-04-24', '2026-12-31'),

    -- ============================================================
    -- LOẠI 2: GIẢM TIỀN MẶT (FIXED) - Không cần Max Discount (NULL)
    -- ============================================================
    (10, 'FREESHIP30K', 'FIXED', 30000.00, 300000.00, NULL, 1000, 0, '2026-04-01', '2026-12-31'),
    (11, 'GIAM50K', 'FIXED', 50000.00, 500000.00, NULL, 500, 0, '2026-04-01', '2026-06-30'),
    (12, 'GIAM100K', 'FIXED', 100000.00, 1000000.00, NULL, 300, 0, '2026-04-01', '2026-05-31'),
    (13, 'PAYDAY200K', 'FIXED', 200000.00, 2000000.00, NULL, 150, 0, '2026-04-25', '2026-05-05'),
    (14, 'BANANA50', 'FIXED', 50000.00, 0.00, NULL, 100, 0, '2026-04-01', '2026-12-31'),
    (15, 'HUGO100K', 'FIXED', 100000.00, 800000.00, NULL, 50, 0, '2026-04-01', '2026-12-31'),
    (16, 'SUMMERCOOL', 'FIXED', 80000.00, 800000.00, NULL, 400, 0, '2026-05-01', '2026-08-31')

) AS tmp(id, code, type, discount_value, min_order_value, max_discount_amount, usage_limit, current_usage, start_date, end_date)
WHERE NOT EXISTS (SELECT 1 FROM [dbo].[Promotion] p WHERE p.id = tmp.id);

SET IDENTITY_INSERT [dbo].[Promotion] OFF;
GO