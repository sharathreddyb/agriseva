package com.agriseva.rental.service;

import com.agriseva.equipment.model.Equipment;
import com.agriseva.equipment.model.EquipmentCategory;
import com.agriseva.equipment.model.EquipmentStatus;
import com.agriseva.equipment.repository.EquipmentRepository;
import com.agriseva.rental.dto.RentalRequest;
import com.agriseva.rental.dto.RentalResponse;
import com.agriseva.rental.dto.RentalStatusUpdateRequest;
import com.agriseva.rental.exception.InvalidRentalDatesException;
import com.agriseva.rental.exception.InvalidRentalStatusException;
import com.agriseva.rental.model.EquipmentRental;
import com.agriseva.rental.model.RentalStatus;
import com.agriseva.rental.repository.EquipmentRentalRepository;
import com.agriseva.user.model.User;
import com.agriseva.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipmentRentalServiceImplTest {

    @Mock
    private EquipmentRentalRepository rentalRepository;

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private UserRepository userRepository;

    private EquipmentRentalServiceImpl rentalService;

    @BeforeEach
    void setUp() {
        rentalService = new EquipmentRentalServiceImpl(
                rentalRepository,
                equipmentRepository,
                userRepository
        );
    }

    @Test
    void createShouldCalculateDaysAndAmount() {
        User farmer = createFarmer();
        User owner = createOwner();
        Equipment equipment = createEquipment(owner);

        RentalRequest request = new RentalRequest();
        request.setEquipmentId(10L);
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(3));
        request.setFarmerNote("Need for land preparation");

        when(userRepository.findByEmailIgnoreCase(
                "farmer@example.com"
        )).thenReturn(Optional.of(farmer));

        when(equipmentRepository.findByIdAndActiveTrue(10L))
                .thenReturn(Optional.of(equipment));

        when(rentalRepository.save(any()))
                .thenAnswer(invocation -> {
                    EquipmentRental rental =
                            invocation.getArgument(0);
                    rental.setId(100L);
                    return rental;
                });

        RentalResponse response = rentalService.create(
                "farmer@example.com",
                request
        );

        assertEquals(100L, response.getId());
        assertEquals(3, response.getTotalDays());
        assertEquals(
                new BigDecimal("7500.00"),
                response.getTotalAmount()
        );
        assertEquals(RentalStatus.PENDING, response.getStatus());

        verify(rentalRepository).save(any(EquipmentRental.class));
    }

    @Test
    void createShouldRejectInvalidDateRange() {
        User farmer = createFarmer();
        User owner = createOwner();
        Equipment equipment = createEquipment(owner);

        RentalRequest request = new RentalRequest();
        request.setEquipmentId(10L);
        request.setStartDate(LocalDate.now().plusDays(5));
        request.setEndDate(LocalDate.now().plusDays(2));

        when(userRepository.findByEmailIgnoreCase(
                "farmer@example.com"
        )).thenReturn(Optional.of(farmer));

        when(equipmentRepository.findByIdAndActiveTrue(10L))
                .thenReturn(Optional.of(equipment));

        assertThrows(
                InvalidRentalDatesException.class,
                () -> rentalService.create(
                        "farmer@example.com",
                        request
                )
        );

        verifyNoInteractions(rentalRepository);
    }

    @Test
    void createShouldRejectOwnerRentingOwnEquipment() {
        User owner = createOwner();
        Equipment equipment = createEquipment(owner);

        RentalRequest request = new RentalRequest();
        request.setEquipmentId(10L);
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(2));

        when(userRepository.findByEmailIgnoreCase(
                "owner@example.com"
        )).thenReturn(Optional.of(owner));

        when(equipmentRepository.findByIdAndActiveTrue(10L))
                .thenReturn(Optional.of(equipment));

        assertThrows(
                InvalidRentalStatusException.class,
                () -> rentalService.create(
                        "owner@example.com",
                        request
                )
        );

        verifyNoInteractions(rentalRepository);
    }

    @Test
    void ownerShouldApprovePendingRental() {
        User farmer = createFarmer();
        User owner = createOwner();
        Equipment equipment = createEquipment(owner);
        EquipmentRental rental =
                createRental(farmer, equipment);

        RentalStatusUpdateRequest request =
                new RentalStatusUpdateRequest();
        request.setStatus(RentalStatus.APPROVED);
        request.setOwnerResponseNote("Approved");

        when(userRepository.findByEmailIgnoreCase(
                "owner@example.com"
        )).thenReturn(Optional.of(owner));

        when(rentalRepository.findByIdAndEquipmentOwnerId(
                100L,
                2L
        )).thenReturn(Optional.of(rental));

        when(rentalRepository.save(rental))
                .thenReturn(rental);

        RentalResponse response = rentalService.updateStatus(
                "owner@example.com",
                100L,
                request
        );

        assertEquals(RentalStatus.APPROVED, response.getStatus());
        assertEquals(
                "Approved",
                response.getOwnerResponseNote()
        );
    }

    @Test
    void farmerShouldCancelPendingRental() {
        User farmer = createFarmer();
        User owner = createOwner();
        Equipment equipment = createEquipment(owner);
        EquipmentRental rental =
                createRental(farmer, equipment);

        when(userRepository.findByEmailIgnoreCase(
                "farmer@example.com"
        )).thenReturn(Optional.of(farmer));

        when(rentalRepository.findByIdAndFarmerId(
                100L,
                1L
        )).thenReturn(Optional.of(rental));

        when(rentalRepository.save(rental))
                .thenReturn(rental);

        RentalResponse response = rentalService.cancel(
                "farmer@example.com",
                100L
        );

        assertEquals(RentalStatus.CANCELLED, response.getStatus());
    }

    @Test
    void getMyRentalsShouldReturnFarmerRequests() {
        User farmer = createFarmer();
        User owner = createOwner();
        Equipment equipment = createEquipment(owner);
        EquipmentRental rental =
                createRental(farmer, equipment);

        when(userRepository.findByEmailIgnoreCase(
                "farmer@example.com"
        )).thenReturn(Optional.of(farmer));

        when(rentalRepository
                .findByFarmerIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(rental));

        List<RentalResponse> responses =
                rentalService.getMyRentals(
                        "farmer@example.com"
                );

        assertEquals(1, responses.size());
        assertEquals(100L, responses.get(0).getId());
    }

    private User createFarmer() {
        User farmer = new User();
        farmer.setId(1L);
        farmer.setFullName("Test Farmer");
        farmer.setEmail("farmer@example.com");
        return farmer;
    }

    private User createOwner() {
        User owner = new User();
        owner.setId(2L);
        owner.setFullName("Equipment Owner");
        owner.setEmail("owner@example.com");
        return owner;
    }

    private Equipment createEquipment(User owner) {
        Equipment equipment = new Equipment();
        equipment.setId(10L);
        equipment.setOwner(owner);
        equipment.setName("Mahindra Tractor");
        equipment.setCategory(EquipmentCategory.TRACTOR);
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

    private EquipmentRental createRental(
            User farmer,
            Equipment equipment
    ) {
        EquipmentRental rental = new EquipmentRental();
        rental.setId(100L);
        rental.setFarmer(farmer);
        rental.setEquipment(equipment);
        rental.setStartDate(LocalDate.now().plusDays(1));
        rental.setEndDate(LocalDate.now().plusDays(3));
        rental.setTotalDays(3);
        rental.setRentalPricePerDay(
                new BigDecimal("2500.00")
        );
        rental.setTotalAmount(
                new BigDecimal("7500.00")
        );
        rental.setStatus(RentalStatus.PENDING);
        rental.setFarmerNote("Need for land preparation");
        return rental;
    }
}