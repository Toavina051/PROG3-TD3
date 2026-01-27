import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Ingredient {

    private int id;
    private String name;
    private double price;
    private CategoryEnum category;

    private List<StockMouvement> stockMouvementList = new ArrayList<>();

    public Ingredient(int id, String name, double price, CategoryEnum category, Dish dish) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public Ingredient() {

    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public CategoryEnum getCategory() { return category; }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setCategory(CategoryEnum category) {
        this.category = category;
    }

    public void setStockMouvementList(List<StockMouvement> stockMouvementList) {
        this.stockMouvementList = stockMouvementList;
    }

    public List<StockMouvement> getStockMovementList() {
        return stockMouvementList;
    }

    public void setStockMovementList(List<StockMouvement> stockMouvementList) {
        this.stockMouvementList = stockMouvementList;
    }

    public double getStockValueAt(LocalDate date) {
        double stock = 0;

        for (StockMouvement sm : stockMouvementList) {
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

    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", category=" + category +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ingredient that)) return false;
        return id == that.id &&
                Double.compare(that.price, price) == 0 &&
                Objects.equals(name, that.name) &&
                category == that.category;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, category);
    }
}
