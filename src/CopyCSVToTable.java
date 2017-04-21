import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.util.*;

 class  RouterDevice {
     static String TitleLine;
     String RouterName;
     String RouterIP;
     Integer RouterPort;
     String DeviceName;
     String DeviceIP;
     String DeviceDescription;
     Integer DevicePort;
    
     static {

         TitleLine = "RouterName RouterIP RouterPost DeviceName DeviceIP DeviceDescription DevicePort";
    }
     
    public void setRouterName(String RouterName) {
        this.RouterName = "'" + RouterName + "'";
    }
    public void setRouterIP(String RouterIP) {
        this.RouterIP = "'" + RouterIP + "'";
    }
    public void setRouterPort(Integer RouterPort) {
        this.RouterPort = RouterPort;
    }
    public void setDeviceName(String DeviceName) {
        this.DeviceName = "'" + DeviceName + "'";
    }
    public void setDeviceIP(String DeviceIP) {
        this.DeviceIP = "'" + DeviceIP + "'";
    }
    public void setDeviceDescription(String DeviceDescription) {
        
        String strSQDeviceDescription = escapeSingleQuote(DeviceDescription);
        this.DeviceDescription = "'" + strSQDeviceDescription + "'";
    }
    public void setDevicePort(Integer DevicePort) {
        this.DevicePort = DevicePort;
    }
    public String getRouterName() {
        return RouterName;
    }
    public String getRouterIP() {
        return RouterIP;
    }
    public Integer getRouterPort() {
        return RouterPort;
    }
    public String getDeviceName() {
        return DeviceName;
    }
    public String getDeviceIP() {
        return DeviceIP;
    }
    public String getDeviceDescription() {
        return DeviceDescription;
    }
    public Integer getDevicePort() {
        return DevicePort;
    }    
    private String escapeSingleQuote(String strDescription) {
        String strSQDescription = "";
        String strSQQDescription = "";
        String[] result = strDescription.split("'");
        
        for (int i=0; i < result.length; i++) {
        if ( result[i].equalsIgnoreCase("") ) continue;
        
            if (i == (result.length - 1) ) {
                strSQDescription += result[i];
                break;
            } else {
            String str = result[i] + "''";
            strSQDescription += str;
            }
        }   
        return strSQDescription;
    } //end escapeSingleQuote

 } //end class

public class CopyCSVToTable extends JFrame {
   RouterDevice RDObject;
   ArrayList<RouterDevice> RDList = new ArrayList<>(10);
   String rdTitleLine;
  // Text file info
  private JTextField jtfFilename = new JTextField();
  private JTextArea jtaFile = new JTextArea();

  // JDBC and table info
  private JComboBox jcboDriver = new JComboBox(new String[] {
    "com.mysql.jdbc.Driver", "sun.jdbc.odbc.JdbcOdbcDriver",
    "oracle.jdbc.driver.OracleDriver", "org.apache.derby.jdbc.ClientDriver"});
  private JComboBox jcboURL = new JComboBox(new String[] {
    "jdbc:mysql://localhost/test", 
    "jdbc:odbc:exampleDataSource",
    "jdbc:oracle:thin:@simeonbin.gmail.com:1521:orcl",
    "jdbc:derby://localhost:1527/RouterDevices"});
  private JTextField jtfUsername = new JTextField();
  private JPasswordField jtfPassword = new JPasswordField();
  private JTextField jtfTableName = new JTextField();

  private JButton jbtViewFile = new JButton("Read-View CSV");
  private JButton jbtCopy = new JButton("Copy CSV Data to Tables");
  private JLabel jlblStatus = new JLabel();

