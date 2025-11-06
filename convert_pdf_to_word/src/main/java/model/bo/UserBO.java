package model.bo;
import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;

import model.bean.User;
import model.dao.UserDAO;
public class UserBO {
    private final UserDAO userDAO = new UserDAO();

    public boolean isEmailRegistered(String email){
        Optional<User> user = userDAO.findByEmail(email);
        return user.isPresent();
    }

    public boolean isAccountExist(String email, String username) {
        Optional<User> user = userDAO.findByEmailAndUsername(email, username);
        return user.isPresent();
    }

    public boolean forgotPassword(String email, String username, String newPassword) {
        Optional<User> userOpt = userDAO.findByEmailAndUsername(email, username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(hashPassword(newPassword));
            return userDAO.resetPassword(user);
        }
        return false;
    }

    public boolean registerUser(User user){
        if(isEmailRegistered(user.getEmail())){
            return false;
        }
        
        String hashedPassword = hashPassword(user.getPassword());
        user.setPassword(hashedPassword);

        return userDAO.register(user);
    }

    public Optional<User> login(String email, String password){
        Optional<User> userOpt = userDAO.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String storedHash = user.getPassword();
            
            if (checkPassword(password, storedHash)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public User findUserByEmail(String email){
        Optional<User> user = userDAO.findByEmail(email);
        return user.orElse(null);
    }

    //Hash mật khẩu thô
    private String hashPassword(String plaintextPassword) {
        return BCrypt.hashpw(plaintextPassword, BCrypt.gensalt());
    }

    //Kiểm tra mất khẩu thô với hash đã lưu
    private boolean checkPassword(String plaintextPassword, String hashedPassword) {
        return BCrypt.checkpw(plaintextPassword, hashedPassword);
    }
}
