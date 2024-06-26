import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class RegistrationForm extends JDialog {
    private JTextField tfName;
    private JTextField tfEmail;
    private JTextField tfPhone;
    private JTextField tfAddress;
    private JButton btnRegister;
    private JButton btnCancel;
    private JPanel registerPanel;
    private JPasswordField pfPassword;
    private JPasswordField pfConfirmPassword;
    private JButton login;

    public RegistrationForm(JFrame parent) {
        super(parent);
        setTitle("Create a new account");
        setContentPane(registerPanel);
        setMinimumSize(new Dimension(600, 500));
        setModal(true);
        setLocationRelativeTo(parent);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openLoginForm();
            }
        });
        setVisible(true);
    }
    private void registerUser() {
        String name = tfName.getText();
        String email = tfEmail.getText();
        String phone = tfPhone.getText();
        String address = tfAddress.getText();
        String password = pfPassword.getText();
        String confirmPassword = String.valueOf(pfConfirmPassword.getPassword());

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()
                || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields " +
                    "are required", "Try again", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do" +
                    " not match", "Try again", JOptionPane.ERROR_MESSAGE);
            return;
        }
       user = addUserToDatabase(name,email,phone,address,password);
        if(user != null) {
            dispose();
        }else {
            JOptionPane.showMessageDialog(this, "Failed to " +
                    "register new user", "Try again", JOptionPane.ERROR_MESSAGE);

        }
    }
    private void openLoginForm() {
        dispose();
        LoginForm loginForm = new LoginForm(null);
    }

    public User user;
    private User addUserToDatabase(String name, String email, String phone,
                                   String address, String password) {
        User user = null;

        final String DB_URL="jdbc:mysql://localhost:/MyStore?serverTimezone=UTC";
        final String USERNAME="root";
        final String PASSWORD="";


        try{
            Connection conn= DriverManager.getConnection(DB_URL,USERNAME, PASSWORD);

            Statement stmt=conn.createStatement();
            String sql="INSERT INTO users (name, email, phone, address, password)" +
                    "VALUES (?,?,?,?,?)";
            PreparedStatement preparedStatement=conn.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, phone);
            preparedStatement.setString(4, address);
            preparedStatement.setString(5, password);

            int addedRows=preparedStatement.executeUpdate();

            if(addedRows>0){
                user = new User();
                user.name = name;
                user.email = email;
                user.phone = phone;
                user.address = address;
                user.password = password;

            }
            stmt.close();
            conn.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
        return user;
    }

    public static void main(String[] args) {
        RegistrationForm myForm = new RegistrationForm(null);
        User user = myForm.user;
        if(user != null) {
            System.out.println("Successful registration of " +user.name);
        }else{
            System.out.println("Registration canceled ");
        }
    }
}
