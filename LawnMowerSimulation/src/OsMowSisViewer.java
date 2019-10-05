import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.util.ArrayList;

public class OsMowSisViewer extends JFrame {
    private static int ROWS = Grid.getROWS();
    private static int COLS = Grid.getCOLS();
    public static int pauseTime = 300;
    public static boolean hitNext ;
    public static boolean hitFastForward = false;
    public static boolean hitStop = false;

    JPanel lawnPanel = new JPanel();
    static public JTextArea display = new JTextArea(7 + FileReader.mowers.size(),30);
    static private JLabel labels[][] = new JLabel[ROWS][COLS];

    static private JButton nextButton = new JButton("Next");
    static private JButton fastForward = new JButton("Fast-Forward");
    static private JButton stopButton = new JButton("Stop");
    static private JButton flashButton = new JButton("Flash ON/OFF");


    public OsMowSisViewer() {
        super("OsMowSis Lawn Mower Simulator");
    }

    public void createDisplay() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0 ));
        contentPane.setBackground(Color.GRAY);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JPanel displayPanel = new JPanel();
        Font font = new Font("Courier", Font.BOLD,10);
        display.setFont(font);
        display.setForeground(Color.DARK_GRAY);
        display.setLineWrap(true);
        displayPanel.add(display);

        JPanel nextButtonPanel = new JPanel();
        nextButton.addActionListener(ae -> hitNext = !hitNext);
        nextButtonPanel.add(nextButton);
        nextButtonPanel.setBackground(Color.GRAY);

        JPanel fastForwardPanel = new JPanel();
        fastForward.addActionListener(ae -> hitNext = false);
        fastForward.addActionListener(ae -> hitFastForward = true);
        fastForward.addActionListener(ae -> pauseTime = 0);
        fastForwardPanel.add(fastForward);
        fastForwardPanel.setBackground(Color.GRAY);

        JPanel stopButtonPanel = new JPanel();
        stopButton.addActionListener(ae -> hitStop = true);
        stopButtonPanel.add(stopButton);
        stopButtonPanel.setBackground(Color.GRAY);

        JPanel flashButtonPanel = new JPanel();
        flashButton.addActionListener(ae -> Mower.flashON = ! Mower.flashON);
        flashButtonPanel.add(flashButton);
        flashButtonPanel.setBackground(Color.GRAY);

        leftPanel.add(displayPanel);
        leftPanel.add(nextButtonPanel);
        leftPanel.add(fastForwardPanel);
        leftPanel.add(stopButtonPanel);
        leftPanel.add(flashButtonPanel);
        contentPane.add(leftPanel);

        lawnPanel.setLayout(new GridLayout(ROWS,COLS));
        lawnPanel.setBorder(BorderFactory.createLineBorder(Color.getHSBColor(345,67,22), 10));
        initializeLabels();
        contentPane.add(lawnPanel);

        setContentPane(contentPane);
        pack();
        setLocationByPlatform(true);
        setVisible(true);
    }

    public void initializeLabels() {
        for (int row=0; row<ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                labels[row][col] = new JLabel();
                lawnPanel.add(labels[row][col]);
            }
        }
        drawLawn(pauseTime);
    }

    public static void pause(int milliseconds){
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    // Update the lawn without drawing it.
    public static void update(){
        ArrayList<Mower> mowers = FileReader.mowers;
        Square[][] squares = Grid.getSquares();
        for (Mower mower : mowers) {
            squares[mower.getY_pos()][mower.getX_pos()].setMarker(mower.getMowerMark());
            String mowerImage = Grid.getDirectionToImage().get(mower.getMowerDirection());
            if (!mower.getState().equals("OFF")) {
                mower.mowerIcon = new ImageIcon(mowerImage);
            }
            squares[mower.getY_pos()][mower.getX_pos()].setSquareIcon(mower.mowerIcon);
            labels[mower.getY_pos()][mower.getX_pos()].setText("" + mower.getMowerNumber());
            labels[mower.getY_pos()][mower.getX_pos()].setForeground(Color.LIGHT_GRAY);
            Font font = new Font("Courier", Font.BOLD,25);
            labels[mower.getY_pos()][mower.getX_pos()].setFont(font);
            labels[mower.getY_pos()][mower.getX_pos()].setHorizontalTextPosition(JLabel.CENTER);
            labels[mower.getY_pos()][mower.getX_pos()].setVerticalTextPosition(JLabel.CENTER);
        }

        Double percentCut = SimController.getLawn().percentCut();
        display.setText(String.format("%.2f",percentCut) + "% of lawn mowed. ");
        display.append(Integer.toString(Mower.totalTurns) + " / " + FileReader.MAX_TURNS + " turns taken\n");
        for (Mower mower : mowers) {
            String mowerName = "mower_" + Integer.toString(mower.getMowerNumber());
            String chargeLevel = Integer.toString(mower.getChargeLevel());
            display.append(mowerName + ":  " + chargeLevel + " charge units\n");
        }
        display.append("\n");
        for (Mower mower : mowers) {
            if (mower.getMowerNumber() == Mower.activeMower) {
                String mowerName = "mower_" + Integer.toString(mower.getMowerNumber()) + "\n";
                display.append(mowerName + mower.mowerOutput);
            }
        }
    }

    public static void drawLawn(int pauseTime) {
        Square[][] squares = Grid.getSquares();
        update();
        // draw lawn frame.
        for (int row = 0; row < ROWS; row++){
            for (int col = 0; col < COLS; col++){
                labels[row][col].setIcon(squares[row][col].getSquareIcon());
            }
        }
        pause(pauseTime);
        for (int row = 0; row < ROWS; row++){
            for (int col = 0; col < COLS; col++){
                //labels[row][col].setIcon(squares[row][col].getSquareIcon());
                labels[row][col].setText("");
                labels[row][col].setHorizontalTextPosition(JLabel.CENTER);
                labels[row][col].setVerticalTextPosition(JLabel.CENTER);
            }
        }
    }

    public static JLabel[][] getLabels() {
        return labels;
    }
}
