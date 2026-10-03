INSERT INTO app_users (username, email, password_hash, role) VALUES ('alice', 'alice@example.com', '7abdccbea8473767e91378e37850d296', 'CUSTOMER');
INSERT INTO app_users (username, email, password_hash, role) VALUES ('bob', 'bob@example.com', '2acba7f51acfd4fd5102ad090fc612ee', 'CUSTOMER');
INSERT INTO app_users (username, email, password_hash, role) VALUES ('clerk', 'clerk@example.com', 'ad4ac7fa40b0af2bae7374c57173f26c', 'STAFF');

INSERT INTO books (title, author, isbn, price, stock, genre) VALUES ('Clean Code', 'Robert C. Martin', '9780132350884', 33.50, 12, 'programming');
INSERT INTO books (title, author, isbn, price, stock, genre) VALUES ('Effective Java', 'Joshua Bloch', '9780134685991', 41.99, 8, 'programming');
INSERT INTO books (title, author, isbn, price, stock, genre) VALUES ('Dune', 'Frank Herbert', '9780441172719', 9.99, 40, 'scifi');
INSERT INTO books (title, author, isbn, price, stock, genre) VALUES ('The Hobbit', 'J. R. R. Tolkien', '9780547928227', 8.49, 25, 'fantasy');
INSERT INTO books (title, author, isbn, price, stock, genre) VALUES ('Refactoring', 'Martin Fowler', '9780134757599', 47.00, 5, 'programming');
INSERT INTO books (title, author, isbn, price, stock, genre) VALUES ('Neuromancer', 'William Gibson', '9780441569595', 7.99, 0, 'scifi');

INSERT INTO reviews (book_id, user_id, rating, comment) VALUES (1, 1, 5, 'Changed how I write methods');
INSERT INTO reviews (book_id, user_id, rating, comment) VALUES (1, 2, 4, 'Good but opinionated');
INSERT INTO reviews (book_id, user_id, rating, comment) VALUES (3, 1, 5, 'A classic');
