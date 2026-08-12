DELETE FROM comments;
DELETE FROM bookings;
DELETE FROM items;
DELETE FROM users;

ALTER TABLE comments ALTER COLUMN id RESTART WITH 1;
ALTER TABLE bookings ALTER COLUMN id RESTART WITH 1;
ALTER TABLE items ALTER COLUMN id RESTART WITH 1;
ALTER TABLE users ALTER COLUMN id RESTART WITH 1;

INSERT INTO users (name, email) VALUES
('Алексей Иванов', 'alexey.ivanov@mail.ru'),
('Мария Петрова', 'maria.petrova@yandex.ru'),
('Дмитрий Смирнов', 'dmitry.smirnov@gmail.com'),
('Елена Кузнецова', 'elena.kuznetsova@bk.ru'),
('Сергей Попов', 'sergey.popov@mail.ru');

INSERT INTO items (name, description, available, owner_id) VALUES
('Электрическая дрель', 'Мощная дрель с регулировкой скорости, 1200 Вт', true, 1),
('Набор отверток', 'Набор из 12 отверток с магнитными наконечниками', true, 1),
('Циркулярная пила', 'Дисковая пила для точных резов, 1800 Вт', false, 2),
('Шлифовальная машина', 'Ленточная шлифмашина для обработки древесины', true, 3),
('Перфоратор', 'Профессиональный перфоратор с SDS-патроном', true, 2),
('Угловая шлифмашина', 'Болгарка для резки металла и камня', false, 4),
('Аккумуляторный шуруповерт', 'Беспроводной шуруповерт 18 В', true, 5),
('Рубанок', 'Электрический рубанок для строгания досок', true, 3);

INSERT INTO comments (text, author_id, created, item_id) VALUES
('Отличная дрель! Очень мощная, справляется с любыми задачами.', 2, DATEADD('DAY', -2, CURRENT_TIMESTAMP), 1),
('Набор отверток просто супер! Все на месте, качество отличное.', 3, DATEADD('DAY', -1, CURRENT_TIMESTAMP), 2),
('Пила хорошая, но тяжеловата. Для профессионального использования — то что надо.', 4, DATEADD('DAY', -3, CURRENT_TIMESTAMP), 3),
('Шлифмашина работает тихо и плавно. Очень доволен покупкой.', 2, DATEADD('HOUR', -5, CURRENT_TIMESTAMP), 4),
('Перфоратор мощный, но шумноват. В целом рекомендую.', 5, DATEADD('HOUR', -12, CURRENT_TIMESTAMP), 5),
('Болгарка отличная, но диск нужно было положить в комплект.', 1, DATEADD('HOUR', -3, CURRENT_TIMESTAMP), 6),
('Шуруповерт очень удобный, батареи хватает надолго.', 3, DATEADD('DAY', -1, CURRENT_TIMESTAMP), 7),
('Рубанок настроен отлично, стружку снимает идеально.', 4, DATEADD('HOUR', -2, CURRENT_TIMESTAMP), 8),
('Дрель рекомендую всем! Уже два месяца пользуюсь, полет нормальный.', 5, DATEADD('DAY', -4, CURRENT_TIMESTAMP), 1),
('Отвертки магнитные — это спасение! Мелкие детали не теряются.', 1, DATEADD('HOUR', -6, CURRENT_TIMESTAMP), 2);

INSERT INTO bookings (start_date, end_date, item_id, booker, status) VALUES
('2026-08-15 10:00:00', '2026-08-17 18:00:00', 1, 2, 'APPROVED'),
('2026-08-20 09:00:00', '2026-08-22 17:00:00', 2, 3, 'WAITING'),
('2026-08-25 14:00:00', '2026-08-27 12:00:00', 3, 1, 'REJECTED'),
('2026-09-01 08:00:00', '2026-09-03 20:00:00', 4, 2, 'APPROVED'),
('2026-09-05 11:00:00', '2026-09-07 16:00:00', 5, 4, 'WAITING'),
('2026-09-10 13:00:00', '2026-09-12 19:00:00', 1, 3, 'CANCELED'),
('2026-09-15 07:00:00', '2026-09-17 21:00:00', 6, 5, 'APPROVED'),
('2026-09-20 12:00:00', '2026-09-22 15:00:00', 7, 1, 'WAITING'),
('2026-09-25 09:30:00', '2026-09-27 18:30:00', 8, 2, 'APPROVED'),
('2026-10-01 10:00:00', '2026-10-03 14:00:00', 2, 4, 'REJECTED');