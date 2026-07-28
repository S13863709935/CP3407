INSERT INTO user (id, username, password, name, role)
VALUES
    (1, 'current-resident', 'test-only', 'Current Resident', 'USER'),
    (2, 'neighbour', 'test-only', 'Neighbour', 'USER');

INSERT INTO goods
    (id, name, price, content, address, date, status, category, user_id, sale_status, read_count)
VALUES
    (1, 'Camera', 150.00, 'Working camera', 'North', '2026-07-28', '通过', 'Electronics', 2, 'Listed', 10),
    (2, 'Hidden Camera', 100.00, 'Off shelf', 'North', '2026-07-28', '通过', 'Electronics', 2, 'Off-shelf', 20),
    (3, 'Pending Camera', 90.00, 'Awaiting review', 'North', '2026-07-28', '待审核', 'Electronics', 2, 'Listed', 30),
    (4, 'My Camera', 80.00, 'Owned by current resident', 'North', '2026-07-28', '通过', 'Electronics', 1, 'Listed', 40),
    (5, 'Bookshelf', 45.00, 'Wooden shelf', 'South', '2026-07-28', '通过', 'Furniture', 2, 'Listed', 5);
