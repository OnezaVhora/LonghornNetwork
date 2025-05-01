import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class NetworkLabUI extends JFrame {
    private static final Color LONGHORN_ORANGE = Color.decode("#A15032");
    private JTextArea testOutputArea, roommateArea, referralArea, friendChatArea;
    private GraphPanel graphPanel;
    private JComboBox<String> testCaseSelector, viewSelector, graphCaseSelector, startStudentSelector;
    private JTextField targetCompanyField;
    private List<List<UniversityStudent>> testCases;
    private List<UniversityStudent> currentTestCaseStudents = new ArrayList<>();

    public NetworkLabUI() {
        setTitle("Longhorn Network Lab UI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 750);
        setLocationRelativeTo(null);

        testCases = Arrays.asList(
            Main.generateTestCase1(),
            Main.generateTestCase2(),
            Main.generateTestCase3()
        );

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Test Runner", createTestRunnerPanel());
        tabs.addTab("Graph Viewer", createGraphViewerPanel());
        tabs.addTab("Roommate Pairs", createRoommatePanel());
        tabs.addTab("Referral Path", createReferralPanel());
        tabs.addTab("Friend & Chat", createFriendChatPanel());

        add(tabs);
        setVisible(true);
    }

    private JPanel createTestRunnerPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel top = new JPanel();
        testCaseSelector = new JComboBox<>(new String[]{"Test Case 1", "Test Case 2", "Test Case 3", "All Test Cases"});
        JButton runTestsButton = new JButton("Run Tests");
        runTestsButton.addActionListener(e -> runSelectedTests());
        top.add(new JLabel("Select Test Case:"));
        top.add(testCaseSelector);
        top.add(runTestsButton);
        panel.add(top, BorderLayout.NORTH);

        testOutputArea = new JTextArea();
        testOutputArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(testOutputArea);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createGraphViewerPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel controls = new JPanel();
        graphCaseSelector = new JComboBox<>(new String[]{"Test Case 1", "Test Case 2", "Test Case 3"});
        JButton loadGraphButton = new JButton("Load Graph");
        loadGraphButton.addActionListener(e -> {
            int idx = graphCaseSelector.getSelectedIndex();
            List<UniversityStudent> data = testCases.get(idx);
            currentTestCaseStudents = data;
            StudentGraph graph = new StudentGraph(data);
            graphPanel.setGraph(graph, data);
        });
        controls.add(new JLabel("Select Data:"));
        controls.add(graphCaseSelector);
        controls.add(loadGraphButton);
        panel.add(controls, BorderLayout.NORTH);

        graphPanel = new GraphPanel();
        panel.add(graphPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createRoommatePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        roommateArea = new JTextArea();
        roommateArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(roommateArea);

        JComboBox<String> rmCaseSelector = new JComboBox<>(new String[]{"Test Case 1", "Test Case 2", "Test Case 3"});
        JButton computeButton = new JButton("Compute Roommates");
        computeButton.addActionListener(e -> {
            int idx = rmCaseSelector.getSelectedIndex();
            List<UniversityStudent> data = testCases.get(idx);
            List<String> pairs = Main.computeRoommates(data);
            roommateArea.setText(String.join("\n", pairs));
        });

        JPanel top = new JPanel();
        top.add(new JLabel("Select Test Case:"));
        top.add(rmCaseSelector);
        top.add(computeButton);

        panel.add(top, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createReferralPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        referralArea = new JTextArea();
        referralArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(referralArea);

        startStudentSelector = new JComboBox<>();
        targetCompanyField = new JTextField(10);
        JButton computeButton = new JButton("Find Referral Path");
        computeButton.addActionListener(e -> {
            String student = (String) startStudentSelector.getSelectedItem();
            String company = targetCompanyField.getText();
            if (student != null && !company.isEmpty()) {
                String result = Main.findReferralPath(currentTestCaseStudents, student, company);
                referralArea.setText(result);
            }
        });

        JPanel top = new JPanel();
        top.add(new JLabel("From Student:"));
        top.add(startStudentSelector);
        top.add(new JLabel("To Company:"));
        top.add(targetCompanyField);
        top.add(computeButton);

        panel.add(top, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createFriendChatPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        friendChatArea = new JTextArea();
        friendChatArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(friendChatArea);

        JComboBox<String> chatCaseSelector = new JComboBox<>(new String[]{"Test Case 1", "Test Case 2", "Test Case 3"});
        JButton showButton = new JButton("Show Friend & Chat Data");
        showButton.addActionListener(e -> {
            int idx = chatCaseSelector.getSelectedIndex();
            List<UniversityStudent> data = testCases.get(idx);
            friendChatArea.setText(Main.displayFriendRequestsAndChats(data));
        });

        JPanel top = new JPanel();
        top.add(new JLabel("Select Test Case:"));
        top.add(chatCaseSelector);
        top.add(showButton);

        panel.add(top, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void runSelectedTests() {
        testOutputArea.setText("");
        int selected = testCaseSelector.getSelectedIndex();
        int overallScore = 0, count = 0;

        if (selected == 3) {
            for (int i = 0; i < testCases.size(); i++) {
                List<UniversityStudent> tc = testCases.get(i);
                int score = Main.gradeLab(tc, i + 1);
                testOutputArea.append("Test Case " + (i + 1) + " Score: " + score + "\n");
                overallScore += score;
                count++;
            }
            testOutputArea.append("Average Score: " + (overallScore / count) + "\n");
        } else {
            List<UniversityStudent> tc = testCases.get(selected);
            int score = Main.gradeLab(tc, selected + 1);
            testOutputArea.append("Test Case " + (selected + 1) + " Score: " + score + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(NetworkLabUI::new);
    }
}

class GraphPanel extends JPanel {
    private StudentGraph graph;
    private List<UniversityStudent> students;

    public void setGraph(StudentGraph g, List<UniversityStudent> s) {
        this.graph = g;
        this.students = s;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.WHITE);
        if (graph == null || students == null) return;

        Graphics2D g2d = (Graphics2D) g;
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = 200;
        int nodeRadius = 20;

        Map<UniversityStudent, Point> positions = new HashMap<>();
        for (int i = 0; i < students.size(); i++) {
            double angle = 2 * Math.PI * i / students.size();
            int x = (int) (centerX + radius * Math.cos(angle));
            int y = (int) (centerY + radius * Math.sin(angle));
            positions.put(students.get(i), new Point(x, y));
        }

        g2d.setStroke(new BasicStroke(2));
        for (UniversityStudent u1 : students) {
            for (UniversityStudent u2 : u1.getConnections().keySet()) {
                Point p1 = positions.get(u1);
                Point p2 = positions.get(u2);
                if (p1 != null && p2 != null) {
                    g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }
        }

        for (UniversityStudent student : students) {
            Point p = positions.get(student);
            if (p != null) {
                g2d.setColor(Color.ORANGE);
                g2d.fillOval(p.x - nodeRadius, p.y - nodeRadius, nodeRadius * 2, nodeRadius * 2);
                g2d.setColor(Color.BLACK);
                g2d.drawString(student.getName(), p.x - nodeRadius, p.y - nodeRadius - 5);
            }
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NetworkLabUI().setVisible(true));
    }
}

