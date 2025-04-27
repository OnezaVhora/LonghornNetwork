//** NOTE: this is about a bit less than 50% of the lab grade covered, there will be a a bit of a stretch to get a 100.

/*
 * Loading data from Main.java. Use the given main.java to load data. This can be displayed by simply showing the different test cases being loaded once the UI loads up. [20 points]
Visualize the data as a graph. Display a graph that shows students and their names and connections as weighted edges. Must be displayed as a graph. [30 points]
visualize roommates and referral path finder within the student graph. [15 points each] 
Visualize each student's friend request and chat history. If 'None' then show 'None'. [10 points]
Intuitive & Friendly User Interface. Is the user interface intuitive to use, are their load data, filter data by student, run buttons or equivalents? Is the user interface just one or two monotone colors or vibrant? [10 points]
 */
import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class NetworkLabUI extends JFrame {
    private JComboBox<String> testCaseSelector; //combo box for selecting the testcase
    private JButton runTestsButton;
    private JTextArea testOutputArea;
    private GraphPanel graphPanel;
    private JTextArea roommateArea;
    private JComboBox<String> startStudentSelector;
    private JTextField targetCompanyField;
    private JTextArea referralArea;

    private List<List<UniversityStudent>> testCases;

    public NetworkLabUI() {
        super("Longhorn Network Lab UI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);

        // Prepare test cases
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
        tabs.addTab("Friend Requests & Chat", createFriendChatPanel());

        add(tabs);
    }

    private JPanel createTestRunnerPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel top = new JPanel();
        testCaseSelector = new JComboBox<>(new String[]{"Test Case 1", "Test Case 2", "Test Case 3", "All Test Cases"});
        runTestsButton = new JButton("Run Tests");
        runTestsButton.addActionListener(e -> onRunTests());
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
        JComboBox<String> graphCaseSelector = new JComboBox<>(new String[]{"Test Case 1", "Test Case 2", "Test Case 3"});
        JButton loadGraphButton = new JButton("Load Graph");
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
        panel.add(graphPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createRoommatePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel controls = new JPanel();
        JComboBox<String> rmCaseSelector = new JComboBox<>(new String[]{"Test Case 1", "Test Case 2", "Test Case 3"});
        JButton computeButton = new JButton("Compute Roommates");
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
        panel.add(new JScrollPane(roommateArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createReferralPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel controls = new JPanel();
        JComboBox<String> refCaseSelector = new JComboBox<>(new String[]{"Test Case 1", "Test Case 2", "Test Case 3"});
        startStudentSelector = new JComboBox<>();
        targetCompanyField = new JTextField(10);
        JButton findButton = new JButton("Find Path");
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
        panel.add(new JScrollPane(referralArea), BorderLayout.CENTER);
        return panel;
    }

        private JPanel createFriendChatPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel controls = new JPanel();
        
        JComboBox<String> studentSelector = new JComboBox<>();
        JButton loadButton = new JButton("Load Data");
        JTextArea friendChatArea = new JTextArea();
        friendChatArea.setEditable(false);
    
        // Populate student selector when the tab is loaded
        studentSelector.addActionListener(e -> {
            int idx = testCaseSelector.getSelectedIndex();
            List<UniversityStudent> data = testCases.get(idx);
            studentSelector.removeAllItems();
            data.forEach(s -> studentSelector.addItem(s.getName()));
        });
    
        loadButton.addActionListener(e -> {
            int idx = testCaseSelector.getSelectedIndex();
            List<UniversityStudent> data = testCases.get(idx);
            String selectedName = (String) studentSelector.getSelectedItem();
            UniversityStudent student = data.stream()
                                            .filter(s -> s.getName().equals(selectedName))
                                            .findFirst()
                                            .orElse(null);
            if (student != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("Friend Requests:\n");
                sb.append(student.getFriendRequests().isEmpty() ? "None" : String.join(", ", student.getFriendRequests()));
                sb.append("\n\nChat History:\n");
                sb.append(student.getChatHistory().isEmpty() ? "None" : String.join("\n", student.getChatHistory()));
                friendChatArea.setText(sb.toString());
            }
        });
    
        controls.add(new JLabel("Select Student:"));
        controls.add(studentSelector);
        controls.add(loadButton);
        panel.add(controls, BorderLayout.NORTH);
        panel.add(new JScrollPane(friendChatArea), BorderLayout.CENTER);
    
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

    // Custom panel to draw the graph
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