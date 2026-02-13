import java.io.*;
import java.util.*;

public class DataManager {
    private static final String WEEKLY_PLAN_FILE = "weekly_plan.dat";
    private static final String SHOPPING_LIST_FILE = "shopping_list.dat";
    private static final String RECIPES_FILE = "recipes.dat";

    // Сохранение недельного плана
    public void saveWeeklyPlan(Map<String, List<Product>> weeklyPlan) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(WEEKLY_PLAN_FILE))) {
            oos.writeObject(new LinkedHashMap<>(weeklyPlan));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Загрузка недельного плана
    @SuppressWarnings("unchecked")
    public Map<String, List<Product>> loadWeeklyPlan() {
        File file = new File(WEEKLY_PLAN_FILE);
        if (!file.exists()) {
            return new LinkedHashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(file))) {
            return (Map<String, List<Product>>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new LinkedHashMap<>();
        }
    }

    // Сохранение списка покупок
    public void saveShoppingList(List<Product> shoppingList) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(SHOPPING_LIST_FILE))) {
            oos.writeObject(new ArrayList<>(shoppingList));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Загрузка списка покупок
    @SuppressWarnings("unchecked")
    public List<Product> loadShoppingList() {
        File file = new File(SHOPPING_LIST_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(file))) {
            return (List<Product>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Сохранение рецептов
    public void saveRecipes(Map<String, String[]> recipes) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(RECIPES_FILE))) {
            oos.writeObject(new HashMap<>(recipes));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Загрузка рецептов
    @SuppressWarnings("unchecked")
    public Map<String, String[]> loadRecipes() {
        File file = new File(RECIPES_FILE);
        if (!file.exists()) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(file))) {
            return (Map<String, String[]>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}