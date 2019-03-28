/*
 *     AdvancedWhitelist - An advanced whitelist plugin for Sponge and Spigot
 *     Copyright (C) 2019  Mathéo CIMBARO
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package fr.discowzombie.advancedwhitelist.common.message;

public class Replacer {

    private final String textToMatch, replacement;
    private final Integer limit;

    public Replacer(String textToMatch, String replacement, Integer limit) {
        this.textToMatch = textToMatch;
        this.replacement = replacement;
        this.limit = limit;
    }

    public String getTextToMatch() {
        return textToMatch;
    }

    public String getReplacement() {
        return replacement;
    }

    public Integer getLimit() {
        return limit;
    }

    public String apply(String applyOn) {
        for (int i = 0; i < (this.limit <= 0 ? 100 : this.limit); i++) {
            applyOn = applyOn.replaceFirst(this.textToMatch, this.replacement);
        }

        return applyOn;
    }
}
