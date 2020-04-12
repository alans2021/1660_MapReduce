import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class GUI {

    private JFrame frame;
    private JPanel panel;
    private JButton button1;
    private JButton button2;
    private JButton search;
    private JButton topN;
    private JButton action;
    private JTextArea fileNames;
    private JTextArea textArea;
    private JTextArea select;
    private JTextField term;
    private JLabel hyperlink;
    private JScrollPane jScrollPane;
    private String[] dataFiles;
//    private GCP_Connection connect;
    private PostingList postingList;

    private TopN obj;
    public GUI() {
        frame = new JFrame("Alan Shen Search Engine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 900);

        panel = new JPanel();
        panel.setSize(900, 900);
        GridLayout experimentLayout = new GridLayout(6, 3);
        panel.setLayout(experimentLayout);

        textArea = new JTextArea("Load My Engine");
        textArea.setFont(new Font("Serif", Font.BOLD,40));
        panel.add(textArea);

        button1 = new JButton("Choose Files");
        button1.addActionListener(new ClickListener());
        button1.setLocation(2, 2);
        panel.add(button1);

        fileNames = new JTextArea();
        fileNames.setFont(new Font("Serif", Font.PLAIN,15));
        panel.add(fileNames);

        button2 = new JButton("Construct Inverted Indices");
        button2.addActionListener(new ClickListener());
        button2.setEnabled(false);
        panel.add(button2);

        frame.add(panel);
        frame.setVisible(true);
    }

    private class ClickListener implements ActionListener {

        public void actionPerformed(ActionEvent e){
            if(e.getSource() == button1){
                JFileChooser chooser = new JFileChooser("C:\\Users\\alans\\Documents" +
                        "\\College\\Junior year\\Spring\\CS 1660\\Project\\Data");
                chooser.setMultiSelectionEnabled(true);
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = chooser.showOpenDialog(frame);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    File[] chosen = chooser.getSelectedFiles();
                    dataFiles = new String[chosen.length];
                    button2.setEnabled(true);
                    for(int i = 0; i < chosen.length; i++){
                        File f = chosen[i];
                        fileNames.append(f.getName() + "\n");
                        dataFiles[i] = f.getName();
                    }
                }
            }
            else if(e.getSource() == button2){
                new GCP_Connection(dataFiles);
                //postingList = new PostingList(dataFiles);

                panel.remove(fileNames);
                panel.remove(button1);
                panel.remove(button2);

                textArea.setText("Engine was loaded & \n");
                textArea.append("Inverted Indices were constructed successfully");
                select = new JTextArea("Please Select Action");
                select.setFont(new Font("Serif", Font.BOLD,40));
                panel.add(select);

                search = new JButton("Search for Term");
                topN = new JButton("Top-N");
                search.addActionListener(new SecondayClickListener());
                topN.addActionListener(new SecondayClickListener());
                panel.add(search);
                panel.add(topN);
            }
        }
    }

    private class SecondayClickListener implements ActionListener{

        public void actionPerformed(ActionEvent e) {
            panel.remove(select);
            panel.remove(search);
            panel.remove(topN);

            postingList = new PostingList(dataFiles);
            if(e.getSource() == search) {
                textArea.setText("Enter Your Search Term");
                term = new JTextField("Type Your Search Term...");
            }
            else {
                textArea.setText("Enter Your N Value");
                term = new JTextField("Type Your N...");
                term.addKeyListener(new FieldKeyListener());
            }

            action = new JButton("Search");
            action.addActionListener(new AlgorithmClickListener());

            panel.add(term);
            panel.add(action);
        }
    }

    private class AlgorithmClickListener implements ActionListener{

        public void actionPerformed(ActionEvent e) {
            JTable table;
            String text = term.getText();
            if(text != null && !text.equals("")){
                if(textArea.getText().contains("Search")){
                    Search test = new Search(text);
                    textArea.setText("You searched for: " + test.getTerm() + "\n");
                    textArea.append("Executed in " + test.getElapsedTime() + "ms");
                    table = new JTable(test.getData(), test.getColumnNames());
                }
                else{
                    table = new JTable();
                    if(obj != null){
                        textArea.setText("Top-N Frequent Terms");
                        obj.setData();
                        table = new JTable(obj.getData(), obj.getColumns());
                    }
                }
                panel.remove(term);
                panel.remove(action);
                panel.remove(search);
                panel.remove(topN);
                jScrollPane = new JScrollPane();
                jScrollPane.setViewportView(table);
                panel.add(jScrollPane);
                reset();
            }
        }

        private void reset(){
            hyperlink = new JLabel("Back to Search");
            hyperlink.setForeground(Color.BLUE.darker());
            hyperlink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            hyperlink.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    panel.remove(hyperlink);
                    panel.remove(jScrollPane);
                    textArea.setText("Engine was loaded & \n");
                    textArea.append("Inverted Indices were constructed successfully");
                    select = new JTextArea("Please Select Action");
                    select.setFont(new Font("Serif", Font.BOLD,40));
                    panel.add(select);

                    search = new JButton("Search for Term");
                    topN = new JButton("Top-N");
                    search.addActionListener(new SecondayClickListener());
                    topN.addActionListener(new SecondayClickListener());
                    panel.add(search);
                    panel.add(topN);
                }
                public void mouseEntered(MouseEvent e) {}
                public void mouseExited(MouseEvent e) {}
            });
            panel.add(hyperlink);
        }
    }

    private class FieldKeyListener implements KeyListener{
        public void keyPressed(KeyEvent e) {}
        public void keyReleased(KeyEvent e) {
            try{
                int n = Integer.parseInt(term.getText());
                obj = new TopN(n);
                textArea.setText("Inverted indices were constructed successfully");
            }
            catch (Exception ex){}
        }
        public void keyTyped(KeyEvent e) { }
    }
}
