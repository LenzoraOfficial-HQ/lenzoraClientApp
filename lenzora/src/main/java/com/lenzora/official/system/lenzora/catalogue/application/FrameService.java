package com.lenzora.official.system.lenzora.catalogue.application;

import com.lenzora.official.system.lenzora.catalogue.domain.Frame;
import com.lenzora.official.system.lenzora.catalogue.infrastructure.FrameRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for handling business operations related to Frame entities.
 *
 * <p>This class manages the update operation for frames
 * and communicates with the FrameRepository to access the database.</p>
 */
@Service
public class FrameService {

    private final FrameRepository frameRepository;

    /**
     * Creates a FrameService with the required repository.
     *
     * @param frameRepository repository used to perform database operations
     */
    public FrameService(FrameRepository frameRepository) {
        this.frameRepository = frameRepository;
    }

    /**
     * Updates an existing frame with the provided information.
     *
     * @param frameId ID of the frame to be updated
     * @param updatedFrame frame object containing the new information
     * @return the updated Frame entity
     * @throws RuntimeException if the frame does not exist
     */
    @Transactional
    public Frame updateFrame(Long frameId, Frame updatedFrame) {

        Frame existingFrame = frameRepository.findById(frameId)
                .orElseThrow(() -> new RuntimeException("Frame not found"));

        existingFrame.setSupplier(updatedFrame.getSupplier());
        existingFrame.setBrand(updatedFrame.getBrand());
        existingFrame.setModel(updatedFrame.getModel());
        existingFrame.setColour(updatedFrame.getColour());
        existingFrame.setSize(updatedFrame.getSize());
        existingFrame.setMaterial(updatedFrame.getMaterial());
        existingFrame.setPrice(updatedFrame.getPrice());
        existingFrame.setImageUrl(updatedFrame.getImageUrl());
        existingFrame.setAvailable(updatedFrame.getAvailable());
        existingFrame.setActive(updatedFrame.getActive());

        return frameRepository.save(existingFrame);
    }
}