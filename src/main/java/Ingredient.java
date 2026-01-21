public class Ingredient {
    private final int id;
    private final String name;
    private final Double price;
    private final CategoryEnum category;
    private final Dish dish;

    public Ingredient(int id, String name, Double price, CategoryEnum category, Dish dish) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.dish = dish;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public CategoryEnum getCategory() {
        return category;
    }

    public Dish getDish() {
        return dish;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "name='" + name + '\'' +
                '}';
    }
}
