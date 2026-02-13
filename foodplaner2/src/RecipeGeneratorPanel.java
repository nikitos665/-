import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RecipeGeneratorPanel extends JPanel {
    private Map<String, String[]> recipes;
    private JTextArea recipeArea;
    private JComboBox<String> recipeComboBox;
    private DataManager dataManager;
    private DefaultListModel<String> recipeListModel;
    private JList<String> recipeList;

    public RecipeGeneratorPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        dataManager = new DataManager();
        recipes = dataManager.loadRecipes();

        if (recipes.isEmpty()) {
            initializeDefaultRecipes();
        }

        // Заголовок
        JLabel titleLabel = new JLabel("ГЕНЕРАТОР РЕЦЕПТОВ", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Разделим панель на две части
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(350);

        // Левая панель - список рецептов и добавление
        JPanel leftPanel = createLeftPanel();
        splitPane.setLeftComponent(leftPanel);

        // Правая панель - отображение рецепта
        JPanel rightPanel = createRightPanel();
        splitPane.setRightComponent(rightPanel);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        // Панель со списком рецептов
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.ORANGE, 2),
                "МОИ РЕЦЕПТЫ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                Color.ORANGE
        ));

        recipeListModel = new DefaultListModel<>();
        updateRecipeList();

        recipeList = new JList<>(recipeListModel);
        recipeList.setFont(new Font("Arial", Font.PLAIN, 14));
        recipeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        recipeList.setFixedCellHeight(30);

        JScrollPane listScrollPane = new JScrollPane(recipeList);
        listScrollPane.setPreferredSize(new Dimension(300, 300));
        listPanel.add(listScrollPane, BorderLayout.CENTER);

        // Кнопки для работы с рецептами
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton showButton = new JButton("📖 Показать рецепт");
        JButton randomButton = new JButton("🎲 Случайный");
        JButton deleteButton = new JButton("❌ Удалить");

        showButton.setBackground(new Color(70, 130, 180));
        showButton.setForeground(Color.WHITE);
        randomButton.setBackground(new Color(255, 140, 0));
        randomButton.setForeground(Color.WHITE);
        deleteButton.setBackground(new Color(220, 20, 60));
        deleteButton.setForeground(Color.WHITE);

        buttonPanel.add(showButton);
        buttonPanel.add(randomButton);
        buttonPanel.add(deleteButton);
        listPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(listPanel, BorderLayout.CENTER);

        // Панель добавления нового рецепта
        JPanel addPanel = createAddRecipePanel();
        panel.add(addPanel, BorderLayout.SOUTH);

        // Обработчики событий
        showButton.addActionListener(e -> {
            String selectedRecipe = recipeList.getSelectedValue();
            if (selectedRecipe != null) {
                displayRecipe(selectedRecipe);
            } else {
                JOptionPane.showMessageDialog(this, "Выберите рецепт из списка");
            }
        });

        randomButton.addActionListener(e -> {
            if (!recipeListModel.isEmpty()) {
                int randomIndex = new Random().nextInt(recipeListModel.size());
                String randomRecipe = recipeListModel.getElementAt(randomIndex);
                recipeList.setSelectedIndex(randomIndex);
                displayRecipe(randomRecipe);
            }
        });

        deleteButton.addActionListener(e -> {
            String selectedRecipe = recipeList.getSelectedValue();
            if (selectedRecipe != null) {
                int result = JOptionPane.showConfirmDialog(
                        this,
                        "Удалить рецепт \"" + selectedRecipe + "\"?",
                        "Подтверждение удаления",
                        JOptionPane.YES_NO_OPTION
                );

                if (result == JOptionPane.YES_OPTION) {
                    recipes.remove(selectedRecipe);
                    dataManager.saveRecipes(recipes);
                    updateRecipeList();
                    recipeArea.setText("");
                    JOptionPane.showMessageDialog(this, "Рецепт удален");
                }
            }
        });

        // Двойной клик по рецепту
        recipeList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    String selectedRecipe = recipeList.getSelectedValue();
                    if (selectedRecipe != null) {
                        displayRecipe(selectedRecipe);
                    }
                }
            }
        });

        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GREEN, 2),
                "РЕЦЕПТ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                Color.GREEN
        ));

        recipeArea = new JTextArea();
        recipeArea.setEditable(false);
        recipeArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        recipeArea.setMargin(new Insets(15, 15, 15, 15));
        recipeArea.setLineWrap(true);
        recipeArea.setWrapStyleWord(true);
        recipeArea.setBackground(new Color(255, 255, 240));

        JScrollPane recipeScroll = new JScrollPane(recipeArea);
        recipeScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(recipeScroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAddRecipePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                "ДОБАВИТЬ НОВЫЙ РЕЦЕПТ",
                TitledBorder.LEFT,
                TitledBorder.TOP
        ));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField recipeNameField = new JTextField(20);
        JTextArea recipeInstructionsArea = new JTextArea(8, 30);
        recipeInstructionsArea.setLineWrap(true);
        recipeInstructionsArea.setWrapStyleWord(true);
        JScrollPane instructionsScroll = new JScrollPane(recipeInstructionsArea);

        JButton addRecipeButton = new JButton("💾 СОХРАНИТЬ РЕЦЕПТ");
        addRecipeButton.setFont(new Font("Arial", Font.BOLD, 12));
        addRecipeButton.setBackground(new Color(34, 139, 34));
        addRecipeButton.setForeground(Color.WHITE);

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Название:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(recipeNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        inputPanel.add(new JLabel("Ингредиенты и способ приготовления:"), gbc);

        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        inputPanel.add(instructionsScroll, gbc);

        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        inputPanel.add(addRecipeButton, gbc);

        panel.add(inputPanel, BorderLayout.CENTER);

        // Обработчик добавления рецепта
        addRecipeButton.addActionListener(e -> {
            String name = recipeNameField.getText().trim();
            String instructions = recipeInstructionsArea.getText().trim();

            if (name.isEmpty() || instructions.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Заполните все поля!");
                return;
            }

            if (recipes.containsKey(name)) {
                int result = JOptionPane.showConfirmDialog(
                        this,
                        "Рецепт с таким названием уже существует.\nЗаменить его?",
                        "Подтверждение",
                        JOptionPane.YES_NO_OPTION
                );

                if (result != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            recipes.put(name, new String[]{instructions});
            dataManager.saveRecipes(recipes);
            updateRecipeList();

            recipeNameField.setText("");
            recipeInstructionsArea.setText("");

            JOptionPane.showMessageDialog(this, "Рецепт успешно добавлен!");
        });

        return panel;
    }

    private void initializeDefaultRecipes() {
        recipes = new HashMap<>();

        recipes.put("🥔 Мятая картошка с сосиской", new String[]{
                "ИНГРЕДИЕНТЫ:\n" +
                        "• Картофель - 500 г\n" +
                        "• Сосиски - 4 шт\n" +
                        "• Сливочное масло - 50 г\n" +
                        "• Молоко - 100 мл\n" +
                        "• Соль, перец - по вкусу\n" +
                        "• Зелень (укроп, петрушка) - для подачи\n\n" +
                        "ПРИГОТОВЛЕНИЕ:\n" +
                        "1. Картофель очистить, нарезать крупными кусками\n" +
                        "2. Отварить картофель в подсоленной воде до готовности (20-25 минут)\n" +
                        "3. Сосиски отварить или обжарить на сковороде\n" +
                        "4. Слить воду с картофеля, добавить сливочное масло\n" +
                        "5. Подогреть молоко и добавить к картофелю\n" +
                        "6. Размять картофель вилкой (не до состояния пюре, должны оставаться кусочки)\n" +
                        "7. Посолить, поперчить, перемешать\n" +
                        "8. Подавать с сосисками, посыпать зеленью\n\n" +
                        "СОВЕТ: Картофель лучше мять толкушкой, а не блендером"
        });

        recipes.put("🍝 Паста Карбонара", new String[]{
                "ИНГРЕДИЕНТЫ:\n" +
                        "• Спагетти - 300 г\n" +
                        "• Бекон или панчетта - 150 г\n" +
                        "• Яйца - 2 шт\n" +
                        "• Сыр Пармезан - 50 г\n" +
                        "• Чеснок - 2 зубчика\n" +
                        "• Оливковое масло - 2 ст. ложки\n" +
                        "• Соль, черный перец - по вкусу\n\n" +
                        "ПРИГОТОВЛЕНИЕ:\n" +
                        "1. Отварить спагетти в подсоленной воде до состояния al dente\n" +
                        "2. Нарезать бекон, обжарить с чесноком на оливковом масле\n" +
                        "3. В миске взбить яйца, добавить тертый Пармезан, перец\n" +
                        "4. Слить воду со спагетти, оставив немного воды\n" +
                        "5. Горячие спагетти смешать с беконом, убрать с огня\n" +
                        "6. Добавить яичную смесь, быстро перемешать\n" +
                        "7. При необходимости добавить немного воды от пасты\n" +
                        "8. Подавать сразу, посыпать сыром и перцем"
        });

        recipes.put("🥗 Овощной сад", new String[]{
                "ИНГРЕДИЕНТЫ:\n" +
                        "• Помидоры - 2 шт\n" +
                        "• Огурцы - 2 шт\n" +
                        "• Болгарский перец - 1 шт\n" +
                        "• Красный лук - 1 шт\n" +
                        "• Оливки - 50 г\n" +
                        "• Сыр Фета - 100 г\n" +
                        "• Оливковое масло - 3 ст. ложки\n" +
                        "• Лимонный сок - 1 ст. ложка\n" +
                        "• Соль, орегано - по вкусу\n\n" +
                        "ПРИГОТОВЛЕНИЕ:\n" +
                        "1. Нарезать помидоры дольками, огурцы полукружьями\n" +
                        "2. Перец нарезать полосками, лук - полукольцами\n" +
                        "3. Сыр Фета нарезать кубиками\n" +
                        "4. Смешать оливковое масло с лимонным соком\n" +
                        "5. Выложить овощи на тарелку, сверху сыр и оливки\n" +
                        "6. Полить заправкой, посолить, посыпать орегано"
        });

        recipes.put("🥞 Блинчики", new String[]{
                "ИНГРЕДИЕНТЫ:\n" +
                        "• Мука - 250 г\n" +
                        "• Молоко - 500 мл\n" +
                        "• Яйца - 2 шт\n" +
                        "• Сахар - 2 ст. ложки\n" +
                        "• Соль - щепотка\n" +
                        "• Растительное масло - 2 ст. ложки\n" +
                        "• Сливочное масло - для смазывания\n\n" +
                        "ПРИГОТОВЛЕНИЕ:\n" +
                        "1. Взбить яйца с сахаром и солью\n" +
                        "2. Добавить половину молока, перемешать\n" +
                        "3. Постепенно всыпать муку, перемешивая\n" +
                        "4. Добавить оставшееся молоко и масло\n" +
                        "5. Дать тесту постоять 15-20 минут\n" +
                        "6. Жарить на разогретой сковороде\n" +
                        "7. Готовые блины смазывать сливочным маслом\n\n" +
                        "ПОДАЧА: Со сметаной, вареньем, медом или икрой"
        });

        dataManager.saveRecipes(recipes);
    }

    private void updateRecipeList() {
        recipeListModel.clear();
        recipes.keySet().stream()
                .sorted()
                .forEach(recipeListModel::addElement);
    }

    private void displayRecipe(String recipeName) {
        String[] recipeDetails = recipes.get(recipeName);
        if (recipeDetails != null && recipeDetails.length > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("═══════════════════════════════════════════════\n");
            sb.append("              ").append(recipeName).append("\n");
            sb.append("═══════════════════════════════════════════════\n\n");
            sb.append(recipeDetails[0]);
            recipeArea.setText(sb.toString());
            recipeArea.setCaretPosition(0);
        }
    }
}
