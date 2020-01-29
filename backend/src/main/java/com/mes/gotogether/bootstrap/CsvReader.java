package com.mes.gotogether.bootstrap;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public class CsvReader {

    private static final CsvMapper csvMapper = new CsvMapper();

    public static List<UserDTO> readUserDetails(File csvFile) throws Exception{

        // create a schema for the object and order the columns to get the proper inputs
        CsvSchema csvSchema = csvMapper
                .schemaFor(UserDTO.class)
                .withHeader()
                .withColumnReordering(true);

        // Read the values and ignore the unknown columns
        MappingIterator<UserDTO> userDTOIterator = csvMapper
                .enable(CsvParser.Feature.IGNORE_TRAILING_UNMAPPABLE)
                .readerFor(UserDTO.class)
                .with(csvSchema)
                .readValues(new InputStreamReader(new FileInputStream(csvFile), "ISO-8859-1"));

        return userDTOIterator.readAll();
    }

    public static List<AddressDTO> readAddressDetails(File csvFile) throws Exception{

        // create a schema for the object and order the columns to get the proper inputs
        CsvSchema csvSchema = csvMapper
                .schemaFor(AddressDTO.class)
                .withHeader()
                .withColumnReordering(true);

        // Read the values and ignore the unknown columns
        MappingIterator<AddressDTO> addressDTOIterator = csvMapper
                .enable(CsvParser.Feature.IGNORE_TRAILING_UNMAPPABLE)
                .readerFor(AddressDTO.class)
                .with(csvSchema)
                .readValues(new InputStreamReader(new FileInputStream(csvFile), "ISO-8859-1"));

        return addressDTOIterator.readAll();
    }

    public static List<GroupDTO> readGroupDetails(File csvFile) throws Exception{

        // create a schema for the object and order the columns to get the proper inputs
        CsvSchema csvSchema = csvMapper
                .schemaFor(GroupDTO.class)
                .withHeader()
                .withColumnReordering(true);

        // Read the values and ignore the unknown columns
        MappingIterator<GroupDTO> groupDTOIterator = csvMapper
                .enable(CsvParser.Feature.IGNORE_TRAILING_UNMAPPABLE)
                .readerFor(GroupDTO.class)
                .with(csvSchema)
                .readValues(new InputStreamReader(new FileInputStream(csvFile), "ISO-8859-1"));

        return groupDTOIterator.readAll();
    }


}
