package org.example.backend.service;

import org.example.backend.exeption.*;
import org.example.backend.model.Advertisement;
import org.example.backend.model.User;
import org.example.backend.repository.AdvertisementRepository;
import org.example.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AdvertisementRepository advertisementRepository;

    public UserService(UserRepository userRepository ,AdvertisementRepository advertisementRepository ) {
        this.userRepository = userRepository;
        this.advertisementRepository = advertisementRepository;
    }

    public boolean registerUser(User newUser) {

        String pass = newUser.getPassword();
        if ( pass == null || pass.length() < 8 ){
            throw new PassNotValidException("رمز عبور باید حداقل ۸ کاراکتر باشد.");
        }
        String fullName = newUser.getFullName();
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("وارد کردن نام و نام خانوادگی اجباری است.");
        }
        String phoneNum = newUser.getPhoneNum();
        if ( phoneNum == null || phoneNum.length() != 11 || !phoneNum.startsWith("09")){
            throw new InvalidPhoneNumException("شماره تلفن وارد شده معتبر نیست (باید ۱۱ رقم و با ۰۹ آغاز شود).");        }

        List<User> users = userRepository.findAll();
        for ( User user : users ){
            if ( user.getUsername().equals(newUser.getUsername())){
                throw new UsernameAlreadyExistsException("این نام کاربری قبلاً ثبت شده است.");            }
            if ( user.getEmail().equals(newUser.getEmail())){
                throw new EmailAlreadyExistsException("این ایمیل قبلاً ثبت شده است.");            }
            if ( user.getPhoneNum().equals(newUser.getPhoneNum())){
                throw new PhoneNumAlreadyExistsException("این شماره تلفن قبلاً ثبت شده است.");            }
        }

        newUser.setId(UUID.randomUUID().toString());
        newUser.setRole("USER");
        newUser.setEnabled(true);
        userRepository.save(newUser);
        return true;
    }


    public Optional<User> loginUser(String username, String password) {


        User myUser = userRepository.findAll().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("این شناسه کاربری وجود ندارد"));

        if ( !myUser.getPassword().equals(password)){
            throw new WrongPasswordException("پسوورد اشتباه");
        }
        if (!myUser.isEnabled()){
            throw new UserBannedException("کاربر مسدود است");
        }
        return Optional.of(myUser);
    }

    public boolean addAdvertisementToFavorites(String userId, String adId) {
        User myUser = userRepository.findByID(userId)
                .orElseThrow(() -> new UserNotFoundException("کاربر مورد نظر یافت نشد."));

        if (!advertisementRepository.existsById(adId)) {
            throw new AdvertisementNotFoundException("آگهی مورد نظر وجود ندارد یا حذف شده است");
        }
        List<String> userFavsId = myUser.getFavoriteAdIds();
        if ( userFavsId.contains(adId)){
            throw new AdAlreadyFavoriteException("این آگهی قبلاً در لیست علاقه مندی های شما ذخیره شده است");
        }

        myUser.getFavoriteAdIds().add(adId);
        userRepository.save(myUser);

        return true;
    }

    public boolean removeAdvertisementFromFavorites(String userId, String adId) {
        User myUser = userRepository.findByID(userId)
                .orElseThrow(() -> new UserNotFoundException("کاربر مورد نظر یافت نشد."));

        if (!advertisementRepository.existsById(adId)) {
            throw new AdvertisementNotFoundException("آگهی مورد نظر وجود ندارد یا حذف شده است");
        }

        List<String> userFavsId = myUser.getFavoriteAdIds();
        if ( !userFavsId.contains(adId)){
            throw new AdNotFavException("آگهی در لیست علاقه مندی نیست");
        }
        userFavsId.removeIf(ID -> ID.equals(adId));
        userRepository.save(myUser);


        return true;
    }

    public List<String> getUserFavoriteAdIds(String userId) {
        User myUser = userRepository.findByID(userId)
                .orElseThrow(() -> new UserNotFoundException("کاربر مورد نظر یافت نشد."));

        return myUser.getFavoriteAdIds();
    }


    public List<User> getAllUsersForAdmin(String adminId) {
        User requester = userRepository.findByID(adminId)
                .orElseThrow(() -> new NoAcceessException("شما دسترسی لازم برای مشاهده این اطلاعات را ندارید!"));

        if (!"ADMIN".equalsIgnoreCase(requester.getRole())) {
            throw new NoAcceessException("شما دسترسی لازم برای مشاهده این اطلاعات را ندارید!");
        }
        List<User> allUsers = userRepository.findAll();
        return allUsers.stream().filter(user -> "USER".equalsIgnoreCase(user.getRole())).toList();
    }


    public boolean blockUser(String userId, String adminID) {
        User admin = userRepository.findByID(adminID).orElseThrow(() -> new UserNotFoundException("ادمین یافت نشد"));
        if ( !admin.getRole().equals("ADMIN")){
            throw new NoAcceessException("برای مسدود کردن دسترسی ادمین لازم است");
        }
        User myUser = userRepository.findByID(userId).orElseThrow(() -> new UserNotFoundException("کاربر یافت نشد"));
        myUser.setEnabled(false);
        userRepository.save(myUser);

        return true;
    }

    public boolean unblockUser(String userId , String adminId) {
        User admin = userRepository.findByID(adminId).orElseThrow(() -> new UserNotFoundException("ادمین یافت نشد"));
        if ( !admin.getRole().equals("ADMIN")){
            throw new NoAcceessException("برای مسدود کردن دسترسی ادمین لازم است");
        }
        User myUser = userRepository.findByID(userId).orElseThrow(() -> new UserNotFoundException("کاربر یافت نشد"));
        myUser.setEnabled(true);
        userRepository.save(myUser);

        return true;
    }
}