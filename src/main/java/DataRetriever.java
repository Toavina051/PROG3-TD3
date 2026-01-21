import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    private final DBConnection connection;

    public DataRetriever(DBConnection connection) {
        this.connection = connection;
    }

    public Dish findDishById(Integer id) {

        String sql = "SELECT dish.id, dish.name, dish.dish_type, dish.price, ingredient.id, ingredient.name, ingredient.price, ingredient.category FROM dish LEFT JOIN ingredient ON dish.id = ingredient.id_dish WHERE dish.id = ?";

        try (Connection dbconnection = connection.getDBConnection();
             PreparedStatement statement = dbconnection.prepareStatement(sql)) {

            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();

            Dish dish = null;
            List<Ingredient> ingredients = new ArrayList<>();

            while (result.next()) {

                if (dish == null) {
                    dish = new Dish(
                            result.getInt(1),
                            result.getString(2),
                            DishTypeEnum.valueOf(result.getString(3)),
                            ingredients,
                            result.getDouble(4)
                    );
                }

                int ingredientId = result.getInt(5);
                if (!result.wasNull()) {
                    ingredients.add(new Ingredient(
                            ingredientId,
                            result.getString(6),
                            result.getDouble(7),
                            CategoryEnum.valueOf(result.getString(8)),
                            null
                    ));
                }
            }

            if (dish == null) {
                throw new RuntimeException("Dish not found");
            }

            return dish;

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<Ingredient> findIngredients(int page, int size) {

        List<Ingredient> ingredients = new ArrayList<>();
        int offset = (page - 1) * size;

        String sql = "SELECT ingredient.id, ingredient.name, ingredient.price, ingredient.category FROM ingredient ORDER BY ingredient.id LIMIT ? OFFSET ?";

        try (Connection dbconnection = connection.getDBConnection();
             PreparedStatement statement = dbconnection.prepareStatement(sql)) {

            statement.setInt(1, size);
            statement.setInt(2, offset);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                ingredients.add(new Ingredient(
                        result.getInt(1),
                        result.getString(2),
                        result.getDouble(3),
                        CategoryEnum.valueOf(result.getString(4)),
                        null
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error in findIngredients: " + e.getMessage());
        }
        if (ingredients.isEmpty()) {
            System.out.println("List vide");
        }
        return ingredients;
    }

    public List<Ingredient> createIngredients(List<Ingredient> newIngredients) {
        String sql = "INSERT INTO ingredient(name, price, category, id_dish) VALUES (?, ?, ?, ?) returning id";

        try (Connection dbconnection = connection.getDBConnection()) {
            for (Ingredient ingredient : newIngredients) {
                try (PreparedStatement insertStatement = dbconnection.prepareStatement(sql)) {
                    insertStatement.setInt(1, ingredient.getId());
                    insertStatement.setString(2, ingredient.getName());
                    insertStatement.setDouble(3, ingredient.getPrice());
                    insertStatement.setString(4, ingredient.getCategory().name());
                    insertStatement.executeUpdate();
                }
            }
            return newIngredients;

        } catch (Exception e) {
            throw new RuntimeException("Error in createIngredients: " + e.getMessage());
        }
    }

    public Dish saveDish(Dish dishToSave) {

        String insertDishSql = "INSERT INTO dish(name, dish_type, price) VALUES (?, ?, ?) RETURNING id";

        String updateDishSql = "UPDATE dish SET name = ?, dish_type = ?, price = ? WHERE id = ?";

        String deleteRelationSql = "DELETE FROM ingredient WHERE id_dish = ?";

        String insertRelationSql = "INSERT INTO ingredient(id_dish, ingredient.id) VALUES (?, ?)";
        try (Connection dbconnection = connection.getDBConnection()) {
            dbconnection.setAutoCommit(false);
            int dishId = dishToSave.getId();
            if (dishId == 0) {
                try (PreparedStatement statement = dbconnection.prepareStatement(insertDishSql)) {
                    statement.setString(1, dishToSave.getName());
                    statement.setString(2, dishToSave.getDishType().name());
                    statement.setObject(3, dishToSave.getPrice());

                    ResultSet result = statement.executeQuery();
                    result.next();
                    dishId = result.getInt(1);
                }
            }
            else {
                try (PreparedStatement statement = dbconnection.prepareStatement(updateDishSql)) {
                    statement.setString(1, dishToSave.getName());
                    statement.setString(2, dishToSave.getDishType().name());
                    statement.setObject(3, dishToSave.getPrice());
                    statement.executeUpdate();
                }
            }
            try (PreparedStatement statement = dbconnection.prepareStatement(deleteRelationSql)) {
                statement.setInt(1, dishId);
                statement.executeUpdate();
            }
            for (Ingredient ingredient : dishToSave.getIngredients()) {
                try (PreparedStatement statement = dbconnection.prepareStatement(insertRelationSql)) {
                    statement.setInt(1, dishId);
                    statement.setInt(2, ingredient.getId());
                    statement.executeUpdate();
                }
            }

            dbconnection.commit();

            return new Dish(
                    dishId,
                    dishToSave.getName(),
                    dishToSave.getDishType(),
                    dishToSave.getIngredients(),
                    dishToSave.getPrice()
            );


        } catch (Exception e) {
            throw new RuntimeException("Error in saveDish: " + e.getMessage());
        }
    }

    public List<Dish> findDishByIngredientName(String ingredientName) {

        List<Dish> dishes = new ArrayList<>();

        String sql = "SELECT dish.id, dish.name, dish.dish_type, ingredient.id, ingredient.name, ingredient.price, ingredient.category FROM dish JOIN ingredient ON dish.id = ingredient.id_dish WHERE ingredient.name ILIKE ? ORDER BY dish.id";
        try (
                Connection connection = this.connection.getDBConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, "%" + ingredientName + "%");

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    dishes.add(new Dish(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            DishTypeEnum.valueOf(resultSet.getString(3))
                    ));
                }
            }

            return dishes;

        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }


}
