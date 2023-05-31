package com.processing.cps.utils;

import com.processing.cps.exceptions.InvalidParametersException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ReadFileTest {
    @InjectMocks
    ReadFile readFile;

    @Test
    public void readFileSuccess() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File channelsFile = new File(Objects.requireNonNull(classLoader.getResource("channels.txt")).getFile());
        File paramsFile = new File(Objects.requireNonNull(classLoader.getResource("parameters.txt")).getFile());
        Map<String, Double []> channelsData = readFile.readChannelsData(channelsFile.getPath());
        Map<String, Double> paramssData = readFile.readParamsData(paramsFile.getPath());
        assertEquals("Size of Channel Data is not as expected.", 1, channelsData.keySet().size());
        assertEquals("Size of Parameters is not as expected.", 2, paramssData.keySet().size());
    }

    @Test(expected = FileNotFoundException.class)
    public void readChannelsDataFailNotFound() throws IOException {
        readFile.readChannelsData("invalidPath");
    }

    @Test(expected = FileNotFoundException.class)
    public void readParamsDataFailNotFound() throws IOException {
        readFile.readParamsData("invalidPath");
    }

    @Test(expected = NumberFormatException.class)
    public void readChannelsFailInvalidData() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file1 = new File(Objects.requireNonNull(classLoader.getResource("invalidChannelData.txt")).getFile());
        readFile.readChannelsData(file1.getPath());
    }

    @Test(expected = InvalidParametersException.class)
    public void readParamsFailInvalidDataFormat() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file1 = new File(Objects.requireNonNull(classLoader.getResource("invalidParams.txt")).getFile());
        readFile.readParamsData(file1.getPath());
    }

    @Test(expected = NumberFormatException.class)
    public void readParamsFailInvalidData() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file1 = new File(Objects.requireNonNull(classLoader.getResource("invalidParams1.txt")).getFile());
        readFile.readParamsData(file1.getPath());
    }
}