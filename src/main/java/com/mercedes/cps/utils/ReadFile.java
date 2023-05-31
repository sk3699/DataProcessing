package com.processing.cps.utils;

import com.processing.cps.exceptions.InvalidParametersException;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reads the data of Channels and Params from text file provided with Absolute Path.
 */
@NoArgsConstructor
public class ReadFile {
    Logger LOGGER = Logger.getLogger(ReadFile.class.getName());
    private static final String NO_FILE_FOUND_MSG = "\nNo file found at: %s. Exception is: ";
    private static final String INVALID_DATA_IN_CHANNELS_MSG = "\nInvalid Data:%s in Channels File: ";
    private static final String INVALID_DATA_MSG = "\nSorry, Invalid Data!!! \nException is: ";
    private static final String INVALID_DATA_IN_PARAMS_MSG = "\nInvalid Data in Parameters File provided.";

    /**
     * Reads Channels data from text file provided in Absolute Path.
     * @param filePath
     * @return
     * @throws IOException
     */
    public Map<String, Double []> readChannelsData(String filePath) throws IOException {
        String line;
        Map<String, Double []> channelsData = new HashMap<>();
        String rawData = "";
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            while ((line = bufferedReader.readLine()) != null) {
                List<Double> data = new ArrayList<>();
                String[] splitRawData = line.split(",");
                for (int i = 0; i < splitRawData.length; i++) {
                    rawData = splitRawData[i];
                    //Ignoring the first element in text file as they are Channel names.
                    if(i > 0) {
                        double parsedDouble = Double.parseDouble(rawData);
                        data.add(parsedDouble);
                    }
                }
                channelsData.put(splitRawData[0], data.toArray(new Double []{}));
            }
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, String.format(NO_FILE_FOUND_MSG, filePath), e);
            System.out.printf(NO_FILE_FOUND_MSG, filePath);
            throw e;
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, String.format(INVALID_DATA_IN_CHANNELS_MSG, rawData), e);
            System.out.printf(INVALID_DATA_IN_CHANNELS_MSG, rawData);
            throw e;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, INVALID_DATA_MSG, e);
            throw e;
        }
        return channelsData;
    }

    /**
     * Reads parameters Data from text file provided in Absolute path.
     * @param filePath
     * @return
     * @throws IOException
     */
    public Map<String, Double> readParamsData(String filePath) throws IOException {
        String line;
        Map<String, Double> paramsData = new HashMap<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            while ((line = bufferedReader.readLine()) != null) {
                String[] splitRawData = line.split(",");
                //Error is thrown if the length of line is > 2. As the param should contain only name & value.
                if(splitRawData.length > 2) {
                    throw new InvalidParametersException(Arrays.toString(splitRawData));
                }
                double parsedDouble = Double.parseDouble(splitRawData[1]);
                paramsData.put(splitRawData[0], parsedDouble);
            }
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, String.format(NO_FILE_FOUND_MSG, filePath), e);
            System.out.printf(NO_FILE_FOUND_MSG, filePath);
            throw e;
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, INVALID_DATA_IN_PARAMS_MSG, e);
            System.out.println(INVALID_DATA_IN_PARAMS_MSG);
            throw e;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, INVALID_DATA_MSG, e);
            throw e;
        }
        return paramsData;
    }
}
