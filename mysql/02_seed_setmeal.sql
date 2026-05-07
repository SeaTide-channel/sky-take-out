SET NAMES utf8mb4;

USE `sky_take_out`;

SET FOREIGN_KEY_CHECKS = 0;

-- 步骤一（子表）：清空 setmeal_dish，重置 AUTO_INCREMENT（表结构由 01_sky.sql 保证）
TRUNCATE TABLE `setmeal_dish`;
ALTER TABLE `setmeal_dish` AUTO_INCREMENT = 1000;

-- 步骤二（主表）：清空 setmeal，重置 AUTO_INCREMENT
TRUNCATE TABLE `setmeal`;
ALTER TABLE `setmeal` AUTO_INCREMENT = 1000;

SET FOREIGN_KEY_CHECKS = 1;

-- 步骤三：写入套餐主数据
-- category_id 必须与 01_sky.sql 中 category 一致：type=2 为套餐分类
--   13 -> 人气套餐   15 -> 商务套餐
INSERT INTO `setmeal` (
  `id`, `category_id`, `name`, `price`, `status`, `description`, `image`,
  `create_time`, `update_time`, `create_user`, `update_user`
) VALUES
(101, 13, '双人饮品套餐', 15.00, 1, '王老吉2份与北冰洋1份', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/41bfcacf-7ad4-4927-8b26-df366553a94c.png', '2022-06-10 12:00:00', '2022-06-10 12:00:00', 1, 1),
(102, 13, '酸菜鱼主食套餐', 58.00, 1, '酸菜鱼米饭鸡蛋汤', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/4a9cefba-6a74-467e-9fde-6e687ea725d7.png', '2022-06-10 12:00:00', '2022-06-10 12:00:00', 1, 1),
(103, 15, '商务牛蛙四人餐', 258.00, 0, '停售示例', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/7694a5d8-7938-4e9d-8b9e-2075983a2e38.png', '2022-06-10 12:00:00', '2022-06-10 12:00:00', 1, 1);

-- 步骤四：写入 setmeal_dish（setmeal_id 对应步骤三；dish_id/name/price 与 01_sky.dish 对齐）
INSERT INTO `setmeal_dish` (`id`, `setmeal_id`, `dish_id`, `name`, `price`, `copies`) VALUES
(501, 101, 46, '王老吉', 6.00, 2),
(502, 101, 47, '北冰洋', 4.00, 1),
(503, 102, 51, '老坛酸菜鱼', 56.00, 1),
(504, 102, 49, '米饭', 2.00, 2),
(505, 102, 68, '鸡蛋汤', 4.00, 1),
(506, 103, 62, '金汤酸菜牛蛙', 88.00, 2),
(507, 103, 63, '香锅牛蛙', 88.00, 1),
(508, 103, 54, '清炒小油菜', 18.00, 1);
