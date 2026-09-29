package com.lenzora.official.system.lenzora.catalogue.application;

import com.lenzora.official.system.lenzora.catalogue.domain.LensOption;
import com.lenzora.official.system.lenzora.catalogue.infrastructure.LensOptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Updates lens options in the catalogue. */
@Service
public class LensOptionService {

    private final LensOptionRepository lensOptionRepository;

    public LensOptionService(LensOptionRepository lensOptionRepository) {
        this.lensOptionRepository = lensOptionRepository;
    }

    /**
     * Copies the new values onto the stored lens option.
     * @param lensOptionId the lens option to update
     * @param updatedLensOption the values to copy onto it
     * @return the saved lens option
     * @throws RuntimeException when there is no lens option with that id
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