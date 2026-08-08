package com.vivero.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vivero.dto.CustomerDTOs.*;
import com.vivero.entity.Customer;
import com.vivero.exception.ResourceNotFoundException;
import com.vivero.repository.CustomerRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    @PostConstruct
    @Transactional
    public void initDefaultCustomers() {
        if (customerRepository.count() == 0) {
            customerRepository.save(Customer.builder()
                    .fullName("Juan Pérez")
                    .documentType("DNI")
                    .documentNumber("45892134")
                    .phone("+51 981234567")
                    .whatsapp("51981234567")
                    .email("juan.perez@email.com")
                    .address("Av. Los Jardines 123, San Isidro")
                    .isFrequent(true)
                    .totalPurchases(new BigDecimal("2450.00"))
                    .lastPurchaseDate(LocalDateTime.now().minusDays(2))
                    .notes("Cliente Frecuente. Compra Grass Americano en rollos para proyectos de paisajismo.")
                    .active(true)
                    .build());

            customerRepository.save(Customer.builder()
                    .fullName("María López")
                    .documentType("DNI")
                    .documentNumber("71239845")
                    .phone("+51 987654321")
                    .whatsapp("51987654321")
                    .email("maria.lopez@email.com")
                    .address("Calle Las Flores 456, Miraflores")
                    .isFrequent(true)
                    .totalPurchases(new BigDecimal("1800.00"))
                    .lastPurchaseDate(LocalDateTime.now().minusDays(5))
                    .notes("Le gustan las Palmeras Areca y Ficus Lyrata para departamento.")
                    .active(true)
                    .build());

            customerRepository.save(Customer.builder()
                    .fullName("Carlos Ruiz")
                    .documentType("DNI")
                    .documentNumber("10458923")
                    .phone("+51 974125896")
                    .whatsapp("51974125896")
                    .email("carlos.ruiz@email.com")
                    .address("Jr. El Bosque 789, Surco")
                    .isFrequent(false)
                    .totalPurchases(new BigDecimal("850.00"))
                    .lastPurchaseDate(LocalDateTime.now().minusDays(8))
                    .notes("Solicitó cotización de instalación de m² de grass bermuda.")
                    .active(true)
                    .build());

            customerRepository.save(Customer.builder()
                    .fullName("Inmobiliaria Los Parques S.A.C.")
                    .documentType("RUC")
                    .documentNumber("20601234567")
                    .phone("+51 963258741")
                    .whatsapp("51963258741")
                    .email("compras@losparques.pe")
                    .address("Av. Primavera 1020, San Borja")
                    .isFrequent(true)
                    .totalPurchases(new BigDecimal("5200.00"))
                    .lastPurchaseDate(LocalDateTime.now().minusDays(1))
                    .notes("Cliente Empresa RUC. Requiere facturación y despachos semanales.")
                    .active(true)
                    .build());
        }
    }

    public List<CustomerDTO> getAllCustomers() {
        if (customerRepository.count() == 0) {
            initDefaultCustomers();
        }
        return customerRepository.findByActiveTrue().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CustomerDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + id));
        return mapToDTO(customer);
    }

    public List<CustomerDTO> searchCustomers(String query) {
        return customerRepository.findByFullNameContainingIgnoreCaseOrPhoneContaining(query, query).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CustomerDTO createCustomer(CreateCustomerRequest request, String createdBy) {
        Customer customer = Customer.builder()
                .fullName(request.getFullName())
                .documentType(request.getDocumentType() != null ? request.getDocumentType() : "DNI")
                .documentNumber(request.getDocumentNumber())
                .phone(request.getPhone())
                .whatsapp(request.getWhatsapp() != null ? request.getWhatsapp() : request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .notes(request.getNotes())
                .isFrequent(false)
                .totalPurchases(BigDecimal.ZERO)
                .active(true)
                .createdBy(createdBy)
                .build();

        customerRepository.save(customer);
        return mapToDTO(customer);
    }

    @Transactional
    public CustomerDTO updateCustomer(Long id, CreateCustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + id));

        customer.setFullName(request.getFullName());
        if (request.getDocumentType() != null) customer.setDocumentType(request.getDocumentType());
        if (request.getDocumentNumber() != null) customer.setDocumentNumber(request.getDocumentNumber());
        customer.setPhone(request.getPhone());
        customer.setWhatsapp(request.getWhatsapp() != null ? request.getWhatsapp() : request.getPhone());
        if (request.getEmail() != null) customer.setEmail(request.getEmail());
        if (request.getAddress() != null) customer.setAddress(request.getAddress());
        if (request.getNotes() != null) customer.setNotes(request.getNotes());

        customerRepository.save(customer);
        return mapToDTO(customer);
    }

    public CustomerDTO mapToDTO(Customer customer) {
        return CustomerDTO.builder()
                .id(customer.getId())
                .fullName(customer.getFullName())
                .documentType(customer.getDocumentType())
                .documentNumber(customer.getDocumentNumber())
                .phone(customer.getPhone())
                .whatsapp(customer.getWhatsapp())
                .email(customer.getEmail())
                .address(customer.getAddress())
                .isFrequent(customer.getIsFrequent())
                .totalPurchases(customer.getTotalPurchases())
                .lastPurchaseDate(customer.getLastPurchaseDate())
                .notes(customer.getNotes())
                .active(customer.getActive())
                .build();
    }
}
