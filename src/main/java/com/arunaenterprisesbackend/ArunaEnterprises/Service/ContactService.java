package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.ContactDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.ContactMessage;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    public String registerContactInfo(ContactDTO contactinfo) {
        try{
            ContactMessage contactMessage = new ContactMessage();

            contactMessage.setName(contactinfo.getName());
            contactMessage.setEmail(contactinfo.getEmail());
            contactMessage.setPhone(contactinfo.getPhone());
            contactMessage.setMessage(contactinfo.getMessage());
            contactRepository.save(contactMessage);
            return "Contact Info submitted";
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while registering contact info", e);
        }

    }
}
