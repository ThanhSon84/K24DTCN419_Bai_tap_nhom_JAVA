import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Enumeration;

public class FlashcardApp extends JFrame {
    private FlashcardManager manager;
    private JTextField wordField;
    private JTextField meaningField;
    private JTable cardTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton saveButton;
    private JButton loadButton;
    private JButton practiceButton;
    private JButton deleteButton;

    public static void setUIFont(Font font) {
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, new javax.swing.plaf.FontUIResource(font));
            }
        }
    }

    public FlashcardApp() {
        Locale.setDefault(new Locale("vi", "VN"));
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}

        Font unicodeFont = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
        setUIFont(unicodeFont);

        manager = new FlashcardManager();
        setTitle("Flashcard App (Anh - Việt)");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Thêm từ mới"));

        inputPanel.add(new JLabel("Từ (Tiếng Anh):"));
        wordField = new JTextField();
        wordField.enableInputMethods(true);
        inputPanel.add(wordField);

        inputPanel.add(new JLabel("Nghĩa (Tiếng Việt):"));
        meaningField = new JTextField();
        meaningField.enableInputMethods(true);
        inputPanel.add(meaningField);

        addButton = new JButton("Thêm từ");
        inputPanel.add(addButton);
        add(inputPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"STT", "Từ", "Nghĩa"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c != 0; }
        };
        cardTable = new JTable(tableModel);
        cardTable.setRowHeight(30);

        JTextField tableTextField = new JTextField();
        tableTextField.enableInputMethods(true);
        cardTable.setDefaultEditor(Object.class, new DefaultCellEditor(tableTextField));

        tableModel.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                Object w = tableModel.getValueAt(row, 1);
                Object m = tableModel.getValueAt(row, 2);
                if (w != null && m != null) manager.updateFlashcard(row, w.toString(), m.toString());
            }
        });

        add(new JScrollPane(cardTable), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        deleteButton = new JButton("Xóa"); saveButton = new JButton("Lưu");
        loadButton = new JButton("Mở"); practiceButton = new JButton("Ôn tập");
        controlPanel.add(deleteButton); controlPanel.add(saveButton);
        controlPanel.add(loadButton); controlPanel.add(practiceButton);
        add(controlPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> {
            manager.addFlashcard(wordField.getText(), meaningField.getText());
            wordField.setText(""); meaningField.setText(""); updateTable();
        });
        deleteButton.addActionListener(e -> {
            if (cardTable.getSelectedRow() >= 0) { manager.deleteFlashcard(cardTable.getSelectedRow()); updateTable(); }
        });
        saveButton.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try { manager.saveToFile(fc.getSelectedFile()); } catch (IOException ex) {}
            }
        });
        loadButton.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try { manager.loadFromFile(fc.getSelectedFile()); updateTable(); } catch (IOException ex) {}
            }
        });
        practiceButton.addActionListener(e -> {

            if (manager.getFlashcards().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Hiện không có bất kì flashcard nào trong thư viện, vui lòng thêm vào để ôn tập nhé.");
                return;
            }

            new PracticeFrame(manager.getShuffledCards());
        });
        setLocationRelativeTo(null);
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        List<Flashcard> cards = manager.getFlashcards();
        for (int i = 0; i < cards.size(); i++) {
            tableModel.addRow(new Object[]{i + 1, cards.get(i).getWord(), cards.get(i).getMeaning()});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FlashcardApp().setVisible(true));
    }
}