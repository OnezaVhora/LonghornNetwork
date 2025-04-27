import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;

public class NetworkLabUI extends JFrame {
    private JComboBox<String> testCaseSelector;
    private JButton runTestsButton;
    private JTextArea testOutputArea;
    private GraphPanel graphPanel;
    private JTextArea roommateArea;
    private JComboBox<String> startStudentSelector;
    private JTextField targetCompanyField;
    private JTextArea referralArea;
    private List<List<UniversityStudent>> testCases;

    private static final Color BACKGROUND_COLOR = Color.decode("#BF5700");
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final String LOGO_PATH = "C:\\Users\\oneza\\OneDrive\\Desktop\\lab5\\LonghornNetwork\\logo.png";
    //"C:\Users\oneza\OneDrive\Desktop\lab5\LonghornNetwork\logo.png"
    private JLabel logoLabel;

    public NetworkLabUI() {
        super("Longhorn Network Lab UI");
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

        //setContentPane(new GlitterBackgroundPanel(createMainPanel(tabs)));
        setContentPane(createMainPanel(tabs));  
    }

    private JPanel createMainPanel(JTabbedPane tabs) {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        
        // Logo Panel
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(BACKGROUND_COLOR);
        logoLabel = createLogoLabel(LOGO_PATH);
        logoPanel.add(logoLabel);

        // Add logo and tabs to the main panel
        mainPanel.add(logoPanel, BorderLayout.NORTH);
        mainPanel.add(tabs, BorderLayout.CENTER);

        return mainPanel;
    }

    private JLabel createLogoLabel(String path) {
        try {
            BufferedImage logoImage = ImageIO.read(new File(path));
            ImageIcon logoIcon = new ImageIcon(logoImage.getScaledInstance(150, 150, Image.SCALE_SMOOTH));
            JLabel logoLabel = new JLabel(logoIcon);
            return logoLabel;
        } catch (Exception e) {
            e.printStackTrace();
            return new JLabel("Logo not found");
        }
    }

