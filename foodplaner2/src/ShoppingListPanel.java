import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ShoppingListPanel extends JPanel {
    private List<Product> shoppingList;
    private DefaultTableModel tableModel;
    private JTable table;
    private DataManager dataManager;
    private JLabel totalLabel;
    private JTextField nameField;
    private JTextField priceField;
    private JTextField quantityField;

    public ShoppingListPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        dataManager = new DataManager();
        shoppingList = dataManager.loadShoppingList();

        // Заголовок
        JLabel titleLabel = new JLabel("НЕЗАВИСИМЫЙ СПИСОК ПОКУПОК", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Основная панель
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Панель добавления продуктов (полностью независимая)
        JPanel addPanel = createAddProductPanel();
        mainPanel.add(addPanel, BorderLayout.NORTH);

        // Панель с таблицей
        JPanel tablePanel = createTablePanel();
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Инициализация
        refreshTable();
        updateTotalPrice();
    }

    private JPanel createAddProductPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLUE, 2),
                "ДОБАВИТЬ В СПИСОК ПОКУПОК",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                Color.BLUE
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Поля ввода
        nameField = new JTextField(25);
        priceField = new JTextField(15);
        quantityField = new JTextField(10);
        quantityField.setText("1");

        // Кнопки
        JButton addButton = new JButton("➕ ДОБАВИТЬ В СПИСОК");
        addButton.setFont(new Font("Arial", Font.BOLD, 14));
        addButton.setBackground(new Color(34, 139, 34));
        addButton.setForeground(Color.WHITE);
        addButton.setPreferredSize(new Dimension(250, 40));

        JButton clearAllButton = new JButton("🗑️ ОЧИСТИТЬ ВЕСЬ СПИСОК");
        clearAllButton.setFont(new Font("Arial", Font.BOLD, 12));
        clearAllButton.setBackground(new Color(255, 69, 0));
        clearAllButton.setForeground(Color.WHITE);

        JButton clearPurchasedButton = new JButton("✅ ОЧИСТИТЬ КУПЛЕННОЕ");
        clearPurchasedButton.setFont(new Font("Arial", Font.BOLD, 12));
        clearPurchasedButton.setBackground(new Color(70, 130, 180));
        clearPurchasedButton.setForeground(Color.WHITE);

        // Размещение компонентов
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Название товара:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Цена за шт (руб):"), gbc);
        gbc.gridx = 1;
        panel.add(priceField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Количество:"), gbc);
        gbc.gridx = 1;
        panel.add(quantityField, gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("шт"), gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(addButton, gbc);

        // Панель с дополнительными кнопками
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(clearPurchasedButton);
        buttonPanel.add(clearAllButton);

        gbc.gridy = 4;
        panel.add(buttonPanel, gbc);

        // Обработчики событий
        addButton.addActionListener(e -> addProductToList());
        clearAllButton.addActionListener(e -> clearAllList());
        clearPurchasedButton.addActionListener(e -> clearPurchasedItems());

        // Обработчик Enter
        nameField.addActionListener(e -> priceField.requestFocus());
        priceField.addActionListener(e -> quantityField.requestFocus());
        quantityField.addActionListener(e -> addProductToList());

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("ТЕКУЩИЙ СПИСОК ПОКУПОК"));

        // Таблица продуктов
        String[] columns = {"✓", "№", "Название", "Цена за шт", "Кол-во", "Общая цена", "Статус"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // Только чекбокс можно редактировать
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(240, 240, 240));

        // Настройка ширины колонок
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.getColumnModel().getColumn(1).setMaxWidth(40);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(60);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
        table.getColumnModel().getColumn(6).setPreferredWidth(100);

        // Обработчик изменения чекбокса
        tableModel.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int column = e.getColumn();
            if (column == 0 && row != -1 && row < shoppingList.size()) {
                Boolean purchased = (Boolean) tableModel.getValueAt(row, 0);
                shoppingList.get(row).setPurchased(purchased);
                updateTableRowStyle(row, purchased);
                dataManager.saveShoppingList(shoppingList);
                updateTotalPrice();
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(900, 400));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Панель с итоговой суммой
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        totalLabel = new JLabel("Общая сумма к оплате: 0.00 руб.");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setForeground(new Color(0, 100, 0));
        totalPanel.add(totalLabel);

        panel.add(totalPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void addProductToList() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Введите название товара");
            nameField.requestFocus();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceField.getText().trim());
            if (price <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Введите корректную цену (больше 0)");
            priceField.requestFocus();
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText().trim());
            if (quantity <= 0) quantity = 1;
        } catch (NumberFormatException ex) {
            quantity = 1;
        }

        // Создаем продукт с учетом количества
        Product product = new Product(name, price, null);
        product.setQuantity(quantity); // Нужно добавить поле quantity в класс Product
        product.setTotalPrice(price * quantity);

        shoppingList.add(product);

        // Очистка полей
        nameField.setText("");
        priceField.setText("");
        quantityField.setText("1");
        nameField.requestFocus();

        refreshTable();
        dataManager.saveShoppingList(shoppingList);
        updateTotalPrice();

        JOptionPane.showMessageDialog(this,
                "Товар добавлен в список!\n" + name + " - " + quantity + " шт x " + price + " руб.",
                "Успешно",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearAllList() {
        int result = JOptionPane.showConfirmDialog(
                this,
                "Вы уверены, что хотите очистить ВЕСЬ список покупок?",
                "Подтверждение",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            shoppingList.clear();
            refreshTable();
            dataManager.saveShoppingList(shoppingList);
            updateTotalPrice();
        }
    }

    private void clearPurchasedItems() {
        shoppingList.removeIf(Product::isPurchased);
        refreshTable();
        dataManager.saveShoppingList(shoppingList);
        updateTotalPrice();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        int counter = 1;
        for (Product product : shoppingList) {
            double totalPrice = product.getPrice() * product.getQuantity();
            tableModel.addRow(new Object[]{
                    product.isPurchased(),
                    counter++,
                    product.getName(),
                    String.format("%.2f", product.getPrice()),
                    product.getQuantity(),
                    String.format("%.2f", totalPrice),
                    product.isPurchased() ? "КУПЛЕНО ✓" : "ОЖИДАЕТ"
            });
        }
    }

    private void updateTableRowStyle(int row, boolean purchased) {
        if (purchased) {
            tableModel.setValueAt("КУПЛЕНО ✓", row, 6);
        } else {
            tableModel.setValueAt("ОЖИДАЕТ", row, 6);
        }
    }

    private void updateTotalPrice() {
        double total = shoppingList.stream()
                .filter(p -> !p.isPurchased())
                .mapToDouble(p -> p.getPrice() * p.getQuantity())
                .sum();
        totalLabel.setText(String.format("Общая сумма к оплате: %.2f руб.", total));
    }
}