import java.util.ArrayList;
import java.util.List;

public class Dish {
    private int id;
    private String name;
    private DishTypeEnum dishType;
    private List<Ingredient>  ingredients;
    private Double price;

    public Dish(int id, String name, DishTypeEnum dishType, List<Ingredient> ingredients) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.ingredients = ingredients;
    }

    public Dish(int id, String name, DishTypeEnum dishType, List<Ingredient> ingredients, Double price) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.ingredients = ingredients;
        this.price = price;
    }

    public Dish(int id, String name, DishTypeEnum dishType) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public DishTypeEnum getDishType() {
        return dishType;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public Double getPrice() {
        return price;
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

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    /*public Double getDishPrice() {
            Double price = 0.0;
            int count = 0;
            while ( count != ingredients.size() ) {
                price += ingredients.getFirst().getPrice();
                count++;
            }
            return price;
        }*/
    public Double getDishCost(){
        return ingredients.stream()
                .mapToDouble(Ingredient::getPrice)
                .sum();
    }

    public Double getGrossMargin(){
        try{
            if (this.getPrice() == null){
                throw new RuntimeException("Prix de vente n'as pas encore de valeur");
            }
            return this.getPrice()-this.getDishCost();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "Dish{" +
                "name='" + name + '\'' +
                '}';
    }
}
