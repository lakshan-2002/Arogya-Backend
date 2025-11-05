package com.lakshan.user_service.service;

import com.lakshan.user_service.entity.UserProfile;
import com.lakshan.user_service.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    @Autowired
    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public void addNewUserProfile(UserProfile userProfile) {
        userProfileRepository.save(userProfile);
    }

    public UserProfile getUserProfileById(int id){
        return userProfileRepository.findById(id).orElseThrow(() ->
                new RuntimeException("User Profile not found with id: " + id)
        );
    }

    public List<UserProfile> getAllUserProfiles(){
        return userProfileRepository.findAll();
    }

    public UserProfile getUserProfileByUserId(int userId){
        return userProfileRepository.findByUserId(userId).orElseThrow(() ->
                new RuntimeException("User Profile not found with user id: " + userId)
        );
    }

    public void updateUserProfile(UserProfile userProfile){
        if(userProfileRepository.existsById(userProfile.getId()))
            userProfileRepository.save(userProfile);
        else
            throw new RuntimeException("User Profile not found with id: " + userProfile.getId());
    }

    public void deleteUserProfile(int id){
        if(userProfileRepository.existsById(id))
            userProfileRepository.deleteById(id);
        else
            throw new RuntimeException("User Profile not found with id: " + id);
    }
}
