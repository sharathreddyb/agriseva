package com.agriseva.rental.service;

import com.agriseva.rental.dto.RentalRequest;
import com.agriseva.rental.dto.RentalResponse;
import com.agriseva.rental.dto.RentalStatusUpdateRequest;

import java.util.List;

public interface EquipmentRentalService {

    RentalResponse create(
            String authenticatedEmail,
            RentalRequest request
    );

    List<RentalResponse> getMyRentals(
            String authenticatedEmail
    );

    List<RentalResponse> getOwnerRequests(
            String authenticatedEmail
    );

    RentalResponse updateStatus(
            String authenticatedEmail,
            Long rentalId,
            RentalStatusUpdateRequest request
    );

    RentalResponse cancel(
            String authenticatedEmail,
            Long rentalId
    );
}