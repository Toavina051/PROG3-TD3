import java.util.List;

public class MainTest {
    public static void main(String[] args) {
        System.out.println("MAIN STARTED");

        DataRetriever dataRetriever = new DataRetriever();

        Ingredient laitue = new Ingredient();
        laitue.setName("Laitue");
        laitue.setCategory(CategoryEnum.VEGETABLE);
        laitue.setPrice(300.0);

        Ingredient tomate = new Ingredient();
        tomate.setName("Tomate");
        tomate.setCategory(CategoryEnum.VEGETABLE);
        tomate.setPrice(600.0);

        dataRetriever.saveIngredients(List.of(laitue, tomate));

        Dish salade = new Dish();
        salade.setName("Salade fraîche");
        salade.setDishType(DishTypeEnum.STARTER);
        salade.setPrice(8000.0);
        laitue.setQuantity(1.0);
        tomate.setQuantity(0.25);

        salade.setIngredients(List.of(laitue, tomate));

        Dish savedDish = dataRetriever.saveDish(salade);

        Dish fetchedDish = dataRetriever.findDishById(savedDish.getId());

        System.out.println("Plat récupéré :");
        System.out.println(fetchedDish);

        /*System.out.println("\nCoût du plat (ATTENDU = 450):");
        System.out.println(fetchedDish.getDishCost());

        System.out.println("\nMarge brute (ATTENDU = 7550):");
        System.out.println(fetchedDish.getGrossMargin());*/
    }
}