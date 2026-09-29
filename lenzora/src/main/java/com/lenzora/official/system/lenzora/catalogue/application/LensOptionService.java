package com.lenzora.official.system.lenzora.catalogue.application;

import com.lenzora.official.system.lenzora.catalogue.domain.LensOption;
import com.lenzora.official.system.lenzora.catalogue.infrastructure.LensOptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for handling business operations related to LensOption entities.
 *
 * <p>This class manages the update operation for lens options
 * and communicates with the LensOptionRepository to access the database.</p>
 */
@Service
public class LensOptionService {

    private final LensOptionRepository lensOptionRepository;

    /**
     * Creates a LensOptionService with the required repository.
     *
     * @param lensOptionRepository repository used to perform database operations
     */
    public LensOptionService(LensOptionRepository lensOptionRepository) {
        this.lensOptionRepository = lensOptionRepository;
    }

    /**
     * Updates an existing lens option with the provided information.
     *
     * @param lensOptionId ID of the lens option to be updated
     * @param updatedLensOption lens option object containing the new information
     * @return the updated LensOption entity
     * @throws RuntimeException if the lens option does not exist
     */
    @Transactional
    public LensOption updateLensOption(
            Long lensOptionId,
            LensOption updatedLensOption) {

        LensOption existingLensOption = lensOptionRepository.findById(lensOptionId)
                .orElseThrow(() -> new RuntimeException("Lens option not found"));

        existingLensOption.setLensCode(updatedLensOption.getLensCode());
        existingLensOption.setLabel(updatedLensOption.getLabel());
        existingLensOption.setExtraCharge(updatedLensOption.getExtraCharge());
        existingLensOption.setActive(updatedLensOption.getActive());

        return lensOptionRepository.save(existingLensOption);
    }
}