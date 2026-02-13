import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class WeeklyPlannerPanel extends JPanel {
    private Map<String, List<Product>> weeklyPlan;
    private JTabbedPane dayTabs;
    private DataManager dataManager;
    private final String[] DAYS = {"Понедельник", "Вторник", "Среда", "Четверг",
            "Пятница", "Суббота", "Воскресенье"};

    public WeeklyPlannerPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        dataManager = new DataManager();
        weeklyPlan = dataManager.loadWeeklyPlan();

        if (weeklyPlan.isEmpty()) {
            initializeWeeklyPlan();
        }

        // Заголовок
        JLabel titleLabel = new JLabel("ПЛАНИРОВЩИК ПИТАНИЯ НА НЕДЕЛЮ", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Центральная панель с табами
        JPanel centerPanel = new JPanel(new BorderLayout());
        dayTabs = new JTabbedPane();
        refreshTabs();
        centerPanel.add(dayTabs, BorderLayout.CENTER);

        // Панель добавления продуктов
        JPanel addPanel = createAddProductPanel();
        centerPanel.add(addPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        // Нижняя панель с кнопкой сохранения
        JButton saveButton = new JButton("Сохранить изменения");
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveButton.addActionListener(e -> {
            dataManager.saveWeeklyPlan(weeklyPlan);
            JOptionPane.showMessageDialog(this, "План сохранен!");
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(saveButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createAddProductPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Добавить новый продукт"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(20);
        JTextField priceField = new JTextField(10);
        JComboBox<String> dayCombo = new JComboBox<>(DAYS);
        JButton addButton = new JButton("Добавить в план");
        addButton.setFont(new Font("Arial", Font.BOLD, 12));
        addButton.setBackground(new Color(70, 130, 180));
        addButton.setForeground(Color.WHITE);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Название продукта:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Цена (руб):"), gbc);
        gbc.gridx = 1;
        panel.add(priceField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("День недели:"), gbc);
        gbc.gridx = 1;
        panel.add(dayCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(addButton, gbc);

        // Обработчик добавления продукта
        addButton.addActionListener((ActionEvent e) -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите название продукта");
                return;
            }

            double price;
            try {
                price = Double.parseDouble(priceField.getText().trim());
                if (price < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Введите корректную цену");
                return;
            }

            String day = (String) dayCombo.getSelectedItem();
            Product product = new Product(name, price, day);
            weeklyPlan.get(day).add(product);

            nameField.setText("");
            priceField.setText("");

            refreshTabs();
            dataManager.saveWeeklyPlan(weeklyPlan);

            JOptionPane.showMessageDialog(this, "Продукт добавлен в " + day);
        });

        return panel;
    }

    private void initializeWeeklyPlan() {
        weeklyPlan = new LinkedHashMap<>(); // Используем LinkedHashMap для сохранения порядка
        for (String day : DAYS) {
            weeklyPlan.put(day, new ArrayList<>());
        }
    }

    private void refreshTabs() {
        dayTabs.removeAll();

        // Обновляем порядок дней
        Map<String, List<Product>> orderedPlan = new LinkedHashMap<>();
        for (String day : DAYS) {
            orderedPlan.put(day, weeklyPlan.getOrDefault(day, new ArrayList<>()));
        }
        weeklyPlan = orderedPlan;

        for (String day : DAYS) {
            List<Product> products = weeklyPlan.get(day);
            if (products == null) {
                products = new ArrayList<>();
                weeklyPlan.put(day, products);
            }
            JPanel dayPanel = createDayPanel(day, products);
            dayTabs.addTab(day, dayPanel);
        }
    }

    private JPanel createDayPanel(String dayName, List<Product> products) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Информация о дне
        JLabel dayInfoLabel = new JLabel("Продукты на " + dayName.toLowerCase() + ":");
        dayInfoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(dayInfoLabel, BorderLayout.NORTH);

        // Таблица продуктов
        String[] columns = {"№", "Название продукта", "Цена (руб)"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        int counter = 1;
        double totalPrice = 0;
        for (Product product : products) {
            model.addRow(new Object[]{counter++, product.getName(), String.format("%.2f", product.getPrice())});
            totalPrice += product.getPrice();
        }

        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Нижняя панель с итогом и кнопкой удаления
        JPanel bottomPanel = new JPanel(new BorderLayout());

        JLabel totalLabel = new JLabel(String.format("Итого за день: %.2f руб.", totalPrice));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 12));
        bottomPanel.add(totalLabel, BorderLayout.WEST);

        JButton removeButton = new JButton("Удалить выбранный продукт");
        removeButton.setBackground(new Color(220, 20, 60));
        removeButton.setForeground(Color.WHITE);
        removeButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                products.remove(selectedRow);
                refreshTabs();
                dataManager.saveWeeklyPlan(weeklyPlan);
            } else {
                JOptionPane.showMessageDialog(panel, "Выберите продукт для удаления");
            }
        });
        bottomPanel.add(removeButton, BorderLayout.EAST);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }
}