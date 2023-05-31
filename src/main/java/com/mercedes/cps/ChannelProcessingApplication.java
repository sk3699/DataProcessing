package com.processing.cps;

import com.processing.cps.service.ChannelProcessingService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Start point of Channel Processing App.
 * Main Application class.
 */
public class ChannelProcessingApplication {
    static Logger LOGGER = Logger.getLogger(ChannelProcessingApplication.class.getName());

    private static final String CONFIRMATION_MSG = "\nDo you wish to try again with more Channels?(y/n): ";
    private static final String CLOSING_MSG = "\nIf you wish to Process the Channels again, please run the startup script.\nThank you!\n";
    private static final String INVALID_MSG = "\nInvalid Data found!!! \nException is: ";
    private static final String ABORT_MSG = "Aborted due to: ";
    private static final String NO_FILE_FOUND_MSG = "No file found. %nException is: ";
    private static final String PROVIDE_VALID_PATH_MSG = "\nPlease provide a valid file path.";
    /**
     * Main method.
     * @param args
     */
    public static void main(String[] args) {
        LOGGER.entering(ChannelProcessingApplication.class.getName(), "main");
        
        try(Scanner scanner = new Scanner(System.in)) {
            LogManager.getLogManager().readConfiguration(ChannelProcessingApplication.class.getClassLoader().getResourceAsStream("logging.properties"));
            String yesOrNo = "";
            do {
                ChannelProcessingService service = new ChannelProcessingService();
                //By calling startExecutionsOfAllFunctions method, channels & params files will be processed
                //and all the functions will be executed.
                service.startExecutionsOfAllFunctions();
                System.out.println(CONFIRMATION_MSG);
                yesOrNo = scanner.next();
            } while (yesOrNo.equalsIgnoreCase("y"));
        } catch (FileNotFoundException e) {
            System.out.println(NO_FILE_FOUND_MSG + e.getMessage() + PROVIDE_VALID_PATH_MSG);
        } catch (IOException | NumberFormatException e) {
            System.out.println(INVALID_MSG + e.getMessage());
        } catch (Exception e) {
            System.out.println(ABORT_MSG + e.getMessage());
        } finally {
            System.out.println(CLOSING_MSG);
            scanner.close();
        }
        LOGGER.exiting(ChannelProcessingApplication.class.getName(), "main");
    }
}