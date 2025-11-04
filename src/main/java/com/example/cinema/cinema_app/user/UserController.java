package com.example.cinema.cinema_app.user;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping()
    public String getAllUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "user/listUsers";
    }

    @GetMapping("/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new User());
        return "user/addUser";
    }

    @PostMapping("/add")
    public String addUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("success", "Пользователь успешно добавлен!");
        return "redirect:/users";
    }

    @GetMapping("/edit")
    public String showEditUserForm(@RequestParam String email, Model model) {
        User user = userRepository.findByEmail(email);
        model.addAttribute("user", user);
        return "user/editUser";
    }

    @PostMapping("/edit")
    @Transactional
    public String editUser(@ModelAttribute User user,
                           RedirectAttributes redirectAttributes) {

        try {
            System.out.println("Получен пользователь: " + user.getEmail() + ", имя: " + user.getName());

            User existingUser = userRepository.findByEmail(user.getEmail());
            if (existingUser == null) {
                redirectAttributes.addFlashAttribute("error", "Пользователь не найден!");
                return "redirect:/users";
            }

            existingUser.setName(user.getName());

            if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
                existingUser.setPassword(user.getPassword());
                System.out.println("Пароль обновлен (без хеширования)");
            }

            userRepository.save(existingUser);
            System.out.println("Пользователь сохранен");
            redirectAttributes.addFlashAttribute("success", "Пользователь успешно обновлен!");

        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Ошибка при обновлении пользователя: " + e.getMessage());
        }

        return "redirect:/users";
    }

    @PostMapping("/delete")
    @Transactional
    public String deleteUser(@RequestParam String email, RedirectAttributes redirectAttributes) {
        userRepository.deleteByEmail(email);
        redirectAttributes.addFlashAttribute("success", "Пользователь успешно удален!");
        return "redirect:/users";
    }
}
