public class DishIngredient {
    private Dish dish;
    private Ingredient ingredient;
    private Double quantity;

    public DishIngredient(Dish dish, Ingredient ingredient, Double quantity) {
        this.dish = dish;
        this.ingredient = ingredient;
        this.quantity = quantity;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "DishIngredient{" +
                "dish=" + dish +
                ", ingredient=" + ingredient +
                ", quantity=" + quantity +
                '}';
    }
}


