package com.lenzora.official.system.lenzora.catalogue.application;

import com.lenzora.official.system.lenzora.catalogue.domain.Frame;
import com.lenzora.official.system.lenzora.catalogue.infrastructure.FrameRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Updates frames in the catalogue. */
@Service
public class FrameService {

    private final FrameRepository frameRepository;

    public FrameService(FrameRepository frameRepository) {
        this.frameRepository = frameRepository;
    }

    /**
     * Copies the new values onto the stored frame.
     * @param frameId the frame to update
     * @param updatedFrame the values to copy onto it
     * @return the saved frame
     * @throws RuntimeException when there is no frame with that id
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