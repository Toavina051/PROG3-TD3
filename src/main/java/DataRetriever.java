import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataRetriever {
    public Dish findDishById(Integer id) {
        String sql = """
            SELECT id, name, dish_type, price
            FROM dish
            WHERE id = ?
        """;

        try (Connection connection = new DBConnection().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();

            if (!result.next()) {
                throw new RuntimeException("Dish not found: " + id);
            }

            Dish dish = new Dish();
            dish.setId(result.getInt("id"));
            dish.setName(result.getString("name"));
            dish.setDishType(DishTypeEnum.valueOf(result.getString("dish_type")));
            dish.setPrice(result.getObject("price") == null ? null : result.getDouble("price"));

            dish.setIngredients(findIngredientByDishId(id));

            return dish;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public Dish saveDish(Dish dish) {

        String sql = """
        INSERT INTO dish (name, dish_type, price)
        VALUES (?, ?::dish_type, ?)
        RETURNING id
    """;

        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dish.getName());
            ps.setString(2, dish.getDishType().name());
            ps.setDouble(3, dish.getPrice());

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                dish.setId(rs.getInt(1));
            }

            saveDishIngredients(conn, dish);

            return findDishById(dish.getId());

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void saveDishIngredients(Connection conn, Dish dish) throws SQLException {

        String sql = """
        INSERT INTO dish_ingredient (id_dish, id_ingredient, quantity_required, unit)
        VALUES (?, ?, ?, ?::unit_type)
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Ingredient ingredient : dish.getIngredients()) {
                ps.setInt(1, dish.getId());
                ps.setInt(2, ingredient.getId());
                ps.setDouble(3, ingredient.getQuantity());
                ps.setString(4, "PCS");
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }






    public void deleteDishIngredientsByDishId(Integer dishId, Connection conn) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(
                "DELETE FROM dish_ingredient WHERE id_dish = ?")) {
            statement.setInt(1, dishId);
            statement.executeUpdate();
        }
    }

    public List<Ingredient> saveIngredients(List<Ingredient> ingredients) {

        if (ingredients == null || ingredients.isEmpty()) {
            return List.of();
        }

        String selectSql = """
        SELECT id, price, category
        FROM ingredient
        WHERE name = ?
    """;

        String insertSql = """
        INSERT INTO ingredient (name, category, price)
        VALUES (?, ?::ingredient_category, ?)
        RETURNING id
    """;

        try (Connection connection = new DBConnection().getConnection()) {

            for (Ingredient ingredient : ingredients) {
                try (PreparedStatement ps = connection.prepareStatement(selectSql)) {
                    ps.setString(1, ingredient.getName());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            ingredient.setId(rs.getInt("id"));
                            ingredient.setPrice(rs.getDouble("price"));
                            ingredient.setCategory(
                                    CategoryEnum.valueOf(rs.getString("category"))
                            );
                            continue;
                        }
                    }
                }

                try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
                    ps.setString(1, ingredient.getName());
                    ps.setString(2, ingredient.getCategory().name());
                    ps.setDouble(3, ingredient.getPrice());

                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        ingredient.setId(rs.getInt(1));
                    }
                }
            }

            return ingredients;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void detachIngredients(Connection conn, Integer dishId, List<Ingredient> ingredients)
            throws SQLException {
        if (ingredients == null || ingredients.isEmpty()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM dish_ingredient WHERE id_dish = ?")) {
                ps.setInt(1, dishId);
                ps.executeUpdate();
            }
            return;
        }

        String baseSql = """
                    DELETE FROM dish_ingredient WHERE id_dish = ?
                """;

        String inClause = ingredients.stream()
                .map(i -> "?")
                .collect(Collectors.joining(","));

        String sql = String.format(baseSql, inClause);

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dishId);
            int index = 2;
            for (Ingredient ingredient : ingredients) {
                ps.setInt(index++, ingredient.getId());
            }
            ps.executeUpdate();
        }
    }

    private void attachIngredients(Connection conn, Integer dishId, List<Ingredient> ingredients)
            throws SQLException {

        if (ingredients == null || ingredients.isEmpty()) {
            return;
        }

        String attachSql = """
                    UPDATE ingredient
                    SET id_dish = ?
                    WHERE id = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(attachSql)) {
            for (Ingredient ingredient : ingredients) {
                ps.setInt(1, dishId);
                ps.setInt(2, ingredient.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private List<Ingredient> findIngredientByDishId(int dishId) {

        List<Ingredient> ingredients = new ArrayList<>();

        String sql = """
        SELECT i.id,
               i.name,
               i.category,
               i.price,
               di.quantity_required
        FROM ingredient i
        JOIN dish_ingredient di ON di.id_ingredient = i.id
        WHERE di.id_dish = ?
    """;

        DBConnection dbConnection = new DBConnection();

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, dishId);

            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setId(result.getInt("id"));
                ingredient.setName(result.getString("name"));
                ingredient.setCategory(CategoryEnum.valueOf(result.getString("category")));
                ingredient.setPrice(result.getDouble("price"));
                ingredient.setQuantity(result.getDouble("quantity_required"));

                ingredients.add(ingredient);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return ingredients;
    }


    // TD4

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

        try (Connection connection = new DBConnection().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, ingredientId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                StockMouvement sm = new StockMouvement();
                sm.setId(rs.getInt("id_stock"));
                sm.setQuantity(rs.getDouble("quantity"));
                sm.setMouvementType(
                        MouvementType.valueOf(rs.getString("type"))
                );
                sm.setUnit(UnitType.valueOf(rs.getString("unit")));
                sm.setCreaction_datetime(
                        rs.getDate("creation_datetime").toLocalDate()
                );

                movements.add(sm);
            }

            return movements;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
