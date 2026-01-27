import java.time.LocalDate;

public class StockMouvement {

    private int id;
    private Ingredient ingredient;
    private MouvementType mouvementType;
    private double quantity;
    private UnitType unit;
    private LocalDate creaction_datetime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public MouvementType getMouvementType() {
        return mouvementType;
    }

    public void setMouvementType(MouvementType mouvementType) {
        this.mouvementType = mouvementType;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public UnitType getUnit() {
        return unit;
    }

    public void setUnit(UnitType unit) {
        this.unit = unit;
    }

    public LocalDate getCreaction_datetime() {
        return creaction_datetime;
    }

    public void setCreaction_datetime(LocalDate creaction_datetime) {
        this.creaction_datetime = creaction_datetime;
    }
}
