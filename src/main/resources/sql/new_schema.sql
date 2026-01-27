
-- Pour Enum
CREATE TYPE dish_type AS ENUM ('START', 'MAIN', 'DESSERT');

CREATE TYPE ingredient_category AS ENUM (
    'VEGETABLE',
    'MEAT',
    'DAIRY',
    'FRUIT',
    'GRAIN'
);

CREATE TYPE unit_type AS ENUM ('PCS', 'KG', 'L');
CREATE TYPE mouvement_type AS ENUM ('IN', 'OUT');

-- Pour Dish
CREATE TABLE dish (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    dish_type dish_type NOT NULL,
    selling_price NUMERIC
);


-- Pour Ingredient
CREATE TABLE ingredient (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price NUMERIC NOT NULL,
    category ingredient_category NOT NULL
);

CREATE TABLE stockmovement (
    id_stock INT PRIMARY KEY,
    id_ingredient INT NOT NULL REFERENCES ingredient(id),
    quantity NUMERIC NOT NULL,
    type mouvement_type NOT NULL,
    unit unit_type NOT NULL,
    creation_datetime DATE NOT NULL
);

CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    reference VARCHAR(20) UNIQUE NOT NULL,
    creation_datetime TIMESTAMP NOT NULL
);

CREATE TABLE dish_order (
    id SERIAL PRIMARY KEY,
    order_id INT NOT NULL REFERENCES orders(id),
    dish_id INT NOT NULL REFERENCES dish(id),
    quantity INT NOT NULL
);



-- Pour les jointures
CREATE TABLE dish_ingredient (
    id SERIAL PRIMARY KEY,
    id_dish INT NOT NULL,
    id_ingredient INT NOT NULL,
    quantity_required NUMERIC NOT NULL,
    unit unit_type NOT NULL,

    FOREIGN KEY (id_dish) REFERENCES dish(id),
    FOREIGN KEY (id_ingredient) REFERENCES ingredient(id)
);


-- Les data
INSERT INTO dish_ingredient (id, id_dish, id_ingredient, quantity_required, unit) VALUES
(1, 1, 1, 0.20, 'KG'),
(2, 1, 2, 0.15, 'KG'),
(3, 2, 3, 1.00, 'KG'),
(4, 4, 4, 0.30, 'KG'),
(5, 4, 5, 0.20, 'KG');


-- MAJ des prix
UPDATE dish SET selling_price = 3500 WHERE id = 1;
UPDATE dish SET selling_price = 12000 WHERE id = 2;
UPDATE dish SET selling_price = NULL WHERE id = 3;
UPDATE dish SET selling_price = 8000 WHERE id = 4;
UPDATE dish SET selling_price = NULL WHERE id = 5;