package com.processing.cps.service;

import com.processing.cps.enums.CpsEnums;
import com.processing.cps.utils.ReadFile;
import lombok.NoArgsConstructor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Service class to hold all the executions.
 */
@NoArgsConstructor
public class ChannelProcessingService {
    Logger LOGGER = Logger.getLogger(ChannelProcessingService.class.getName());

    protected final ReadFile fileReader = new ReadFile();
    private static final String APPLICATION_STARTED_MSG = "Channel Processing System started!!!";
    private static final String CHANNELS_FILE_INPUT_MSG = "\nPlease provide Absolute path for Channels file: ";
    private static final String PARAMETERS_FILE_INPUT_MSG = "Please provide Absolute path for Parameters file: ";
    private  static final String PROCESSING_MSG = "Processing the Channel Data provided. Please wait...";
    private static final String OPTIONS_FOR_USER = "Please select from one of options:" +
            "\na) Y=mX+c" +
            "\nb) Mean Value" +
            "\nc) A=1/X" +
            "\nd) C=X+b" +
            "\ne) EXIT" +
            "\nTo check the result of functions individually please select from above: ";
    private static final String LINEAR_FUN_MSG = "Augmented Data for Linear Function Y=mX+c: ";
    private static final String MEAN_FUN_MSG = "Mean value for Channels Processed: ";
    private static final String INVERSE_FUN_MSG = "Augmented Data for Inverse Function A=1/X: ";
    private static final String SUM_CHANNEL_WITH_MEAN_MSG = "Augmented Data for Function C=X+b: ";
    private static final String INVALID_OPTION_MSG = "Invalid Option: %s.%nPlease select a valid option!!!";

