CREATE TABLE todos (
  id SERIAl PRIMARY KEY,
  title VARCHAR(100) NOT NULL,
  description TEXT,
  done BOOLEAN,
  due_date DATE
);