  public CopyCSVToTable () {      
    
    JPanel jPanel1 = new JPanel();
    jPanel1.setLayout(new BorderLayout());
    jPanel1.add(new JLabel("Filename"), BorderLayout.WEST);
    jPanel1.add(jbtViewFile, BorderLayout.EAST);
    jtfFilename.setText("connections-short.csv");
    jtfFilename.setPreferredSize(new Dimension(100, 20));
    jtfFilename.setMinimumSize(new Dimension(80, 20));
    jPanel1.add(jtfFilename, BorderLayout.CENTER);

    JPanel jPanel2 = new JPanel();
    jPanel2.setLayout(new BorderLayout());
    jPanel2.setBorder(new TitledBorder("Source Text File"));
    jPanel2.add(jPanel1, BorderLayout.NORTH);
    jPanel2.add(new JScrollPane(jtaFile), BorderLayout.CENTER);

    JPanel jPanel3 = new JPanel();
    jPanel3.setLayout(new GridLayout(5, 0));
    jPanel3.add(new JLabel("JDBC Driver"));
    jPanel3.add(new JLabel("Database URL"));
    jPanel3.add(new JLabel("Username"));
    jPanel3.add(new JLabel("Password"));
    jPanel3.add(new JLabel("Table Name(s)"));

    JPanel jPanel4 = new JPanel();
    jPanel4.setLayout(new GridLayout(5, 0));
    jcboDriver.setEditable(true);
    jPanel4.add(jcboDriver);
    jcboURL.setEditable(true);
    jPanel4.add(jcboURL);
    jtfUsername.setText("simbin");
    jPanel4.add(jtfUsername);
    jtfPassword.setText("password");
    jPanel4.add(jtfPassword);
    jtfTableName.setText("RouterDevices");
    jPanel4.add(jtfTableName);

    JPanel jPanel5 = new JPanel();
    jPanel5.setLayout(new BorderLayout());
    jPanel5.setBorder(new TitledBorder("Target Database Table"));
    jPanel5.add(jbtCopy, BorderLayout.SOUTH);
    jPanel5.add(jPanel3, BorderLayout.WEST);
    jPanel5.add(jPanel4, BorderLayout.CENTER);

    add(jlblStatus, BorderLayout.SOUTH);
    add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
      jPanel2, jPanel5), BorderLayout.CENTER);

    jbtViewFile.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent evt) {
        readShowFile();
      }
    });

    jbtCopy.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent evt) {
        try {
          copyFile();
        }
        catch (Exception ex) {
          jlblStatus.setText(ex.toString());
        }
      }
    });
  } // end of CopyCSVToTable constructor

  private RouterDevice setRouterDevice(String[] strRD ) {
      RDObject = new RouterDevice();
      
      RDObject.setRouterName (strRD[0].trim());
      RDObject.setRouterIP (strRD[1].trim());
      
      Integer intRD = Integer.valueOf(strRD[2].trim());
      RDObject.setRouterPort (intRD);
      RDObject.setDeviceName (strRD[3].trim());
      RDObject.setDeviceIP (strRD[4].trim());
      RDObject.setDeviceDescription (strRD[5].trim());
      
      intRD = Integer.valueOf(strRD[6].trim());
      RDObject.setDevicePort (intRD);    

       return RDObject;
  }
  
  private String getRouterDevice( RouterDevice RDObject) {
      
      String str1 = RDObject.getRouterName ();
      String str2 = RDObject.getRouterIP();
      
      String strRouterPort = String.valueOf(RDObject.getRouterPort());
      String str4 = RDObject.getDeviceName();
      String str5 = RDObject.getDeviceIP();
      String str6 = RDObject.getDeviceDescription ();
      
      String strDevicePort = String.valueOf(RDObject.getDevicePort());
      
      String strRouterDevice = str1 + "," + str2 + "," + strRouterPort + "," + 
                           str4 + "," + str5 + "," + str6 + "," + strDevicePort;
      
      return strRouterDevice;
  }
  
  /** Display the file in the text area */
  private void readShowFile() {
    Scanner input = null;
    File fileInput;
    RouterDevice RD = null;
    
    String[] RDArray = new String[7];
    String rdLine = null;
    jtaFile.setText("");
        
    try {
      // Use a Scanner to read text from the file
      fileInput = new File(jtfFilename.getText().trim());
      input = new Scanner(fileInput);
      
      if (input.hasNext()) {
          rdTitleLine = input.nextLine();
          RouterDevice.TitleLine = rdTitleLine;
      } 
             
      // Read a line, split to comma and append the line to the text area
      while (input.hasNext()) {         
         rdLine = input.nextLine();
          System.out.println ("Reading CSV " + rdLine);
          
         jtaFile.append(rdLine + '\n');
         RDArray = (rdLine.split(",")); 
      
      //Build the RouterDevice object from RDArray
        RD = setRouterDevice(RDArray);
          boolean addB = RDList.add(RD);   
      }      
         
    }    
    catch (FileNotFoundException ex) {
      System.out.println("File not found: " + jtfFilename.getText());
    }
    //catch (IOException ex) {
   // }
    finally {
      if (input != null) input.close();
    }
  }

  private void copyFile() throws Exception {
    Connection conn = null;
    Properties connectionProps = new Properties();
    connectionProps.put("user", "simbin");
    connectionProps.put("password", "password");
      
       // Load the JDBC driver
       //   Class.forName(((String)jcboDriver.getSelectedItem()).trim());
       Class<?> forName = Class.forName("org.apache.derby.jdbc.ClientDriver");
    System.out.println("Driver loaded");

    // Establish a connection
//    Connection conn = DriverManager.getConnection
//      (((String)jcboURL.getSelectedItem()).trim(),
//      jtfUsername.getText().trim(),
//      String.valueOf(jtfPassword.getPassword()).trim());
    
conn = DriverManager.getConnection("jdbc:" + "derby" + "://" +
                           "localhost:1527/" + "RouterDevices/" +
                   ";create=true", connectionProps);   

    System.out.println("Database connected");

    // Read each line from the text file and insert it to the table
    insertRows(conn);
  }

  private void insertRows(Connection connection) {
      String rdLine = null;
      String rId=""; String rName=""; String rIP = ""; String rPort = "";
      String dId=""; String dName = "";String dIP = ""; String dPort = "";
      RouterDevice RDO;
      long  keyNumRouter = 1;
      long keyNumDevice = 1;
      
    // Build the SQL INSERT statement
    String sqlInsert = "insert into " + jtfTableName.getText() + " values (";    
    String sqlInsert2 = "insert into " + "ROUTER(NAME,IP) " + " values (";    
    String sqlInsert3 = "insert into " + "DEVICE(NAME,IP)"  + " values (";
    String sqlInsert4 = "insert into " + "CONNECTIONS "  + " values (";

    // Use a Scanner to read text from the file
    Scanner input = null;
    // Get file name from the text field
    String filename = jtfFilename.getText().trim();

    try {
      // Create a scanner
      input = new Scanner(new File(filename));

      // Create a few statement's
      Statement statement = connection.createStatement();
      Statement statement2 = connection.createStatement();
      Statement statement3 = connection.createStatement();
      Statement statement4 = connection.createStatement();

      System.out.println("Driver major version? " +
        connection.getMetaData().getDriverMajorVersion());

      // Determine if batchUpdatesSupported is supported
      boolean batchUpdatesSupported = false;

      try {
        if (connection.getMetaData().supportsBatchUpdates()) {
          batchUpdatesSupported = true;
          System.out.println("batch updates supported");
        }
        else {
          System.out.println("The driver is of JDBC 2 type, but " +
            "does not support batch updates");
        }
      }
      catch (UnsupportedOperationException ex) {
        System.out.println("The driver does not support JDBC 2");
      }

      // Determine if the driver is capable of batch updates
      // For now forget Batch Updates, we'll look at later
      batchUpdatesSupported = false;
    if (batchUpdatesSupported) {
          
     // Read a line and add the insert table command to the batch
     // while (input.hasNext()) {
     // String sqlString =  sqlInsert + input.nextLine() + ")";
     // Go through RDList of RouterDevice Objects
     
     for (int iRD = 0; iRD < RDList.size(); iRD++ ) {             
    
     RDO = RDList.get(iRD); rdLine = getRouterDevice(RDO);
     rId = String.valueOf(iRD+1); rName=RDO.getRouterName(); rIP=RDO.getRouterIP();
     rPort= String.valueOf(RDO.getRouterPort()); dPort = String.valueOf(RDO.getDevicePort());
     dId = String.valueOf(iRD+1); dName=RDO.getDeviceName(); dIP=RDO.getDeviceIP();
     
    String sqlString = sqlInsert + rdLine + ")";   
    String sqlString2 = sqlInsert2+rId+","+rName+","+rIP + ")"; 
    String sqlString3 = sqlInsert3+dId+","+dName+","+dIP + ")"; 
    String sqlString4 = sqlInsert4+rId+","+rPort+","+dId+","+dPort + ")"; 
            
    System.out.println(sqlString); System.out.println(sqlString2);
    System.out.println(sqlString3); System.out.println(sqlString4);
            
    statement.addBatch(sqlString); statement2.addBatch(sqlString2);
    statement3.addBatch(sqlString3); statement4.addBatch(sqlString4);
            
    } // end for 
    statement.executeBatch(); statement2.executeBatch();
    statement3.executeBatch(); statement4.executeBatch();
       
      jlblStatus.setText("Batch updates completed");
    }
    else {
        // Read a line and execute insert table command
        // while (input.hasNext()) {
        // Go through RDList of RouterDevice Objects
        for (int iRD = 0; iRD < RDList.size(); iRD++ ) {
        //Get a RouterDevice Object    
        RDO = RDList.get(iRD);
        rdLine = getRouterDevice(RDO); // Build a "LINE" to INSERT 
     rId = String.valueOf(iRD+1); rName=RDO.getRouterName(); rIP=RDO.getRouterIP();
     rPort= String.valueOf(RDO.getRouterPort()); dPort = String.valueOf(RDO.getDevicePort());
     dId = String.valueOf(iRD+1); dName=RDO.getDeviceName(); dIP=RDO.getDeviceIP();
     
     String sqlString = sqlInsert + rdLine + ")";   
     String sqlString2 = sqlInsert2+rName+","+rIP + ")"; 
     String sqlString3 = sqlInsert3+dName+","+dIP + ")"; 
              
     System.out.println(sqlString);System.out.println(sqlString2); System.out.println(sqlString3);             
           
     statement.executeUpdate(sqlString);
       
       statement2.executeUpdate(sqlString2, Statement.RETURN_GENERATED_KEYS);
       ResultSet rs = statement2.getGeneratedKeys();            
       if (rs != null && rs.next()) {
        keyNumRouter = rs.getLong(1);
        }
       
       statement3.executeUpdate(sqlString3, Statement.RETURN_GENERATED_KEYS);
       rs = statement3.getGeneratedKeys();            
       if (rs != null && rs.next()) {
        keyNumDevice = rs.getLong(1);
        }
       System.out.println("keyNumRouter = " + keyNumRouter); // autincrement for ROUTER Table
       System.out.println("keyNumDevice = " + keyNumDevice); // autoincrement for DEVICE Table  
       
       String sqlString4 = sqlInsert4+keyNumRouter+","+rPort+","+keyNumDevice+","+dPort + ")";       
       System.out.println(sqlString4);
       
       statement3.executeUpdate(sqlString4);
       
        } // end of for iRD

        jlblStatus.setText("Single row updates completed");
     } // end of else
      
    } // end new Scanner
    catch (SQLException ex) {
      System.out.println(ex);
    }
    catch (FileNotFoundException ex) {
      System.out.println("File not found: " + filename);
    }
    //catch (IOException ex) {
    //  ex.printStackTrace();
   // }
    finally {
      if (input != null) input.close();
    }
  }

  public static void main(String args[]) {
    JFrame frame = new CopyCSVToTable();
    frame.setTitle("CopyCSVToTable");
    frame.setSize(700, 200);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}
