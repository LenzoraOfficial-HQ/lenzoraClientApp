package com.lenzora.official.system.lenzora.config;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 * The hospital details the administrator keeps in one place. They are held in
 * hospital.properties next to the application and are used to fill the hospital
 * boxes of every new order.
 */
@Component
public class HospitalSettings {

    /** The file the details are kept in. */
    private static final String FILE_NAME = "hospital.properties";

    private String name = "";
    private String location = "";
    private String address = "";
    private String phone = "";

    public HospitalSettings() {
        load();
    }

    /** Reads the saved details, when the file is already there. */
    private void load() {

        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return;
        }

        Properties saved = new Properties();

        try (FileReader reader = new FileReader(file)) {
            saved.load(reader);
        } catch (IOException pError) {
            System.out.println("Could not read " + FILE_NAME + ": " + pError.getMessage());
            return;
        }

        this.name = text(saved.getProperty("hospital.name"));
        this.location = text(saved.getProperty("hospital.location"));
        this.address = text(saved.getProperty("hospital.address"));
        this.phone = text(saved.getProperty("hospital.phone"));

        System.out.println("Hospital details read from " + file.getAbsolutePath() + ".");
    }

    /**
     * Saves the details the administrator typed, so the order form can show them.
     *
     * @param pName the hospital or clinic the orders go to
     * @param pLocation the branch the orders are sent from
     * @param pAddress the address shown with the hospital
     * @param pPhone the contact number shown with the hospital
     * @return an empty text when the details were saved, or the error message
     */
    public String save(String pName, String pLocation, String pAddress, String pPhone) {

        String wantedName = text(pName);

        if (wantedName.isEmpty()) {
            return "Hospital or clinic name is required.";
        }

        String wantedLocation = text(pLocation);

        if (wantedLocation.isEmpty()) {
            return "The branch the orders are sent from is required.";
        }

        String wantedAddress = text(pAddress);
        String wantedPhone = text(pPhone);

        Properties toKeep = new Properties();
        toKeep.setProperty("hospital.name", wantedName);
        toKeep.setProperty("hospital.location", wantedLocation);
        toKeep.setProperty("hospital.address", wantedAddress);
        toKeep.setProperty("hospital.phone", wantedPhone);

        File file = new File(FILE_NAME);

        try (FileWriter writer = new FileWriter(file)) {
            toKeep.store(writer, "Hospital details used to fill the order form");
        } catch (IOException pError) {
            System.out.println("Could not save " + FILE_NAME + ": " + pError.getMessage());
            return "The hospital details could not be saved.";
        }

        this.name = wantedName;
        this.location = wantedLocation;
        this.address = wantedAddress;
        this.phone = wantedPhone;

        System.out.println("Hospital details saved in " + file.getAbsolutePath() + ".");

        return "";
    }

    /** @return true when the administrator already saved a hospital */
    public boolean hasDetails() {
        return !name.isEmpty();
    }

    /**
     * Trims the typed text.
     *
     * @param pValue the text typed by the administrator
     * @return the trimmed text, or an empty text when there was nothing
     */
    private static String text(String pValue) {

        if (pValue == null) {
            return "";
        }

        return pValue.trim();
    }

    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
}
