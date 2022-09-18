package com.edu.ulab.app.storage.generatorid;

import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class GeneratorUserId {

    private final static String PATH = "user_storage.json";

    public static long userId() {
        List<Long> listId = new ArrayList<>();
        JSONObject data = getDataFromStorage();

        if(data.isEmpty())
            return 1;

        JSONArray userArray = (JSONArray) data.get("users");
        userArray.forEach(jsonObject -> {
            JSONObject userObject = (JSONObject) jsonObject;
            listId.add((long)userObject.get("id"));
        });
        return listId.stream().max(Long::compareTo).orElse(0L) + 1;

    }

    private static JSONObject getDataFromStorage() {
        JSONObject data = null;

        try {
            StringBuilder sb = new StringBuilder();
            List<String> lines = Files.readAllLines(Paths.get(PATH));
            lines.forEach(sb::append);
            String stringUsers = sb.toString();
            if (stringUsers.isEmpty())
                return new JSONObject();

            JSONParser parser = new JSONParser();
            data = (JSONObject) parser.parse(stringUsers);
        } catch (ParseException | IOException e) {
            log.error(e.getMessage());
        }
        return data;
    }
}
