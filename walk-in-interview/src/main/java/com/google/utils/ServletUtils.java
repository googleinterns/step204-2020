package com.google.utils;

import com.google.gson.Gson;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;

public final class ServletUtils {
    private ServletUtils() {}

    /**
     * @return the value of parameter with the {@code name} in the {@code request}
     *         or returns {@code defaultValue} if that parameter does not exist.
     */
    public static String getStringParameter(HttpServletRequest request, String name, String defaultValue) {
        String value = request.getParameter(name);
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        return value;
    }

    /**
     * Gets the integer parameter from html form input.
     * @param request Http request.
     * @param parameterName Input id in html element.
     * @param defaultValue Default integer.
     * @return Parsed integer or default value if exception occur
     */
    public static int getIntParameter(HttpServletRequest request, String parameterName, int defaultValue) {
        String resultStr = request.getParameter(parameterName);

        try {
            int result = Integer.parseInt(resultStr);

            return result;
        } catch (NumberFormatException e) {
            // TODO: error handling

            // TODO: add logging to log the error

            return defaultValue;
        }
    }

    /**
     * Gets the float parameter from html form input.
     * @param request Http request.
     * @param parameterName Input id in html element.
     * @param defaultValue Default integer.
     * @return Parsed float or default value if exception occur
     */
    public static float getFloatParameter(HttpServletRequest request, String parameterName, float defaultValue) {
        String resultStr = request.getParameter(parameterName);

        try {
            float result = Float.parseFloat(resultStr);

            return result;
        } catch (NumberFormatException e) {
            // TODO: error handling

            // TODO: add logging to log the error

            return defaultValue;
        }
    }

    /**
     * Gets the LocalDate parameter from html form input.
     * @param request Http request.
     * @param parameterName Input id in html element.
     * @param defaultValue Default integer.
     * @return Parsed date or default value if exception occur
     */
    public static LocalDate getLocalDateParameter(HttpServletRequest request,
                                                  String parameterName, LocalDate defaultValue) {
        String resultStr = request.getParameter(parameterName);

        try {
            LocalDate result = LocalDate.parse(resultStr);

            return result;
        } catch (NumberFormatException e) {
            // TODO: error handling

            // TODO: add logging to log the error

            return defaultValue;
        }
    }

    /**
     * Converts the target item into json format.
     * @param item Target item.
     * @return Target item in json format.
     */
    public static <T> String convertToJsonUsingGson(T item) {
        Gson gson = new Gson();
        String json = gson.toJson(item);
        return json;
    }
}
