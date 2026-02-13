import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainFrame() {
        setTitle("Планировщик питания");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Создание модулей
        WeeklyPlannerPanel weeklyPlanner = new WeeklyPlannerPanel();
        ShoppingListPanel shoppingList = new ShoppingListPanel();
        RecipeGeneratorPanel recipeGenerator = new RecipeGeneratorPanel();

        mainPanel.add(weeklyPlanner, "PLANNER");
        mainPanel.add(shoppingList, "SHOPPING");
        mainPanel.add(recipeGenerator, "RECIPES");

        // Создание меню
        JMenuBar menuBar = new JMenuBar();
        JMenu modulesMenu = new JMenu("Модули");

        JMenuItem plannerItem = new JMenuItem("Планировщик на неделю");
        plannerItem.addActionListener(e -> cardLayout.show(mainPanel, "PLANNER"));
        plannerItem.setIcon(new ImageIcon());

        JMenuItem shoppingItem = new JMenuItem("Независимый список покупок");
        shoppingItem.addActionListener(e -> cardLayout.show(mainPanel, "SHOPPING"));
        shoppingItem.setIcon(new ImageIcon());

        JMenuItem recipesItem = new JMenuItem("Генератор рецептов");
        recipesItem.addActionListener(e -> cardLayout.show(mainPanel, "RECIPES"));
        recipesItem.setIcon(new ImageIcon());

        modulesMenu.add(plannerItem);
        modulesMenu.add(shoppingItem);
        modulesMenu.addSeparator();
        modulesMenu.add(recipesItem);

        menuBar.add(modulesMenu);

        // Добавляем пункт "О программе"
        JMenu helpMenu = new JMenu("Помощь");
        JMenuItem aboutItem = new JMenuItem("О программе");
        aboutItem.addActionListener(e ->
                JOptionPane.showMessageDialog(this,
                        "Планировщик питания на неделю\nВерсия 2.0\n\nНезависимые модули:\n" +
                                "- Планировщик на неделю\n- Список покупок\n- Генератор рецептов",
                        "О программе", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
        add(mainPanel);
        setVisible(true);
    }
}