package com.vivero.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vivero.dto.DriverDTOs.*;
import com.vivero.entity.Driver;
import com.vivero.exception.ResourceNotFoundException;
import com.vivero.repository.DriverRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;

    @PostConstruct
    @Transactional
    public void initDefaultDrivers() {
        if (driverRepository.count() == 0) {
            driverRepository.save(Driver.builder()
                    .fullName("Carlos Delivery")
                    .documentNumber("45891234")
                    .phone("+51 987654323")
                    .vehicleInfo("Camión Isuzu 3.5T (Placa: ABC-123)")
                    .licenseNumber("A-IIb 45891234")
                    .active(true)
                    .build());

            driverRepository.save(Driver.builder()
                    .fullName("Miguel Transporte")
                    .documentNumber("71234567")
                    .phone("+51 981122334")
                    .vehicleInfo("Furgón Toyota HiAce (Placa: XYZ-789)")
                    .licenseNumber("A-IIa 71234567")
                    .active(true)
                    .build());

            driverRepository.save(Driver.builder()
                    .fullName("Pedro Logística")
                    .documentNumber("10987654")
                    .phone("+51 998877665")
                    .vehicleInfo("Camión Volvo 10T (Placa: VVV-456)")
                    .licenseNumber("A-IIIc 10987654")
                    .active(true)
                    .build());
        }
    }

    public List<DriverDTO> getAllDrivers() {
        if (driverRepository.count() == 0) {
            initDefaultDrivers();
        }
        return driverRepository.findAllByOrderByFullNameAsc().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public DriverDTO createDriver(CreateDriverRequest request) {
        Driver driver = Driver.builder()
                .fullName(request.getFullName())
                .documentNumber(request.getDocumentNumber())
                .phone(request.getPhone())
                .vehicleInfo(request.getVehicleInfo())
                .licenseNumber(request.getLicenseNumber())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        return mapToDTO(driverRepository.save(driver));
    }

    @Transactional
    public DriverDTO updateDriver(Long id, CreateDriverRequest request) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Repartidor no encontrado con ID: " + id));

        driver.setFullName(request.getFullName());
        driver.setDocumentNumber(request.getDocumentNumber());
        driver.setPhone(request.getPhone());
        driver.setVehicleInfo(request.getVehicleInfo());
        driver.setLicenseNumber(request.getLicenseNumber());
        if (request.getActive() != null) {
            driver.setActive(request.getActive());
        }

        return mapToDTO(driverRepository.save(driver));
    }

    @Transactional
    public DriverDTO toggleDriverStatus(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Repartidor no encontrado con ID: " + id));

        driver.setActive(!Boolean.TRUE.equals(driver.getActive()));
        return mapToDTO(driverRepository.save(driver));
    }

    private DriverDTO mapToDTO(Driver driver) {
        return DriverDTO.builder()
                .id(driver.getId())
                .fullName(driver.getFullName())
                .documentNumber(driver.getDocumentNumber())
                .phone(driver.getPhone())
                .vehicleInfo(driver.getVehicleInfo())
                .licenseNumber(driver.getLicenseNumber())
                .active(driver.getActive())
                .build();
    }
}