    private JPanel createTestRunnerPanel() {
        JPanel panel = createStyledPanel(new BorderLayout());
        JPanel top = createStyledPanel();
        testCaseSelector = new JComboBox<>(new String[]{"Test Case 1", "Test Case 2", "Test Case 3", "All Test Cases"});
        runTestsButton = new JButton("Run Tests");
        styleButton(runTestsButton);
        runTestsButton.addActionListener(e -> onRunTests());

        top.add(createStyledLabel("Select Test Case:"));
        top.add(testCaseSelector);
        top.add(runTestsButton);

        panel.add(top, BorderLayout.NORTH);
        testOutputArea = createStyledTextArea();
        panel.add(new JScrollPane(testOutputArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createGraphViewerPanel() {
        JPanel panel = createStyledPanel(new BorderLayout());
        JPanel controls = createStyledPanel();
        JComboBox<String> graphCaseSelector = new JComboBox<>(new String[]{"Test Case 1", "Test Case 2", "Test Case 3"});
        JButton loadGraphButton = new JButton("Load Graph");
        styleButton(loadGraphButton);

        loadGraphButton.addActionListener(e -> {
            int idx = graphCaseSelector.getSelectedIndex();
            List<UniversityStudent> data = testCases.get(idx);
            StudentGraph graph = new StudentGraph(data);
            graphPanel.setGraph(graph, data);
        });

        controls.add(createStyledLabel("Select Data:"));
        controls.add(graphCaseSelector);
        controls.add(loadGraphButton);

        panel.add(controls, BorderLayout.NORTH);
        graphPanel = new GraphPanel();
        panel.add(graphPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createRoommatePanel() {
        JPanel panel = createStyledPanel(new BorderLayout());
        JPanel controls = createStyledPanel();
        JComboBox<String> rmCaseSelector = new JComboBox<>(new String[]{"Test Case 1", "Test Case 2", "Test Case 3"});
        JButton computeButton = new JButton("Compute Roommates");
        styleButton(computeButton);

        computeButton.addActionListener(e -> {
            int idx = rmCaseSelector.getSelectedIndex();
            List<UniversityStudent> data = testCases.get(idx);
            data.forEach(s -> s.setRoommate(null));
            GaleShapley.assignRoommates(data);
            StringBuilder sb = new StringBuilder();
            for (UniversityStudent s : data) {
                if (s.getRoommate() != null && s.getName().compareTo(s.getRoommate().getName()) < 0) {
                    sb.append(s.getName()).append(" ↔ ").append(s.getRoommate().getName()).append("\n");
                }
            }
            roommateArea.setText(sb.toString());
        });

        controls.add(createStyledLabel("Select Data:"));
        controls.add(rmCaseSelector);
        controls.add(computeButton);

        panel.add(controls, BorderLayout.NORTH);
        roommateArea = createStyledTextArea();
        panel.add(new JScrollPane(roommateArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createReferralPanel() {
        JPanel panel = createStyledPanel(new BorderLayout());
        JPanel controls = createStyledPanel();
        JComboBox<String> refCaseSelector = new JComboBox<>(new String[]{"Test Case 1", "Test Case 2", "Test Case 3"});
        startStudentSelector = new JComboBox<>();
        targetCompanyField = new JTextField(10);
        JButton findButton = new JButton("Find Path");
        styleButton(findButton);

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
                path.forEach(s -> sb.append(s.getName()).append(" → "));
                if (!path.isEmpty()) sb.setLength(sb.length() - 2);
                referralArea.setText(sb.toString());
            }
        });

        refCaseSelector.addActionListener(e -> {
            int idx = refCaseSelector.getSelectedIndex();
            List<UniversityStudent> data = testCases.get(idx);
            startStudentSelector.removeAllItems();
            data.forEach(s -> startStudentSelector.addItem(s.getName()));
        });
        refCaseSelector.setSelectedIndex(0);

        controls.add(createStyledLabel("Data:"));
        controls.add(refCaseSelector);
        controls.add(createStyledLabel("Start:"));
        controls.add(startStudentSelector);
        controls.add(createStyledLabel("Target Company:"));
        controls.add(targetCompanyField);
        controls.add(findButton);

        panel.add(controls, BorderLayout.NORTH);
        referralArea = createStyledTextArea();
        panel.add(new JScrollPane(referralArea), BorderLayout.CENTER);
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
        data.forEach(s -> testOutputArea.append(s + "\n"));
        testOutputArea.append("\n");
        int score = Main.gradeLab(data, caseNum);
        testOutputArea.append("Test Case " + caseNum + " Score: " + score + "\n\n");
    }

    private JPanel createStyledPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BACKGROUND_COLOR);
        return panel;
    }

    private JPanel createStyledPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(BACKGROUND_COLOR);
        return panel;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private JTextArea createStyledTextArea() {
        JTextArea area = new JTextArea();
        area.setBackground(BACKGROUND_COLOR);
        area.setForeground(TEXT_COLOR);
        area.setEditable(false);
        return area;
    }

    private void styleButton(JButton button) {
        button.setBackground(TEXT_COLOR);
        button.setForeground(BACKGROUND_COLOR);
        button.setFocusPainted(false);
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

            setBackground(BACKGROUND_COLOR);
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(TEXT_COLOR);

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

            for (UniversityStudent s : nodes) {
                for (StudentGraph.Edge e : graph.getNeighbors(s)) {
                    UniversityStudent t = e.neighbor;
                    if (coords.containsKey(t)) {
                        Point p1 = coords.get(s);
                        Point p2 = coords.get(t);
                        g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                    }
                }
            }

            for (Map.Entry<UniversityStudent, Point> entry : coords.entrySet()) {
                UniversityStudent student = entry.getKey();
                Point point = entry.getValue();
                g2.fillOval(point.x - 10, point.y - 10, 20, 20);
                g2.drawString(student.getName(), point.x + 12, point.y + 5);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new NetworkLabUI().setVisible(true);
        });
    }
}
