package com.vivero.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vivero.dto.UbigeoDTOs.*;
import com.vivero.entity.Department;
import com.vivero.entity.District;
import com.vivero.entity.Province;
import com.vivero.exception.BadRequestException;
import com.vivero.exception.ResourceNotFoundException;
import com.vivero.repository.DepartmentRepository;
import com.vivero.repository.DistrictRepository;
import com.vivero.repository.ProvinceRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UbigeoService {

    private final DepartmentRepository departmentRepository;
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;

    @Transactional(readOnly = true)
    public List<DepartmentDTO> getAllDepartments() {
        return departmentRepository.findAllByOrderByNameAsc()
                .stream()
                .map(this::mapDepartmentToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProvinceDTO> getAllProvinces() {
        return provinceRepository.findAllByOrderByNameAsc()
                .stream()
                .map(this::mapProvinceToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProvinceDTO> getProvincesByDepartment(Long departmentId) {
        return provinceRepository.findByDepartmentIdOrderByNameAsc(departmentId)
                .stream()
                .map(this::mapProvinceToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DistrictDTO> getAllDistricts() {
        return districtRepository.findAllByOrderByNameAsc()
                .stream()
                .map(this::mapDistrictToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DistrictDTO> getDistrictsByProvince(Long provinceId) {
        return districtRepository.findByProvinceIdOrderByNameAsc(provinceId)
                .stream()
                .map(this::mapDistrictToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public DepartmentDTO createDepartment(CreateDepartmentRequest req) {
        if (departmentRepository.existsByCode(req.getCode())) {
            throw new BadRequestException("Ya existe un departamento con el código: " + req.getCode());
        }
        Department dept = Department.builder()
                .code(req.getCode())
                .name(req.getName())
                .active(true)
                .build();
        return mapDepartmentToDTO(departmentRepository.save(dept));
    }

    @Transactional
    public DepartmentDTO updateDepartment(Long id, UpdateDepartmentRequest req) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Departamento no encontrado con id: " + id));
        if (departmentRepository.existsByCodeAndIdNot(req.getCode(), id)) {
            throw new BadRequestException("Ya existe otro departamento con el código: " + req.getCode());
        }
        dept.setCode(req.getCode());
        dept.setName(req.getName());
        if (req.getActive() != null) {
            dept.setActive(req.getActive());
        }
        return mapDepartmentToDTO(departmentRepository.save(dept));
    }

    @Transactional
    public DepartmentDTO toggleDepartmentStatus(Long id) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Departamento no encontrado con id: " + id));
        dept.setActive(!Boolean.TRUE.equals(dept.getActive()));
        return mapDepartmentToDTO(departmentRepository.save(dept));
    }

    @Transactional
    public ProvinceDTO createProvince(CreateProvinceRequest req) {
        Department dept = departmentRepository.findById(req.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Departamento no encontrado con id: " + req.getDepartmentId()));

        Province province = Province.builder()
                .code(req.getCode())
                .name(req.getName())
                .department(dept)
                .active(true)
                .build();
        return mapProvinceToDTO(provinceRepository.save(province));
    }

    @Transactional
    public ProvinceDTO updateProvince(Long id, UpdateProvinceRequest req) {
        Province prov = provinceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provincia no encontrada con id: " + id));
        Department dept = departmentRepository.findById(req.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Departamento no encontrado con id: " + req.getDepartmentId()));

        prov.setCode(req.getCode());
        prov.setName(req.getName());
        prov.setDepartment(dept);
        if (req.getActive() != null) {
            prov.setActive(req.getActive());
        }
        return mapProvinceToDTO(provinceRepository.save(prov));
    }

    @Transactional
    public ProvinceDTO toggleProvinceStatus(Long id) {
        Province prov = provinceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provincia no encontrada con id: " + id));
        prov.setActive(!Boolean.TRUE.equals(prov.getActive()));
        return mapProvinceToDTO(provinceRepository.save(prov));
    }

    @Transactional
    public DistrictDTO createDistrict(CreateDistrictRequest req) {
        Province prov = provinceRepository.findById(req.getProvinceId())
                .orElseThrow(() -> new ResourceNotFoundException("Provincia no encontrada con id: " + req.getProvinceId()));

        District district = District.builder()
                .code(req.getCode())
                .name(req.getName())
                .province(prov)
                .active(true)
                .build();
        return mapDistrictToDTO(districtRepository.save(district));
    }

    @Transactional
    public DistrictDTO updateDistrict(Long id, UpdateDistrictRequest req) {
        District dist = districtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Distrito no encontrado con id: " + id));
        Province prov = provinceRepository.findById(req.getProvinceId())
                .orElseThrow(() -> new ResourceNotFoundException("Provincia no encontrada con id: " + req.getProvinceId()));

        dist.setCode(req.getCode());
        dist.setName(req.getName());
        dist.setProvince(prov);
        if (req.getActive() != null) {
            dist.setActive(req.getActive());
        }
        return mapDistrictToDTO(districtRepository.save(dist));
    }

    @Transactional
    public DistrictDTO toggleDistrictStatus(Long id) {
        District dist = districtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Distrito no encontrado con id: " + id));
        dist.setActive(!Boolean.TRUE.equals(dist.getActive()));
        return mapDistrictToDTO(districtRepository.save(dist));
    }

    @Transactional
    public void deleteDepartment(Long id) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Departamento no encontrado"));
        dept.setActive(false);
        departmentRepository.save(dept);
    }

    @Transactional
    public void deleteProvince(Long id) {
        Province prov = provinceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provincia no encontrada"));
        prov.setActive(false);
        provinceRepository.save(prov);
    }

    @Transactional
    public void deleteDistrict(Long id) {
        District dist = districtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Distrito no encontrado"));
        dist.setActive(false);
        districtRepository.save(dist);
    }

    @Transactional
    public BulkImportResponse importBulkUbigeo(BulkUbigeoImportRequest req) {
        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new BadRequestException("La lista de elementos a importar está vacía.");
        }

        int newDepts = 0;
        int newProvs = 0;
        int newDists = 0;

        for (BulkImportItem item : req.getItems()) {
            // 1. Process Department
            Department dept = null;
            if (item.getDepartmentCode() != null && !item.getDepartmentCode().trim().isEmpty()) {
                String dCode = item.getDepartmentCode().trim();
                String dName = (item.getDepartmentName() != null && !item.getDepartmentName().trim().isEmpty())
                        ? item.getDepartmentName().trim() : ("Departamento " + dCode);

                var existingDept = departmentRepository.findByCode(dCode);
                if (existingDept.isPresent()) {
                    dept = existingDept.get();
                    if (!dept.getName().equalsIgnoreCase(dName)) {
                        dept.setName(dName);
                        departmentRepository.save(dept);
                    }
                } else {
                    dept = Department.builder()
                            .code(dCode)
                            .name(dName)
                            .active(true)
                            .build();
                    dept = departmentRepository.save(dept);
                    newDepts++;
                }
            }

            // 2. Process Province
            Province prov = null;
            if (item.getProvinceCode() != null && !item.getProvinceCode().trim().isEmpty()) {
                String pCode = item.getProvinceCode().trim();
                String pName = (item.getProvinceName() != null && !item.getProvinceName().trim().isEmpty())
                        ? item.getProvinceName().trim() : ("Provincia " + pCode);

                var existingProv = provinceRepository.findByCode(pCode);
                if (existingProv.isPresent()) {
                    prov = existingProv.get();
                    boolean updated = false;
                    if (!prov.getName().equalsIgnoreCase(pName)) {
                        prov.setName(pName);
                        updated = true;
                    }
                    if (dept != null && (prov.getDepartment() == null || !prov.getDepartment().getId().equals(dept.getId()))) {
                        prov.setDepartment(dept);
                        updated = true;
                    }
                    if (updated) {
                        provinceRepository.save(prov);
                    }
                } else if (dept != null) {
                    prov = Province.builder()
                            .code(pCode)
                            .name(pName)
                            .department(dept)
                            .active(true)
                            .build();
                    prov = provinceRepository.save(prov);
                    newProvs++;
                }
            }

            // 3. Process District
            if (item.getDistrictCode() != null && !item.getDistrictCode().trim().isEmpty()) {
                String distCode = item.getDistrictCode().trim();
                String distName = (item.getDistrictName() != null && !item.getDistrictName().trim().isEmpty())
                        ? item.getDistrictName().trim() : ("Distrito " + distCode);

                var existingDist = districtRepository.findByCode(distCode);
                if (existingDist.isPresent()) {
                    District dist = existingDist.get();
                    boolean updated = false;
                    if (!dist.getName().equalsIgnoreCase(distName)) {
                        dist.setName(distName);
                        updated = true;
                    }
                    if (prov != null && (dist.getProvince() == null || !dist.getProvince().getId().equals(prov.getId()))) {
                        dist.setProvince(prov);
                        updated = true;
                    }
                    if (updated) {
                        districtRepository.save(dist);
                    }
                } else if (prov != null) {
                    District dist = District.builder()
                            .code(distCode)
                            .name(distName)
                            .province(prov)
                            .active(true)
                            .build();
                    districtRepository.save(dist);
                    newDists++;
                }
            }
        }

        return BulkImportResponse.builder()
                .importedDepartments(newDepts)
                .importedProvinces(newProvs)
                .importedDistricts(newDists)
                .totalRecords(req.getItems().size())
                .message("Importación masiva completada exitosamente en la base de datos.")
                .build();
    }

    private DepartmentDTO mapDepartmentToDTO(Department dept) {
        return DepartmentDTO.builder()
                .id(dept.getId())
                .code(dept.getCode())
                .name(dept.getName())
                .active(dept.getActive())
                .build();
    }

    private ProvinceDTO mapProvinceToDTO(Province prov) {
        return ProvinceDTO.builder()
                .id(prov.getId())
                .code(prov.getCode())
                .name(prov.getName())
                .departmentId(prov.getDepartment().getId())
                .departmentName(prov.getDepartment().getName())
                .active(prov.getActive())
                .build();
    }

    private DistrictDTO mapDistrictToDTO(District dist) {
        return DistrictDTO.builder()
                .id(dist.getId())
                .code(dist.getCode())
                .name(dist.getName())
                .provinceId(dist.getProvince().getId())
                .provinceName(dist.getProvince().getName())
                .departmentId(dist.getProvince().getDepartment().getId())
                .departmentName(dist.getProvince().getDepartment().getName())
                .active(dist.getActive())
                .build();
    }
}
