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
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Retail {
   public static String userID; 

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of Retail shop
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Retail(String dbname, String dbport, String user, String passwd) throws SQLException {

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
   }//end Retail

   // Method to calculate euclidean distance between two latitude, longitude pairs. 
   public double calculateDistance (double lat1, double long1, double lat2, double long2){
      double t1 = (lat1 - lat2) * (lat1 - lat2);
      double t2 = (long1 - long2) * (long1 - long2);
      return Math.sqrt(t1 + t2); 
   }
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
      stmt.close ();
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
    * aMethod to close the physical connection if it is open.
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
            Retail.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      Retail esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Retail object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Retail (dbname, dbport, user, "");

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
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. View Stores within 30 miles");
                System.out.println("2. View Product List");
                System.out.println("3. Place a Order");
                System.out.println("4. View 5 recent orders");

                //the following functionalities basically used by managers
                System.out.println("5. Update Product");
                System.out.println("6. View 5 recent Product Updates Info");
                System.out.println("7. View 5 Popular Items");
                System.out.println("8. View 5 Popular Customers");
                System.out.println("9. Place Product Supply Request to Warehouse");

                System.out.println(".........................");
                System.out.println("20. Log out");
                switch (readChoice()){
                   case 1: viewStores(esql); break;
                   case 2: viewProducts(esql); break;
                   case 3: placeOrder(esql); break;
                   case 4: viewRecentOrders(esql); break;
                   case 5: updateProduct(esql); break;
                   case 6: viewRecentUpdates(esql); break;
                   case 7: viewPopularProducts(esql); break;
                   case 8: viewPopularCustomers(esql); break;
                   case 9: placeProductSupplyRequests(esql); break;

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
   public static void CreateUser(Retail esql){
      try{
         System.out.print("\tEnter name: ");
         String name = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine();
         System.out.print("\tEnter latitude: ");   
         String latitude = in.readLine();       //enter lat value between [0.0, 100.0]
         System.out.print("\tEnter longitude: ");  //enter long value between [0.0, 100.0]
         String longitude = in.readLine();
         
         String type="Customer";

			String query = String.format("INSERT INTO USERS (name, password, latitude, longitude, type) VALUES ('%s','%s', %s, %s,'%s')", name, password, latitude, longitude, type);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Retail esql){
      try{
         System.out.print("\tEnter name: ");
         String name = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM USERS WHERE name = '%s' AND password = '%s'", name, password);
         int userNum = esql.executeQuery(query);
         String query2 = String.format("SELECT userID FROM USERS WHERE name = '%s' AND password = '%s'", name, password);
	 if (userNum > 0){
      List<List<String>> userResult = esql.executeQueryAndReturnResult(query2);
      userID = userResult.get(0).get(0);
		return name;
    }
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

// Rest of the functions definition go in here

   public static void viewStores(Retail esql) {
       try {
		String query = String.format("SELECT DISTINCT(s.storeID), s.name, calculate_distance(u.latitude, u.longitude, s.latitude, s.longitude) as dist FROM users u, store s WHERE calculate_distance(u.latitude, u.longitude, s.latitude, s.longitude) < 30 AND u.userID = '%s'", userID);
		esql.executeQueryAndPrintResult(query);
	} catch(Exception e){
		System.err.println (e.getMessage());
		System.out.println ("No stores less than 30 miles nearby");
	}
   }
   public static void viewProducts(Retail esql) {
     try{
		String query = "SELECT *  FROM Product WHERE Product.storeID = ";
		System.out.print("\tEnter Store ID: ");
     String input = in.readLine();
		query +=input;
		esql.executeQueryAndPrintResult(query);
	} catch (Exception e) {
		System.err.println (e.getMessage());
	}
	}
   public static void placeOrder(Retail esql) {
      try{
         System.out.print("Store ID: ");
         String storeID = in.readLine();

         System.out.print("Product name: ");
         String productName = in.readLine();

         System.out.print("Number of Units: ");
         String numUnits = in.readLine();

         String query = String.format("SELECT latitude, longitude FROM Store WHERE storeID = '%s'", storeID);
         String query2 = String.format("SELECT latitude, longitude FROM USERS WHERE userID = '%s'", userID);
   
         List<List<String>> storeLoc = esql.executeQueryAndReturnResult(query);
         List<List<String>> userLoc = esql.executeQueryAndReturnResult(query2);

         String storeLat = storeLoc.get(0).get(0);
         String storeLong = storeLoc.get(0).get(1);
         String userLat = userLoc.get(0).get(0);
         String userLong = userLoc.get(0).get(1);
         double dist = esql.calculateDistance(Double.parseDouble(userLat),Double.parseDouble(userLong), Double.parseDouble(storeLat),Double.parseDouble(storeLong) );

         if(dist < 30){
            String queryInsertProduct = String.format("INSERT INTO Orders( customerID, storeID, productName, unitsOrdered, orderTime) VALUES ('%s','%s','%s','%s',now())", userID, storeID, productName, numUnits );
            esql.executeUpdate(queryInsertProduct);
         } 
         else{
            System.out.println("The selected store is not within 30 miles.");
         }
      }
      catch (Exception e) {
		System.err.println (e.getMessage());
	}
   }
   public static void viewRecentOrders(Retail esql) {
	try {
	String query = String.format("SELECT o.storeID, s.name, o.productName, o.unitsOrdered, o.orderTime FROM Store s, Orders o WHERE o.storeID = s.storeID AND o.customerID = '%s' ORDER BY o.orderTime DESC LIMIT 5", userID);
	int userNum = esql.executeQueryAndPrintResult(query);
}
    catch (Exception e) {
        System.err.println (e.getMessage());
        }
   }

   public static void updateProduct(Retail esql) {
      try{
      String query = String.format("SELECT type FROM Users WHERE userID = '%s'", userID);
      List<List<String>> type = esql.executeQueryAndReturnResult(query);
      String userType = type.get(0).get(0);
      userType = userType.trim();
      

      if(userType.equals("manager")){
         System.out.print("Store ID: " );
         String storeID = in.readLine();
         System.out.print("Product Name: " );
         String productName = in.readLine();
         System.out.print("Updated Number of Units: " );
         String numUnits = in.readLine();
         System.out.print("Updated Price per Unit: " );
         String priceUnit = in.readLine();

         String queryGetStores = String.format("SELECT * FROM Store WHERE storeID = '%s' AND managerID = '%s'", storeID, userID);
         int storeNum = esql.executeQuery(queryGetStores);
         if(storeNum > 0 ){
            String queryGetProducts = String.format("SELECT * FROM Product WHERE productName = '%s'", productName); 
            int productNum = esql.executeQuery(queryGetProducts);
            if(productNum > 0){
               String queryUpdate = String.format("UPDATE Product SET numberOfUnits = '%s', pricePerUnit = '%s' WHERE storeID = '%s' AND productName = '%s'", numUnits, priceUnit, storeID, productName);
               esql.executeUpdate(queryUpdate);

               String queryInsert = String.format("INSERT INTO ProductUpdates(managerID, storeID, productName, updatedOn) VALUES ('%s', '%s', '%s', now())", userID, storeID, productName);
               esql.executeUpdate(queryInsert);
            }
            else{
               System.out.println("The selected product does not exist in this store.");
            }

         }
         else{
            System.out.println("You're not the manager of this store");
         }
   
      }
      else{
         System.out.print("You're not a manager");
      }
      } catch(Exception e){
         System.err.println(e.getMessage());
      }
   }
   

   
   public static void viewRecentUpdates(Retail esql) {
      try{
   
         String query = String.format("SELECT * FROM ProductUpdates ORDER BY updatedOn DESC LIMIT 5", userID);
         int userNum = esql.executeQueryAndPrintResult(query);
      }catch(Exception e){
           System.err.println (e.getMessage());
      }
      
   }
   public static void viewPopularProducts(Retail esql) {}
   public static void viewPopularCustomers(Retail esql) {}
   public static void placeProductSupplyRequests(Retail esql) {}

}//end Retail

