import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {
    public Ingredient findIngredientById(int ingredientId) {

        String sql = """
            SELECT id, name, price, category
            FROM ingredient
            WHERE id = ?
        """;

        Connection connection = new DBConnection().getConnection();

        try (connection;
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, ingredientId);
            ResultSet result = ps.executeQuery();

            if (!result.next()) {
                throw new RuntimeException("Ingredient not found (id=" + ingredientId + ")");
            }

            Ingredient ingredient = new Ingredient(
                    result.getInt("id"),
                    result.getString("name"),
                    result.getDouble("price"),
                    CategoryEnum.valueOf(result.getString("category")),
                    null
            );

            ingredient.setStockMovementList(
                    findStockMovementsByIngredientId(ingredientId)
            );

            return ingredient;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<StockMouvement> findStockMovementsByIngredientId(int ingredientId) {

        String sql = """
            SELECT id_stock,
                   quantity,
                   type,
                   unit,
                   creation_datetime
            FROM stockmovement
            WHERE id_ingredient = ?
            ORDER BY creation_datetime
        """;

        List<StockMouvement> movements = new ArrayList<>();

        Connection connection = new DBConnection().getConnection();

        try (connection;
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, ingredientId);
            ResultSet result = ps.executeQuery();

            while (result.next()) {

                StockMouvement sm = new StockMouvement();
                sm.setId(result.getInt("id_stock"));
                sm.setQuantity(result.getDouble("quantity"));
                sm.setMouvementType(
                        MouvementType.valueOf(result.getString("type"))
                );
                sm.setUnit(UnitType.valueOf(result.getString("unit")));
                sm.setCreaction_datetime(
                        result.getDate("creation_datetime").toLocalDate()
                );

                movements.add(sm);
            }

            return movements;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Ingredient saveIngredient(Ingredient ingredient) {

        Connection connection = new DBConnection().getConnection();

        try {
            connection.setAutoCommit(false);
            if (ingredient.getId() == 0) {
                String insertIngredient = """
                    INSERT INTO ingredient(name, price, category)
                    VALUES (?, ?, ?)
                    RETURNING id
                """;

                try (PreparedStatement ps = connection.prepareStatement(insertIngredient)) {
                    ps.setString(1, ingredient.getName());
                    ps.setDouble(2, ingredient.getPrice());
                    ps.setString(3, ingredient.getCategory().name());

                    ResultSet rs = ps.executeQuery();
                    rs.next();
                    ingredient.setId(rs.getInt(1));
                }
            }

            for (StockMouvement sm : ingredient.getStockMovementList()) {

                String insertMovement = """
                    INSERT INTO stockmovement
                    (id_stock, id_ingredient, quantity, type, unit, creation_datetime)
                    VALUES (?, ?, ?, ?::mouvement_type, ?::unit_type, ?)
                    ON CONFLICT (id_stock) DO NOTHING
                """;

                try (PreparedStatement ps = connection.prepareStatement(insertMovement)) {
                    ps.setInt(1, sm.getId());
                    ps.setInt(2, ingredient.getId());
                    ps.setDouble(3, sm.getQuantity());
                    ps.setString(4, sm.getMouvementType().name());
                    ps.setString(5, sm.getUnit().name());
                    ps.setDate(6, Date.valueOf(sm.getCreaction_datetime()));
                    ps.executeUpdate();
                }
            }

            connection.commit();
            return ingredient;

        } catch (Exception e) {
            try { connection.rollback(); } catch (SQLException ignored) {}
            throw new RuntimeException(e);

        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }

    public Order saveOrder(Order orderToSave) {

        String insertOrder = """
        INSERT INTO orders(reference, creation_datetime)
        VALUES (?, ?)
        RETURNING id
    """;

        String insertDishOrder = """
        INSERT INTO dish_order(order_id, dish_id, quantity)
        VALUES (?, ?, ?)
    """;

        String insertStockMovement = """
        INSERT INTO stockmovement
        (id_stock, id_ingredient, quantity, type, unit, creation_datetime)
        VALUES (?, ?, ?, 'OUT', 'KG', ?)
        ON CONFLICT (id_stock) DO NOTHING
    """;

        Connection connection = new DBConnection().getConnection();

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement ps = connection.prepareStatement(insertOrder)) {
                ps.setString(1, orderToSave.getReference());
                ps.setTimestamp(2, Timestamp.from(orderToSave.getCreationDate()));

                ResultSet rs = ps.executeQuery();
                rs.next();
                orderToSave.setId(rs.getInt(1));
            }

            for (DishOrder dishOrder : orderToSave.getDishOrders()) {

                try (PreparedStatement ps = connection.prepareStatement(insertDishOrder)) {
                    ps.setInt(1, orderToSave.getId());
                    ps.setInt(2, dishOrder.getDish().getId());
                    ps.setInt(3, dishOrder.getQuantity());
                    ps.executeUpdate();
                }

                for (Ingredient ingredient : dishOrder.getDish().getIngredients()) {

                    StockMouvement sm = new StockMouvement();
                    sm.setId((int) (System.currentTimeMillis() % Integer.MAX_VALUE));
                    sm.setIngredient(ingredient);
                    sm.setQuantity(dishOrder.getQuantity());
                    sm.setMouvementType(MouvementType.OUT);
                    sm.setUnit(UnitType.KG);
                    sm.setCreaction_datetime(orderToSave.getCreationDate().atZone(
                            java.time.ZoneId.systemDefault()).toLocalDate()
                    );

                    try (PreparedStatement ps = connection.prepareStatement(insertStockMovement)) {
                        ps.setInt(1, sm.getId());
                        ps.setInt(2, ingredient.getId());
                        ps.setDouble(3, sm.getQuantity());
                        ps.setDate(4, Date.valueOf(sm.getCreaction_datetime()));
                        ps.executeUpdate();
                    }
                }
            }

            connection.commit();
            return orderToSave;

        } catch (Exception e) {
            try { connection.rollback(); } catch (SQLException ignored) {}
            throw new RuntimeException(e);

        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }

    public Order findOrderByReference(String reference) {

        String sqlOrder = """
        SELECT id, reference, creation_datetime
        FROM orders
        WHERE reference = ?
    """;

        String sqlDishOrders = """
        SELECT d.id, d.name, d.dish_type, d.price,
               do.quantity
        FROM dish_order do
        JOIN dish d ON d.id = do.dish_id
        WHERE do.order_id = ?
    """;

        Connection connection = new DBConnection().getConnection();

        try (connection;
             PreparedStatement psOrder = connection.prepareStatement(sqlOrder)) {

            psOrder.setString(1, reference);
            ResultSet rsOrder = psOrder.executeQuery();

            if (!rsOrder.next()) {
                throw new RuntimeException("Order not found: " + reference);
            }

            Order order = new Order();
            order.setId(rsOrder.getInt("id"));
            order.setReference(rsOrder.getString("reference"));
            order.setCreationDate(rsOrder.getTimestamp("creation_datetime").toInstant());

            List<DishOrder> dishOrders = new ArrayList<>();

            try (PreparedStatement psDish = connection.prepareStatement(sqlDishOrders)) {
                psDish.setInt(1, order.getId());
                ResultSet rsDish = psDish.executeQuery();

                while (rsDish.next()) {

                    Dish dish = new Dish(
                            rsDish.getInt("id"),
                            rsDish.getString("name"),
                            DishTypeEnum.valueOf(rsDish.getString("dish_type")),
                            rsDish.getDouble("price")
                    );

                    DishOrder dishOrder = new DishOrder();
                    dishOrder.setDish(dish);
                    dishOrder.setQuantity(rsDish.getInt("quantity"));

                    dishOrders.add(dishOrder);
                }
            }

            order.setDishOrders(dishOrders);
            return order;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public double getStockValueAt(int ingredientId, LocalDate date) {

        Ingredient ingredient = findIngredientById(ingredientId);

        double stock = 0;

        for (StockMouvement sm : ingredient.getStockMovementList()) {
            if (!sm.getCreaction_datetime().isAfter(date)) {
                if (sm.getMouvementType() == MouvementType.IN) {
                    stock += sm.getQuantity();
                } else {
                    stock -= sm.getQuantity();
                }
            }
        }
        return stock;
    }
}