import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.swing.JOptionPane;
public class FlashcardManager {
    private List<Flashcard> flashcards = new ArrayList<>();
    public void addFlashcard(String w, String m) { if (!w.trim().isEmpty() && !m.trim().isEmpty()) flashcards.add(new Flashcard(w.trim(), m.trim())); }
    public void updateFlashcard(int i, String w, String m) { if (i >= 0 && i < flashcards.size()) { flashcards.get(i).setWord(w); flashcards.get(i).setMeaning(m); } }
    public void deleteFlashcard(int i) { if (i >= 0 && i < flashcards.size()) flashcards.remove(i); }
    public List<Flashcard> getFlashcards() { return new ArrayList<>(flashcards); }
    public void saveToFile(File f) throws IOException {
        try (BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8))) {
            for (Flashcard c : flashcards) { w.write(c.toString()); w.newLine(); }
        }
    }
    public void loadFromFile(File file) throws IOException {

        flashcards.clear();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));

        String line;
        StringBuilder errorLines = new StringBuilder(); // gom các dòng lỗi

        while ((line = reader.readLine()) != null) {

            String[] parts = line.split("@@@");

            if (parts.length == 2) {
                flashcards.add(new Flashcard(parts[0], parts[1]));
            } else {
                errorLines.append(line).append("\n");
            }
        }

        reader.close();

        // Hiện popup nếu có dòng lỗi
        if (errorLines.length() > 0) {
            JOptionPane.showMessageDialog(null,
                    "Trong file có những dòng lỗi sau:\n\n"
                            + errorLines.toString());
        }
    }
    public List<Flashcard> getShuffledCards() { List<Flashcard> s = new ArrayList<>(flashcards); Collections.shuffle(s); return s; }
}