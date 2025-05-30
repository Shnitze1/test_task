package com.example.test_task.services;

import com.example.test_task.exeptions.InvalidInitDataException;
import com.example.test_task.exeptions.UserDataProcessingException;
import com.example.test_task.models.User;
import com.example.test_task.repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static com.example.test_task.utils.SameMethods.parseInitData;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TelegramAuthService telegramAuthService;
    private final ObjectMapper objectMapper;

    @Transactional
    public User processTelegramInitData(String initData) throws InvalidInitDataException, UserDataProcessingException, JsonProcessingException {
        validateInitData(initData);
        Map<String, String> params = parseInitData(initData);
        JsonNode userNode = objectMapper.readTree(params.get("user"));

        return findOrCreateUser(userNode);
    }

    private void validateInitData(String initData) throws InvalidInitDataException {
        if (!telegramAuthService.validateInitData(initData)) {
            throw new InvalidInitDataException("Telegram authentication failed");
        }
    }


    private User findOrCreateUser(JsonNode userNode) throws UserDataProcessingException {
        try {
            Long userId = userNode.get("id").asLong();
            String firstName = userNode.get("first_name").asText();
            String lastName = userNode.path("last_name").asText("");
            String username = userNode.path("username").asText("");

            return userRepository.findById(userId)
                    .map(existingUser -> updateUser(existingUser, firstName, lastName, username))
                    .orElseGet(() -> createUser(userId, firstName, lastName, username));
        } catch (Exception exception) {
            throw new UserDataProcessingException("Error processing user data", exception);
        }
    }

    private User createUser(Long id, String firstName, String lastName, String username) {
        User newUser = new User(id, firstName, lastName, username);
        return userRepository.save(newUser);
    }

    private User updateUser(User user, String firstName, String lastName, String username) {
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUserName(username);
        return userRepository.save(user);
    }
}