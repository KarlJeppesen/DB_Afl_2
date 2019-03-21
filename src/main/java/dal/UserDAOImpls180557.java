package dal;

import dal.dto.IUserDTO;
import dal.dto.UserDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//Some elements of this implementation are inspired by "https://drive.google.com/file/d/1Mu0fsUye_lL5bIuuczq5y88Cd9GbsHyt/view" by Christian Budtz, chbu@dtu.dk
//For changes according to link refer to previously submitted assignment.

public class UserDAOImpls180557 implements IUserDAO {

    String user = "s180557";
    String pw = "SnM6HsTt8iPhYpasthnCW";
    String path = "jdbc:mysql://ec2-52-30-211-3.eu-west-1.compute.amazonaws.com/"+user+"?";

    private Connection createConnection() throws SQLException {
        return  DriverManager.getConnection(path + "user=" + user + "&password=" + pw + "&useSSL=FALSE");
    }

    @Override
    public void createUser(IUserDTO user) throws DALException {
        //SSL has been disabled

        try (Connection c = createConnection()) {

            String roleString = String.join(";", user.getRoles());

            PreparedStatement createUser = c.prepareStatement(
                    "INSERT INTO userTable (userId,userName,ini,roles) VALUES (?, ?, ?, ?)" );

            createUser.setInt(1, user.getUserId());
            createUser.setString(2, user.getUserName());
            createUser.setString(3, user.getIni());
            createUser.setString(4, roleString);

            createUser.executeUpdate();

        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }

    }

    @Override
    public IUserDTO getUser(int userId) throws DALException {

        try (Connection c = createConnection()){

            UserDTO user = null;

            PreparedStatement getUser = c.prepareStatement("SELECT * FROM userTable where userId = ?");
            getUser.setInt(1, userId);

            ResultSet resultSet = getUser.executeQuery();

            //Make a user from the resultset

            if (resultSet.next()) {
                user = new UserDTO();
                user.setUserId(userId);
                user.setUserName(resultSet.getString(2));
                user.setIni(resultSet.getString(3));

                //Extract roles as String
                String roleString = resultSet.getString(4);
                //Split string by ;
                String[] roleArray = roleString.split(";");
                //Convert to List
                List<String> roleList = Arrays.asList(roleArray);
                user.setRoles(roleList);
            }

            return user;
        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }

    }



    @Override
    public List<IUserDTO> getUserList() throws DALException {

        //No need for prepared statements here because no variables are being taken in.

        try (Connection c = createConnection()) {

            List<IUserDTO> users = new ArrayList<>();

            Statement statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM userTable;");

            while (resultSet.next()) {

                String userIDString = resultSet.getString(1);
                int userID = Integer.parseInt(userIDString);
                users.add(getUser(userID));
            }

            return users;

        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }

    }



    @Override
    public void updateUser(IUserDTO user) throws DALException {

        try (Connection c = createConnection()){

            String roleString = String.join(";", user.getRoles());

            PreparedStatement updateUser = c.prepareStatement(
                    "UPDATE userTable SET userName = ?, ini = ?, roles = ? WHERE userId = ?;");

            updateUser.setString(1, user.getUserName());
            updateUser.setString(2, user.getIni());
            updateUser.setString(3, roleString);
            updateUser.setInt(4, user.getUserId());

            updateUser.execute();

        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }

    }

    @Override
    public void deleteUser(int userId) throws DALException {

        try (Connection c = createConnection()){

            PreparedStatement deleteUser = c.prepareStatement("DELETE FROM userTable WHERE userId = ?");
            deleteUser.setInt(1, userId);

            deleteUser.execute();

        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }

    }

}
