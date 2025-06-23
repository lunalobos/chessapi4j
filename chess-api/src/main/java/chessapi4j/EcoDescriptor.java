/*
 * Copyright 2025 Miguel Angel Luna Lobos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/lunalobos/chessapi4j/blob/master/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package chessapi4j;

import java.util.Objects;
//bean
/**
 * A simple data class with the ECO code and the opening name.
 * 
 * @author lunalobos
 * @since 1.2.7
 */
public class EcoDescriptor {
    private final String eco;
    private final String name;

    /**
     * The eco code
     * @return the eco code
     */
    public String getEco() {
        return this.eco;
    }

    /**
     * The opening name
     * @return the opening name
     */
    public String getName() {
        return this.name;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof EcoDescriptor)) {
            return false;
        } else {
            EcoDescriptor other = (EcoDescriptor)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$eco = this.getEco();
                Object other$eco = other.getEco();
                if (this$eco == null) {
                    if (other$eco != null) {
                        return false;
                    }
                } else if (!this$eco.equals(other$eco)) {
                    return false;
                }

                Object this$name = this.getName();
                Object other$name = other.getName();
                if (this$name == null) {
                    if (other$name != null) {
                        return false;
                    }
                } else if (!this$name.equals(other$name)) {
                    return false;
                }

                return true;
            }
        }
    }

    private boolean canEqual(Object other) {
        return other instanceof EcoDescriptor;
    }

    public int hashCode() {
        return 57649 * Objects.hash(eco, name);
    }

    public String toString() {
        return new StringBuilder("EcoDescriptor(eco=")
                .append(this.getEco())
                .append(", name=")
                .append(this.getName())
                .append(")")
                .toString();
    }

    /**
     * Creates a new EcoDescriptor
     * @param eco the eco code
     * @param name the opening name
     */
    public EcoDescriptor(String eco, String name) {
        this.eco = eco;
        this.name = name;
    }
}
