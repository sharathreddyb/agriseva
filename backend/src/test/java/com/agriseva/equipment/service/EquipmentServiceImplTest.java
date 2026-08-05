package com.agriseva.equipment.service;

import com.agriseva.equipment.dto.EquipmentRequest;
import com.agriseva.equipment.dto.EquipmentResponse;
import com.agriseva.equipment.exception.EquipmentAccessDeniedException;
import com.agriseva.equipment.exception.EquipmentOwnerRoleRequiredException;
import com.agriseva.equipment.model.Equipment;
import com.agriseva.equipment.model.EquipmentCategory;
import com.agriseva.equipment.model.EquipmentStatus;
import com.agriseva.equipment.repository.EquipmentRepository;
import com.agriseva.user.model.Role;
import com.agriseva.user.model.RoleType;
import com.agriseva.user.model.User;
import com.agriseva.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipmentServiceImplTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private UserRepository userRepository;

    private EquipmentServiceImpl equipmentService;

    @BeforeEach
    void setUp() {
        equipmentService = new EquipmentServiceImpl(
                equipmentRepository,
                userRepository
        );
    }

    @Test
    void createShouldSaveEquipmentForOwner() {
        User owner = createOwner();
        EquipmentRequest request = createRequest();

        when(userRepository.findByEmailIgnoreCase(
                "sharath.test@example.com"
        )).thenReturn(Optional.of(owner));

        when(equipmentRepository.save(any()))
                .thenAnswer(invocation -> {
                    Equipment equipment = invocation.getArgument(0);
                    equipment.setId(10L);
                    return equipment;
                });

        EquipmentResponse response = equipmentService.create(
                "sharath.test@example.com",
                request
        );

        assertEquals(10L, response.getId());
        assertEquals(1L, response.getOwnerId());
        assertEquals("Mahindra Tractor", response.getName());
        assertEquals(EquipmentCategory.TRACTOR, response.getCategory());
        assertEquals(
                new BigDecimal("2500.00"),
                response.getRentalPricePerDay()
        );
        assertEquals(EquipmentStatus.AVAILABLE, response.getStatus());

        verify(equipmentRepository).save(any(Equipment.class));
    }

    @Test
    void createShouldRejectUserWithoutEquipmentOwnerRole() {
        User user = new User();
        user.setId(1L);
        user.setEmail("farmer@example.com");
        user.addRole(new Role(RoleType.FARMER));

        when(userRepository.findByEmailIgnoreCase(
                "farmer@example.com"
        )).thenReturn(Optional.of(user));

        assertThrows(
                EquipmentOwnerRoleRequiredException.class,
                () -> equipmentService.create(
                        "farmer@example.com",
                        createRequest()
                )
        );

        verifyNoInteractions(equipmentRepository);
    }

    @Test
    void updateShouldRejectAnotherOwner() {
        User authenticatedOwner = createOwner();

        User actualOwner = new User();
        actualOwner.setId(2L);
        actualOwner.setFullName("Another Owner");

        Equipment equipment = createEquipment(actualOwner);
        equipment.setId(10L);

        when(userRepository.findByEmailIgnoreCase(
                "sharath.test@example.com"
        )).thenReturn(Optional.of(authenticatedOwner));

        when(equipmentRepository.findById(10L))
                .thenReturn(Optional.of(equipment));

        assertThrows(
                EquipmentAccessDeniedException.class,
                () -> equipmentService.update(
                        "sharath.test@example.com",
                        10L,
                        createRequest()
                )
        );

        verify(equipmentRepository, never()).save(any());
    }

    @Test
    void deactivateShouldMakeEquipmentInactive() {
        User owner = createOwner();

        Equipment equipment = createEquipment(owner);
        equipment.setId(10L);

        when(userRepository.findByEmailIgnoreCase(
                "sharath.test@example.com"
        )).thenReturn(Optional.of(owner));

        when(equipmentRepository.findById(10L))
                .thenReturn(Optional.of(equipment));

        equipmentService.deactivate(
                "sharath.test@example.com",
                10L
        );

        assertFalse(equipment.isActive());
        assertEquals(
                EquipmentStatus.UNAVAILABLE,
                equipment.getStatus()
        );

        verify(equipmentRepository).save(equipment);
    }

    @Test
    void searchShouldReturnMappedEquipment() {
        User owner = createOwner();
    
        Equipment equipment = createEquipment(owner);
        equipment.setId(10L);
    
        when(equipmentRepository.findAll(
                any(org.springframework.data.jpa.domain.Specification.class)
        )).thenReturn(List.of(equipment));
    
        List<EquipmentResponse> responses =
                equipmentService.search(
                        EquipmentCategory.TRACTOR,
                        "Siddipet",
                        null,
                        EquipmentStatus.AVAILABLE,
                        "Mahindra"
                );
    
        assertEquals(1, responses.size());
        assertEquals(
                "Mahindra Tractor",
                responses.get(0).getName()
        );
    }

    private User createOwner() {
        User owner = new User();
        owner.setId(1L);
        owner.setFullName("Sharath Reddy");
        owner.setEmail("sharath.test@example.com");
        owner.addRole(new Role(RoleType.FARMER));
        owner.addRole(new Role(RoleType.EQUIPMENT_OWNER));
        return owner;
    }

    private Equipment createEquipment(User owner) {
        Equipment equipment = new Equipment();
        equipment.setOwner(owner);
        equipment.setName("Mahindra Tractor");
        equipment.setCategory(EquipmentCategory.TRACTOR);
        equipment.setDescription("55 HP tractor");
        equipment.setRentalPricePerDay(
                new BigDecimal("2500.00")
        );
        equipment.setSecurityDeposit(
                new BigDecimal("5000.00")
        );
        equipment.setStatus(EquipmentStatus.AVAILABLE);
        equipment.setServiceAddress("Main Road");
        equipment.setVillage("Chinnagundavelly");
        equipment.setDistrict("Siddipet");
        equipment.setState("Telangana");
        equipment.setPostalCode("502103");
        equipment.setActive(true);
        return equipment;
    }

    private EquipmentRequest createRequest() {
        EquipmentRequest request = new EquipmentRequest();
        request.setName("Mahindra Tractor");
        request.setCategory(EquipmentCategory.TRACTOR);
        request.setDescription("55 HP tractor");
        request.setRentalPricePerDay(
                new BigDecimal("2500.00")
        );
        request.setSecurityDeposit(
                new BigDecimal("5000.00")
        );
        request.setStatus(EquipmentStatus.AVAILABLE);
        request.setServiceAddress("Main Road");
        request.setVillage("Chinnagundavelly");
        request.setDistrict("Siddipet");
        request.setState("Telangana");
        request.setPostalCode("502103");
        request.setImageUrl("https://example.com/tractor.jpg");
        return request;
    }
}