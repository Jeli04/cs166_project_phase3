/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;



//import com.apple.laf.resources.aqua_zh_TW;

import java.util.ArrayList;
import java.lang.Math;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
@SuppressWarnings("deprecation")
public class PizzaStore {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of PizzaStore
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public PizzaStore(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end PizzaStore

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
		 if(outputHeader){
			for(int i = 1; i <= numCol; i++){
			System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			outputHeader = false;
		 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            PizzaStore.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      PizzaStore esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the PizzaStore object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new PizzaStore (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("\nMAIN MENU");
                System.out.println("---------");
                System.out.println("1. View Profile");
                System.out.println("2. Update Profile");
                System.out.println("3. View Menu");
                System.out.println("4. Place Order"); //make sure user specifies which store
                System.out.println("5. View Full Order ID History");
                System.out.println("6. View Past 5 Order IDs");
                System.out.println("7. View Order Information"); //user should specify orderID and then be able to see detailed information about the order
                System.out.println("8. View Stores"); 

                //**the following functionalities should only be able to be used by drivers & managers**
                System.out.println("9. Update Order Status");

                //**the following functionalities should ony be able to be used by managers**
                System.out.println("10. Update Menu");
                System.out.println("11. Update User");

                System.out.println(".........................");
                System.out.println("20. Log out");
                switch (readChoice()){
                   case 1: viewProfile(esql,authorisedUser); break;
                   case 2: updateProfile(esql,authorisedUser); break;
                   case 3: viewMenu(esql); break;
                   case 4: placeOrder(esql); break;
                   case 5: viewAllOrders(esql); break;
                   case 6: viewRecentOrders(esql); break;
                   case 7: viewOrderInfo(esql); break;
                   case 8: viewStores(esql); break;
                   case 9: updateOrderStatus(esql,authorisedUser); break;
                   case 10: updateMenu(esql,authorisedUser); break;
                   case 11: updateUser(esql); break;



                   case 20: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user
    **/
   public static void CreateUser(PizzaStore esql){
      try{
         String query = "INSERT INTO Users (login, password, role, favoriteItems, phoneNum) " +
                        "VALUES (";
         System.out.print("\tEnter login: $");
         String input = in.readLine();
         query += "'" + input + "',";
         System.out.print("\tEnter password: $");
         input = in.readLine();
         query += "'" + input + "',";
         System.out.print("\tEnter role: $");
         input = in.readLine();
         query += "'" + input + "',";
         System.out.print("\tEnter Favorite Items: $");
         input = in.readLine();
         query += "'" + input + "',";
         System.out.print("\tEnter Phone Number: $");
         input = in.readLine();
         query += "'" + input + "');";

         int rowCount = esql.executeQuery(query);
         System.out.println ("total row(s): " + rowCount);
      }
      catch(Exception e){
         System.err.println (e.getMessage());
      }
   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(PizzaStore esql){

      try{
         System.out.print("Enter login: ");
         String entered_login = in.readLine();

         System.out.print("Enter password: ");
         String entered_pw = in.readLine();

         String query = "SELECT login FROM Users WHERE login = '" + entered_login + 
                       "' AND password = '" + entered_pw + "';";
         
         List<List<String>> result = esql.executeQueryAndReturnResult(query); //List<List<String>>: all rows retrieved , List<String>: Represents single row//

         if (!result.isEmpty()) { //found a match//
            System.out.println("Login successful. Welcome, " + entered_login + "!");
            return entered_login;  // Return the username to indicate success
        } 
        else {
            System.out.println("Invalid login credentials.");
        }
    } 

      catch (Exception e) {
         System.err.println("Error: " + e.getMessage());
      }

      return null;
   }//end

// Rest of the functions definition go in here

public static void viewProfile(PizzaStore esql, String user) {
   try {
       // Now selecting role along with other fields
       String query = "SELECT login, password, role, favoriteItems, phoneNum FROM Users WHERE login = '" + user + "';";
       List<List<String>> result = esql.executeQueryAndReturnResult(query);

       if (!result.isEmpty()) {
           List<String> row = result.get(0); // Only one row expected since login is unique
           
           System.out.println("\nUser Profile:");
           System.out.println("-------------");
           System.out.println("Login: " + row.get(0));
           System.out.println("Password: " + row.get(1));
           System.out.println("Role: " + row.get(2));  // Added Role output
           System.out.println("Favorite Item: " + row.get(3));
           System.out.println("Phone Number: " + row.get(4));
           System.out.println();
       } else {
           System.out.println("User not found.");
       }
   } catch (Exception e) {
       System.err.println("Error: " + e.getMessage());
   }
}


   public static String get_role(PizzaStore esql, String user) {
      try {
          String query = "SELECT role FROM Users WHERE login = '" + user + "';";
          return esql.executeQueryAndReturnResult(query).get(0).get(0).trim(); 
      } 
      catch (Exception e) {
          System.err.println("Error fetching role: " + e.getMessage());
          return null; // Return null in case of an error
      }
  }

   public static void updateProfile(PizzaStore esql, String user) { //user can change pw&#, manager can change can edit user login&pw&role, anyone can update favorite item//
      
      boolean going = true; //while 
      String user_role = get_role(esql, user);


      while(going){
         
         System.out.println("\nUPDATE PROFILE ");
         System.out.println("---------");

         System.out.println("1. Password");
         System.out.println("2. Phone number");
         System.out.println("3. Favorite item");
         System.out.println("4. Exit");

         if(user_role.trim().equals("manager")){ 
            System.out.println("5. Users information");
         }

         String new_data = "";
         String query = "";
         String action = "";
         String action_query = "";
         String managed_user = ""; //if manager wants to change a users login or pw//

         switch(readChoice()){
            case 1: //PW FINISHED
               action = "password";
               action_query = "password";     

               break;
               
            case 2: //PHONE # FINISHED
            action = "phone number";
            action_query = "phoneNum";          

            break;
            
            case 3: //DONE
               action = "favorite item";
               action_query = "favoriteItems";
                  
               break;

            case 4: //DONE
               going = false;
               break;
            

            case 5:

               System.out.print("Enter the username of the user to modify: ");

               while (true) {
                  try {
                     System.out.print("Enter the username of the user to modify: ");
                     managed_user = in.readLine(); // Read the username
            
                     // Check if the user exists in the database
                     String checkQuery = "SELECT COUNT(*) FROM Users WHERE login = '" + managed_user + "';";
                     List<List<String>> result = esql.executeQueryAndReturnResult(checkQuery);
            
                     if (!result.isEmpty() && Integer.parseInt(result.get(0).get(0)) > 0) {
                        break; // Exit loop if user exists
                     } else {
                        System.out.println("Error: User does not exist. Please enter a valid username.");
                     }
                  } catch (IOException e) {
                     System.err.println("Error reading input: " + e.getMessage());
                  } catch (SQLException e) {
                     System.err.println("SQL Error: " + e.getMessage());
                  }
            }
            

               // Display options
               System.out.println("1. Change Login");
               System.out.println("2. Change Role");

               int choice = readChoice();

               if (choice == 1) {
                  try {
                     System.out.print("Enter new login: ");
                     String new_login = in.readLine();
                     
                     // Update login in the database
                     query = "UPDATE Users SET login = '" + new_login + "' WHERE login = '" + managed_user + "';";
                     esql.executeUpdate(query);
                     
                     System.out.println("User login updated successfully.");
                  } catch (IOException e) {
                     System.err.println("Error reading input: " + e.getMessage());
                  } catch (SQLException e) {
                     System.err.println("SQL Error: " + e.getMessage());
                  }
               } 
               else if (choice == 2) {
                  try {
                     System.out.print("Enter new role: ");
                     String new_role = in.readLine();

                     while (true) {
                        if (!(new_role.equals("manager") || new_role.equals("user") || new_role.equals("driver"))) {
                            System.out.print("Error: Enter new role (manager, user, driver) (no space): ");
                            new_role = in.readLine();
                        } 
                        else {
                            break; // Exit the loop if the input is correct
                        }
                    }

                     // Update role in the database
                     query = "UPDATE Users SET role = '" + new_role + "' WHERE login = '" + managed_user + "';";
                     esql.executeUpdate(query);
                     
                     System.out.println("User role updated successfully.");
                  } catch (IOException e) {
                     System.err.println("Error reading input: " + e.getMessage());
                  } catch (SQLException e) {
                     System.err.println("SQL Error: " + e.getMessage());
                  }
               } 
               else {
                  System.out.println("Invalid choice. Please select 1 or 2.");
               }
               
         }
         if(going && managed_user == ""){
            try {
               System.out.print("Enter new " + action +": ");

               new_data = in.readLine(); // May throw IOException
               query = "UPDATE Users SET "+ action_query + " = '" + new_data + "' WHERE login = '" + user + "';";
               System.out.println("New " + action + ": " + new_data);
               try {
                  esql.executeUpdate(query);
            } catch (SQLException e) {
                  System.err.println("SQL Error: " + e.getMessage());
            }
            } 
            catch (IOException e) {
               System.err.println("Error reading input: " + e.getMessage());
            }
      }
         
   }

   }
         

   public static void viewMenu(PizzaStore esql) {
      // Assuming this only checks for sides, drinks, and entrees 
      boolean ordermenu = true;
      while(ordermenu) {
         System.out.println("ORDER MENU");
         System.out.println("---------");
         System.out.println("1. View Full Menu");
         System.out.println("2. View Sides");
         System.out.println("3. View Drinks");
         System.out.println("4. View Entrees");
         System.out.println("7. Back");

         switch (readChoice()){
            case 1: 
               try{
                  String query = "SELECT * FROM Items;";
                  esql.executeQueryAndPrintResult(query);
               }catch(Exception e){
                  System.err.println (e.getMessage());
               }
               break;
            case 2: 
               try{
                  String query = "SELECT * FROM Items WHERE typeOfItem = ' sides';";
                  esql.executeQueryAndPrintResult(query);
               }catch(Exception e){
                  System.err.println (e.getMessage());
               }
               break;
            case 3: 
               try{
                  String query = "SELECT * FROM Items WHERE typeOfItem = ' drinks';";
                  esql.executeQueryAndPrintResult(query);
               }catch(Exception e){
                  System.err.println (e.getMessage());
               }
               break;
            case 4: 
               try{
                  String query = "SELECT * FROM Items WHERE typeOfItem = ' entree';";
                  esql.executeQueryAndPrintResult(query);
               }catch(Exception e){
                  System.err.println (e.getMessage());
               }
               break;
            case 7:
               ordermenu = false;
               break;
         }
      }
   }
   public static void placeOrder(PizzaStore esql) {}
   public static void viewAllOrders(PizzaStore esql) {}
   public static void viewRecentOrders(PizzaStore esql) {}
   public static void viewOrderInfo(PizzaStore esql) {}

   public static void viewStores(PizzaStore esql) {
      boolean going = true;
  
      while (going) {
          System.out.println("\nSTORES LIST");
          System.out.println("------------");
          System.out.println("FILTER BY");
          System.out.println("1. City");
          System.out.println("2. State");
          System.out.println("3. Is Open");
          System.out.println("4. Rating");
          System.out.println("------------");
          System.out.println("5. Exit");
  
          String filterCondition = "";
          String userInput = "";
          int choice = readChoice();
  
          switch (choice) {
              case 1: // Filter by City
                  System.out.print("Enter city name: ");
                  try {
                      userInput = in.readLine().trim();
                      filterCondition = " WHERE city ILIKE '" + userInput + "' ";
                  } catch (IOException e) {
                      System.err.println("Error reading input: " + e.getMessage());
                  }
                  break;
  
              case 2: // Filter by State
                  System.out.print("Enter state name: ");
                  try {
                      userInput = in.readLine().trim();
                      filterCondition = " WHERE state ILIKE '" + userInput + "' ";
                  } catch (IOException e) {
                      System.err.println("Error reading input: " + e.getMessage());
                  }
                  break;
  
              case 3: // Filter by Open/Closed g)
                  System.out.print("Show only open stores? (yes/no): ");
                  try {
                      userInput = in.readLine().trim().toLowerCase();
                      if (userInput.equals("yes")) {
                          filterCondition = " WHERE isOpen ILIKE 'yes' ";
                      } else if (userInput.equals("no")) {
                          filterCondition = " WHERE isOpen ILIKE 'no' ";
                      } else {
                          System.out.println("Invalid input. Showing all stores.");
                      }
                  } catch (IOException e) {
                      System.err.println("Error reading input: " + e.getMessage());
                  }
                  break;
  
              case 4: 
                  System.out.print("Enter minimum rating (1.0 - 5.0): ");
                  try {
                      float rating = Float.parseFloat(in.readLine().trim());
                      while (rating < 1.0 || rating > 5.0) {
                          System.out.print("Invalid rating. Enter a value between 1.0 and 5.0: ");
                          rating = Float.parseFloat(in.readLine().trim());
                      }
                      filterCondition = " WHERE reviewScore >= " + rating + " ";
                  } catch (IOException | NumberFormatException e) {
                      System.err.println("Invalid input. Please enter a valid number.");
                  }
                  break;
  
              case 5: // Exit
                  going = false;
                  continue;
  
              default:
                  System.out.println("Invalid choice. Try again.");
                  continue;
          }
  
          // Only execute the query if a filter was selected
          if (!filterCondition.isEmpty()) {
              String query = "SELECT storeID, address, city, state, isOpen, reviewScore FROM Store" + filterCondition + ";";
  
              try {
                  List<List<String>> results = esql.executeQueryAndReturnResult(query);
  
                  if (results.isEmpty()) {
                      System.out.println("No matching stores found.");
                  } else {
                      // Calculate max column widths dynamically
                      int[] columnWidths = {10, 25, 12, 15, 8, 8}; // Default widths
  
                      // Adjust column widths based on actual data
                      for (List<String> row : results) {
                          columnWidths[0] = Math.max(columnWidths[0], row.get(0).length()); // Store ID
                          columnWidths[1] = Math.max(columnWidths[1], row.get(1).length()); // Address
                          columnWidths[2] = Math.max(columnWidths[2], row.get(2).length()); // City
                          columnWidths[3] = Math.max(columnWidths[3], row.get(3).length()); // State
                          columnWidths[4] = Math.max(columnWidths[4], row.get(4).length()); // Open/Closed
                          columnWidths[5] = Math.max(columnWidths[5], row.get(5).length()); // Rating
                      }
  
                      String format = "| %-" + columnWidths[0] + "s | %-" + columnWidths[1] + "s | %-" + columnWidths[2] + "s | %-" + columnWidths[3] + "s | %-" + columnWidths[4] + "s | %-" + columnWidths[5] + "s |\n";
  
                      String lineSeparator = "+-" + "-".repeat(columnWidths[0]) + "-+-" + "-".repeat(columnWidths[1]) + "-+-" +
                                             "-".repeat(columnWidths[2]) + "-+-" + "-".repeat(columnWidths[3]) + "-+-" +
                                             "-".repeat(columnWidths[4]) + "-+-" + "-".repeat(columnWidths[5]) + "-+";
  
                      System.out.println(lineSeparator);
                      System.out.printf(format, "Store ID", "Address", "City", "State", "Open", "Rating");
                      System.out.println(lineSeparator);
  
                      // Print each row with proper alignment
                      for (List<String> row : results) {
                          System.out.printf(format, row.get(0), row.get(1), row.get(2), row.get(3), row.get(4), row.get(5));
                      }
                      System.out.println(lineSeparator);
                  }
              } catch (SQLException e) {
                  System.err.println("SQL Error: " + e.getMessage());
              }
          }
      }
  }

  public static void updateOrderStatus(PizzaStore esql, String user) {
   try {
       // Check if the user is a manager or driver
       String roleQuery = "SELECT role FROM Users WHERE login = '" + user + "';";
       String role = esql.executeQueryAndReturnResult(roleQuery).get(0).get(0).trim();

       if (!role.equalsIgnoreCase("manager") && !role.equalsIgnoreCase("driver")) {
           System.out.println("Access Denied: Only managers or drivers can update order status.");
           return;
       }

       boolean updating = true;
       while (updating) {
           // Display all pending/incomplete orders
           System.out.println("\nPENDING & INCOMPLETE ORDERS:");
           String listOrdersQuery = "SELECT orderID, login, storeID, totalPrice, orderStatus FROM FoodOrder WHERE orderStatus != 'complete' ORDER BY orderID;";
           esql.executeQueryAndPrintResult(listOrdersQuery);

           System.out.println("\nOptions:");
           System.out.println("1. Update an Order Status");
           System.out.println("2. Exit");
           System.out.print("Enter your choice: ");

           int choice = readChoice();
           if (choice == 2) {
               updating = false;
               continue;
           } else if (choice != 1) {
               System.out.println("Invalid choice. Try again.");
               continue;
           }

           System.out.print("\nEnter the Order ID to update (or type 'exit' to cancel): ");
           String orderID = in.readLine().trim();
           if (orderID.equalsIgnoreCase("exit")) {
               updating = false;
               continue;
           }

           // Verify the order exists
           String checkOrderQuery = "SELECT COUNT(*) FROM FoodOrder WHERE orderID = '" + orderID + "';";
           int count = Integer.parseInt(esql.executeQueryAndReturnResult(checkOrderQuery).get(0).get(0));

           if (count == 0) {
               System.out.println("Error: Order ID not found.");
               continue;
           }

           // Ask for new order status using numbers
           System.out.println("\nSet Order Status:");
           System.out.println("1. Incomplete");
           System.out.println("2. Complete");
           System.out.print("Enter your choice: ");
           
           int statusChoice = readChoice();
           String newStatus = "";

           if (statusChoice == 1) {
               newStatus = "incomplete"; // Stored in lowercase
           } else if (statusChoice == 2) {
               newStatus = "complete"; // Stored in lowercase
           } else {
               System.out.println("Invalid choice. Returning to menu.");
               continue;
           }

           // Update order status
           String updateQuery = "UPDATE FoodOrder SET orderStatus = '" + newStatus + "' WHERE orderID = '" + orderID + "';";
           esql.executeUpdate(updateQuery);

           System.out.println("✅ Order ID " + orderID + " updated to status: " + newStatus);
       }
   } catch (Exception e) {
       System.err.println("Error: " + e.getMessage());
   }
}




   public static void updateMenu(PizzaStore esql, String user) {
      try {
          // Check if the user is a manager
          String roleQuery = "SELECT role FROM Users WHERE login = '" + user + "';";
          String role = esql.executeQueryAndReturnResult(roleQuery).get(0).get(0).trim();
  
          if (!role.equalsIgnoreCase("manager")) {
              System.out.println("Access Denied: Only managers can update the menu.");
              return;
          }
  
          boolean menuLoop = true;
          while (menuLoop) {
              System.out.println("\nMENU MANAGEMENT");
              System.out.println("1. Add New Item");
              System.out.println("2. Update Existing Item");
              System.out.println("3. Exit");
  
              switch (readChoice()) {
                  case 1: // Add New Item
                      System.out.print("Enter item name: ");
                      String itemName = in.readLine().trim();
  
                      System.out.print("Enter ingredients: ");
                      String ingredients = in.readLine().trim();
  
                      System.out.print("Enter item type (e.g., pizza, drink, side): ");
                      String type = in.readLine().trim();
  
                      System.out.print("Enter price: ");
                      String price = in.readLine().trim();
  
                      System.out.print("Enter description: ");
                      String description = in.readLine().trim();
  
                      String addQuery = "INSERT INTO Items (itemName, ingredients, typeOfItem, price, description) " +
                                        "VALUES ('" + itemName + "', '" + ingredients + "', '" + type + "', " + price + ", '" + description + "');";
                      
                      esql.executeUpdate(addQuery);
                      System.out.println("Item added successfully!");
                      break;
  
                  case 2: // Update Existing Item
                      System.out.println("\nCURRENT MENU ITEMS:");
                      String listQuery = "SELECT itemName, price FROM Items ORDER BY itemName;";
                      esql.executeQueryAndPrintResult(listQuery);
  
                      System.out.print("\nEnter the name of the item to update: ");
                      String existingItem = in.readLine().trim();
  
                      System.out.print("Enter new price: ");
                      String newPrice = in.readLine().trim();
  
                      System.out.print("Enter new description: ");
                      String newDescription = in.readLine().trim();
  
                      String updateQuery = "UPDATE Items SET price = " + newPrice + ", description = '" + newDescription + "' " +
                                           "WHERE itemName = '" + existingItem + "';";
                      
                      esql.executeUpdate(updateQuery);
                      System.out.println("Item updated successfully!");
                      break;
  
                  case 3: // Exit
                      menuLoop = false;
                      break;
  
                  default:
                      System.out.println("Invalid choice. Try again.");
              }
          }
      } catch (Exception e) {
          System.err.println("Error: " + e.getMessage());
      }
  }
  
  
  

   public static void updateUser(PizzaStore esql) {}


}//end PizzaStore

