import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MainTest {
    public static void main(String[] args) {

        DataRetriever dataRetriever = new DataRetriever();

        // ===========================
        // Tester la récupération d'un ingrédient
        // ===========================
        try {
            int ingredientId = 1; // à adapter selon ta base
            Ingredient ingredient = dataRetriever.findIngredientById(ingredientId);
            System.out.println("Ingrédient récupéré : " + ingredient.getName()
                    + ", prix : " + ingredient.getPrice());

            System.out.println("Mouvements de stock :");
            for (StockMouvement sm : ingredient.getStockMovementList()) {
                System.out.println(" - " + sm.getMouvementType()
                        + " " + sm.getQuantity()
                        + " " + sm.getUnit()
                        + " le " + sm.getCreaction_datetime());
            }

        } catch (Exception e) {
            System.err.println("Erreur récupération ingrédient : " + e.getMessage());
        }

        // ===========================
        //Tester l'ajout d'un nouvel ingrédient
        // ===========================
        try {
            Ingredient newIngredient = new Ingredient();
            newIngredient.setName("Voanj");
            newIngredient.setPrice(2.5);
            newIngredient.setCategory(CategoryEnum.VEGETABLE);

            List<StockMouvement> movements = new ArrayList<>();
            StockMouvement sm1 = new StockMouvement();
            sm1.setId((int) (System.currentTimeMillis() % Integer.MAX_VALUE));
            sm1.setQuantity(100);
            sm1.setMouvementType(MouvementType.IN);
            sm1.setUnit(UnitType.KG);
            sm1.setCreaction_datetime(LocalDate.now());
            movements.add(sm1);

            newIngredient.setStockMovementList(movements);

            Ingredient savedIngredient = dataRetriever.saveIngredient(newIngredient);
            System.out.println("Ingrédient ajouté avec ID : " + savedIngredient.getId());

        } catch (Exception e) {
            System.err.println("Erreur ajout ingrédient : " + e.getMessage());
        }

        // ===========================
        // Tester la création d'une commande
        // ===========================
        try {
            Dish dish = new Dish();
            dish.setId(1);
            dish.setName("Salade");
            dish.setPrice(10.0);
            dish.setDishType(DishTypeEnum.MAIN);
            dish.setIngredients(new ArrayList<>());
            dish.getIngredients().add(dataRetriever.findIngredientById(1));

            DishOrder dishOrder = new DishOrder();
            dishOrder.setDish(dish);
            dishOrder.setQuantity(2);

            Order order = new Order();
            order.setReference("ORD-" + System.currentTimeMillis());
            order.setCreationDate(Instant.now());
            List<DishOrder> dishOrders = new ArrayList<>();
            dishOrders.add(dishOrder);
            order.setDishOrders(dishOrders);

            Order savedOrder = dataRetriever.saveOrder(order);
            System.out.println("Commande enregistrée avec ID : " + savedOrder.getId());

        } catch (Exception e) {
            System.err.println("Erreur création commande : " + e.getMessage());
        }

        // ===========================
        //Tester le calcul de stock à une date donnée
        // ===========================
        try {
            int ingredientId = 1; // à adapter
            LocalDate date = LocalDate.now();
            double stockValue = dataRetriever.getStockValueAt(ingredientId, date);
            System.out.println("Stock de l'ingrédient " + ingredientId + " au " + date + " : " + stockValue);
        } catch (Exception e) {
            System.err.println("Erreur calcul stock : " + e.getMessage());
        }

    }
}