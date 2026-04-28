import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Launch {
    private static final Color BG = new Color(245, 245, 239);
    private static final Color SURFACE = new Color(250, 250, 247);
    private static final Color INK = new Color(18, 20, 22);
    private static final Color MUTED = new Color(93, 98, 105);
    private static final Color ACCENT = new Color(28, 54, 90);

    private final Map<String, Lesson> lessons = buildLessons();

    private JFrame frame;
    private CardLayout cards;
    private JPanel contentPanel;

    private JList<String> walkthroughTopicList;
    private JTextArea walkthroughSummary;
    private JTextArea walkthroughCode;
    private JTextArea walkthroughOutput;

    private JLabel quizMeta;
    private JTextArea quizPrompt;
    private JTextField quizAnswer;
    private JTextArea quizExpected;
    private JTextArea quizExplanation;
    private JLabel quizResult;

    private List<QuestionItem> activeQuiz = new ArrayList<>();
    private int quizIndex = 0;
    private int quizScore = 0;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Launch app = new Launch();
            app.buildUi();
            app.frame.setVisible(true);
        });
    }

    private void buildUi() {
        setLookAndFeel();

        frame = new JFrame("c syntax trainer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1120, 760);
        frame.setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildMain(), BorderLayout.CENTER);

        frame.setContentPane(root);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

        JLabel title = new JLabel("c syntax trainer");
        title.setForeground(INK);
        title.setFont(new Font("Serif", Font.PLAIN, 34));

        JLabel subtitle = new JLabel("guided walkthrough + quiz + run native c app");
        subtitle.setForeground(MUTED);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(title);
        left.add(Box.createVerticalStrut(3));
        left.add(subtitle);

        header.add(left, BorderLayout.WEST);
        return header;
    }

    private JSplitPane buildMain() {
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBackground(SURFACE);
        nav.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 212)),
            BorderFactory.createEmptyBorder(14, 12, 14, 12)
        ));

        JButton walkthroughBtn = createNavButton("walkthrough");
        walkthroughBtn.addActionListener(e -> showCard("walkthrough"));

        JButton quizBtn = createNavButton("quiz mode");
        quizBtn.addActionListener(e -> {
            showCard("quiz");
            startQuiz();
        });

        JButton runNativeBtn = createNavButton("run native c app");
        runNativeBtn.addActionListener(e -> runNativeCApp());

        nav.add(walkthroughBtn);
        nav.add(Box.createVerticalStrut(10));
        nav.add(quizBtn);
        nav.add(Box.createVerticalStrut(10));
        nav.add(runNativeBtn);
        nav.add(Box.createVerticalGlue());

        cards = new CardLayout();
        contentPanel = new JPanel(cards);
        contentPanel.setOpaque(false);
        contentPanel.add(buildWalkthroughCard(), "walkthrough");
        contentPanel.add(buildQuizCard(), "quiz");

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, nav, contentPanel);
        split.setDividerLocation(230);
        split.setBorder(null);
        split.setBackground(BG);
        return split;
    }

    private JPanel buildWalkthroughCard() {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setBackground(BG);

        List<String> topics = new ArrayList<>(lessons.keySet());
        walkthroughTopicList = new JList<>(topics.toArray(new String[0]));
        walkthroughTopicList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        walkthroughTopicList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateWalkthroughTopic();
            }
        });

        JScrollPane leftScroll = new JScrollPane(walkthroughTopicList);
        leftScroll.setPreferredSize(new Dimension(240, 0));

        JPanel detail = new JPanel();
        detail.setLayout(new BoxLayout(detail, BoxLayout.Y_AXIS));
        detail.setBackground(SURFACE);
        detail.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 212)),
            BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));

        walkthroughSummary = createReadArea(4);
        walkthroughCode = createReadArea(10);
        walkthroughOutput = createReadArea(3);

        detail.add(sectionLabel("summary"));
        detail.add(walkthroughSummary);
        detail.add(Box.createVerticalStrut(8));
        detail.add(sectionLabel("example"));
        detail.add(walkthroughCode);
        detail.add(Box.createVerticalStrut(8));
        detail.add(sectionLabel("expected output"));
        detail.add(walkthroughOutput);

        panel.add(leftScroll, BorderLayout.WEST);
        panel.add(detail, BorderLayout.CENTER);

        if (!topics.isEmpty()) {
            walkthroughTopicList.setSelectedIndex(0);
        }
        return panel;
    }

    private JPanel buildQuizCard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);

        JPanel detail = new JPanel();
        detail.setLayout(new BoxLayout(detail, BoxLayout.Y_AXIS));
        detail.setBackground(SURFACE);
        detail.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 212)),
            BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));

        quizMeta = new JLabel("click quiz mode to begin");
        quizMeta.setForeground(ACCENT);
        quizMeta.setFont(new Font("SansSerif", Font.BOLD, 13));

        quizPrompt = createReadArea(7);
        quizAnswer = new JTextField();
        quizExpected = createReadArea(3);
        quizExplanation = createReadArea(8);
        quizResult = new JLabel(" ");
        quizResult.setForeground(MUTED);

        JButton checkBtn = new JButton("check answer");
        checkBtn.addActionListener(e -> checkQuizAnswer());

        JButton nextBtn = new JButton("next question");
        nextBtn.addActionListener(e -> nextQuizQuestion());

        JPanel controls = new JPanel(new BorderLayout(8, 0));
        controls.setOpaque(false);
        controls.add(checkBtn, BorderLayout.WEST);
        controls.add(nextBtn, BorderLayout.CENTER);

        detail.add(quizMeta);
        detail.add(Box.createVerticalStrut(8));
        detail.add(sectionLabel("question"));
        detail.add(quizPrompt);
        detail.add(Box.createVerticalStrut(8));
        detail.add(sectionLabel("your answer"));
        detail.add(quizAnswer);
        detail.add(Box.createVerticalStrut(8));
        detail.add(controls);
        detail.add(Box.createVerticalStrut(8));
        detail.add(quizResult);
        detail.add(Box.createVerticalStrut(8));
        detail.add(sectionLabel("expected"));
        detail.add(quizExpected);
        detail.add(Box.createVerticalStrut(8));
        detail.add(sectionLabel("example explanation"));
        detail.add(quizExplanation);

        panel.add(detail, BorderLayout.CENTER);
        return panel;
    }

    private void updateWalkthroughTopic() {
        String topic = walkthroughTopicList.getSelectedValue();
        if (topic == null) {
            return;
        }

        Lesson lesson = lessons.get(topic);
        walkthroughSummary.setText(lesson.summary);
        walkthroughCode.setText(lesson.exampleCode);
        walkthroughOutput.setText(lesson.expectedOutput);
    }

    private void startQuiz() {
        activeQuiz = new ArrayList<>();
        for (Map.Entry<String, Lesson> entry : lessons.entrySet()) {
            String topic = entry.getKey();
            for (QuestionItem item : entry.getValue().questions) {
                activeQuiz.add(new QuestionItem(topic, item.prompt, item.expectedAnswer, item.explanation));
            }
        }

        quizIndex = 0;
        quizScore = 0;
        loadQuizQuestion();
    }

    private void loadQuizQuestion() {
        if (activeQuiz.isEmpty()) {
            quizMeta.setText("no questions found");
            quizPrompt.setText("");
            return;
        }

        if (quizIndex >= activeQuiz.size()) {
            double pct = (100.0 * quizScore) / activeQuiz.size();
            JOptionPane.showMessageDialog(
                frame,
                String.format("session complete\nscore: %d/%d (%.1f%%)", quizScore, activeQuiz.size(), pct),
                "complete",
                JOptionPane.INFORMATION_MESSAGE
            );
            quizIndex = 0;
            quizScore = 0;
        }

        QuestionItem q = activeQuiz.get(quizIndex);
        quizMeta.setText(String.format("%s | question %d/%d", q.topic, quizIndex + 1, activeQuiz.size()));
        quizPrompt.setText(q.prompt);
        quizAnswer.setText("");
        quizExpected.setText("");
        quizExplanation.setText("");
        quizResult.setText(" ");
    }

    private void checkQuizAnswer() {
        if (activeQuiz.isEmpty()) {
            return;
        }

        QuestionItem q = activeQuiz.get(quizIndex);
        String userAnswer = normalize(quizAnswer.getText());
        boolean correct = normalize(q.expectedAnswer).equals(userAnswer);

        if (correct) {
            quizScore++;
            quizResult.setForeground(new Color(18, 120, 56));
            quizResult.setText("correct");
        } else {
            quizResult.setForeground(new Color(140, 38, 38));
            quizResult.setText("incorrect");
        }

        quizExpected.setText(q.expectedAnswer);
        quizExplanation.setText(q.explanation);
    }

    private void nextQuizQuestion() {
        if (activeQuiz.isEmpty()) {
            return;
        }
        quizIndex++;
        loadQuizQuestion();
    }

    private void runNativeCApp() {
        String exe = "out\\c_syntax.exe";
        String bin = "./out/c_syntax";
        ProcessBuilder builder = isWindows() ? new ProcessBuilder(exe) : new ProcessBuilder(bin);
        builder.inheritIO();

        try {
            Process process = builder.start();
            int code = process.waitFor();
            JOptionPane.showMessageDialog(frame, "native c app exited with code " + code, "native app", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                frame,
                "could not launch native c app. build it first with make or gcc.",
                "native app",
                JOptionPane.WARNING_MESSAGE
            );
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            JOptionPane.showMessageDialog(frame, "launch interrupted.", "native app", JOptionPane.WARNING_MESSAGE);
        }
    }

    private boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase().contains("win");
    }

    private JTextArea createReadArea(int rows) {
        JTextArea area = new JTextArea(rows, 30);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBackground(new Color(247, 247, 242));
        area.setForeground(INK);
        area.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(223, 223, 216)),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        return area;
    }

    private JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(ACCENT);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        return label;
    }

    private JButton createNavButton(String label) {
        JButton button = new JButton(label);
        button.setFocusPainted(false);
        button.setBackground(new Color(241, 241, 236));
        button.setForeground(INK);
        button.setBorder(BorderFactory.createLineBorder(new Color(222, 222, 215)));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        return button;
    }

    private void showCard(String name) {
        cards.show(contentPanel, name);
    }

    private String normalize(String value) {
        return value.replace("\r\n", "\n").trim();
    }

    private void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
    }

    private Map<String, Lesson> buildLessons() {
        Map<String, Lesson> map = new LinkedHashMap<>();

        map.put("Basics", new Lesson(
            "Variables, primitive types, and printing with format specifiers.",
            "int age = 21;\nfloat gpa = 3.7f;\nprintf(\"age=%d gpa=%.1f\\n\", age, gpa);",
            "age=21 gpa=3.7",
            List.of(new QuestionItem(
                "Basics",
                "What prints from: int x = 5; printf(\"%d\\n\", x * 2);",
                "10",
                "x * 2 becomes 10, so printf prints 10."
            ))
        ));

        map.put("Control Flow", new Lesson(
            "if/else, loops, and switch patterns.",
            "int sum = 0;\nfor (int i = 1; i <= 4; i++) {\n    sum += i;\n}\nprintf(\"%d\\n\", sum);",
            "10",
            List.of(new QuestionItem(
                "Control Flow",
                "for (int i=0;i<3;i++) printf(\"%d\", i); outputs?",
                "012",
                "Loop runs with i = 0, 1, 2 and prints each value in order."
            ))
        ));

        map.put("Functions", new Lesson(
            "Function declarations, definitions, and return values.",
            "int add(int a, int b) { return a + b; }\nprintf(\"%d\\n\", add(2, 3));",
            "5",
            List.of(new QuestionItem(
                "Functions",
                "If square returns n*n, what is square(4)?",
                "16",
                "4 * 4 = 16."
            ))
        ));

        map.put("Pointers", new Lesson(
            "Memory addresses, dereference, and pass-by-reference style updates.",
            "int value = 7;\nint *p = &value;\n*p = 9;\nprintf(\"%d\\n\", value);",
            "9",
            List.of(new QuestionItem(
                "Pointers",
                "What does *p mean when p is int*?",
                "value at address stored in p",
                "Dereference operator reads or writes the pointed value."
            ))
        ));

        map.put("Arrays and Strings", new Lesson(
            "Indexed storage and null-terminated strings.",
            "char name[] = \"kerwin\";\nprintf(\"%c %s\\n\", name[0], name);",
            "k kerwin",
            List.of(new QuestionItem(
                "Arrays and Strings",
                "What is strlen(\"cat\")?",
                "3",
                "strlen excludes the trailing null terminator."
            ))
        ));

        map.put("Structs and Memory", new Lesson(
            "Grouping data with struct and heap allocation with malloc/free.",
            "typedef struct { int id; } User;\nUser u = {42};\nprintf(\"%d\\n\", u.id);",
            "42",
            List.of(new QuestionItem(
                "Structs and Memory",
                "Which function releases malloc memory?",
                "free",
                "Always pair malloc/calloc/realloc with free."
            ))
        ));

        map.put("File IO", new Lesson(
            "Reading and writing files with fopen/fprintf/fgets/fclose.",
            "FILE *fp = fopen(\"note.txt\", \"w\");\nfprintf(fp, \"hello\\n\");\nfclose(fp);",
            "file note.txt contains hello",
            List.of(new QuestionItem(
                "File IO",
                "What mode opens a file for append?",
                "a",
                "Mode a appends to end; a+ appends and allows read."
            ))
        ));

        return map;
    }

    private static final class Lesson {
        private final String summary;
        private final String exampleCode;
        private final String expectedOutput;
        private final List<QuestionItem> questions;

        private Lesson(String summary, String exampleCode, String expectedOutput, List<QuestionItem> questions) {
            this.summary = summary;
            this.exampleCode = exampleCode;
            this.expectedOutput = expectedOutput;
            this.questions = questions;
        }
    }

    private static final class QuestionItem {
        private final String topic;
        private final String prompt;
        private final String expectedAnswer;
        private final String explanation;

        private QuestionItem(String topic, String prompt, String expectedAnswer, String explanation) {
            this.topic = topic;
            this.prompt = prompt;
            this.expectedAnswer = expectedAnswer;
            this.explanation = explanation;
        }
    }
}
