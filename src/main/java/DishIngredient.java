import java.util.Objects;

public class DishIngredient {
    private int id;
    private double requiredQuantity;
    private UnitType unit;
    private Dish dish;
    private Ingredient ingredient;

    public DishIngredient() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getRequiredQuantity() {
        return requiredQuantity;
    }

    public void setRequiredQuantity(double requiredQuantity) {
        this.requiredQuantity = requiredQuantity;
    }

    public UnitType getUnit() {
        return unit;
    }

    public void setUnit(UnitType unit) {
        this.unit = unit;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DishIngredient that = (DishIngredient) o;
        return id == that.id && Double.compare(requiredQuantity, that.requiredQuantity) == 0 && unit == that.unit && Objects.equals(dish, that.dish) && Objects.equals(ingredient, that.ingredient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, requiredQuantity, unit, dish, ingredient);
    }


    @Override
    public String toString() {
        return "DishIngredient{" +
                "id=" + id +
                ", requiredQuantity=" + requiredQuantity +
                ", unitType=" + unit +
                ", ingredient=" + ingredient.getName() +
                '}';
    }
}