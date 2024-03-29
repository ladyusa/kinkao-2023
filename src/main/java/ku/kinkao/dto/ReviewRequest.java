package ku.kinkao.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ReviewRequest {
    @Min(1)
    @Max(5)
    private int rating;

    @NotBlank
    private String reviewText;

    @NotNull
    private UUID restaurantId;
}
