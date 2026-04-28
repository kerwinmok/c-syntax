import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Launch {
    private static final String RESET = "\u001B[0m";
    private static final String INK = "\u001B[38;2;18;20;22m";
    private static final String MUTED = "\u001B[38;2;93;98;105m";
    private static final String ACCENT = "\u001B[38;2;28;54;90m";

    private final Scanner scanner = new Scanner(System.in);
    private final Map<String, Lesson> lessons = buildLessons();

    public static void main(String[] args) {
        new Launch().run();
    }

    private void run() {
        printHero();
        while (true) {
            printMainMenu();
            String input = scanner.nextLine().trim();

            if ("1".equals(input)) {
                runGuidedWalkthrough();
            } else if ("2".equals(input)) {
                runQuizMode();
            } else if ("3".equals(input)) {
                launchNativeCApp();
            } else if ("4".equals(input)) {
                printlnAccent("see you next session.");
                return;
            } else {
                printlnMuted("invalid option. choose 1 to 4.");
            }
        }
    }

    private void runGuidedWalkthrough() {
        List<String> topics = new ArrayList<>(lessons.keySet());
        while (true) {
            printSectionHeader("guided c syntax walkthrough");
            for (int i = 0; i < topics.size(); i++) {
                System.out.printf("%s%d%s) %s%n", ACCENT, i + 1, RESET, topics.get(i));
            }
            System.out.printf("%s%d%s) run all topics%n", ACCENT, topics.size() + 1, RESET);
            System.out.printf("%s%d%s) back%n", ACCENT, topics.size() + 2, RESET);
            System.out.print("choose: ");

            int choice = parseChoice(scanner.nextLine().trim());
            if (choice == topics.size() + 2) {
                return;
            }
            if (choice == topics.size() + 1) {
                for (String topic : topics) {
                    showLesson(topic, lessons.get(topic));
                }
                continue;
            }
            if (choice < 1 || choice > topics.size()) {
                printlnMuted("out of range.");
                continue;
            }

            String topic = topics.get(choice - 1);
            showLesson(topic, lessons.get(topic));
        }
    }

    private void showLesson(String topic, Lesson lesson) {
        printSectionHeader(topic.toLowerCase());
        printlnMuted(lesson.summary);
        System.out.println();
        printlnAccent("example");
        System.out.println(lesson.exampleCode);
        System.out.println();
        printlnAccent("expected output");
        System.out.println(lesson.expectedOutput);
        System.out.println();
        printlnMuted("press enter to continue...");
        scanner.nextLine();
    }

    private void runQuizMode() {
        int correct = 0;
        int total = 0;
        printSectionHeader("quiz mode");
        printlnMuted("answer each question, then reveal the solution.");

        for (Map.Entry<String, Lesson> entry : lessons.entrySet()) {
            String topic = entry.getKey();
            Lesson lesson = entry.getValue();

            for (Question q : lesson.questions) {
                total++;
                System.out.println();
                System.out.printf("%stopic:%s %s%n", MUTED, RESET, topic);
                System.out.println(q.prompt);
                System.out.print("your answer: ");
                String answer = scanner.nextLine().trim();

                if (normalize(answer).equals(normalize(q.expectedAnswer))) {
                    correct++;
                    System.out.printf("%sresult:%s correct%n", ACCENT, RESET);
                } else {
                    System.out.printf("%sresult:%s incorrect%n", ACCENT, RESET);
                    printlnMuted("expected: " + q.expectedAnswer);
                }

                printlnAccent("example explanation");
                System.out.println(q.explanation);
            }
        }

        System.out.println();
        System.out.printf("%sscore%s %d/%d (%.1f%%)%n", INK, RESET, correct, total,
            total == 0 ? 0.0 : (100.0 * correct) / total);
        printlnMuted("press enter to continue...");
        scanner.nextLine();
    }

    private void launchNativeCApp() {
        printSectionHeader("run native c app");
        String windowsBinary = "out\\c_syntax.exe";
        String unixBinary = "./out/c_syntax";

        ProcessBuilder builder = isWindows()
            ? new ProcessBuilder(windowsBinary)
            : new ProcessBuilder(unixBinary);

        builder.inheritIO();

        try {
            Process process = builder.start();
            int code = process.waitFor();
            printlnMuted("native app exited with code " + code + ".");
        } catch (IOException ex) {
            printlnMuted("could not launch native c app. build it first with make or gcc.");
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            printlnMuted("launch interrupted.");
        }
    }

    private boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase().contains("win");
    }

    private int parseChoice(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private String normalize(String value) {
        return value.trim().replace("\r\n", "\n");
    }

    private void printHero() {
        System.out.println(ACCENT + "============================================================" + RESET);
        System.out.println(INK + " c syntax trainer | clean terminal ui" + RESET);
        System.out.println(MUTED + " styled to match your portfolio vibe: lowercase + minimal" + RESET);
        System.out.println(ACCENT + "============================================================" + RESET);
    }

    private void printMainMenu() {
        System.out.println();
        printSectionHeader("main menu");
        System.out.printf("%s1%s) guided syntax walkthrough%n", ACCENT, RESET);
        System.out.printf("%s2%s) quiz mode (questions + examples)%n", ACCENT, RESET);
        System.out.printf("%s3%s) run native c app%n", ACCENT, RESET);
        System.out.printf("%s4%s) exit%n", ACCENT, RESET);
        System.out.print("choose: ");
    }

    private void printSectionHeader(String label) {
        System.out.printf("%n%s[%s]%s%n", INK, label, RESET);
    }

    private void printlnAccent(String line) {
        System.out.println(ACCENT + line + RESET);
    }

    private void printlnMuted(String line) {
        System.out.println(MUTED + line + RESET);
    }

    private Map<String, Lesson> buildLessons() {
        Map<String, Lesson> map = new LinkedHashMap<>();

        map.put("Basics", new Lesson(
            "Variables, primitive types, and printing with format specifiers.",
            "int age = 21;\nfloat gpa = 3.7f;\nprintf(\"age=%d gpa=%.1f\\n\", age, gpa);",
            "age=21 gpa=3.7",
            List.of(
                new Question(
                    "What prints from: int x = 5; printf(\"%d\\n\", x * 2);",
                    "10",
                    "x * 2 becomes 10, so printf prints 10."
                )
            )
        ));

        map.put("Control Flow", new Lesson(
            "if/else, loops, and switch patterns.",
            "int sum = 0;\nfor (int i = 1; i <= 4; i++) {\n    sum += i;\n}\nprintf(\"%d\\n\", sum);",
            "10",
            List.of(
                new Question(
                    "for (int i=0;i<3;i++) printf(\"%d\", i); outputs?",
                    "012",
                    "Loop runs with i = 0, 1, 2 and prints each value in order."
                )
            )
        ));

        map.put("Functions", new Lesson(
            "Function declarations, definitions, and return values.",
            "int add(int a, int b) { return a + b; }\nprintf(\"%d\\n\", add(2, 3));",
            "5",
            List.of(
                new Question(
                    "If square returns n*n, what is square(4)?",
                    "16",
                    "4 * 4 = 16."
                )
            )
        ));

        map.put("Pointers", new Lesson(
            "Memory addresses, dereference, and pass-by-reference style updates.",
            "int value = 7;\nint *p = &value;\n*p = 9;\nprintf(\"%d\\n\", value);",
            "9",
            List.of(
                new Question(
                    "What does *p mean when p is int*?",
                    "value at address stored in p",
                    "Dereference operator reads or writes the pointed value."
                )
            )
        ));

        map.put("Arrays and Strings", new Lesson(
            "Indexed storage and null-terminated strings.",
            "char name[] = \"kerwin\";\nprintf(\"%c %s\\n\", name[0], name);",
            "k kerwin",
            List.of(
                new Question(
                    "What is strlen(\"cat\")?",
                    "3",
                    "strlen excludes the trailing null terminator."
                )
            )
        ));

        map.put("Structs and Memory", new Lesson(
            "Grouping data with struct and heap allocation with malloc/free.",
            "typedef struct { int id; } User;\nUser u = {42};\nprintf(\"%d\\n\", u.id);",
            "42",
            List.of(
                new Question(
                    "Which function releases malloc memory?",
                    "free",
                    "Always pair malloc/calloc/realloc with free."
                )
            )
        ));

        map.put("File IO", new Lesson(
            "Reading and writing files with fopen/fprintf/fgets/fclose.",
            "FILE *fp = fopen(\"note.txt\", \"w\");\nfprintf(fp, \"hello\\n\");\nfclose(fp);",
            "file note.txt contains hello",
            List.of(
                new Question(
                    "What mode opens a file for append?",
                    "a",
                    "Mode a appends to end; a+ appends and allows read."
                )
            )
        ));

        return map;
    }

    private static final class Lesson {
        private final String summary;
        private final String exampleCode;
        private final String expectedOutput;
        private final List<Question> questions;

        private Lesson(String summary, String exampleCode, String expectedOutput, List<Question> questions) {
            this.summary = summary;
            this.exampleCode = exampleCode;
            this.expectedOutput = expectedOutput;
            this.questions = questions;
        }
    }

    private static final class Question {
        private final String prompt;
        private final String expectedAnswer;
        private final String explanation;

        private Question(String prompt, String expectedAnswer, String explanation) {
            this.prompt = prompt;
            this.expectedAnswer = expectedAnswer;
            this.explanation = explanation;
        }
    }
}
