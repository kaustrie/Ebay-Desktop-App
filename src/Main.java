
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Component;
import javax.swing.Box;

public class Main extends JFrame implements ActionListener {

	private static Categories categ;
	private static Categories categ2;
	private static Search search = new Search("harry porter", "158658","92074");
	private JPanel contentPane;
	private static JInternalFrame ebaySearchFrame;
	private JTextField keywordText;
	private JComboBox childCatBox;
	private JComboBox categoryBox;
	private JComboBox childCatBox2;
	private JComboBox categoryBox2;
	private JLabel errorLabel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					categ = new Categories();
					categ2 = new Categories();
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Main() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 724, 487);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
		ebaySearchFrame = new JInternalFrame("ebay Search");
		ebaySearchFrame.getContentPane().setVisible(true);
		ebaySearchFrame.setClosable(true);
		ebaySearchFrame.setBackground(Color.ORANGE);
		ebaySearchFrame.setForeground(Color.LIGHT_GRAY);
		ebaySearchFrame.setBounds(10, 32, 688, 377);
		contentPane.add(ebaySearchFrame); 
		ebaySearchFrame.getContentPane().setLayout(null);
		
		JButton btnSearch = new JButton("Search");
		btnSearch.setActionCommand("Search");
		btnSearch.addActionListener(this);
		
		btnSearch.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnSearch.setBounds(282, 300, 89, 23);
		ebaySearchFrame.getContentPane().add(btnSearch);
		
		
		keywordText = new JTextField();
		keywordText.setBounds(200, 115, 240, 20);
		ebaySearchFrame.getContentPane().add(keywordText);
		keywordText.setColumns(10);
		
		categoryBox = new JComboBox();
		categoryBox.setToolTipText("Please select Category");
		categoryBox.addActionListener(this);
		categoryBox.setActionCommand("Cat1");
		String[] list = categ.toArray();
		System.out.println("This is list size "+ list.length);
		categoryBox.setModel(new DefaultComboBoxModel(list));
		categoryBox.setBounds(46, 184, 260, 20);
		ebaySearchFrame.getContentPane().add(categoryBox);
		
		childCatBox = new JComboBox();
		childCatBox.setBounds(46, 215, 260, 20);
		childCatBox.setVisible(false);
		ebaySearchFrame.getContentPane().add(childCatBox);
		
		categoryBox2 = new JComboBox();
		categoryBox2.setToolTipText("Please select Category");
		categoryBox2.addActionListener(this);
		categoryBox2.setActionCommand("Cat2");
		String[] list2 = categ2.toArray();
		categoryBox2.setModel(new DefaultComboBoxModel(list2));
		categoryBox2.setBounds(331, 184, 260, 20);
		ebaySearchFrame.getContentPane().add(categoryBox2);
		
		childCatBox2 = new JComboBox();
		childCatBox2.setToolTipText("Please select Category");
		childCatBox2.setBounds(331, 215, 260, 20);
		childCatBox2.setVisible(false);
		ebaySearchFrame.getContentPane().add(childCatBox2);
		
		JLabel lblSelectCategoryFor = new JLabel("Select Category for search");
		lblSelectCategoryFor.setBounds(46, 159, 152, 14);
		ebaySearchFrame.getContentPane().add(lblSelectCategoryFor);
		
		JLabel lblSelectSecondCategory = new JLabel("Select second Category for search");
		lblSelectSecondCategory.setBounds(331, 159, 198, 14);
		ebaySearchFrame.getContentPane().add(lblSelectSecondCategory);
		
		JLabel lblKeywords = new JLabel("Keyword(s)");
		lblKeywords.setBounds(200, 90, 89, 14);
		ebaySearchFrame.getContentPane().add(lblKeywords);
		
		errorLabel = new JLabel("");
		errorLabel.setForeground(Color.RED);
		errorLabel.setBounds(119, 31, 410, 23);
		ebaySearchFrame.getContentPane().add(errorLabel);
		
		JMenuBar mainMenu = new JMenuBar();
		mainMenu.setBounds(0, 0, 97, 21);
		contentPane.add(mainMenu);

		JMenu mnSearch = new JMenu("Search");
		mainMenu.add(mnSearch);
		
		JMenuItem searchEbay = new JMenuItem("eBay");
		searchEbay.addActionListener(this);
		mnSearch.add(searchEbay);
		
		JMenuItem searchDatabase = new JMenuItem("Database");
		searchDatabase.addActionListener(this);
		mnSearch.add(searchDatabase);
		
		
		
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String  item;
      
           item = event.getActionCommand(); //gets command associated to the event
           //Verifies which reaction the command relates
           //to and then performs the necessary action
           if (item.equals("eBay")){ //user inputs values for new Polynomial
              	ebaySearchFrame.setVisible(true);
             	
           }
           else if (item.equals("Cat1")){
        	   JComboBox box = (JComboBox)event.getSource();
        	   int i = box.getSelectedIndex();
        	   loadChildren(i);
        	  
           }
           else if (item.equals("Cat2")){
        	   JComboBox box = (JComboBox)event.getSource();
        	   int i = box.getSelectedIndex();
        	   loadChildren2(i);
        	  
           }
           else if(item.equals("Search")){
        	   if (keywordText.getText() == null || childCatBox.getSelectedIndex() == -1){
        		  errorLabel.setText("Please enter keyword and select category before attempting to search.");
        		  errorLabel.setVisible(true);
        	   }
        	   else{
        		   errorLabel.setVisible(false);
        	   int i = childCatBox.getSelectedIndex();
        	   int catID1 = categ.getChildId(i);
        	   int j = childCatBox2.getSelectedIndex();
        	   int catID2 = categ2.getChildId(j);     	  
           	   String keyword = keywordText.getText();
           	   search = new Search(keyword, ((Integer)(catID1)).toString(), ((Integer)(catID2)).toString());
        	   System.out.println("Id1: " +catID1+" Id2: "+catID2+" keyword: "+keyword);
        	   }
           }
		
	}
	
	public void loadChildren(int index){
		String[] list = categ.toChildArray(index);
		System.out.println("This is list size "+ list.length);
		childCatBox.setModel(new DefaultComboBoxModel(list));
		childCatBox.setVisible(true);
	}
	
	public void loadChildren2(int index){
		String[] list = categ2.toChildArray(index);
		System.out.println("This is list size "+ list.length);
		childCatBox2.setModel(new DefaultComboBoxModel(list));
		childCatBox2.setVisible(true);
	}
}
