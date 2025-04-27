/*
 * Loading data from Main.java. Use the given main.java to load data. This can be displayed by simply showing the different test cases being loaded once the UI loads up. [20 points]
Visualize the data as a graph. Display a graph that shows students and their names and connections as weighted edges. Must be displayed as a graph. [30 points]
visualize roommates and referral path finder within the student graph.
Visualize each student's friend request and chat history. If 'None' then show 'None'. [10 points]
Intuitive & Friendly User Interface. Is the user interface intuitive to use, are their load data, filter data by student, run buttons or equivalents? Is the user interface just one or two monotone colors or vibrant? [10 points]
 */

 import java.awt.*;
 import java.awt.event.*;
 import java.util.*;
 import java.util.List;
 import javax.swing.*;
 import javax.swing.Timer;
 import java.awt.image.BufferedImage;

 
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
 
         setContentPane(new GlitterBackgroundPanel(tabs));
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
                     if (nodes.indexOf(t) <= nodes.indexOf(s)) continue;
                     Point p1 = coords.get(s), p2 = coords.get(t);
                     g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                     int mx = (p1.x + p2.x) / 2, my = (p1.y + p2.y) / 2;
                     g2.drawString(String.valueOf(e.weight), mx, my);
                 }
             }
 
             for (UniversityStudent s : nodes) {
                 Point p = coords.get(s);
                 g2.fillOval(p.x - 15, p.y - 15, 30, 30);
                 g2.setColor(TEXT_COLOR);
                 g2.drawString(s.getName(), p.x - 20, p.y - 20);
                 g2.setColor(TEXT_COLOR);
             }
         }
     }
 
     public class LonghornMascotAnimation {

        // Custom JPanel for Glitter Effect
        private static class GlitterBackgroundPanel extends JPanel {
            private final JComponent content;
            private final Timer timer;
            private float hueShift = 0f;
            private static final Color BASE_COLOR = new Color(191, 87, 0); // #CC5500
    
            public GlitterBackgroundPanel(JComponent content) {
                this.content = content;
                setLayout(new BorderLayout());
                add(content, BorderLayout.CENTER);
    
                timer = new Timer(100, e -> {
                    hueShift += 0.01f;
                    if (hueShift > 1f) hueShift -= 1f;
                    repaint();
                });
                timer.start();
            }
    
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                float hue = Color.RGBtoHSB(BASE_COLOR.getRed(), BASE_COLOR.getGreen(), BASE_COLOR.getBlue(), null)[0];
                float brightness = 0.85f + 0.15f * (float) Math.sin(hueShift * Math.PI * 2);
                Color shimmerColor = Color.getHSBColor(hue, 1.0f, brightness);
    
                Graphics2D g2d = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, shimmerColor, getWidth(), getHeight(), BASE_COLOR, true);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        }
    
        // Custom JPanel to draw Hook 'em Horns logo
        private static class LonghornLogoPanel extends JPanel {
            private final Random random = new Random();
    
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
    
                // Set the burnt orange background
                setBackground(new Color(191, 87, 0));
    
                // Draw the Hook 'em Horns logo in white pixels
                g.setColor(Color.WHITE);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
                // Simulate the Hook 'em Horns with a simple version
                drawHookEmLogo(g2d);
    
                // Add sparkle animation effect
                addSparkles(g2d);
            }
    
            private void drawHookEmLogo(Graphics2D g2d) {
                // A simple version of the Hook 'em Horns
                g2d.setStroke(new BasicStroke(10));
                g2d.drawLine(150, 200, 300, 100); // Left horn
                g2d.drawLine(300, 100, 450, 200); // Right horn
                g2d.drawLine(250, 200, 400, 200); // Connecting line
            }
    
            private void addSparkles(Graphics2D g2d) {
                // Random sparkle effects around the logo
                for (int i = 0; i < 50; i++) {
                    int x = random.nextInt(getWidth());
                    int y = random.nextInt(getHeight());
                    int size = random.nextInt(4) + 1;
                    g2d.setColor(new Color(255, 255, 255, random.nextInt(255)));
                    g2d.fillOval(x, y, size, size);
                }
            }
        }
    
        public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> {
                // Set up frame
                JFrame frame = new JFrame("Longhorn Hook 'Em Horns");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(600, 400);
    
                // Set up content panel with glitter effect
                LonghornLogoPanel logoPanel = new LonghornLogoPanel();
                GlitterBackgroundPanel glitterPanel = new GlitterBackgroundPanel(logoPanel);
                frame.add(glitterPanel);
    
                // Display the window
                frame.setVisible(true);
            });
        }
    }
    
 
     public static void main(String[] args) {
         SwingUtilities.invokeLater(() -> new NetworkLabUI().setVisible(true));
     }
 }
 