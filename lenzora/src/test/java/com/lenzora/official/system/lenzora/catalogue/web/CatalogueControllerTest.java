package com.lenzora.official.system.lenzora.catalogue.web;

import com.lenzora.official.system.lenzora.catalogue.domain.Frame;
import com.lenzora.official.system.lenzora.catalogue.infrastructure.FrameRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.ui.ExtendedModelMap;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CatalogueControllerTest {

    @Test
    void clientFrameImagesContainOnlyOrderableFrames() {
        FrameRepository frameRepository = mock(FrameRepository.class);
        List<Frame> availableFrames = List.of(new Frame());
        when(frameRepository.findByAvailableTrueAndActiveTrue()).thenReturn(availableFrames);
        CatalogueController controller = new CatalogueController(
                null, null, null, frameRepository, null, null);
        var model = new ExtendedModelMap();
        var client = UsernamePasswordAuthenticationToken.authenticated(
                "client", "n/a", AuthorityUtils.createAuthorityList("ROLE_ORTHOPTIST"));

        assertEquals("catalogue/frame-images", controller.showFrameImages(model, client));
        assertEquals(availableFrames, model.get("frames"));
        assertEquals("frames", model.get("activeNav"));
        verify(frameRepository).findByAvailableTrueAndActiveTrue();
    }

    @Test
    void adminFrameImagesContainAllFrames() {
        FrameRepository frameRepository = mock(FrameRepository.class);
        List<Frame> allFrames = List.of(new Frame(), new Frame());
        when(frameRepository.findAll()).thenReturn(allFrames);
        CatalogueController controller = new CatalogueController(
                null, null, null, frameRepository, null, null);
        var model = new ExtendedModelMap();
        var admin = UsernamePasswordAuthenticationToken.authenticated(
                "admin", "n/a", AuthorityUtils.createAuthorityList("ROLE_ADMIN"));

        assertEquals("catalogue/frame-images", controller.showFrameImages(model, admin));
        assertEquals(allFrames, model.get("frames"));
        verify(frameRepository).findAll();
    }
}