    /**
     * Triggered from main method to read and process data from txt files.
     * @throws IOException
     */
    public void startExecutionsOfAllFunctions() throws IOException {
        LOGGER.entering(ChannelProcessingService.class.getName(), "startExecutionsOfAllFunctions");
        Scanner scanner = new Scanner(System.in);
        String channelFilePath = "";
        String paramFilePath = "";
        try {
            System.out.println(APPLICATION_STARTED_MSG + CHANNELS_FILE_INPUT_MSG);
            channelFilePath = scanner.next();
            System.out.println(PARAMETERS_FILE_INPUT_MSG);
            paramFilePath = scanner.next();
            //Read from txt files and store the data.
            Map<String, Double[]> channelData = fileReader.readChannelsData(channelFilePath);
            Map<String, Double> paramsData = fileReader.readParamsData(paramFilePath);
            System.out.println(PROCESSING_MSG);
            //All the methods are called from here and linear, inverse channel data is passed to calculate mean value.
            Map<String, Double[]> linearChnlData = calculateLinear(channelData, paramsData);
            Map<String, Double[]> inverseChnlData = calculateInverse(channelData);
            Map<String, Double> meanValues = calculateMean(linearChnlData, inverseChnlData);
            Map<String, Double[]> summedWithMean = sumWithMean(channelData, meanValues);
            String option;
            do {
                //Displays options to view the result of each function.
                System.out.println(OPTIONS_FOR_USER);
                option = scanner.next();
                switch (option) {
                    case "a":
                        System.out.println(LINEAR_FUN_MSG);
                        for(Map.Entry<String, Double[]> entry : linearChnlData.entrySet()) {
                            System.out.println(entry.getKey() + ": " + Arrays.toString(entry.getValue()));
                        }
                        break;
                    case "b":
                        System.out.println(MEAN_FUN_MSG + meanValues);
                        break;
                    case "c":
                        System.out.println(INVERSE_FUN_MSG);
                        for(Map.Entry<String, Double[]> entry : inverseChnlData.entrySet()) {
                            System.out.println(entry.getKey() + ": " + Arrays.toString(entry.getValue()));
                        }
                        break;
                    case "d":
                        System.out.println(SUM_CHANNEL_WITH_MEAN_MSG);
                        for(Map.Entry<String, Double[]> entry : summedWithMean.entrySet()) {
                            System.out.println(entry.getKey() + ": " + Arrays.toString(entry.getValue()));
                        }
                        break;
                    case "e":
                        break;
                    default:
                        System.out.printf(INVALID_OPTION_MSG, option);
                }
            } while (!option.equals("e"));
            LOGGER.exiting(ChannelProcessingService.class.getName(), "startExecutionsOfAllFunctions");
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException | NumberFormatException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Calculates the Linear data with params provided and produces a new augmented Channel.
     * @param channelData
     * @param paramsData
     * @return
     */
    public Map<String, Double[]> calculateLinear(Map<String, Double[]> channelData, Map<String, Double> paramsData) {
        LOGGER.entering(ChannelProcessingService.class.getName(), "calculateLinear");
        Map<String, Double[]> linearChannels = new HashMap<>();
        double m = paramsData.get(CpsEnums.M.getValue());
        double c = paramsData.get(CpsEnums.C.getValue());
        for(Map.Entry<String, Double []> entry : channelData.entrySet()) {
            String key = entry.getKey();
            Double[] data = entry.getValue();
            Double [] augmentedData = new Double [data.length];
            for (int i = 0; i < data.length; i++) {
                //calculating the linear value for the channels
                augmentedData[i] = m * data[i] + c;
            }
            linearChannels.put(key, augmentedData);
        }
        LOGGER.exiting(ChannelProcessingService.class.getName(), "calculateLinear");
        return linearChannels;
    }

    /**
     * Calculates means value for linear and inverse channels passed.
     * @param linearChannels
     * @param inverseChannels
     * @return
     */
    public Map<String, Double> calculateMean(Map<String, Double[]> linearChannels, Map<String, Double[]> inverseChannels) {
        LOGGER.entering(ChannelProcessingService.class.getName(), "calculateMean");
        Map<String, Double> meanData = new HashMap<>();
        for(Map.Entry<String, Double []> entry : linearChannels.entrySet()) {
            double sum = 0;
            String key = entry.getKey();
            Double[] linearData = entry.getValue();
            Double[] inverseData = inverseChannels.get(key);
            for (int i = 0; i < linearData.length; i++) {
                //sums up the linear and inverse data of channel
                double addedData = linearData[i] + inverseData[i];
                sum += addedData;
            }
            //mean value is calculated here
            double mean = sum / linearData.length;
            meanData.put(key, mean);
        }
        LOGGER.exiting(ChannelProcessingService.class.getName(), "calculateMean");
        return meanData;
    }

    /**
     * Calculate the inverse of channel and produces a new channel.
     * @param channelData
     * @return
     */
    public Map<String, Double[]> calculateInverse(Map<String, Double[]> channelData) {
        LOGGER.entering(ChannelProcessingService.class.getName(), "calculateInverse");
        Map<String, Double[]> inverseChannels = new HashMap<>();
        for(Map.Entry<String, Double[]> entry : channelData.entrySet()) {
            String key = entry.getKey();
            Double[] data = entry.getValue();
            Double[] augmentedData = new Double [data.length];
            for (int i = 0; i < data.length; i++) {
                //inverse the channels
                augmentedData[i] = 1 / data[i];
            }
            inverseChannels.put(key, augmentedData);
        }
        LOGGER.exiting(ChannelProcessingService.class.getName(), "calculateInverse");
        return inverseChannels;
    }

    /**
     * Augment the Channel data by adding the mean value generate in calculateMean method.
     * @param channelData
     * @param meanValues
     * @return
     */
    public Map<String, Double[]> sumWithMean(Map<String, Double[]> channelData, Map<String, Double> meanValues) {
        LOGGER.entering(ChannelProcessingService.class.getName(), "sumWithMean");
        Map<String, Double[]> processedData = new HashMap<>();
        for(Map.Entry<String, Double[]> entry : channelData.entrySet()) {
            String key = entry.getKey();
            Double[] data = entry.getValue();
            double mean = meanValues.get(key);
            Double[] augmentedData = new Double [data.length];
            for (int i = 0; i < data.length; i++) {
                //Augmenting the channel data by adding Mean value of processed channels
                augmentedData[i] = data[i] + mean;
            }
            processedData.put(key, augmentedData);
        }
        LOGGER.exiting(ChannelProcessingService.class.getName(), "sumWithMean");
        return processedData;
    }
}
