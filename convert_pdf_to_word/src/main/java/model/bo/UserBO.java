package model.bo;
import java.util.Optional;
import model.bean.User;
import model.dao.UserDAO;

public class UserBO {
    private UserDAO userDAO = new UserDAO();

    public boolean isEmailRegistered(String email){
        Optional<User> user = userDAO.findByEmail(email);
        if(user.isPresent()){
            return true;
        }
        return false;
    }

    public boolean registerUser(User user){
        if(isEmailRegistered(user.getEmail())){
            return false;
        }
        return userDAO.register(user);
    }

    public boolean login(String email, String password){
        return userDAO.login(email, password);
    }

    public User findUserByEmail(String email){
        Optional<User> user = userDAO.findByEmail(email);
        return user.orElse(null);
    }
}
