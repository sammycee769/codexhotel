package com.sammy.codexhotel.dtos.requests;

import com.sammy.codexhotel.data.models.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Body of the ADMIN-only role change. Kept separate from {@link UpdateUserRequest} on purpose:
 * that request is one a guest may send against their own profile, so a role field there would be
 * a self-promotion path. This one is only reachable through PATCH /api/users/role/{userId}.
 */
@Data
public class UpdateRoleRequest {

    @NotNull(message = "Role is required")
    private UserRole role;
}
