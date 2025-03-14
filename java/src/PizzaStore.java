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
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.time.LocalDateTime; // getting time
import java.time.format.DateTimeFormatter;
import java.sql.Timestamp;


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
                //System.out.println("11. Update User");

                System.out.println(".........................");
                System.out.println("20. Log out");
                switch (readChoice()){
                   case 1: viewProfile(esql,authorisedUser); break;
                   case 2: updateProfile(esql,authorisedUser); break;
                   case 3: viewMenu(esql); break;
                   case 4: placeOrder(esql, authorisedUser); break;
                   case 5: viewAllOrders(esql, authorisedUser); break;
                   case 6: viewRecentOrders(esql, authorisedUser); break;
                   case 7: viewOrderInfo(esql, authorisedUser); break;
                   case 8: viewStores(esql); break;
                   case 9: updateOrderStatus(esql,authorisedUser); break;
                   case 10: updateMenu(esql,authorisedUser); break;
                  // case 11: updateUser(esql); break;



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

            try {
               System.out.println("\n FILTER BY ");
               System.out.println("1. Customer ");
               System.out.println("2. Manager ");
               System.out.println("3. Driver ");
           
               int role_choice;
               String role = "";
           
               while (true) {
                   role_choice = readChoice();
           
                   if (role_choice == 1) {
                       role = "customer";
                       break;
                   } else if (role_choice == 2) {
                       role = "manager";
                       break;
                   } else if (role_choice == 3) {
                       role = "driver";
                       break;
                   }
           
                   System.out.println("\nMust choose 1, 2, or 3");
                   System.out.println("1. Customer ");
                   System.out.println("2. Manager ");
                   System.out.println("3. Driver ");
               }
           
               // Execute query with correct SQL formatting
               query = "SELECT login FROM Users WHERE TRIM(role) = '" + role + "' ORDER BY login;";
               List<List<String>> users = esql.executeQueryAndReturnResult(query);
           
               if (users.isEmpty()) {
                   System.out.println("No users found for role: " + role);
               } else {
                   // Determine the max width for username formatting
                   int maxUserWidth = "Username".length();
                   for (List<String> row : users) {
                       maxUserWidth = Math.max(maxUserWidth, row.get(0).length());
                   }
           
                   // Formatting
                   String lineSeparator = "+-" + "-".repeat(maxUserWidth) + "-+";
                   String format = "| %-" + maxUserWidth + "s |\n";
           
                   // Print Header
                   System.out.println(lineSeparator);
                   System.out.printf(format, "Username");
                   System.out.println(lineSeparator);
           
                   // Print Each User
                   for (List<String> row : users) {
                       System.out.printf(format, row.get(0));
                   }
           
                   // Print Footer
                   System.out.println(lineSeparator);
                   while (true) {
                     try {
                         System.out.print("Enter username: ");
                         managed_user = in.readLine().trim(); // Read user input
                         
                         // Check if the username exists in the database
                         String checkQuery = "SELECT COUNT(*) FROM Users WHERE login = '" + managed_user + "';";
                         List<List<String>> result = esql.executeQueryAndReturnResult(checkQuery);
                 
                         if (!result.isEmpty() && Integer.parseInt(result.get(0).get(0)) > 0) {
                             System.out.println("User found: " + managed_user);
                             break; // Exit loop if user exists
                         } else {
                             System.out.println("Error: User does not exist. Please enter a valid username.");
                         }
                     } catch (IOException e) {
                         System.err.println("Error reading input: " + e.getMessage());
                         break; // Exit loop if input fails
                     } catch (SQLException e) {
                         System.err.println("SQL Error: " + e.getMessage());
                         break; // Exit loop if database fails
                     }
                 }
            }  
         }
         catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
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
                        if (!(new_role.equals("manager") || new_role.equals("customer") || new_role.equals("driver"))) {
                            System.out.print("Error: Enter new role (manager, customer, driver) (no space): ");
                            new_role = in.readLine();
                        } 
                        else {
                            break; 
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

   // Helper function for viewMenu
   public static boolean isFloat(String str) {
      try {
         Float.parseFloat(str);
         return true;
      } catch (NumberFormatException e) {
         return false;
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
         System.out.println("5. Under Price Search");
         System.out.println("6. Ascending Prices");
         System.out.println("7. Descending Prices");
         System.out.println("8. Back");

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
            case 5: 
               try{
                  String price = "";
                  while(!isFloat(price)){
                     System.out.println("Enter a price: ");
                     price = in.readLine();
                  }

                  String query = "SELECT * FROM Items WHERE price < " + price + ";";
                  esql.executeQueryAndPrintResult(query);
               }catch(Exception e){
                  System.err.println (e.getMessage());
               }
               break;
            case 6: 
               try{
                  String query = "SELECT * FROM Items ORDER BY price ASC;";
                  esql.executeQueryAndPrintResult(query);
               }catch(Exception e){
                  System.err.println (e.getMessage());
               }
               break;
            case 7: 
               try{
                  String query = "SELECT * FROM Items ORDER BY price DESC;";
                  esql.executeQueryAndPrintResult(query);
               }catch(Exception e){
                  System.err.println (e.getMessage());
               }
               break;
            case 8:
               ordermenu = false;
               break;
         }
      }
   }

   public static int validStore(PizzaStore esql, String userStore){
      String query = "SELECT * FROM Store WHERE storeID = " + userStore + ";";
      int rowCount = 0;
      try{
         rowCount = esql.executeQuery(query);
      } catch (SQLException e) {
         return 0;
      }
      return rowCount;
   }

   public static int validOrder(PizzaStore esql, String userOrder){
      String query = "SELECT * FROM Items WHERE itemName = '" + userOrder + "';";
      int rowCount = 0;
      try{
         rowCount = esql.executeQuery(query);
      } catch (SQLException e) {
         return 0;
      }
      return rowCount;
   }

   public static int generateOrderID(PizzaStore esql, String userStore) {
      String query = "SELECT MAX(orderID) FROM FoodOrder WHERE storeID = " + userStore + ";";
      int orderID = 0;
      try {
         List<List<String>> result = esql.executeQueryAndReturnResult(query);
         // Get the max value string from the result
         String maxStr = result.get(0).get(0);
         if (maxStr == null) {
            orderID = 1;  // No order exists yet for this store
         } else {
            orderID = Integer.parseInt(maxStr) + 1;
         }
      } catch (SQLException e) {
         return -1;
      }
      return orderID;
   }
   
   public static void printPrices(PizzaStore esql, int orderID){
      String query = "SELECT iio.itemName, iio.quantity, i.price * iio.quantity AS totalCost "
                  + "FROM ItemsInOrder iio "
                  + "JOIN Items i ON iio.itemName = i.itemName "
                  + "WHERE iio.orderID = " + orderID + ";";      
             
      try{
         orderID = esql.executeQueryAndPrintResult(query);
      } catch (SQLException e) {
         
      }
   }

   public static void placeOrder(PizzaStore esql, String userLogin) {
      try{
         String userStore = "";
         while(validStore(esql, userStore) < 1){
            System.out.println("Which store to place your order: $");
            userStore = in.readLine();
         }

         String userOrder = "";
         String option = "";
         boolean going = true;
         int orderID = generateOrderID(esql, userStore);
         double totalPrice = 0.0;

         // Get time
         LocalDateTime now = LocalDateTime.now();
         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
         String timestamp = now.format(formatter);
         Timestamp sqlTimestamp = Timestamp.valueOf(timestamp);

         // update the entire food order
         String query = "INSERT INTO FoodOrder (orderID, login, storeID, totalPrice, orderTimestamp, orderStatus) " +
                        "VALUES (" + orderID + ", '" + userLogin + "', " + userStore + ", " + 0.0 + ", '" + sqlTimestamp + "', 'incomplete');";
         esql.executeUpdate(query);

         while(going){
            System.out.println("\nOrder for Store " + userStore);
            System.out.println("---------");
            System.out.println("1. Place Item Order");       
            System.out.println("2. Finish Order");

            switch (readChoice()){
               case 1:          
                  while(validOrder(esql, userOrder) < 1){
                     System.out.println("Place your order: $");
                     userOrder = in.readLine();
                  }

                  int quantity = 1;
                  do {
                     System.out.print("Enter a quantity: ");
                     try {
                           quantity = Integer.parseInt(in.readLine());
                           break;
                     } catch (Exception e) {
                           System.out.println("Invalid input. Please enter a valid integer.");
                     }
                  } while (true);

                  System.out.println("Placing order of " + quantity + "x "+ userOrder);

                  query = "INSERT INTO ItemsInOrder (orderID, itemName, quantity)" + 
                                 "VALUES (" + orderID + ", '" + userOrder + "'," + quantity + ");";
                  esql.executeUpdate(query);

                  // update total price 
                  query = "SELECT price FROM Items WHERE itemName = '" + userOrder + "';";
                  List<List<String>> result = esql.executeQueryAndReturnResult(query);
                  double itemPrice = Double.parseDouble(result.get(0).get(0));
                  totalPrice = totalPrice + (itemPrice * quantity);

                  break;
               case 2:
                  going = false;
                  break;
               default:
                  System.out.println("Invalid option. Please enter 1 or 2.");
                  break;
            }
         }
         System.out.println("\nORDER TOTAL ");
         System.out.println("---------");
         printPrices(esql, orderID);
         System.out.println("---------");
         System.out.println("Total Price: " + String.format("%.2f", totalPrice));
         System.out.println("---------");

         // Confirm order with the user
         boolean validResponse = false;
         String complete = "incomplete";
         while (!validResponse) {
            System.out.print("Do you want to complete this order? (yes/no): ");
            String userResponse = in.readLine().trim().toLowerCase(); // Read and normalize input

            if (userResponse.equals("yes")) {
               validResponse = true;
               complete = "complete";
            } else if (userResponse.equals("no")) {
               validResponse = true;
               System.out.println("Order not completed. You can modify your order.");
            } else {
               System.out.println("Invalid response. Please enter 'yes' or 'no'.");
            }
         }

         String updateQuery = "UPDATE FoodOrder SET orderStatus = '" + complete + "', totalPrice = " + totalPrice + 
                              " WHERE orderID = " + orderID + ";";
         esql.executeUpdate(updateQuery);

      }
      catch(Exception e){
         System.err.println (e.getMessage());
         System.out.println("Invalid Order.");
      }
   }


   public static void viewAllOrders(PizzaStore esql, String userLogin) {
      String role = get_role(esql, userLogin);
      String query = "SELECT iio.itemName, iio.quantity, (i.price * iio.quantity) AS totalCost " +
                     "FROM FoodOrder fo " +
                     "JOIN ItemsInOrder iio ON fo.orderID = iio.orderID " +
                     "JOIN Items i ON iio.itemName = i.itemName ";
                           
      if(role.equals("customer")){
         query = query + "WHERE fo.login = '" + userLogin + "';";
      }
      try{
         esql.executeQueryAndPrintResult(query);
      }catch (Exception e) {
         System.err.println("Error: " + e.getMessage());
      }
   }


   public static void viewRecentOrders(PizzaStore esql, String userLogin) {
      String role = get_role(esql, userLogin);
      String query = "SELECT iio.itemName, iio.quantity, (i.price * iio.quantity) AS totalCost " +
                     "FROM FoodOrder fo " +
                     "JOIN ItemsInOrder iio ON fo.orderID = iio.orderID " +
                     "JOIN Items i ON iio.itemName = i.itemName ";
                     
      if (role.equals("customer")) {
         query += "WHERE fo.login = '" + userLogin + "' ";
      }
      
      query += "ORDER BY fo.orderTimestamp DESC LIMIT 5;";
      
      try {
         esql.executeQueryAndPrintResult(query);
      } catch (Exception e) {
         System.err.println("Error: " + e.getMessage());
      }
   }

   public static void viewOrderInfo(PizzaStore esql, String userLogin) {
      String role = get_role(esql, userLogin);
      String query = "SELECT fo.orderID, fo.orderTimestamp, fo.orderStatus, " +
                     "iio.itemName, iio.quantity, (i.price * iio.quantity) AS totalCost " +
                     "FROM FoodOrder fo " +
                     "JOIN ItemsInOrder iio ON fo.orderID = iio.orderID " +
                     "JOIN Items i ON iio.itemName = i.itemName ";
                              
      if(role.equals("customer")){
         query += "WHERE fo.login = '" + userLogin + "' ";
      }
      try{
         esql.executeQueryAndPrintResult(query);
      } catch (Exception e) {
         System.err.println("Error: " + e.getMessage());
      }
   }

   public static void viewStores(PizzaStore esql) {
      try {
         boolean going = true;
         while (going) {
            System.out.println("\nSTORE SEARCH");
            System.out.println("-------------");

            // Step 1: Display available states
            System.out.println("Available States:");
            String stateQuery = "SELECT DISTINCT state FROM Store ORDER BY state;";
            List<List<String>> states = esql.executeQueryAndReturnResult(stateQuery);
            
            for (List<String> row : states) {
                  System.out.println("- " + row.get(0));
            }

            // Step 2: Ask for State Selection
            System.out.print("\nEnter state name: ");
            String stateInput = in.readLine().trim();
            String filterCondition = " WHERE state ILIKE '" + stateInput + "' ";

            // Step 3: Display available cities in that state
            System.out.println("\nAvailable Cities in " + stateInput + ":");
            String cityQuery = "SELECT DISTINCT city FROM Store WHERE state ILIKE '" + stateInput + "' ORDER BY city;";
            List<List<String>> cities = esql.executeQueryAndReturnResult(cityQuery);

            for (List<String> row : cities) {
                  System.out.println("- " + row.get(0));
            }

            // Step 4: Ask for City Selection
            System.out.print("\nEnter city name: ");
            String cityInput = in.readLine().trim();
            filterCondition += " AND city ILIKE '" + cityInput + "' ";

            // Step 5: Ask if they want to filter by open/closed stores
            System.out.print("Show only open stores? (yes/no, press Enter to skip): ");
            String openFilter = in.readLine().trim().toLowerCase();
            if (openFilter.equals("yes")) {
                  filterCondition += " AND isOpen ILIKE 'yes' ";
            } else if (openFilter.equals("no")) {
                  filterCondition += " AND isOpen ILIKE 'no' ";
            }

            // Step 6: Ask if they want to filter by rating
            System.out.print("Enter minimum star rating (1-5, press Enter to skip): ");
            String ratingInput = in.readLine().trim();
            if (!ratingInput.isEmpty()) {
                  try {
                     int rating = Integer.parseInt(ratingInput);
                     if (rating >= 1 && rating <= 5) {
                        filterCondition += " AND reviewScore >= " + rating + " ";
                     } else {
                        System.out.println("Invalid rating. Skipping rating filter.");
                     }
                  } catch (NumberFormatException e) {
                     System.out.println("Invalid input. Skipping rating filter.");
                  }
            }

            // Execute the query with all filters applied
            String query = "SELECT storeID, address, city, state, isOpen, reviewScore FROM Store" + filterCondition + ";";

            List<List<String>> results = esql.executeQueryAndReturnResult(query);

            if (results.isEmpty()) {
                  System.out.println("No matching stores found.");
            } else {
                  // Dynamically adjust column widths
                  int[] columnWidths = {10, 25, 12, 15, 8, 8};

                  for (List<String> row : results) {
                     columnWidths[0] = Math.max(columnWidths[0], row.get(0).length());
                     columnWidths[1] = Math.max(columnWidths[1], row.get(1).length());
                     columnWidths[2] = Math.max(columnWidths[2], row.get(2).length());
                     columnWidths[3] = Math.max(columnWidths[3], row.get(3).length());
                     columnWidths[4] = Math.max(columnWidths[4], row.get(4).length());
                     columnWidths[5] = Math.max(columnWidths[5], row.get(5).length());
                  }

                  String format = "| %-" + columnWidths[0] + "s | %-" + columnWidths[1] + "s | %-" + columnWidths[2] + "s | %-" +
                                 columnWidths[3] + "s | %-" + columnWidths[4] + "s | %-" + columnWidths[5] + "s |\n";

                  String lineSeparator = "+-" + "-".repeat(columnWidths[0]) + "-+-" + "-".repeat(columnWidths[1]) + "-+-" +
                                       "-".repeat(columnWidths[2]) + "-+-" + "-".repeat(columnWidths[3]) + "-+-" +
                                       "-".repeat(columnWidths[4]) + "-+-" + "-".repeat(columnWidths[5]) + "-+";

                  System.out.println(lineSeparator);
                  System.out.printf(format, "Store ID", "Address", "City", "State", "Open", "Rating");
                  System.out.println(lineSeparator);

                  for (List<String> row : results) {
                     System.out.printf(format, row.get(0), row.get(1), row.get(2), row.get(3), row.get(4), row.get(5));
                  }
                  System.out.println(lineSeparator);
            }

            // Ask if they want to search again or exit
            System.out.print("\nWould you like to search again? (yes/no): ");
            String again = in.readLine().trim().toLowerCase();
            if (again.equals("no")) {
                  going = false;
            }
         }
      } catch (Exception e) {
         System.err.println("Error: " + e.getMessage());
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
   
  
   // public static void updateUser(PizzaStore esql) { **combined with update profile**


   // }


}//end PizzaStore

