package com.processing.cps.service;

import com.processing.cps.utils.ReadFile;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.PrintStream;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ChannelProcessingServiceTest {

    @InjectMocks
    ChannelProcessingService service;
    @Mock
    ReadFile readFile;

    @Before
    public void setUp() throws Exception {
        Map<String, Double[]> channelData = new HashMap<>();
        Map<String, Double> meanValues = new HashMap<>();
        setupChannelAndMeanData(channelData, meanValues);
        ChannelProcessingService service1 = Mockito.mock(ChannelProcessingService.class);
        //service = service1;
    }

    private void setUpChannelAndParamsData(Map<String, Double[]> channelData, Map<String, Double> paramsData) {
        double channelD = 1.3769;
        for(char a = 'X'; a <= 'Z'; a++) {
            Double[] measuredData = new Double[5];
            for (int i = 0; i < 5; i++) {
                channelD *= i + 1;
                measuredData[i] = channelD;
            }
            channelD *= 3;
            channelData.put(String.valueOf(a), measuredData);
        }
        paramsData.put("m", 2.1);
        paramsData.put("c", 3.4);
    }

    private void setUpChannelsData(Map<String, Double[]> channelData, Map<String, Double[]> inverseChannelData) {
        double channelD = 1.3769;
        for(char a = 'X'; a <= 'Z'; a++) {
            Double[] measuredData = new Double[5];
            for (int i = 0; i < 5; i++) {
                channelD *= i + 1;
                measuredData[i] = channelD;
            }
            channelD *= 3;
            channelData.put(String.valueOf(a), measuredData);
        }
        inverseChannelData.putAll(service.calculateInverse(channelData));
    }

    private void setupChannelAndMeanData(Map<String, Double[]> channelData, Map<String, Double> meanValues) {
        double channelD = 1.3769;
        for(char a = 'X'; a <= 'Z'; a++) {
            Double[] measuredData = new Double[5];
            for (int i = 0; i < 5; i++) {
                channelD *= i + 1;
                measuredData[i] = channelD;
            }
            channelD *= 3;
            channelData.put(String.valueOf(a), measuredData);
        }
        meanValues.put("X", 1.2234354);
        meanValues.put("Y", 2.3542342);
        meanValues.put("Z", 3.2341234);
    }

    @Test
    public void calculateLinear() {
        Map<String, Double[]> channelData = new HashMap<>();
        Map<String, Double> paramsData = new HashMap<>();
        setUpChannelAndParamsData(channelData, paramsData);
        Map<String, Double[]> returnedData = service.calculateLinear(channelData, paramsData);
        assertEquals("The expected size was not returned.", 3, returnedData.keySet().size());
    }

    @Test
    public void calculateMean() {
        Map<String, Double[]> linearChannelData = new HashMap<>();
        Map<String, Double[]> inverseChannelData = new HashMap<>();
        setUpChannelsData(linearChannelData, inverseChannelData);
        Map<String, Double> returnedData = service.calculateMean(linearChannelData, inverseChannelData);
        assertEquals("The expected size was not returned.", 3, returnedData.keySet().size());
        assertEquals("Keys are not matching.", inverseChannelData.keySet(), returnedData.keySet());
    }

    @Test
    public void calculateInverse() {
        Map<String, Double[]> channelData = new HashMap<>();
        Map<String, Double[]> inverseChannelData = new HashMap<>();
        setUpChannelsData(channelData, inverseChannelData);
        Map<String, Double[]> returnedInverseChannels = service.calculateInverse(channelData);
        assertEquals("The expected size was not returned.", 3, returnedInverseChannels.keySet().size());
        assertEquals("Keys are not matching.", channelData.keySet(), returnedInverseChannels.keySet());
        assertArrayEquals("Data Not matching.", inverseChannelData.values().toArray(), returnedInverseChannels.values().toArray());
    }

    @Test
    public void sumWithMean() {
        Map<String, Double[]> channelData = new HashMap<>();
        Map<String, Double> meanValues = new HashMap<>();
        setupChannelAndMeanData(channelData, meanValues);
        Map<String, Double[]> resultData = service.sumWithMean(channelData, meanValues);
        assertEquals("Keys are not matching.", channelData.keySet(), resultData.keySet());
        for(Map.Entry<String, Double[]> entry : resultData.entrySet()) {
            Double[] initialChannelData = channelData.get(entry.getKey());
            double valueToAdd = meanValues.get(entry.getKey());
            Double[] updatedChannelData = Arrays.stream(initialChannelData).map(x -> x + valueToAdd).toArray(Double[]::new);
            assertArrayEquals("Data not matching.", updatedChannelData, resultData.get(entry.getKey()));
        }
    }

    @Test
    public void startExecutionsOfAllFunctionsSuccess() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File channelsFile = new File(Objects.requireNonNull(classLoader.getResource("channels.txt")).getFile());
        File paramsFile = new File(Objects.requireNonNull(classLoader.getResource("parameters.txt")).getFile());
        String input = String.format("%s\n%s\n%s\n%s\n",channelsFile.getPath(), paramsFile.getPath(), "b","e");
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        service.startExecutionsOfAllFunctions();
        int lineCount = outContent.toString().split("\n").length;
        assertEquals("Expected Line Count mismatch.", 19, lineCount);
    }

    @Test(expected = FileNotFoundException.class)
    public void startExecutionsOfAllFunctionsFailedFileNotFound() throws IOException {
        File channelsFile = new File("channe.txt");
        File paramsFile = new File("parameters.txt");
        String input = String.format("%s\n%s\n%s\n",channelsFile.getPath(), paramsFile.getPath(), "e");
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        service.startExecutionsOfAllFunctions();
    }

    @Test(expected = NumberFormatException.class)
    public void startExecutionsOfAllFunctionsFailedIOException() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File channelsFile = new File(Objects.requireNonNull(classLoader.getResource("invalidChannelData.txt")).getFile());
        File paramsFile = new File(Objects.requireNonNull(classLoader.getResource("parameters.txt")).getFile());
        String input = String.format("%s\n%s\n%s\n",channelsFile.getPath(), paramsFile.getPath(), "e");
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        service.startExecutionsOfAllFunctions();
    }

    @Test
    public void startExecutionsOfAllFunctionsSuccessSeeAllOutputs() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File channelsFile = new File(Objects.requireNonNull(classLoader.getResource("channels.txt")).getFile());
        File paramsFile = new File(Objects.requireNonNull(classLoader.getResource("parameters.txt")).getFile());
        String input = String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s\n",channelsFile.getPath(), paramsFile.getPath(), "b","a","c","d","e");
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        service.startExecutionsOfAllFunctions();
        int lineCount = outContent.toString().split("\n").length;
        assertEquals("Expected Line Count mismatch.", 46, lineCount);
    }
}