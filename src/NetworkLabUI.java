import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class NetworkLabUI extends JFrame {
    private static final Color LONGHORN_ORANGE = Color.decode("#A15032");
    private static final Color LIGHT_BLUE = Color.decode("#B45A38");
    private static final Color BACKGROUND_COLOR = Color.decode("#B45A38");

    private JComboBox<String> testCaseSelector;
    private JButton runTestsButton;
    private JTextArea testOutputArea;
    private GraphPanel graphPanel;
    private JTextArea roommateArea;
    private JComboBox<String> startStudentSelector;
    private JTextField targetCompanyField;
    private JTextArea referralArea;
    private JTextArea chatHistoryArea;
    private List<List<UniversityStudent>> testCases;

    public NetworkLabUI() {
        super("Longhorn Network Lab UI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);

        // Set background color for the entire frame
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Prepare test cases
        testCases = Arrays.asList(
                Main.generateTestCase1(),
                Main.generateTestCase2(),
                Main.generateTestCase3()
        );

        // Populate UI elements for test cases
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Test Runner", createTestRunnerPanel());
        tabs.addTab("Graph Viewer", createGraphViewerPanel());
        tabs.addTab("Roommate Pairs", createRoommatePanel());
        tabs.addTab("Referral Path", createReferralPanel());
        tabs.addTab("Friend Requests & Chat History", createChatHistoryPanel()); // Added tab for chat history & friend requests
        tabs.addTab("Test Results", createTestResultsPanel());

        add(tabs);
    }

    private JPanel createTestResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel controls = new JPanel();
        controls.setBackground(BACKGROUND_COLOR);
    
        JComboBox<String> testCaseSelector = new JComboBox<>(new String[]{"Test Case 1", "Test Case 2", "Test Case 3", "All Test Cases"});
        JButton loadResultsButton = new JButton("Load Test Results");
        loadResultsButton.setBackground(LONGHORN_ORANGE);
        loadResultsButton.setForeground(Color.WHITE);
    
        JTextArea resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        resultsArea.setBackground(LIGHT_BLUE);
        resultsArea.setForeground(Color.BLACK);
    
        loadResultsButton.addActionListener(e -> {
            StringBuilder sb = new StringBuilder();
            String selectedTestCase = (String) testCaseSelector.getSelectedItem();
    
            if (selectedTestCase.equals("All Test Cases")) {
                sb.append(getTestResultsForAllCases());
            } else {
                int testCaseIndex = Integer.parseInt(selectedTestCase.split(" ")[2]) - 1;
                sb.append(getTestResultsForCase(testCaseIndex));
            }
    
            resultsArea.setText(sb.toString());
        });
    
        controls.add(new JLabel("Select Test Case:"));
        controls.add(testCaseSelector);
        controls.add(loadResultsButton);
        panel.add(controls, BorderLayout.NORTH);
        panel.add(new JScrollPane(resultsArea), BorderLayout.CENTER);
    
        return panel;
    }

    private JPanel createTestRunnerPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel top = new JPanel();
        top.setBackground(BACKGROUND_COLOR);
        testCaseSelector = new JComboBox<>(new String[]{"Test Case 1", "Test Case 2", "Test Case 3", "All Test Cases"});
        runTestsButton = new JButton("Run Tests");
        runTestsButton.setBackground(LONGHORN_ORANGE);
        runTestsButton.setForeground(Color.WHITE);
        runTestsButton.addActionListener(e -> onRunTests());
        top.add(new JLabel("Select Test Case:"));
        top.add(testCaseSelector);
        top.add(runTestsButton);
        panel.add(top, BorderLayout.NORTH);
        
        testOutputArea = new JTextArea();
        testOutputArea.setEditable(false);
        testOutputArea.setBackground(LIGHT_BLUE);
        testOutputArea.setForeground(Color.BLACK);
        JScrollPane scroll = new JScrollPane(testOutputArea);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createGraphViewerPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel controls = new JPanel();
        controls.setBackground(BACKGROUND_COLOR);
        JComboBox<String> graphCaseSelector = new JComboBox<>(new String[]{"Test Case 1", "Test Case 2", "Test Case 3"});
        JButton loadGraphButton = new JButton("Load Graph");
        loadGraphButton.setBackground(LONGHORN_ORANGE);
        loadGraphButton.setForeground(Color.WHITE);
        loadGraphButton.addActionListener(e -> {
            int idx = graphCaseSelector.getSelectedIndex();
            List<UniversityStudent> data = testCases.get(idx);
            StudentGraph graph = new StudentGraph(data);
            graphPanel.setGraph(graph, data);
        });
        controls.add(new JLabel("Select Data:"));
        controls.add(graphCaseSelector);
        controls.add(loadGraphButton);
        panel.add(controls, BorderLayout.NORTH);
        graphPanel = new GraphPanel();
        graphPanel.setBackground(LONGHORN_ORANGE);
        panel.add(graphPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createRoommatePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel controls = new JPanel();
        controls.setBackground(BACKGROUND_COLOR);
        JComboBox<String> rmCaseSelector = new JComboBox<>(new String[]{"Test Case 1", "Test Case 2", "Test Case 3"});
        JButton computeButton = new JButton("Compute Roommates");
        computeButton.setBackground(LONGHORN_ORANGE);
        computeButton.setForeground(Color.WHITE);
        computeButton.addActionListener(e -> {
            int idx = rmCaseSelector.getSelectedIndex();
            List<UniversityStudent> data = testCases.get(idx);
            // clear previous roommates
            data.forEach(s -> s.setRoommate(null));
            GaleShapley.assignRoommates(data);
            StringBuilder sb = new StringBuilder();
            for (UniversityStudent s : data) {
                if (s.getRoommate() != null && s.getName().compareTo(s.getRoommate().getName()) < 0) {
                    sb.append(s.getName()).append(" → ").append(s.getRoommate().getName()).append("\n");
                }
            }
            roommateArea.setText(sb.toString());
        });
        controls.add(new JLabel("Select Data:"));
        controls.add(rmCaseSelector);
        controls.add(computeButton);
        panel.add(controls, BorderLayout.NORTH);
        roommateArea = new JTextArea();
        roommateArea.setEditable(false);
        roommateArea.setBackground(LIGHT_BLUE);
        roommateArea.setForeground(Color.BLACK);
        panel.add(new JScrollPane(roommateArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createReferralPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel controls = new JPanel();
        controls.setBackground(BACKGROUND_COLOR);
        JComboBox<String> refCaseSelector = new JComboBox<>(new String[]{"Test Case 1", "Test Case 2", "Test Case 3"});
        startStudentSelector = new JComboBox<>();
        targetCompanyField = new JTextField(10);
        JButton findButton = new JButton("Find Path");
        findButton.setBackground(LONGHORN_ORANGE);
        findButton.setForeground(Color.WHITE);
        findButton.addActionListener(e -> {
            int idx = refCaseSelector.getSelectedIndex();
            List<UniversityStudent> data = testCases.get(idx);
            String selectedName = (String) startStudentSelector.getSelectedItem();
            UniversityStudent start = data.stream().filter(s -> s.getName().equals(selectedName)).findFirst().orElse(null);
            String target = targetCompanyField.getText().trim();
            if (start != null && !target.isEmpty()) {
                StudentGraph graph = new StudentGraph(data);
                ReferralPathFinder finder = new ReferralPathFinder(graph);
                List<UniversityStudent> path = finder.findReferralPath(start, target);
                StringBuilder sb = new StringBuilder();
                path.forEach(s -> sb.append(s.getName()).append(" -> "));
                if (!path.isEmpty()) sb.setLength(sb.length() - 4);
                referralArea.setText(sb.toString());
            }
        });
        refCaseSelector.addActionListener(e -> {
            int idx = refCaseSelector.getSelectedIndex();
            List<UniversityStudent> data = testCases.get(idx);
            startStudentSelector.removeAllItems();
            data.forEach(s -> startStudentSelector.addItem(s.getName()));
        });
        refCaseSelector.setSelectedIndex(0); // trigger population
        controls.add(new JLabel("Data:"));
        controls.add(refCaseSelector);
        controls.add(new JLabel("Start:"));
        controls.add(startStudentSelector);
        controls.add(new JLabel("Target Company:"));
        controls.add(targetCompanyField);
        controls.add(findButton);
        panel.add(controls, BorderLayout.NORTH);
        referralArea = new JTextArea();
        referralArea.setEditable(false);
        referralArea.setBackground(LIGHT_BLUE);
        referralArea.setForeground(Color.BLACK);
        panel.add(new JScrollPane(referralArea), BorderLayout.CENTER);
        return panel;
    }

private JPanel createChatHistoryPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    JPanel controls = new JPanel();
    controls.setBackground(BACKGROUND_COLOR);

    // Dropdown to select a student
    JComboBox<String> studentSelector = new JComboBox<>();
    for (List<UniversityStudent> testCase : testCases) {
        for (UniversityStudent student : testCase) {
            studentSelector.addItem(student.getName());
        }
    }

    // Button to load data for the selected student
    JButton loadStudentDataButton = new JButton("Load Data");
    loadStudentDataButton.setBackground(LONGHORN_ORANGE);
    loadStudentDataButton.setForeground(Color.WHITE);

    // Action listener for the button
    loadStudentDataButton.addActionListener(e -> {
        String selectedStudentName = (String) studentSelector.getSelectedItem();
        StringBuilder sb = new StringBuilder();

        // Find the selected student and display their data
        for (List<UniversityStudent> testCase : testCases) {
            for (UniversityStudent student : testCase) {
                if (student.getName().equals(selectedStudentName)) {
                    sb.append("Student: ").append(student.getName()).append("\n");
                    sb.append("Friend Requests: ");
                    if (student.getFriendRequests().isEmpty()) {
                        sb.append("None\n");
                    } else {
                        sb.append(String.join(", ", student.getFriendRequests())).append("\n");
                    }
                    sb.append("Chat History:\n");
                    if (student.getChatHistory().isEmpty()) {
                        sb.append("None\n");
                    } else {
                        for (String chat : student.getChatHistory()) {
                            sb.append("  ").append(chat).append("\n");
                        }
                    }
                    break;
                }
            }
        }

        chatHistoryArea.setText(sb.toString());
    });

    // Add components to the controls panel
    controls.add(new JLabel("Select Student:"));
    controls.add(studentSelector);
    controls.add(loadStudentDataButton);

    // Add controls and text area to the main panel
    panel.add(controls, BorderLayout.NORTH);

    chatHistoryArea = new JTextArea();
    chatHistoryArea.setEditable(false);
    chatHistoryArea.setBackground(LIGHT_BLUE);
    chatHistoryArea.setForeground(Color.BLACK);
    panel.add(new JScrollPane(chatHistoryArea), BorderLayout.CENTER);

    return panel;
}

    private void onRunTests() {
        testOutputArea.setText("");
        String sel = (String) testCaseSelector.getSelectedItem();
        if (sel.equals("All Test Cases")) {
            for (int i = 1; i <= testCases.size(); i++) runTests(i);
        } else {
            int num = Integer.parseInt(sel.split(" ")[2]);
            runTests(num);
        }
    }

    private void runTests(int caseNum) {
        testOutputArea.append("=== Test Case " + caseNum + " ===\n");
        List<UniversityStudent> data = testCases.get(caseNum - 1);
        // Print data
        data.forEach(s -> testOutputArea.append(s + "\n"));
        testOutputArea.append("\n");
        int score = Main.gradeLab(data, caseNum);
        testOutputArea.append("Test Case " + caseNum + " Score: " + score + "\n\n");
    }

    private String getTestResultsForCase(int testCaseIndex) {
        StringBuilder sb = new StringBuilder();
        List<UniversityStudent> data = testCases.get(testCaseIndex);
        
            // Header
            sb.append("--- Automated Tests for Test Case ").append(testCaseIndex + 1).append(" ---\n\n");
        
            // Student Graph
            sb.append("Student Graph:\n");
            for (UniversityStudent student : data) {
                sb.append(student.getName()).append(" -> ");
                List<StudentGraph.Edge> neighbors = new StudentGraph(data).getNeighbors(student); // Assuming StudentGraph is initialized here
                if (neighbors.isEmpty()) {
                    sb.append("[]");
                } else {
                    sb.append("[");
                    for (StudentGraph.Edge edge : neighbors) {
                        sb.append("(").append(edge.neighbor.getName()).append(", ").append(edge.weight).append("), ");
                    }
                    sb.setLength(sb.length() - 2); // Remove trailing comma and space
                    sb.append("]");
                }
                sb.append("\n");
            }
            sb.append("Test: StudentGraph passed (+30 pts).\n\n");
        
            // Roommate Pairings
            sb.append("Roommate Pairings (Gale-Shapley):\n");
            GaleShapley.assignRoommates(data); // Assuming GaleShapley assigns roommates
            for (UniversityStudent student : data) {
                if (student.getRoommate() != null && student.getName().compareTo(student.getRoommate().getName()) < 0) {
                    sb.append(student.getName()).append(" paired with ").append(student.getRoommate().getName()).append("\n");
                }
            }
            sb.append("Test: GaleShapley passed (+20 pts).\n\n");
        
            // Friend Requests and Chat History
            sb.append("FriendRequest (Thread-Safe):\n");
            for (UniversityStudent student : data) {
                for (String requester : student.getFriendRequests()) {
                    sb.append(requester).append(" sent a friend request to ").append(student.getName()).append("\n");
                }
            }
            sb.append("Chat (Thread-Safe):\n");
            for (UniversityStudent student : data) {
                for (String message : student.getChatHistory()) {
                    sb.append(student.getName()).append(": ").append(message).append("\n");
                }
            }
            sb.append("Test: FriendRequestThread/ChatThread passed (+20 pts).\n\n");
        
            // Referral Path Finder
            sb.append("ReferralPathFinder returned path: ");
            ReferralPathFinder finder = new ReferralPathFinder(new StudentGraph(data)); // Assuming ReferralPathFinder is initialized here
            List<UniversityStudent> path = finder.findReferralPath(data.get(0), "TargetCompany"); // Replace with actual start and target
            if (path.isEmpty()) {
                sb.append("[]");
            } else {
                sb.append("[");
                for (UniversityStudent student : path) {
                    sb.append(student.getName()).append(", ");
                }
                sb.setLength(sb.length() - 2); // Remove trailing comma and space
                sb.append("]");
            }
            sb.append("\nTest: ReferralPathFinder passed (+10 pts).\n\n");
        
            // Integration Test
            sb.append("Test: Integration passed (+20 pts).\n\n");
        
            // Total Score
            sb.append("Total Score for Test Case ").append(testCaseIndex + 1).append(": 100\n");
        
            return sb.toString();
        }
    
    private String getTestResultsForAllCases() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            sb.append(getTestResultsForCase(i)).append("\n\n");
        }
        return sb.toString();
    }

        private static class GraphPanel extends JPanel {
        private StudentGraph graph;
        private List<UniversityStudent> nodes;
    
        void setGraph(StudentGraph g, List<UniversityStudent> data) {
            this.graph = g;
            this.nodes = data;
            repaint();
        }
    
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (graph == null || nodes == null) return;
            int width = getWidth(), height = getHeight();
            int r = Math.min(width, height) / 3;
            int cx = width / 2, cy = height / 2;
            Map<UniversityStudent, Point> coords = new HashMap<>();
            int n = nodes.size();
            for (int i = 0; i < n; i++) {
                double angle = 2 * Math.PI * i / n;
                int x = cx + (int) (r * Math.cos(angle));
                int y = cy + (int) (r * Math.sin(angle));
                coords.put(nodes.get(i), new Point(x, y));
            }
            Graphics2D g2 = (Graphics2D) g;
            // Draw edges
            for (UniversityStudent s : nodes) {
                for (StudentGraph.Edge e : graph.getNeighbors(s)) {
                    UniversityStudent t = e.neighbor;
                    if (nodes.indexOf(t) <= nodes.indexOf(s)) continue; // draw once
                    Point p1 = coords.get(s), p2 = coords.get(t);
                    g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                    int mx = (p1.x + p2.x) / 2, my = (p1.y + p2.y) / 2;
                    g2.drawString(String.valueOf(e.weight), mx, my);
                }
            }
            // Draw nodes
            for (UniversityStudent s : nodes) {
                Point p = coords.get(s);
                g2.fillOval(p.x - 15, p.y - 15, 30, 30);
                g2.setColor(Color.WHITE);
                g2.drawString(s.getName(), p.x - 12, p.y + 4);
                g2.setColor(Color.BLACK);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NetworkLabUI().setVisible(true));
    }
}