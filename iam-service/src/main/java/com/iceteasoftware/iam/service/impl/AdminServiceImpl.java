package com.iceteasoftware.iam.service.impl;

import com.iceteasoftware.iam.repository.UserRepository;
import com.iceteasoftware.iam.service.AdminService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;

    @Override
    public List<String> getAllAdminEmails() {
        return userRepository.findAllAdminEmails();
    }
}
