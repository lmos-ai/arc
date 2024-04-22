/*
 * // SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
 * //
 * // SPDX-License-Identifier: Apache-2.0
 */

/**
 * My weather function.
 */
function(
    name = "get_weather",
    description = "Returns real-time weather information for any location",
    group = "weather",
    params = types(string("location", "a city to obtain the weather for."))
) { (location) ->
        """
         The weather is good in $location. It is 20 degrees celsius.
        """
}
