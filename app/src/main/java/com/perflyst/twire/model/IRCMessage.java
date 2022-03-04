package com.perflyst.twire.model;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IRCMessage {
    static Pattern ircPattern = Pattern.compile("(?:@(.+) )?:.+ ([A-Z]+) #\\S*(?: :(.+))?");

    static ImmutableMap<String, String> replacements = ImmutableMap.of(
        "\\:", ";",
        "\\s", " ",
        "\\\\", "\\",
        "\\r", "\r",
        "\\n", "\n"
    );

    public Map<String, String> tags;
    public String command;
    public String content;

    public static IRCMessage parse(String message) {
        Matcher ircMatcher = ircPattern.matcher(message);
        if (!ircMatcher.matches())
            return null;

        IRCMessage ircMessage = new IRCMessage();
        ircMessage.tags = parseTags(ircMatcher.group(1));
        ircMessage.command = ircMatcher.group(2);
        ircMessage.content = ircMatcher.group(3);

        if (ircMessage.content == null) {
            ircMessage.content = "";
        }

        return ircMessage;
    }

    private static Map<String, String> parseTags(@Nullable String tagString) {
        if (tagString == null)
            return Collections.emptyMap();

        Map<String, String> tags = new HashMap<>();
        for (String tag : tagString.split(";")) {
            int index = tag.indexOf('=');
            if (index == tag.length() - 1 || index == -1) {
                continue;
            }

            String value = tag.substring(index + 1);
            for (Map.Entry<String, String> entry : replacements.entrySet()) {
                value = value.replace(entry.getKey(), entry.getValue());
            }

            tags.put(tag.substring(0, index), value);
        }

        return tags;
    }
}
