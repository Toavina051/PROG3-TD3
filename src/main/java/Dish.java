import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Dish {

    private int id;
    private  String name;
    private  DishTypeEnum dishType;
    //    private final List<Ingredient> ingredients;
    private List<Ingredient> ingredients;
    private Double price;

    public Dish(int id, String name, DishTypeEnum dishType, Double price) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.price = price;
        this.ingredients = new ArrayList<Ingredient>();
    }

    public Dish() {

    }

    public int getId() { return id; }
    public String getName() { return name; }
    public DishTypeEnum getDishType() { return dishType; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public List<Ingredient> getIngredients() { return ingredients; }
    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients.clear();
        this.ingredients.addAll(ingredients);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDishType(DishTypeEnum dishType) {
        this.dishType = dishType;
    }

    public void addIngredient(Ingredient ingredient) {
        if (ingredient != null) this.ingredients.add(ingredient);
    }

    public double getDishCost() {
        return ingredients.stream()
                .mapToDouble(Ingredient::getPrice)
                .sum();
    }

    public double getGrossMargin() {

        if (price == null) {
            throw new RuntimeException(
                    "le prix de vente n'est pas encore défini"
            );
        }

        return price - getDishCost();
    }

    @Override
    public String toString() {
        return "Dish{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dishType=" + dishType +
                ", price=" + price +
                ", ingredients=" + ingredients +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dish dish)) return false;
        return id == dish.id &&
                Objects.equals(name, dish.name) &&
                dishType == dish.dishType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, dishType);
    }


}
