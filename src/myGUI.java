/* Name: Thomas Dathe
 Course: CNT 4714 – Fall 2023
 Assignment title: Project 1 – Event-driven Enterprise Simulation
 Date: Sunday September 17, 2023
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class myGUI extends JFrame {

    //variables for the GUI
	private JTextField itemIdField;
    private JTextField quantityField;
    private JTextArea detailsTextArea;
    private JTextArea subtotalField;
    private JTextArea cartTextArea;
    
    private String[] itemIDString; 
    private String[] descriptions; 
    private boolean[] inStockStrings; 
    private int[] quantities; 
    private double[] prices; 
    
    private DecimalFormat decimalFormat = new DecimalFormat("0.00");
    
    private List<Item> shoppingCart = new ArrayList<>(); 

    public myGUI() {
        // main frame
    	
    	
    	// windows look and feel
    	
    	 try {
             UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
         } catch (Exception e) {
             e.printStackTrace();
         }
    	 
        setTitle("ShoppingCart.com - Fall 2023");
        setSize(1000, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(4, 2));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 

        JLabel label1 = new JLabel("Enter item ID for Item: ");
        itemIdField = new JTextField();
        JLabel label2 = new JLabel("Enter quantity for Item: ");
        quantityField = new JTextField();
        JLabel label3 = new JLabel("Details for Item: ");
        detailsTextArea = new JTextArea();
        JLabel label4 = new JLabel("Order subtotal for Item(s): ");
        subtotalField = new JTextArea();
        
        cartTextArea = new JTextArea(); 

        topPanel.add(label1);
        topPanel.add(itemIdField);
        topPanel.add(label2);
        topPanel.add(quantityField);
        topPanel.add(label3);
        topPanel.add(detailsTextArea);
        topPanel.add(label4);
        topPanel.add(subtotalField);

        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(3, 2, 10, 10)); 

        JButton button1 = new JButton("Find Item");
        JButton button2 = new JButton("View Cart");
        JButton button3 = new JButton("Empty Cart - Start Over");
        JButton button4 = new JButton("Add Item to Cart");
        JButton button5 = new JButton("Check Out");
        JButton button6 = new JButton("Exit (Close App)");

        bottomPanel.add(button1);
        bottomPanel.add(button2);
        bottomPanel.add(button3);
        bottomPanel.add(button4);
        bottomPanel.add(button5);
        bottomPanel.add(button6);
        
        

        // action listeners for different buttons 
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // find item button by ID
            	
            	String enteredItemID = itemIdField.getText().trim();
            	
            	findItemByID(enteredItemID);
            	
            }
        });

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cart View before checkout 
            	
            	showCart(); 
            }
        });

        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Start Over Button 
            	itemIdField.setText("");
                quantityField.setText("");
                detailsTextArea.setText("");
                subtotalField.setText("");
                cartTextArea.setText("");
                
                shoppingCart.clear();
            	
            }
        });

        button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Add to Cart Button 
            	
            	
                    
                    String enteredItemID = itemIdField.getText().trim();
                    
                   
                    String quantityStr = quantityField.getText().trim();
                    int qty = 0;
                    try {
                        qty = Integer.parseInt(quantityStr);
                    } catch (NumberFormatException ex) {
                        
                        detailsTextArea.setText("Invalid quantity input");
                        return; 
                    }
                    
                    
                    Item foundItem = findItemInInventory(enteredItemID);
                    
                    if (foundItem != null) {
                       
                        if (foundItem.getQuantity() >= qty) {
                           
                            foundItem.setQuantity(foundItem.getQuantity() - qty);
                            
                            
                            shoppingCart.add(new Item(foundItem.getId(), foundItem.getDescription(), true, qty, foundItem.getPrice()));
                            
                            
                            detailsTextArea.setText("Item added to cart!");
                        } else {
                            
                            detailsTextArea.setText("Cannot buy " + qty + " of item. Only " + foundItem.getQuantity() + " are available.");
                        }
                    } else {
                        
                        detailsTextArea.setText("Item not found");
                    }
                }
            private Item findItemInInventory(String itemID) {
                for (int i = 0; i < itemIDString.length; i++) {
                    if (itemIDString[i] != null && itemIDString[i].equals(itemID)) {
                        return new Item(itemIDString[i], descriptions[i], inStockStrings[i], quantities[i], prices[i]);
                    }
                }
                return null; 
                
                
            
        }   
      });   
        

        button5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Checkout and End program button 
            	
            	performCheckout();      	
            	
            	
            }
        });
        
        

        button6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
            	//Exit program button 
            	
                System.exit(0); 
            }
        });

        
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(bottomPanel, BorderLayout.CENTER);

        
        setVisible(true);
        
     
        int itemCount = 100; 
        itemIDString = new String[itemCount];
        descriptions = new String[itemCount];
        inStockStrings = new boolean[itemCount];
        quantities = new int[itemCount];
        prices = new double[itemCount];
        
        readInputFile("inventory.csv"); 
    }
    
    public void readInputFile(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            int itemCount = 0;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    itemIDString[itemCount] = parts[0].trim();
                    descriptions[itemCount] = parts[1].trim();
                    inStockStrings[itemCount] = Boolean.parseBoolean(parts[2].trim());
                    quantities[itemCount] = Integer.parseInt(parts[3].trim());
                    prices[itemCount] = Double.parseDouble(parts[4].trim());
                    itemCount++;
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void findItemByID(String itemID) {
        
    	
    	int marker = 0; 
    	String quantityStr = quantityField.getText().trim();

       
        int qty = 0;

       
        try {
            qty = Integer.parseInt(quantityStr);
        } catch (NumberFormatException ex) {
           
            detailsTextArea.setText("Invalid quantity input");
            return; 
        }
        
    	
        for (int i = 0; i < itemIDString.length; i++) {
            if (itemIDString[i] != null && itemIDString[i].equals(itemID)) {
                
            	
            	DecimalFormat decimalFormat = new DecimalFormat("0.00"); 
                String details = itemIDString[i] + " " + descriptions[i] + " $" + prices[i] + " " + qty + " $" + decimalFormat.format(prices[i] * qty);
                
               
                detailsTextArea.setText(details);
                double total = (prices[i] * qty);
                String totalStr = "$" + decimalFormat.format(total) + ""; 
                subtotalField.setText(totalStr);
                marker = 1; 
            }
        }
        if(marker == 0) {
        	detailsTextArea.setText("Item not found!");
        	
        }
    }
    
    private class Item {
        String id;
        String description;
        boolean inStock;
        int quantity;
        double price;

        public Item(String id, String description, boolean inStock, int quantity, double price) {
            this.id = id;
            this.description = description;
            this.inStock = inStock;
            this.quantity = quantity;
            this.price = price;
        }

        // Getters and Setters
        public int getQuantity() {
            return quantity;
        }

        public String getDescription() {
            return description;
        }
        public String getId() {
            return id;
        }
        public boolean isInStock() {
            return inStock;
        }
        public double getPrice() {
            return price;
        }
        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
        
        

       
        
    }
    
    private void showCart() {
        
        JFrame cartFrame = new JFrame("Shopping Cart - ShoppingCart.com");
        cartFrame.setSize(400, 300);

      
        JTextArea cartDetailsTextArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(cartDetailsTextArea);
        cartDetailsTextArea.setEditable(false);

        
        StringBuilder cartDetails = new StringBuilder();
        int itemNumber = 1;
        double totalDiscount = 0;
        double totalCost = 0;

        for (Item item : shoppingCart) {
            double itemDiscount = calculateDiscount(item.getQuantity());
            double itemCost = item.getQuantity() * item.getPrice() * (1 - itemDiscount);
            totalDiscount += itemDiscount * item.getPrice() * item.getQuantity();
            totalCost += itemCost;
             

            cartDetails.append("(Item #").append(itemNumber).append(") ");
            cartDetails.append("Item ID: ").append(item.getId()).append("\n");
            cartDetails.append("Description: ").append(item.getDescription()).append("\n");
            cartDetails.append("Price: $").append(item.getPrice()).append("\n");
            cartDetails.append("Discount: ").append(decimalFormat.format(itemDiscount * 100)).append("%\n");
            cartDetails.append("Total: $").append(decimalFormat.format(itemCost)).append("\n\n");

            itemNumber++;
        }

        cartDetails.append("Total Discount: $").append(decimalFormat.format(totalDiscount)).append("\n");
        cartDetails.append("Total Cost: $").append(decimalFormat.format(totalCost)).append("\n");

        cartDetailsTextArea.setText(cartDetails.toString());

        
        cartFrame.add(scrollPane);

       
        cartFrame.setVisible(true);
    }

    private double calculateDiscount(int quantity) {
        if (quantity >= 1 && quantity <= 4) {
            return 0.0;
        } else if (quantity >= 5 && quantity <= 9) {
            return 0.10; 
        } else if (quantity >= 10 && quantity <= 14) {
            return 0.15; 
        } else {
            return 0.20;
        }
    }
    
    private void performCheckout() {
        
        JFrame checkoutPopup = new JFrame("ShoppingCart - Final Invoice");

        
        JPanel checkoutPanel = new JPanel();
        checkoutPanel.setLayout(new BorderLayout());

        
        JTextArea cartDetails = new JTextArea();
        cartDetails.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(cartDetails);

        
        JTextArea summaryArea = new JTextArea();
        summaryArea.setEditable(false);

       
        JButton closeButton = new JButton("OK");

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               
               //Exit program with 'ok' button  
                System.exit(0);
            }
        });

        
        checkoutPanel.add(scrollPane, BorderLayout.CENTER);
        checkoutPanel.add(summaryArea, BorderLayout.SOUTH);
        checkoutPanel.add(closeButton, BorderLayout.SOUTH);

        
        checkoutPopup.getContentPane().add(checkoutPanel);

        
        checkoutPopup.setSize(800, 400);
        checkoutPopup.setLocationRelativeTo(this); 
        checkoutPopup.setVisible(true);
        
        
        Date currentDateStamp = new Date(); 
        SimpleDateFormat dateFormatStr = new SimpleDateFormat("MM, dd yyyy HH:mm:ss EEE");
        String dateFinal = "Date: " + dateFormatStr.format(currentDateStamp); 

       
        double subtotal = 0.0;
        double discount = 0.0;
        double total = 0.0;
        double taxRate = 0.06; 
        
        

        StringBuilder cartText = new StringBuilder("Cart Details:\n");
        cartText.append(dateFinal).append("\n\n"); 
        
        for (int i = 0; i < shoppingCart.size(); i++) {
            Item item = shoppingCart.get(i);
            double itemTotal = item.getPrice() * item.getQuantity();
            subtotal += itemTotal;

           
            cartText.append("Item #").append(i + 1).append(": ");
            cartText.append("Item ID: ").append(item.getId()).append(", ");
            cartText.append("Description: ").append(item.getDescription()).append(", ");
            cartText.append("Price: $").append(item.getPrice()).append(", ");
            cartText.append("Quantity: ").append(item.getQuantity()).append(", ");
            cartText.append("Total: $").append(itemTotal).append("\n");
        }

     
        if (subtotal >= 1 && subtotal <= 4) {
            discount = 0.0;
        } else if (subtotal >= 5 && subtotal <= 9) {
            discount = 0.10; 
        } else if (subtotal >= 10 && subtotal <= 14) {
            discount = 0.15; 
        } else if (subtotal >= 15) {
            discount = 0.20; 
        }

        //Final Total 
        double tax = subtotal * taxRate;
        total = (subtotal) + tax;

       
        
        cartText.append("\nOrder subtotal: $" + subtotal);
        cartText.append("\nTax Rate: ").append(taxRate * 100).append("%\n");
        cartText.append("Tax Amount: $").append(decimalFormat.format(tax)).append("\n");
        cartText.append("Order Total (after discount and tax): $").append(decimalFormat.format(total)).append("\n");
        cartText.append("Thank you for shopping at ShoppingCart.com!");
        
        
         

        
        cartDetails.setText(cartText.toString());
        summaryArea.setText("Tax: $" + tax + "\nDiscount: $" + decimalFormat.format(discount) + "\nTotal: $" + decimalFormat.format(total));
        
        exportCartToCSV(); 
    }
    
    private void exportCartToCSV() {
    	
    	Date currentDateStr = new Date();
        SimpleDateFormat dateFormatStr = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String transactionID = dateFormatStr.format(currentDateStr);
        
        try {
           
            FileWriter writer = new FileWriter("transaction_" + transactionID + ".csv");

           
            writer.write("Transaction ID,Item ID,Description,Price,Quantity,Total\n");

           
            for (Item item : shoppingCart) {
                double itemTotal = item.getPrice() * item.getQuantity();
                
                String line = transactionID + "," + item.getId() + "," + item.getDescription() + ","
                        + item.getPrice() + "," + item.getQuantity() + "," + itemTotal + "\n";
                writer.write(line);
            }

            
            writer.close();

            
            
        } catch (IOException e) {
           
            JOptionPane.showMessageDialog(this, "Error exporting cart to CSV", "Export Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new myGUI();
            }
        });
    }
    }


    

