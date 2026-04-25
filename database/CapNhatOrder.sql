USE [ShopGiayDB];
GO

-- 1. Đảm bảo bảng Order dùng tên cột customer_id (đồng bộ với DAO)
-- Nếu bảng đang dùng user_id, hãy chạy lệnh rename này:
-- EXEC sp_rename 'Order.user_id', 'customer_id', 'COLUMN';

-- 2. Thêm các cột còn thiếu nếu chưa có
IF COL_LENGTH('Order', 'customer_phone') IS NULL
    ALTER TABLE [dbo].[Order] ADD [customer_phone] VARCHAR(20) NULL;

IF COL_LENGTH('Order', 'status') IS NULL
    ALTER TABLE [dbo].[Order] ADD [status] NVARCHAR(50) DEFAULT N'Chưa thanh toán';

IF COL_LENGTH('Order', 'total_amount') IS NULL
    ALTER TABLE [dbo].[Order] ADD [total_amount] DECIMAL(18, 2) NULL;
GO