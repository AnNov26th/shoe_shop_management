USE [ShopGiayDB];
GO

-- 1. Thêm cột 'expires_at' (Hạn chót giữ chỗ) vào bảng chi tiết giỏ hàng
IF COL_LENGTH('Cart_Item', 'expires_at') IS NULL
BEGIN
    ALTER TABLE [dbo].[Cart_Item] ADD [expires_at] DATETIME NULL;
END
GO

-- 2. Tạo Máy Hút Bụi (Stored Procedure): Tự động trả kho và xóa hàng hết hạn
CREATE OR ALTER PROCEDURE sp_ClearExpiredCart
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Trả lại số lượng tồn kho cho các đôi giày bị giam quá 5 phút
    UPDATE pv
    SET pv.stock_quantity = pv.stock_quantity + ci.quantity
    FROM [dbo].[Product_Variant] pv
    JOIN [dbo].[Cart_Item] ci ON pv.id = ci.variant_id
    JOIN [dbo].[Cart] c ON ci.cart_id = c.id
    WHERE ci.expires_at IS NOT NULL 
      AND ci.expires_at <= GETDATE() 
      AND c.user_id IS NOT NULL; -- (Chỉ áp dụng cho user đã đăng nhập)

    -- Đuổi các món hàng đó ra khỏi giỏ
    DELETE ci
    FROM [dbo].[Cart_Item] ci
    JOIN [dbo].[Cart] c ON ci.cart_id = c.id
    WHERE ci.expires_at IS NOT NULL 
      AND ci.expires_at <= GETDATE() 
      AND c.user_id IS NOT NULL;
END
GO

USE [ShopGiayDB];
GO
SELECT id, full_name, role_id FROM [User]

USE [ShopGiayDB];
GO

-- Thêm cột Trạng thái thanh toán (Mặc định là Chưa thanh toán)
IF COL_LENGTH('Order', 'status') IS NULL
BEGIN
    ALTER TABLE [dbo].[Order] ADD [status] NVARCHAR(50) DEFAULT N'Chưa thanh toán';
END
GO

-- Thêm cột ID Khách hàng để biết ai đặt đơn này
IF COL_LENGTH('Order', 'customer_id') IS NULL
BEGIN
    ALTER TABLE [dbo].[Order] ADD [customer_id] INT NULL;
END
GO

USE [ShopGiayDB];
GO

-- Thêm cột số điện thoại khách hàng vào bảng Order
IF COL_LENGTH('Order', 'customer_phone') IS NULL
BEGIN
    ALTER TABLE [dbo].[Order] ADD [customer_phone] VARCHAR(20) NULL;
END
GO