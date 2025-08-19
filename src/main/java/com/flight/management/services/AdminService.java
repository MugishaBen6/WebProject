package com.flight.management.services;

import com.flight.management.model.Admin;
import com.flight.management.repositories.AdminRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    public Optional<Admin> getAdminById(Long id) {
        return adminRepository.findById(id);
    }

    public Admin createAdmin(Admin admin) {
        return adminRepository.save(admin);
    }

    public Admin updateAdmin(Long id, Admin updatedAdmin) {
        if (adminRepository.existsById(id)) {
            updatedAdmin.setId(id);
            return adminRepository.save(updatedAdmin);
        }
        return null; // Or throw an exception
    }

    public void deleteAdmin(Long id) {
        adminRepository.deleteById(id);
    }

    // Add methods for specific business logic related to Admins
}